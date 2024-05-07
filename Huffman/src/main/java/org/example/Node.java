package org.example;

public class Node {
    byte b;
    Node left=null;
    Node right=null;

    public Node(byte b){
        this.b=b;
    }

    public Node(byte b, Node left, Node right){
        this.b = b;
        this.left=left;
        this.right=right;
    }
}
