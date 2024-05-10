package org.example;

import java.util.Hashtable;


public class Main {



    public static void main(String[] args) {
        System.out.println("Welcome to Huffman Encoder....!");

        //start frame
        StartFrame startFrame = new StartFrame();

        //wait for the user to finish his interactions
        try {
            startFrame.waitForStart(); // Wait for the start button to be clicked
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //start to calculate run-time
        long start = System.currentTimeMillis();

        if(startFrame.getChoice() == 'c') {
            //encode the bytes of input file
            ByteCounter bc = new ByteCounter();
            Encoder huffmanEncoder = new Encoder(startFrame.getFilePath(), bc);
            huffmanEncoder.encode();

            Hashtable<Byte, String> codes = huffmanEncoder.getHuffmanCodes();

            for (byte b : codes.keySet()) {
                System.out.println(b + " : " + codes.get(b));
            }

            HeaderMaker headerMaker = new HeaderMaker(bc, huffmanEncoder);

            //start compress the file
            try {
                Compressor compressor = new Compressor(headerMaker, huffmanEncoder.getHuffmanCodes());
                compressor.writeCompressedFile(startFrame.getFilePath(), (startFrame.getFilePath() + ".hc"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                Decompressor decompressor = new Decompressor();
                String fileName = startFrame.getFileName();
                fileName = fileName.replace(".hc", "");
                String[] name = fileName.split("\\.");
                name[0] = name[0] + "_decompressed";
                String outputFileName = name[0] + "." + name[1];
                String outputFilePath = startFrame.getFilePath().replace(fileName+".hc", outputFileName);
                System.out.println("output file: " + outputFilePath);
                decompressor.decompress(startFrame.getFilePath(), outputFilePath);//startFrame.getFilePath());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        long end = System.currentTimeMillis();
        long runtime = end-start;

        //give the user notification that the file is done
        System.out.println("this code took: " + runtime + " ms" + " == " + (runtime/1000) + " sec");
    }
}