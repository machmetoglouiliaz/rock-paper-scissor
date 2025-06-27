package com.mourat.rockpaperscissors.application.model;

import com.mourat.rockpaperscissors.application.dto.ResultDto;
import com.mourat.rockpaperscissors.application.services.GameSessionFactory;
import com.mourat.rockpaperscissors.domain.model.Game;
import com.mourat.rockpaperscissors.domain.model.Move;
import com.mourat.rockpaperscissors.domain.model.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GameSessionTest {

    @Autowired
    GameSessionFactory sessionFactory;
    GameSession session;

    @BeforeEach
    void setUp(){
        Player player1 = Player.newPlayerWithName("testPlayer1");
        Game game = Game.newGame(player1, 1);
        session = sessionFactory.createSession(player1, game);
    }

    @Test
    void joinGame_nullPlayer_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> session.joinGame(null));

        assertEquals("Player must be valid to join a game", exception.getMessage());
    }

    @Test
    void joinGame_morePlayersThanExpected_failsWithFalse() {
        Player player2 = Player.newPlayerWithName("testPlayer2");
        Player player3 = Player.newPlayerWithName("testPlayer3");
        boolean firstJoin = session.joinGame(player2);
        boolean secondJoin = session.joinGame(player3);

        assertTrue(firstJoin);
        assertFalse(secondJoin);
    }

    @Test
    void joinGame_samePlayer_failsWithFalse() {
        boolean result = session.joinGame(session.getPlayer1());

        assertFalse(result);
        assertNull(session.getPlayer2());
    }

    @Test
    void makeMove_nullPlayer_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> session.makeMove(null, Move.PAPER));

        assertEquals("Player must be valid" ,exception.getMessage());

    }

    @Test
    void makeMove_nullMove_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> session.makeMove(Player.newPlayerWithName("testPlayer2"), null));

        assertEquals("Move must be a valid move" ,exception.getMessage());
    }

    @Test
    void makeMove_beforeJoin_returnsNull() {
        ResultDto result = session.makeMove(session.getPlayer1(), Move.PAPER);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNull(session.getPlayer2());
    }

    @Test
    void makeMove_foreignPlayer_returnsNull() {
        makeTwoPlayerSession();
        Player foreignPlayer = Player.newPlayerWithName("foreignPlayer");
        ResultDto result = session.makeMove(foreignPlayer, Move.PAPER);

        assertNotNull(result);
        assertFalse(result.isSuccess());
    }

    @Test
    void makeMove_bothPlayers_returnsResultSuccessfully() {
        makeTwoPlayerSession();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<ResultDto> future1 = executor.submit(() -> session.makeMove(session.getPlayer1(), Move.PAPER));
        Future<ResultDto> future2 = executor.submit(() -> session.makeMove(session.getPlayer2(), Move.PAPER));

        ResultDto result1 = null;
        ResultDto result2 = null;

        try {
            result1 = future1.get();
            result2 = future2.get();
        }catch(Exception e){
            System.out.println("Something went wrong with testing threads");
        }

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1, result2);
    }

    @Test
    void makeMove_doubleMovePlayer1_returnsNull() {
        makeTwoPlayerSession();

        ExecutorService executor = Executors.newFixedThreadPool(1);
        ResultDto result1 = null;

        Future<ResultDto> future1;
        try {
            future1 = executor.submit(() -> session.makeMove(session.getPlayer1(), Move.PAPER));

            Thread.sleep(100);

            future1.cancel(true);
            future1 = executor.submit(() -> session.makeMove(session.getPlayer1(), Move.PAPER));

            result1 = future1.get();
        }catch(Exception e){
            System.out.println("Something went wrong with testing threads");
        }

        assertNotNull(result1);
        assertFalse(result1.isSuccess());
    }

    @Test
    void makeMove_doubleMovePlayer2_returnsNull() {
        makeTwoPlayerSession();

        ExecutorService executor = Executors.newFixedThreadPool(1);
        ResultDto result1 = null;

        Future<ResultDto> future1;
        try {
            future1 = executor.submit(() -> session.makeMove(session.getPlayer2(), Move.PAPER));

            Thread.sleep(100);

            future1.cancel(true);
            future1 = executor.submit(() -> session.makeMove(session.getPlayer2(), Move.PAPER));

            result1 = future1.get();
        }catch(Exception e){
            System.out.println("Something went wrong with testing threads");
        }

        assertNotNull(result1);
        assertFalse(result1.isSuccess());
    }

    void makeTwoPlayerSession(){
        Player player = Player.newPlayerWithName("testPlayer2");
        session.joinGame(player);
    }


}