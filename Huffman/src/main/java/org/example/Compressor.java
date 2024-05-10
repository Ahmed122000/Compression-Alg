package org.example;

import java.io.*;
import java.nio.Buffer;
import java.util.Hashtable;


public class Compressor {
    private final int bufferSize = 1024;

    HeaderMaker headerMaker;
    Hashtable<Byte, String> codes;
    public Compressor(HeaderMaker headerMaker, Hashtable<Byte, String> codes){
        this.headerMaker = headerMaker;
        this.codes = codes;
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
        try(ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream)){
            objectOutputStream.writeObject(headerMaker.makeHeader());
            serializedHeader = byteStream.toByteArray();
        }
        catch (IOException e){
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


        while ((bytesRead=bis.read(inputBuffer)) != -1){
            int outputIndex = 0;

            for(int b = 0; b < bytesRead; b++){
                String code = this.codes.get(inputBuffer[b]);                //get the code of the byte
                for(int i = 0; i < code.length(); i++){
                    codeBits <<= 1;
                    if (code.charAt(i) == '1'){
                        codeBits |=1;                           //if 1 or with 1 then shift left
                    }
                    collectedBits++;                            //count the bits collected
                    if(collectedBits == 8){
                        outputBuffer[outputIndex++] = codeBits; //if we collect byte then save it in the buffer
                        collectedBits = 0;                      //start counting from again
                        codeBits = 0;                           //empty the byte for the new compressed byte
                    }
                    //else{
                    //    codeBits <<=1;
                   // }

                    if(outputIndex == bufferSize){
                        bos.write(outputBuffer);
                        outputIndex=0;
                    }
                }

            }

            bos.write(outputBuffer, 0, outputIndex);
        }
        //outputBuffer[outputIndex] = codeBits;
        bos.write(codeBits);
        bos.write('\0');
        bos.close();
        bis.close();
    }
}
