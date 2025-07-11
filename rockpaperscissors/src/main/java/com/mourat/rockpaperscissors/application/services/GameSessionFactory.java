package com.mourat.rockpaperscissors.application.services;

import com.mourat.rockpaperscissors.application.model.GameSession;
import com.mourat.rockpaperscissors.domain.model.Game;
import com.mourat.rockpaperscissors.domain.model.Player;
import com.mourat.rockpaperscissors.domain.service.GameRulesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Factory class responsible for creating {@link GameSession} instances.
 * <p>
 * Injects the required domain-level {@link GameRulesService} to ensure all created sessions
 * are fully configured and ready for use.
 */
@Service
public class GameSessionFactory {

    private static final Logger logger = LoggerFactory.getLogger(GameSessionFactory.class);

    private final GameRulesService gameRulesService;

    /**
     * Constructs a session factory with a game rules service dependency.
     *
     * @param gameRulesService the service used for evaluating game rules
     */
    @Autowired
    public GameSessionFactory (GameRulesService gameRulesService){
        this.gameRulesService = gameRulesService;
    }

    /**
     * Creates and returns a new {@link GameSession} for the given player and game.
     *
     * @param owner the {@link Player} who creates the game session
     * @param game the {@link Game} instance to associate with the session
     * @return a new {@link GameSession} instance
     */
    public GameSession createSession(Player owner, Game game){
        if(owner == null){
            logger.error("Session creation in session factory can't continue, owner of the session is null and it is not allowed");
            throw new IllegalArgumentException("Creator of the session cant be invalid");
        }
        if(game == null){
            logger.error("Session creation in session factory can't continue, game of the session is null and it is not allowed");
            throw new IllegalArgumentException("Game of the session cant be invalid");
        }

        logger.debug("Session factory creating a new session for the game \"{}\" by player \"{}\":\"{}\"...", game.getId(), owner.getName(), owner.getId());
        return new GameSession(owner, game, gameRulesService);
    }
}
