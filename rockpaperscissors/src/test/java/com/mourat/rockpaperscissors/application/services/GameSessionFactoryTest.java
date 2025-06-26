package com.mourat.rockpaperscissors.application.services;

import com.mourat.rockpaperscissors.application.model.GameSession;
import com.mourat.rockpaperscissors.application.model.SessionState;
import com.mourat.rockpaperscissors.domain.model.Game;
import com.mourat.rockpaperscissors.domain.model.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GameSessionFactoryTest {

    @Autowired
    GameSessionFactory sessionFactory;

    @Test
    void createSession_nullOwner_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sessionFactory.createSession(null, Game.newGame(Player.newPlayerWithName("test"), 2)));

        assertEquals("Creator of the session cant be invalid", exception.getMessage());
    }

    @Test
    void createSession_nullGame_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sessionFactory.createSession(Player.newPlayerWithName("test"), null));

        assertEquals("Game of the session cant be invalid", exception.getMessage());
    }

    @Test
    void createSession_validArguments_createsNewGameSessionSuccessfully() {
        Player player = Player.newPlayerWithName("test");
        Game game = Game.newGame(player, 2);
        GameSession session = sessionFactory.createSession(player, game);

        assertEquals(game, session.getGame());
        assertNotNull(session.getPlayer1());
        assertEquals("test", session.getPlayer1().getName());
        assertEquals(session, session.getPlayer1().getGameSession());

        assertEquals(SessionState.WAITING_FOR_JOIN, session.getState());
    }
}