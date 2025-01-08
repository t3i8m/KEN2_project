package com.ken2.bots.MLbot.utils;

import com.ken2.Game_Components.Board.Coin;
import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Ring;
import com.ken2.Game_Components.Board.Vertex;

public class BoardTransformation {
    Vertex[][] board;
    public BoardTransformation(Game_Board board){
        this.board = board.getBoard();
    }

    // 0 - empty cell
    // 1 - WHITE RING
    // 2 - BLACK RING
    // 3 - WHITE COIN
    // 4 - BLACK COIN
    public int[] toVector(){
        int[] finalVector = new int[85];
        int index = 0;
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                if(board[i][j]!=null){
                    if(board[i][j].hasRing()){
                        switch (board[i][j].getRing().getColour().toLowerCase()) {
                            case "white":
                                finalVector[index] = 1;
                                break;
                            case "black":
                                finalVector[index] = 2;
                                break;
                        }
                    } else if(board[i][j].hasCoin()){
                        switch (board[i][j].getCoin().getColour().toLowerCase()) {
                            case "white":
                                finalVector[index] = 3;
                                break;
                            case "black":
                                finalVector[index] = 4;
                                break;
                        }
                    } else{
                        finalVector[index] = 0;
                    }
                    index+=1;
                }
            }
        }
        return finalVector;
    }


       public Game_Board fromVector(int[] vector) {
            Vertex[][] reconstructedBoard = new Vertex[this.board.length][this.board[0].length];
            int index = 0;
            int vertexNumber=0;
            for (int i = 0; i < this.board.length; i++) {
                for (int j = 0; j < this.board[i].length; j++) {
                    if (this.board[i][j] != null) {
                        Vertex vertex = new Vertex(i,j); 
                        vertex.setVertexNumber(vertexNumber);
                        switch (vector[index]) {
                            case 1: // WHITE RING
                                Ring newRingW = new Ring("white");
                                vertex.setPlayObject(newRingW);
                                vertex.setRing(newRingW);
                                break;
                            case 2: // BLACK RING
                                Ring newRing = new Ring("black");
                                vertex.setPlayObject(newRing);
                                vertex.setRing(newRing);
                                break;
                            case 3: // WHITE COIN
                                Coin newCoin = new Coin("white");
                                vertex.setPlayObject(newCoin);
                                vertex.setCoin(newCoin);
                                break;
                            case 4: // BLACK COIN
                                Coin newCoinB = new Coin("black");
                                vertex.setPlayObject(newCoinB);
                                vertex.setCoin(newCoinB);
                                break;
                            case 0: // EMPTY CELL
                                // vertex.setPlayObject(null);
                            default:
                                vertex.setPlayObject(null);
                                break;
                        }

                        reconstructedBoard[i][j] = vertex; 
                        index++;
                        vertexNumber++;  

                    }
                }
            }
            Game_Board gb = new Game_Board();
            gb.the_Board = reconstructedBoard;
            return gb;
        }
}

