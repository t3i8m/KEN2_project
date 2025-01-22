package com.ken2_1.bots.DQN_BOT_ML.botComponents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ken2_1.Game_Components.Board.Coin;
import com.ken2_1.Game_Components.Board.Game_Board;
import com.ken2_1.Game_Components.Board.PlayObj;
import com.ken2_1.Game_Components.Board.Ring;
import com.ken2_1.Game_Components.Board.Vertex;
import com.ken2_1.bots.Bot;
import com.ken2_1.bots.BotAbstract;
import com.ken2_1.bots.DQN_BOT_ML.NeuralNetwork.NeuralNetwork;
import com.ken2_1.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.LoosFunction.MSE;
import com.ken2_1.bots.DQN_BOT_ML.utils.BoardTransformation;
import com.ken2_1.bots.DQN_BOT_ML.utils.ReplayBuffer;
import com.ken2_1.bots.DQN_BOT_ML.utils.Reward;
import com.ken2_1.bots.DQN_BOT_ML.utils.Experience;
import com.ken2_1.bots.DQN_BOT_ML.utils.ActionIndexer;
import com.ken2_1.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.Layer;
import com.ken2_1.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.Neuron;
import com.ken2_1.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions.Linear;
import com.ken2_1.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions.ReLu;
import com.ken2_1.engine.GameEngine;
import com.ken2_1.engine.GameState;
import com.ken2_1.engine.Move;

/**
 * Implementation of a Deep Q-Network (DQN) based bot for decision-making in a game.
 * The bot uses a neural network to approximate Q-values and chooses actions based
 * on an ε-greedy policy.
 */
public class DQN_BOT  extends BotAbstract{
    public NeuralNetwork qNetwork;
    private NeuralNetwork targetNetwork; 
    public double epsilon; //max epsilon for the egreedy
    public double epsilonMin; // min epsilon for the egreedy
    public double epsilonDecay; // speed of the epsilon decrease
    private double gamma;  //discount for the Q update
    private Random random;
    private int actionSize;
    private ReplayBuffer replayBuffer = new ReplayBuffer(100000);
    private int[] mask;
    private int chipsToRemove;
    private List<Integer> winningChips = new ArrayList<>();
    private int updateCount = 0;
    private final int TARGET_UPDATE_FREQUENCY = 250;

    /**
     * Constructor to initialize the bot with its color.
     *
     * @param color The color of the bot (e.g., "white" or "black").
     */
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
        // loadEpsilon();
        this.epsilon = 0.0001;
        this.targetNetwork = copyNetwork(this.qNetwork);

