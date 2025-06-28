package com.mourat.rockpaperscissors.domain.model;

import com.mourat.rockpaperscissors.application.model.GameSession;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Represents a player in the game.
 * Each player has a name, a unique ID, and may be associated with a game.
 */
@Getter
public class Player {

    private static final Logger logger = LoggerFactory.getLogger(Player.class);

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
        logger.debug("A player with name {} and id {} is created", name, id);
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
            logger.error("The name of the player must be between 2 and 16 characters.");
            throw new IllegalArgumentException("Name must be between 2 and 16 characters");
        }
        return new Player(name);
    }

    /**
     * Detaches this player from any currently associated game and game session.
     * <p>
     * Sets both {@code gameSession} and {@code gamePlaying} to {@code null}.
     */
    public void detachGame(){
        logger.debug("Player {} detached from game {}", id, gamePlaying.getId());
        this.gameSession = null;
        this.gamePlaying = null;
    }
}