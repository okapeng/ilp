package uk.ac.ed.inf.powergrab.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

	public static void outputGeojson(String filename, String features) {
		try {
			FileWriter writer = new FileWriter(new File(filename + ".geojson"));
			writer.write(features);
			writer.close();
		} catch (IOException e) {
			System.err.println("Error occurs when generating geojson output file!");
		}
	}

	public static void outputTxt(String filename, String moveTraces) {
		try {
			FileWriter writer = new FileWriter(new File(filename + ".txt"));
			writer.write(moveTraces);
			writer.close();
		} catch (IOException e) {
			System.err.println("Error occurs when generating txt output file!");
		}
	}

}
