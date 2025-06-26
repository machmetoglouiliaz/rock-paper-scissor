package com.mourat.rockpaperscissors.application.services;

import com.mourat.rockpaperscissors.application.dto.ResultDto;

/**
 * Application-layer service responsible for orchestrating all domain-level entities and services.
 * <p>
 * Provides high-level access for clients to interact with the game, such as creating players,
 * starting games, joining games, and making moves.
 * <p>
 * This service acts as the primary entry point to the game logic, isolating the client from
 * domain internals and maintaining game and player state.
 */
public interface GameRunnerService {

    /**
     * Creates a new game session with the given number of rounds, initiated by the specified player.
     *
     * @param playerId the ID of the player creating the game
     * @param rounds the number of rounds the game will run
     * @return the ID of the newly created game; an empty string if creation failed
     */
    String createGame(String playerId, int rounds);

    /**
     * Creates a new player with the given name.
     *
     * @param name the name of the new player
     * @return the ID of the newly created player; an empty string if creation failed
     */
    String createPlayer(String name);

    /**
     * Allows a player to join an existing open game session.
     *
     * @param playerId the ID of the player attempting to join
     * @return the ID of the joined game; an empty string if join failed
     */
    String joinGame(String playerId);

    /**
     * Submits a move for the specified player in their active game session.
     *
     * @param playerId the ID of the player making the move
     * @param move the move made by the player (e.g., "ROCK", "PAPER", "SCISSORS")
     * @return a {@link ResultDto} containing the result of the round and current game state
     */
    ResultDto makeMove(String playerId, String move);
}
