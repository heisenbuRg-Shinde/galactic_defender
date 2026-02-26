package com.galacticdefender.engine;

import com.galacticdefender.managers.*;
import com.galacticdefender.objects.*;
import com.galacticdefender.ui.*;
import com.galacticdefender.utils.Constants;
import com.galacticdefender.utils.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JPanel;

/**
 * Main rendering and update panel.
 * Uses Swing's double-buffering via BufferStrategy on the parent window.
 * Aggregates all game-world state and drives each frame's update + render.
 *
 * Demonstrates: Multithreading (game loop calls renderFrame from a thread),
 * Polymorphism (iterating ArrayList<GameObject>), Collections Framework.
 */
public class GamePanel extends JPanel {

    // ── All active game objects (thread-safe for concurrent modification) ──
    private final CopyOnWriteArrayList<GameObject> objects = new CopyOnWriteArrayList<>();

    // ── Shared subsystems ──────────────────────────────────────
    private final Player player;
    private final InputHandler input;
    private final ScoreManager score;
    private final LevelManager level;
    private final CollisionManager collision;
    private final SpawnManager spawn;
    private final SceneManager scene;

    // ── UI screens ────────────────────────────────────────────
    private final StartScreen startScreen = new StartScreen();
    private final GameScreen gameScreen = new GameScreen();
    private final GameOverScreen gameOverScreen = new GameOverScreen();
    private final PauseScreen pauseScreen = new PauseScreen();

    // ── Background ────────────────────────────────────────────
    private BufferedImage background;
    private float parallaxOffset = 0f;

    public GamePanel(Player player, InputHandler input,
            ScoreManager score, LevelManager level,
            CollisionManager collision, SpawnManager spawn,
            SceneManager scene) {
        this.player = player;
        this.input = input;
        this.score = score;
        this.level = level;
        this.collision = collision;
        this.spawn = spawn;
        this.scene = scene;

        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        background = ResourceLoader.loadImage(Constants.IMG_BG);
        if (background != null)
            background = ResourceLoader.scaleImage(background,
                    Constants.WINDOW_WIDTH + 60, Constants.WINDOW_HEIGHT);
    }

    // ── Update ────────────────────────────────────────────────

    public void update(float dt) {
        switch (scene.getState()) {
            case START -> startScreen.update(dt);
            case PLAYING -> updatePlaying(dt);
            case GAME_OVER -> gameOverScreen.update(dt);
            case PAUSED -> {
                /* frozen */ }
        }
    }

    private void updatePlaying(float dt) {
        // Parallax scroll
        parallaxOffset = (parallaxOffset + 15 * dt) % 60f;

        // Player
        player.update(dt);

        // Collect player bullets
        objects.addAll(player.consumePendingBullets());

        // Update all objects polymorphically; enemies need player reference
        for (GameObject obj : objects) {
            if (!obj.isActive())
                continue;
            if (obj instanceof Enemy)
                ((Enemy) obj).update(dt, player);
            else
                obj.update(dt);
        }

        // Collect enemy bullets
        List<Bullet> newBullets = new ArrayList<>();
        for (GameObject obj : objects) {
            if (obj instanceof Enemy && obj.isActive())
                newBullets.addAll(((Enemy) obj).consumePendingBullets());
        }
        objects.addAll(newBullets);

        // Collision detection
        List<GameObject> spawned = collision.checkAll(objects, player, score);
        objects.addAll(spawned);

        // Spawn new wave / power-ups
        objects.addAll(spawn.update(dt, level.getLevel()));

        // Level progression (lambda on stream – Java 8 feature)
        if (level.checkLevelUp(score.getScore())) {
            System.out.println("[Level] Advanced to level " + level.getLevel());
        }

        // Remove inactive objects
        objects.removeIf(obj -> !obj.isActive());

        // Check game-over
        if (player.isDead()) {
            score.saveHighScore();
            scene.setState(SceneManager.Scene.GAME_OVER);
            gameOverScreen.reset();
        }
    }

    // ── Render ────────────────────────────────────────────────

    public void renderFrame(Graphics2D g) {
        // Background
        if (background != null)
            g.drawImage(background, -(int) parallaxOffset, 0, null);
        else {
            g.setColor(new Color(18, 20, 30));
            g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        }

        switch (scene.getState()) {
            case START -> startScreen.render(g);
            case PLAYING, PAUSED -> {
                renderWorld(g);
                gameScreen.render(g, player, score, level);
                if (scene.isPaused())
                    pauseScreen.render(g);
            }
            case GAME_OVER -> {
                renderWorld(g);
                gameScreen.render(g, player, score, level);
                gameOverScreen.render(g, score.getScore(), score.getHighScore());
            }
        }
    }

    private void renderWorld(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Platforms
        for (Platform p : World.platforms)
            p.render(g);
        // All game objects (polymorphic render)
        for (GameObject obj : objects) {
            if (obj.isActive())
                obj.render(g);
        }
        // Player
        player.render(g);
    }

    // ── Accessors for GameEngine ──────────────────────────────

    public void clearObjects() {
        objects.clear();
    }

    public CopyOnWriteArrayList<GameObject> getObjects() {
        return objects;
    }
}
