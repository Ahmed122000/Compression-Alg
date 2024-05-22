package org.example.ArithmeticCoding;

import org.example.Utils.BitInputStream;
import org.example.Utils.ArithmeticUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;

public class ArithmeticDecoder {
    private final BitInputStream bitInputStream;
    private long tag;

    private final long halfRange;
    private final long quarterRange;
    private final long bitMask;

    private long low;
    private long high;


    public ArithmeticDecoder(int numBits, BitInputStream bitInputStream) throws IOException {
        long fullRange = 1L << numBits;    // 2^numBits
        this.halfRange = fullRange >>> 1;  // 2^(numBits-1) "Non-zero"
        this.quarterRange = halfRange >>> 1;  // 2^(numBits-2) "Can be zero"
        this.bitMask = fullRange - 1; // numBits ones

        this.low = 0;
        this.high = bitMask;
        this.bitInputStream = bitInputStream;
        tag = 0;
        for(int i=0; i<numBits; i++)
            tag = tag << 1 | readNextBit();
    }

    public int read(ArithmeticUtils arithmeticUtils) throws IOException {
        long total = arithmeticUtils.getNumSymbols();
        long range = high - low + 1;
        long offset = tag - low;
        long value = ((offset + 1) * total - 1) / range;
        if((value * range / total) > offset)
            throw new AssertionError();
        if(!(value>=0 && value<total))
            throw new AssertionError();

        // Find the highest symbol such that frequencyTable.getRangeLow(symbol) <= value.
        int start = 0;
        int end = arithmeticUtils.getNumUniqueSymbols();
        while(end - start > 1) {
            int middle = (start + end) / 2; //  middle = (start + end)/2
            if(arithmeticUtils.getLowRange(middle) > value)
                end = middle;
            else
                start = middle;
        }
        if(start+1 != end)
            throw new AssertionError("Symbol Not Found");
        int symbol = start;

        long low_range_total = arithmeticUtils.getLowRange(symbol) * range / total;
        long high_range_total = arithmeticUtils.getHighRange(symbol) * range / total;
        if(!(offset>=low_range_total && offset<high_range_total))
            throw new AssertionError();

        updateRange(arithmeticUtils, symbol);

        if(!(tag >= low && tag <= high))
            throw new AssertionError("tag out of range");
        return symbol;
    }

    // Returns the next bit (0 or 1) from the input stream.
    // The end of stream is treated as an infinite number of trailing zeros.
    private int readNextBit() throws IOException {
        int temp = bitInputStream.read();
        if(temp == -1)
            temp = 0;
        return temp;
    }

    public static ArithmeticUtils readFrequencyArr(int numBits, BitInputStream bitInputStream) throws IOException {
        int[] frequencyTable = new int[ArithmeticUtils.SYMBOL_NUM_VALUES];
        for(int i = 0; i< ArithmeticUtils.BYTE_NUM_VALUES; i++)
            frequencyTable[i] = readInt(bitInputStream, numBits);
        frequencyTable[ArithmeticUtils.BYTE_NUM_VALUES] = 1;  // EOF symbol
        return new ArithmeticUtils(frequencyTable);
    }

    private static int readInt(BitInputStream bitInputStream, int numBits) throws IOException {
        int result = 0;
        for(int i=0; i<numBits; i++) {
            int nextResult = bitInputStream.read();
            if(nextResult != -1) {
                // read the current byte from the MSB to LSB (Big Indian)
                result = (result << 1) | nextResult;
            }
            else {
                throw new EOFException();
            }
        }
        return result;
    }

    public void decompress(ArithmeticUtils arithmeticUtils, OutputStream outputStream) throws IOException {
        while(true) {
            int symbol = read(arithmeticUtils);
            if(symbol == ArithmeticUtils.BYTE_NUM_VALUES) // EOF symbol
                break;
            outputStream.write(symbol);
        }
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
            tag = ((tag << 1) & bitMask) | readNextBit();
            low = ((low << 1) & bitMask); // low = 2 * low
            high = ((high << 1) & bitMask) | 1; // high = 2 * high + 1
        }

        // Now low's LSB bit must be 0 and high's LSB bit must be 1
        // While low's two LSB are 10 and high are 01, delete the second LSB bit of both
        while((low & ~high & quarterRange) != 0) {
            tag = (tag & halfRange) | ((tag << 1) & (bitMask >>> 1)) | readNextBit();
            low = (low << 1) ^ halfRange; // low = 2 * low - N
            high = ((high ^ halfRange) << 1) | halfRange | 1; // high = 2 * high - N
        }
    }
}
