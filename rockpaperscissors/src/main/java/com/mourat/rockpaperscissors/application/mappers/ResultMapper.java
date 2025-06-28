package com.mourat.rockpaperscissors.application.mappers;

import com.mourat.rockpaperscissors.application.dto.ResultDto;
import com.mourat.rockpaperscissors.domain.model.Game;
import com.mourat.rockpaperscissors.domain.model.GameState;
import com.mourat.rockpaperscissors.domain.model.RoundResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class responsible for mapping domain objects related to the game session
 * into Data Transfer Objects (DTOs) for communication layer.
 */
public class ResultMapper {

    private static final Logger logger = LoggerFactory.getLogger(ResultMapper.class);

    /**
     * Converts a {@link Game} domain object into a {@link ResultDto} object,
     * extracting and transforming relevant game result data.
     *
     * @param game the game session to map; must not be null
     * @return a {@link ResultDto} representing the current state and results of the game session
     * @throws IllegalArgumentException if {@code gameSession} is null
     */
    public static ResultDto toResultDto(Game game){

        if(game == null){
            logger.error("The game given to the mapper is null, can't map a null game to a Data Transfer Object (DTO)");
            throw new IllegalArgumentException("Game argument must not be null");
        }
        logger.debug("Creating a Data Transfer Object (DTO) for the last turn of the game with id \"{}\"", game.getId());
        ResultDto resultDto = new ResultDto();

        resultDto.setGameFinished(game.getState() == GameState.FINISHED);

        resultDto.setPlayer1Name(game.getPlayer1().getName());
        resultDto.setPlayer1Id(game.getPlayer1().getId().toString());
        if(resultDto.isGameFinished()){
            resultDto.setPlayer1Score(Integer.toString(game.getResult().nOfPlayer1Wins()));
        }
        else {
            resultDto.setPlayer1Score(Integer.toString(game.getPlayer1Score()));
        }

        resultDto.setPlayer2Name(game.getPlayer2().getName());
        resultDto.setPlayer2Id(game.getPlayer2().getId().toString());
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
