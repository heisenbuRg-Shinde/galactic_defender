package com.galacticdefender.utils;

import com.galacticdefender.objects.Platform;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds shared, static world data (platforms) accessible to all game objects
 * without requiring constructor injection of the full GameEngine.
 */
public final class World {
    private World() {}

    public static final List<Platform> platforms = new ArrayList<>();

    /** Initialise the platform layout for the industrial level. */
    public static void init() {
        platforms.clear();
        // Ground floor
        platforms.add(new Platform(0,   640, 900, 60));
        // Low left / right shelves
        platforms.add(new Platform(50,  510, 220, 18));
        platforms.add(new Platform(630, 510, 220, 18));
        // Mid centre bridge
        platforms.add(new Platform(280, 430, 340, 18));
        // High left / right gantries
        platforms.add(new Platform(60,  310, 200, 18));
        platforms.add(new Platform(640, 310, 200, 18));
        // Top catwalk
        platforms.add(new Platform(310, 190, 280, 18));
    }
}
