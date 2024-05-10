package org.example;

import java.io.*;
import java.util.Hashtable;

public class Decompressor {

    private final int bufferSize = 1024;
    /**
     * function to get the bits of the provided byte as booleans (1=true, 0=false)
     * @param b, the byte to extract its bits
     * @return bits, array of booleans represents the bits of the byte
     */
    public static boolean[] getBits(byte b) {
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

    /**
     * Function to decompress the compress file
     * @param inputFilePath the path to the compress file
     * @param outputFilePath the path to write the decompressed file
     * @throws IOException in case there was any error reading/writing the file
     * @throws ClassNotFoundException in case there was error reading the header from compressed file
     */
    public void decompress(String inputFilePath, String outputFilePath) throws IOException, ClassNotFoundException {

        //to read the compressed file
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFilePath));
        ObjectInputStream objectStream = new ObjectInputStream(bis);

        //to write the decompressed file
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath));

        //getting the header which is Hashtable of codes and last byte bits
        Hashtable<String, Byte> dhashtable = (Hashtable<String, Byte>) objectStream.readObject();


        //make sure header was read correctly (it followed by /r/n)
        int newlineChar = bis.read();
        if(newlineChar != '\r'){
            throw new IOException("expected new line character not found");
        }
        bis.read(); //to remove \n character

        int bytesRead;
        byte[] inputBuffer = new byte[bufferSize];

        //follow the output buffer so it doesn't overflow
        int outputBufferIndex = 0;
        byte[] outputBuffer = new byte[bufferSize];


        String code = "";

        //while the file still has bytes
        while((bytesRead=bis.read(inputBuffer)) != -1){
            for(int i =0; i< bytesRead; i++){
                //get the bits for each byte
                boolean bits[] = getBits(inputBuffer[i]);

                //if it's the last byte
                if(i < bufferSize-2 && inputBuffer[i+1] == '\0' ){
                    int start = 8-dhashtable.get("last");
                    for(int j = start ; j < 8 ; j++){
                        if(bits[j]){
                            code+='1';
                        }
                        else{
                            code += '0';
                        }
                        if(dhashtable.keySet().contains(code)){
                            outputBuffer[outputBufferIndex++] = dhashtable.get(code);
                            code = "";
                        }
                    }
                    break;
                }
                else{
                    for (int j = 0; j < 8; j++){
                        if(bits[j]){
                            code+= '1';
                        }
                        else{
                            code+='0';
                        }
                        if(dhashtable.keySet().contains(code)){
                            outputBuffer[outputBufferIndex++] = dhashtable.get(code);
                            code= "";
                        }
                        if(outputBufferIndex == bufferSize){
                            bos.write(outputBuffer);
                            outputBufferIndex=0;
                        }
                    }
                }
            }
            bos.write(outputBuffer, 0, outputBufferIndex);
        }
        bos.close();
        bis.close();
    }
}