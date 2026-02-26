package com.galacticdefender.engine;

import com.galacticdefender.utils.Constants;

/**
 * Fixed-timestep game loop running on its own thread.
 * Targets 60 FPS. Sleeps to compensate for fast frames.
 *
 * Demonstrates: Multithreading (implements Runnable, runs as a Thread).
 */
public class GameLoop implements Runnable {

    private final GameEngine engine;
    private volatile boolean running = false;
    private Thread thread;

    private static final long TARGET_NS = 1_000_000_000L / Constants.FPS;

    public GameLoop(GameEngine engine) {
        this.engine = engine;
    }

    public synchronized void start() {
        if (running)
            return;
        running = true;
        thread = new Thread(this, "GameLoop-Thread");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            long elapsed = now - lastTime;
            lastTime = now;
            float dt = elapsed / 1_000_000_000f;

            // Cap dt to prevent spiral-of-death on lag spikes
            if (dt > 0.05f)
                dt = 0.05f;

            engine.tick(dt);

            // Sleep to hit target frame rate
            long sleep = TARGET_NS - (System.nanoTime() - now);
            if (sleep > 0) {
                try {
                    Thread.sleep(sleep / 1_000_000L, (int) (sleep % 1_000_000L));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
