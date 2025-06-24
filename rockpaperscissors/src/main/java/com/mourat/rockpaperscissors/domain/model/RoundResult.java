package com.mourat.rockpaperscissors.domain.model;

/***
 * A single round result in the game
 * Immutable value object
 *
 * @param player1Move move chosen by player 1
 * @param player2Move move chosen by player 2
 * @param winner the winner of the round, or null if it is a draw
 ***/
public record RoundResult(Move player1Move, Move player2Move, Player winner) {
}
