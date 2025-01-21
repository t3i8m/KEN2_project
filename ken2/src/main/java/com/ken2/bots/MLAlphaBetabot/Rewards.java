package com.ken2.bots.MLAlphaBetabot;

import com.ken2.engine.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Rewards {

    private final HashMap<Direction,Float>  rewrds;



    // THE ORDERED LIST IN TERMS OF WEIGHTS !!
    private ArrayList<Direction> directionslist;

    // Give a predefined set of rewards for the diff directions
    // The probabilities of the moves

    public Rewards() {
        rewrds = new HashMap<>();
        rewrds.put(Direction.UP,0.16f);
        rewrds.put(Direction.DOWN,0.16f);
        rewrds.put(Direction.LEFT_UP,0.16f);
        rewrds.put(Direction.LEFT_DOWN,0.16f);
        rewrds.put(Direction.RIGHT_UP,0.16f);
        rewrds.put(Direction.RIGHT_DOWN,0.16f);

        directionslist = new ArrayList<>();
        directionslist.add(Direction.UP);
        directionslist.add(Direction.DOWN);
        directionslist.add(Direction.LEFT_UP);
        directionslist.add(Direction.LEFT_DOWN);
        directionslist.add(Direction.RIGHT_UP);
        directionslist.add(Direction.RIGHT_DOWN);
    }

    public void increment(Direction direction, float value) {
        rewrds.put(direction,rewrds.get(direction)+value);
    }

    public void decrement(Direction direction, float value) {
        rewrds.put(direction,rewrds.get(direction)-value);
    }

    // Use this method to order the values of the directions
    public void orderDirections() {
        ArrayList<Direction> ordertoreturn = new ArrayList<>();

        while (ordertoreturn.size() < rewrds.size()) {
            Direction maxDirection = null;
            float maxValue = Float.NEGATIVE_INFINITY;

            for (Map.Entry<Direction, Float> entry : rewrds.entrySet()) {
                if (!ordertoreturn.contains(entry.getKey()) && entry.getValue() > maxValue) {
                    maxDirection = entry.getKey();
                    maxValue = entry.getValue();
                }
            }
            if (maxDirection != null) {
                ordertoreturn.add(maxDirection);
            }
        }
        directionslist = ordertoreturn; // Assign sorted list
    }

    public ArrayList<Direction> getDirectionslist() {
        orderDirections();
        return directionslist;
    }

    public void update(Direction direction, float value) {

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
