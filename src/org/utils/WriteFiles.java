package org.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WriteFiles {
	
	/**
	 * write list of lines to filename
	 * @param contents		list of line. Each of line does not have end line
	 * @param filename		file name
	 * @throws IOException	cannot create file
	 */
	public static void writeHomeLocation(ArrayList<String> contents, String filename) throws IOException{
		File f = new File(filename);
		
		if (!f.exists())
			f.createNewFile();
		
		FileWriter fw = new FileWriter(f.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		for (String c : contents)
			bw.write(c + "\n");
		bw.close();
	}
}
