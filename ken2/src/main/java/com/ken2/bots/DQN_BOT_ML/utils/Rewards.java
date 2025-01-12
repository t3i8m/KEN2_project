package com.ken2.bots.DQN_BOT_ML.utils;

public enum Rewards {
    WIN(100),
    CHIPS_IN_A_ROW(40),
    THREE_IN_A_ROW(70),
    FOUR_IN_A_ROW(90),
    FIVE_CHIPS_IN_A_ROW(1000),
    YOUR_RING_REMOVAL(100),
    OPPONENT_RING_REMOVAL(-10000),
    SUCCESSFUL_MOVE(1),
    //COMPLEX_MOVE (5),
    DOUBLE_ROW_CREATION (10000),
    FLIP_MARKERS(1),
    INVALID_MOVE(-100),
    SELF_BLOCK(0),
    DRAW(-100),
    LOSE(-100000),
    TWO_IN_A_ROW(20),
    FIVE_IN_A_ROW(1000),

    OPPONENT_ROW_CREATION_TWO(-40),

    OPPONENT_ROW_CREATION_THREE(-60),
    OPPONENT_ROW_CREATION_FOUR(-70),
    OPPONENT_ROW_CREATION_FIVE(-90),
    FLIP_MARKERS_OPPONENT(-1);


    private int value;

   Rewards(int bonus){
    this.value = bonus;
   }

   public int getValue() {
    return this.value;
}
    public void setValue(int newValue) {
        this.value = newValue;
    }

    public static int calculateReward(Rewards rewardType, int additionalFactor) {
        return rewardType.getValue() + additionalFactor;
    }

    public static void logReward(Rewards rewardType, String context) {
        System.out.println("Reward applied: " + rewardType.name() + " | Value: " + rewardType.getValue() + " | Context: " + context);
    }

    public static double normalizeReward(double reward) {
        // final int MIN_REWARD = -100;
        // final int MAX_REWARD = 50;
        // return (double)(reward - MIN_REWARD) / (MAX_REWARD - MIN_REWARD) * 2 - 1;
        return reward;
    }



}
