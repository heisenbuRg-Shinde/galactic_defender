package com.galacticdefender.managers;

import com.galacticdefender.utils.Constants;

import java.io.*;
import java.nio.file.*;

/**
 * Manages live score and high score with file persistence.
 * Demonstrates File Handling and IOException management using
 * try-with-resources.
 * Encapsulates all score state behind getters/setters.
 */
public class ScoreManager {

    private int score = 0;
    private int highScore = 0;

    public ScoreManager() {
        loadHighScore();
    }

    public void addScore(int points) {
        score += points;
        if (score > highScore)
            highScore = score;
    }

    public void reset() {
        score = 0;
    }

    // ── File I/O ────────────────────────────────────────────────

    /** Persist the current high score to disk. Handles IOException gracefully. */
    public void saveHighScore() {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(Constants.HIGHSCORE_FILE))) {
            bw.write(String.valueOf(highScore));
        } catch (IOException e) {
            System.err.println("[ScoreManager] Could not save high score: " + e.getMessage());
        }
    }

    /** Load the persisted high score from disk on startup. */
    private void loadHighScore() {
        Path p = Paths.get(Constants.HIGHSCORE_FILE);
        if (!Files.exists(p))
            return;
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line = br.readLine();
            if (line != null)
                highScore = Integer.parseInt(line.trim());
        } catch (IOException | NumberFormatException e) {
            System.err.println("[ScoreManager] Could not load high score: " + e.getMessage());
        }
    }

    // ── Getters ─────────────────────────────────────────────────
    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }
}
