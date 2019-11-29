package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.FeatureCollection;
import uk.ac.ed.inf.powergrab.drone.Drone.DroneType;
import uk.ac.ed.inf.powergrab.engine.PowerGrab;
import uk.ac.ed.inf.powergrab.map.Map;
import uk.ac.ed.inf.powergrab.utils.FileUtils;
import uk.ac.ed.inf.powergrab.utils.NetworkUtils;

import java.io.IOException;
import java.util.Arrays;

public class AppMulti {
	public static void main(String[] args) {
		for (DroneType droneType : DroneType.values()) {
			if (!droneType.equals(DroneType.stateful)) {
				continue;
			}
			PowerGrab.count = 0;
			for (int i = 1; i < 13; i++) {
				for (int j = 1; j < 2; j++) {
					Map.reset();

					try {
						String mapString = String.format(
								"http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson", 2019,
								String.format("%02d", i), String.format("%02d", i));
						System.out.printf("Date: %s/%s/%s \n", 2019, String.format("%02d", i),
								String.format("%02d", i));
						String mapSource = NetworkUtils.downloadMap(mapString);
						Map.getInstance().setFeatures(FeatureCollection.fromJson(mapSource));

						int randomSeed = Integer.parseInt(args[5]);

						PowerGrab powerGrab = new PowerGrab(Double.parseDouble(args[3]), Double.parseDouble(args[4]), droneType.name(), randomSeed);
						String moveTrace = powerGrab.play();
//					System.out.println(moveTrace);

						String fileName = String.format("%s-%s-%s-%s", droneType, String.format("%02d", i),
								String.format("%02d", i), 2019);
						FileUtils.writeGeojson(fileName, Map.getInstance().getFeatures().toJson());
						FileUtils.writeTxt(fileName, moveTrace);

					} catch (IOException e) {
						System.out.println("Unable to acquire map from the server. Please try again.");
					} catch (IllegalArgumentException e) {
						System.out.println("Incorrect argument! \n"
								+ "Usage: java -jar powergrab-0.0.1-SNAPSHOT.jar DD MM YYYY <initial_latitude> <initial_longitude> <random_seed> <drone_type>\n"
								+ "Note: valid dronetypes: " + Arrays.toString(DroneType.values()));
					}

				}
			}
			System.out.printf("Number of map fails for %s drone: %d\n", droneType, PowerGrab.count);
		}

	}
}
