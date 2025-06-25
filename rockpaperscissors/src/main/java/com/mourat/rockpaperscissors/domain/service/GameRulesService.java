package com.mourat.rockpaperscissors.domain.service;

import com.mourat.rockpaperscissors.domain.model.Move;
import com.mourat.rockpaperscissors.domain.model.Player;
import com.mourat.rockpaperscissors.domain.model.RoundResult;

/**
 * Defines the game rules for determining the round winner.
 * Intended to be used by the application layer.
 */
public interface GameRulesService {

    /**
     * Determines the winner of a round.
     * Assumes all inputs are already validated.
     * <p>
     * Logic:
     * <ul>
     *   <li>ROCK beats SCISSORS</li>
     *   <li>SCISSORS beats PAPER</li>
     *   <li>PAPER beats ROCK</li>
     * </ul>
     *
     * @param player1     the first player
     * @param player1Move move of the first player
     * @param player2     the second player
     * @param player2Move move of the second player
     * @return {@link RoundResult} representing the outcome
     */
    RoundResult checkRoundWinner(Player player1, Move player1Move, Player player2, Move player2Move);
}
