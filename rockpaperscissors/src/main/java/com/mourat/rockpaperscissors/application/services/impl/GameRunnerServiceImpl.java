package com.mourat.rockpaperscissors.application.services.impl;

import com.mourat.rockpaperscissors.application.dto.ResultDto;
import com.mourat.rockpaperscissors.application.model.GameSession;
import com.mourat.rockpaperscissors.application.services.GameRunnerService;
import com.mourat.rockpaperscissors.application.services.GameSessionFactory;
import com.mourat.rockpaperscissors.domain.model.Game;
import com.mourat.rockpaperscissors.domain.model.Move;
import com.mourat.rockpaperscissors.domain.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private List<GameSession> activeGames = new ArrayList<>();
    private List<GameSession> completeGames = new ArrayList<>();
    private List<GameSession> waitingGames = new ArrayList<>();

    // Session factory injected by spring
    private GameSessionFactory gameSessionFactory;

    // injects session factory
    @Autowired
    public GameRunnerServiceImpl(GameSessionFactory sessionFactory){
        this.gameSessionFactory = sessionFactory;
    }


    // Creates a new game by the given player and given rounds long
    @Override
    public String createGame(String playerId, int rounds){

        Game newGame;
        Player player = this.findPlayerById(playerId);

        if(player == null){
            System.out.println("Player with id" + playerId + " doesn't exist");
            return null;
        }

        // Try to create a new game, throws exception if the player does not exist or rounds are not in correct range
        try {
            newGame = Game.newGame(player, rounds);
        } catch(Exception e){
            System.out.println(e.getMessage());
            return "";
        }

        // Add the newly created game to the waiting list by creating a new session for it
        this.waitingGames.addLast(gameSessionFactory.createSession(player, newGame));
        return newGame.getId().toString();
    }

    // Creates a new player with the given name
    @Override
    public String createPlayer(String name){

        Player newPlayer;

        // Try to create a new player, throws exception if the given name is not correct
        // Returns empty string as an id
        try{
            newPlayer = Player.newPlayerWithName(name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }

        // Add the successfully created player to the player list
        this.players.addLast(newPlayer);
        return newPlayer.getId().toString();
    }

    // Finds an already existing game for the player
    @Override
    public String joinGame(String playerId){

        Player player = this.findPlayerById(playerId);

        if(player == null){
            System.out.println("Player with id" + playerId + " doesn't exist");
            return null;
        }

        // Check if there is a game which is waiting for player
        if(this.waitingGames.isEmpty()) {
            return "";
        }

        // Take a game which waits for a player
        GameSession gameSession = this.waitingGames.removeFirst();
        Game game = gameSession.getGame();

        // Connect the player and the game to each other
        gameSession.joinGame(player);

        // Add the game to the active games list
        this.activeGames.addLast(gameSession);

        return game.getId().toString();

    }

    // Plays the round for the player with given id
    @Override
    public ResultDto makeMove(String playerId, String moveString) {

        Move move;
        Player player = findPlayerById(playerId);
        GameSession gameSession;

        try {
            move = Move.valueOf(moveString);
        } catch (IllegalArgumentException e){
            System.out.println("Move " + moveString + " does not exist!");
            return null;
        }

        if(player == null){
            System.out.println("Player with id " + playerId + " doesn't exist");
            return null;
        }

        gameSession = player.getGameSession();
        if(gameSession == null){
            System.out.println("Player with id " + playerId + " is not joined to a game yet");
            return null;
        }

        return gameSession.makeMove(player, move);
    }

    /**
     * Search player with given id in the player list
     *
     * @param playerId given playerId as string
     * @return player object if there is a player with the given id, else null
     */
    private Player findPlayerById(String playerId){

        for(Player player : players){
            if(player.getId().equals(UUID.fromString(playerId))){
                return player;
            }
        }

        return null;
    }

}
