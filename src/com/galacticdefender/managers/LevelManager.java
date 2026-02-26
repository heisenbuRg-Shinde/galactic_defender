package com.galacticdefender.managers;

import com.galacticdefender.utils.Constants;

/**
 * Tracks the current game level and determines when to advance.
 * Uses the score thresholds defined in Constants.
 */
public class LevelManager {

    private int level = 1;

    /** Call every frame to check whether the score warrants a level-up. */
    public boolean checkLevelUp(int score) {
        int newLevel = 1;
        if (score >= Constants.LEVEL_3_SCORE)
            newLevel = 3;
        else if (score >= Constants.LEVEL_2_SCORE)
            newLevel = 2;

        if (newLevel > level) {
            level = newLevel;
            return true; // signals GameEngine to trigger wave / boss
        }
        return false;
    }

    public void reset() {
        level = 1;
    }

    public int getLevel() {
        return level;
    }

    public boolean isBossLevel() {
        return level >= 3;
    }
}
