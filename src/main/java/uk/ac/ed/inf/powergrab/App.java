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
			

			Position initPosition = new Position(Double.parseDouble(args[3]), Double.parseDouble(args[4]));
			int randomSeed = Integer.parseInt(args[5]);
			DroneType droneType = DroneType.valueOf(args[6]);
			
			PowerGrab powerGrab = new PowerGrab(initPosition, droneType, randomSeed);
			powerGrab.play();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}

	}

}
