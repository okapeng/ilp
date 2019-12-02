package uk.ac.ed.inf.powergrab.map;

import com.mapbox.geojson.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton class to maintain the map for a PowerGrab game
 * 
 * @author Ivy Wang
 *
 */
public class Map {
    // Maximum distance for a transfer to be happened
	private static final double MAX_TRANSFER_DISTANCE = 0.00025;

	private static Map map = null;
	private List<Feature> features;
	// List of all the charging stations in the map
	private List<ChargingStation> chargingStations = new ArrayList<>();
    // List of points the drone has visited
	private List<Point> droneTrace = new ArrayList<>();

	private Map() {
	}

	public static Map getInstance() {
		if (map == null) {
			synchronized (Map.class) {
				map = new Map();
			}
		}
		return map;
	}

	public static void reset() {
		map = null;
	}

	/**
     * Extract map information from the geojson string downloaded from the sever
	 *
     * @param map a geojson formatted string
	 */
    public void setMap(String map) {
        this.features = FeatureCollection.fromJson(map).features();
		for (Feature feature : this.features) {
			Geometry geometry = feature.geometry();
			if (geometry instanceof Point) {
				Position chargingStationPos = new Position(((Point) geometry).latitude(),
						((Point) geometry).longitude());
				ChargingStation newChargingStation = new ChargingStation(chargingStationPos,
						feature.getNumberProperty("coins"), feature.getNumberProperty("power"));
				chargingStations.add(newChargingStation);
			}
		}
	}

	/**
	 * 
	 * @return The feature collection with the drone trace added
	 */
	public FeatureCollection getFeatures() {
		features.add(Feature.fromGeometry(LineString.fromLngLats(droneTrace)));
		return FeatureCollection.fromFeatures(features);
	}

	/**
     * @return A copy of all the charging stations in the map
	 */
	public List<ChargingStation> getChargingStations() {
		return new ArrayList<>(this.chargingStations);
	}

	/**
     * Find the closest charging station within the minimum distance for transfer happening
     * between station and drone
	 * @param position
	 * @return The nearest charging station in range. If there is no charging
	 *         station nearby, return a station with no coins and power.
	 */
	public ChargingStation getNearestStationInRange(Position position) {
		List<Double> distances = chargingStations.stream()
				.map(station -> position.getRelativeDistance(station.getPosition())).collect(Collectors.toList());
		Double minDistance = Collections.min(distances);
		ChargingStation nearestStation = chargingStations.get(distances.indexOf(minDistance));

		return minDistance < MAX_TRANSFER_DISTANCE ? nearestStation : new ChargingStation(position, 0, 0);
	}

    /**
     * Add the drone's current position to the map
     *
     * @param position drone's position
     */
    public void addDronePosition(Position position) {
        droneTrace.add(Point.fromLngLat(position.longitude, position.latitude));
    }

}
