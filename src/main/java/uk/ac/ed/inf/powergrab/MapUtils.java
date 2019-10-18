package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class MapUtils {

	public static final double MAX_TRANSFER_DISTANCE = 0.00025;

	private static MapUtils mapUtils = null;
	private List<Feature> features;
	private List<ChargingStation> chargingStations = new ArrayList<ChargingStation>();

	private MapUtils() {
	}

	public static MapUtils getInstance() {
		if (mapUtils == null) {
			synchronized (MapUtils.class) {
				mapUtils = new MapUtils();
			}
		}
		return mapUtils;
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

	public ChargingStation getNearestStation(Position curPosition) {
		List<Double> distances = chargingStations.stream()
				.map(stations -> curPosition.getRelativeDistance(stations.getPosition())).collect(Collectors.toList());
		Double minDistance = Collections.min(distances);

		return chargingStations.get(distances.indexOf(minDistance));
	}

	public ChargingStation getNearestStationInRange(Position curPosition) {
		ChargingStation nearestStation = getNearestStation(curPosition);
		return curPosition.getRelativeDistance(nearestStation.getPosition()) < MAX_TRANSFER_DISTANCE ? nearestStation
				: new ChargingStation(curPosition, 0, 0);
	}

	public void drawTrajectory(Position oldPosition, Position newPosition) {
		List<Point> endPoints = new ArrayList<Point>();
		endPoints.add(Point.fromLngLat(oldPosition.longitude, oldPosition.latitude));
		endPoints.add(Point.fromLngLat(newPosition.longitude, newPosition.latitude));
		Feature lineString = Feature.fromGeometry(LineString.fromLngLats(endPoints));
		features.add(lineString);
	}
}
