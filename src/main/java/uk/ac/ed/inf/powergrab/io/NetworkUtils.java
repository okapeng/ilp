package uk.ac.ed.inf.powergrab.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Tool class for downloading the map from server
 * 
 * @author Ivy Wang
 *
 */
public class NetworkUtils {

	public static String downloadMap(String mapString) throws IOException {
		StringBuilder sb = new StringBuilder();
		HttpURLConnection conn = getConnection(mapString);
		try (InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream())) {
			int c = inputStreamReader.read();
			while (c != -1) {
				sb.append((char) c);
				c = inputStreamReader.read();
			}
			return sb.toString();
		}
	}

	private static HttpURLConnection getConnection(String mapString) throws IOException {
		URL mapUrl = new URL(mapString);
		HttpURLConnection conn = (HttpURLConnection) mapUrl.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.connect();
		return conn;
	}
}
