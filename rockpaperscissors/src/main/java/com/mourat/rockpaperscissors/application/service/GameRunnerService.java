package com.mourat.rockpaperscissors.application.service;

import com.mourat.rockpaperscissors.domain.model.Player;

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
     * @param player the player who creates the game
     * @param rounds number of rounds the game should end
     * @return true if the game creation is success else false
     */
    boolean createGame(Player player, int rounds);

    /**
     * Creates a new player with the given name
     *
     * @param name the name of the new player
     * @return true if the player creation is success else false
     */
    boolean createPlayer(String name);

    /**
     * Finds an already existing game for the player
     *
     * @param player player who search for a game
     * @return true if a game is found and joined successfully else false
     */
    boolean joinGame(Player player);
}
