package com.mourat.rockpaperscissors.domain.model;

/**
 * Immutable value object representing the final result of a game.
 * <p>
 * Each game has one associated result upon completion.
 *
 * @param nOfPlayer1Wins the number of rounds won by Player 1
 * @param nOfPlayer2Wins the number of rounds won by Player 2
 * @param nOfDraws the number of drawn rounds
 * @param winner the overall winner of the game, or {@code null} in case of a tie
 */
public record GameResult(int nOfPlayer1Wins, int nOfPlayer2Wins, int nOfDraws, Player winner) {
}
