package com.galacticdefender.objects;

import com.galacticdefender.engine.InputHandler;
import com.galacticdefender.engine.ResourceLoader;
import com.galacticdefender.utils.Collidable;
import com.galacticdefender.utils.Constants;
import com.galacticdefender.utils.World;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * The player-controlled character.
 * Handles movement, jumping, shooting, power-up timers, and health.
 * Uses encapsulation throughout: all state accessed via methods.
 */
public class Player extends GameObject implements Collidable {

    // ── Sprite ──────────────────────────────────────────────────
    private static BufferedImage cachedSprite;
    private static final int W = 55, H = 72;

    // ── Input ───────────────────────────────────────────────────
    private final InputHandler input;

    // ── Combat state ────────────────────────────────────────────
    private int health;
    private boolean facingRight = true;
    private boolean onGround = false;
    private long gunCooldown = 0; // ms until next shot allowed
    private long lastTime = System.currentTimeMillis();

    // ── Power-up timers (ms remaining) ──────────────────────────
    private long doubleShotTimer = 0;
    private long shieldTimer = 0;
    private long speedBoostTimer = 0;

    // ── Invincibility frames after damage ───────────────────────
    private long invincibilityTimer = 0;

    // ── Pending bullets to add this frame ───────────────────────
    private final List<Bullet> pendingBullets = new ArrayList<>();

    // ── Animation ───────────────────────────────────────────────
    private float walkCycle = 0f;
    private boolean isMoving = false;

    public Player(float x, float y, InputHandler input) {
        super(x, y, W, H);
        this.health = Constants.PLAYER_MAX_HEALTH;
        this.input = input;
        if (cachedSprite == null) {
            BufferedImage raw = ResourceLoader.loadImage(Constants.IMG_PLAYER);
            cachedSprite = (raw != null) ? ResourceLoader.scaleImage(raw, W, H) : null;
        }
    }

    @Override
    public void update(float dt) {
        long now = System.currentTimeMillis();
        long elapsed = now - lastTime;
        lastTime = now;

        // Tick power-up timers
        doubleShotTimer = Math.max(0, doubleShotTimer - elapsed);
        shieldTimer = Math.max(0, shieldTimer - elapsed);
        speedBoostTimer = Math.max(0, speedBoostTimer - elapsed);
        invincibilityTimer = Math.max(0, invincibilityTimer - elapsed);
        gunCooldown = Math.max(0, gunCooldown - elapsed);

        // Movement
        float speed = Constants.PLAYER_RUN_SPEED * (speedBoostTimer > 0 ? 1.5f : 1.0f);
        isMoving = false;
        if (input.isHeld(KeyEvent.VK_LEFT)) {
            velX = -speed;
            facingRight = false;
            isMoving = true;
        } else if (input.isHeld(KeyEvent.VK_RIGHT)) {
            velX = speed;
            facingRight = true;
            isMoving = true;
        } else
            velX = 0;

        // Jump
        if (input.isJustPressed(KeyEvent.VK_UP) && onGround) {
            velY = Constants.PLAYER_JUMP_VELOCITY;
            onGround = false;
        }

        // Shoot
        if (input.isHeld(KeyEvent.VK_SPACE) && gunCooldown == 0) {
            shoot();
            gunCooldown = Constants.GUN_COOLDOWN_MS;
        }

        // Gravity
        velY += Constants.GRAVITY * dt;
        x += velX * dt;
        y += velY * dt;

        // Screen horizontal clamp
        x = Math.max(0, Math.min(x, Constants.WINDOW_WIDTH - width));

        // Platform landing
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
        // World floor
        if (y + height >= Constants.WINDOW_HEIGHT) {
            y = Constants.WINDOW_HEIGHT - height;
            velY = 0;
            onGround = true;
        }
        if (y < 0) {
            y = 0;
            velY = 0;
        }

        // Walk cycle animation
        if (isMoving)
            walkCycle += dt * 8f;
    }

