package org.example.Utils;

public class ArithmeticUtils {

    public static int BYTE_NUM_VALUES = 256;
    public static int SYMBOL_NUM_VALUES = BYTE_NUM_VALUES + 1; // include terminator
    private long totalNumSymbols;
    private boolean isProbCalculated;
    private int[] frequency;
    private long[] accProb;

    public ArithmeticUtils() {
        this.frequency = new int[SYMBOL_NUM_VALUES];
        this.accProb = new long[SYMBOL_NUM_VALUES+1];
        this.totalNumSymbols = 0;
        this.isProbCalculated = false;
    }

    public ArithmeticUtils(int[] frequency) {
        this.frequency = frequency;
        this.accProb = new long[frequency.length+1];
        this.totalNumSymbols = 0;
        for (int j : frequency)
            totalNumSymbols += j;
        calculateAccProb();
    }

    private void calculateAccProb() {
        long probSum = 0;
        for(int i=0; i<frequency.length; i++) {
            probSum += frequency[i];
            accProb[i+1] = probSum;
        }
        isProbCalculated = true;
    }


    public void incrementFrequency(int symbol) {
        if(!isValidSymbol(symbol))
            throw new IllegalArgumentException("Invalid Symbol, Out Of Range");
        frequency[symbol]++;
        totalNumSymbols++;
        isProbCalculated = false;
    }

    public long getLowRange(int symbol) {
        if(!isValidSymbol(symbol))
            throw new IllegalArgumentException("Invalid Symbol, Out Of Range");
        if(!isProbCalculated)
            calculateAccProb();
        return accProb[symbol];
    }

    public long getHighRange(int symbol) {
        if(!isValidSymbol(symbol))
            throw new IllegalArgumentException("Invalid Symbol, Out Of Range");
        if(!isProbCalculated)
            calculateAccProb();
        return accProb[symbol+1];
    }

    public int getFrequency(int symbol) {
        if(!isValidSymbol(symbol))
            throw new IllegalArgumentException("Invalid Symbol, Out Of Range");
        return frequency[symbol];
    }

    private boolean isValidSymbol(int symbol) {
        return ((symbol >= 0) && (symbol < frequency.length));
    }

    public long getNumSymbols() {
        return totalNumSymbols;
    }

    public int getNumUniqueSymbols() {
        return frequency.length;
    }
}
