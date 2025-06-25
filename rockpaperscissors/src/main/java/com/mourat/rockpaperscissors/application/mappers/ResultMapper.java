package com.mourat.rockpaperscissors.application.mappers;

import com.mourat.rockpaperscissors.application.dto.ResultDto;
import com.mourat.rockpaperscissors.application.model.GameSession;
import com.mourat.rockpaperscissors.domain.model.Game;
import com.mourat.rockpaperscissors.domain.model.GameState;
import com.mourat.rockpaperscissors.domain.model.RoundResult;

public class ResultMapper {

    public static ResultDto toResultDto(GameSession gameSession){

        ResultDto resultDto = new ResultDto();

        Game game = gameSession.getGame();

        if(game.getState() == GameState.FINISHED){
            resultDto.setGameFinished(true);
        }
        else {
            resultDto.setGameFinished(false);
        }

        resultDto.setPlayer1Name(gameSession.getPlayer1().getName());
        resultDto.setPlayer1Id(gameSession.getPlayer1().getId().toString());
        if(resultDto.isGameFinished()){
            resultDto.setPlayer1Score(Integer.toString(game.getResult().nOfPlayer1Wins()));
        }
        else {
            resultDto.setPlayer1Score(Integer.toString(game.getPlayer1Score()));
        }

        resultDto.setPlayer2Name(gameSession.getPlayer2().getName());
        resultDto.setPlayer2Id(gameSession.getPlayer2().getId().toString());
        if(resultDto.isGameFinished()){
            resultDto.setPlayer2Score(Integer.toString(game.getResult().nOfPlayer2Wins()));
        }
        else {
            resultDto.setPlayer2Score(Integer.toString(game.getPlayer2Score()));
        }

        resultDto.setTies(Integer.toString(game.getDraws()));

        resultDto.setGameId(game.getId().toString());
        resultDto.setTotalRounds(Integer.toString(game.getRounds()));
        resultDto.setCurrentRound(Integer.toString(game.getActiveRound()));

        RoundResult lastRoundResult = game.getLastRoundResult();
        resultDto.setPlayer1LastMove(lastRoundResult.player1Move().toString());
        resultDto.setPlayer2LastMove(lastRoundResult.player2Move().toString());
        if(lastRoundResult.winner() != null) {
            resultDto.setRoundWinnerPlayerName(lastRoundResult.winner().getName());
            resultDto.setRoundWinnerPlayerId(lastRoundResult.winner().getId().toString());
        }

        if(game.getResult() != null && game.getResult().winner() != null) {
            resultDto.setGameWinnerPlayerName(game.getResult().winner().getName());
            resultDto.setGameWinnerPlayerId(game.getResult().winner().getId().toString());
        }

        return resultDto;
    }
}
