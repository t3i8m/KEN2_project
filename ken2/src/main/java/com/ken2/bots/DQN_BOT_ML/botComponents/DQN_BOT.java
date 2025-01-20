package com.ken2.bots.DQN_BOT_ML.botComponents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.Neuron;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions.ActivationFunction;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions.Linear;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions.ReLu;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameSimulation;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;

public class DQN_BOT  extends BotAbstract{
    private NeuralNetwork qNetwork;
    private NeuralNetwork targetNetwork; 
    public double epsilon; //max epsilon for the egreedy
    public double epsilonMin; // min epsilon for the egreedy
    public double epsilonDecay; // speed of the epsilon decrease
    private double gamma;  //discount for the Q update
    private Random random;
    private int actionSize;
    private ReplayBuffer replayBuffer = new ReplayBuffer(1000);
    private int[] mask;
    private int chipsToRemove;
    private List<Integer> winningChips = new ArrayList<>();
    private int updateCount = 0;
    private final int TARGET_UPDATE_FREQUENCY = 100;

    public DQN_BOT(String color){
        super(color);
        this.actionSize = 100;

        initializeNN();
        try{
            // this.qNetwork.saveWeights();
            this.qNetwork.loadWeights();

        } catch(Exception ex){
            // this.qNetwork.saveWeights();
            System.out.println(ex);
        }
        loadEpsilon();
        this.targetNetwork = copyNetwork(this.qNetwork);

        this.mask = new int[actionSize];
        this.epsilonMin = 0.01;
        this.epsilonDecay = 0.995;
        this.gamma = 0.1;
        this.random = new Random();

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
        if (Arrays.stream(stateVector).anyMatch(v -> Double.isNaN(v) || Double.isInfinite(v))) {
            System.out.println("stateVector has NaN/Inf: " + Arrays.toString(stateVector));
        }
        double[] qValues = qNetwork.predict(stateVector);
        if (Arrays.stream(qValues).anyMatch(Double::isNaN)) {
            System.out.println("NaN in qValues after predict for state=" 
                               + Arrays.toString(stateVector)
                               + " => " + Arrays.toString(qValues));
        }
        
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
        ArrayList<Move> allValidMoves = filterValidMoves(vertexMove, state);

        this.mask = fillMask(allValidMoves);
        qValues = filterQValues(qValues, this.mask);

       
        actionMapping.initializeActions(allValidMoves);
        int actionIndex = chooseAction(qValues, allValidMoves);

        if (epsilon > epsilonMin) {
            epsilon *= epsilonDecay;
        }

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
        if (chosenMove == null || allValidMoves.size()==0)  {
            System.out.println("All Possible Moves: " + allValidMoves.size());

            System.out.println("Action Index " + actionIndex + " does not map to a valid move!");
        }
        GameState nextState = moveState(state.clone(), chosenMove);

        double reward = Reward.calculateRewardWITHOUT(ge, state, chosenMove, nextState, super.getColor());
        BoardTransformation boardTransformNEW = new BoardTransformation(nextState.getGameBoard());

        // System.out.println(reward);
        // System.out.println(state.getGameBoard().strMaker());
        // System.out.println(chosenMove);
        // System.out.println(nextState.getGameBoard().strMaker());
        storeExperience(stateVector, actionIndex, reward, boardTransformNEW.toVector(super.getColor().toLowerCase().equals("white") ?"white":"black"));
        // storeExperience(stateVector, actionIndex, reward, boardTransformNEW.toVector(super.getColor().toLowerCase().equals("white") ?"black":"white"));
        return chosenMove;
    }

