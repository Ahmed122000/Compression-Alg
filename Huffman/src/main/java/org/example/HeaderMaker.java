package org.example;

import java.util.Hashtable;

public class HeaderMaker {

    ByteCounter bc;
    Encoder encoder;
    Hashtable<Byte, String> codes;
    Hashtable<Byte, Long> freq;


    public HeaderMaker(ByteCounter bc, Encoder encoder){
        this.bc = bc;
        this.encoder = encoder;

        this.freq = bc.getFrequency();
        this.codes = encoder.getHuffmanCodes();
    }

    /**
     * Function to count the bits in the last byte in the compressed file
     * @return last, number of correct bits in the last byte
     */
    private byte lastBits(){
        int last = 0;
        for(byte b: codes.keySet()){
            last += (freq.get(b) * codes.get(b).length());
        }

        last %= 8;
        return (byte)last;
    }

    /**
     * makeHeader function to make the header for the compressed file
     * @return header, a Hash table that contains mapping between :
     * 1. Huffman code
     * 2. its corresponding byte in the real file
     * and hold number of bits in the last byte in the compress file
     */
    public Hashtable<String, Byte> makeHeader(){
        Hashtable<String, Byte> header = new Hashtable<String, Byte>();

        for(byte b: this.codes.keySet()){
            header.put(this.codes.get(b), b);
        }
        header.put("last", lastBits());
        return header;
    }
}
