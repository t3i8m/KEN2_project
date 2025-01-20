package com.ken2.bots.DQN_BOT_ML.utils;

import java.util.ArrayList;
import java.util.Random;

public class ReplayBuffer {
    private ArrayList<Experience> buffer = new ArrayList<>();
    private int capacity;

    public ReplayBuffer(int capacity){
        this.capacity=capacity;
    }

    public void add(Experience newExperience){
        if(buffer.size()>=capacity){
            buffer.remove(0);
        }
        buffer.add(newExperience);
    }

    public ArrayList<Experience> createSample(int sampleSize){
        Random rand = new Random();
        ArrayList<Experience> container = new ArrayList<>(sampleSize);
    
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < buffer.size(); i++) {
            indices.add(i);
        }
    
        java.util.Collections.shuffle(indices, rand);
    
        int actualSize = Math.min(sampleSize, buffer.size());
        for (int i = 0; i < actualSize; i++) {
            container.add(buffer.get(indices.get(i)));
        }
    
        return container;
    }
    

    public ArrayList<Experience> getBuffer(){
        return this.buffer;
    }
    
}
