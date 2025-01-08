package com.ken2.bots.MLbot.neuralNetwork;

public enum Rewards {
    WIN(10),
    CHIPS_IN_A_ROW(4),
    FIVE_CHIPS_IN_A_ROW(6),
    YOUR_RING_REMOVAL(10),
    OPPONENT_RING_REMOVAL(-10),
    SUCCESSSFUL_MOVE(1),
    INVALID_MOVE(-100),
    DRAW(5),
    LOSE(-10);


    private final int value;

   Rewards(int bonus){
    this.value = bonus;
   }

   public int getValue() {
    return this.value;
}
}
