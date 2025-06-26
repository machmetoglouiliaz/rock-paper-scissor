package com.mourat.rockpaperscissors.domain.model;

import com.mourat.rockpaperscissors.application.model.GameSession;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents a player in the game.
 * Each player has a name, a unique ID, and may be associated with a game.
 */
@Getter
public class Player {

    /** Unique player ID. */
    private final UUID id;

    /** Player's name. */
    private String name;

    /** Game currently played by the player. */
    @Setter
    private Game gamePlaying;

    /** Game session the player is part of. */
    @Setter
    private GameSession gameSession;

    /**
     * Constructs a player with the given name.
     * Use {@link #newPlayerWithName(String)} for validation.
     *
     * @param name player's name
     */
    private Player(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    /**
     * Factory method to create a player with a validated name.
     *
     * @param name player's name (2–16 characters)
     * @return new {@code Player} instance
     * @throws IllegalArgumentException if name is invalid
     */
    public static Player newPlayerWithName(String name) {
        if (name == null || name.trim().length() < 2 || name.trim().length() > 16) {
            throw new IllegalArgumentException("Name must be between 2 and 16 characters");
        }
        return new Player(name);
    }
}