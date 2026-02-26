package com.galacticdefender.objects;

import com.galacticdefender.utils.Collidable;
import com.galacticdefender.utils.Constants;

import java.awt.*;

/**
 * A projectile fired by either the player or an enemy.
 * Implements Collidable – deactivates on first collision.
 * Demonstrates constructor chaining and interface implementation.
 */
public class Bullet extends GameObject implements Collidable {

    private final boolean friendly; // true = fired by player
    private final int damage;

    public Bullet(float x, float y, float velX, float velY, boolean friendly) {
        super(x, y, 14, 5);
        this.velX = velX;
        this.velY = velY;
        this.friendly = friendly;
        this.damage = friendly ? 25 : 10;
    }

    @Override
    public void update(float dt) {
        x += velX * dt;
        y += velY * dt;
        if (x < -30 || x > Constants.WINDOW_WIDTH + 30 ||
                y < -30 || y > Constants.WINDOW_HEIGHT + 30) {
            active = false;
        }
    }

    @Override
    public void render(Graphics2D g) {
        Composite old = g.getComposite();
        if (friendly) {
            // Gold tracer with soft glow
            g.setColor(new Color(255, 230, 80, 90));
            g.fillRoundRect((int) x - 3, (int) y - 3, width + 6, height + 6, 6, 6);
            g.setColor(new Color(255, 215, 50));
            g.fillRoundRect((int) x, (int) y, width, height, 4, 4);
            g.setColor(Color.WHITE);
            g.fillOval((int) x + width - 4, (int) y, 4, 5);
        } else {
            // Red enemy tracer
            g.setColor(new Color(255, 60, 60, 90));
            g.fillRoundRect((int) x - 3, (int) y - 3, width + 6, height + 6, 6, 6);
            g.setColor(new Color(255, 80, 80));
            g.fillRoundRect((int) x, (int) y, width, height, 4, 4);
        }
        g.setComposite(old);
    }

    // ── Collidable ──────────────────────────────────────────────
    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    @Override
    public void onCollision(GameObject o) {
        active = false;
    }

    // ── Getters ─────────────────────────────────────────────────
    public boolean isFriendly() {
        return friendly;
    }

    public int getDamage() {
        return damage;
    }
}
