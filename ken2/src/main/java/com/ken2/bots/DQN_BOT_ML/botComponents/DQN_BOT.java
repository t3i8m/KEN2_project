package com.ken2.bots.DQN_BOT_ML.botComponents;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ken2.Game_Components.Board.Coin;
import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.PlayObj;
import com.ken2.Game_Components.Board.Ring;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.bots.Bot;
import com.ken2.bots.BotAbstract;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.NeuralNetwork;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.LoosFunction.MSE;
import com.ken2.bots.DQN_BOT_ML.utils.BoardTransformation;
import com.ken2.bots.DQN_BOT_ML.utils.ReplayBuffer;
import com.ken2.bots.DQN_BOT_ML.utils.Reward;
import com.ken2.bots.DQN_BOT_ML.utils.Experience;
import com.ken2.bots.DQN_BOT_ML.utils.ActionIndexer;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.Layer;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions.ActivationFunction;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions.Linear;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions.ReLu;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameSimulation;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;

public class DQN_BOT  extends BotAbstract{
    private NeuralNetwork qNetwork;
    public double epsilon; //max epsilon for the egreedy
    public double epsilonMin; // min epsilon for the egreedy
    public double epsilonDecay; // speed of the epsilon decrease
    private double gamma;  //discount for the Q update
    private Random random;
    private int actionSize = 25;
    private ReplayBuffer replayBuffer = new ReplayBuffer(1000);

    private int chipsToRemove;
    private List<Integer> winningChips = new ArrayList<>();


    public DQN_BOT(String color){
        super(color);

        initializeNN();
        this.epsilon = 1;
        this.epsilonMin = 0.01;
        this.epsilonDecay = 0.995;
        this.gamma = 0.99;
        this.random = new Random();
        this.actionSize = 25; //CAN BE ADJUSTED;

    }

    public DQN_BOT(String color,NeuralNetwork qNetwork, double epsilon, double epsilonMin, double epsilonDecay, double gamma) {
        super(color);
        this.qNetwork = qNetwork;
        this.epsilon=epsilon;
        this.epsilonMin = epsilonMin;
        this.epsilonDecay=epsilonDecay;
        this.gamma = gamma;
        this.random = new Random();
    }

    public Move makeMove(GameState state){
        Game_Board board = state.getGameBoard();
        GameEngine ge = new GameEngine();

        ActionIndexer actionMapping = new ActionIndexer();
        BoardTransformation boardTransform = new BoardTransformation(board);
        double[] stateVector = boardTransform.toVector(super.getColor());
        double[] qValues = qNetwork.predict(stateVector);

        
        ArrayList<Vertex> allFreePositions = board.getAllFreeVertexes();

        if (allFreePositions.isEmpty()) {
            return null;
        }

        if (state.ringsPlaced < 10) {
            Vertex targetVertex = allFreePositions.get(random.nextInt(allFreePositions.size()));
            int vertexNumber = targetVertex.getVertextNumber();
            int[] targetPosition = board.getVertexPositionByNumber(vertexNumber);
            PlayObj ring = new Ring(super.getColor());
            board.updateBoard(vertexNumber, ring);
            state.ringsPlaced++;
            return new Move(targetPosition[0], targetPosition[1], null);
        }


        ArrayList<Vertex> allRingPositions = state.getAllVertexOfColor(state.currentPlayerColor());
        HashMap<Vertex, ArrayList<Move>> vertexMove = ge.getAllMovesFromAllPositions(allRingPositions, state.gameBoard);
        ArrayList<Move> allPossible = new ArrayList<>();
        for (Map.Entry<Vertex, ArrayList<Move>> entry : vertexMove.entrySet()) {
            if(entry.getValue()==null) continue;
            for(Move m: entry.getValue()){
                if(entry==null){
                    continue;
                }
                int vertexFrom = m.getStartingVertex().getVertextNumber();
                int vertexTo = state.gameBoard.getVertexNumberFromPosition(m.getXposition(), m.getYposition());

                if (!isValidMove(state, m)) {

                    continue; 
                }
                if(vertexFrom<0 || vertexTo<0){
                    continue;
                }

                Vertex targetVertex = state.getGameBoard().getVertex(
                    state.getGameBoard().getVertexNumberFromPosition(m.getXposition(), m.getYposition())
                );
                if (targetVertex == null || targetVertex.hasCoin() || targetVertex.hasRing()) continue;

                allPossible.add(m);
            }
        }
        
        actionMapping.initializeActions(allPossible);
        int actionIndex = chooseAction(qValues, allPossible);

        Move chosenMove;
        try{
            chosenMove= actionMapping.getMove(actionIndex);

        }catch(Exception ex){
            System.out.println(ex);
            chosenMove=null;
        }

        // System.out.println(chosenMove);
        // // System.out.println(chosenMove);
        // System.out.println("Q Values: " + Arrays.toString(qValues));
        // System.out.println("Chosen Action Index: " + actionIndex);
        // System.out.println("Chosen Move: " + chosenMove);
        if (chosenMove == null || allPossible.size()==0)  {
            System.out.println("All Possible Moves: " + allPossible.size());

            System.out.println("Action Index " + actionIndex + " does not map to a valid move!");
        }
        GameState nextState = moveState(state.clone(), chosenMove);

        double reward = Reward.calculateRewardWITHOUT(ge, state, chosenMove, nextState, super.getColor());
        BoardTransformation boardTransformNEW = new BoardTransformation(nextState.getGameBoard());

        // System.out.println(reward);
        // System.out.println(state.getGameBoard().strMaker());
        // System.out.println(chosenMove);
        // System.out.println(nextState.getGameBoard().strMaker());

        storeExperience(stateVector, actionIndex, reward, boardTransformNEW.toVector(super.getColor().toLowerCase().equals("white") ?"black":"white"));
        return chosenMove;
    }

