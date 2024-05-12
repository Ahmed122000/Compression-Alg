package org.example;

import java.io.*;
import java.util.Hashtable;

public class ByteCounter{
    private Hashtable<Byte, Long> frequency = new Hashtable<Byte, Long>();


    public void countBytesFrequency(String filePath) throws IOException {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                System.out.println("We read: " + bytesRead + " bytes");
                for(int i =0; i < bytesRead; i++){
                    System.out.println("Byte: " + buffer[i] + " represents:  " + (char)buffer[i]);
                    if(frequency.containsKey(buffer[i])){
                        frequency.put(buffer[i], frequency.get(buffer[i])+1);
                    }
                    else{
                        frequency.put(buffer[i], (long)1);
                    }
                }
            }

            bis.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error in Opening the file");
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }

    public Hashtable<Byte, Long> getFrequency(){
        return this.frequency;
    }

}
