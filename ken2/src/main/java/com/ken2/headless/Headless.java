package com.ken2.headless;


import com.ken2.bots.Bot;
import com.ken2.bots.DQN_BOT_ML.utils.BoardTransformation;
import com.ken2.bots.DQN_BOT_ML.utils.Reward;
import com.ken2.utils.Player;

import javafx.scene.canvas.GraphicsContext;

import com.ken2.engine.GameEngine;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;
import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.Game_Components.Board.Ring;
import com.ken2.Game_Components.Board.Coin;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that manages multiple matches between two bots on the given board.
 * It also provides functionality to export detailed move-by-move data to a CSV file.
 */
public class Headless {

    private int games;
    private Bot whiteBot;
    private Bot blackBot;

    private int whiteWins = 0;
    private int blackWins = 0;
    private int draws     = 0;
    private int notValid  = 0;

    private int chipsToRemove;
    private List<Integer> winningChips = new ArrayList<>();

    

    /**
     * Constructs Headless with the number of games and two participating bots.
     *
     * @param games The total number of matches to be played.
     * @param whiteBot Bot instance playing as White.
     * @param blackBot Bot instance playing as Black.
     */
    public Headless(int games, Bot whiteBot, Bot blackBot) {
        this.games = games;
        this.whiteBot = whiteBot;
        this.blackBot = blackBot;
    }

    /**
     * Runs the specified number of matches and prints the overall statistics of the results.
     * This method does not create any CSV output. 
     * If you need CSV export, call exportGamesToCsv(filename) instead.
     */
    public void runGames() {
        for (int i = 0; i < games; i++) {
            String result;
            try {
                result = runSingleGame(whiteBot, blackBot, null, i+1);
            } catch (Exception ex) {
                result = "u";
            }

            switch (result) {
                case "white": whiteWins++; break;
                case "black": blackWins++; break;
                case "draw":  draws++;     break;
                case "u":     notValid++;  break;
            }
        }
        double whitePercent = 100.0 * whiteWins / games;
        double blackPercent = 100.0 * blackWins / games;
        double drawsPercent = 100.0 * draws / games;
        double notValidPercent = 100.0 * notValid / games;
        System.out.println("=========================================");
        System.out.println("Games played:       " + games);
        System.out.println(String.format("White wins:         %d (%.2f%%)", whiteWins, whitePercent));
        System.out.println(String.format("Black wins:         %d (%.2f%%)", blackWins, blackPercent));
        System.out.println(String.format("Draws:              %d (%.2f%%)", draws, drawsPercent));
        System.out.println(String.format("Not Valid games:    %d (%.2f%%)", notValid, notValidPercent));
        System.out.println("=========================================");
    }

    public void runGamesWithoutStats() {
        for (int i = 0; i < games; i++) {
            String result;
            try {
                result = runSingleGame(whiteBot, blackBot, null, i+1);
            } catch (Exception ex) {
                System.out.println(ex);

                result = "u";
            }

            switch (result) {
                case "white": whiteWins++; break;
                case "black": blackWins++; break;
                case "draw":  draws++;     break;
                case "u":     notValid++;  break;
            }
        }

    }

    public int getWhiteWins() {
        return whiteWins;
    }
    
    public int getBlackWins() {
        return blackWins;
    }
    
    public int getDraws() {
        return draws;
    }
    
    public int getInvalidGames() {
        return notValid;
    }
    

