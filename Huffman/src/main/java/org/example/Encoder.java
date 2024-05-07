package org.example;

import java.io.IOException;
import java.util.Hashtable;
import java.util.PriorityQueue;

public class Encoder {
    private String filePath;
    private Hashtable<Byte, String> huffmanCodes = new Hashtable<Byte, String>();

    public Encoder(String filePath){
        this.filePath = filePath;
    }

    private void generateCodes(Node head, String code){
        if(head.left == null && head.right == null){
            head.setCode(code);
            this.huffmanCodes.put(head.b, head.getCode());
        }
        if(head.left != null)
            generateCodes(head.left, code+"0");
        if(head.right != null)
            generateCodes(head.right, code+"1");
    }

    public void encode(){
        ByteCounter bc = new ByteCounter();
        try {
            bc.countBytesFrequency(filePath);
        } catch (IOException e) {
            System.out.println("couldn't count frequency");
            throw new RuntimeException(e);
        }
        Hashtable<Byte, Integer> freq = bc.getFrequency();

        PriorityQueue<Node> heap = new PriorityQueue<>();
        for(byte b: freq.keySet()){
            Node temp = new Node(b, freq.get(b));
            heap.add(temp);
        }

        while(heap.size()!=1){
            Node left = heap.poll();
            Node right = heap.poll();
            Node temp = new Node((byte)0, left, right, (left.freq+right.freq));
            heap.add(temp);
        }

        generateCodes(heap.poll(), "");
    }


    public Hashtable<Byte, String> getHuffmanCodes(){
        return this.huffmanCodes;
    }

}
