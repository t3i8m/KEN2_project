package com.ken2.Game_Components.Board;

public interface PlayObj {
    public String getColour();
    public void setColour(String color);
    public PlayObj clone();
}
