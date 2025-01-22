package com.ken2_1.bots;

import java.util.ArrayList;

import com.ken2_1.Game_Components.Board.Vertex;
import com.ken2_1.engine.GameState;
import com.ken2_1.engine.Move;

/**
 * Interface for all bots.
 * Provides a standard structure for bot implementations to interact with the game engine.
 */
public interface Bot {
    /**
     * Retrieves the color assigned to the bot.
     *
     * @return The bot's color (e.g., "white" or "black").
     */
    String getColor();

    /**
     * Retrieves the name of the bot.
     *
     * @return The bot's name.
     */
    String getName();

    /**
     * Determines the bot's next move based on the current game state.
     *
     * @param state The current game state.
     * @return The move that the bot decides to make.
     */
    Move makeMove(GameState state);

    /**
     * Removes a ring from the board as part of the game rules.
     *
     * @param state The current game state.
     * @return The vertex from which the ring is removed.
     */
    Vertex removeRing(GameState state);

    /**
     * Removes chips from the board based on the game's rules.
     *
     * @param state The current game state.
     * @return A list of vertex indices where chips were removed.
     */
    ArrayList<Integer> removeChips(GameState state);
}
