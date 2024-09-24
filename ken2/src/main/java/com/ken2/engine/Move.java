package com.ken2.engine;

public class Move {
    private int startPoint;
    private int endPoint;

    public Move(int start, int end){
        this.startPoint = start;
        this.endPoint = end;
    }

    public int getStart(){
        return this.startPoint;
    }

    public int getEnd(){
        return this.endPoint;
    }
}
