package com.mourat.rockpaperscissors.domain.model;

import lombok.Getter;

import java.util.UUID;

/**
 * Represents a game of Rock Paper Scissors.
 * <p>
 * Each game has a unique ID, a state, two players, and a fixed number of rounds.
 * After the specified rounds are played, the game calculates the final result and determines the winner.
 */
@Getter
public class Game {

    /** Maximum allowed rounds in a game. */
    public static final int MAX_ROUNDS = 100;

    /** Unique identifier of the game. */
    private final UUID id;

    /** Current state of the game. */
    private GameState state = GameState.INIT;

    /** First player in the game. */
    private final Player player1;

    /** Second player in the game. */
    private Player player2;

    /** Score of player 1. */
    private int player1Score;

    /** Score of player 2. */
    private int player2Score;

    /** Number of drawn rounds. */
    private int draws;

    /** Total number of rounds in this game. */
    private final int rounds;

    /** Tracks the current active round (1-based). */
    private int activeRound;

    /** Array storing the results of each round. */
    private final RoundResult[] roundResults;

    /** The final result of the game after all rounds have been played. */
    private GameResult result;

    /**
     * Private constructor to enforce controlled creation of game instances.
     *
     * @param player the owner (player 1) who starts the game
     * @param rounds number of rounds for the game
     */
    private Game(Player player, int rounds) {
        this.id = UUID.randomUUID();
        this.rounds = rounds;
        this.activeRound = 1;
        this.roundResults = new RoundResult[rounds];
        this.player1Score = 0;
        this.player2Score = 0;
        this.draws = 0;
        this.result = null;

        this.player1 = player;
        player.setGamePlaying(this);

        this.state = GameState.IN_PROGRESS;
    }

    /**
     * Static factory method to create a new game instance.
     *
     * @param owner  the player who owns/starts the game (player 1)
     * @param rounds number of rounds for the game; must be between 1 and {@link #MAX_ROUNDS}
     * @return new game instance with the given parameters
     * @throws IllegalArgumentException if rounds is less than 1 or greater than {@link #MAX_ROUNDS}
     */
    public static Game newGame(Player owner, int rounds) {

        if (owner == null) {
            throw new IllegalArgumentException("Game without a given player, can't be created");
        }

        if (rounds < 1) {
            throw new IllegalArgumentException("Rounds of the game must be a positive number");
        }

        if (rounds > MAX_ROUNDS) {
            throw new IllegalArgumentException("The game can have max " + MAX_ROUNDS + " rounds!");
        }

        return new Game(owner, rounds);
    }

    /**
     * Plays a round by recording the round result, updating scores,
     * and checking if the game has finished.
     *
     * @param roundResult the result data of the current round; must not be null
     * @return {@code null} if the game continues; final {@link GameResult} if the game finishes after this round
     * @throws IllegalStateException    if the game is not {@code IN_PROGRESS}
     * @throws IllegalArgumentException if {@code roundResult} is null
     */
    public GameResult playRound(RoundResult roundResult) {
        if (state != GameState.IN_PROGRESS)
            throw new IllegalStateException("The game state is not in progress, can't play the round");
        if (roundResult == null)
            throw new IllegalArgumentException("Round can be played only with valid rounds data");

        this.roundResults[activeRound - 1] = roundResult;
        this.activeRound++;

        if (roundResult.winner() == null) {
            this.draws++;
        } else if (roundResult.winner().equals(player1)) {
            this.player1Score++;
        } else {
            this.player2Score++;
        }

        if (activeRound > rounds) {
            return endGame();
        }

        return null;
    }

    /**
     * Ends the game by calculating the winner and setting the game state to finished.
     *
     * @return the final {@link GameResult} of the game
     */
    private GameResult endGame() {
        Player winner;

        if (player1Score > player2Score) winner = player1;
        else if (player1Score < player2Score) winner = player2;
        else winner = null;

        this.result = new GameResult(player1Score, player2Score, draws, winner);
        this.state = GameState.FINISHED;

        return this.result;
    }

    /**
     * Adds the second player to the game.
     *
     * @param player the {@link Player} to join as player two
     * @return true if player was successfully added; false if player is null
     */
    public boolean setPlayerTwo(Player player) {
        if (player == null){
            throw new IllegalArgumentException("Joining player is not valid");
        }

        if(this.player2 != null){
            throw new IllegalStateException("Game already has 2 players");
        }

        this.player2 = player;
        player.setGamePlaying(this);

        return true;
    }

    /**
     * Returns the result of the most recently completed round.
     *
     * @return the last {@link RoundResult} or {@code null} if no rounds have been played yet
     */
    public RoundResult getLastRoundResult() {
        if (activeRound < 2){
            return null;
        }
        return roundResults[activeRound - 2];
    }

}
