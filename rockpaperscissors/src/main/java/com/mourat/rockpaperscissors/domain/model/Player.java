package com.mourat.rockpaperscissors.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

// Each user of the application represented by an instance of Player class
public class Player {

    // Unique id of player
    private final UUID id;

    // Name of the player
    private String name;

    /**
     * Each player is created by a name and gets a unique id automatically
     * Player creation is handled by factory method newPlayer
     *
     * @param name name of the player
     */
    private Player (String name){
        this.id = UUID.randomUUID();
        this.name = name;

    }

    /**
     * Creates a new player with a given name
     * Ensures the name is valid
     *
     * @param name new players name
     * @return a new Player instance with the given name
     */
    public static Player newPlayerWithName(String name){

        // Check if the name is valid, valid name must be between 2 and 16 characters
        if(name == null || name.trim().length() < 2 || name.trim().length() > 16){
            throw new IllegalArgumentException("Name must be between 2 and 16 characters");
        }

        return new Player(name);
    }
}
