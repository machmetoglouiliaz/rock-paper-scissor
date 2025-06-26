package com.mourat.rockpaperscissors.application.model;

import com.mourat.rockpaperscissors.application.dto.ResultDto;
import com.mourat.rockpaperscissors.application.mappers.ResultMapper;
import com.mourat.rockpaperscissors.domain.model.*;
import com.mourat.rockpaperscissors.domain.service.GameRulesService;
import lombok.Getter;

import java.util.concurrent.CyclicBarrier;

/**
 * Represents a game session managing the state and synchronization
 * between two players.
 * <p>
 * The session holds references to the {@link Game} and the participating {@link Player}s,
 * manages player moves, and coordinates rounds using a CyclicBarrier.
 */
@Getter
public class GameSession {

    /** Current state of the game session */
    SessionState state = SessionState.INIT;

    /** The game being played in this session */
    private final Game game;

    /** First player in the session (owner) */
    private final Player player1;

    /** First player's move for the current round */
    private Move player1Move;

    /** Second player in the session */
    private Player player2;

    /** Second player's move for the current round */
    private Move player2Move;

    /** Synchronization point to ensure both players have moved before resolving the round */
    private final CyclicBarrier roundBarrier;

    /** Service for applying game rules and determining round outcomes */
    private final GameRulesService gameRulesService;

    /**
     * Constructs a new session for the given game with an owner player.
     *
     * @param owner the first player and session initiator
     * @param game the game instance associated with the session
     * @param gameRulesService service used to resolve rounds
     */
    public GameSession(Player owner, Game game, GameRulesService gameRulesService){
        this.game = game;
        this.roundBarrier = new CyclicBarrier(2);
        this.player1 = owner;
        owner.setGameSession(this);
        this.state = SessionState.WAITING_FOR_JOIN;
        this.player1Move = null;
        this.player2Move = null;

        this.gameRulesService = gameRulesService;
    }

    /**
     * Attempts to join a second player to the session. Only allowed if the session is in the
     * {@code WAITING_FOR_JOIN} state.
     *
     * @param player the player attempting to join
     * @return {@code true} if the player successfully joined, {@code false} otherwise
     */
    public synchronized boolean joinGame(Player player){

        // If the player is not valid abort joining
        if(player == null) return false;

        // If the session is not in a state waiting for player to join, abort the command
        if(state != SessionState.WAITING_FOR_JOIN) return false;

        // Add the player as the second player and start the game

        if(!game.setPlayerTwo(player)){
            return false;
        }
        this.player2 = player;
        player.setGameSession(this);

        state = SessionState.WAITING_FOR_MOVES;
        return true;
    }

    /**
     * Submits a player's move and evaluates the round once both players have submitted.
     * Blocks at a barrier to synchronize both players.
     *
     * @param player the player submitting a move
     * @param move the move made by the player
     * @return a {@link ResultDto} representing the current game state; {@code null} if move was ignored
     * @throws IllegalArgumentException if the player or move is null
     */
    public ResultDto makeMove(Player player, Move move){

        RoundResult roundResult;
        GameResult gameResult;

        if(player == null) throw new IllegalArgumentException("Player must be valid");
        if(move == null) throw new IllegalArgumentException("Move must be a valid move");

        // Assign the move to the correct player
        if( player.getId().equals(this.player1.getId()) && this.player1Move == null){
            this.player1Move = move;

        } else if (player.getId().equals(this.player2.getId()) && this.player2Move == null) {
            this.player2Move = move;
        } else {
            // Duplicate or invalid move
            return null;
        }

        // If both players have submitted, resolve the round
        if(player1Move != null && player2Move != null){
            this.state = SessionState.RUNNING;
            roundResult =  gameRulesService.checkRoundWinner(player1, player1Move, player2, player2Move);
            gameResult = game.playRound(roundResult);

            // Reset moves for next round
            player1Move = null;
            player2Move = null;
            this.state = SessionState.WAITING_FOR_MOVES;

            if(gameResult != null) this.state = SessionState.TERMINATED;
        }

        // Wait for both players to reach this point before proceeding
        try {
            roundBarrier.await();
        }catch(Exception e){
            System.out.println("Something went wrong with synchronization");
        }

        return ResultMapper.toResultDto(this.game);
    }
}
