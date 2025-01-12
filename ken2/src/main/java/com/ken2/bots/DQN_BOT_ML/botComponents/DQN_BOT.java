package com.ken2.bots.DQN_BOT_ML.botComponents;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.PlayObj;
import com.ken2.Game_Components.Board.Ring;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.bots.BotAbstract;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.NeuralNetwork;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.LoosFunction.MSE;
import com.ken2.bots.DQN_BOT_ML.utils.BoardTransformation;
import com.ken2.bots.DQN_BOT_ML.utils.ReplayBuffer;
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

            qValues[action] = reward+(gamma*maxNextQ);
            qNetwork.trainMiniBatch(new double[][]{state}, new double[][]{qValues});

        }
    }

    public int chooseAction(double[] qValues, ArrayList<Move> allPossible){
        if(random.nextDouble() < epsilon){ // random action if less than epsilon
            return random.nextInt(allPossible.size()-1);
        }
        else{
            return argMax(qValues, allPossible);
        }
    }
    //finds an index with the highest q-value
    public int argMax(double[] qValues,ArrayList<Move>  allPossible){
        int bestAction = 0;
        for (int i =0; i <qValues.length;i++){
            if(qValues[i]>qValues[bestAction]){
                bestAction=i;
            }

            if(i>=allPossible.size()-1){
                return bestAction;

            }
        }
        return bestAction;
    }

    public String getName() {
        return "DQN BOT";
    }

    public void initializeNN(){
        this.qNetwork = new NeuralNetwork(0.01, new MSE());
        qNetwork.addLayer(new Layer(128, 86, new ReLu()));  // first hidden layer
        qNetwork.addLayer(new Layer(64, 128, new ReLu())); 
        qNetwork.addLayer(new Layer(actionSize, 64, new Linear())); 
    }

    // public void updateOutputLayer(int newSize) {
    //     qNetwork.updateOutputLayer(new Layer(newSize, qNetwork.getPreviousLayerSize(), new Linear()));
    // }
    

    // public ArrayList<Move> allPossibleMoves(GameState state) {
    //     ArrayList<Move> possibleMoves = new ArrayList<>();
    //     Game_Board board = state.getGameBoard();
    //     GameSimulation simulation = new GameSimulation();

    //     if (state.ringsPlaced < 10) {
    //         ArrayList<Vertex> freePositions = board.getAllFreeVertexes();
    //         for (Vertex vertex : freePositions) {
    //             if (vertex != null) {
    //                 Move move = new Move(vertex.getXposition(), vertex.getYposition(), null);
    //                 possibleMoves.add(move);
    //             }
    //         }
    //         return possibleMoves;
    //     }

    //     ArrayList<Vertex> allRingPositions = state.getAllVertexOfColor(state.currentPlayerColor());
    //     for (Vertex ringPosition : allRingPositions) {
    //         if (ringPosition != null && ringPosition.hasRing()) {
    //             HashMap<Vertex, ArrayList<Move>> movesFromPosition = simulation.getAllMovesFromAllPositions(
    //                 allRingPositions, state.getGameBoard()
    //             );

    //             for (ArrayList<Move> moves : movesFromPosition.values()) {
    //                 possibleMoves.addAll(moves);
    //             }
    //         }
    //     }

    // return possibleMoves;
    // }

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
}