package com.ken2.bots;

import java.util.ArrayList;

import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;

// interface for all of the bots
public interface Bot {
    String getColor();
    String getName();
    Move makeMove(GameState state);
    Vertex removeRing(GameState state);
    ArrayList<Integer> removeChips(GameState state);
}
