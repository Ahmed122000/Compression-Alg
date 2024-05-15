package org.example.Utils;

import java.io.IOException;
import java.io.InputStream;

public class BitInputStream implements AutoCloseable {
    private final InputStream inputStream;
    private int currentByte; // value current byte [0,255]
    private int numBitsFilled; // filled bits [0,7] in current byte

    public BitInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        currentByte = 0;
        numBitsFilled = 0;
    }

    public int read() throws IOException {
        // read next bit in the current byte of input stream and current byte from the MSB to LSB (Big Indian)
        if(currentByte == -1)
            return -1;

        // if there is no remaining bits in the current byte, get the next byte
        if(numBitsFilled == 0) {
            currentByte = inputStream.read();
            if(currentByte == -1)
                return -1;
            numBitsFilled = 8;
        }

        if(numBitsFilled <= 0)
            throw new AssertionError("No Bits to read");
        numBitsFilled--;

        /*
         right shift by the number of remaining bit and clear any other bit by Bitwise AND with 1
         eg- (10010111 >>> 7) & 1 = 1  , (10010111 >>> 5) & 1 = 0
        */
        return (currentByte >>> numBitsFilled) & 1;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
        currentByte = -1;
        numBitsFilled = 0;
    }
}
