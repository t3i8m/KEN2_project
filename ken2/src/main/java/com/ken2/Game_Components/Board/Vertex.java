package com.ken2.Game_Components.Board;

/** Vertex is a simple placeholder for the playing object (ring or coin)
 *
 */
public class Vertex {

    private PlayObj[] playObjects; // the current player object, either ring or tile
    private int xIndex;
    private int yIndex;

    public Vertex(int xIndex, int yIndex){
        playObjects = new PlayObj[2];
        this.xIndex = xIndex;
        this.yIndex = yIndex;
    }

    public int getXposition(){
        return xIndex;
    }

    public int getYposition(){
        return yIndex;
    }


    /** Returns the play object currently at the vertex
     * @return the play object
     */
    public PlayObj[] getPlayObject() {
        return playObjects;
    }

    /** Sets the play object currently held in it
     * @param playObject the player object we want to place in a vertex
     */
    public void setPlayObject(PlayObj playObject) {
        if(playObject instanceof Ring){       // sets if it is a ring in the 0th index
            playObjects[0]=playObject;
        }else{                              // sets otherwise a coin in the 1st index
            playObjects[1]=playObject;
        }
//        label=playObject.type;
    }
}
