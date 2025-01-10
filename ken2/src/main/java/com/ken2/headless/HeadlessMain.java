package com.ken2.headless;

import com.ken2.Game_Components.Board.Coin;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.bots.Bot;
import com.ken2.bots.DQN_BOT_ML.utils.Reward;
import com.ken2.bots.DQN_BOT_ML.utils.Rewards;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;
import com.ken2.utils.BotFactory;

public class HeadlessMain {
    public static void main(String[] args){
        BotFactory factory = new BotFactory();
        Bot whiteBot = factory.getBot("rulebased bot", "White");
        Bot blackBot = factory.getBot("rulebased bot", "Black");

        Headless headless = new Headless(1, whiteBot, blackBot);

        GameEngine engine = new GameEngine();
        engine.resetGame();
        GameState previous = engine.getGameState();
        GameState newState = null;
        int moveCount = 1;
        for (int i = 1; i <=70;i++){
            if(engine.currentState.chipsRemaining<=0){
                System.out.print("Draw");
                break;
            }
            Bot current = engine.currentState.isWhiteTurn ? whiteBot:blackBot;
            Move move = current.makeMove(engine.currentState);
            if(move ==null){
                System.out.print("INVALID");
                break;
            }

            // Simulate game state transition
            newState = engine.currentState.clone();
            // Place a coin on the board for the move
            int vertexNumber = newState.getGameBoard().getVertexNumberFromPosition(move.getXposition(), move.getYposition());
            Vertex targetVertex = newState.getGameBoard().getVertex(vertexNumber);

            if (targetVertex == null || targetVertex.hasCoin()) {
                System.out.println("Invalid move: Target vertex is null or already occupied.");
                break;
            }

            Coin newCoin = new Coin(current.getColor());
            targetVertex.setPlayObject(newCoin);

            engine.currentState = newState;
            System.out.println(current.getColor());
            // Calculate the reward for the move
            double reward = Reward.calculateReward(engine, previous, move, newState);
            System.out.println("Move " + moveCount + ": Reward = " + reward);

            // Update previous state for next iteration
            previous = newState.clone();

            // Check for game end conditions
            if (engine.win(engine.currentState.getVertexesOfFlippedCoins())) {
                System.out.println("Player " + current.getColor() + " wins!");
                break;
            }
            newState.isWhiteTurn = !newState.isWhiteTurn;
            moveCount++;
        }
        // headless.runGames(); // without csv export
        headless.exportGamesToCsv();// with csv export
    }
}
