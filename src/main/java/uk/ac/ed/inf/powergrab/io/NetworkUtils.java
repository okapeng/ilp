package uk.ac.ed.inf.powergrab.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

	public static HttpURLConnection getConnection(String mapString) throws IOException {
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

	public static String getMapSource(HttpURLConnection conn) throws IOException {
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
