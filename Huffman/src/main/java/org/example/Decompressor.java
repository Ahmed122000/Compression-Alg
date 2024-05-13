package org.example;

import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class Decompressor {

    public static void deCompressFile(String compressedFile, String decompressedFile) throws IOException, ClassNotFoundException {
        ObjectInputStream input = new ObjectInputStream(new FileInputStream(compressedFile));
        OutputStream out = new FileOutputStream(decompressedFile);
        int n = input.readInt();

        Hashtable<String, Byte> codesWords = (Hashtable<String, Byte>)input.readObject();

        byte[] buffer = new byte[500];
        byte[] output = new byte[2000];
        int numBytes = 0;
        int bytesRead;

        StringBuilder curr = new StringBuilder();
        while ((bytesRead = input.read(buffer)) != -1) {

            for (int i = 0; i < bytesRead; i++){
                byte b = buffer[i];

                String str = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
                for (int j = 0; j < str.length(); j++){
                    curr.append(str.charAt(j));
                    if (codesWords.containsKey(String.valueOf(curr))){
                        byte by = codesWords.get(String.valueOf(curr));
                        if (output.length - numBytes < 1){
                            out.write(output, 0, numBytes);
                            numBytes = 0;
                        }
                        output[numBytes] = by;
                        numBytes += 1;
                        curr = new StringBuilder();
                    }
                }
            }
        }
        out.write(output, 0, numBytes);
        input.close();
        out.close();
    }
}
