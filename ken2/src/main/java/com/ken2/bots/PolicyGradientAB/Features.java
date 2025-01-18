package com.ken2.bots.PolicyGradientAB;

import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Vertex;

public class Features {

    // Make 4 tables for the white and black rings and chips

    private int[] whiteRings;
    private int[] blackRings;
    private int[] whiteCoins;
    private int[] blackCoins;

    // Class that makes the feature matrix for each step of the game to train the model

    public Features(Game_Board gameBoard) {
        int verticesNumber = 86;

        whiteRings = new int[verticesNumber];
        blackRings = new int[verticesNumber];
        whiteCoins = new int[verticesNumber];
        blackCoins = new int[verticesNumber];

        // iterating the board and filling in the respective vertices
        for (int i = 0; i < verticesNumber; i++) {
            Vertex curr = gameBoard.getVertex(i);

            if (curr.hasRing()){
                if (curr.getRing().getColour().equals("white")){
                    whiteRings[i] = 1;
                    blackRings[i] = 0;
                }
                else if (curr.getRing().getColour().equals("black")){
                    whiteRings[i] = 0;
                    blackRings[i] = 1;
                }
            }
            else {
                whiteRings[i] = 0;
                blackRings[i] = 0;
            }

            if (curr.hasCoin()){
                if (curr.getCoin().getColour().equals("white")){
                    whiteCoins[i] = 1;
                    blackCoins[i] = 0;
                }
                else if (curr.getCoin().getColour().equals("black")){
                    whiteCoins[i] = 0;
                    blackCoins[i] = 1;
                }
            }
            else {
                whiteCoins[i] = 0;
                blackCoins[i] = 0;
            }
        }

    }

    // return the feature list as a single

    public int[][] get_features(){
        return new int[][]{whiteRings, blackRings, whiteCoins, blackCoins};
    }





    // GETTERS BELOW THIS POINT !!

    public int[] getWhiteRings() {
        return whiteRings;
    }

    public int[] getBlackRings() {
        return blackRings;
    }

    public int[] getWhiteCoins() {
        return whiteCoins;
    }

    public int[] getBlackCoins() {
        return blackCoins;
    }

}
