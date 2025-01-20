//package com.ken2.bots.Tests;
//
//import com.ken2.bots.AlphaBetaBot.AlphaBetaBot;
//import com.ken2.bots.Bot;
//import com.ken2.bots.RuleBased.RuleBasedBot;
//
//import java.util.Random;
//
//public class BotComparisonTest {
//    public static void main(String[] args) {
//        int totalGames = 100;
//        int alphaBetaWins = 0;
//        int randomWins = 0;
//        long totalAlphaBetaTime = 0;
//        long totalRandomTime = 0;
//        int totalMoves = 0;
//
//        for (int i = 0; i < totalGames; i++) {
//            Game game = new Game();
//            Bot alphaBetaBot = new AlphaBetaBot(4);  // Глубина 4
//            Bot randomBot = new RuleBasedBot();
//
//            long startTimeAB = System.currentTimeMillis();
//            int result = playGame(alphaBetaBot, randomBot, game);
//            long endTimeAB = System.currentTimeMillis();
//            totalAlphaBetaTime += (endTimeAB - startTimeAB);
//
//            if (result == 1) {
//                alphaBetaWins++;
//            } else {
//                randomWins++;
//            }
//
//            totalMoves += game.getMoveCount();
//        }
//
//        // Вывод результатов тестов
//        System.out.println("Alpha-Beta Wins: " + alphaBetaWins);
//        System.out.println("Random Bot Wins: " + randomWins);
//        System.out.println("Alpha-Beta Avg Move Time: " + (totalAlphaBetaTime / totalGames) + " ms");
//        System.out.println("Random Bot Avg Move Time: " + (totalRandomTime / totalGames) + " ms");
//        System.out.println("Average Moves per Game: " + (totalMoves / totalGames));
//    }
//
//    // Метод для симуляции одной игры
//    public static int playGame(Bot alphaBetaBot, Bot randomBot, Game game) {
//        while (!game.isFinished()) {
//            if (game.currentPlayer() == 1) {
//                alphaBetaBot.makeMove(game);
//            } else {
//                randomBot.makeMove(game);
//            }
//        }
//        return game.getWinner();  // 1 - победа АБ, 2 - победа рандома
//    }
//}
