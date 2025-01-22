package com.ken2_1.bots.DQN_BOT_ML.utils;

/**
 * Enum representing rewards and penalties for various game actions.
 */
public enum Rewards {
  
    WIN(10),               
    LOSE(-10),             
    DRAW(0),                

    YOUR_RING_REMOVAL(+7),          
    OPPONENT_RING_REMOVAL(-7),      

    SUCCESSFUL_MOVE(+1),     
    INVALID_MOVE(-2),        
    SELF_BLOCK(-1),          

    FLIP_MARKERS(+1),       
    FLIP_MARKERS_OPPONENT(-1), 

    TWO_IN_A_ROW(+1),
    THREE_IN_A_ROW(+2),
    FOUR_IN_A_ROW(+3),
    FIVE_IN_A_ROW(+5),              
    FIVE_CHIPS_IN_A_ROW(+5),        

    DOUBLE_ROW_CREATION(+6),        

    OPPONENT_ROW_CREATION_TWO(-1),
    OPPONENT_ROW_CREATION_THREE(-2),
    OPPONENT_ROW_CREATION_FOUR(-4),
    OPPONENT_ROW_CREATION_FIVE(-5),

    CHIPS_IN_A_ROW(0); 

    private int value;


    /**
     * Constructor to initialize the reward value.
     *
     * @param value The numerical value of the reward/penalty.
     */
   Rewards(int bonus){
    this.value = bonus;
   }

    /**
     * Gets the reward value.
     *
     * @return The value of the reward/penalty.
     */
   public int getValue() {
        return this.value;
    }

    /**
     * Sets a new value for the reward.
     *
     * @param newValue The updated reward/penalty value.
     */
    public void setValue(int newValue) {
        this.value = newValue;
    }

    /**
     * Calculates a modified reward based on an additional factor.
     *
     * @param rewardType       The type of reward.
     * @param additionalFactor A factor to adjust the reward.
     * @return The adjusted reward value.
     */
    public static int calculateReward(Rewards rewardType, int additionalFactor) {
        return rewardType.getValue() + additionalFactor;
    }

    /**
     * Logs the applied reward for debugging or analysis purposes.
     *
     * @param rewardType The type of reward applied.
     * @param context    Additional context about the reward application.
     */
    public static void logReward(Rewards rewardType, String context) {
        System.out.println("Reward applied: " + rewardType.name() + " | Value: " + rewardType.getValue() + " | Context: " + context);
    }

    /**
     * Normalizes a reward value to a range of [-1, 1].
     *
     * @param reward The original reward value.
     * @return The normalized reward value.
     */
    public static double normalizeReward(double reward) {
        double scaled = Math.max(-10, Math.min(10, reward));
        return scaled / 10.0;
    }



}
