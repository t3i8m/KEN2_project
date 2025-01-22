package com.ken2.utils;

import com.ken2.bots.Bot;
/**
 * Represents a player in the game, which can be either a human or a bot.
 */
public class Player {
    private String color;
    private boolean isBot;
    private Bot bot;
    private String name;

    /**
     * Constructor for a human player.
     *
     * @param color The color of the player (e.g., "white" or "black").
     * @param name The name of the human player.
     */
    public Player(String color,String name) {
        this.color = color;
        this.isBot = false;
        this.bot = null;
        this.name = name;
    }

    /**
     * Constructor for a bot player.
     *
     * @param color The color of the player (e.g., "white" or "black").
     * @param bot The bot instance controlling the player.
     * @param name The name of the bot.
     */
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
