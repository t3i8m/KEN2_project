package com.ken2.bots.DQN_BOT_ML.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ReplayBuffer {
    private ArrayList<Experience> buffer = new ArrayList<>();
    private int capacity;
    private double topRatio;
    private Random random;
    private ArrayList<Double> priorities = new ArrayList<>();

    public ReplayBuffer(int capacity){
        this.capacity=capacity;
        this.topRatio = 0.5;
        this.buffer = new ArrayList<>(capacity);
        this.priorities = new ArrayList<>(capacity);
        this.random = new Random();
    }

    public void add(Experience newExperience, double priority){
        if(buffer.size()>=capacity){
            buffer.remove(0);
            priorities.remove(0);
        }
        buffer.add(newExperience);
        // priorities.add(Math.abs(newExperience.getReward()));
        // normalizePriorities();
        priorities.add(newExperience.getReward());
    }

    // public ArrayList<Experience> createSample(int sampleSize){
    //     Random random = new Random();

    //     // double sumPriorities = priorities.stream().mapToDouble(p -> Math.pow(p, 1)).sum();
    //     // ArrayList<Experience> container = new ArrayList<>(sampleSize);
    
    //     // ArrayList<Integer> indices = new ArrayList<>();
    //     // for (int i = 0; i < buffer.size(); i++) {
    //     //     indices.add(i);
    //     // }
    
    //     // java.util.Collections.shuffle(indices, rand);
    
    //     // int actualSize = Math.min(sampleSize, buffer.size());
    //     // for (int i = 0; i < actualSize; i++) {
    //     //     container.add(buffer.get(indices.get(i)));
    //     // }
    
    //     // return container;

    //     double sumPriorities = priorities.stream().mapToDouble(p -> Math.pow(p, 1)).sum();
    //     ArrayList<Experience> sample = new ArrayList<>();
    
    //     for (int i = 0; i < sampleSize; i++) {
    //         double rand = random.nextDouble() * sumPriorities;
    //         double cumulative = 0.0;
            
    //         for (int j = 0; j < priorities.size(); j++) {
    //             cumulative += priorities.get(j);
    //             if (rand <= cumulative) {

    //                 sample.add(buffer.get(j));
    //                 break;
    //             }
    //         }
    //     }
    //     return sample;
    
    // }

        public ArrayList<Experience> createSample(int sampleSize){
        if (sampleSize <= 0) {
            throw new IllegalArgumentException("Размер выборки должен быть положительным.");
        }

        sampleSize = Math.min(sampleSize, buffer.size());
        ArrayList<Experience> sample = new ArrayList<>(sampleSize);

        int numTopSamples = (int) Math.round(sampleSize * topRatio);
        numTopSamples = Math.min(numTopSamples, buffer.size()); 
        int numRandomSamples = sampleSize - numTopSamples;

        List<Integer> topIndices = getTopIndices(numTopSamples);

        for (int index : topIndices) {
            sample.add(buffer.get(index));
        }

        List<Integer> randomIndices = getRandomIndices(numRandomSamples, topIndices);
        for (int index : randomIndices) {
            sample.add(buffer.get(index));
        }

        Collections.shuffle(sample, random);

        return sample;
    }


    private List<Integer> getTopIndices(int topN){
        List<Integer> indices = new ArrayList<>();
        for(int i = 0; i < buffer.size(); i++){
            indices.add(i);
        }

        indices.sort((i1, i2) -> Double.compare(priorities.get(i2), priorities.get(i1)));

        return indices.subList(0, topN);
    }


    private List<Integer> getRandomIndices(int numSamples, List<Integer> exclude){
        List<Integer> availableIndices = new ArrayList<>();
        for(int i = 0; i < buffer.size(); i++){
            if(!exclude.contains(i)){
                availableIndices.add(i);
            }
        }

        Collections.shuffle(availableIndices, random);

        return availableIndices.subList(0, Math.min(numSamples, availableIndices.size()));
    }

    // public void normalizePriorities() {
    //     double sumRewards = buffer.stream()
    //                               .mapToDouble(exp -> Math.abs(exp.getReward()))
    //                               .sum();
    
    //     if (sumRewards == 0) {
    //         sumRewards = 1e-6;
    //     }
    
    //     for (int i = 0; i < buffer.size(); i++) {
    //         Experience exp = buffer.get(i);
    //         double normalizedPriority = Math.abs(exp.getReward()) / sumRewards;
    //         priorities.set(i, normalizedPriority);
    //     }
    // }

    // public void updatePriorities(ArrayList<Integer> indices, ArrayList<Double> newPriorities) {
    //     for (int i = 0; i < indices.size(); i++) {
    //         int index = indices.get(i);
    //         if (index >= 0 && index < priorities.size()) {
    //             priorities.set(index, Math.abs(newPriorities.get(i)));
    //         }
    //     }
    //     normalizePriorities();
    // }
    
    
    
    

    public ArrayList<Experience> getBuffer(){
        return this.buffer;
    }
    
}
