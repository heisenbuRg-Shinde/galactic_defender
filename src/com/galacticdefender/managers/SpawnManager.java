package com.galacticdefender.managers;

import com.galacticdefender.objects.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controls when and where enemies and power-ups spawn.
 * Tracks wave timers and current level, producing new GameObjects to add.
 * Demonstrates the use of Random, timers, and level-aware logic.
 */
public class SpawnManager {

    private float waveTimer = 0f;
    private float waveInterval = 6.0f; // seconds between waves
    private int waveCount = 0;
    private final Random rng = new Random();

    // Spawn locations across the top
    private static final float[] SPAWN_X = { 60, 200, 400, 600, 800 };

    public void reset() {
        waveTimer = 0;
        waveCount = 0;
    }

    /**
     * Called every frame. Returns a list of new objects to add to the game world.
     * 
     * @param dt    Elapsed time since last frame (seconds).
     * @param level Current game level (1-3).
     */
    public List<GameObject> update(float dt, int level) {
        List<GameObject> spawned = new ArrayList<>();
        waveTimer += dt;
        if (waveTimer < waveInterval)
            return spawned;

        waveTimer = 0;
        waveCount++;
        spawned.addAll(spawnWave(level));
        // Randomly drop a power-up every 3 waves
        if (waveCount % 3 == 0)
            spawned.add(spawnPowerUp());
        return spawned;
    }

    private List<GameObject> spawnWave(int level) {
        List<GameObject> wave = new ArrayList<>();
        int count = 2 + level; // more enemies at higher levels

        // Level 3: spawn a boss after wave 1 (waveCount == 1 handled outside)
        if (level == 3 && waveCount == 1) {
            wave.add(new BossEnemy(400, -140));
            return wave;
        }

        for (int i = 0; i < count; i++) {
            float sx = SPAWN_X[rng.nextInt(SPAWN_X.length)] + rng.nextInt(40) - 20;
            if (level == 1) {
                wave.add(new BasicEnemy(sx, -80));
            } else {
                // Level 2+: mix of basic and fast
                wave.add(rng.nextBoolean() ? new BasicEnemy(sx, -80) : new FastEnemy(sx, -80));
            }
        }
        return wave;
    }

    private PowerUp spawnPowerUp() {
        float px = 100 + rng.nextInt(700);
        int type = rng.nextInt(3);
        return switch (type) {
            case 0 -> new DoubleBulletPowerUp(px, -50);
            case 1 -> new ShieldPowerUp(px, -50);
            default -> new SpeedBoostPowerUp(px, -50);
        };
    }
}
