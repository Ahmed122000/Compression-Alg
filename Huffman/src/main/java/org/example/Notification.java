package org.example;

import javax.swing.*;

public class Notification {

    char operation;
    long runtime;
    int headerSize;
    long originSize;
    long compressedSize;

    Notification(long runtime){

        this.runtime = runtime;
        JOptionPane.showMessageDialog(null,"Your operation is Done in: " + this.runtime + " millisecond", "Task completed", JOptionPane.PLAIN_MESSAGE);


    }
}
