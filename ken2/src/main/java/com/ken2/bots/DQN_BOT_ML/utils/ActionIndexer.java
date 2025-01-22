package com.ken2.bots.DQN_BOT_ML.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ken2.engine.Move;
/**
 * Maps actions (represented as moves) to unique indices and vice versa.
 * Useful for encoding moves into numerical representations for machine learning models.
 */
public class ActionIndexer {
    private Map<Move, Integer> moveToIndex;
    private Map<Integer, Move> indexToMove;
    private int currentIndex;

    /**
     * Constructs an empty ActionIndexer with no predefined moves.
     */
    public ActionIndexer(){
        moveToIndex = new HashMap<>();
        indexToMove = new HashMap<>();
        currentIndex = 0;
    }

    /**
     * Gets the index of a move, assigning a new index if the move is not yet indexed.
     *
     * @param move The move to get or assign an index for.
     * @return The index of the move.
     */
    public int getIndex(Move move){
        if (!moveToIndex.containsKey(move)) {
            moveToIndex.put(move, currentIndex);
            indexToMove.put(currentIndex, move);
            currentIndex++;
        }
        return moveToIndex.get(move);
    }

    /**
     * Retrieves the move corresponding to a given index.
     *
     * @param index The index of the move.
     * @return The move corresponding to the index, or null if the index does not exist.
     */
    public Move getMove(int index){
        return indexToMove.get(index);
    }

    /**
     * Returns the total number of indexed moves.
     *
     * @return The current size of the index.
     */
    public int size(){
        return currentIndex;
    }
    
    /**
     * Initializes the action indexer with a list of possible moves.
     * Assigns indices to all moves in the provided list.
     *
     * @param possibleMoves The list of moves to index.
     */
    public void initializeActions(List<Move> possibleMoves) {
    for (Move move : possibleMoves) {
        getIndex(move);
    }
}



}
