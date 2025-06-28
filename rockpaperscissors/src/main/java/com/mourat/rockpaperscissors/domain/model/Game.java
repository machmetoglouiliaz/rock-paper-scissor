package com.mourat.rockpaperscissors.domain.model;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Represents a game of Rock Paper Scissors.
 * <p>
 * Each game has a unique ID, a state, two players, and a fixed number of rounds.
 * After the specified rounds are played, the game calculates the final result and determines the winner.
 */
@Getter
public class Game {

    private static final Logger logger = LoggerFactory.getLogger(Game.class);
    private static final Logger recordLogger = LoggerFactory.getLogger("recordsLogger");

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
        logger.debug("A game with id \"{}\" is created by player \"{}\"", id, player.getName());
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
            logger.error("This code should never be executed! Null player reached domain layer. New game can't be created by a null player. On this call, player is never null, check for corruption");
            throw new IllegalArgumentException("Game without a given player, can't be created");
        }

        if (rounds < 1) {
            logger.warn("A try for new game creation with invalid round count occurred");
            throw new IllegalArgumentException("Rounds of the game must be a positive number");
        }

        if (rounds > MAX_ROUNDS) {
            logger.warn("A try for new game creation with invalid round count occurred");
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
        if (state != GameState.IN_PROGRESS) {
            logger.error("The game is not in progress, can't play the round");
            throw new IllegalStateException("The game is not in progress, can't play the round");
        }
        if (roundResult == null) {
            logger.error("Round data is null! To record a game round, round's data must exist");
            throw new IllegalArgumentException("Round can be played only with valid rounds data");
        }

        this.roundResults[activeRound - 1] = roundResult;
        this.activeRound++;

        if (roundResult.winner() == null) {
            this.draws++;
        } else if (roundResult.winner().equals(player1)) {
            this.player1Score++;
        } else {
            this.player2Score++;
        }
        logger.debug("Round: P1: {}, P2: {}, Winner: {}", roundResult.player1Move(), roundResult.player2Move(), roundResult.winner() != null ? roundResult.winner().getName() : "No winner");
        recordLogger.warn("Round: P1: {}, P2: {}, \tWinner: {}", roundResult.player1Move(), roundResult.player2Move(), roundResult.winner() != null ? roundResult.winner().getName() : "No winner");

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

        logger.debug("Game Scores: P1: {}, P2: {}, Winner: {}", result.nOfPlayer1Wins(), result.nOfPlayer2Wins(), result.winner() != null ? result.winner().getName() : "It's a tie");
        recordLogger.error("Game Scores: P1: {}, P2: {}, \tWinner: {}", result.nOfPlayer1Wins(), result.nOfPlayer2Wins(), result.winner() != null ? result.winner().getName() : "It's a tie");

        this.player1.detachGame();
        this.player2.detachGame();

        return this.result;
    }

    /**
     * Adds the second player to the game.
     *
     * @param player the {@link Player} to join as player two
     * @return true if player was successfully added; false otherwise
     */
    public boolean setPlayerTwo(Player player) {
        if (player == null){
            logger.error("This code should never be executed! Null player reached domain layer. Null player can't join to a game. On this call, player is never null, check for corruption");
            throw new IllegalArgumentException("Joining player is not valid");
        }

        if(this.player2 != null){
            logger.error("A try to join to a full game occurred. This code can't be reached with normal flow. Check for corruption");
            return false;
        }

        if(player.equals(this.player1)){
            logger.warn("Player 1 trying to join to the same game. This code can't be reached with normal flow. Check for corruption");
            return false;
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
            logger.warn("Asked for the last played round before playing any rounds");
            return null;
        }
        return roundResults[activeRound - 2];
    }

}
