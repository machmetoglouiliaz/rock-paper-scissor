package com.mourat.rockpaperscissors.application.service.impl;

import com.mourat.rockpaperscissors.application.service.GameRunnerService;
import com.mourat.rockpaperscissors.domain.model.Game;
import com.mourat.rockpaperscissors.domain.model.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*
 * Game runner service orchestrates all the domain level entities and services
 * User has access only to the application level services
 *
 * This service tracks the game and players also their states
 */
@Service
public class GameRunnerServiceImpl implements GameRunnerService {

    // Tracks all the players in case of player details is needed, or same player looks for another game
    private List<Player> players = new ArrayList<>();

    // Tracks different kind of games to manage them accordingly
    private List<Game> activeGames = new ArrayList<>();
    private List<Game> completeGames = new ArrayList<>();
    private List<Game> waitingGames = new ArrayList<>();


    // Creates a new game by the given player and given rounds long
    public boolean createGame(Player player, int rounds){

        Game newGame;

        // Try to create a new game, throws exception if the player does not exist or rounds are not in correct range
        try {
            newGame = Game.newGame(player, rounds);
        } catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }

        // Add the newly created game to the waiting list
        this.waitingGames.addLast(newGame);
        return true;
    }

    // Creates a new player with the given name
    public boolean createPlayer(String name){

        Player newPlayer;

        // Try to create a new player, throws exception if the given name is not correct
        try{
            newPlayer = Player.newPlayerWithName(name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        // Add the successfully created player to the player list
        this.players.addLast(newPlayer);
        return true;
    }

    // Finds an already existing game for the player
    public boolean joinGame(Player player){

        // Check if there is a game which is waiting for player
        if(this.waitingGames.isEmpty()) {
            return false;
        }

        // Take a game which waits for a player
        Game game = this.waitingGames.removeFirst();

        // Connect the player and the game to each other
        game.setPlayerTwo(player);

        // Add the game to the active games list
        this.activeGames.addLast(game);

        return true;

    }
}
