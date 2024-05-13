package org.example;

import java.io.*;
import java.util.Hashtable;

public class Compressor {

    private long headerSize;
    private long originFileSize;
    private long compressedFileSize;

    HeaderMaker headerMaker;
    Hashtable<Byte, String> codes;
    public Compressor(HeaderMaker headerMaker, Hashtable<Byte, String> codes){
        this.headerMaker = headerMaker;
        this.codes = codes;
    }

    /**
     *
     * @param filePath the path for the original file
     * @param compressedFile the path for the compressed file
     * @return double represents the ratio between original and compressed file
     * @throws IOException if there is problem handling the files
     */
    public void compressFile(String filePath, String compressedFile) throws IOException {

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(compressedFile)));

        // write data for decompress
        long numsInput = 0;
        long numsOutput = 0;
        oos.writeInt(1);

        Hashtable<String, Byte> hashMap = this.headerMaker.makeHeader();
        oos.writeObject(hashMap); //write hashtable of the codes

        InputStream in = new FileInputStream(compressedFile);
        numsOutput += in.readAllBytes().length; //calculate number of bytes in the output file until now
        in.close();

        this.headerSize = numsOutput;
        numsOutput = 0;

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
        this.originFileSize = numsInput;
        this.compressedFileSize = numsOutput;
    }

    public long getHeaderSize(){
        return this.headerSize;
    }

    public long getOriginFileSize(){
        return this.originFileSize;
    }

    public long getCompressedFileSize(){
        return this.compressedFileSize;
    }
}