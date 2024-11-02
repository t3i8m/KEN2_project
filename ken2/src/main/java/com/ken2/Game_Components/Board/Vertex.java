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
        this.vertexNumber = -1;
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
        if (newRing instanceof Ring) {
            this.playObjects[0] = newRing;
            System.out.println("Ring set at (" + xIndex + ", " + yIndex + ")");
        } else {
            System.out.println("Attempted to set non-ring object as ring at (" + xIndex + ", " + yIndex + ")");
        }
    }

    public void setCoin(PlayObj newCoin) {
        if (newCoin instanceof Coin) {
            this.playObjects[1] = newCoin;
            System.out.println("Coin set at (" + xIndex + ", " + yIndex + ")");
        } else {
            System.out.println("Attempted to set non-coin object as coin at (" + xIndex + ", " + yIndex + ")");
        }
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
        boolean result = playObjects[0] instanceof Ring;
        System.out.println("Checking for ring at (" + xIndex + ", " + yIndex + "): " + result);
        return result;
    }
    public boolean hasCoin(){
        boolean result = playObjects[1] instanceof Coin;
        System.out.println("Checking for coin at (" + xIndex + ", " + yIndex + "): " + result);
        return result;
    }

    /** Sets the play object currently held in it
     * @param playObject the player object we want to place in a vertex
     */
    public void setPlayObject(PlayObj playObject) {
        if (playObject instanceof Ring) {
            playObjects[0] = playObject; // Store Ring at index 0
            System.out.println("Ring set at vertex " + this.vertexNumber);
        } else if (playObject instanceof Coin) {
            playObjects[1] = playObject; // Store Coin at index 1
            System.out.println("Coin set at vertex " + this.vertexNumber);
        } else {
            System.out.println("Error: Attempted to set unknown PlayObj at vertex " + this.vertexNumber);
        }
    }



}
