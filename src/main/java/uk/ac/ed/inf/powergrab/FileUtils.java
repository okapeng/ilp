package uk.ac.ed.inf.powergrab;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.mapbox.geojson.FeatureCollection;

public class FileUtils {
	private static FileUtils fileUtils = null;
	private String filename;
	
	private FileUtils() {
	}

	public static FileUtils getInstance() {
		if (fileUtils == null) {
			synchronized (MapUtils.class) {
				fileUtils = new FileUtils();
			}
		}
		return fileUtils;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void outputGeojson(String features) throws IOException {
		FileWriter writer = new FileWriter(new File(filename + ".geojson"));
		writer.write(features);
		writer.close();
	}
	
	public void outputTxt(String moveTraces) throws IOException {
		FileWriter writer = new FileWriter(new File(filename + ".txt"));
		writer.write(moveTraces);
		writer.close();
	}
		
}
