package com.ken2.Game_Components.Board;

import com.ken2.engine.GameSimulation;

/**
 * This is a sample of how to run the game board in practice

 * I have made a simple board and just printed it out

 * This is a very simple implementation will work on improving on the side
 */
public class game_board_tester {
    public static void main(String[] args) {
        Game_Board gb1 = new Game_Board();

        gb1.fillBoard();
        GameSimulation gs = new GameSimulation();
        gs.startSimulation(gb1, 5,5);
        System.out.println(gs.getAllPossibleMoves());

        String h = gb1.strMaker();
        System.out.println(h);
    }
}
