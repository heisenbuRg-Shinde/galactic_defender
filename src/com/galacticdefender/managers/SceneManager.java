package com.galacticdefender.managers;

/**
 * Tracks the high-level game state (which screen is active).
 * Demonstrates state-pattern thinking and encapsulation.
 */
public class SceneManager {

    public enum Scene {
        START, PLAYING, PAUSED, GAME_OVER
    }

    private Scene current = Scene.START;

    public void setState(Scene s) {
        current = s;
    }

    public Scene getState() {
        return current;
    }

    public boolean isPlaying() {
        return current == Scene.PLAYING;
    }

    public boolean isPaused() {
        return current == Scene.PAUSED;
    }

    public boolean isGameOver() {
        return current == Scene.GAME_OVER;
    }

    public boolean isStart() {
        return current == Scene.START;
    }

    public void togglePause() {
        if (current == Scene.PLAYING)
            current = Scene.PAUSED;
        else if (current == Scene.PAUSED)
            current = Scene.PLAYING;
    }
}
