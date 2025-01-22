package com.ken2_1.bots.DQN_BOT_ML.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * ReplayBuffer is a utility class for storing and sampling experiences during reinforcement learning.
 * It supports prioritized replay by associating a priority with each experience.
 */
public class ReplayBuffer {
    private ArrayList<Experience> buffer = new ArrayList<>();
    private int capacity;
    private double topRatio;
    private Random random;
    private ArrayList<Double> priorities = new ArrayList<>();

    /**
     * Constructs a ReplayBuffer with a specified capacity.
     *
     * @param capacity The maximum number of experiences the buffer can hold.
     */
    public ReplayBuffer(int capacity){
        this.capacity=capacity;
        this.topRatio = 0.5;
        this.buffer = new ArrayList<>(capacity);
        this.priorities = new ArrayList<>(capacity);
        this.random = new Random();
    }

    /**
     * Adds a new experience to the buffer. If the buffer is full, removes the oldest experience.
     *
     * @param newExperience The new experience to add.
     * @param priority       The priority associated with the new experience.
     */
    public synchronized  void add(Experience newExperience, double priority){
        if(buffer.size()>=capacity){
            buffer.remove(0);
            priorities.remove(0);
        }
        buffer.add(newExperience);

        priorities.add(Math.abs(priority) + 1e-5);
    }

    /**
     * Creates a sample of experiences from the buffer.
     * 
     * @param sampleSize The size of the sample.
     * @return A list of sampled experiences.
     */
    public ArrayList<Experience> createSample(int sampleSize){
        Random random = new Random();

        double sumPriorities = priorities.stream().mapToDouble(p -> Math.pow(p, 1)).sum();
        ArrayList<Experience> container = new ArrayList<>(sampleSize);
    
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < buffer.size(); i++) {
            indices.add(i);
        }
    
        java.util.Collections.shuffle(indices, random);
    
        int actualSize = Math.min(sampleSize, buffer.size());
        for (int i = 0; i < actualSize; i++) {
            container.add(buffer.get(indices.get(i)));
        }
    
        return container;

    
    }

    
    

    /**
     * Retrieves the top-priority indices from the buffer.
     *
     * @param topN The number of top-priority experiences to retrieve.
     * @return A list of the highest-priority experiences.
     */
    private synchronized  ArrayList<Experience> getTopIndices(int topN){
        List<Integer> indices = new ArrayList<>();
        for(int i = 0; i < buffer.size(); i++){
            indices.add(i);
        }
        int realSize = Math.min(buffer.size(), priorities.size());
        if (realSize < topN) {
            topN = realSize;
        }
        indices = indices.subList(0, realSize);
        indices.sort((i1, i2) -> Double.compare(priorities.get(i2), priorities.get(i1))); 

        ArrayList<Experience> result = new ArrayList<>(topN);
        for (int i = 0; i < topN; i++){
            int idx = indices.get(i);
            result.add(buffer.get(idx));
        }
        return result;
    }

    /**
     * Retrieves random indices from the buffer, excluding specific indices.
     *
     * @param numSamples The number of random indices to retrieve.
     * @param exclude    A list of indices to exclude from sampling.
     * @return A list of random indices.
     */
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

   
    /**
     * Returns the current experience buffer.
     *
     * @return The list of stored experiences.
     */
    public ArrayList<Experience> getBuffer(){
        return this.buffer;
    }
    
}
