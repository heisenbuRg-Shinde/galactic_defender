package com.galacticdefender.objects;

import com.galacticdefender.utils.Collidable;
import com.galacticdefender.utils.Constants;
import com.galacticdefender.utils.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base for all enemy types.
 * Extends GameObject, implements Collidable.
 * Declares abstract move() so each subclass defines unique behaviour —
 * demonstrating inheritance and polymorphism.
 */
public abstract class Enemy extends GameObject implements Collidable {

    protected int health, maxHealth;
    protected int damage; // contact damage dealt to player
    protected int scoreValue;
    protected boolean facingRight;
    protected boolean onGround;
    protected float shootTimer;
    protected float shootInterval;
    protected BufferedImage sprite;

    // Bullets queued to be added to the world by SpawnManager / GameEngine
    protected final List<Bullet> pendingBullets = new ArrayList<>();

    protected Enemy(float x, float y, int w, int h,
            int health, int damage, int score, float shootInterval) {
        super(x, y, w, h);
        this.health = health;
        this.maxHealth = health;
        this.damage = damage;
        this.scoreValue = score;
        this.shootInterval = shootInterval;
        this.shootTimer = shootInterval * 0.5f; // stagger first shot
    }

    /** Subclass-specific movement logic. */
    protected abstract void move(float dt, Player player);

    /** Called each frame by GameEngine, passing the player for AI targeting. */
    public void update(float dt, Player player) {
        if (!active)
            return;

        move(dt, player);

        // Shoot at player if in range
        shootTimer -= dt;
        if (shootTimer <= 0 && player != null) {
            shootTimer = shootInterval;
            tryShoot(player);
        }

        // Gravity
        velY += Constants.GRAVITY * dt;
        x += velX * dt;
        y += velY * dt;

        // Screen horizontal clamp with bounce
        if (x < 0) {
            x = 0;
            velX = Math.abs(velX);
        }
        if (x + width > Constants.WINDOW_WIDTH) {
            x = Constants.WINDOW_WIDTH - width;
            velX = -Math.abs(velX);
        }

        // Platform collision (land on top surface only)
        onGround = false;
        for (Platform p : World.platforms) {
            Rectangle pb = p.getBounds();
            float myBot = y + height, prevBot = myBot - velY * dt;
            if (getBounds().intersects(pb) && prevBot <= pb.y + 4 && velY >= 0) {
                y = pb.y - height;
                velY = 0;
                onGround = true;
            }
        }
        // World floor clamp
        if (y + height >= Constants.WINDOW_HEIGHT) {
            y = Constants.WINDOW_HEIGHT - height;
            velY = 0;
            onGround = true;
        }
        if (y < 0) {
            y = 0;
            velY = 0;
        }

        if (health <= 0)
            active = false;
    }

    @Override
    public void update(float dt) {
        /* Use update(dt, player) */ }

    protected void tryShoot(Player player) {
        float cx = x + width / 2f, cy = y + height / 2f + 5;
        float pcx = player.getX() + player.getWidth() / 2f;
        float pcy = player.getY() + player.getHeight() / 2f;
        float dx = pcx - cx, dy = pcy - cy;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len > 0 && len < 580) {
            pendingBullets.add(new Bullet(cx, cy, dx / len * 380f, dy / len * 80f, false));
        }
    }

    @Override
    public void render(Graphics2D g) {
        if (!active)
            return;
        renderSprite(g);
        renderHealthBar(g);
    }

    protected void renderSprite(Graphics2D g) {
        if (sprite != null) {
            if (!facingRight)
                g.drawImage(sprite, (int) x, (int) y, width, height, null);
            else
                g.drawImage(sprite, (int) x + width, (int) y, -width, height, null);
        } else {
            // Placeholder rect – should not appear during normal gameplay
            g.setColor(new Color(190, 55, 55));
            g.fillRect((int) x, (int) y, width, height);
        }
    }

    protected void renderHealthBar(Graphics2D g) {
        if (health >= maxHealth)
            return;
        int bx = (int) x, by = (int) y - 9, bw = width, bh = 5;
        g.setColor(new Color(50, 18, 18, 210));
        g.fillRect(bx, by, bw, bh);
        int fill = (int) ((float) health / maxHealth * bw);
        g.setColor(new Color(220, 55, 55));
        g.fillRect(bx, by, fill, bh);
        g.setColor(new Color(255, 100, 100));
        g.drawRect(bx, by, bw, bh);
    }

    public void takeDamage(int amt) {
        health = Math.max(0, health - amt);
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public List<Bullet> consumePendingBullets() {
        List<Bullet> out = new ArrayList<>(pendingBullets);
        pendingBullets.clear();
        return out;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    @Override
    public void onCollision(GameObject other) {
        if (other instanceof Bullet) {
            Bullet b = (Bullet) other;
            if (b.isFriendly())
                takeDamage(b.getDamage());
        }
    }
}
