package com.mourat.rockpaperscissors.application.services;

import com.mourat.rockpaperscissors.application.model.GameSession;
import com.mourat.rockpaperscissors.domain.model.Game;
import com.mourat.rockpaperscissors.domain.model.Player;
import com.mourat.rockpaperscissors.domain.service.GameRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameSessionFactory {

    private GameRulesService gameRulesService;

    @Autowired
    public GameSessionFactory (GameRulesService gameRulesService){
        this.gameRulesService = gameRulesService;
    }

    public GameSession createSession(Player owner, Game game){
        return new GameSession(owner, game, gameRulesService);
    }
}
