package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.Arrays;

import com.mapbox.geojson.FeatureCollection;

import uk.ac.ed.inf.powergrab.drone.DroneType;
import uk.ac.ed.inf.powergrab.io.FileUtils;
import uk.ac.ed.inf.powergrab.io.NetworkUtils;
import uk.ac.ed.inf.powergrab.map.Map;
import uk.ac.ed.inf.powergrab.map.Position;

public class AppMulti {
	public static void main(String[] args) {
		for (int i = 1; i < 13; i++) {
			for (int j = 1; j < 30; j++) {
				Map.getInstance().reset();

				try {
					String mapString = String.format(
							"http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson", 2020,
							String.format("%02d", i), String.format("%02d", j));
					System.out.printf("Date: %s/%s/%s \n", 2020, String.format("%02d", i), String.format("%02d", j));
					String mapSource = NetworkUtils.downloadMap(mapString);
					Map.getInstance().setFeatures(FeatureCollection.fromJson(mapSource));

					Position initPosition = new Position(Double.parseDouble(args[3]), Double.parseDouble(args[4]));
					int randomSeed = Integer.parseInt(args[5]);
					DroneType droneType = DroneType.valueOf(args[6]);

					PowerGrab powerGrab = new PowerGrab(initPosition, droneType, randomSeed);
					String moveTrace = powerGrab.play();
//				System.out.println(moveTrace);

					String fileName = String.format("%s-%s-%s-%s", droneType, String.format("%02d", i),
							String.format("%02d", i), 2019);
					FileUtils.outputGeojson(fileName, Map.getInstance().getFeatures().toJson());
					FileUtils.outputTxt(fileName, moveTrace);

				} catch (IOException e) {
					System.out.println("Unable to acquire map from the server. Please try again.");
				} catch (IllegalArgumentException e) {
					System.out.println("Incorrect argument! \n"
							+ "Usage: java -jar powergrab-0.0.1-SNAPSHOT.jar DD MM YYYY <initial_latitude> <initial_longitude> <random_seed> <drone_type>\n"
							+ "Note: valid dronetypes: " + Arrays.toString(DroneType.values()));
				}

			}
		}
		System.out.println(PowerGrab.count);
	}
}
