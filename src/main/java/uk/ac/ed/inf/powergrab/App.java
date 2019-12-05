package uk.ac.ed.inf.powergrab;

import uk.ac.ed.inf.powergrab.engine.PowerGrab;
import uk.ac.ed.inf.powergrab.map.Map;
import uk.ac.ed.inf.powergrab.utils.FileUtils;
import uk.ac.ed.inf.powergrab.utils.NetworkUtils;

import java.io.IOException;

/**
 * Entrance point for PowerGrab App
 * 
 * @author Ivy Wang
 *
 */
public class App {
	/**
	 * @param args DD MM YYYY <initial_latitude> <initial_longitude> <random_seed> <drone_type>
	 */
	public static void main(String[] args) {

		try {
			if (args.length != 7)
				throw new IllegalArgumentException("Incorrect arguments number! ");

			String mapString = String.format(
					"http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson", args[2], args[1],
					args[0]);
			String mapSource = NetworkUtils.downloadMap(mapString);
			Map.getInstance().setMap(mapSource);

			int randomSeed = Integer.parseInt(args[5]);

			PowerGrab powerGrab = new PowerGrab(Double.parseDouble(args[3]), Double.parseDouble(args[4]), args[6], randomSeed);
			String moveTrace = powerGrab.play();
			System.out.println(moveTrace);

			String fileName = String.format("%s-%s-%s-%s", args[6], args[0], args[1], args[2]);
			FileUtils.writeGeojson(fileName, Map.getInstance().getFeatures().toJson());
			FileUtils.writeTxt(fileName, moveTrace);

		} catch (IOException e) {
			System.out.println("Unable to download map from server. Please try again.");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// Either because the number of argument is wrong or the DroneType doesn't match
			// with any supported type
			System.out.println(e.getMessage()
					+ "\nUsage: java -jar powergrab-0.0.1-SNAPSHOT.jar DD MM YYYY <initial_latitude> <initial_longitude> <random_seed> <drone_type>\n");
		}

	}

}
