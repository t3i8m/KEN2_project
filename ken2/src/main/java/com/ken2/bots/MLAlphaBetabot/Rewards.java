package com.ken2.bots.MLAlphaBetabot;

import com.ken2.engine.Direction;

import java.util.HashMap;

public class Rewards {

    private final HashMap<Direction,Float>
            rewrds;

    // Give a predefined set of rewards for the diff directions

    public Rewards() {
        rewrds = new HashMap<>();
        rewrds.put(Direction.UP,0.25f);
        rewrds.put(Direction.DOWN,0.25f);
        rewrds.put(Direction.LEFT_UP,0.25f);
        rewrds.put(Direction.LEFT_DOWN,0.25f);
        rewrds.put(Direction.RIGHT_UP,0.25f);
        rewrds.put(Direction.RIGHT_DOWN,0.25f);
    }

    public void increment(Direction direction, float value) {
        rewrds.put(direction,rewrds.get(direction)+value);
    }

    public void decrement(Direction direction, float value) {
        rewrds.put(direction,rewrds.get(direction)-value);
    }



    // Make a evaluation method

    // Then make a method that evalutes all the steps in a particular direction and average that and alter the
    // rewards in that direction.

    // You can take all the methods in one array from the allpossiblemoves variable and get the moves in one
    // direction that u want it too work in

    // Then during this evaluation of methods u can store the top 3 moves in that direction

    // After going thru all the moves and altering the weights choose the direction with the most weights and
    // choose a random method from the top 3 in that direction


}
