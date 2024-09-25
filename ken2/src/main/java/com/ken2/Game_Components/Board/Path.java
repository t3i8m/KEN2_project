package com.ken2.Game_Components.Board;

import java.util.List;

public class Path {
    private List<String> moves;

    public Path(List<String> moves) {
        this.moves = moves;
    }
    public List<String> getMoves() {
        return moves;
    }
    public void setMoves(List<String> moves) {
        this.moves = moves;
        }

    public interface GameStuff {
        Coin createCoin(String color);
        Ring createRing(String color);
        Board createBoard(int size);
        Path createPath(List<String> moves);
    }
}

