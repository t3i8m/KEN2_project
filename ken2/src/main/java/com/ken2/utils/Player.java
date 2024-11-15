package com.ken2.utils;

import com.ken2.bots.Bot;

public class Player {
    private String color;
    private boolean isBot;
    private Bot bot;

    // contructor for human
    public Player(String color) {
        this.color = color;
        this.isBot = false;
        this.bot = null;
    }

    // constructor for a bot
    public Player(String color, Bot bot) {
        this.color = color;
        this.isBot = true;
        this.bot = bot;
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
