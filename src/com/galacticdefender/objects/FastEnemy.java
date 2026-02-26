package com.galacticdefender.objects;

import com.galacticdefender.engine.ResourceLoader;
import com.galacticdefender.utils.Constants;

import java.awt.image.BufferedImage;

/**
 * Fast enemy with a zigzag movement pattern.
 * Lower health but harder to hit due to horizontal swerving.
 */
public class FastEnemy extends Enemy {

    private static final int W = 46, H = 62;
    private static final float SPEED = 165f;
    private static BufferedImage cachedSprite;

    private float zigTimer = 0f;
    private float zigDir = 1f;

    public FastEnemy(float x, float y) {
        super(x, y, W, H, 30, 10, 150, 2.0f);
        if (cachedSprite == null) {
            BufferedImage raw = ResourceLoader.loadImage(Constants.IMG_ENEMY_FAST);
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

        // Toggle zigzag direction every 0.45 s
        zigTimer += dt;
        if (zigTimer > 0.45f) {
            zigTimer = 0;
            zigDir *= -1;
        }

        float base = (px < ex) ? -SPEED : SPEED;
        velX = base + zigDir * 70f;
        facingRight = px > ex;
    }
}
