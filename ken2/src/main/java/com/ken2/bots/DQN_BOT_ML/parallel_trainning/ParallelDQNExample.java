package com.ken2.bots.DQN_BOT_ML.parallel_trainning;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ken2.bots.DQN_BOT_ML.botComponents.DQN_BOT;
import com.ken2.bots.DQN_BOT_ML.utils.ReplayBuffer;

public class ParallelDQNExample {
    private static final int NUM_THREADS = 5;      
    private static final int EPISODES_PER_THREAD = 30;  
    private static final int TRAIN_BATCH_SIZE = 32;     

    private static ReplayBuffer replayBuffer = new ReplayBuffer(10000);

    private static DQN_BOT dqnBot = new DQN_BOT("white");

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(new GameWorker(EPISODES_PER_THREAD, replayBuffer, dqnBot));

        }

        executor.shutdown();


        while (!executor.isTerminated()) {
            try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }

            // double before = dqnBot.qNetwork.sumWeights();
            dqnBot.train(TRAIN_BATCH_SIZE);
            // double after = dqnBot.qNetwork.sumWeights();

            // System.out.println("Delta Weights = " + (after - before));
        }

        dqnBot.train(TRAIN_BATCH_SIZE);


        dqnBot.getQNetwork().saveWeights();
        dqnBot.saveEpsilon();
        // for (int i = 0; i < dqnBot.qNetwork.getBatchLossHistory().size(); i++) {
        //     System.out.println(i + "," +  dqnBot.qNetwork.getBatchLossHistory().get(i));
        // }
        saveBatchLossHistory(dqnBot.qNetwork.getBatchLossHistory());
        System.out.println("Done parallel training!");
    }

    private static void saveBatchLossHistory(List<Double> batchLossHistory) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ken2\\src\\main\\java\\com\\ken2\\bots\\DQN_BOT_ML\\parallel_trainning\\batch_loss.csv"))) {
            writer.write("Batch,Loss\n");  
            for (int i = 0; i < batchLossHistory.size(); i++) {
                writer.write(i + "," + batchLossHistory.get(i) + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing batch loss history to file: " + e.getMessage());
        }
    }
}