    /**
     * Runs the specified number of matches and writes detailed move data to a CSV file.
     * Also prints overall statistics at the end.
     */
    public void exportGamesToCsv() {
        String csvFilePath = "ken2\\src\\main\\java\\com\\ken2\\headless\\result\\moves_log.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFilePath))) {
            // Write CSV header
            writer.println("GameIndex,MoveNumber,CurrentPlayer,MoveType,FromVertex,ToVertex,ChipsRemaining,WhiteScore,BlackScore,CoinsFlippedVertices,GameResult");

            for (int i = 0; i < games; i++) {
                List<GameMoveRecord> logs = new ArrayList<>();
                String result;
                try {
                    result = runSingleGame(whiteBot, blackBot, logs, i + 1);
                } catch (Exception ex) {
                    System.out.println(ex);
                    result = "u";
                }
                switch (result) {
                    case "white": whiteWins++; break;
                    case "black": blackWins++; break;
                    case "draw":  draws++;     break;
                    case "u":     notValid++;  break;
                }

                // Write each move log to CSV
                for (GameMoveRecord record : logs) {
                    writer.println(record.toCsvLine());
                }
            }

            double whitePercent = 100.0 * whiteWins / games;
            double blackPercent = 100.0 * blackWins / games;
            double drawsPercent = 100.0 * draws / games;
            double notValidPercent = 100.0 * notValid / games;


            System.out.println("=========================================");
            System.out.println("CSV Export completed: " + csvFilePath);
            System.out.println("Games played:       " + games);
            System.out.println(String.format("White wins:         %d (%.2f%%)", whiteWins, whitePercent));
            System.out.println(String.format("Black wins:         %d (%.2f%%)", blackWins, blackPercent));
            System.out.println(String.format("Draws:              %d (%.2f%%)", draws, drawsPercent));
            System.out.println(String.format("Not Valid games:    %d (%.2f%%)", notValid, notValidPercent));
            System.out.println("=========================================");

        } catch (IOException e) {
            System.err.println("Failed to write CSV file: " + e.getMessage());
        }
    }

    /**
     * Manages a single match between the two bots (White and Black).
     * Returns a string indicating the result: "white", "black", "draw", or "u".
     *
     * @param whiteBot Bot instance playing as White.
     * @param blackBot Bot instance playing as Black.
     * @param logs     A list to store move-by-move records. If null, no logging is performed.
     * @param gameIndex The index of the current game in the series.
     * @return The result of the match ("white", "black", "draw", or "u").
     */
    private String runSingleGame(Bot whiteBot, Bot blackBot, List<GameMoveRecord> logs, int gameIndex) {

        GameEngine gameEngine = new GameEngine();
        gameEngine.resetGame();

        gameEngine.currentState.chipsRemaining = 51;
        String currentPlayerColor="";
        int whiteScore = 0;
        int blackScore = 0;
        int moveNumber = 1;
        //////
        //////////

        while (true) {
            try{
                GameState state = gameEngine.currentState;
                Game_Board board = state.getGameBoard();
                GameState previuos = gameEngine.currentState.clone();
                GameState newState ;
                double reward = 0.0;
    
    
    
    
                Bot currentBot = state.isWhiteTurn ? whiteBot : blackBot;
    
                currentPlayerColor = state.isWhiteTurn ? "white" : "black";
                // System.out.println(state.currentPlayerColor());
                // System.out.println(currentPlayerColor);

                // System.out.println(currentPlayerColor);
                // BoardTransformation boardToVector = new BoardTransformation(board);
                // System.out.println("BEFORE: \n"+board.strMaker());
                // double[] vectorBoard = boardToVector.toVector(currentPlayerColor);
    
                // System.out.println("AFTER: \n"+boardToVector.fromVector(vectorBoard).strMaker());
                // System.out.println(Arrays.toString(vectorBoard));
                // System.out.println(currentPlayerColor);
    
                if (state.ringsPlaced < 10) {
                    Move ringPlacement = currentBot.makeMove(state);
                    if (ringPlacement == null) {
                        System.out.println("ringPlacement is null. CurrentBot: " + currentBot.getName());
    
                        return "u";
                    }
    
                    int chosenVertex = board.getVertexNumberFromPosition(
                            ringPlacement.getXposition(),
                            ringPlacement.getYposition()
                    );
                    Vertex boardV = board.getVertex(chosenVertex);
                    if (boardV == null) {
                        System.out.println("boardV is null. CurrentBot: " + currentBot.getName());
    
                        return "u";
                    }
                    Ring ring = new Ring(currentBot.getColor());
                    boardV.setPlayObject(ring);
    
                    if (logs != null) {
                        GameMoveRecord rec = new GameMoveRecord();
                        rec.gameIndex = gameIndex;
                        rec.moveNumber = moveNumber;
                        rec.currentPlayer = currentPlayerColor;
                        rec.moveType = "RingPlacement";
                        rec.fromVertex = -1; // Not applicable
                        rec.toVertex = chosenVertex;
                        rec.chipsRemaining = state.chipsRemaining;
                        rec.whiteScore = whiteScore;
                        rec.blackScore = blackScore;
                        rec.coinsFlippedVertices = "";
                        logs.add(rec);
                    }
    
                    ////////////
                    // newState = state.clone();
    
                    // System.out.println("\n"+"Move " + moveNumber +":");
                    // reward = Reward.calculateReward(gameEngine, previuos, ringPlacement, newState, currentPlayerColor);
                    // System.out.println("TOTAL REWARD = " + reward+" BOT color: "+currentPlayerColor+" BOT type "+currentBot.getName());
    
    
                } else {
                    Move move = currentBot.makeMove(state.clone());
    
                    if (move == null) {
                        System.out.println("Move is null. CurrentBot: " + currentBot.getName());
    
    
                        return "u";
                    }
    
                    Vertex fromVertex = move.getStartingVertex();
                    if (fromVertex == null) {
                        System.out.println("fromVertex is null. CurrentBot: " + currentBot.getName());
    
                        return "u";
                    }
    
                    int fromIndex = fromVertex.getVertextNumber();
                    int toIndex = board.getVertexNumberFromPosition(
                            move.getXposition(),
                            move.getYposition()
                    );
    
                    if (toIndex < 0) {
                        System.out.println("toIndex is null. CurrentBot: " + currentBot.getName());
    
                        return "u";
                    }
    
                    gameEngine.placeChip(fromIndex);
    
                    Move currentMove = gameEngine.gameSimulation.simulateMove(
                            board,
                            gameEngine.currentState.gameBoard.getVertex(fromIndex),
                            gameEngine.currentState.gameBoard.getVertex(toIndex)
                    );
    
                    Vertex sourceV = gameEngine.currentState.gameBoard.getVertex(fromIndex);
                    if (sourceV == null) {
                        System.out.println("sourceV is null. CurrentBot: " + currentBot.getName());
    
                        return "u";
                    }
    
                    Ring ringToMove;
                    if (sourceV.hasRing()) {
                        ringToMove = (Ring) sourceV.getRing();
                        sourceV.setRing(null);
                    } else {
                        System.out.println("hasRing is null. CurrentBot: " + currentBot.getName());
    
                        return "u";
                    }
    
                    List<Coin> flippedCoins = currentMove.getFlippedCoins();
                    ArrayList<Vertex> verticesToFlip = new ArrayList<>();
    
                    if (flippedCoins != null && !flippedCoins.isEmpty()) {
                        for (Coin coin : flippedCoins) {
                            Vertex coinVert = gameEngine.currentState.gameBoard.getVertex(coin.getVertex());
                            if (coinVert == null) {
                                Vertex newVert = gameEngine.currentState.gameBoard.getVertex(coin.getVertex());
                                if (!verticesToFlip.contains(newVert)) {
                                    verticesToFlip.add(newVert);
                                }
                                continue;
                            }
                            if (!verticesToFlip.contains(coinVert)) {
                                verticesToFlip.add(coinVert);
                            }
                        }
                    }
    
                    gameEngine.gameSimulation.flipCoinsByVertex(verticesToFlip, gameEngine.currentState.gameBoard);
                    Vertex targetV = gameEngine.currentState.gameBoard.getVertex(toIndex);
    
                    if (targetV == null || targetV.hasRing()) {
                        System.out.println("targetV is null. CurrentBot: " + currentBot.getName());
    
                        return "u";
                    }
                    targetV.setRing(ringToMove);
    
                    gameEngine.currentState.chipsRemaining--;
                    gameEngine.currentState.chipRingVertex = -1;
                    gameEngine.currentState.chipPlaced = false;
                    gameEngine.currentState.selectedRingVertex = -1;
                    gameEngine.currentState.updateChipsRingCountForEach();
                    gameEngine.currentState.setVertexesOfFlippedCoins(verticesToFlip);
                    if (!verticesToFlip.contains(sourceV)) {
                        verticesToFlip.add(sourceV);
                    }
    
                    if (gameEngine.currentState.chipsRemaining <= 0) {
                        if (logs != null) {
                            GameMoveRecord rec = new GameMoveRecord();
                            rec.gameIndex = gameIndex;
                            rec.moveNumber = moveNumber;
                            rec.currentPlayer = currentPlayerColor;
                            rec.moveType = "RingMovement";
                            rec.fromVertex = fromIndex;
                            rec.toVertex = toIndex;
                            rec.chipsRemaining = state.chipsRemaining;
                            rec.whiteScore = whiteScore;
                            rec.blackScore = blackScore;
                            rec.coinsFlippedVertices = coinsFlippedString(flippedCoins);
                            rec.gameResult = "draw";
                            logs.add(rec);
                        }
                        // newState = gameEngine.currentState.clone();
                        // System.out.println(gameEngine.currentState.chipsRemaining);
                        // System.out.println("\n"+"Move " + moveNumber +":");
                        // reward = Reward.calculateReward(gameEngine, previuos, move, newState, currentPlayerColor);
                        // System.out.println("TOTAL REWARD = " + reward+" BOT color: "+currentPlayerColor+" BOT type "+currentBot.getName());
    
                        return "draw";
                    }
    
                    // GameEngine newGE = gameEngine.clone();
    
                    String gameResult = null;
                    boolean isWinningRow = gameEngine.win(gameEngine.currentState.getVertexesOfFlippedCoins());
                    
                    if (isWinningRow) {
                        
                        String winnerColor = gameEngine.getWinningColor();
                        Bot currentPlayer = winnerColor.equalsIgnoreCase("white") ? whiteBot : blackBot;
    
                        Vertex vertexToRemoveBOT = currentPlayer.removeRing(gameEngine.currentState);
                        handleWinningRing(vertexToRemoveBOT.getVertextNumber(), gameEngine);
    
                        ArrayList<Integer> allRemoveChips = currentPlayer.removeChips(gameEngine.currentState);
                        for (Integer vert : allRemoveChips) {
                            handleChipRemove(vert, gameEngine, currentBot);
                        }
    
                        gameEngine.setWinningRing(false);
                        gameEngine.setChipRemovalMode(false);
    
                        if (winnerColor.equalsIgnoreCase("white")) {
                            whiteScore++;
                            if (whiteScore == 3) {
                                gameResult = "white";
                            }
                        } else {
                            blackScore++;
                            if (blackScore == 3) {
                                gameResult = "black";
                            }
                        }
    
    
                    }
                    
                    
                    // newState = gameEngine.currentState.clone();
                    // System.out.println("\n"+"Move " + moveNumber +":");
                    // reward = Reward.calculateReward(gameEngine, previuos, move, newState, currentPlayerColor);
                    // System.out.println("TOTAL REWARD = " + reward+" BOT color: "+currentPlayerColor+" BOT type "+currentBot.getName());
    
                    if (logs != null) {
                        GameMoveRecord rec = new GameMoveRecord();
                        rec.gameIndex = gameIndex;
                        rec.moveNumber = moveNumber;
                        rec.currentPlayer = currentPlayerColor;
                        rec.moveType = "RingMovement";
                        rec.fromVertex = fromIndex;
                        rec.toVertex = toIndex;
                        rec.chipsRemaining = gameEngine.currentState.chipsRemaining;
                        rec.whiteScore = whiteScore;
                        rec.blackScore = blackScore;
                        rec.coinsFlippedVertices = coinsFlippedString(flippedCoins);
                        rec.gameResult = gameResult; 
                        logs.add(rec);
                    }
    
                    if (gameResult != null) {
                        return gameResult;
                    }
                }
    
                if (gameEngine.currentState.chipsRemaining <= 0) {
                    System.out.println("HELLO");
                    return "draw";
                }
                if (whiteScore == 3) {
                    return "white";
                }
                if (blackScore == 3) {
                    return "black";
                }
    
                gameEngine.currentState.resetTurn();
                switchTurn(gameEngine.currentState);
                moveNumber+=1;
    
            
            } catch(Exception ex){
                // System.out.println(ex+" "+currentPlayerColor);

                ex.printStackTrace(); // Prints the full stack trace

            }
        }


    }

    /**
     * Changes the turn from one player to another.
     *
     * @param state The current game state.
     */
    private void switchTurn(GameState state) {
        state.isWhiteTurn = !state.isWhiteTurn;
    }

    /**
     * Removes the winning ring of the winning color from the given vertex.
     *
     * @param vertex The vertex index where the ring to be removed is located.
     * @param gameEngine The game engine handling the match state.
     */
    public void handleWinningRing(int vertex, GameEngine gameEngine) {
        Vertex v = gameEngine.currentState.gameBoard.getVertex(vertex);
        if (v != null
            && v.hasRing()
            && v.getRing().getColour().equalsIgnoreCase(gameEngine.getWinningColor())) {

            v.setRing(null);
            gameEngine.findAndSetAllWinningChips(gameEngine.getWinningColor());
            gameEngine.currentState.setAllPossibleCoinsToRemove(gameEngine.getWinningChips());
            gameEngine.setRingSelectionMode(false);
            gameEngine.setChipRemovalMode(true);
            chipsToRemove = 5;
        }
    }

    /**
     * Removes the specified chip if it belongs to the winning player.
     *
     * @param vertex The vertex index of the chip to be removed.
     * @param gameEngine The game engine handling the match state.
     * @param activeBot The bot currently making the removal.
     */
    public void handleChipRemove(int vertex, GameEngine gameEngine, Bot activeBot) {
        if (!gameEngine.getWinningChips().contains(vertex)) {
            return;
        }

        Vertex v = gameEngine.currentState.gameBoard.getVertex(vertex);
        if (v == null || !v.hasCoin()) {
            return;
        }

        String currColor = v.getCoin().getColour().toLowerCase();
        if (currColor.equalsIgnoreCase(gameEngine.getWinningColor())) {
            v.setPlayObject(null);
            gameEngine.currentState.chipsRemaining += 1;
            chipsToRemove--;
            gameEngine.getWinningChips().remove(Integer.valueOf(vertex));

            List<Integer> adjacentVertices = gameEngine.getAdjacentVertices(vertex);
            List<Integer> validRemovableChips = new ArrayList<>();
            for (int adjVertex : adjacentVertices) {
                if (winningChips.contains(adjVertex)) {
                    Vertex adjV = gameEngine.currentState.gameBoard.getVertex(adjVertex);
                    if (adjV != null && adjV.hasCoin()
                            && adjV.getCoin().getColour().equalsIgnoreCase(gameEngine.getWinningColor())) {
                        validRemovableChips.add(adjVertex);
                    }
                }
            }

            if (chipsToRemove <= 0) {
                gameEngine.setChipRemovalMode(false);
                gameEngine.setRingSelectionMode(false);
                gameEngine.setWinningColor("");
                winningChips.clear();
                gameEngine.getWinningChips().clear();

                if (currColor.equals(activeBot.getColor().toLowerCase())) {
                    switchTurn(gameEngine.currentState);
                }
            }
        }
    }

    /**
     * Converts a list of flipped coins to a string (e.g. "12;15;19").
     *
     * @param flippedCoins The list of flipped coins.
     * @return A semicolon-separated list of vertex indices.
     */
    private String coinsFlippedString(List<Coin> flippedCoins) {
        if (flippedCoins == null || flippedCoins.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < flippedCoins.size(); i++) {
            sb.append(flippedCoins.get(i).getVertex());
            if (i < flippedCoins.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }
}
