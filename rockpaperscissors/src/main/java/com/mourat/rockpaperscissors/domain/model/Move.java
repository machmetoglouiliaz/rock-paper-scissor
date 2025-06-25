package com.mourat.rockpaperscissors.domain.model;

/**
 * Possible moves in the game.
 */
public enum Move {

    /** Rock move. */
    ROCK,

    /** Paper move. */
    PAPER,

    /** Scissors move. */
    SCISSORS;

    /**
     * Checks if this move beats the other move.
     *
     * @param other the move to compare with
     * @return {@code true} if this move beats the other, else {@code false}
     */
    public boolean beats(Move other) {
        return (this == ROCK && other == SCISSORS) ||
                (this == PAPER && other == ROCK) ||
                (this == SCISSORS && other == PAPER);
    }
}