    public void storeExperience(double[] state, int action, double reward, double[] nextState) {
        Experience experience = new Experience(state, action, reward, nextState);
        replayBuffer.add(experience);
    }

    public void train(int batchSize){
        ArrayList<Experience> buffer = replayBuffer.getBuffer();
        if (buffer.size() < batchSize) {
            return; //not enough replays
        }

        ArrayList<Experience> sample = replayBuffer.createSample(batchSize);
        for(Experience exp: buffer){
            double[] state = exp.getState();
            int action = exp.getAction();
            double reward = exp.getReward();
            double[] nextState = exp.getNextState();

            double[] qValues = qNetwork.predict(state);
            double[] nextQValues = qNetwork.predict(nextState);

            double maxNextQ = Arrays.stream(nextQValues).max().orElse(0.0);
            // System.out.println("qValues size: " + qValues.length);
            // System.out.println("Action index: " + action);
            // System.out.println("qValues: " + Arrays.toString(qValues));
            qValues[action] = reward + (gamma * maxNextQ);

            if (action < 0 || action >= qValues.length) {
                throw new IllegalStateException("Action index " + action + " out of bounds for qValues size " + qValues.length);
            }
            
            

            try {
                qNetwork.trainMiniBatch(new double[][]{state}, new double[][]{qValues});
            } catch (Exception e) {
                System.out.println("Error during training: " + e.getMessage());
                e.printStackTrace();
                return;
            }


        }
    }

    public int chooseAction(double[] qValues, ArrayList<Move> allPossible) {
        int maxActions = Math.min(qValues.length, allPossible.size());
    
        if (random.nextDouble() < epsilon) { 
            return random.nextInt(maxActions);
        } else {
            return argMax(qValues, maxActions); 
        }
    }
    
    //finds an index with the highest q-value
    public int argMax(double[] qValues, int maxActions) {
        int bestAction = 0;
        for (int i = 1; i < maxActions; i++) {
            if (qValues[i] > qValues[bestAction]) {
                bestAction = i;
            }
        }
        return bestAction;
    }
    

    public String getName() {
        return "DQN BOT";
    }

    public void initializeNN(){
        this.qNetwork = new NeuralNetwork(0.001, new MSE());
        qNetwork.addLayer(new Layer(128, 86, new ReLu()));  // first hidden layer
        qNetwork.addLayer(new Layer(64, 128, new ReLu())); 
        qNetwork.addLayer(new Layer(actionSize, 64, new Linear())); 
    }



    private boolean isValidMove(GameState state, Move move) {
        Vertex[][] board = state.getGameBoard().getBoard();
        Vertex startVertex = move.getStartingVertex();
    
        if (startVertex == null || !startVertex.hasRing()) {

            return false;
        }
    
        PlayObj ring = (Ring)startVertex.getRing();
    
        if (!ring.getColour().equals(super.getColor())) {

            return false;
        }
    
        int startX = startVertex.getXposition();
        int startY = startVertex.getYposition();
        int targetX = move.getXposition();
        int targetY = move.getYposition();
    
        com.ken2.engine.Direction direction = move.getDirection();
    
        if (direction == null) {
            return false;
        }
    
        int deltaX = direction.getDeltaX();
        int deltaY = direction.getDeltaY();
    
        if (deltaX == 0 && deltaY == 0) {
            return false;
        }
    
        int dx = targetX - startX;
        int dy = targetY - startY;
    
        if (deltaX == 0) {
            if (dx != 0) {
                return false; 
            }
            if (deltaY == 0 || dy % deltaY != 0 || (dy / deltaY) <= 0) {
                return false; 
            }
        } else if (deltaY == 0) {
            if (dy != 0) {
                return false; 
            }
            if (deltaX == 0 || dx % deltaX != 0 || (dx / deltaX) <= 0) {
                return false; 
            }
        } else {
            if (dx % deltaX != 0 || dy % deltaY != 0) {
                return false; 
            }
            int stepsX = dx / deltaX;
            int stepsY = dy / deltaY;
            if (stepsX != stepsY || stepsX <= 0) {
                return false; 
            }
        }
    
        int currentX = startX;
        int currentY = startY;
    
        while (true) {
            currentX += deltaX;
            currentY += deltaY;
    
            if (currentX == targetX && currentY == targetY) {
                break;
            }
    
            if (currentX < 0 || currentX >= board.length || currentY < 0 || currentY >= board[0].length) {
                return false; 
            }
    
            Vertex currentVertex = board[currentX][currentY];
    
            if (currentVertex != null && currentVertex.hasRing()) {
                return false; 
            }
    
        }
    
        Vertex targetVertex = board[targetX][targetY];
    
        if (targetVertex == null || targetVertex.hasRing() || targetVertex.hasCoin()) {
            return false; 
        }
    
        return true;
    }

