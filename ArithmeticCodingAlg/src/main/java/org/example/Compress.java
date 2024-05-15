package org.example;

import org.example.ArithmeticCoding.ArithmeticEncoder;
import org.example.Utils.BitOutputStream;
import org.example.Utils.ArithmeticUtils;

import java.io.*;
import java.nio.file.Files;


public class Compress {
    public static void compress(String inputPath) throws IOException {
        int longNumBits = 32;
        File inputFile  = new File(inputPath);
        File outputFile = new File(inputFile.getParent(), "Compressed_" + inputFile.getName());

        System.out.println("Starting Compressing");
        long startTime = System.currentTimeMillis();

        ArithmeticUtils arithmeticUtils = ArithmeticEncoder.calcFrequencyArr(inputFile);

        try(InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));
            BitOutputStream bitOutputStream = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {
            ArithmeticEncoder encoder = new ArithmeticEncoder(longNumBits, bitOutputStream);
            encoder.writeFrequencyArr(arithmeticUtils);
            encoder.compress(arithmeticUtils, inputStream);
        }

        System.out.println("Compression Done\n" +
                "Compression Time: " + (System.currentTimeMillis() - startTime) + "ms\n" +
                "Size before compression: "+ Files.size(inputFile.toPath())+"\n"+
                "Size after compression: "+Files.size(outputFile.toPath())+"\n"
        );
    }

}
