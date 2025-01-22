package com.ken2.bots.DQN_BOT_ML.utils;

import com.ken2.Game_Components.Board.Coin;
import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Ring;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.engine.GameState;

public class BoardTransformation {
    Vertex[][] board;
    public BoardTransformation(Game_Board board){
        this.board = board.getBoard();
    }
    public BoardTransformation() {
        
    }

    // 0 - empty cell
    // 1 - WHITE RING
    // 2 - BLACK RING
    // 3 - WHITE COIN
    // 4 - BLACK COIN
    // LAST DIGIT:
        // 5 - white player turn
        // 6 - black player turn
        
    public double[] toVector(String currentPlayerColor){
        double[] finalVector = new double[86];
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
        finalVector[index] = currentPlayerColor.toLowerCase().equals("white") ? 5:6;
        return finalVector;
    }

    public GameState fromVectorToState(double[] vector) {
        if (vector.length != 86) {
            throw new IllegalArgumentException(
                "Vector length != 86 " + vector.length);
        }
        
        GameState newState = new GameState();
        
        Game_Board newBoard = new Game_Board();


        
        final int ROWS = 13;
        final int COLS = 7;
        Vertex[][] reconstructedBoard = new Vertex[ROWS][COLS];

        int index = 0;        
        int vertexNumber = 0;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (index < 85) {
                    int cellValue = (int) vector[index];
                    
                    Vertex vertex = new Vertex(i, j);
                    vertex.setVertexNumber(vertexNumber);

                    
                    switch (cellValue) {
                        case 1: // WHITE RING
                            Ring ringW = new Ring("white");
                            vertex.setPlayObject(ringW);
                            vertex.setRing(ringW);
                            break;
                        case 2: // BLACK RING
                            Ring ringB = new Ring("black");
                            vertex.setPlayObject(ringB);
                            vertex.setRing(ringB);
                            break;
                        case 3: // WHITE COIN
                            Coin coinW = new Coin("white");
                            vertex.setPlayObject(coinW);
                            vertex.setCoin(coinW);
                            break;
                        case 4: // BLACK COIN
                            Coin coinB = new Coin("black");
                            vertex.setPlayObject(coinB);
                            vertex.setCoin(coinB);
                            break;
                        default:
                            // 0 или что-то иное — пусто
                            vertex.setPlayObject(null);
                    }

                    reconstructedBoard[i][j] = vertex;

                    index++;
                    vertexNumber++;

                } else {
                    
                    reconstructedBoard[i][j] = null;
                }
            }
        }

        double turnValue = vector[85];
        if ((int)turnValue == 5) {
            newState.setCurrentPlayer("white");
        } else {
            newState.setCurrentPlayer("black");
        }

        newBoard.the_Board = reconstructedBoard;
        newState.gameBoard = newBoard;
        
        return newState;
    }
}


