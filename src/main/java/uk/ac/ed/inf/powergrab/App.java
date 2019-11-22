package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.Arrays;

import com.mapbox.geojson.FeatureCollection;

import uk.ac.ed.inf.powergrab.drone.DroneType;
import uk.ac.ed.inf.powergrab.io.FileUtils;
import uk.ac.ed.inf.powergrab.io.NetworkUtils;
import uk.ac.ed.inf.powergrab.map.Map;
import uk.ac.ed.inf.powergrab.map.Position;

/**
 * 
 * @author Ivy Wang
 *
 */
public class App {
	public static void main(String[] args) {

		try {
			if (args.length != 7)
				throw new IllegalArgumentException();

			String mapString = String.format(
					"http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson", args[2], args[1],
					args[0]);
			String mapSource = NetworkUtils.downloadMap(mapString);
			Map.getInstance().reset();
			Map.getInstance().setFeatures(FeatureCollection.fromJson(mapSource));

			Position initPosition = new Position(Double.parseDouble(args[3]), Double.parseDouble(args[4]));
			int randomSeed = Integer.parseInt(args[5]);
			DroneType droneType = DroneType.valueOf(args[6]);

			PowerGrab powerGrab = new PowerGrab(initPosition, droneType, randomSeed);
			String moveTrace = powerGrab.play();
//			System.out.println(moveTrace);

			String fileName = String.format("%s-%s-%s-%s", droneType, args[0], args[1], args[2]);
			FileUtils.outputGeojson(fileName, Map.getInstance().getFeatures().toJson());
			FileUtils.outputTxt(fileName, moveTrace);

		} catch (IOException e) {
			System.out.println("Network issue, unable to acquire map from server. Please try again.");
		} catch (IllegalArgumentException e) {
			System.out.println("Incorrect argument! \n"
					+ "Usage: java -jar powergrab-0.0.1-SNAPSHOT.jar DD MM YYYY <initial_latitude> <initial_longitude> <random_seed> <drone_type>\n"
					+ "Note: valid dronetypes: " + Arrays.toString(DroneType.values()));
		}

	}

}
