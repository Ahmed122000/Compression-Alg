package org.example;

import java.io.*;
import static java.lang.System.exit;

public class Main {
	public static void main(String[] args) throws IOException {
		String op = args[0];
        if(op.equals("compress"))
            Compress.compress(args[1]);
		else if(op.equals("decompress"))
            Decompress.decompress(args[1]);
		else if(op.equals("compare"))
			System.out.println(compareFiles(args[1], args[2]));
		else{
			System.out.println("Invalid Choice");
			exit(-1);
		}
	}

	public static String compareFiles(String filePath1, String filePath2) throws IOException {
		File file1 = new File(filePath1);
		File file2 = new File(filePath2);

		if (!file1.exists() || !file2.exists())
			return "Error, One or both files not found.";
		if (file1.length() != file2.length())
			return "Files have different sizes";

		int counter = 0;
		try (InputStream is1 = new FileInputStream(file1);
			 InputStream is2 = new FileInputStream(file2)) {

			int byte1, byte2;

			// Compare the contents of the files byte by byte
			do {
				byte1 = is1.read();
				byte2 = is2.read();
				if (byte1 != byte2) {
					counter++; // Files have different content
				}
			} while (byte1 != -1 && byte2 != -1);

			if(counter == 0)
				return "True, 2 files are same";
			return "False, 2 files have " + counter + " different bytes";
		}
	}
}
