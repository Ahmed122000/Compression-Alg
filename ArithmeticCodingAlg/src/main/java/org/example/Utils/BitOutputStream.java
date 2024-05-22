package org.example.Utils;

import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream implements AutoCloseable {
    private final OutputStream outputStream;
    private int currentByte; // value current byte [0,255]
    private int numBitsFilled; // filled bits [0,7] in current byte


    public BitOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        currentByte = 0;
        numBitsFilled = 0;
    }

    public void writeNextBit(int bit) throws IOException {
        // write the current byte from the MSB to LSB (Big Indian)
        /*
         left shift the current byte by 1 to clear the LSB and set it the value of bit by Bitwise OR with it
         eg- (00000000 << 1) | 1 = 00000001  , (00000001 << 1) | 0 = 00000010
         */
        currentByte = (currentByte << 1) | bit;
        numBitsFilled++;
        if(numBitsFilled == 8) {
            // write byte when it is completed
            outputStream.write(currentByte);
            currentByte = 0;
            numBitsFilled = 0;
        }
    }

    @Override
    public void close() throws IOException {
        // pad last byte if req then write it then close output stream
        while(numBitsFilled != 0)
            writeNextBit(0);
        outputStream.close();
    }

}
