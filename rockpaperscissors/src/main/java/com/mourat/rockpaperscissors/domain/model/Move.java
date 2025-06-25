package com.mourat.rockpaperscissors.domain.model;

public enum Move {
    ROCK,
    PAPER,
    SCISSORS;

    public boolean beats(Move other) {
        return (this == Move.ROCK && other == Move.SCISSORS) ||
                (this == Move.PAPER && other == Move.ROCK) ||
                (this == Move.SCISSORS && other == Move.PAPER);
    }
}
