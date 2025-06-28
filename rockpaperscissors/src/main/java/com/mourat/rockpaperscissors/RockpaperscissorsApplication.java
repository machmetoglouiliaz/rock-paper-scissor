package com.mourat.rockpaperscissors;

import com.mourat.rockpaperscissors.application.dto.ResultDto;
import com.mourat.rockpaperscissors.application.services.GameRunnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.processing.Generated;
import java.util.Random;

@SpringBootApplication
public class RockpaperscissorsApplication {

    private static final Logger logger = LoggerFactory.getLogger(RockpaperscissorsApplication.class);

    private GameRunnerService gameRunnerService;
    private final int iterations = 1;
    int roundsPerGame = 100; // This number has no meaning for game joining players - isCreatingGame = false

    public static void main(String[] args) {
        SpringApplication.run(RockpaperscissorsApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(GameRunnerService gameRunnerService) {

        this.gameRunnerService = gameRunnerService;

        return runner -> simulateGame();
    }

    private void simulateGame() {

        Thread bot1 = new Thread(this::botOne);
        Thread bot2 = new Thread(this::botTwo);

        bot1.start();
        bot2.start();

    }


    public void botTwo() {

        String[] moveSet = {"ROCK", "PAPER", "SCISSORS"};
        String playerName = "Player B";
        String botName = "BOT 2";
        boolean isCreatingGame = false;

        iterateSimulationFor(botName, playerName, isCreatingGame, roundsPerGame, moveSet);

    }


    public void botOne() {

        String[] moveSet = {"PAPER"};
        String playerName = "Player A";
        String botName = "BOT 1";
        boolean isCreatingGame = true;

        iterateSimulationFor(botName, playerName, isCreatingGame, roundsPerGame, moveSet);

    }

    private String initializePlayerFor(String botName, String playerName) {

        logger.info("{}: Initializing...", botName);
        logger.info("{}: Creating player with name {}", botName, playerName);
        String playerId = gameRunnerService.createPlayer(playerName);
        if (playerId.contains("ERROR")) {
            logger.info("{}: Player creation ended with error: {}  - Check error.log for more info!", botName, playerId);
            return playerId;
        }
        logger.info("{}: Player created successfully!", botName);
        logger.info("{}: player id: {}", botName, playerId);

        return playerId;
    }

    private ResultDto playGame(String playerId, String[] moveSet) throws Exception {

        Random random = new Random();
        ResultDto result;

        do {
            result = gameRunnerService.makeMove(playerId, moveSet[random.nextInt(moveSet.length)]);
            if (result == null || !result.isSuccess()) {
                sleepABit();
            }
        } while (result == null || !result.isGameFinished());

        return result;
    }

    private String createOrJoinGame(boolean isCreator, String playerId, int rounds) throws Exception {
        String gameId = "";

        if (isCreator) {
            gameId = gameRunnerService.createGame(playerId, rounds);
        } else {
            while (gameId.isEmpty() || gameId.contains("ERROR")) {
                gameId = gameRunnerService.joinGame(playerId);
                if (gameId.isEmpty() || gameId.contains("ERROR")) {
                    sleepABit();
                }
            }
        }

        return gameId;
    }

    private void iterateSimulationFor(String botName, String playerName, boolean isCreator, int rounds, String[] moveSet) {
        String gameId;
        String playerId = initializePlayerFor(botName, playerName);
        ResultDto result;

        for (int i = 0; i < iterations; i++) {
            logger.info("{}: Iteration: {}", botName, i);

            logger.info("{}: {} game...", botName, (isCreator ? "Creating new" : "Joining to"));
            try {
                gameId = createOrJoinGame(isCreator, playerId, rounds);
				logger.info("{}: Entered game with id: {}", botName, gameId);

				result = playGame(playerId, moveSet);
				logger.info("{}: Game ended with message {}", botName, result.getStatusMessage());
            } catch (Exception e) {
                logger.error("{}: {}",botName, e.getMessage());
                return;
            }

            if (isCreator) {
                logger.info("{}: \n{}\n\n", botName, result);
                tyntecOutputFormater(result);
            }
        }
    }

	private void sleepABit() throws Exception {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			throw new Exception("Something went wrong with sleep");
		}
	}

    private void tyntecOutputFormater(ResultDto result){
        System.out.println("\"" + result.getPlayer1Name() + " wins " + result.getPlayer1Score() + " of " + result.getTotalRounds() + " games\"");
        System.out.println("\"" + result.getPlayer2Name() + " wins " + result.getPlayer2Score() + " of " + result.getTotalRounds() + " games\"");
        System.out.println("\"Tie:  " + result.getTies() + " of " + result.getTotalRounds() + " games\"");
    }
}
