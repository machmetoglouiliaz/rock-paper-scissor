package com.mourat.rockpaperscissors.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void newGame_nullOwner_throwsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Game.newGame(null, 10));
        assertEquals("Game without a given player, can't be created", exception.getMessage());
    }

    @Test
    void newGame_zeroRounds_throwsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Game.newGame(Player.newPlayerWithName("test"), 0));
        assertEquals("Rounds of the game must be a positive number", exception.getMessage());
    }

    @Test
    void newGame_negativeRounds_throwsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Game.newGame(Player.newPlayerWithName("test"), -1));
        assertEquals("Rounds of the game must be a positive number", exception.getMessage());
    }

    @Test
    void newGame_excessRounds_throwsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Game.newGame(Player.newPlayerWithName("test"), 101));
        assertEquals("The game can have max 100 rounds!", exception.getMessage());
    }

    @Test
    void newGame_validArguments_createsGameSuccessfully() {
        Game game = Game.newGame(Player.newPlayerWithName("test"), 10);

        assertNotNull(game.getId());
        assertEquals(10, game.getRounds());
        assertNotNull(game.getRoundResults());
        assertEquals(10, game.getRoundResults().length);
        assertEquals(1, game.getActiveRound());
        assertEquals(0, game.getPlayer1Score());
        assertEquals(0, game.getPlayer2Score());
        assertEquals(0, game.getDraws());
        assertNull(game.getResult());

        assertNotNull(game.getPlayer1());
        assertEquals("test", game.getPlayer1().getName());
        assertNotNull(game.getPlayer1().getGamePlaying());

        assertNull(game.getPlayer2());
        assertEquals(GameState.IN_PROGRESS, game.getState());
    }

    @Test
    void playRound_nullRoundResult_throwsIllegalArgumentException() {
        Game game = getGameWithTwoPlayers(2);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> game.playRound(null));
        assertEquals("Round can be played only with valid rounds data", exception.getMessage());
        assertNull(game.getRoundResults()[0]);
    }

    @Test
    void playRound_excessRoundToFinnishedGame_throwsIllegalStateException() {
        Game game = getGameWithTwoPlayers(1);
        game.playRound(new RoundResult(Move.ROCK, Move.PAPER,game.getPlayer2()));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> game.playRound(new RoundResult(Move.ROCK, Move.PAPER,game.getPlayer2())));
        assertEquals("The game state is not in progress, can't play the round", exception.getMessage());
        assertEquals(1, game.getRoundResults().length);
    }

    @Test
    void playRound_validRoundPlayer2Wins_playRoundSuccessfully() {
        Game game = getGameWithTwoPlayers(1);
        GameResult result = game.playRound(new RoundResult(Move.ROCK, Move.PAPER,game.getPlayer2()));

        assertNotNull(result);
        assertNotNull(game.getRoundResults()[0]);
        assertEquals(2, game.getActiveRound());

        assertEquals(0, game.getPlayer1Score());
        assertEquals(1, game.getPlayer2Score());
        assertEquals(0, game.getDraws());
    }

    @Test
    void playRound_validRoundPlayer1Wins_playRoundSuccessfully() {
        Game game = getGameWithTwoPlayers(1);
        GameResult result = game.playRound(new RoundResult(Move.SCISSORS, Move.PAPER,game.getPlayer1()));

        assertNotNull(result);
        assertNotNull(game.getRoundResults()[0]);
        assertEquals(2, game.getActiveRound());

        assertEquals(1, game.getPlayer1Score());
        assertEquals(0, game.getPlayer2Score());
        assertEquals(0, game.getDraws());
    }

    @Test
    void playRound_validRoundTie_playRoundSuccessfully() {
        Game game = getGameWithTwoPlayers(1);
        GameResult result = game.playRound(new RoundResult(Move.PAPER, Move.PAPER, null));

        assertNotNull(result);
        assertNotNull(game.getRoundResults()[0]);
        assertEquals(2, game.getActiveRound());

        assertEquals(0, game.getPlayer1Score());
        assertEquals(0, game.getPlayer2Score());
        assertEquals(1, game.getDraws());
    }

    @Test
    void playRound_validRoundUnfinishedGame_playRoundSuccessfully() {
        Game game = getGameWithTwoPlayers(2);
        GameResult result = game.playRound(new RoundResult(Move.PAPER, Move.PAPER, null));

        assertNull(result);
        assertNotNull(game.getRoundResults()[0]);
        assertEquals(2, game.getActiveRound());

        assertEquals(0, game.getPlayer1Score());
        assertEquals(0, game.getPlayer2Score());
        assertEquals(1, game.getDraws());
    }

    @Test
    void setPlayerTwo_nullPlayer_throwsIllegalArgumentException() {
        Game game = Game.newGame(Player.newPlayerWithName("test"), 1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> game.setPlayerTwo(null));

        assertEquals("Joining player is not valid", exception.getMessage());
        assertNull(game.getPlayer2());
    }

    @Test
    void setPlayerTwo_validPlayerToWaitingGame_playerJoinsSuccessfully() {
        Game game = Game.newGame(Player.newPlayerWithName("test"), 1);
        game.setPlayerTwo(Player.newPlayerWithName("test2"));

        assertNotNull(game.getPlayer2());
        assertNotNull(game.getPlayer2().getGamePlaying());
    }

    @Test
    void setPlayerTwo_validPlayerToFullGame_throwsIllegalStateException() {
        Game game = getGameWithTwoPlayers(2);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> game.setPlayerTwo(Player.newPlayerWithName("test3")));

        assertEquals("Game already has 2 players", exception.getMessage());
        assertEquals("test2", game.getPlayer2().getName());
    }

    @Test
    void getLastRoundResult_fromFreshGame_returnsNull() {
        Game game = getGameWithTwoPlayers(1);
        RoundResult result = game.getLastRoundResult();

        assertNull(result);
    }

    @Test
    void getLastRoundResult_fromPlayedGame_returnsLastPlayedRoundsRecord() {
        Game game = getGameWithTwoPlayers(1);
        game.playRound(new RoundResult(Move.PAPER, Move.ROCK, game.getPlayer1()));
        RoundResult result = game.getLastRoundResult();

        assertNotNull(result);
        assertEquals(Move.PAPER, result.player1Move());
        assertEquals(Move.ROCK, result.player2Move());
        assertEquals(game.getPlayer1(), result.winner());
    }

    private static Game getGameWithTwoPlayers(int rounds) {
        Game game = Game.newGame(Player.newPlayerWithName("test"), rounds);
        game.setPlayerTwo(Player.newPlayerWithName("test2"));
        return game;
    }
}