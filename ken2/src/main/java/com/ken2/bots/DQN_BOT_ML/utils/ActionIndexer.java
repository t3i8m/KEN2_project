package com.ken2.bots.DQN_BOT_ML.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ken2.engine.Move;

public class ActionIndexer {
    private Map<Move, Integer> moveToIndex;
    private Map<Integer, Move> indexToMove;
    private int currentIndex;

    public ActionIndexer(){
        moveToIndex = new HashMap<>();
        indexToMove = new HashMap<>();
        currentIndex = 0;
    }

    public int getIndex(Move move){
        if (!moveToIndex.containsKey(move)) {
            moveToIndex.put(move, currentIndex);
            indexToMove.put(currentIndex, move);
            currentIndex++;
        }
        return moveToIndex.get(move);
    }

    public Move getMove(int index){
        return indexToMove.get(index);
    }

    public int size(){
        return currentIndex;
    }

    public void initializeActions(List<Move> possibleMoves) {
    for (Move move : possibleMoves) {
        getIndex(move);
    }
}



}
