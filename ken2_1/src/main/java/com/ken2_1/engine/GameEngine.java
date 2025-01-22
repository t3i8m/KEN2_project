package com.ken2_1.engine;


import com.ken2_1.Game_Components.Board.Coin;
import com.ken2_1.Game_Components.Board.Game_Board;
import com.ken2_1.Game_Components.Board.Ring;
import com.ken2_1.Game_Components.Board.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GameEngine {

    public GameState currentState;
    public GameSimulation gameSimulation;

    public static int[][] vertexCoordinates;
    private Direction direction;
    public Game_Board gameBoard;

    private static final int WIN_CONDITION = 5;
    private boolean isRingSelectionMode = false;
    private boolean isChipRemovalMode = false;

    public static List<Integer> winningChips = new ArrayList<>();
    /**
     * Constructor to initialize the game engine, setting up the board and game state.
     */
    public GameEngine(){
        currentState=new GameState();
        gameSimulation=new GameSimulation();
        vertexCoordinates = new int[85][2];
        initialiseVertex();
        this.gameBoard = new Game_Board();
        this.currentState.gameBoard = this.gameBoard;
    }
    /**
     * Places a starting ring at the given vertex if valid.
     *
     * @param vertex The vertex index to place the ring.
     * @param ringColor The color of the ring.
     * @return True if the ring was successfully placed, otherwise false.
     */
    public boolean placeStartingRing(int vertex,String ringColor) {
        Vertex boardVertex = currentState.gameBoard.getVertex(vertex);
        if (boardVertex != null && !boardVertex.hasRing()) {

            Ring newRing = new Ring(ringColor);
            boardVertex.setPlayObject(newRing); // add ring to the board data structure
            currentState.ringsPlaced++;

            if (currentState.ringsPlaced > 10) currentState.chipPlacement= true;
            currentState.resetTurn();

            return true;
        } else {
            // GameAlerts.alertRingPlacement(); // Alert for when you try to place a ring on another one during the starting phase. Does not work if bot is white
        }
        return false;
    }


    /**
     * Retrieves the available places for starting rings.
     *
     * @return A list of available vertex positions.
     */
    public ArrayList<Vertex> availablePlacesForStartingRings() {
        Vertex[][] board = currentState.gameBoard.getBoard();
        return this.gameSimulation.getAllPossibleStartingRingPlaces(board);
    }
    /**
     * Gets all winning rings of a specified color.
     *
     * @param color The color of the rings to search for.
     * @return A list of vertex numbers where winning rings are located.
     */
    public ArrayList<Integer> getWinningRings(String color){
        ArrayList<Integer> winningRings = new ArrayList<>();

        ArrayList<Vertex> allVertices = currentState.gameBoard.getAllVertices();

        for (Vertex vertex : allVertices) {
            if (vertex.hasRing() && vertex.getRing().getColour().toLowerCase().equalsIgnoreCase(color)) {
                winningRings.add(vertex.getVertextNumber());
            }
        }
        return winningRings;
    }
    /**
     * Places a chip at the specified vertex if valid.
     *
     * @param vertex The vertex index to place the chip.
     * @return True if the chip was successfully placed, otherwise false.
     */

    public boolean placeChip(int vertex) {
        Vertex boardVertex = currentState.gameBoard.getVertex(vertex);


        if (boardVertex != null && boardVertex.hasRing() && !boardVertex.hasCoin()) {
            String chipColor = boardVertex.getRing().getColour();

            //condition for same ring colors
            if (!boardVertex.getRing().getColour().equalsIgnoreCase(chipColor)) {
               return false;
            }

            Coin newChip = new Coin(chipColor.toLowerCase());
            newChip.setVertex(vertex);

            boardVertex.setPlayObject(newChip);
            currentState.chipRingVertex = vertex;
            currentState.chipNumber.add(vertex);
            currentState.chipPlaced = true;//after chip placement we can move the ring
            currentState.resetTurn();
            return true;
        } else {

        }
        return false;

    }

    /**
     * simulate and get the possible vertices
     * @param boardVertex the starting vertex
     */
    public ArrayList<Move> possibleMoves(Vertex boardVertex) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            Diagonal diagonal = new Diagonal(direction, new int[]{boardVertex.getXposition(), boardVertex.getYposition()}, currentState.gameBoard);
            possibleMoves.addAll(diagonal.moveAlongDiagonal(currentState.gameBoard.getBoard()));
        }
        return possibleMoves;
    }

    // gets ring positions and returns all of the moves from these positions
    public HashMap<Vertex, ArrayList<Move>> getAllMovesFromAllPositions(ArrayList<Vertex> allRingPositions, Game_Board board){
        GameSimulation gs  = new GameSimulation();

        HashMap<Vertex, ArrayList<Move>> allMoves = new HashMap<Vertex, ArrayList<Move>>();
        for(Vertex v: allRingPositions){

            gs.startSimulation(board.getBoard().clone(), v.getXposition(), v.getYposition());
            ArrayList<ArrayList<Move>> possibleMoves = gs.getAllPossibleMoves();
            ArrayList<Move> allMovesdig = new ArrayList<>();
            for (ArrayList<Move> moveList : possibleMoves) {
                if(moveList.isEmpty()){
                    continue;
                }
                allMovesdig.addAll(moveList);
            }
            if (allMovesdig.isEmpty()) {

            } else{

            }
            allMoves.put(v, allMovesdig);

            for(Move m: allMoves.get(v)){
                m.setStartingVertex(v);
            }
        }
        return allMoves;
    }

    public  GameState getGameState(){

        return this.currentState;
    }
    /**
     * Checks if the player has won by analyzing the flipped coins on the board.
     *
     * @param vertexesOfFlippedCoins The list of flipped coin vertices.
     * @return True if a win condition is met, otherwise false.
     */
    public boolean win(ArrayList<Vertex> vertexesOfFlippedCoins){
        boolean win = false;
        if(vertexesOfFlippedCoins==null){
            return false;
        }
        for(Vertex v:vertexesOfFlippedCoins){
            int vertex = v.getVertextNumber();
            // System.out.println("VERTEX BEING CHECKED: "+vertex);
            if(v.hasCoin()){
                // System.out.println("COLOR "+v.getCoin().getColour());
                String color = v.getCoin().getColour();
                for (Direction direction: Direction.values()){
                    int k = 1;

                    int first = 0;
                    int second = 0;
                    // System.out.println("CURRENT DIRECTION: "+direction.name());
                    first=countChipsInOneDirection(vertex, color, direction.getDeltaX(), direction.getDeltaY());
                    second=countChipsInOneDirection(vertex, color, -direction.getDeltaX(), -direction.getDeltaY());
                    if(first>=0 || second>=0){
                        k+=(first+second);
                    }
                    // System.out.println("TOTAL K:"+k);
                    if (k>=WIN_CONDITION){

                        win = true;
                        // System.out.print("WIN--------------");
                        // System.out.println("------------K:"+k);
                        winningRing=win;

                        setRingSelectionMode(true);
                        currentState.updateRingCount(color);
                        // findAndSetAllWinningChips(color);
                        setWinningColor(color);
                        ringSelection(color);

                        return win;
                    }
                    // win = false;
                }

            }

        }
        return win;

    }

    /**
     * Determines the winning color based on flipped coins on the board.
     *
     * @param vertexesOfFlippedCoins The list of flipped coin vertices.
     * @return The color of the winning player, or null if no winner is found.
     */
    public String winningColor(ArrayList<Vertex> vertexesOfFlippedCoins) {
        String color = getWinningColor();
        if (vertexesOfFlippedCoins == null) {
            return null;
        }
        for (Vertex v : vertexesOfFlippedCoins) {
            int vertex = v.getVertextNumber();
            // System.out.println("VERTEX BEING CHECKED: " + vertex);
            if (v.hasCoin()) {
                // System.out.println("COLOR " + v.getCoin().getColour());
                color = v.getCoin().getColour();
                for (Direction direction : Direction.values()) {
                    int k = 1;

                    int first = 0;
                    int second = 0;
                    // System.out.println("CURRENT DIRECTION: " + direction.name());
                    first = countChipsInOneDirection(vertex, color, direction.getDeltaX(), direction.getDeltaY());
                    second = countChipsInOneDirection(vertex, color, -direction.getDeltaX(), -direction.getDeltaY());
                    if (first >= 0 || second >= 0) {
                        k += (first + second);
                    }
                    // System.out.println("TOTAL K:" + k);
                    if (k >= WIN_CONDITION) {


                        return color;
                    }
                    // win = false;
                }

            }

        }
        return color;
    }

    private boolean winningRing = false;
    private String winningColor = "";


    public void ringSelection(String color){
        this.winningRing = true;
        this.winningColor = color;
    }
    public boolean GetWinningRing(){
        return winningRing;
    }
    public String getWinningColor(){
        return  winningColor;
    }
    public boolean isInChipRemovalMode() {
        return isChipRemovalMode;
    }

    public boolean isInRingRemovalMode(){
        return isRingSelectionMode;
    }
    public void setWinningRing(boolean winningRing) {
        this.winningRing = winningRing;
    }

    public void setWinningColor(String color) {
        this.winningColor = color;
    }

    public static List<Integer> getWinningChips() {
        return winningChips;
    }

    public void setWinningChips(List<Integer> chips) {
        winningChips.clear();
        winningChips.addAll(chips);
    }

    public void setChipRemovalMode(boolean mode) {
        this.isChipRemovalMode = mode;
        isRingSelectionMode = false;
    }

    public void setRingSelectionMode(boolean mode) {
        this.isRingSelectionMode = mode;
        isChipRemovalMode = false;
    }



    /**
     * Counts the number of consecutive chips of the same color in a given direction.
     *
     * @param start The starting vertex number.
     * @param chipColor The color of the chip.
     * @param dx The x-direction step.
     * @param dy The y-direction step.
     * @return The count of consecutive chips in the specified direction.
     */
    public int countChipsInOneDirection(int start, String chipColor, int dx, int dy){
        int k = 0;
        int x = currentState.gameBoard.getVertex(start).getXposition();
        int y = currentState.gameBoard.getVertex(start).getYposition();
        while(true){
            x+=dx;
            y+=dy;
            int next=currentState.gameBoard.getVertexNumberFromPosition(x,y);
            if(next==-1)
                return k;

            Vertex v =currentState.gameBoard.getVertex(next);
            if(!v.hasCoin() ){
                return k;
            }
            if(v==null || !v.hasCoin() || !v.getCoin().getColour().toLowerCase().equals(chipColor)){

                if(v!=null){

                    if(v.hasCoin()){

                    }
                }

                return k;
            }
            k++;



        }

    }
    /**
     * Retrieves the chips in a given direction from a starting vertex.
     *
     * @param startVertex The starting vertex number.
     * @param chipColor The color of the chip.
     * @param dx The x-direction step.
     * @param dy The y-direction step.
     * @return A list of vertex numbers containing chips in the specified direction.
     */
    public List<Integer> getChipsInDirection(int startVertex, String chipColor, int dx, int dy) {
        List<Integer> chips = new ArrayList<>();
        Vertex currentVertex = currentState.gameBoard.getVertex(startVertex);

        if (currentVertex == null) {
            // System.out.println("Starting vertex is null for: " + startVertex);
            return chips;
        }

        int x = currentState.gameBoard.getVertex(startVertex).getXposition();
        int y = currentState.gameBoard.getVertex(startVertex).getYposition();

        while (true) {
            x += dx;
            y += dy;

            int next = currentState.gameBoard.getVertexNumberFromPosition(x, y);
            if (next == -1) {
                // System.out.println("Reached invalid position.");
                break;
            }

            Vertex v = currentState.gameBoard.getVertex(next);
            if (v == null) {
                // System.out.println("Vertex " + next + " is null.");
                break;
            }

            if (!v.hasCoin() || !v.getCoin().getColour().toLowerCase().equalsIgnoreCase(chipColor.toLowerCase())) {
                // System.out.println("Vertex " + next + " does not have the correct chip.");
                break;
            } else{

                // System.out.println("Adding vertex " + next + " to chips.");
                chips.add(next);
            }

        }

        return chips;
    }
    /**
     * Finds all winning chip sequences on the board for a given color.
     *
     * @param chipColor The color of the chips to check.
     * @return A list of lists containing the winning chip sequences.
     */
    public List<List<Integer>> findAllWinningChipsMOD(String chipColor) {
        List<List<Integer>> winningChips = new ArrayList<>();
        List<Vertex> allVertices = currentState.getVertexesOfFlippedCoins();
        // System.out.println(allVertices.get(0).getVertextNumber());

        for(Vertex v: allVertices){

            for (Direction direction : Direction.values()) {
                // System.out.println("Checking asdasd: " + direction.name());
                List<Integer> chipsInOneDirection = getChipsInDirection(v.getVertextNumber(), v.getCoin().getColour(), direction.getDeltaX(), direction.getDeltaY());
                List<Integer> chipsInSecondDirection = getChipsInDirection(v.getVertextNumber(), v.getCoin().getColour(), -direction.getDeltaX(), -direction.getDeltaY());
                int result = chipsInOneDirection.size()+chipsInSecondDirection.size()+1;
                // System.out.println(result);
                if(result>=5){

                    List<Integer> currArray = new ArrayList<>();
                    int lastFirst;
                    int secondLast;
                    if(!chipsInOneDirection.isEmpty()){
                        lastFirst = chipsInOneDirection.getLast();
                        chipsInOneDirection.remove(chipsInOneDirection.size() - 1);
                    } else{
                        lastFirst=-1;
                    }

                    if(!chipsInSecondDirection.isEmpty()){
                        secondLast = chipsInSecondDirection.getLast();
                        chipsInSecondDirection.remove(chipsInSecondDirection.size()-1);
                    }else{
                        secondLast=-1;
                    }


                    if (secondLast!=-1){
                        currArray.add(secondLast);
                    }
                    if(!chipsInOneDirection.isEmpty()){
                        for(Integer i: chipsInOneDirection){
                            currArray.add(i);
                        }
                    }
                    if(!chipsInSecondDirection.isEmpty()){
                        for(Integer i: chipsInSecondDirection){
                            currArray.add(i);
                        }
                    }
                    if(lastFirst!=-1){
                        currArray.add(lastFirst);

                    }
                    currArray.add(v.getVertextNumber());


                    // System.out.println("WE HAVE CHIPS VALID "+currArray);
                    winningChips.add(currArray);}
            }
        }

        return winningChips;
    }

    /**
     * Finds and sets all winning chip sequences of a given color.
     *
     * @param chipColor The color of the chips to check.
     */
    public void findAndSetAllWinningChips(String chipColor) {
        List<List<Integer>> a = findAllWinningChipsMOD(chipColor); // a has all of the positions that we can remove
        setWinningChips(a.get(0));
    }
    /**
     * Gets the adjacent vertices of a given vertex.
     *
     * @param vertex The vertex to find adjacent vertices for.
     * @return A list of adjacent vertex numbers.
     */
    public List<Integer> getAdjacentVertices(int vertex) {
        List<Integer> adjacentVertices = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            int adjVertex = currentState.gameBoard.getAdjacentVertex(vertex, direction.getDeltaX(), direction.getDeltaY());
            if (adjVertex >= 0) {
                adjacentVertices.add(adjVertex);
            }
        }
        return adjacentVertices;
    }

    /**
     * Initializes the coordinates of the vertices, Maps the vertex number with coordinates
     */
    private void initialiseVertex() {
        int index = 0;
        // a
        for (int i = 0; i < 4; i++) {
            vertexCoordinates[index] = new int[] { 175 - 38 * 4, (i * 44) + 19 + 44*3};
            index++;
        }
        // b
        for (int i = 0; i < 7; i++) {
            vertexCoordinates[index] = new int[] { 175 - 38 * 3, (i * 44) + 19 + 66 };
            index++;
        }
        // c
        for (int i = 0; i < 8; i++) {
            vertexCoordinates[index] = new int[] { 175 - 38 * 2, ((i * 44) + 19 + 44)};
            index++;
        }
        // d
        for (int i = 0; i < 9; i++) {
            vertexCoordinates[index] = new int[] { 175 - 38, ((i * 44) + 19 + 22)};
            index++;
        }
        // e
        for (int i = 0; i < 10; i++) {
            vertexCoordinates[index] = new int[] { 175, (i * 44) + 19 };
            index++;
        }
        // f
        for (int i = 0; i < 9; i++) {
            vertexCoordinates[index] = new int[] { 175 + 38, (i * 44) + 19 + 22};
            index++;
        }
        // g
        for (int i = 0; i < 10; i++) {
            vertexCoordinates[index] = new int[] { 175 + 38*2, (i * 44) + 19};
            index++;
        }
        // h
        for (int i = 0; i < 9; i++) {
            vertexCoordinates[index] = new int[] { 175 + 38 * 3, (i * 44) + 19 + 22};
            index++;
        }
        // i
        for (int i = 0; i < 8; i++) {
            vertexCoordinates[index] = new int[] { 175 + 38 * 4, (i * 44) + 19 + 44};
            index++;
        }
        // j
        for (int i = 0; i < 7; i++) {
            vertexCoordinates[index] = new int[] { 175 + 38 * 5, (i * 44) + 19 + 66};
            index++;
        }
        // k
        for (int i = 0; i < 4; i++) {
            vertexCoordinates[index] = new int[] { 175 + 38 * 6, (i * 44) + 19 + 44 * 3};
            index++;
        }
    }
    /**
     * Finds the closest vertex to the given coordinates.
     *
     * @param xCoordinate The x-coordinate of the click.
     * @param yCoordinate The y-coordinate of the click.
     * @return The index of the closest vertex, or -1 if no vertex is found within range.
     */

    public int findClosestVertex(double xCoordinate, double yCoordinate){

        // System.out.println();

        for(int i = 0 ; i < 85; i++){
            double vX = vertexCoordinates[i][0] + 18;
            double vY = vertexCoordinates[i][1] + 18;

            double xDist = Math.abs(xCoordinate - vX);
            double yDist = Math.abs(yCoordinate - vY);

            if(xDist<=10 && yDist <=10){
                return i;
            }
        }
        return -1;
    }

    public int[][] getVertexCoordinates() {
        return vertexCoordinates;
    }

    public int[] getcoordinates(int vertex){
        return vertexCoordinates[vertex];
    }
    /**
     * Resets the game state and reinitializes vertex coordinates.
     */
    public void resetGame(){
        currentState=new GameState();
        vertexCoordinates = new int[85][2];
        initialiseVertex();
    }
    /**
     * Clones the current game engine instance.
     *
     * @return A deep copy of the current GameEngine object.
     */
    @Override
    public GameEngine clone() {
        try {
            GameEngine clonedEngine = new GameEngine();

            clonedEngine.currentState = this.currentState.clone();

            clonedEngine.gameSimulation = this.gameSimulation;

            clonedEngine.vertexCoordinates = new int[this.vertexCoordinates.length][2];
            for (int i = 0; i < this.vertexCoordinates.length; i++) {
                clonedEngine.vertexCoordinates[i] = this.vertexCoordinates[i].clone();
            }

            // Clone the game board
            clonedEngine.gameBoard = this.gameBoard.clone();

            clonedEngine.direction = this.direction;
            clonedEngine.isRingSelectionMode = this.isRingSelectionMode;
            clonedEngine.isChipRemovalMode = this.isChipRemovalMode;

            clonedEngine.setWinningChips(new ArrayList<>(this.getWinningChips()));

            clonedEngine.winningColor = this.winningColor;
            clonedEngine.winningRing = this.winningRing;

            return clonedEngine;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cloning failed.", e);
        }
    }


}