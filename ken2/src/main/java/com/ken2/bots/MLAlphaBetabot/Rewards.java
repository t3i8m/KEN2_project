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

}
