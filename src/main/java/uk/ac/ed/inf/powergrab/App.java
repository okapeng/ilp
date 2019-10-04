package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;

import javax.sound.midi.Soundbank;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mapbox.geojson.FeatureCollection;

import uk.ac.ed.inf.powergrab.Position;;

public class App {
	public static void main(String[] args) {
		if (args.length != 7) {
			return;
		}

		String mapString = String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson",
				args[2], args[1], args[0]);

		Position initPos = new Position(Double.parseDouble(args[3]), Double.parseDouble(args[4]));
				
//		int randomSeed = Integer.parseInt(args[5]);
//		DroneType droneType = DroneType.valueOf(args[6]);
//		
//		//TODO download json map data
//
//		switch (droneType) {
//		case stateful:
//
//			break;
//		case stateless:
//
//			break;
//
//		default:
//			return;
//		}

		try {
			HttpURLConnection conn = getConnection(mapString);
			String mapSource = getMapSource(conn);
			FeatureCollection features = FeatureCollection.fromJson(mapSource);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static HttpURLConnection getConnection(String mapString) throws Exception {
		URL mapUrl = null;
		HttpURLConnection conn = null;
		mapUrl = new URL(mapString);
		conn = (HttpURLConnection) mapUrl.openConnection();
		conn.setReadTimeout(10000); 
		conn.setConnectTimeout(15000); 
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.connect();
		return conn;
	}

	private static String getMapSource(HttpURLConnection conn) throws IOException {
		StringBuilder sb = new StringBuilder();
		InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
		int c = inputStreamReader.read();
		while (c != -1) {
			sb.append((char) c);
			c = inputStreamReader.read();
		}
		return sb.toString();
	}
}
