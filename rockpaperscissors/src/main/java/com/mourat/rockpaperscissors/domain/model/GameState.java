package com.mourat.rockpaperscissors.domain.model;

/**
 * States of a game.
 */
public enum GameState {

    /** Game created but not started. */
    INIT,

    /** Game is ongoing. */
    IN_PROGRESS,

    /** Game is finished. */
    FINISHED
}