    private void shoot() {
        float sx = facingRight ? x + width : x - 14;
        float sy = y + height / 2f - 3;
        float vx = facingRight ? Constants.BULLET_SPEED : -Constants.BULLET_SPEED;
        pendingBullets.add(new Bullet(sx, sy, vx, 0, true));
        if (doubleShotTimer > 0) {
            // Second bullet slightly offset vertically
            pendingBullets.add(new Bullet(sx, sy + 8, vx, 0, true));
        }
    }

    @Override
    public void render(Graphics2D g) {
        // Shield aura
        if (shieldTimer > 0) {
            float alpha = 0.35f + 0.2f * (float) Math.sin(System.currentTimeMillis() / 150.0);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.setColor(new Color(80, 160, 255));
            g.fillOval((int) x - 10, (int) y - 10, width + 20, height + 20);
            g.setComposite(AlphaComposite.SrcOver);
        }

        // Damage flash
        if (invincibilityTimer > 0 && (System.currentTimeMillis() / 80) % 2 == 0) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        }

        // Sprite or placeholder
        if (cachedSprite != null) {
            if (facingRight)
                g.drawImage(cachedSprite, (int) x, (int) y, width, height, null);
            else
                g.drawImage(cachedSprite, (int) x + width, (int) y, -width, height, null);
        } else {
            g.setColor(new Color(60, 160, 255));
            g.fillRect((int) x, (int) y, width, height);
            g.setColor(Color.WHITE);
            g.fillRect((int) x + 10, (int) y + 15, 12, 18); // body
        }
        g.setComposite(AlphaComposite.SrcOver);

        // Muzzle flash indicator when shooting
        if (gunCooldown > Constants.GUN_COOLDOWN_MS - 80) {
            float mx = facingRight ? x + width + 2 : x - 10;
            g.setColor(new Color(255, 220, 80, 180));
            g.fillOval((int) mx, (int) (y + height / 2f - 6), 12, 12);
        }
    }

    // ── Damage ───────────────────────────────────────────────────
    public void takeDamage(int amt) {
        if (invincibilityTimer > 0 || shieldTimer > 0)
            return;
        health = Math.max(0, health - amt);
        invincibilityTimer = Constants.DAMAGE_INVINCIBILITY_MS;
    }

    // ── Power-up activators ───────────────────────────────────────
    public void activateDoubleShot(long durationMs) {
        doubleShotTimer = durationMs;
    }

    public void activateShield(long durationMs) {
        shieldTimer = durationMs;
    }

    public void activateSpeedBoost(long durationMs) {
        speedBoostTimer = durationMs;
    }

    // ── Collidable ────────────────────────────────────────────────
    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    @Override
    public void onCollision(GameObject other) {
        if (other instanceof Enemy)
            takeDamage(((Enemy) other).getDamage());
        if (other instanceof Bullet) {
            Bullet b = (Bullet) other;
            if (!b.isFriendly())
                takeDamage(b.getDamage());
        }
    }

    // ── Getters ───────────────────────────────────────────────────
    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return Constants.PLAYER_MAX_HEALTH;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public boolean isShieldActive() {
        return shieldTimer > 0;
    }

    public boolean isDoubleShotActive() {
        return doubleShotTimer > 0;
    }

    public boolean isSpeedBoostActive() {
        return speedBoostTimer > 0;
    }

    public long getDoubleShotTimer() {
        return doubleShotTimer;
    }

    public long getShieldTimer() {
        return shieldTimer;
    }

    public long getSpeedBoostTimer() {
        return speedBoostTimer;
    }

    public List<Bullet> consumePendingBullets() {
        List<Bullet> out = new ArrayList<>(pendingBullets);
        pendingBullets.clear();
        return out;
    }

    /** Reset player to starting state for a new game. */
    public void reset() {
        x = 60;
        y = 560;
        velX = 0;
        velY = 0;
        health = Constants.PLAYER_MAX_HEALTH;
        doubleShotTimer = shieldTimer = speedBoostTimer = invincibilityTimer = gunCooldown = 0;
        active = true;
        lastTime = System.currentTimeMillis();
    }
}
