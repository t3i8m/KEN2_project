package com.ken2_1.Game_Components.Board;

/** Vertex is a simple placeholder for the playing object (ring or coin)
 *
 */
public class Vertex {

    private PlayObj[] playObjects; // the current player object, either ring or tile
    private int xIndex;
    private int yIndex;
    private int vertexNumber;
    /**
     * Constructor to initialize a vertex with x and y indices.
     *
     * @param xIndex The x-coordinate index.
     * @param yIndex The y-coordinate index.
     */
    public Vertex(int xIndex, int yIndex){
        playObjects = new PlayObj[2];
        this.xIndex = xIndex;
        this.yIndex = yIndex;
        this.vertexNumber = -1;
    }
    /**
     * Copy constructor to create a new vertex as a copy of another.
     *
     * @param other The vertex to copy.
     */
    public Vertex(Vertex other) {
        this.xIndex = other.xIndex;
        this.yIndex = other.yIndex;
        this.vertexNumber = other.vertexNumber;

        this.playObjects = new PlayObj[2];
        if (other.playObjects[0] != null) {
            this.playObjects[0] = other.playObjects[0].clone(); 
        }
        if (other.playObjects[1] != null) {
            this.playObjects[1] = other.playObjects[1].clone();
        }
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
        return (Ring)playObjects[0];
    }
    public PlayObj getCoin() {
        return playObjects[1];
    }
    public boolean hasRing() {
        boolean result = playObjects[0] instanceof Ring;
        return result;
    }

    public boolean hasCoin() {
        // System.out.println(playObjects[1]);
        boolean result = playObjects[1] instanceof Coin;
        // System.out.println(result);
        return result;
    }

    /** Sets the play object currently held in it
     * @param playObject the player object we want to place in a vertex
     */
    public void setPlayObject(PlayObj playObject) {
        if (playObject instanceof Ring) {
            playObjects[0] = playObject;
        } else if (playObject instanceof Coin) {
            playObjects[1] = playObject;
        } else if (playObject == null) {
            playObjects[0] = null;
            playObjects[1] = null;

        }
    }



}
