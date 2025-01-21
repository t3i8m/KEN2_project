package com.ken2.bots.DQN_BOT_ML.utils;

public enum Rewards {
    // WIN(10000),
    // CHIPS_IN_A_ROW(0),
    // THREE_IN_A_ROW(30),
    // FOUR_IN_A_ROW(90),
    // FIVE_CHIPS_IN_A_ROW(1000),
    // YOUR_RING_REMOVAL(10000),
    // OPPONENT_RING_REMOVAL(-10000),
    // SUCCESSFUL_MOVE(1),
    // //COMPLEX_MOVE (5),
    // DOUBLE_ROW_CREATION (1000),
    // FLIP_MARKERS(1),
    // INVALID_MOVE(-100),
    // SELF_BLOCK(0),
    // DRAW(-100),
    // LOSE(-1000),
    // TWO_IN_A_ROW(20),
    // FIVE_IN_A_ROW(1000),

    // OPPONENT_ROW_CREATION_TWO(-5),

    // OPPONENT_ROW_CREATION_THREE(-60),
    // OPPONENT_ROW_CREATION_FOUR(-70),
    // OPPONENT_ROW_CREATION_FIVE(-90),
    // FLIP_MARKERS_OPPONENT(-1);
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

    // WIN( +100 ),      
    // LOSE( -100 ),     
    // DRAW( -10 ),     

    // OPPONENT_RING_REMOVAL( +40 ), 
    // YOUR_RING_REMOVAL( -40 ),     

    // FLIP_MARKERS( +3 ),          
    // FLIP_MARKERS_OPPONENT( -3 ),  

    // TWO_IN_A_ROW( +4 ),  
    // THREE_IN_A_ROW( +8 ),
    // FOUR_IN_A_ROW( +15 ),
    // FIVE_IN_A_ROW( +30 ),        
    // FIVE_CHIPS_IN_A_ROW( +30 ),  

    // SELF_BLOCK( -5 ),            
    // INVALID_MOVE( -20 ),          
    // SUCCESSFUL_MOVE( +5 ),      
    
    // OPPONENT_ROW_CREATION_TWO( -4 ),
    // OPPONENT_ROW_CREATION_THREE( -8 ),
    // OPPONENT_ROW_CREATION_FOUR( -15 );


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
        // return reward;
        double scaled = Math.max(-10, Math.min(10, reward));
        return scaled / 10.0;
    }



}
