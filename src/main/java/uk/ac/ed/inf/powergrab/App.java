package uk.ac.ed.inf.powergrab;

import java.net.HttpURLConnection;

import com.mapbox.geojson.FeatureCollection;;

/**
 * 
 * @author Ivy Wang
 *
 */
public class App {
	public static void main(String[] args) {
		// TODO add argument check

		String mapString = String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson",
				args[2], args[1], args[0]);
		HttpURLConnection conn = null;
		String mapSource = null;

		try {
			conn = NetworkUtils.getConnection(mapString);
			mapSource = NetworkUtils.getMapSource(conn);
			MapUtils.getInstance().setFeatures(FeatureCollection.fromJson(mapSource));
			System.out.println(MapUtils.getInstance().getchargingStations().stream().map(ChargingStation::getCoins)
					.filter(x -> x > 0).reduce(Double::sum));

			Position initPosition = new Position(Double.parseDouble(args[3]), Double.parseDouble(args[4]));
			int randomSeed = Integer.parseInt(args[5]);
			DroneType droneType = DroneType.valueOf(args[6]);

			PowerGrab powerGrab = new PowerGrab(initPosition, droneType, randomSeed);
			String moveTrace = powerGrab.play();
//			System.out.println(moveTrace);

//			MapUtils.getInstance().getchargingStations().forEach(station -> {
//				if (station.getCoins() > 0) {
//					System.out.println(station);
//				}
//			});

			FileUtils fileUtils = FileUtils.getInstance();
			fileUtils.setFilename(String.format("%s-%s-%s-%s", droneType, args[0], args[1], args[2]));
			fileUtils.outputGeojson(MapUtils.getInstance().getFeatures().toJson());
			fileUtils.outputTxt(moveTrace);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}

	}

}
