package com.ken2.engine;


import com.ken2.Game_Components.Board.Coin;
import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Ring;
import com.ken2.Game_Components.Board.Vertex;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class GameEngine {

    public GameState currentState;
    public GameSimulation gameSimulation;

    public static int[][] vertexCoordinates;
    private Direction direction;
    public Game_Board gameBoard;

    private static final int WIN_CONDITION = 5;
    private boolean isRingSelectionMode = false;
    private boolean isChipRemovalMode = false;

    private List<Integer> winningChips = new ArrayList<>();



    public GameEngine(){
        currentState=new GameState();
        gameSimulation=new GameSimulation();
        vertexCoordinates = new int[85][2];
        initialiseVertex();
        this.gameBoard = new Game_Board();
        this.currentState.gameBoard = this.gameBoard;
    }

    public boolean placeStartingRing(int vertex,String ringColor) {
        Vertex boardVertex = currentState.gameBoard.getVertex(vertex);
        if (boardVertex != null && !boardVertex.hasRing()) {
            // ringColor = currentState.currentPlayerColor();
            // ringColor = ringColor;
            // if(ringColor.equals("White")){
            //     currentState.updateRingCount("white");

            // } else{
            //     ringColor="Black";
            //     currentState.updateRingCount("black");
            // }

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


    public ArrayList<Vertex> availablePlacesForStartingRings() {
        Vertex[][] board = currentState.gameBoard.getBoard();
        return this.gameSimulation.getAllPossibleStartingRingPlaces(board);
    }

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


    public boolean placeChip(int vertex) {
        Vertex boardVertex = currentState.gameBoard.getVertex(vertex);


        if (boardVertex != null && boardVertex.hasRing() && !boardVertex.hasCoin()) {
            String chipColor = boardVertex.getRing().getColour();

            //condition for same ring colors
            if (!boardVertex.getRing().getColour().equalsIgnoreCase(chipColor)) {
                showAlert("Warning", "Cannot place a " + chipColor + " chip in a ring of a different color!");
                return false;
            }

            Coin newChip = new Coin(chipColor.toLowerCase());
            boardVertex.setPlayObject(newChip);
            currentState.chipRingVertex = vertex;
            System.out.println("After setting chip, hasCoin at (" + boardVertex.getXposition() + ", " + boardVertex.getYposition() + "): " + boardVertex.hasCoin());
            currentState.chipNumber.add(vertex);
            // currentState.chipsRemaining--;
            currentState.chipPlaced = true;//after chip placement we can move the ring
            currentState.resetTurn();
            return true;
        } else {
            showAlert("Warning", "Cannot place a chip here");
        }
        return false;

    }


//    public boolean checkPlaceRingVertex(int frmVertex,int toVertex,ArrayList<Integer> availableMoves) {
//        Vertex targetVertex = currentState.gameBoard.getVertex(toVertex);
//        if (!availableMoves.contains(toVertex)){
//            showAlert("INVALID", "JJDJDJD");
//            return false;
//        }
//
//      else if (targetVertex.hasRing().currentState.chipNumber.contains(toVertex)) {
//            showAlert("Invalid Move", "Cannot move ring here as it already has an object.");
//            return false;
//        }
//        return true;
//    }


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
    public HashMap<Vertex, ArrayList<Move>> getAllMovesFromAllPositions(ArrayList<Vertex> allRingPositions){
        HashMap<Vertex, ArrayList<Move>> allMoves = new HashMap<Vertex, ArrayList<Move>>();
        for(Vertex v: allRingPositions){
            allMoves.put(v, this.possibleMoves(v));
            for(Move m: allMoves.get(v)){
                m.setStartingVertex(v);
            }
        }
        return allMoves;
    }

    public GameState getGameState(){

        return this.currentState;
    }

    // public void checkWinning(int vertex, String chipColor){
    //     chipColor=chipColor.toLowerCase();
    //     for (Direction direction: Direction.values()){
    //         int k = 1;
    //         k+=countChipsInOneDirection(vertex, chipColor, direction.getDeltaX(), direction.getDeltaY());
    //         k+=countChipsInOneDirection(vertex, chipColor, -direction.getDeltaX(), -direction.getDeltaY());


    //         //check based on terminal output

    //          if (k>=WIN_CONDITION){
    //              GameAlerts.alertRowCompletion(chipColor);
    //              //System.out.print("jdhjjdhdh");
    //              setRingSelectionMode(true);
    //              setWinningColor(chipColor);
    //              ringSelection(chipColor);
    //              return;
    //          }
    //     }
    // }
    public boolean win(ArrayList<Vertex> vertexesOfFlippedCoins){
        boolean win = false;
        if(vertexesOfFlippedCoins==null){
            return false;
        }
        for(Vertex v:vertexesOfFlippedCoins){
            int vertex = v.getVertextNumber();
            System.out.println("VERTEX BEING CHECKED: "+vertex);
            if(v.hasCoin()){
                System.out.println("COLOR "+v.getCoin().getColour());
                String color = v.getCoin().getColour();
                for (Direction direction: Direction.values()){
                    int k = 1;
    
                    int first = 0;
                    int second = 0;
                    System.out.println("CURRENT DIRECTION: "+direction.name());
                    first=countChipsInOneDirection(vertex, color, direction.getDeltaX(), direction.getDeltaY());
                    second=countChipsInOneDirection(vertex, color, -direction.getDeltaX(), -direction.getDeltaY());
                    if(first>=0 || second>=0){
                        k+=(first+second);
                    }
                    System.out.println("TOTAL K:"+k);
                    if (k>=WIN_CONDITION){
        
                        win = true;
                        System.out.print("WIN--------------");
                        System.out.println("------------K:"+k);
                        winningRing=win;
        
                        setRingSelectionMode(true);
                        // findAndSetAllWinningChips(color);
                        setWinningColor(color);
                        ringSelection(color);
        
                        return win;
                    }
                    // win = false;
                }

            }
            
        }



        // setRingSelectionMode(win);

        System.out.println("WIN STATE: "+win);
        return win;

    }

    public String winningColor(ArrayList<Vertex> vertexesOfFlippedCoins) {
        String color = getWinningColor();
        if (vertexesOfFlippedCoins == null) {
            return null;
        }
        for (Vertex v : vertexesOfFlippedCoins) {
            int vertex = v.getVertextNumber();
            System.out.println("VERTEX BEING CHECKED: " + vertex);
            if (v.hasCoin()) {
                System.out.println("COLOR " + v.getCoin().getColour());
                color = v.getCoin().getColour();
                for (Direction direction : Direction.values()) {
                    int k = 1;

                    int first = 0;
                    int second = 0;
                    System.out.println("CURRENT DIRECTION: " + direction.name());
                    first = countChipsInOneDirection(vertex, color, direction.getDeltaX(), direction.getDeltaY());
                    second = countChipsInOneDirection(vertex, color, -direction.getDeltaX(), -direction.getDeltaY());
                    if (first >= 0 || second >= 0) {
                        k += (first + second);
                    }
                    System.out.println("TOTAL K:" + k);
                    if (k >= WIN_CONDITION) {

//                        win = true;
                        System.out.print("WIN--------------");
                        System.out.println("------------K:" + k);
//                        winningRing = win;

//                        setRingSelectionMode(true);
//                        findAndSetAllWinningChips(color);
//                        setWinningColor(color);
//                        ringSelection(color);

                        return color;
                    }
                    // win = false;
                }

            }

        }
        // setRingSelectionMode(win);

//        System.out.println("WIN STATE: "+win);
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
    public boolean ringselectionmode(){
        return isRingSelectionMode;
    }
    public void setWinningRing(boolean winningRing) {
        this.winningRing = winningRing;
    }

    public void setWinningColor(String color) {
        this.winningColor = color;
    }

    public List<Integer> getWinningChips() {
        return winningChips;
    }

    public void clearWinningChips() {
        winningChips.clear();
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



    //method for calculating 5 chips in a row
    public int countChipsInOneDirection(int start, String chipColor, int dx, int dy){
        int k = 0;
        int x = currentState.gameBoard.getVertex(start).getXposition();
        int y = currentState.gameBoard.getVertex(start).getYposition();
        // if(currentState.gameBoard.getVertex(start)!=null){
        //     if(currentState.gameBoard.getVertex(start).hasCoin() && currentState.gameBoard.getVertex(start).getCoin().getColour().toLowerCase().equals(chipColor)){
        //         k=1;
        //     }
        // }
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
            //System.out.println("CHECKING "+v.getVertextNumber());
            if(v==null || !v.hasCoin() || !v.getCoin().getColour().toLowerCase().equals(chipColor)){
                //System.out.println("PROBLEM: "+v.getVertextNumber());
                if(v!=null){
                    //System.out.println("HAS COIN "+v.hasCoin());
                    //System.out.println(v.getCoin());
                    if(v.hasCoin()){
                        //System.out.println("COLOR WE NEED: "+chipColor);
                        //System.out.println("SAME COLOR: "+v.getCoin().getColour().toLowerCase().equals(chipColor));
                    }
                }

                //System.out.println("K: "+k);
                return k;
            }
            k++;
            //System.out.println("COUNT IT "+k);


        }
        // return k;
    }

    public List<Integer> getChipsInDirection(int startVertex, String chipColor, int dx, int dy) {
        List<Integer> chips = new ArrayList<>();
        Vertex currentVertex = currentState.gameBoard.getVertex(startVertex);

        if (currentVertex == null) {
            System.out.println("Starting vertex is null for: " + startVertex);
            return chips;
        }

        int x = currentState.gameBoard.getVertex(startVertex).getXposition();
        int y = currentState.gameBoard.getVertex(startVertex).getYposition();

        while (true) {
            x += dx;
            y += dy;

            int next = currentState.gameBoard.getVertexNumberFromPosition(x, y);
            if (next == -1) {
                System.out.println("Reached invalid position.");
                break;
            }

            Vertex v = currentState.gameBoard.getVertex(next);
            if (v == null) {
                System.out.println("Vertex " + next + " is null.");
                break;
            }

            if (!v.hasCoin() || !v.getCoin().getColour().toLowerCase().equalsIgnoreCase(chipColor.toLowerCase())) {
                System.out.println("Vertex " + next + " does not have the correct chip.");
                break;
            } else{
                
                System.out.println("Adding vertex " + next + " to chips.");
                chips.add(next);
            }

        }

        return chips;
    }

    public List<Integer> findAllWinningChips(String chipColor) {
        List<Integer> winningChips = new ArrayList<>();
        List<Vertex> allVertices = currentState.gameBoard.getAllVertices();

        System.out.println("Total vertices on board: " + allVertices.size());

        for (Vertex vertex : allVertices) {
            if (vertex.hasCoin() && vertex.getCoin().getColour().equalsIgnoreCase(chipColor)) {
                System.out.println("Checking vertex: " + vertex.getVertextNumber());
                List<Integer> chipsFromVertex = findWinningChipsFromVertex(vertex.getVertextNumber(), chipColor);
                System.out.println("Found chips from vertex " + vertex.getVertextNumber() + ": " + chipsFromVertex);
                winningChips.addAll(chipsFromVertex);
            }
        }

        List<Integer> distinctWinningChips = winningChips.stream().distinct().collect(Collectors.toList());
        System.out.println("Final winning chips: " + distinctWinningChips);

        return distinctWinningChips;
    }

    public List<List<Integer>> findAllWinningChipsMOD(String chipColor) {
        List<List<Integer>> winningChips = new ArrayList<>();
        List<Vertex> allVertices = currentState.getVertexesOfFlippedCoins();
        System.out.println(allVertices.get(0).getVertextNumber());

        for(Vertex v: allVertices){

            for (Direction direction : Direction.values()) {
                System.out.println("Checking asdasd: " + direction.name());
                List<Integer> chipsInOneDirection = getChipsInDirection(v.getVertextNumber(), v.getCoin().getColour(), direction.getDeltaX(), direction.getDeltaY());
                List<Integer> chipsInSecondDirection = getChipsInDirection(v.getVertextNumber(), v.getCoin().getColour(), -direction.getDeltaX(), -direction.getDeltaY());
                int result = chipsInOneDirection.size()+chipsInSecondDirection.size()+1;
                System.out.println(result);
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
                    
                    
                    System.out.println("WE HAVE CHIPS VALID "+currArray);
                    winningChips.add(currArray);}
            }
        }

        return winningChips;
    }

    public List<Integer> findWinningChipsFromVertex(int startVertex, String chipColor) {
        List<Integer> winningChips = new ArrayList<>();
        System.out.println("Finding winning chips from vertex: " + startVertex);

        for (Direction direction : Direction.values()) {
            System.out.println("Checking direction: " + direction.name());
            List<Integer> chipsInDirection = getChipsInDirection(startVertex, chipColor, direction.getDeltaX(), direction.getDeltaY());
            winningChips.addAll(chipsInDirection);
        }

        if (!winningChips.contains(startVertex)) {
            System.out.println("Including starting vertex: " + startVertex);
            winningChips.add(startVertex);
        }

        System.out.println("Winning chips from vertex " + startVertex + ": " + winningChips);
        return winningChips;
    }

    public void findAndSetAllWinningChips(String chipColor) {
        // List<Integer> allWinningChips = findAllWinningChips(chipColor);
        List<List<Integer>> a = findAllWinningChipsMOD(chipColor); // a has all of the positions that we can remove
        System.out.println(a);
        setWinningChips(a.get(0));
        System.out.println("Global Winning Chips Set: " + getWinningChips());
    }

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


    public int findClosestVertex(double xCoordinate, double yCoordinate){

        System.out.println();

        for(int i = 0 ; i < 85; i++){
            double vX = vertexCoordinates[i][0] + 18;
            double vY = vertexCoordinates[i][1] + 18;

            double xDist = Math.abs(xCoordinate - vX);
            double yDist = Math.abs(yCoordinate - vY);

            if(xDist<=10 && yDist <=10){
                System.out.println("Vertex Clicked: " + i);
                return i;
            }
        }
        return -1;
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public int[][] getVertexCoordinates() {
        return vertexCoordinates;
    }

    public int[] getcoordinates(int vertex){
        return vertexCoordinates[vertex];
    }

    public void resetGame(){
        currentState=new GameState();
        vertexCoordinates = new int[85][2];
        initialiseVertex();
    }


}