    private GameState moveState(GameState state, Move move) {
        GameState newState = state.clone();
        
        Game_Board board = state.getGameBoard();

        String thisBotColor = super.getColor().toLowerCase().equals("white")? "white":"black";
        Bot thisBot = new DQN_BOT(thisBotColor);
        Bot opponentBot = new DQN_BOT(thisBotColor.toLowerCase().equals("white")?"black":"white");

        String currentPlayerColor="";
    
        currentPlayerColor = state.isWhiteTurn ? "white" : "black";

        GameEngine tempEngine = new GameEngine();
        tempEngine.currentState = newState;
    
        Vertex fromVertex = move.getStartingVertex();
    
        if (fromVertex == null) {
            System.out.println("Error: Source vertex is null.");
            return state; 
        }

        int fromIndex = fromVertex.getVertextNumber();
        int toIndex = board.getVertexNumberFromPosition(
                move.getXposition(),
                move.getYposition()
        );
    
        if (toIndex == -1) {
            System.out.println("Error: Invalid target vertex index.");
            return state;
        }

        tempEngine.placeChip(fromIndex);
        Move currentMove = tempEngine.gameSimulation.simulateMove(
            board,
            tempEngine.currentState.gameBoard.getVertex(fromIndex),
            tempEngine.currentState.gameBoard.getVertex(toIndex)
        );

        Vertex sourceV = tempEngine.currentState.gameBoard.getVertex(fromIndex);

    
        if (sourceV == null) {
            System.out.println("sourceV is null. : ");

            return state;
        }

        Ring ringToMove;
        if (sourceV.hasRing()) {
            ringToMove = (Ring) sourceV.getRing();
            sourceV.setRing(null);
        } else {
            System.out.println("hasRing is null. CurrentBot: " );

            return state;
        }

    
        List<Coin> flippedCoins = currentMove.getFlippedCoins();
        ArrayList<Vertex> verticesToFlip = new ArrayList<>();
        if (flippedCoins != null && !flippedCoins.isEmpty()) {
            for (Coin coin : flippedCoins) {
                Vertex coinVert = tempEngine.currentState.gameBoard.getVertex(coin.getVertex());
                if (coinVert == null) {
                    Vertex newVert = tempEngine.currentState.gameBoard.getVertex(coin.getVertex());
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

        tempEngine.gameSimulation.flipCoinsByVertex(verticesToFlip, tempEngine.currentState.gameBoard);
        Vertex targetV = tempEngine.currentState.gameBoard.getVertex(toIndex);

        if (targetV == null || targetV.hasRing()) {
            System.out.println("targetV is null. CurrentBot: ");

            return state;
        }
        targetV.setRing(ringToMove);

    
        tempEngine.currentState.chipsRemaining--;
        tempEngine.currentState.chipRingVertex = -1;
        tempEngine.currentState.chipPlaced = false;
        tempEngine.currentState.selectedRingVertex = -1;
        tempEngine.currentState.updateChipsRingCountForEach();
        tempEngine.currentState.setVertexesOfFlippedCoins(verticesToFlip);
        if (!verticesToFlip.contains(sourceV)) {
            verticesToFlip.add(sourceV);
        }
        boolean isWinningRow = tempEngine.win(tempEngine.currentState.getVertexesOfFlippedCoins());

    
        if (isWinningRow) {
                        
            String winnerColor = tempEngine.getWinningColor();
            Bot currentPlayer = winnerColor.equalsIgnoreCase(super.getColor()) ? thisBot : opponentBot;

            Vertex vertexToRemoveBOT = currentPlayer.removeRing(tempEngine.currentState);
            
            handleWinningRing(vertexToRemoveBOT.getVertextNumber(), tempEngine);

            ArrayList<Integer> allRemoveChips = currentPlayer.removeChips(tempEngine.currentState);
            for (Integer vert : allRemoveChips) {
                handleChipRemove(vert, tempEngine, currentPlayer);
            }

            tempEngine.setWinningRing(false);
            tempEngine.setChipRemovalMode(false);


    
    
        }
    
        return tempEngine.currentState;
    }

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

    private void switchTurn(GameState state) {
        state.isWhiteTurn = !state.isWhiteTurn;
    }
    
}