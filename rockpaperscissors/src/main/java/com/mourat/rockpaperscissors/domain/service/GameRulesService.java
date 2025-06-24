package com.mourat.rockpaperscissors.domain.service;

import com.mourat.rockpaperscissors.domain.model.Move;
import com.mourat.rockpaperscissors.domain.model.Player;
import com.mourat.rockpaperscissors.domain.model.RoundResult;

/**
 * Game rules service interface
 * Application layer services will use this interface to check rules
 */
public interface GameRulesService {

    /**
     * The game logic that applies to the round
     * This method must only be called from application layer,
     * so the arguments should already be validated before
     * LOGIC:   ROCK wins SCISSORS,
     *          SCISSORS wins PAPER,
     *          PAPER wins ROCK
     *
     * @param player1     first player who plays the round
     * @param player1Move the move of player 1
     * @param player2     second player who plays the round
     * @param player2Move the move of player 2
     * @return returns an immutable record instance of result
     */
    RoundResult checkRoundWinner(Player player1, Move player1Move, Player player2, Move player2Move);
}
