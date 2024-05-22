package org.example;

import java.util.Hashtable;

public class TerminalMain {

    public static void main(String[] args){
        long startTime=0;
        long endTime=0;

        if(args[0].equals("c")){

            startTime = System.currentTimeMillis();
            ByteCounter bc = new ByteCounter();
            Encoder huffmanEncoder = new Encoder(args[1], bc);
            huffmanEncoder.encode();

            //Hashtable<Byte, String> codes = huffmanEncoder.getHuffmanCodes();

            HeaderMaker headerMaker = new HeaderMaker(bc, huffmanEncoder);

            //start compress the file
            try {
                Compressor compressor = new Compressor(headerMaker, huffmanEncoder.getHuffmanCodes());
                compressor.compressFile(args[1], (args[1] + ".hc"));
                endTime= System.currentTimeMillis();
                long original_size = compressor.getOriginFileSize();
                long header_size = compressor.getHeaderSize();
                long encoded_size = compressor.getCompressedFileSize();

                System.out.println("Encoding & Compression time: " + (endTime-startTime) + " ms");
                System.out.println("Original file size: " + original_size+ " bytes");
                System.out.println("header size: "+ header_size + " bytes");
                System.out.println("Encoded file size: " + encoded_size + " bytes");
                System.out.println("Compressed File size: " + (encoded_size+header_size) + " bytes");
                System.out.println("Compression Percentage without Header: " + (double)(encoded_size/original_size));
                System.out.println("Compression Percentage with header: " + (double)((header_size+encoded_size) / original_size));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (args[0].equals("d")){
            try {
                startTime = System.currentTimeMillis();
                Decompressor decompressor = new Decompressor();
                String outputFilePath = outputFileName(args[1]);
                decompressor.deCompressFile(args[1], outputFilePath);
                endTime = System.currentTimeMillis();
                System.out.println("Decompress & Decoding time: " + (endTime-startTime) + " ms");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            System.out.println("Un-Supported Operation, exit.....");
            System.exit(-1);
        }
    }

    static String outputFileName(String  filePath){
        filePath = filePath.replace(".hc", "");
        String[] name = filePath.split("\\.");
        name[0] = name[0] + "_decompressed";
        String outputFileName = name[0] + "." + name[1];
        String outputFilePath = filePath.replace(filePath, outputFileName);
        return outputFilePath;
    }
}
