package com.mourat.rockpaperscissors.application.mappers;

import com.mourat.rockpaperscissors.application.dto.ResultDto;
import com.mourat.rockpaperscissors.domain.model.Game;
import com.mourat.rockpaperscissors.domain.model.Move;
import com.mourat.rockpaperscissors.domain.model.Player;
import com.mourat.rockpaperscissors.domain.model.RoundResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ResultMapperTest {

    @Test
    void toResultDto_unfinishedGameWithRoundWinner_correctMapRoundFields() {
        Game game = createUnfinishedGameWithRoundWinner();

        ResultDto result = ResultMapper.toResultDto(game);

        assertNotNull(result);

        assertEquals("Player A", result.getPlayer1Name());
        assertFalse(result.getPlayer1Id().isEmpty());
        assertEquals("2", result.getPlayer1Score());

        assertEquals("Player B", result.getPlayer2Name());
        assertFalse(result.getPlayer2Id().isEmpty());
        assertEquals("0", result.getPlayer2Score());

        assertFalse(result.getGameId().isEmpty());
        assertEquals("4", result.getTotalRounds());
        assertEquals("3", result.getCurrentRound());

        assertEquals("0", result.getTies());

        assertEquals("SCISSORS", result.getPlayer1LastMove());
        assertEquals("PAPER", result.getPlayer2LastMove());
        assertFalse(result.getRoundWinnerPlayerId().isEmpty());
        assertEquals("Player A", result.getRoundWinnerPlayerName());

        assertFalse(result.isGameFinished());

        assertTrue(result.getGameWinnerPlayerId().isEmpty());
        assertTrue(result.getGameWinnerPlayerName().isEmpty());

        assertNotEquals("", result.toString());
    }

    @Test
    void toResultDto_unfinishedGameWithTie_correctMapRoundFields() {
        Game game = createUnfinishedGameWithTie();

        ResultDto result = ResultMapper.toResultDto(game);

        assertNotNull(result);

        assertEquals("Player A", result.getPlayer1Name());
        assertFalse(result.getPlayer1Id().isEmpty());
        assertEquals("1", result.getPlayer1Score());

        assertEquals("Player B", result.getPlayer2Name());
        assertFalse(result.getPlayer2Id().isEmpty());
        assertEquals("0", result.getPlayer2Score());

        assertFalse(result.getGameId().isEmpty());
        assertEquals("4", result.getTotalRounds());
        assertEquals("3", result.getCurrentRound());

        assertEquals("1", result.getTies());

        assertEquals("PAPER", result.getPlayer1LastMove());
        assertEquals("PAPER", result.getPlayer2LastMove());
        assertTrue(result.getRoundWinnerPlayerId().isEmpty());
        assertTrue(result.getRoundWinnerPlayerName().isEmpty());

        assertFalse(result.isGameFinished());

        assertTrue(result.getGameWinnerPlayerId().isEmpty());
        assertTrue(result.getGameWinnerPlayerName().isEmpty());

        assertNotEquals("", result.toString());
    }

    @Test
    void toResultDto_finishedGameWithTie_correctMapAllFields() {
        Game game = createFinishedGameWithTie();

        ResultDto result = ResultMapper.toResultDto(game);

        assertNotNull(result);

        assertEquals("Player A", result.getPlayer1Name());
        assertFalse(result.getPlayer1Id().isEmpty());
        assertEquals("1", result.getPlayer1Score());

        assertEquals("Player B", result.getPlayer2Name());
        assertFalse(result.getPlayer2Id().isEmpty());
        assertEquals("1", result.getPlayer2Score());

        assertFalse(result.getGameId().isEmpty());
        assertEquals("4", result.getTotalRounds());
        assertEquals("5", result.getCurrentRound());

        assertEquals("2", result.getTies());

        assertEquals("PAPER", result.getPlayer1LastMove());
        assertEquals("PAPER", result.getPlayer2LastMove());
        assertTrue(result.getRoundWinnerPlayerId().isEmpty());
        assertTrue(result.getRoundWinnerPlayerName().isEmpty());

        assertTrue(result.isGameFinished());

        assertTrue(result.getGameWinnerPlayerId().isEmpty());
        assertTrue(result.getGameWinnerPlayerName().isEmpty());

        assertNotEquals("", result.toString());
    }

    @Test
    void toResultDto_finishedGameWithGameWinner_correctMapAllFields() {
        Game game = createFinishedGameWithRoundWinner();

        ResultDto result = ResultMapper.toResultDto(game);

        assertNotNull(result);

        assertEquals("Player A", result.getPlayer1Name());
        assertFalse(result.getPlayer1Id().isEmpty());
        assertEquals("3", result.getPlayer1Score());

        assertEquals("Player B", result.getPlayer2Name());
        assertFalse(result.getPlayer2Id().isEmpty());
        assertEquals("1", result.getPlayer2Score());

        assertFalse(result.getGameId().isEmpty());
        assertEquals("4", result.getTotalRounds());
        assertEquals("5", result.getCurrentRound());

        assertEquals("0", result.getTies());

        assertEquals("SCISSORS", result.getPlayer1LastMove());
        assertEquals("PAPER", result.getPlayer2LastMove());
        assertFalse(result.getRoundWinnerPlayerId().isEmpty());
        assertEquals("Player A", result.getRoundWinnerPlayerName());

        assertTrue(result.isGameFinished());

        assertFalse(result.getGameWinnerPlayerId().isEmpty());
        assertEquals("Player A", result.getGameWinnerPlayerName());

        assertNotEquals("", result.toString());

    }


    @Test
    void toResultDto_nullArgument_throwsIllegalArgumentException() {
        ResultMapper mapper = new ResultMapper(); // not necessary, just for JaCoCo completeness
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ResultMapper.toResultDto(null));
        assertEquals("Game argument must not be null", exception.getMessage());
    }


    private Game createUnfinishedGameWithRoundWinner() {
        Game game = createUnfinishedTestGame();

        RoundResult rResult = new RoundResult(Move.SCISSORS, Move.PAPER, game.getPlayer1());
        game.playRound(rResult);

        return game;
    }

    private Game createUnfinishedGameWithTie() {
        Game game = createUnfinishedTestGame();

        RoundResult rResult = new RoundResult(Move.PAPER, Move.PAPER, null);
        game.playRound(rResult);

        return game;
    }

    private Game createFinishedGameWithTie() {
        Game game = createUnfinishedTestGame();

        RoundResult rResult = new RoundResult(Move.PAPER, Move.PAPER, null);
        game.playRound(rResult);
        rResult = new RoundResult(Move.ROCK, Move.PAPER, game.getPlayer2());
        game.playRound(rResult);
        rResult = new RoundResult(Move.PAPER, Move.PAPER, null);
        game.playRound(rResult);

        return game;
    }

    private Game createFinishedGameWithRoundWinner() {
        Game game = createUnfinishedTestGame();

        RoundResult rResult = new RoundResult(Move.SCISSORS, Move.PAPER, game.getPlayer1());
        game.playRound(rResult);
        rResult = new RoundResult(Move.ROCK, Move.PAPER, game.getPlayer2());
        game.playRound(rResult);
        rResult = new RoundResult(Move.SCISSORS, Move.PAPER, game.getPlayer1());
        game.playRound(rResult);

        return game;
    }


    private Game createUnfinishedTestGame(){

        Player player1 = Player.newPlayerWithName("Player A");
        Player player2 = Player.newPlayerWithName("Player B");

        Game game = Game.newGame(player1, 4);
        game.setPlayerTwo(player2);


        RoundResult rResult = new RoundResult(Move.SCISSORS, Move.PAPER, player1);
        game.playRound(rResult);

        return game;
    }
}

