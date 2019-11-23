package uk.ac.ed.inf.powergrab.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Tool class for generating output file
 * 
 * @author Ivy Wang
 *
 */
public class FileUtils {

	public static void writeGeojson(String filename, String features) {
		try (FileWriter writer = new FileWriter(new File(filename + ".geojson"))) {
			writer.write(features);
		} catch (IOException e) {
			System.err.println("Error occurs when generating geojson output file!");
		}
	}

	public static void writeTxt(String filename, String moveTraces) {
		try (FileWriter writer = new FileWriter(new File(filename + ".txt"))) {
			writer.write(moveTraces);
		} catch (IOException e) {
			System.err.println("Error occurs when generating geojson output file!");
		}
	}

}
