package com.ken2.Game_Components.Board;

/** Vertex is a simple placeholder for the playing object (ring or coin)
 *
 */
public class Vertex {

    private PlayObj[] playObjects; // the current player object, either ring or tile
    private int xIndex;
    private int yIndex;
    private int vertexNumber;

    public Vertex(int xIndex, int yIndex){
        playObjects = new PlayObj[2];
        this.xIndex = xIndex;
        this.yIndex = yIndex;
    }

    public void setVertexNumber(int vertexNumber){
        this.vertexNumber = vertexNumber;
    }

    public int getVertextNumber(){
        return this.vertexNumber;
    }

    public int getXposition(){
        return xIndex;
    }

    public int getYposition(){
        return yIndex;
    }

    public void setRing(PlayObj newRing) {
        // if(newRing==null){
        //     this.playObjects = = new PlayObj[2]{,playObjects[1]};
        // }
        this.playObjects[0] = newRing;
    }

    public void setCoin(PlayObj newCoin) {
        this.playObjects[1] = newCoin;
    }


    /** Returns the play object currently at the vertex
     * 0 - Ring, 1 - Coin
     * @return the play object
     */
    public PlayObj[] getPlayObject() {
        return playObjects;
    }

    public PlayObj getRing(){
        return playObjects[0];
    }
    public PlayObj getCoin() {
        return playObjects[1];
    }
    public boolean hasRing(){
        return playObjects[0]!=null;
    }
    public boolean hasCoin(){
        return playObjects[1]!=null;
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
