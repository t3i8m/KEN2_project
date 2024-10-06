package com.ken2.engine;

import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.PlayObj;
import com.ken2.Game_Components.Board.Ring;

public class GameEngineGUI {

    private Game_Board theBoard;

    public GameEngineGUI() {

        this.theBoard = new Game_Board();

    }

    public boolean hasRing(int vertex){
        return (theBoard.getPlayObject(vertex)[0]!=null);
    }

    public boolean hasCoin(int vertex){
        return (theBoard.getPlayObject(vertex)[1]!=null);
    }

    public boolean isWhiteRing(int vertex){
        return (theBoard.getPlayObject(vertex)[0].getColour().equals("white"));
    }

    public PlayObj[] getPlayObj(int vertex){
        System.out.println("!!!!!!!!!!!!!!!!!!!VERTEX: "+vertex);
        return theBoard.getPlayObject(vertex);
    }

    public void updateBoard(int vertex, PlayObj playObj){
        theBoard.updateBoard(vertex, playObj);
    }
}
