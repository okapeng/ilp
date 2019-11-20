package uk.ac.ed.inf.powergrab.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import uk.ac.ed.inf.powergrab.map.Map;

public class FileUtils {
	private static FileUtils fileUtils = null;
	private String filename;

	private FileUtils() {
	}

	public static FileUtils getInstance() {
		if (fileUtils == null) {
			synchronized (Map.class) {
				fileUtils = new FileUtils();
			}
		}
		return fileUtils;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void outputGeojson(String features) {
		try {
			FileWriter writer = new FileWriter(new File(filename + ".geojson"));
			writer.write(features);
			writer.close();
		} catch (IOException e) {
			System.err.println("Error occurs when generating geojson output file!");
		}
	}

	public void outputTxt(String moveTraces) {
		try {
			FileWriter writer = new FileWriter(new File(filename + ".txt"));
			writer.write(moveTraces);
			writer.close();
		} catch (IOException e) {
			System.err.println("Error occurs when generating txt output file!");
		}
	}

}
