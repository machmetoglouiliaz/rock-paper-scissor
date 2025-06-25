package com.mourat.rockpaperscissors.application.model;

/**
 * Represents the lifecycle states of a game session.
 * Used to control flow and synchronization within a game session.
 */
public enum SessionState {
    /** Session has been created but not yet started */
    INIT,

    /** First player is waiting for a second player to join */
    WAITING_FOR_JOIN,

    /** Both players are present and waiting to submit their moves */
    WAITING_FOR_MOVES,

    /** A round is in progress and being evaluated */
    RUNNING,

    /** The game session has finished */
    TERMINATED
}
