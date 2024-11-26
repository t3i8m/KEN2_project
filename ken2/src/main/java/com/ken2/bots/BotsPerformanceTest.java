package com.ken2.bots;

import java.io.FileWriter;
import java.io.IOException;

import java.util.Random;
import com.ken2.engine.GameState;
import com.ken2.utils.BotFactory;
import com.ken2.engine.GameEngine;


public class BotsPerformanceTest {

    private static GameEngine gameEngine;
    private static int trials = 200;

    public static void main(String[] args){
        gameEngine = new GameEngine();
        Random random = new Random();

        String csvFile = "bot_results.csv";
        String[] headers = {"Bot Name", "Move Count", "Execution Time (ns)"};

        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append(String.join(",", headers)).append("\n");
            
            GameState[] gameStates= new GameState[trials];
            int[] moveCounts = new int[trials];

            for(int i = 0 ; i < trials ; i++){
                int m = 20 + random.nextInt(20);
                gameStates[i] = makeRandomGameState(m);
                moveCounts[i] = m;
            }

            //RANDOM BOT

            for (int i = 0 ; i < trials; i++) {
                GameState gameState = gameStates[i];
                int moveCount = moveCounts[i];

                String currentPlayer = (gameState.isWhiteTurn) ? "White" : "Black";
                BotFactory botFactory = new BotFactory();
                Bot testBot = botFactory.getBot("rulebased bot", currentPlayer); 
                
                long startTime = System.nanoTime();
                
                randomBot(testBot, gameState);

                long executionTime = System.nanoTime() - startTime;


                // results for random bot
                writer.append("Random Bot")
                        .append(",").append(Integer.toString(moveCount))
                        .append(",").append(String.valueOf(executionTime))
                        .append("\n");
            }

            // ALPHA-BETA BOT
            for (int i = 0 ; i < trials; i++) {
                GameState gameState = gameStates[i];
                int moveCount = moveCounts[i];

                String currentPlayer = (gameState.isWhiteTurn) ? "White" : "Black";
                BotFactory botFactory = new BotFactory();
                Bot testBot = botFactory.getBot("rulebased bot", currentPlayer); 

                long startTime = System.nanoTime();

                alphaBetaBot(testBot, gameState);
                long executionTime = System.nanoTime() - startTime;

                // results for Alpha-Beta bot
                writer.append("Alpha-Beta Bot")
                        .append(",").append(Integer.toString(moveCount))
                        .append(",").append(String.valueOf(executionTime)) // example outcome
                        .append("\n");
            }

            System.out.println("Results saved to " + csvFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Simulate the Random Bot's execution
    private  static void randomBot(Bot testBot, GameState gameState) {
    
        testBot.makeMove(gameState);
        gameEngine.currentState.selectedChipVertex = -1;
    }

    // Simulate the Alpha-Beta Bot's execution
    private static void alphaBetaBot(Bot testBot, GameState gameState) {
        testBot.makeMove(gameState);
    }

    private static GameState makeRandomGameState(int moveCount){

        gameEngine.resetGame();
        GameState gameState = gameEngine.getGameState();

        BotFactory botFactory = new BotFactory();

        Bot whiteBot = botFactory.getBot("rulebased bot", "white");
        Bot blackbot = botFactory.getBot("rulebased bot", "black");
        
        Bot currenBot;
        if (gameState.isWhiteTurn) {
            currenBot = whiteBot;
        } else {
            currenBot = blackbot;
        }

        for(int i = 0; i < moveCount ; i++){
            currenBot.makeMove(gameState);
            gameEngine.currentState.switchPlayer();
            gameEngine.currentState.selectedChipVertex = -1;
            

            if (gameState.isWhiteTurn) {
                currenBot = whiteBot;
            } else {
                currenBot = blackbot;
            }
        }

        gameState = gameEngine.getGameState();


        return gameState;
    }



}