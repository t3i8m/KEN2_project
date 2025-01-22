package com.ken2.Game_Components.Board;


import java.util.ArrayList;

import com.ken2.engine.GameState;


/**
 * The class that builds the board object for the game
 */
public class Game_Board {
    public Vertex[][] the_Board;   // initialize a blank 2d array of vertices for the board

    private int[][] blanks = {{0,1,2,8,9,10},{0,1,9,10},{0,10},{0,10},{0,10}};  // blank spaces to shape the board

    /**
     * Game Board Object constructor
     */
    public Game_Board() {
        the_Board = new Vertex[19][11];     // Create the blank board
        fillBoard();
    }
    /**
     * Copy constructor to create a deep copy of another game board.
     *
     * @param other The game board to copy.
     */
    public Game_Board(Game_Board other) {
        if (other.the_Board != null) {
            this.the_Board = new Vertex[other.the_Board.length][other.the_Board[0].length];
            for (int i = 0; i < other.the_Board.length; i++) {
                for (int j = 0; j < other.the_Board[i].length; j++) {
                    if (other.the_Board[i][j] != null) {
                        this.the_Board[i][j] = new Vertex(other.the_Board[i][j]); 
                    }
                }
            }
        }
        if (other.blanks != null) {
            this.blanks = new int[other.blanks.length][];
            for (int i = 0; i < other.blanks.length; i++) {
                this.blanks[i] = other.blanks[i].clone();
            }
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
            // System.out.println(i+" "+(19-i));
            for(int j: blanks[i]){
                the_Board[i][j]=null;
                the_Board[18-i][j]=null;
            }
        }
        for (int i = 0; i < the_Board[0].length; i++) {
            // System.out.println("column: "+Integer.toString(i));
            for (int j=0; j < the_Board.length; j+=1) {
                if (the_Board[j][i] != null) {
                    the_Board[j][i].setVertexNumber(vertexNumber);
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
    /**
     * Gets the position of a vertex by its number.
     *
     * @param vertexNumber The vertex number to search for.
     * @return An array with the x and y coordinates of the vertex.
     */
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
    /**
     * Gets the vertex number from a given x and y position.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The vertex number, or -1 if not found.
     */
    public int getVertexNumberFromPosition(int x, int y) {
        if (x >= 0 && x < the_Board.length && y >= 0 && y < the_Board[0].length) {
            Vertex vertex = the_Board[x][y];
            if (vertex != null) {
                return vertex.getVertextNumber();
            }
        }
        return -1;
    }

    /**
     * Gets the vertex object by its number.
     *
     * @param vertexNumber The vertex number to retrieve.
     * @return The vertex object, or null if not found.
     */
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
    /**
     * Updates the play object at a given vertex number.
     *
     * @param vertexNumber The vertex number to update.
     * @param playObject The play object to set at the vertex.
     */
    public void updateBoard(int vertexNumber, PlayObj playObject) {
        Vertex vertex = getVertex(vertexNumber);
        if (vertex != null) {
            vertex.setPlayObject(playObject);  // Directly set the play object on the vertex
        } else {
            // System.out.println("Vertex not found for vertex number: " + vertexNumber);
        }
    }
    /**
     * Retrieves all free vertices on the board.
     *
     * @return A list of vertices that do not contain a ring or a coin.
     */
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
    /**
     * Gets the adjacent vertex in a given direction.
     *
     * @param vertex The vertex number.
     * @param deltaX The x-direction offset.
     * @param deltaY The y-direction offset.
     * @return The vertex number of the adjacent vertex, or -1 if not found.
     */
    public int getAdjacentVertex(int vertex, int deltaX, int deltaY) {
        Vertex currentVertex = getVertex(vertex);
        if (currentVertex == null) {
            return -1; 
        }
    
        int newX = currentVertex.getXposition() + deltaX;
        int newY = currentVertex.getYposition() + deltaY;
    
        for (int i = 0; i < the_Board.length; i++) {
            for (int j = 0; j < the_Board[i].length; j++) {
                Vertex vertexCandidate = the_Board[i][j];
                if (vertexCandidate != null &&
                    vertexCandidate.getXposition() == newX &&
                    vertexCandidate.getYposition() == newY) {
                    return vertexCandidate.getVertextNumber(); 
                }
            }
        }
    
        return -1; 
    }
    
    @Override
    public Game_Board clone() {
        return new Game_Board(this);
    }

    /**
     * Creates a new game state from the given game board.
     *
     * @param board1 The game board to create the state from.
     * @return A new GameState object initialized with the given board.
     */
    public  GameState createStatesFromBoards(Game_Board board1) {
        GameState state1 = new GameState();
        state1.gameBoard = board1.clone();
        updateStateFromBoard(state1, board1);

        return state1;
    }
    /**
     * Updates the game state based on the provided game board.
     *
     * @param state The game state to be updated.
     * @param board The game board to derive state information from.
     */
private  void updateStateFromBoard(GameState state, Game_Board board) {
    int ringsWhite = 0;
    int ringsBlack = 0;
    int chipsWhite = 0;
    int chipsBlack = 0;

    for (Vertex[] row : board.getBoard()) {
        for (Vertex vertex : row) {
            if (vertex != null) {
                if (vertex.hasRing()) {
                    String ringColor = vertex.getRing().getColour().toLowerCase();
                    if ("white".equals(ringColor)) {
                        ringsWhite++;
                    } else if ("black".equals(ringColor)) {
                        ringsBlack++;
                    }
                }

                if (vertex.hasCoin()) {
                    String coinColor = vertex.getCoin().getColour().toLowerCase();
                    if ("white".equals(coinColor)) {
                        chipsWhite++;
                    } else if ("black".equals(coinColor)) {
                        chipsBlack++;
                    }
                }
            }
        }
    }

    state.ringsWhite = ringsWhite;
    state.ringsBlack = ringsBlack;
    state.chipsWhite = chipsWhite;
    state.chipsBlack = chipsBlack;

    state.chipsRemaining = 51 - chipsWhite - chipsBlack;

    state.isWhiteTurn = ringsWhite <= ringsBlack;
}

}

 