package com.mourat.rockpaperscissors;

import com.mourat.rockpaperscissors.application.dto.ResultDto;
import com.mourat.rockpaperscissors.application.services.GameRunnerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.processing.Generated;
import java.util.Random;

@SpringBootApplication
public class RockpaperscissorsApplication {

	private GameRunnerService gameRunnerService;
	private final int iterations = 2;

	public static void main(String[] args) {
		SpringApplication.run(RockpaperscissorsApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(GameRunnerService gameRunnerService){

		this.gameRunnerService = gameRunnerService;

		return runner -> simulateGame();
	}

	private void simulateGame(){

		Thread bot1 = new Thread(this::botOne);
		Thread bot2 = new Thread(this::botTwo);

		bot1.start();
		bot2.start();

	}


	public void botTwo(){

		String[] moveSet = {"ROCK", "PAPER", "SCISSORS"};
		Random random = new Random();

		String playerId = gameRunnerService.createPlayer("Player B");;

		ResultDto result;
		String gameId;

		System.out.println("I'm bot 2");
		System.out.println("BOT 2: player id:" + playerId);

		for(int i = 0; i < iterations; i++){
			System.out.println("BOT 2: Iteration: " + i);

			do {
				try {
					Thread.sleep(100);
				} catch (InterruptedException _) {
				}
				gameId = gameRunnerService.joinGame(playerId);
			} while (gameId.isEmpty());
			System.out.println("BOT 2: game id:" + gameId);


			do {
				result = gameRunnerService.makeMove(playerId, moveSet[random.nextInt(3)]);
			} while (result == null || Integer.parseInt(result.getCurrentRound()) != Integer.parseInt(result.getTotalRounds()) + 1);

			System.out.println("BOT 2: \n" + result +"\n\n");
		}

	}


	public void botOne(){

		int rounds = 100;
		String playerId = gameRunnerService.createPlayer("Player A");
		String gameId;

		ResultDto result;

		System.out.println("I'm bot 1");
		System.out.println("BOT 1: player id:" + playerId);
		for(int i = 0; i < iterations; i++) {
			System.out.println("BOT 1: Iteration: " + i);

			gameId = gameRunnerService.createGame(playerId, rounds);
			System.out.println("BOT 1: game id:" + gameId);
			do {
				result = gameRunnerService.makeMove(playerId, "PAPER");
				if(result == null){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException _) {}
                }
			} while (result == null || Integer.parseInt(result.getCurrentRound()) != Integer.parseInt(result.getTotalRounds()) + 1);
		}

	}
}