    public void saveEpsilon(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ken2\\src\\main\\java\\com\\ken2\\bots\\DQN_BOT_ML\\NeuralNetwork\\weights\\savedEpsilon.txt"))) {
            writer.write(String.valueOf(this.epsilon));
        }catch (IOException e) {
        e.printStackTrace();
    }

    }

    public void loadEpsilon(){
        double loadedEps = 1.0; 
        try (BufferedReader reader = new BufferedReader(new FileReader("ken2\\src\\main\\java\\com\\ken2\\bots\\DQN_BOT_ML\\NeuralNetwork\\weights\\savedEpsilon.txt"))) {
            loadedEps = Double.parseDouble(reader.readLine());
        } catch (IOException ex) {
            System.out.println("No saved epsilon found, defaulting to 1.0");
        }
        this.epsilon = loadedEps;

    }

    public void storeExperience(double[] state, int action, double reward, double[] nextState) {
        Experience experience = new Experience(state, action, reward, nextState);
        // replayBuffer.add(experience);
        double priority = calculateTDError(experience);

        replayBuffer.add(experience, priority);
       
    }

    public double calculateTDError(Experience exp) {
        double[] currentQ = qNetwork.predict(exp.getState());
        double[] nextQ = targetNetwork.predict(exp.getNextState());
        int[] maskForNextState = getMask(exp.getNextState());
        nextQ = filterQValues(nextQ, maskForNextState);

        int action = exp.getAction();
        double reward = exp.getReward();
        double maxNextQ = Arrays.stream(nextQ).max().orElse(0.0);
        
        return Math.abs(reward + gamma * maxNextQ - currentQ[action]);
    }
    

    private double[] filterQValues(double[] prevQvalues, int[] mask){
        // double[] newQValues = prevQvalues;
        for(int i = 0; i<mask.length;i++){
            if(mask[i]==1){
                continue;
            }else{
                prevQvalues[i] = -1e9;
            }
        }
        return prevQvalues;
    }

    private int[] fillMask(ArrayList<Move> allMoves){
        int[] filledMask = new int[actionSize];

        for(int i = 0; i<filledMask.length;i++){
            if(i<allMoves.size()){

                filledMask[i] = 1;
            }else{
                filledMask[i]=0;
            }
        }
        return filledMask;
    }

    private ArrayList<Move> filterValidMoves(HashMap<Vertex, ArrayList<Move>> vertexMove, GameState state) {
        GameEngine ge = new GameEngine();
        ArrayList<Move> validMoves = new ArrayList<>();

        for (Map.Entry<Vertex, ArrayList<Move>> entry : vertexMove.entrySet()) {
            if (entry == null || entry.getValue() == null) {
                continue; 
            }
    
            for (Move m : entry.getValue()) {
                if (isValidMove(state, m)) {
                    validMoves.add(m);
                }
            }
    
            
        }
    
        return validMoves;
    }

    
    public void train(int batchSize){
        ArrayList<Experience> buffer = replayBuffer.getBuffer();
        if (buffer.size() < batchSize) {
            return; 
        }
    
        ArrayList<Experience> sample = replayBuffer.createSample(batchSize);
        
        for(Experience exp: sample){
            double[] state = exp.getState();
            int action = exp.getAction();
            double reward = exp.getReward();
            double[] nextState = exp.getNextState();

            if (Arrays.stream(nextState).anyMatch(v -> Double.isNaN(v) || Double.isInfinite(v))) {
                System.out.println("nextState has NaN/Inf: train" + Arrays.toString(nextState));
            }
            if (Arrays.stream(state).anyMatch(v -> Double.isNaN(v) || Double.isInfinite(v))) {
                System.out.println("state has NaN/Inf: train" + Arrays.toString(state));
            }

            double[] qValues = qNetwork.predict(state);
            if (Arrays.stream(qValues).anyMatch(Double::isNaN)) {
                System.out.println("NaN in qValues after predict for state=" 
                                   + Arrays.toString(state)
                                   + " => " + Arrays.toString(qValues));
            }

            double[] nextQValuesTarget = targetNetwork.predict(nextState);

            if (Arrays.stream(nextQValuesTarget).anyMatch(Double::isNaN)) {
                System.out.println("NaN in qValues after predict for nextState=" 
                                   + Arrays.toString(state)
                                   + " => " + Arrays.toString(nextQValuesTarget));
            }
    
            int[] maskForNextState = getMask(nextState);
            nextQValuesTarget = filterQValues(nextQValuesTarget, maskForNextState);
    
            double maxNextQ = Arrays.stream(nextQValuesTarget).max().orElse(0.0);
    
            double targetQ = reward + (gamma * maxNextQ);

            double tdError = Math.abs(targetQ - qValues[action]);
            // double priority = Math.pow(tdError + epsilon, 1);

            qValues[action] = targetQ;
    
            qNetwork.trainMiniBatch(new double[][] { state }, new double[][] { qValues });
        }
    
        updateCount += 1;
        if (updateCount % TARGET_UPDATE_FREQUENCY == 0) {
            updateTargetNetwork();
        }
    }

    private void updateTargetNetwork() {
        copyWeights(qNetwork, targetNetwork);
    }

    private NeuralNetwork copyNetwork(NeuralNetwork source) {
    NeuralNetwork newNet = new NeuralNetwork(source.getLearningRate(), source.getLossFunction());
    
    for (Layer layer : source.getLayers()) {
        int numberOfNeurons = layer.getNeurons().size();
        int inputSize = layer.getNeurons().get(0).getWeights().length;
        
        Layer newLayer = new Layer(numberOfNeurons, inputSize, layer.getActivationFunction());
        
        newNet.addLayer(newLayer);
    }
        
        copyWeights(source, newNet);
        return newNet;
    }

    public NeuralNetwork getQNetwork(){
        return this.qNetwork;
    }

    private void copyWeights(NeuralNetwork from, NeuralNetwork to) {
        for (int i = 0; i < from.getLayers().size(); i++) {
            Layer sourceLayer = from.getLayers().get(i);
            Layer targetLayer = to.getLayers().get(i);

            for (int n = 0; n < sourceLayer.getNeurons().size(); n++) {
                Neuron srcNeuron = sourceLayer.getNeurons().get(n);
                Neuron trgNeuron = targetLayer.getNeurons().get(n);
                
                double[] srcWeights = srcNeuron.getWeights();
                double[] trgWeights = trgNeuron.getWeights();
                for (int w = 0; w < srcWeights.length; w++) {
                    trgWeights[w] = srcWeights[w];
                }

                trgNeuron.setBias(srcNeuron.getBias());
            }
        }
    }
    

    private int[] getMask(double[] nextStateVector) {

        BoardTransformation bf = new BoardTransformation();
        GameState possibleNextState = bf.fromVectorToState(nextStateVector);

        if (possibleNextState == null) {
            return new int[this.actionSize];
        }
    
        ArrayList<Vertex> allRingPositions =
            possibleNextState.getAllVertexOfColor(possibleNextState.currentPlayerColor());
    
        GameEngine ge = new GameEngine();
        HashMap<Vertex, ArrayList<Move>> vertexMoveMap
            = ge.getAllMovesFromAllPositions(allRingPositions, possibleNextState.gameBoard);
        ArrayList<Move> allValidMoves = filterValidMoves(vertexMoveMap, possibleNextState);
    

        int[] mask = new int[this.actionSize];
        for(int i = 0; i < mask.length; i++){
            if (i < allValidMoves.size()) {
                mask[i] = 1;
            } else {
                mask[i] = 0;
            }
        }
        return mask;
        }

    public int chooseAction(double[] qValues, ArrayList<Move> allPossible) {
        int maxActions = Math.min(qValues.length, allPossible.size());
    
        // if (random.nextDouble() < epsilon) { 
        //     // System.out.println(random.nextDouble());
        //     return random.nextInt(maxActions);
        // } else {
        //     return argMax(qValues, maxActions); 
        // }
        return argMax(qValues, maxActions); 

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
        this.qNetwork = new NeuralNetwork(0.0001, new MSE());
        qNetwork.addLayer(new Layer(128, 86, new ReLu()));  // first hidden layer
        qNetwork.addLayer(new Layer(64, 128, new ReLu())); 
        qNetwork.addLayer(new Layer(this.actionSize, 64, new Linear())); 
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