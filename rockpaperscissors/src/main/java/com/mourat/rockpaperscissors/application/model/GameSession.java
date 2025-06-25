package com.mourat.rockpaperscissors.application.model;

import com.mourat.rockpaperscissors.application.dto.ResultDto;
import com.mourat.rockpaperscissors.application.mappers.ResultMapper;
import com.mourat.rockpaperscissors.domain.model.*;
import com.mourat.rockpaperscissors.domain.service.GameRulesService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CyclicBarrier;

/**
 * A game session class to keep synchronized the players of the game
 */
@Getter
public class GameSession {

    // State of the session
    SessionState state = SessionState.INIT;

    // The game of the session
    private Game game;

    // The players of the session
    private Player player1;
    private Move player1Move;
    private Player player2;
    private Move player2Move;

    // A cyclic barrier to create a synchronization point
    // It resets on every pass
    private final CyclicBarrier roundBarrier;
    private final GameRulesService gameRulesService;

    /**
     * Creates a session for the given game
     *
     * @param game the game to create a session for it
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
     * Join the player to this session
     *
     * @param player player to join
     * @return true if the player successfully joined, else false
     */
    public synchronized boolean joinGame(Player player){

        // If the player is not valid abort joining
        if(player == null) return false;

        // If the session is not in a state waiting for player to join, abort the command
        if(state != SessionState.WAITING_FOR_JOIN) return false;

        // Add the player as the second player and start the game
        this.player2 = player;
        player.setGameSession(this);
        game.setPlayerTwo(player);

        state = SessionState.WAITING_FOR_MOVES;
        return true;
    }

    public ResultDto makeMove(Player player, Move move){

        RoundResult roundResult = null;
        GameResult gameResult = null;

        // Player and move must be valid
        if(player == null) throw new IllegalArgumentException("Player must be valid");
        if(move == null) throw new IllegalArgumentException("Move must be a valid move");

        if( player.getId().equals(this.player1.getId()) && this.player1Move == null){
            this.player1Move = move;

        } else if (player.getId().equals(this.player2.getId()) && this.player2Move == null) {
            this.player2Move = move;
        } else {
            return null;
        }

        if(player1Move != null && player2Move != null){
            this.state = SessionState.RUNNING;
            roundResult =  gameRulesService.checkRoundWinner(player1, player1Move, player2, player2Move);
            gameResult = game.playRound(roundResult);

            player1Move = null;
            player2Move = null;
            this.state = SessionState.WAITING_FOR_MOVES;
        }

        try {
            roundBarrier.await();
        }catch(Exception e){
            System.out.println("Something went wrong with synchronization");
        }

        return ResultMapper.toResultDto(this);
    }
}
