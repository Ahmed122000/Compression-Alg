package org.example;

import java.io.*;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class Compressor {
    private final int bufferSize = 1024;

    HeaderMaker headerMaker;
    Hashtable<Byte, String> codes;
    public Compressor(HeaderMaker headerMaker, Hashtable<Byte, String> codes){
        this.headerMaker = headerMaker;
        this.codes = codes;
    }



    public double compressFile(String filePath, String compressedFile, int n, Node root) throws IOException, ClassNotFoundException {

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(compressedFile)));

        // write data for decompress
        long numsInput = 0;
        long numsOutput = 0;
        oos.writeInt(n);

        Hashtable<String, Byte> hashMap = this.headerMaker.makeHeader();
        oos.writeObject(hashMap); //write hashtable of the codes

        InputStream in = new FileInputStream(compressedFile);
        numsOutput += in.readAllBytes().length; //calculate number of bytes in the output file until now
        in.close();

        //write compressed file
        try (InputStream input = new FileInputStream(filePath)) {
            byte[] buffer = new byte[2000];
            byte[] output = new byte[2000];
            int numBytes = 0;
            int bytesRead;
            String carry = "";

            while ((bytesRead = input.read(buffer)) != -1) {
                numsInput += bytesRead;
                for (int x = 0; x < bytesRead; x++){
                    String codeWord = carry + codes.get(buffer[x]);
                    if (codeWord.length() < 8){
                        carry = codeWord;
                        continue;
                    }
                    int overflowBits = codeWord.length() % 8;
                    String toWrite = codeWord.substring(0, codeWord.length() - overflowBits);
                    carry = codeWord.substring(codeWord.length() - overflowBits);
                    for (int i = 0; i < toWrite.length(); i += 8){
                        String byteStr = toWrite.substring(i, i + 8);
                        int byteValue = Integer.parseInt(byteStr, 2);
                        output[numBytes++] = (byte) byteValue;
                        if (numBytes == output.length){
                            numsOutput += numBytes;
                            oos.write(output);
                            numBytes = 0;
                        }
                    }
                }
            }
            if (numBytes > 0){
                oos.write(output, 0, numBytes);
                numsOutput += numBytes;
            }
            if (!carry.equals("")){
                int padding = 8 - carry.length() % 8;
                carry += "0".repeat(padding);
                int byteValue = Integer.parseInt(carry, 2);
                oos.writeByte(byteValue);
                numsOutput += 1;
            }
        }
        oos.flush();
        oos.close();
        return numsOutput * 1.0 / numsInput;
    }
    /**
     * Function to write the file after compress its bytes
      * @param inputFilePath, Path to the input file
     * @param outputFilePath, Path to the output file
     * @throws IOException, when error occurs during reading/writing files
     */

    public void writeCompressedFile(String inputFilePath, String outputFilePath) throws IOException {

        //serialize the header into raw bytes to write them into file
        byte[] serializedHeader;
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream)) {
            objectOutputStream.writeObject(headerMaker.makeHeader());
            serializedHeader = byteStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Problem with the Header");
        }


        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFilePath));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath));

        //write the header separated after that with \r then newline
        bos.write(serializedHeader);
        bos.write(new byte[]{'\r', '\n'});

        //to hold the input bytes from file to compress them
        int bytesRead;
        byte[] inputBuffer = new byte[bufferSize];

        //to buffer compressed bytes before write them back into output file
        byte[] outputBuffer = new byte[bufferSize];

        //to follow the bits of Huffman code and save them as bytes
        byte collectedBits = 0;
        byte codeBits = 0;

        int outputIndex = 0;
        while ((bytesRead = bis.read(inputBuffer)) != -1) {
            outputIndex = 0;

            for (int b = 0; b < bytesRead; b++) {
                String code = this.codes.get(inputBuffer[b]);
                for (int i = 0; i < code.length(); i++) {
                    codeBits <<= 1;
                    if (code.charAt(i) == '1') {
                        codeBits |= 1;
                    }
                    collectedBits++;
                    if (collectedBits == 8) {
                        outputBuffer[outputIndex++] = codeBits;
                        if (outputIndex == bufferSize) {
                            bos.write(outputBuffer);
                            outputIndex = 0;
                        }
                        collectedBits = 0;
                        codeBits = 0;
                    }
                }
            }
        }

// Write any remaining bytes in the output buffer
        if (collectedBits > 0) {
            codeBits <<= (8 - collectedBits); // Shift remaining bits to the leftmost position
            outputBuffer[outputIndex++] = codeBits;
        }

        bos.write(outputBuffer, 0, outputIndex);
        bos.close();
        bis.close();
    }
}