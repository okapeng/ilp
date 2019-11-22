package uk.ac.ed.inf.powergrab.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class Map {

	public static final double MAX_TRANSFER_DISTANCE = 0.00025;

	private static Map map = null;
	private List<Feature> features;
	private List<ChargingStation> chargingStations = new ArrayList<ChargingStation>();

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

	public void reset() {
		map = new Map();
	}

	public void setFeatures(FeatureCollection features) {
		this.features = features.features();
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

	public FeatureCollection getFeatures() {
		return FeatureCollection.fromFeatures(features);
	}

	public List<ChargingStation> getchargingStations() {
		return new ArrayList<ChargingStation>(this.chargingStations);
	}

	public ChargingStation getNearestStationInRange(Position curPosition) {
		List<Double> distances = chargingStations.stream()
				.map(station -> curPosition.getRelativeDistance(station.getPosition())).collect(Collectors.toList());
		Double minDistance = Collections.min(distances);
		ChargingStation nearestStation = chargingStations.get(distances.indexOf(minDistance));

		return minDistance < MAX_TRANSFER_DISTANCE ? nearestStation : new ChargingStation(curPosition, 0, 0);
	}

	public void drawTrajectory(Position oldPosition, Position newPosition) {
		List<Point> endPoints = new ArrayList<Point>();
		endPoints.add(Point.fromLngLat(oldPosition.longitude, oldPosition.latitude));
		endPoints.add(Point.fromLngLat(newPosition.longitude, newPosition.latitude));
		Feature lineString = Feature.fromGeometry(LineString.fromLngLats(endPoints));
		features.add(lineString);
	}
}
