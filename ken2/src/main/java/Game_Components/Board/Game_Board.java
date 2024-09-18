package Game_Components.Board;

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
        the_Board = new Vertex[19][11];
    }

    /**
     * Fills the vertices according to the shape of the board
     */
    public void fillBoard(){
        for (int i = 0; i < the_Board.length; i++) {        // fills alternate vertices in rows and columns
            int k=0;
            if (i%2!=0)k++;
            for (int j=k; j < the_Board[0].length-k; j+=2) {
                the_Board[i][j]=new Vertex();
            }
        }
        for (int i=0; i<blanks.length; i++){                // defines the shape of the board by eliminating the 'blanks' spaces
            System.out.println(i+" "+(19-i));
            for(int j: blanks[i]){
                the_Board[i][j]=null;
                the_Board[18-i][j]=null;
            }
        }
    }

    /**
     * Makes a printable string of the game board
     * @return a string of the board
     *  TODO Have to add a functionality to show the different pieces (playing object) with different letters
     *  TODO this can be done easily once we have a functional pieces class
     */
    public String strMaker() {
        String s = "";
        for (int i = 0; i < the_Board.length; i++) {
            s+=i+"\t\t";
            for (int j = 0; j < the_Board[i].length; j++) {
                if (the_Board[i][j]==null){
                    s+="   ";
                }else {
                    s+=" x ";
                }
            }
            s+="\n";
        }
        return s;
    }

}

