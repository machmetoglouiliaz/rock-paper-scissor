package com.mourat.rockpaperscissors.domain.model;

import java.sql.Array;

/**
 *  <--- Game Class --->
 *  Each game has a state, 2 players, a given number of rounds and a result.
 *  After the rounds are played the result is calculated to find the winner.
 *  The game starts only if both players are available.
 */
public class Game {

    // Game state
    private GameState state = GameState.INIT;

    // Players of this game.
    private Player player1;
    private Player player2;

    // Number of rounds and the array to keep the rounds result.
    private final int rounds;
    private int activeRound;
    private RoundResult[] roundResults;

    // Game result
    private GameResult result;

    // Each game must be created by a player.
    // Games without players are considered as concluded.
    private Game (Player player, int rounds){
        this.rounds = rounds;
        player1 = player;
        activeRound = 0;
        roundResults = new RoundResult[rounds];
        state = GameState.WAITING;
    }

    /**
     * Creates a new game with validating the arguments
     * Valid player must not be null
     *
     * @param player player who creates the game
     * @param rounds number of rounds of the game to conclude a winner
     * @return a new game with player as player 1 and a given number of rounds
     */
    public Game newGame(Player player, int rounds){

        // Validate player that exists
        if(player == null){
            throw new IllegalArgumentException("A game can't be created without a player!");
        }

        if(rounds < 1){
            throw new IllegalArgumentException("Rounds of the game must be a positive number!");
        }

        if(rounds <= 100){
            throw new IllegalArgumentException("The game can have max 100 rounds!");
        }

        return new Game(player, rounds);
    }


}
