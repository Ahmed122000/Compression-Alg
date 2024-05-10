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
           Decompressor decompressor = new Decompressor();
           decompressor.decompress(startFrame.getFilePath());
        }
        long end = System.currentTimeMillis();
        long runtime = end-start;


        //give the user notification that the file is done
        System.out.println("this code took: " + runtime + " ms" + " == " + (runtime/1000) + " sec");



    }
}