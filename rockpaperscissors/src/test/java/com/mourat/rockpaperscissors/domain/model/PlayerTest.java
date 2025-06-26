package com.mourat.rockpaperscissors.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void newPlayerWithName_nullName_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Player.newPlayerWithName(null));

        assertEquals("Name must be between 2 and 16 characters", exception.getMessage());
    }

    @Test
    void newPlayerWithName_emptyName_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Player.newPlayerWithName(""));

        assertEquals("Name must be between 2 and 16 characters", exception.getMessage());
    }

    @Test
    void newPlayerWithName_whiteSpacesName_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Player.newPlayerWithName("      "));

        assertEquals("Name must be between 2 and 16 characters", exception.getMessage());
    }

    @Test
    void newPlayerWithName_shortName_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Player.newPlayerWithName("t"));

        assertEquals("Name must be between 2 and 16 characters", exception.getMessage());
    }

    @Test
    void newPlayerWithName_longName_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Player.newPlayerWithName("t1t2t3t4t5t6t7t8t9"));

        assertEquals("Name must be between 2 and 16 characters", exception.getMessage());
    }

    @Test
    void newPlayerWithName_validName_createPlayerSuccessfully() {
        Player player = Player.newPlayerWithName("test");

        assertNotNull(player);
        assertNotNull(player.getId());
        assertEquals("test", player.getName());

        assertNull(player.getGamePlaying());
        assertNull(player.getGameSession());
    }
}