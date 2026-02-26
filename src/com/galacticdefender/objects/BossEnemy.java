package com.galacticdefender.objects;

import com.galacticdefender.engine.ResourceLoader;
import com.galacticdefender.utils.Constants;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Level-3 boss — large, high health, rapid-fire, patrols its platform.
 * Overrides renderSprite to draw an additional HP bar above the sprite.
 */
public class BossEnemy extends Enemy {

    private static final int W = 110, H = 130;
    private static final float SPEED = 60f;
    private static BufferedImage cachedSprite;

    private float patrolTimer = 0f;

    public BossEnemy(float x, float y) {
        super(x, y, W, H, 500, 25, 1000, 0.9f); // rapid fire
        if (cachedSprite == null) {
            BufferedImage raw = ResourceLoader.loadImage(Constants.IMG_ENEMY_BOSS);
            cachedSprite = (raw != null) ? ResourceLoader.scaleImage(raw, W, H) : null;
        }
        this.sprite = cachedSprite;
        this.velX = SPEED;
        this.facingRight = false;
    }

    @Override
    protected void move(float dt, Player player) {
        // Patrol back-and-forth; also slowly descend toward player's Y level
        patrolTimer += dt;
        if (patrolTimer > 2.0f) {
            patrolTimer = 0;
            velX = -velX;
            facingRight = !facingRight;
        }
    }

    @Override
    protected void renderSprite(Graphics2D g) {
        super.renderSprite(g);
        // Thick boss health bar at the top of the screen during boss phase
        int bx = 50, by = 14, bw = Constants.WINDOW_WIDTH - 100, bh = 14;
        g.setColor(new Color(30, 10, 10, 210));
        g.fillRoundRect(bx, by, bw, bh, 6, 6);
        int fill = (int) ((float) health / maxHealth * bw);
        GradientPaint gp = new GradientPaint(bx, 0, new Color(220, 30, 30), bx + fill, 0, new Color(255, 100, 40));
        Paint prev = g.getPaint();
        g.setPaint(gp);
        g.fillRoundRect(bx, by, fill, bh, 6, 6);
        g.setPaint(prev);
        g.setColor(new Color(255, 80, 80));
        g.drawRoundRect(bx, by, bw, bh, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 11));
        String txt = "BOSS  " + health + "/" + maxHealth;
        g.drawString(txt, bx + 6, by + bh - 2);
    }
}
