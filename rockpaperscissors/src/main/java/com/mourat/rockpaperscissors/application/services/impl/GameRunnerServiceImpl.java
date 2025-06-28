package com.mourat.rockpaperscissors.application.services.impl;

import com.mourat.rockpaperscissors.application.dto.ResultDto;
import com.mourat.rockpaperscissors.application.model.GameSession;
import com.mourat.rockpaperscissors.application.services.GameRunnerService;
import com.mourat.rockpaperscissors.application.services.GameSessionFactory;
import com.mourat.rockpaperscissors.domain.model.Game;
import com.mourat.rockpaperscissors.domain.model.Move;
import com.mourat.rockpaperscissors.domain.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GameRunnerServiceImpl implements GameRunnerService {

    private static final Logger logger = LoggerFactory.getLogger(GameRunnerServiceImpl.class);

    private final List<Player> players;
    private final List<GameSession> activeGames;
    private final List<GameSession> completeGames;
    private final List<GameSession> waitingGames;

    private final GameSessionFactory gameSessionFactory;

    @Autowired
    public GameRunnerServiceImpl(GameSessionFactory sessionFactory){
        this.gameSessionFactory = sessionFactory;

        this.players = new ArrayList<>();
        this.activeGames = new ArrayList<>();
        this.completeGames = new ArrayList<>();
        this.waitingGames = new ArrayList<>();
    }


    /**
     * {@inheritDoc}
     * @implNote The game is added to the waiting list immediately after creation.
     */
    @Override
    public String createGame(String playerId, int rounds) {
        Player player = this.findPlayerById(playerId);
        if (player == null) {
            return errorMessageHandler("Player with id \"" + playerId + "\" doesn't exist");
        }

        if (rounds < 1) {
            return errorMessageHandler("Rounds must be a positive number");
        }

        Game newGame = Game.newGame(player, rounds);
        GameSession session = gameSessionFactory.createSession(player, newGame);
        this.waitingGames.add(session);

        String gameId = newGame.getId().toString();
        logger.info("New game created successfully with id \"{}\" by the player \"{}\": \"{}\"", gameId, player.getName(), playerId);
        return gameId;

    }

    /**
     * {@inheritDoc}
     * @implNote Players are stored in memory for reuse across sessions.
     */
    @Override
    public String createPlayer(String name) {
        if(name == null || name.trim().length() < 2 || name.trim().length() > 16){
            return errorMessageHandler("Player name must be between 2 and 16 characters");
        }

        Player newPlayer = Player.newPlayerWithName(name);
        this.players.add(newPlayer);

        String playerId = newPlayer.getId().toString();
        logger.info("New player created successfully with name \"{}\" and id \"{}\"", name, playerId);
        return playerId;
    }

    /**
     * {@inheritDoc}
     * @implNote Players are not allowed to join if already in another session.
     */
    @Override
    public String joinGame(String playerId) {
        if (playerId == null) {
            return errorMessageHandler("Player id is null");
        }

        Player player = this.findPlayerById(playerId);
        if (player == null) {
            return errorMessageHandler("Player with id \"" + playerId + "\" doesn't exist");
        }

        if (waitingGames.isEmpty()) {
            return errorMessageHandler("There are no games to join");
        }

        if (player.getGameSession() != null) {
            return errorMessageHandler("Player with id \"" + playerId + "\" is already in a session");
        }

        GameSession gameSession = this.waitingGames.removeFirst();
        if(!gameSession.joinGame(player)){
            logger.error("This code segment is unreachable by any normal flow, must have an internal domain level corruption");
            throw new IllegalStateException("An internal error occurred");
        }
        this.activeGames.add(gameSession);

        String gameId = gameSession.getGame().getId().toString();
        logger.info("Player with name \"{}\" and id \"{}\" joined to the game with id \"{}\" successfully", player.getName(), playerId, gameId);
        return gameId;
    }

    /**
     * {@inheritDoc}
     * @implNote This method handles both input validation and domain interaction.
     */
    @Override
    public ResultDto makeMove(String playerId, String moveString) {
        ResultDto dto = new ResultDto();
        dto.setSuccess(false);

        if (playerId == null) {
            dto.setStatusMessage(errorMessageHandler("Player id is null"));
            return dto;
        }

        Player player = findPlayerById(playerId);
        if (player == null) {
            dto.setStatusMessage(errorMessageHandler("Player with id \"" + playerId + "\" doesn't exist"));
            return dto;
        }

        Move move;
        try {
            move = Move.valueOf(moveString);
        } catch (IllegalArgumentException e) {
            dto.setStatusMessage(errorMessageHandler("Invalid move: \"" + moveString + "\""));
            return dto;
        }

        GameSession gameSession = player.getGameSession();
        if (gameSession == null) {
            dto.setStatusMessage(errorMessageHandler("Player with id \"" + playerId + "\" is not joined to any game"));
            return dto;
        }

        ResultDto tDto = gameSession.makeMove(player, move);
        if(tDto.isSuccess()) {
            logger.debug("Player \"{}\":\"{}\" played \"{}\" successfully...", player.getName(), playerId, move);
            return tDto;
        }
        else {
            logger.debug("Player \"{}\":\"{}\" can't play \"{}\"...", player.getName(), playerId, move);
            tDto.setStatusMessage(errorMessageHandler(tDto.getStatusMessage()));
            return tDto;
        }
    }

    /**
     * Finds a player object from the list with a matching id.
     *
     * @param playerId string representation of a player id
     * @return the player if found, otherwise null
     */
    private Player findPlayerById(String playerId) {

        UUID id;

        try {
            id = UUID.fromString(playerId);
        } catch (Exception e) {
            logger.warn("The \"{}\" is not in a format of UUID", playerId);
            return null;
        }

        for (Player player : players) {
            if (player.getId().equals(id)) {
                return player;
            }
        }

        return null;
    }

    /**
     * Logs the error and sends it back formatted
     *
     * @param error message to be processed
     * @return formatted error message
     */
    private String errorMessageHandler(String error){

        logger.warn(error);
        return "ERROR: " + error;
    }

}
