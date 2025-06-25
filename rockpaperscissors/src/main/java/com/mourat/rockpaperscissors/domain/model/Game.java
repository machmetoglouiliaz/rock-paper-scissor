package com.mourat.rockpaperscissors.domain.model;


import com.mourat.rockpaperscissors.application.model.SessionState;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * <--- Game Entity --->
 * Each game has a state, a given number of rounds and a result.
 * After the rounds are played the result is calculated to find the winner.
 */
@Getter
public class Game {

    // Maximum rounds of the game, every game should have an end point
    public static final int MAX_ROUNDS = 100;

    // Every game has a unique id
    private UUID id;

    // The state of the game
    GameState state = GameState.INIT;

    // Players of the game
    private Player player1;
    private Player player2;

    // Scores of the players
    private int player1Score;
    private int player2Score;
    private int draws;

    // Number of rounds and the array to keep the rounds result.
    private final int rounds;
    private int activeRound;
    private RoundResult[] roundResults;

    // Game result
    private GameResult result;

    /**
     *
     *
     * @param rounds number of rounds to be played in this game
     */
    private Game(Player player, int rounds) {
        this.id = UUID.randomUUID();
        this.rounds = rounds;
        this.activeRound = 1;
        this.roundResults = new RoundResult[rounds];
        this.state = GameState.IN_PROGRESS;
        this.player1 = player;
        player.setGamePlaying(this);

        this.player1Score = 0;
        this.player2Score = 0;
        this.draws = 0;

        this.result = null;
    }

    /**
     * Creates a new game with given amount of rounds
     *
     * @param rounds number of rounds of the game to conclude a winner
     * @return a new game with player as player 1 and a given number of rounds
     */
    public static Game newGame(Player owner, int rounds) {


        // Rounds must be a positive number, else cant be played
        if (rounds < 1) {
            throw new IllegalArgumentException("Rounds of the game must be a positive number!");
        }

        // The game must end in some point
        if (rounds > MAX_ROUNDS) {
            throw new IllegalArgumentException("The game can have max 100 rounds!");
        }

        return new Game(owner, rounds);
    }

    /**
     * Imports the results to the game object and checks if the game is finished
     *
     * @param roundResult data for the current round
     * @return null if the game continues, game result if the game finished
     */
    public GameResult playRound(RoundResult roundResult){

        // Check if the round can be played
        if(state != GameState.IN_PROGRESS) throw new IllegalStateException("The game state is not in progress, cant play the round");
        if(roundResult == null) throw new IllegalArgumentException("Round can be played only with valid rounds data");

        // Store the round data
        this.roundResults[activeRound - 1] = roundResult;
        this.activeRound++;

        // Increase score according the given data
        if(roundResult.winner() == null) {
            this.draws++;
        }
        else if(roundResult.winner().equals(player1)){
            this.player1Score++;
        }
        else {
            this.player2Score++;
        }

        // Check if the game is finished and return the results if it is
        if (activeRound > rounds) {
            return endGame();
        }

        // Return null if the game continues
        return null;
    }

    /**
     * Changes the game state to finished, making it immutable and creates the final result
     *
     * @return final result of the game
     */
    private GameResult endGame(){

        Player winner;

        // Determine the winner
        if(player1Score > player2Score) winner = player1;
        else if(player1Score < player2Score) winner = player2;
        else winner = null;

        // Create the final result
        this.result = new GameResult(player1Score, player2Score, draws, winner);
        this.state = GameState.FINISHED;

        return this.result;
    }

    /**
     * Adds the second player to this game and connects it from both sides
     *
     * @param player joining player
     * @return true if successful, else false
     */
    public boolean setPlayerTwo(Player player){

        if(player == null) return false;

        this.player2 = player;
        player.setGamePlaying(this);

        return true;
    }

    public RoundResult getLastRoundResult(){
        if(activeRound < 2) return null;
        return roundResults[activeRound - 2];
    }

}
