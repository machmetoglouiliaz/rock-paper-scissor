package com.mourat.rockpaperscissors.application.services.impl;

import com.mourat.rockpaperscissors.application.dto.ResultDto;
import com.mourat.rockpaperscissors.application.model.GameSession;
import com.mourat.rockpaperscissors.application.services.GameRunnerService;
import com.mourat.rockpaperscissors.application.services.GameSessionFactory;
import com.mourat.rockpaperscissors.domain.model.Game;
import com.mourat.rockpaperscissors.domain.model.Move;
import com.mourat.rockpaperscissors.domain.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GameRunnerServiceImpl implements GameRunnerService {

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
        return newGame.getId().toString();

    }

    /**
     * {@inheritDoc}
     * @implNote Players are stored in memory for reuse across sessions.
     */
    @Override
    public String createPlayer(String name) {
        if(name == null || name.trim().length() < 2 || name.trim().length() > 16){
            return errorMessageHandler("Name must be between 2 and 16 characters");
        }

        Player newPlayer = Player.newPlayerWithName(name);
        this.players.add(newPlayer);
        return newPlayer.getId().toString();
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
        gameSession.joinGame(player);
        this.activeGames.add(gameSession);
        return gameSession.getGame().getId().toString();
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
            return tDto;
        }
        else {
            tDto.setStatusMessage(errorMessageHandler(errorMessageHandler(tDto.getStatusMessage())));
            return tDto;
        }
    }

    /**
     * Finds a player object from the list by matching id.
     *
     * @param playerId string representation of a player id
     * @return the player if found, otherwise null
     */
    private Player findPlayerById(String playerId) {

        UUID id;

        try {
            id = UUID.fromString(playerId);
        } catch (Exception e) {
            System.out.println("Given string is not in a format of UUID");
            return null;
        }

        for (Player player : players) {
            if (player.getId().equals(id)) {
                return player;
            }
        }

        return null;
    }

    private String errorMessageHandler(String error){

        System.out.println(error);
        return "ERROR: " + error;
    }

}
