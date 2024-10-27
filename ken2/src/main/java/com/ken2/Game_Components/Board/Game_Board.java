package com.ken2.Game_Components.Board;

// import java.util.Dictionary;
// import java.util.Hashtable;

/**
 * The class that builds the board object for the game
 */
public class Game_Board {
    private Vertex[][] the_Board;   // initialize a blank 2d array of vertices for the board

    private int[][] blanks = {{0,1,2,8,9,10},{0,1,9,10},{0,10},{0,10},{0,10}};  // blank spaces to shape the board

    // Dictionary<Integer,int[]> GUIVertex = new Hashtable<Integer,int[]>();

    /**
     * Game Board Object constructor
     */
    public Game_Board() {
        the_Board = new Vertex[19][11];     // Create the blank board
        fillBoard();

        // Fill the dictionary to synchronize the GUI board and the Game_Board for engine
        // GUIVertex.put(0,new int[]{6,0});
        // GUIVertex.put(1,new int[]{8,0});
        // GUIVertex.put(2,new int[]{10,0});
        // GUIVertex.put(3,new int[]{12,0});

        // GUIVertex.put(4,new int[]{3,1});
        // GUIVertex.put(5,new int[]{5,1});
        // GUIVertex.put(6,new int[]{7,1});
        // GUIVertex.put(7,new int[]{9,1});
        // GUIVertex.put(8,new int[]{11,1});
        // GUIVertex.put(9,new int[]{13,1});
        // GUIVertex.put(10,new int[]{15,1});

        // GUIVertex.put(11,new int[]{2,2});
        // GUIVertex.put(12,new int[]{4,2});
        // GUIVertex.put(13,new int[]{6,2});
        // GUIVertex.put(14,new int[]{8,2});
        // GUIVertex.put(15,new int[]{10,2});
        // GUIVertex.put(16,new int[]{12,2});
        // GUIVertex.put(17,new int[]{14,2});
        // GUIVertex.put(18,new int[]{16,2});

        // GUIVertex.put(19,new int[]{1,3});
        // GUIVertex.put(20,new int[]{3,3});
        // GUIVertex.put(21,new int[]{5,3});
        // GUIVertex.put(22,new int[]{7,3});
        // GUIVertex.put(23,new int[]{9,3});
        // GUIVertex.put(24,new int[]{11,3});
        // GUIVertex.put(25,new int[]{13,3});
        // GUIVertex.put(26,new int[]{15,3});
        // GUIVertex.put(27,new int[]{17,3});

        // GUIVertex.put(28,new int[]{0,4});
        // GUIVertex.put(29,new int[]{2,4});
        // GUIVertex.put(30,new int[]{4,4});
        // GUIVertex.put(31,new int[]{6,4});
        // GUIVertex.put(32,new int[]{8,4});
        // GUIVertex.put(33,new int[]{10,4});
        // GUIVertex.put(34,new int[]{12,4});
        // GUIVertex.put(35,new int[]{14,4});
        // GUIVertex.put(36,new int[]{16,4});
        // GUIVertex.put(37,new int[]{18,4});

        // GUIVertex.put(38,new int[]{1,5});
        // GUIVertex.put(39,new int[]{3,5});
        // GUIVertex.put(40,new int[]{5,5});
        // GUIVertex.put(41,new int[]{7,5});
        // GUIVertex.put(42,new int[]{9,5});
        // GUIVertex.put(43,new int[]{11,5});
        // GUIVertex.put(44,new int[]{13,5});
        // GUIVertex.put(45,new int[]{15,5});
        // GUIVertex.put(46,new int[]{17,5});

        // GUIVertex.put(47,new int[]{0,6});
        // GUIVertex.put(48,new int[]{2,6});
        // GUIVertex.put(49,new int[]{4,6});
        // GUIVertex.put(50,new int[]{6,6});
        // GUIVertex.put(51,new int[]{8,6});
        // GUIVertex.put(52,new int[]{10,6});
        // GUIVertex.put(53,new int[]{12,6});
        // GUIVertex.put(54,new int[]{14,6});
        // GUIVertex.put(55,new int[]{16,6});
        // GUIVertex.put(56,new int[]{18,6});

        // GUIVertex.put(57,new int[]{1,7});
        // GUIVertex.put(58,new int[]{3,7});
        // GUIVertex.put(59,new int[]{5,7});
        // GUIVertex.put(60,new int[]{7,7});
        // GUIVertex.put(61,new int[]{9,7});
        // GUIVertex.put(62,new int[]{11,7});
        // GUIVertex.put(63,new int[]{13,7});
        // GUIVertex.put(64,new int[]{15,7});
        // GUIVertex.put(65,new int[]{17,7});

        // GUIVertex.put(66,new int[]{2,8});
        // GUIVertex.put(67,new int[]{4,8});
        // GUIVertex.put(68,new int[]{6,8});
        // GUIVertex.put(69,new int[]{8,8});
        // GUIVertex.put(70,new int[]{10,8});
        // GUIVertex.put(71,new int[]{12,8});
        // GUIVertex.put(72,new int[]{14,8});
        // GUIVertex.put(73,new int[]{16,8});

        // GUIVertex.put(74,new int[]{3,9});
        // GUIVertex.put(75,new int[]{5,9});
        // GUIVertex.put(76,new int[]{7,9});
        // GUIVertex.put(77,new int[]{9,9});
        // GUIVertex.put(78,new int[]{11,9});
        // GUIVertex.put(79,new int[]{13,9});
        // GUIVertex.put(80,new int[]{15,9});

        // GUIVertex.put(81,new int[]{6,10});
        // GUIVertex.put(82,new int[]{8,10});
        // GUIVertex.put(83,new int[]{10,10});
        // GUIVertex.put(84,new int[]{12,10});

    }

    public Vertex[][] getBoard(){
        return the_Board;
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
        System.out.println(this.strMaker());
        for (int i = 0; i < the_Board[0].length; i++) {
            System.out.println("column: "+Integer.toString(i));
            for (int j=0; j < the_Board.length; j+=1) {
                if (the_Board[j][i] != null) {
                    System.out.println("row: "+Integer.toString(j));
                    the_Board[j][i].setVertexNumber(vertexNumber);  
                    System.out.println("x: "+Integer.toString(the_Board[j][i].getXposition()));
                    System.out.println("y: "+Integer.toString(the_Board[j][i].getYposition()));

                    System.out.println("Vertex number "+Integer.toString(the_Board[j][i].getVertextNumber()));

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
                        s+="R";
                        if(playObjs[0]!=null){
                            if (playObjs[0].getColour().toLowerCase().equals("white")){
                                s+="W";
                            }
                            else if (playObjs[0].getColour().toLowerCase().equals("black")){
                                s+="B";
                            }
                        }
                    
                        if (playObjs[1]!=null) {
                            s+="C";
                            if (playObjs[1].getColour().equals("White")){
                                s+="W";
                            }
                            else if (playObjs[1].getColour().equals("Black")){
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

    /**
     * Update the board during the game
     */
    public void updateBoard(int vertexNumber,PlayObj playObject){
        // int[] currentPosition = GUIVertex.get(vertexNumber);
        int[] currentPosition = this.getVertexPositionByNumber(vertexNumber);
        the_Board[currentPosition[0]][currentPosition[1]].setPlayObject(playObject);
    }


}

 