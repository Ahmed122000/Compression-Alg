package org.example;

public class Node implements Comparable<Node>{

    byte b;
    long freq;
    Node left=null;
    Node right=null;
    private String code = "";

    public Node(byte b, long freq){
        this.b=b;
        this.freq = freq;
    }

    public Node(byte b, Node left, Node right, long freq){
        this.b = b;
        this.left=left;
        this.right=right;
        this.freq=freq;
    }

    public void setCode(String code){
        this.code = code;
    }
    public String getCode(){
        return this.code;
    }


    @Override
    public int compareTo(Node other) {
        return Long.compare(this.freq, other.freq);
    }
}
