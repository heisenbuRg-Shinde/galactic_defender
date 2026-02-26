package com.galacticdefender.utils;

/** Central repository for all game-wide constants. */
public final class Constants {
    private Constants() {}

    // Window
    public static final int WINDOW_WIDTH  = 900;
    public static final int WINDOW_HEIGHT = 700;
    public static final int FPS           = 60;

    // Physics
    public static final float GRAVITY              = 900f;   // px/s²
    public static final float PLAYER_RUN_SPEED     = 260f;   // px/s
    public static final float PLAYER_JUMP_VELOCITY = -560f;  // px/s (negative = up)
    public static final float BULLET_SPEED         = 680f;   // px/s

    // Player
    public static final int  PLAYER_MAX_HEALTH     = 100;
    public static final long GUN_COOLDOWN_MS        = 250;   // ms between shots
    public static final long DAMAGE_INVINCIBILITY_MS = 800;  // ms of invincibility after hit

    // Level score thresholds
    public static final int LEVEL_2_SCORE = 500;
    public static final int LEVEL_3_SCORE = 1500;

    // Power-up durations (ms)
    public static final long DOUBLE_BULLET_DURATION = 10_000L;
    public static final long SHIELD_DURATION        = 5_000L;
    public static final long SPEED_BOOST_DURATION   = 8_000L;

    // Asset paths (relative to working dir)
    public static final String IMG_BG           = "assets/images/background.png";
    public static final String IMG_PLAYER       = "assets/images/player.png";
    public static final String IMG_ENEMY_BASIC  = "assets/images/enemy_basic.png";
    public static final String IMG_ENEMY_FAST   = "assets/images/enemy_fast.png";
    public static final String IMG_ENEMY_BOSS   = "assets/images/enemy_boss.png";
    public static final String IMG_POWERUP      = "assets/images/powerup.png";

    // File I/O
    public static final String HIGHSCORE_FILE = "highscore.txt";
}
