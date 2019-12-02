package uk.ac.ed.inf.powergrab.utils;

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

	/**
	 * Write the geojson string to a geojson file
	 *
	 * @param filename the name of the file without extension
	 * @param map a geojson string containing the charging stations' information
	 *                 and a line string specifying the drone's moving path
	 */
	public static void writeGeojson(String filename, String map) {
		try (FileWriter writer = new FileWriter(new File(filename + ".geojson"))) {
			writer.write(map);
		} catch (IOException e) {
			System.err.println("Error occurs when generating geojson output file!");
		}
	}

	/**
	 * Write the drone trace log to a txt file
	 * @param filename the name of the file without extension
	 * @param moveTraces a String specifying the drone's trace
	 */
	public static void writeTxt(String filename, String moveTraces) {
		try (FileWriter writer = new FileWriter(new File(filename + ".txt"))) {
			writer.write(moveTraces);
		} catch (IOException e) {
			System.err.println("Error occurs when generating geojson output file!");
		}
	}

}
