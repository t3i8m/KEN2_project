package com.ken2.headless;

/**
 * A container class to hold information about each move for CSV export.
 */
public class GameMoveRecord {

    int    gameIndex;
    int    moveNumber;
    String currentPlayer;
    String moveType;              // "RingPlacement" or "RingMovement"
    int    fromVertex;
    int    toVertex;
    int    chipsRemaining;
    int    whiteScore;
    int    blackScore;
    String coinsFlippedVertices;  // e.g. "12;13;14"
    String gameResult;            // "white", "black", "draw", "u" (if the game ended on this move)

    /**
     * Builds a CSV line from the record fields.
     */
    public String toCsvLine() {
        return String.format(
            "%d,%d,%s,%s,%d,%d,%d,%d,%d,%s,%s",
            gameIndex,
            moveNumber,
            currentPlayer,
            moveType,
            fromVertex,
            toVertex,
            chipsRemaining,
            whiteScore,
            blackScore,
            coinsFlippedVertices == null ? "" : coinsFlippedVertices,
            gameResult == null ? "" : gameResult
        );
    }
}
