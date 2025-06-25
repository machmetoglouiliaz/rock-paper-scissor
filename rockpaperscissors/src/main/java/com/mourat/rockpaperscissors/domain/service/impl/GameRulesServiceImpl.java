package com.mourat.rockpaperscissors.domain.service.impl;

import com.mourat.rockpaperscissors.domain.model.Move;
import com.mourat.rockpaperscissors.domain.model.Player;
import com.mourat.rockpaperscissors.domain.model.RoundResult;
import com.mourat.rockpaperscissors.domain.service.GameRulesService;
import org.springframework.stereotype.Service;

/*
 * Game rules service implementation
 * Some rules are round based and others game based
 */
@Service
public class GameRulesServiceImpl implements GameRulesService {

    /*
     * The round logic that applies to each round
     * This method must only be called from application layer,
     * so the arguments should already be validated before
     *
     * LOGIC:   ROCK wins SCISSORS,
     *          SCISSORS wins PAPER,
     *          PAPER wins ROCK
     */
    @Override
    public RoundResult checkRoundWinner(Player player1, Move player1Move, Player player2, Move player2Move) {


        // Check if the given players exists
        if (player1 == null || player2 == null) {
            throw new IllegalArgumentException("Players cant be null! This service should have validated arguments!");
        }

        // The moves must exist to have a winner
        if (player1Move == null || player2Move == null) {
            throw new IllegalArgumentException("Moves cant be null! This service should have validated arguments!");
        }

        // If both have the same move then it is a draw
        if (player1Move == player2Move) {
            return new RoundResult(player1Move, player2Move, null);
        }

        // Applied game logic to determine the winner
        if (player1Move.beats(player2Move)) {
            return new RoundResult(player1Move, player2Move, player1);
        } else {
            return new RoundResult(player1Move, player2Move, player2);
        }
    }

}
