package com.mourat.rockpaperscissors.application.dto;

import lombok.Data;

/**
 * Data transfer object (DTO) representing the result summary of the game
 * Contains player details, scores, current round info and game status
 */
@Data
public class ResultDto {
    private String player1Name = "";
    private String player1Id = "";
    private String player1Score = "";

    private String player2Name = "";
    private String player2Id = "";
    private String player2Score = "";

    private String gameId = "";
    private String totalRounds = "";
    private String currentRound = "";

    private String ties = "";

    private String player1LastMove = "";
    private String player2LastMove = "";
    private String roundWinnerPlayerName = "";
    private String roundWinnerPlayerId = "";

    private boolean isGameFinished = false;

    private String gameWinnerPlayerName = "";
    private String gameWinnerPlayerId = "";

    /**
     * Returns formatted string summarizing the current state of the game
     *
     * @return a multiline string representation of the game result
     */
    @Override
    public String toString() {
        return "-".repeat(30) + "\n" +
                "PLAYER 1: NAME: " + player1Name + " | ID: " + player1Id + " | SCORE: " + player1Score + "\n" +
                "PLAYER 2: NAME: " + player2Name + " | ID: " + player2Id + " | SCORE: " + player2Score + "\n" +
                "TIES: " + ties + "\n" +
                "-".repeat(30) + "\n" +
                "LAST MOVES: \n" +
                "PLAYER 1: " + player1LastMove + "\n" +
                "PLAYER 2: " + player2LastMove + "\n" +
                "WINNER OF THE ROUND IS: " + roundWinnerPlayerName + " | " + roundWinnerPlayerId + "\n" +
                "-".repeat(30) + "\n" +
                "GAME WITH ID " + gameId + " IS " + (isGameFinished ?
                    "FINISHED! WINNER IS: " + gameWinnerPlayerName + " | " + gameWinnerPlayerId
                    : "ON ROUND " + currentRound + "/" + totalRounds);
    }
}
