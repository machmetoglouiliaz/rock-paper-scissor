package com.mourat.rockpaperscissors.application.model;

import com.mourat.rockpaperscissors.application.dto.ResultDto;
import com.mourat.rockpaperscissors.application.mappers.ResultMapper;
import com.mourat.rockpaperscissors.domain.model.*;
import com.mourat.rockpaperscissors.domain.service.GameRulesService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private Logger logger = LoggerFactory.getLogger(GameSession.class);

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

        logger.debug("New session for the game \"{}\" by player \"{}\":\"{}\" successfully created", game.getId(), owner.getName(), owner.getId());
    }

    /**
     * Attempts to join a second player to the session. Only allowed if the session is in the
     * {@code WAITING_FOR_JOIN} state.
     *
     * @param player the player attempting to join
     * @return {@code true} if the player successfully joined, {@code false} otherwise
     * @throws IllegalArgumentException if {@code player} is {@code null}
     */
    public synchronized boolean joinGame(Player player){

        // If the player is not valid abort joining
        if(player == null){
            logger.error("This code should never be executed! Join attempt by a null player, joining player can't be null. On this call, player is never null, check for corruption");
            throw new IllegalArgumentException("Player must be valid to join a game");
        }

        // If the session is not in a state waiting for player to join, abort the command
        if(state != SessionState.WAITING_FOR_JOIN) {
            logger.warn("Join attempt to a game with state \"{}\" can't be done, the state of the session must be \"{}\" to be joined", state, SessionState.WAITING_FOR_JOIN);
            return false;
        }

        // Add the player as the second player and start the game
        if(!game.setPlayerTwo(player)){
            logger.warn("Can't complete the joining process, denied by domain layer");
            return false;
        }
        this.player2 = player;
        player.setGameSession(this);

        state = SessionState.WAITING_FOR_MOVES;
        logger.debug("Player's join request processed successfully by the session...");
        return true;
    }

    /**
     * Submits a player's move and evaluates the round once both players have submitted.
     * Blocks at a barrier to synchronize both players.
     *
     * @param player the player submitting a move
     * @param move the move made by the player
     * @return a {@link ResultDto} representing the current game state
     * @throws IllegalArgumentException if the player or move is null
     */
    public ResultDto makeMove(Player player, Move move){

        RoundResult roundResult;
        GameResult gameResult;
        ResultDto dto = new ResultDto();

        if(player == null){
            logger.error("This code should never be executed! Move can't be made by a null player. On this call, player is never null, check for corruption");
            throw new IllegalArgumentException("Player must be valid");
        }

        if(move == null){
            logger.error("This code should never be executed! Move can't be null. On this call, move is never null, check for corruption");
            throw new IllegalArgumentException("Move must be a valid move");
        }

        if(state != SessionState.WAITING_FOR_MOVES){
            dto.setSuccess(false);
            dto.setStatusMessage("Cant make move in this state of session");
            logger.warn("The session is not in a state to accept move requests");
            return dto;
        }

        // Assign the move to the correct player
        if(player.getId().equals(this.player1.getId()) && this.player1Move == null){
            this.player1Move = move;
        }
        else if (player.getId().equals(this.player2.getId()) && this.player2Move == null) {
            this.player2Move = move;
        }
        else {
            // Duplicate or invalid move
            dto.setSuccess(false);
            dto.setStatusMessage("Invalid player or multiple moves from same player");
            logger.error("The player trying to make the move is not this session's player, or same player trying to make a move again. Check for thread sync or corruption");
            return dto;
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

            if(gameResult != null) {
                logger.info("Session with the game id \"{}\" is ended", game.getId());
                this.state = SessionState.TERMINATED;
            }
        }

        // Wait for both players to reach this point before proceeding
        try {
            roundBarrier.await();
        }catch(Exception e){
            logger.error("Something went wrong with synchronization, the returning result can be corrupted");
            System.out.println("Something went wrong with synchronization, the returning result can be corrupted");
        }

        return ResultMapper.toResultDto(this.game);
    }
}
