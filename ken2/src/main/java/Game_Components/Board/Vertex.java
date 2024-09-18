package Game_Components.Board;

/** Vertex is a simple placeholder for the playing object (ring or coin)
 *
 */
public class Vertex {

    private Object playObject; // the current player object, either ring or tile
    private String label;

    public Vertex(){
        playObject = null;
        label=null;
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