        this.mask = new int[actionSize];
        this.epsilonMin = 0.0001;
        this.epsilonDecay = 0.995;
        this.gamma = 0.9;
        this.random = new Random();

    }

    /**
     * Constructor for advanced initialization with custom parameters.
     *
     * @param color         The color of the bot.
     * @param qNetwork      The Q-network for prediction.
     * @param epsilon       Initial exploration rate.
     * @param epsilonMin    Minimum exploration rate.
     * @param epsilonDecay  Exploration decay rate.
     * @param gamma         Discount factor for future rewards.
     */
    public DQN_BOT(String color,NeuralNetwork qNetwork, double epsilon, double epsilonMin, double epsilonDecay, double gamma) {
        super(color);
        this.qNetwork = qNetwork;
        this.epsilon=0.0001;
        this.epsilonMin = 0.0001;
        this.epsilonDecay=epsilonDecay;
        this.gamma = gamma;
        this.random = new Random();
    }

    /**
     * Determines the next move for the bot based on the current game state.
     *
     * @param state The current game state.
     * @return The move chosen by the bot.
     */
    public Move makeMove(GameState state){
        Game_Board board = state.getGameBoard();
        GameEngine ge = new GameEngine();

        ActionIndexer actionMapping = new ActionIndexer();
        BoardTransformation boardTransform = new BoardTransformation(board);
        double[] stateVector = boardTransform.toVector(super.getColor());
        if (Arrays.stream(stateVector).anyMatch(v -> Double.isNaN(v) || Double.isInfinite(v))) {
            System.out.println("stateVector has NaN/Inf: " + Arrays.toString(stateVector));
        }

        // Predict Q-values for the current state
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

        // If fewer than 10 rings are placed, place a new ring randomly
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

        // Apply a mask to filter valid actions
        this.mask = fillMask(allValidMoves);

        qValues = filterQValues(qValues, this.mask);

       
        actionMapping.initializeActions(allValidMoves);
        int actionIndex = chooseAction(qValues, allValidMoves);
        // System.out.println("[LOG] actionIndex = " + actionIndex + 
        //                " / epsilon = " + epsilon +
        //                " / allValidMoves.size() = " + allValidMoves.size());

        // if (epsilon > epsilonMin) {
        //     epsilon *= epsilonDecay;
        // }

        Move chosenMove;
        try{
            chosenMove= actionMapping.getMove(actionIndex);

        }catch(Exception ex){
            System.out.println(ex);
            chosenMove=null;
        }


        if (chosenMove == null || allValidMoves.size()==0)  {
            // System.out.println("All Possible Moves: " + allValidMoves.size());

            // System.out.println("Action Index " + actionIndex + " does not map to a valid move!");
        }
        GameState nextState = moveState(state.clone(), chosenMove);

        double reward = Reward.calculateRewardWITHOUT(ge, state, chosenMove, nextState, super.getColor());
        BoardTransformation boardTransformNEW = new BoardTransformation(nextState.getGameBoard());
        chosenMove.setReward(reward);
        
        // Store the experience for training
        storeExperience(stateVector, actionIndex, reward, boardTransformNEW.toVector(super.getColor().toLowerCase().equals("white") ?"white":"black"), chosenMove, state, nextState);

        return chosenMove;
    }

    /**
     * Saves the current value of epsilon to a file.
     * The epsilon value is used for the ε-greedy policy.
     */
    public void saveEpsilon(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ken2\\src\\main\\java\\com\\ken2\\bots\\DQN_BOT_ML\\NeuralNetwork\\weights\\savedEpsilon.txt"))) {
            writer.write(String.valueOf(this.epsilon));
        }catch (IOException e) {
        e.printStackTrace();
    }

    }

    /**
     * Loads the epsilon value from a file.
     * If the file does not exist, epsilon defaults to 1.0.
     */
    public void loadEpsilon(){
        double loadedEps = 1.0; 
        try (BufferedReader reader = new BufferedReader(new FileReader("ken2\\src\\main\\java\\com\\ken2\\bots\\DQN_BOT_ML\\NeuralNetwork\\weights\\savedEpsilon.txt"))) {
            loadedEps = Double.parseDouble(reader.readLine());
        } catch (IOException ex) {
            System.out.println("No saved epsilon found, defaulting to 1.0");
        }
        this.epsilon = loadedEps;

    }

    /**
     * Stores an experience in the replay buffer.
     * The experience includes the current state, action, reward, and next state.
     *
     * @param state      The current state as a feature vector.
     * @param action     The action taken in the current state.
     * @param reward     The reward received for taking the action.
     * @param nextState  The resulting state after the action.
     * @param chosenMove The chosen move that led to the experience.
     * @param stateP     The full current game state.
     * @param nexState   The full next game state.
     */
    public void storeExperience(double[] state, int action, double reward, double[] nextState, Move chosenMove, GameState stateP, GameState nexState) {
        Experience experience = new Experience(state, action, reward, nextState, chosenMove, stateP, nexState);
        // replayBuffer.add(experience);
        double priority = calculateTDError(experience);
        // System.out.println("[DEBUG] storeExperience: actionIndex=" + action 
        // + ", chosenMove=" + chosenMove);
        // replayBuffer.add(experience, priority);
       
    }

    /**
     * Calculates the Temporal Difference (TD) error for a given experience.
     * The TD error is used to prioritize experiences in the replay buffer.
     *
     * @param exp The experience for which the TD error is calculated.
     * @return The TD error value.
     */
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
    
    /**
     * Filters Q-values based on the mask of valid actions.
     * Invalid actions are assigned a very low value.
     *
     * @param prevQvalues The original Q-values.
     * @param mask        The mask indicating valid actions.
     * @return The filtered Q-values.
     */
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

    /**
     * Creates a mask indicating which actions are valid.
     * The mask size matches the total number of possible actions.
     *
     * @param allMoves The list of all valid moves.
     * @return The mask array, where 1 indicates a valid action and 0 indicates invalid.
     */
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

    /**
     * Filters valid moves from the given vertex-move mapping.
     * A move is considered valid if it passes the game's validation rules.
     *
     * @param vertexMove A map of vertices and their associated moves.
     * @param state      The current game state.
     * @return A list of valid moves.
     */
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

    /**
     * Trains the Q-network using a mini-batch of experiences from the replay buffer.
     *
     * @param batchSize The number of experiences to sample from the replay buffer.
     */
    public void train(int batchSize){
        ArrayList<Experience> buffer = replayBuffer.getBuffer();

        if (buffer.size() < batchSize) {
            return; 
        }
    
        ArrayList<Experience> sample = replayBuffer.createSample(batchSize);
        double totalLoss = 0.0;
        for(Experience exp: sample){
            double[] state = exp.getState();
            int action = exp.getAction();
            double reward = exp.getReward();
            double[] nextState = exp.getNextState();
            GameState newState = exp.nextState;

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
            double oldValue = qValues[action];
 
   
            qValues[action] = targetQ;

            qNetwork.trainMiniBatch(new double[][] { state }, new double[][] { qValues });
           
            double mse = 0.5 * Math.pow((targetQ - oldValue), 2); 
            totalLoss += mse;

        }
        double avgLoss = totalLoss / sample.size();
        
        updateCount += 1;
        if (updateCount % TARGET_UPDATE_FREQUENCY == 0) {

            updateTargetNetwork();

        }
    }

    /**
     * Updates the target network by copying weights from the Q-network.
     */
    private void updateTargetNetwork() {
        copyWeights(qNetwork, targetNetwork);
    }

    /**
     * Creates a deep copy of the given neural network.
     * The new network will have the same structure, weights, and biases as the source network.
     *
     * @param source The neural network to copy.
     * @return A new neural network instance identical to the source.
     */
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

    /**
     * Returns the Q-network used by the bot.
     * The Q-network is responsible for predicting Q-values for states.
     *
     * @return The Q-network instance.
     */
    public NeuralNetwork getQNetwork(){
        return this.qNetwork;
    }
    /**
     * Copies weights and biases from one neural network to another.
     * Assumes that the source and target networks have the same structure.
     *
     * @param from The source network from which weights and biases are copied.
     * @param to   The target network to which weights and biases are copied.
     */
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
    
    /**
     * Generates a mask indicating valid actions based on the given state vector.
     * The mask is used to filter invalid actions during Q-value prediction.
     *
     * @param nextStateVector The state vector representing the next state.
     * @return An array where 1 indicates a valid action and 0 indicates an invalid action.
     */
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

    /**
     * Chooses an action based on Q-values and an ε-greedy strategy.
     *
     * @param qValues      The Q-values for the current state.
     * @param allPossible  The list of all possible moves.
     * @return The index of the chosen action.
     */
    public int chooseAction(double[] qValues, ArrayList<Move> allPossible) {
        int maxActions = Math.min(qValues.length, allPossible.size());
    
        // if (random.nextDouble() < epsilon) { 
        //     return random.nextInt(allPossible.size());
        // } else {
           
            return argMax(qValues, maxActions); 
        // }

    }


    /**
     * Finds the index of the action with the highest Q-value.
     *
     * @param qValues    The array of Q-values for the available actions.
     * @param maxActions The number of actions to consider (limits the search to the first maxActions).
     * @return The index of the action with the highest Q-value.
     */
    public int argMax(double[] qValues, int maxActions) {
        int bestAction = 0;
        double bestq = 0.0;
        for (int i = 1; i < maxActions; i++) {
            if (qValues[i] > qValues[bestAction]) {
                bestAction = i;
                bestq=qValues[i];
            }
        }

        return bestAction;
    }
    
    /**
     * Returns the name of the bot.
     *
     * @return The name of the bot as a string.
     */
    public String getName() {
        return "DQN BOT";
    }

    /**
     * Initializes the Q-network with the desired architecture.
     * The network is configured with a specific number of layers, neurons, and activation functions.
     */
    public void initializeNN(){
        this.qNetwork = new NeuralNetwork(0.001, new MSE());
        qNetwork.addLayer(new Layer(128, 86, new ReLu()));  // first hidden layer
        qNetwork.addLayer(new Layer(64, 128, new ReLu())); 
        qNetwork.addLayer(new Layer(this.actionSize, 64, new Linear())); 
    }


    /**
     * Validates whether a move is allowed based on the current game state and move details.
     *
     * @param state The current game state.
     * @param move  The move to validate.
     * @return True if the move is valid, false otherwise.
     */
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
    
        com.ken2_1.engine.Direction direction = move.getDirection();
    
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

    /**
     * Applies a move to the current game state and returns the resulting state.
     *
     * @param state The current game state.
     * @param move  The move to apply.
     * @return The new game state after applying the move.
     */
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

    /**
     * Handles the scenario where a winning condition is met.
     *
     * @param tempEngine   The game engine instance.
     * @param thisBot      The bot making the move.
     * @param opponentBot  The opponent bot.
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
     * Removes a chip from the game board if it is part of a winning combination.
     * Updates the game state accordingly and handles chip removal limits.
     *
     * @param vertex      The vertex index of the chip to be removed.
     * @param gameEngine  The game engine managing the current game state.
     * @param activeBot   The bot currently taking the action.
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
     * Switches the turn between players by toggling the current player's color.
     *
     * @param state The current game state.
     */
    private void switchTurn(GameState state) {
        state.isWhiteTurn = !state.isWhiteTurn;
    }
    
}