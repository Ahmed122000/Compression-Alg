package org.example;

import java.io.IOException;
import java.util.Hashtable;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");
        MyFrame startFrame = new MyFrame();

        try {
            startFrame.waitForStart(); // Wait for the start button to be clicked
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        long start = System.currentTimeMillis();
        Encoder huffmanEncoder = new Encoder(startFrame.getFilePath());
        huffmanEncoder.encode();
        Hashtable<Byte, String> codes = huffmanEncoder.getHuffmanCodes();

        for(byte b: codes.keySet()){
            System.out.println(b + " : " + codes.get(b));
        }



        long end = System.currentTimeMillis();

        long runtime = end-start;

        System.out.println("this code took: " + runtime + " ms" + " == " + (runtime/1000) + " sec");



    }
}