package org.example;

import org.example.ArithmeticCoding.ArithmeticDecoder;
import org.example.Utils.BitInputStream;
import org.example.Utils.ArithmeticUtils;

import java.io.*;
import java.nio.file.Files;


public class Decompress {
    public static void decompress(String inputPath) throws IOException {
        int longNumBits = 32;
        File inputFile  = new File(inputPath);
        File outputFile = new File(inputFile.getParent(), "Decompressed_" + inputFile.getName());

        System.out.println("Starting Decompressing");
        long startTime = System.currentTimeMillis();


        try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            ArithmeticUtils arithmeticUtils = ArithmeticDecoder.readFrequencyArr(longNumBits, in);
            ArithmeticDecoder decoder = new ArithmeticDecoder(longNumBits, in);
            decoder.decompress(arithmeticUtils, outputStream);
        }

        System.out.println("Decompression Done\n" +
                "Decompression Time: " + (System.currentTimeMillis() - startTime) + "ms\n" +
                "Size before decompression: "+ Files.size(inputFile.toPath())+"\n"+
                "Size after decompression: "+Files.size(outputFile.toPath())+"\n"
        );

    }

}
