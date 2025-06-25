package com.mourat.rockpaperscissors.application.model;

public enum SessionState {
    INIT,
    WAITING_FOR_JOIN,
    WAITING_FOR_MOVES,
    RUNNING,
    TERMINATED
}
