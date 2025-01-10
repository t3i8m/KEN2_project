package com.ken2.bots.DQN_BOT_ML.utils;

public enum Rewards {
    WIN(50),
    CHIPS_IN_A_ROW(4),
    THREE_IN_A_ROW(2),
    FOUR_IN_A_ROW(3),
    FIVE_CHIPS_IN_A_ROW(6),
    YOUR_RING_REMOVAL(10),
    OPPONENT_RING_REMOVAL(-10),
    SUCCESSFUL_MOVE(1),
    //COMPLEX_MOVE (5),///пересечение коинов
    DOUBLE_ROW_CREATION (15),
    FLIP_MARKERS(1),
    INVALID_MOVE(-100),
    SELF_BLOCK(-10),
    DRAW(5),
    LOSE(-10),
    OPPONENT_ROW_CREATION(-20);


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
        final int MIN_REWARD = -100;
        final int MAX_REWARD = 50;
        return (double)(reward - MIN_REWARD) / (MAX_REWARD - MIN_REWARD) * 2 - 1;
    }



}
