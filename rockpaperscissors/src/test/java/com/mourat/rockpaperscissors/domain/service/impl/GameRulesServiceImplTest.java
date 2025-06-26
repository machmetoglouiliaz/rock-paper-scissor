package com.mourat.rockpaperscissors.domain.service.impl;

import com.mourat.rockpaperscissors.domain.model.Move;
import com.mourat.rockpaperscissors.domain.model.Player;
import com.mourat.rockpaperscissors.domain.model.RoundResult;
import com.mourat.rockpaperscissors.domain.service.GameRulesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GameRulesServiceImplTest {

    @Autowired
    GameRulesService service;

    @ParameterizedTest
    @CsvSource({
            "ROCK, SCISSORS, 1",
            "ROCK, PAPER, 2",
            "ROCK, ROCK, 0",
            "PAPER, ROCK, 1",
            "PAPER, SCISSORS, 2",
            "PAPER, PAPER, 0",
            "SCISSORS, ROCK, 2",
            "SCISSORS, PAPER, 1",
            "SCISSORS, SCISSORS, 0"
    })
    void checkRoundWinner_allLegalCombinations(Move player1Move, Move player2Move, int expectedWinner) {
        Player player1 = Player.newPlayerWithName("test");
        Player player2 = Player.newPlayerWithName("test2");

        RoundResult result = service.checkRoundWinner(player1, player1Move, player2, player2Move);

        if (expectedWinner == 1) {
            assertEquals(player1, result.winner());
        } else if (expectedWinner == 2) {
            assertEquals(player2, result.winner());
        } else {
            assertNull(result.winner());
        }
    }

    @Test
    void checkRoundWinner_nullPlayer1_throwsIllegalArgumentException() {
        Player player = Player.newPlayerWithName("test");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.checkRoundWinner(null, Move.SCISSORS, player, Move.ROCK));

        assertEquals("Players can't be null.", exception.getMessage());

    }

    @Test
    void checkRoundWinner_nullPlayer2_throwsIllegalArgumentException() {
        Player player = Player.newPlayerWithName("test");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.checkRoundWinner(player, Move.SCISSORS, null, Move.ROCK));

        assertEquals("Players can't be null.", exception.getMessage());

    }

    @Test
    void checkRoundWinner_nullPlayer1Move_throwsIllegalArgumentException() {
        Player player1 = Player.newPlayerWithName("test");
        Player player2 = Player.newPlayerWithName("test2");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.checkRoundWinner(player1, null, player2, Move.ROCK));

        assertEquals("Moves can't be null.", exception.getMessage());

    }

    @Test
    void checkRoundWinner_nullPlayer2Move_throwsIllegalArgumentException() {
        Player player1 = Player.newPlayerWithName("test");
        Player player2 = Player.newPlayerWithName("test2");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.checkRoundWinner(player1, Move.ROCK, player2, null));

        assertEquals("Moves can't be null.", exception.getMessage());

    }
}