package com.ken2.Game_Components.Board;

// import java.util.Dictionary;
// import java.util.Hashtable;
import java.util.ArrayList;


/**
 * The class that builds the board object for the game
 */
public class Game_Board {
    private Vertex[][] the_Board;   // initialize a blank 2d array of vertices for the board

    private int[][] blanks = {{0,1,2,8,9,10},{0,1,9,10},{0,10},{0,10},{0,10}};  // blank spaces to shape the board

    /**
     * Game Board Object constructor
     */
    public Game_Board() {
        the_Board = new Vertex[19][11];     // Create the blank board
        fillBoard();
    }

    // constructor for cloning
    public Game_Board(Game_Board other) {
        this.the_Board = new Vertex[other.the_Board.length][other.the_Board[0].length];
        for (int i = 0; i < other.the_Board.length; i++) {
            for (int j = 0; j < other.the_Board[i].length; j++) {
                if (other.the_Board[i][j] != null) {
                    this.the_Board[i][j] = new Vertex(other.the_Board[i][j]); 
                }
            }
        }
        this.blanks = new int[other.blanks.length][];
        for (int i = 0; i < other.blanks.length; i++) {
            this.blanks[i] = other.blanks[i].clone();
        }
    }

    public Vertex[][] getBoard(){
        return the_Board;
    }
    /**
     * Returns a list of all non-null vertices on the board.
     * @return ArrayList of all vertices currently on the board.
     */
    public ArrayList<Vertex> getAllVertices() {
        ArrayList<Vertex> vertices = new ArrayList<>();
        for (int i = 0; i < the_Board.length; i++) {
            for (int j = 0; j < the_Board[i].length; j++) {
                if (the_Board[i][j] != null) {
                    vertices.add(the_Board[i][j]);
                }
            }
        }
        return vertices;
    }



    /**
     * Fills the vertices according to the shape of the board
     */
    private void fillBoard(){
        int vertexNumber = 0;

        for (int i = 0; i < the_Board.length; i++) {        // fills alternate vertices in rows and columns
            int k=0;
            if (i%2!=0)k++;
            for (int j=k; j < the_Board[0].length-k; j+=2) {
                the_Board[i][j]=new Vertex(i, j);
            }
        }
        for (int i=0; i<blanks.length; i++){                // defines the shape of the board by eliminating the 'blanks' spaces
            System.out.println(i+" "+(19-i));
            for(int j: blanks[i]){
                the_Board[i][j]=null;
                the_Board[18-i][j]=null;
            }
        }
        // assign a vertex number for each not null vertex
        // System.out.println(this.strMaker());
        for (int i = 0; i < the_Board[0].length; i++) {
            // System.out.println("column: "+Integer.toString(i));
            for (int j=0; j < the_Board.length; j+=1) {
                if (the_Board[j][i] != null) {
                    // System.out.println("row: "+Integer.toString(j));
                    the_Board[j][i].setVertexNumber(vertexNumber);  
                    // System.out.println("x: "+Integer.toString(the_Board[j][i].getXposition()));
                    // System.out.println("y: "+Integer.toString(the_Board[j][i].getYposition()));

                    // System.out.println("Vertex number "+Integer.toString(the_Board[j][i].getVertextNumber()));

                    vertexNumber++;  
                }
            }
        }
    }



    /**
     * Makes a printable string of the game board
     * @return a string of the board
     */
    public String strMaker() {
        String s = "";
        for (int i = 0; i < the_Board.length; i++) {
            s+=i+"\t\t";
            for (int j = 0; j < the_Board[i].length; j++) {
                if (the_Board[i][j]==null){
                    s+="      ";
                }else {
                    PlayObj[] playObjs = the_Board[i][j].getPlayObject();
                    s+=" |";
                    if(playObjs[0]!=null || playObjs[1]!=null){
                        if(playObjs[0]!=null){
                            s+="R";
                            if (playObjs[0].getColour().toLowerCase().equals("white")){
                                s+="W";
                            }
                            else if (playObjs[0].getColour().toLowerCase().equals("black")){
                                s+="B";
                            }
                        }
                    
                        if (playObjs[1]!=null) {
                            s+="C";
                            if (playObjs[1].getColour().equals("white")){
                                s+="W";
                            }
                            else if (playObjs[1].getColour().equals("black")){
                                s+="B";
                            }
                        }
                    }else {
                        s+="__";
                    }
                    s+="| ";
                }
            }
            s+="\n";
        }
        return s;
    }

