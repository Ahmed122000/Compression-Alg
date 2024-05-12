package org.example;

import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class Decompressor {

    private final int bufferSize = 1024; // Adjusted buffer size


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

    /**
     * Function to decompress the compress file
     *
     * @param inputFilePath  the path to the compress file
     * @param outputFilePath the path to write the decompressed file
     * @throws IOException            in case there was any error reading/writing the file
     * @throws ClassNotFoundException in case there was error reading the header from compressed file
     */
    public void decompress(String inputFilePath, String outputFilePath) throws IOException, ClassNotFoundException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFilePath));
        ObjectInputStream objectStream = new ObjectInputStream(bis);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath));

        Hashtable<String, Byte> dhashtable = (Hashtable<String, Byte>) objectStream.readObject();

        int newlineChar = bis.read();
        if (newlineChar != '\r') {
            throw new IOException("Expected new line character not found");
        }
        bis.read(); // Skip '\n'

        int bytesRead;
        byte[] inputBuffer = new byte[bufferSize];
        byte[] outputBuffer = new byte[bufferSize];
        int outputBufferIndex = 0;

        String code = "";

        while ((bytesRead = bis.read(inputBuffer)) != -1) {
            for (int i = 0; i < bytesRead; i++) {
                boolean bits[] = getBits(inputBuffer[i]);

                for (int j = 0; j < 8; j++) {
                    code += bits[j] ? '1' : '0';

                    if (dhashtable.containsKey(code)) {
                        outputBuffer[outputBufferIndex++] = dhashtable.get(code);
                        code = "";

                        if (outputBufferIndex == bufferSize) {
                            bos.write(outputBuffer, 0, outputBufferIndex);
                            outputBufferIndex = 0;
                        }
                    }
                }
            }
        }

        // Write any remaining bytes in the output buffer
        if (outputBufferIndex > 0) {
            bos.write(outputBuffer, 0, outputBufferIndex);
        }

        // Check for the end marker (\0)
        int endMarker = bis.read();
        if (endMarker != -1 && endMarker != '\0') {
            throw new IOException("Invalid end marker");
        }

        bos.close();
        bis.close();
    }

    // Utility method to get bits of a byte
    private boolean[] getBits(byte b) {
        boolean[] bits = new boolean[8];
        for (int i = 0; i < 8; i++) {
            bits[i] = ((b >> i) & 1) == 1;
        }
        // Reverse the order of bits in the bits array
        for (int i = 0; i < 4; i++) {
            boolean temp = bits[i];
            bits[i] = bits[7 - i];
            bits[7 - i] = temp;
        }

        return bits;
    }
}
