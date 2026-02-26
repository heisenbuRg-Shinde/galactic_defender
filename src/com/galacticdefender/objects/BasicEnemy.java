package com.galacticdefender.objects;

import com.galacticdefender.engine.ResourceLoader;
import com.galacticdefender.utils.Constants;

import java.awt.image.BufferedImage;

/**
 * Slow melee-range enemy that walks toward the player.
 * Low speed, average health. Demonstrates concrete inheritance from Enemy.
 */
public class BasicEnemy extends Enemy {

    private static final int W = 55, H = 70;
    private static final float SPEED = 80f;
    private static BufferedImage cachedSprite;

    public BasicEnemy(float x, float y) {
        super(x, y, W, H, 50, 15, 100, 3.0f);
        if (cachedSprite == null) {
            BufferedImage raw = ResourceLoader.loadImage(Constants.IMG_ENEMY_BASIC);
            cachedSprite = (raw != null) ? ResourceLoader.scaleImage(raw, W, H) : null;
        }
        this.sprite = cachedSprite;
    }

    @Override
    protected void move(float dt, Player player) {
        if (player == null)
            return;
        float px = player.getX() + player.getWidth() / 2f;
        float ex = x + width / 2f;
        float dist = Math.abs(px - ex);
        if (dist > width + 15) {
            velX = (px < ex) ? -SPEED : SPEED;
            facingRight = px > ex;
        } else {
            velX = 0; // stop when very close (will deal contact damage)
        }
    }
}
