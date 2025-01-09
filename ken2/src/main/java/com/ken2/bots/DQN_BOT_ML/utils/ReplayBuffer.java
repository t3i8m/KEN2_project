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

    public ArrayList<Experience> sample(int sampleSize){
        Random rand = new Random();
        ArrayList<Experience> container = new ArrayList<>();
        for(int i =0; i<sampleSize;i++){
            Experience exp = buffer.get(rand.nextInt(buffer.size()));
            container.add(exp);
        }
        return container;
    }
    
}
