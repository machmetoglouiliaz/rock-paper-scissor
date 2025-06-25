package com.mourat.rockpaperscissors;

import com.mourat.rockpaperscissors.application.dto.ResultDto;
import com.mourat.rockpaperscissors.application.services.GameRunnerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class RockpaperscissorsApplication {

	GameRunnerService gameRunnerService;

	public static void main(String[] args) {
		SpringApplication.run(RockpaperscissorsApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(GameRunnerService gameRunnerService){

		this.gameRunnerService = gameRunnerService;

		return runner -> {
			simulateGame();
		};
	}

	private void simulateGame(){

		Thread bot1 = new Thread(this::botOne);
		Thread bot2 = new Thread(this::botTwo);

		bot1.start();
		bot2.start();

	}

	public void botTwo(){
		int rounds = 100;
		String moveSet[] = {"ROCK", "PAPER", "SCISSORS"};
		Random random = new Random();

		System.out.println("I'm bot 2");
		String playerId = gameRunnerService.createPlayer("Player B");
		System.out.println("BOT 2: player id:" + playerId);
		String gameId = gameRunnerService.createGame(playerId, rounds);
		System.out.println("BOT 2: game id:" + gameId);

		ResultDto result;
		do {
			result = gameRunnerService.makeMove(playerId, moveSet[random.nextInt(3)]);
		}while(Integer.parseInt(result.getCurrentRound()) != Integer.parseInt(result.getTotalRounds()) + 1);

		System.out.println("BOT 2: \n" + result);

	}

	public void botOne(){
		System.out.println("I'm bot 1");
		String playerId = gameRunnerService.createPlayer("Player A");
		System.out.println("BOT 1: player id:" + playerId);

		String gameId;
		do {
			gameId = gameRunnerService.joinGame(playerId);
		}while(gameId.isEmpty());

		System.out.println("BOT 1: game id:" + gameId);

		ResultDto result;
		do {
			result = gameRunnerService.makeMove(playerId, "PAPER");
		}while(Integer.parseInt(result.getCurrentRound()) != Integer.parseInt(result.getTotalRounds()) + 1);

	}
}
