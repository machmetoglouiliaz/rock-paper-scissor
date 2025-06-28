package com.mourat.rockpaperscissors.application.services.impl;

import com.mourat.rockpaperscissors.application.dto.ResultDto;
import com.mourat.rockpaperscissors.application.services.GameRunnerService;
import com.mourat.rockpaperscissors.application.services.GameSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class GameRunnerServiceImplTest {

    @Autowired
    GameSessionFactory sessionFactory;
    GameRunnerService service;

    @BeforeEach
    void setUp(){
        service = new GameRunnerServiceImpl(sessionFactory);
    }

    @Test
    void createGame_invalidPlayerId_returnsErrorMessage() {
        String errorMessage = service.createGame("randomText or empty string as ID", 10);

        assertEquals("ERROR: Player with id \"randomText or empty string as ID\" doesn't exist", errorMessage);
    }

    @Test
    void createGame_zeroRounds_returnsErrorMessage() {
        String playerId = service.createPlayer("testPlayer");
        String errorMessage = service.createGame(playerId, 0);

        assertEquals("ERROR: Rounds must be a positive number", errorMessage);
    }

    @Test
    void createGame_negativeRounds_returnsErrorMessage() {
        String playerId = service.createPlayer("testPlayer");
        String errorMessage = service.createGame(playerId, -1);

        assertEquals("ERROR: Rounds must be a positive number", errorMessage);
    }

    @Test
    void createGame_validArguments_createsNewGameWithReturnedId() {
        String playerId = service.createPlayer("testPlayer");
        String gameId = service.createGame(playerId, 1);

        assertNotEquals("ERROR:", gameId.substring(0, 6));
    }

    @Test
    void createPlayer_nullName_returnsErrorMessage() {
        String errorMessage = service.createPlayer(null);

        assertEquals("ERROR: Player name must be between 2 and 16 characters", errorMessage);
    }

    @Test
    void createPlayer_emptyName_returnsErrorMessage() {
        String errorMessage = service.createPlayer("");

        assertEquals("ERROR: Player name must be between 2 and 16 characters", errorMessage);
    }

    @Test
    void createPlayer_whiteSpaceName_returnsErrorMessage() {
        String errorMessage = service.createPlayer("      ");

        assertEquals("ERROR: Player name must be between 2 and 16 characters", errorMessage);
    }

    @Test
    void createPlayer_shortName_returnsErrorMessage() {
        String errorMessage = service.createPlayer(" a  ");

        assertEquals("ERROR: Player name must be between 2 and 16 characters", errorMessage);
    }

    @Test
    void createPlayer_longName_returnsErrorMessage() {
        String errorMessage = service.createPlayer("1t2t3t4t5t6t7t8t9t");

        assertEquals("ERROR: Player name must be between 2 and 16 characters", errorMessage);
    }

    @Test
    void createPlayer_validName_createsPlayerWithReturnedId() {
        String playerId = service.createPlayer("testPlayer");

        assertNotEquals("ERROR:", playerId.substring(0, 6));
    }

    @Test
    void joinGame_nullId_returnsErrorMessage() {
        String error = service.joinGame(null);

        assertEquals("ERROR: Player id is null", error);
    }

    @Test
    void joinGame_invalidId_returnsErrorMessage() {
        String playerId = "Random string as id";
        String error = service.joinGame(playerId);

        assertEquals("ERROR: Player with id \"" + playerId + "\" doesn't exist", error);
    }

    @Test
    void joinGame_noGamesToJoin_returnsErrorMessage() {
        String playerId = service.createPlayer("player A");
        String error = service.joinGame(playerId);

        assertEquals("ERROR: There are no games to join", error);
    }

    @Test
    void joinGame_joinedPlayer_returnsErrorMessage() {
        String playerId = service.createPlayer("player A");
        String gameId = service.createGame(playerId, 10);
        String error = service.joinGame(playerId);

        assertEquals("ERROR: Player with id \"" + playerId + "\" is already in a session", error);
    }

    @Test
    void joinGame_nonExistingPlayer_returnsErrorMessage() {
        String playerId = "0c342b12-b69b-4281-8b46-c1dc3627aa89";
        String error = service.joinGame(playerId);

        assertEquals("ERROR: Player with id \"" + playerId + "\" doesn't exist", error);
    }

    @Test
    void joinGame_validSecondPlayer_joinsGameWithReturnedId() {
        String player1Id = service.createPlayer("player A");
        String createdGameId = service.createGame(player1Id, 10);
        String player2Id = service.createPlayer("player A");
        String joiningGameId = service.joinGame(player2Id);

        assertEquals(joiningGameId, createdGameId);
    }

    @Test
    void makeMove_nullPlayerId_returnsDtoWithErrorMessage() {
        ResultDto errorDto = service.makeMove(null, "PAPER");

        assertNotNull(errorDto);
        assertEquals("ERROR: Player id is null", errorDto.getStatusMessage());
    }

    @Test
    void makeMove_nonExistingPlayerId_returnsDtoWithErrorMessage() {
        String playerId = "d6e17725-ebaa-4bdf-92af-04361b7e8544";
        ResultDto errorDto = service.makeMove(playerId, "PAPER");

        assertNotNull(errorDto);
        assertEquals("ERROR: Player with id \"" + playerId + "\" doesn't exist", errorDto.getStatusMessage());
    }

    @Test
    void makeMove_nonExistingMove_returnsDtoWithErrorMessage() {
        String playerId = service.createPlayer("testPlayer");
        String move = "Random Text";
        ResultDto errorDto = service.makeMove(playerId, move);

        assertNotNull(errorDto);
        assertEquals("ERROR: Invalid move: \"" + move + "\"", errorDto.getStatusMessage());
    }

    @Test
    void makeMove_beforeJoin_returnsDtoWithErrorMessage() {
        String playerId = service.createPlayer("testPlayer");
        String move = "PAPER";
        ResultDto errorDto = service.makeMove(playerId, move);

        assertNotNull(errorDto);
        assertEquals("ERROR: Player with id \"" + playerId + "\" is not joined to any game", errorDto.getStatusMessage());
    }

    @Test
    void makeMove_validArgumentsInternalError_returnsDtoWithErrorMessageFromDeeperLayers() {
        String playerId = service.createPlayer("testPlayer");
        service.createGame(playerId, 10);
        String move = "PAPER";
        ResultDto errorDto = service.makeMove(playerId, move);

        assertNotNull(errorDto);
        assertFalse(errorDto.isSuccess());
        assertNotEquals("Success", errorDto.getStatusMessage());
    }

    @Test
    void makeMove_validArguments_returnsDtoWithSuccessMessage() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        String playerId = service.createPlayer("testPlayer");
        service.createGame(playerId, 10);
        String player2Id = service.createPlayer("testPlayer");
        service.joinGame(player2Id);
        String move = "PAPER";
        Future<ResultDto> future = executorService.submit(() -> service.makeMove(playerId, move));
        executorService.submit(() -> service.makeMove(player2Id, move));

        ResultDto dto = null;
        try {
            dto = future.get();
        } catch (Exception e) {
            System.out.println("An unexpected error occurred on testing thread");
        }

        assertNotNull(dto);
        assertTrue(dto.isSuccess());
        assertEquals("Success", dto.getStatusMessage());
    }
}