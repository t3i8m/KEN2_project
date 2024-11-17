package com.ken2.utils;

import com.ken2.bots.Bot;

public class Player {
    private String color;
    private boolean isBot;
    private Bot bot;
    private String name;

    // contructor for human
    public Player(String color,String name) {
        this.color = color;
        this.isBot = false;
        this.bot = null;
        this.name = name;
    }

    // constructor for a bot
    public Player(String color, Bot bot, String name) {
        this.color = color;
        this.isBot = true;
        this.bot = bot;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public String getColor() {
        return color;
    }

    public boolean isBot() {
        return isBot;
    }

    public Bot getBot() {
        return bot;
    }

    
}
