package com.ken2.Game_Components.Board;

/** Vertex is a simple placeholder for the playing object (ring or coin)
 *
 */
public class Vertex {

    private Object playObject; // the current player object, either ring or tile
    private String label;
    private int xIndex;
    private int yIndex;

    public Vertex(int xIndex, int yIndex){
        playObject = null;
        label=null;
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
    public Object getPlayObject() {
        return playObject;
    }

    /** Sets the play object currently held in it
     * @param playObject the player object we want to place in a vertex
     */
    public void setPlayObject(Object playObject) {
        this.playObject = playObject;
//        label=playObject.type;
    }
}
