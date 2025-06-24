package com.mourat.rockpaperscissors.domain.model;


/**
 * <--- Game Entity --->
 * Each game has a state, 2 players, a given number of rounds and a result.
 * After the rounds are played the result is calculated to find the winner.
 * The game starts only if both players are available.
 */
public class Game {

    // Maximum rounds of the game, every game should have an end point
    public static final int MAX_ROUNDS = 100;

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

    /**
     * Each game must be created by a player.
     * Games without players are considered as concluded.
     *
     * @param player player who creates the game
     * @param rounds number of rounds to be played in this game
     */
    private Game(Player player, int rounds) {
        this.rounds = rounds;
        player1 = player;
        activeRound = 1;
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
    public static Game newGame(Player player, int rounds) {

        // Validate player that exists
        if (player == null) {
            throw new IllegalArgumentException("A game can't be created without a player!");
        }

        // Rounds must be a positive number, else cant be played
        if (rounds < 1) {
            throw new IllegalArgumentException("Rounds of the game must be a positive number!");
        }

        // The game must end in some point
        if (rounds <= MAX_ROUNDS) {
            throw new IllegalArgumentException("The game can have max 100 rounds!");
        }

        return new Game(player, rounds);
    }

    /**
     * Set the second player of the game, first player is always set on creation
     * Connects the player and the game in both sides
     * By joining another player, game starts and the game state changes to RUNNING
     *
     * @param player the joining player
     */
    public void setPlayerTwo(Player player){

        // Player must be valid to join
        if(player == null){
            throw new IllegalArgumentException("Joining player can't be null!");
        }

        // Set the second player, also set the game of the player to this game
        this.player2 = player;
        player.setGamePlaying(this);

        // Change the game state to running
        this.state = GameState.RUNNING;
    }
}
