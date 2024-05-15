package org.example.ArithmeticCoding;

import org.example.Utils.BitOutputStream;
import org.example.Utils.ArithmeticUtils;

import java.io.*;

public class ArithmeticEncoder {
    private final BitOutputStream bitOutputStream;
    private long numUnderflow;  // Number of saved underflow bits

    private final int numBits;
    private final long halfRange;
    private final long quarterRange;
    private final long bitMask;

    private long low;
    private long high;


    public ArithmeticEncoder(int numBits, BitOutputStream bitOutputStream) {
        this.numBits = numBits;
        long fullRange = 1L << numBits;    // 2^numBits
        this.halfRange = fullRange >>> 1;  // 2^(numBits-1) "Non-zero"
        this.quarterRange = halfRange >>> 1;  // 2^(numBits-2) "Can be zero"
        this.bitMask = fullRange - 1; // numBits ones

        this.low = 0;
        this.high = bitMask;
        this.bitOutputStream = bitOutputStream;
        numUnderflow = 0;
    }

    public static ArithmeticUtils calcFrequencyArr(File file) throws IOException {
        ArithmeticUtils arithmeticUtils = new ArithmeticUtils();
        try(InputStream input = new BufferedInputStream(new FileInputStream(file))) {
            while(true) {
                int symbol = input.read();
                if(symbol == -1)
                    break;
                arithmeticUtils.incrementFrequency(symbol);
            }
        }
        arithmeticUtils.incrementFrequency(ArithmeticUtils.BYTE_NUM_VALUES); // EOF symbol gets a frequency of 1
        return arithmeticUtils;
    }

    public void writeFrequencyArr(ArithmeticUtils arithmeticUtils) throws IOException {
        for(int i = 0; i< ArithmeticUtils.BYTE_NUM_VALUES; i++) {
            // for each byte value: get freq then write it to file
            int value = arithmeticUtils.getFrequency(i);
            // write freq value
            for(int j=numBits-1; j>=0; j--) {
                bitOutputStream.writeNextBit((value >>> j) & 1);  // read from the MSB to LSB (Big Indian)
            }
        }
    }

    public void compress(ArithmeticUtils arithmeticUtils, InputStream inputStream) throws IOException {
        while(true) {
            int symbol = inputStream.read();
            if(symbol == -1)
                break;
            updateRange(arithmeticUtils, symbol);
        }
        updateRange(arithmeticUtils, ArithmeticUtils.BYTE_NUM_VALUES);  // EOF
        bitOutputStream.writeNextBit(1);
    }

    private void updateRange(ArithmeticUtils arithmeticUtils, int symbol) throws IOException {
        if(low >= high || (low & bitMask) != low || (high & bitMask) != high)
            throw new AssertionError("Out of Range");

        long range = high - low + 1;
        long total = arithmeticUtils.getNumSymbols();
        long symbolLow = arithmeticUtils.getLowRange(symbol);
        long symbolHigh = arithmeticUtils.getHighRange(symbol);

        // Update low and high
        long newLow  = low + symbolLow * range / total;
        long newHigh = low + (symbolHigh * range / total - 1);
        low = newLow;
        high = newHigh;


        // While low and high have the common MSB, shift them out to save space
        // MSB is the same for low and high
        while(((low ^ high) & halfRange) == 0) {
            int bit = (int)(low >>> (numBits - 1));
            bitOutputStream.writeNextBit(bit);

            // Write out the saved underflow bits
            while(numUnderflow > 0) {
                bitOutputStream.writeNextBit(bit ^ 1);
                numUnderflow--;
            }
            low = ((low << 1) & bitMask); // low = 2 * low
            high = ((high << 1) & bitMask) | 1; // high = 2 * high + 1
        }

        // Now low's LSB bit must be 0 and high's LSB bit must be 1
        // While low's two LSB are 10 and high are 01, delete the second LSB bit of both
        while((low & ~high & quarterRange) != 0) {
            if(numUnderflow == Long.MAX_VALUE) {
                throw new ArithmeticException("Maximum underflow");
            }
            numUnderflow++;
            low = (low << 1) ^ halfRange; // low = 2 * low - N
            high = ((high ^ halfRange) << 1) | halfRange | 1; // high = 2 * high - N
        }
    }

}
