package com.galacticdefender.engine;

import com.galacticdefender.managers.*;
import com.galacticdefender.objects.Player;
import com.galacticdefender.ui.*;
import com.galacticdefender.utils.Constants;
import com.galacticdefender.utils.World;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Top-level orchestrator of the Galactic Defender game.
 *
 * Responsibilities:
 * - Instantiates all subsystems (managers, player, panel, window, loop)
 * - Drives the per-frame tick (called by GameLoop thread)
 * - Handles global input: start, pause, restart, quit
 * - Implements restart logic (reset all subsystems)
 *
 * Demonstrates: Encapsulation (each subsystem behind its own class),
 * Multithreading (GameLoop thread), Collections Framework
 * (CopyOnWriteArrayList in GamePanel).
 */
public class GameEngine {

    // ── Core engine components ─────────────────────────────────
    private final InputHandler input;
    private final ScoreManager score;
    private final LevelManager level;
    private final CollisionManager collision;
    private final SpawnManager spawn;
    private final SceneManager scene;
    private final Player player;
    private final GamePanel panel;
    private final GameWindow window;
    private final GameLoop loop;

    public GameEngine() {
        // Initialise world geometry (platforms)
        World.init();

        // Build subsystems
        input = new InputHandler();
        score = new ScoreManager();
        level = new LevelManager();
        collision = new CollisionManager();
        spawn = new SpawnManager();
        scene = new SceneManager();
        player = new Player(60, 560, input);

        // Build panel and window on the EDT
        panel = new GamePanel(player, input, score, level, collision, spawn, scene);
        window = new GameWindow(panel, input);

        // Start game loop thread
        loop = new GameLoop(this);
        loop.start();
    }

    /**
     * Called by GameLoop every frame.
     * Handles global key events, updates state, renders.
     */
    public void tick(float dt) {
        handleGlobalInput();
        panel.update(dt);
        window.renderFrame(panel);
    }

    // ── Input handling ─────────────────────────────────────────

    private void handleGlobalInput() {
        // Start screen: ENTER to begin
        if (scene.isStart() && input.isJustPressed(KeyEvent.VK_ENTER)) {
            startGame();
        }

        // ESC: quit to desktop from start or game-over; also quits if paused
        if (input.isJustPressed(KeyEvent.VK_ESCAPE)) {
            if (scene.isStart() || scene.isGameOver() || scene.isPaused()) {
                score.saveHighScore();
                System.exit(0);
            }
        }

        // P: toggle pause during play
        if (input.isJustPressed(KeyEvent.VK_P) &&
                (scene.isPlaying() || scene.isPaused())) {
            scene.togglePause();
        }

        // R: restart after game over
        if (input.isJustPressed(KeyEvent.VK_R) && scene.isGameOver()) {
            restartGame();
        }
    }

    private void startGame() {
        score.reset();
        level.reset();
        spawn.reset();
        player.reset();
        panel.clearObjects();
        scene.setState(SceneManager.Scene.PLAYING);
    }

    private void restartGame() {
        startGame(); // constructor-chaining style reuse
    }

    // ── Entry point ───────────────────────────────────────────

    public static void main(String[] args) {
        // Ensure Swing components are created on the Event Dispatch Thread
        SwingUtilities.invokeLater(GameEngine::new);
    }
}