    public int[] getVertexPositionByNumber(int vertexNumber){
        for(int i = 0; i<this.the_Board.length;i++){
            for(int j = 0; j<this.the_Board[j].length;j++){
                if(the_Board[i][j]!=null){
                    if(the_Board[i][j].getVertextNumber()==vertexNumber){
                        return new int[]{this.the_Board[i][j].getXposition(), this.the_Board[i][j].getYposition()};
                    }
                }
            }
        }
        return null;
    }

    public int getVertexNumberFromPosition(int x, int y) {
        if (x >= 0 && x < the_Board.length && y >= 0 && y < the_Board[0].length) {
            Vertex vertex = the_Board[x][y];
            if (vertex != null) {
                return vertex.getVertextNumber();
            }
        }
        return -1;
    }

    public Vertex getVertex(int vertexNumber) {
        for (int i = 0; i < this.the_Board.length; i++) {
            for (int j = 0; j < this.the_Board[i].length; j++) {
                if (the_Board[i][j] != null && the_Board[i][j].getVertextNumber() == vertexNumber) {
                    return the_Board[i][j];
                }
            }
        }
        return null;  // Return null if no vertex with that number exists
    }

    public void updateBoard(int vertexNumber, PlayObj playObject) {
        Vertex vertex = getVertex(vertexNumber);
        if (vertex != null) {
            vertex.setPlayObject(playObject);  // Directly set the play object on the vertex
        } else {
            System.out.println("Vertex not found for vertex number: " + vertexNumber);
        }
    }

    public void replaceRing(int prevVertexNumber,int newVertexNumber) {
        Vertex pVertex = getVertex(prevVertexNumber);
        Vertex nVertex = getVertex(newVertexNumber);
        PlayObj choosenRing = pVertex.getRing();
        if (choosenRing != null) {
            pVertex.setRing(null);
            nVertex.setRing(choosenRing);
        } 
    }

    public ArrayList<Vertex> getAllFreeVertexes(){
        ArrayList<Vertex> takenPositions = new ArrayList<Vertex>();
        for (int i = 0; i < this.the_Board.length; i++) {
            for (int j = 0; j < this.the_Board[i].length; j++) {
                if (the_Board[i][j] != null && the_Board[i][j].hasRing()==false && the_Board[i][j].hasCoin() == false) {
                    takenPositions.add(the_Board[i][j]);
                }
            }
        }
        return takenPositions;
    }

    // public ArrayList<Vertex> getVertexAllOfVertexesOfSpecificColour(String colour){
    //     ArrayList<Vertex> positionsVertexes = new ArrayList<Vertex>();
    //     for (int i = 0; i < this.the_Board.length; i++) {
    //         for (int j = 0; j < this.the_Board[i].length; j++) {
    //             if (the_Board[i][j] != null) {
    //                 if(the_Board[i][j].hasRing()){
    //                     if(the_Board[i][j].getRing().getColour().toLowerCase()==colour.toLowerCase()){
    //                         positionsVertexes.add(the_Board[i][j]);
    //                     }
    //                 }
    //             }
    //         }
    //     }
    //     return positionsVertexes;
    // }
    public Vertex getVertexByCoin(Coin coin) {
        for (int i = 0; i < the_Board.length; i++) {
            for (int j = 0; j < the_Board[i].length; j++) {
                if (the_Board[i][j] != null && the_Board[i][j].hasCoin()) {
                    if (the_Board[i][j].getCoin() == coin) {
                        return the_Board[i][j];
                    }
                }
            }
        }
        System.out.println("Coin not found on the board.");
        return null;
    }



}

 