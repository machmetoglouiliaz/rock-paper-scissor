package com.mourat.rockpaperscissors.domain.service.impl;

import com.mourat.rockpaperscissors.domain.model.Move;
import com.mourat.rockpaperscissors.domain.model.Player;
import com.mourat.rockpaperscissors.domain.model.RoundResult;
import com.mourat.rockpaperscissors.domain.service.GameRulesService;
import org.springframework.stereotype.Service;

/**
 * Implements {@link GameRulesService} to apply round-based rules.
 * Validates and determines the winner of each round.
 */
@Service
public class GameRulesServiceImpl implements GameRulesService {

    /**
     * {@inheritDoc}
     */
    @Override
    public RoundResult checkRoundWinner(Player player1, Move player1Move, Player player2, Move player2Move) {
        if (player1 == null || player2 == null) {
            throw new IllegalArgumentException("Players can't be null.");
        }

        if (player1Move == null || player2Move == null) {
            throw new IllegalArgumentException("Moves can't be null.");
        }

        if (player1Move == player2Move) {
            return new RoundResult(player1Move, player2Move, null);
        }

        return ((player1Move == Move.ROCK && player2Move == Move.SCISSORS) ||
                (player1Move == Move.PAPER && player2Move == Move.ROCK) ||
                (player1Move == Move.SCISSORS && player2Move == Move.PAPER))
                ? new RoundResult(player1Move, player2Move, player1)
                : new RoundResult(player1Move, player2Move, player2);
    }
}
