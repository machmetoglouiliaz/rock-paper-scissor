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
            System.out.println("Player with id " + playerId + " doesn't exist");
            return "";
        }

        try {
            Game newGame = Game.newGame(player, rounds);
            GameSession session = gameSessionFactory.createSession(player, newGame);
            this.waitingGames.add(session);
            return newGame.getId().toString();
        } catch (Exception e) {
            System.out.println("Failed to create game: " + e.getMessage());
            return "";
        }
    }

    /**
     * {@inheritDoc}
     * @implNote Players are stored in memory for reuse across sessions.
     */
    @Override
    public String createPlayer(String name) {
        try {
            Player newPlayer = Player.newPlayerWithName(name);
            this.players.add(newPlayer);
            return newPlayer.getId().toString();
        } catch (Exception e) {
            System.out.println("Failed to create player: " + e.getMessage());
            return "";
        }
    }

    /**
     * {@inheritDoc}
     * @implNote Players are not allowed to join if already in another session.
     */
    @Override
    public String joinGame(String playerId) {
        Player player = this.findPlayerById(playerId);
        if (player == null) {
            System.out.println("Player with id " + playerId + " doesn't exist");
            return "";
        }

        if (waitingGames.isEmpty()) {
            System.out.println("There are no games to join");
            return "";
        }

        if (player.getGameSession() != null) {
            System.out.println("Player with id " + playerId + " is already in a session");
            return "";
        }

        GameSession gameSession = this.waitingGames.removeFirst();
        boolean joined = gameSession.joinGame(player);

        if (!joined) {
            System.out.println("Failed to join game");
            return "";
        }

        this.activeGames.add(gameSession);
        return gameSession.getGame().getId().toString();
    }

    /**
     * {@inheritDoc}
     * @implNote This method handles both input validation and domain interaction.
     */
    @Override
    public ResultDto makeMove(String playerId, String moveString) {
        Player player = findPlayerById(playerId);
        if (player == null) {
            System.out.println("Player with id " + playerId + " doesn't exist");
            return null;
        }

        Move move;
        try {
            move = Move.valueOf(moveString);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid move: " + moveString);
            return null;
        }

        GameSession gameSession = player.getGameSession();
        if (gameSession == null) {
            System.out.println("Player with id " + playerId + " is not joined to any game");
            return null;
        }

        return gameSession.makeMove(player, move);
    }

    /**
     * Finds a player object from the list by matching id.
     *
     * @param playerId string representation of a player id
     * @return the player if found, otherwise null
     */
    private Player findPlayerById(String playerId) {

        UUID id = UUID.fromString(playerId);

        for (Player player : players) {
            if (player.getId().equals(id)) {
                return player;
            }
        }

        return null;
    }

}
