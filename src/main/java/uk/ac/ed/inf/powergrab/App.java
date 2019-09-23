package uk.ac.ed.inf.powergrab;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		if (args.length != 7) {
			return;
		}

		String date = String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson",
				args[2], args[1], args[0]);

		Position initPos = new Position(Double.parseDouble(args[3]), Double.parseDouble(args[4]));
		int randomSeed = Integer.parseInt(args[5]);
		String droneType = args[6];
	}
}
