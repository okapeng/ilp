package uk.ac.ed.inf.powergrab.utils;

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

	/**
	 * Given the URL of a geojson file and read its content to a string
	 *
	 * @param mapUrl the URL address of the map file
	 * @return a string containing the content of that file
	 * @throws IOException fails to read the file's content
	 */
	public static String downloadMap(String mapUrl) throws IOException {
		StringBuilder sb = new StringBuilder();
		HttpURLConnection conn = getConnection(new URL(mapUrl));
		try (InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream())) {
			int c = inputStreamReader.read();
			while (c != -1) {
				sb.append((char) c);
				c = inputStreamReader.read();
			}
			conn.disconnect();
			return sb.toString();
		}
	}

	/**
	 * Given the URL get a connection to the server
	 *
	 * @param mapUrl the URL of the file
	 * @return an instance of Connection class
	 * @throws IOException fails to make the connection
	 */
	private static HttpURLConnection getConnection(URL mapUrl) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) mapUrl.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.connect();
		return conn;
	}
}
