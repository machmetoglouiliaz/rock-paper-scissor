package com.mourat.rockpaperscissors.application.services;

import com.mourat.rockpaperscissors.application.dto.ResultDto;
import com.mourat.rockpaperscissors.domain.model.Player;
import org.springframework.stereotype.Service;

/**
 * Game runner service orchestrates all the domain level entities and services
 * User has access only to the application level services
 *
 * This service tracks the game and players also their states
 */
public interface GameRunnerService {

    /**
     * Creates a new game by the given player and given rounds long
     *
     * @param playerId the id of player who creates the game
     * @param rounds number of rounds the game should end
     * @return id of the new game as a string if the game creation is success, else an empty string
     */
    String createGame(String playerId, int rounds);

    /**
     * Creates a new player with the given name
     *
     * @param name the name of the new player
     * @return id of the new player as a string if the player creation is success, else an empty string
     */
    String createPlayer(String name);

    /**
     * Finds an already existing game for the player
     *
     * @param playerId the id of player who search for a game
     * @return id of the game joined as a string if the game creation is success, else an empty string
     */
    String joinGame(String playerId);

    ResultDto makeMove(String playerId, String move);
}
