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

		Position initPos = new Position(Double.parseDouble(args[3]), Double.parseDouble(args[4]));

		try {
			conn = MapUtil.getConnection(mapString);
			mapSource = MapUtil.getMapSource(conn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FeatureCollection features = FeatureCollection.fromJson(mapSource);

		int randomSeed = Integer.parseInt(args[5]);
		DroneType droneType = DroneType.valueOf(args[6]);

		switch (droneType) {
		case stateful:

			break;
		case stateless:

			break;

		default:
			return;
		}
	}

}
