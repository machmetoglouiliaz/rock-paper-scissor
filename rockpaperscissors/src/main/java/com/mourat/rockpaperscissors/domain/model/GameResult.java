package com.mourat.rockpaperscissors.domain.model;

/**
 * The final result of a game, each game has a result object
 * Immutable value object
 *
 * @param nOfPlayer1Wins the total win count of player 1
 * @param nOfPlayer2Wins the total win count of player 2
 * @param nOfDraws total number of draws
 * @param winner the overall winner of the game
 */
public record GameResult(int nOfPlayer1Wins, int nOfPlayer2Wins, int nOfDraws, Player winner) {
}
