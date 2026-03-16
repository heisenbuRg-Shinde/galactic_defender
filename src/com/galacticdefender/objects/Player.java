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

public class Player extends GameObject implements Collidable {

    // ── Sprite / Animation ─────────────────────────────────────
    private static final int W = 55, H = 72;

    private BufferedImage[][] frames;
    private int currentFrame = 0;
    private int currentRow = 0;

    private float animationTimer = 0f;
    private static final int FRAME_COUNT = 4;
    private static final float FRAME_SPEED = 0.1f;

    // ── Input ──────────────────────────────────────────────────
    private final InputHandler input;

    // ── Combat state ───────────────────────────────────────────
    private int health;
    private boolean facingRight = true;
    private boolean onGround = false;

    private long gunCooldown = 0;
    private long lastTime = System.currentTimeMillis();

    // ── Power-ups ──────────────────────────────────────────────
    private long doubleShotTimer = 0;
    private long shieldTimer = 0;
    private long speedBoostTimer = 0;

    private long invincibilityTimer = 0;

    // ── Bullets ────────────────────────────────────────────────
    private final List<Bullet> pendingBullets = new ArrayList<>();

    // ── Movement state ─────────────────────────────────────────
    private boolean isMoving = false;

    public Player(float x, float y, InputHandler input) {
        super(x, y, W, H);

        this.health = Constants.PLAYER_MAX_HEALTH;
        this.input = input;

        loadSpriteSheet();
    }

    // ───────────────────────────────────────────────────────────
    // LOAD SPRITE SHEET
    // ───────────────────────────────────────────────────────────
    private void loadSpriteSheet() {

        BufferedImage raw = ResourceLoader.loadImage(Constants.IMG_PLAYER);

        if (raw == null) return;

        int frameW = raw.getWidth() / 8;
        int frameH = raw.getHeight() / 5;

        frames = new BufferedImage[5][8];

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {

                BufferedImage frame = raw.getSubimage(
                        col * frameW,
                        row * frameH,
                        frameW,
                        frameH
                );

                frames[row][col] = ResourceLoader.scaleImage(frame, W, H);
            }
        }
    }

    // ───────────────────────────────────────────────────────────
    // UPDATE
    // ───────────────────────────────────────────────────────────
    @Override
    public void update(float dt) {

        long now = System.currentTimeMillis();
        long elapsed = now - lastTime;
        lastTime = now;

        doubleShotTimer = Math.max(0, doubleShotTimer - elapsed);
        shieldTimer = Math.max(0, shieldTimer - elapsed);
        speedBoostTimer = Math.max(0, speedBoostTimer - elapsed);
        invincibilityTimer = Math.max(0, invincibilityTimer - elapsed);
        gunCooldown = Math.max(0, gunCooldown - elapsed);

        // ── Movement
        float speed = Constants.PLAYER_RUN_SPEED * (speedBoostTimer > 0 ? 1.5f : 1.0f);

        isMoving = false;

        if (input.isHeld(KeyEvent.VK_LEFT)) {
            velX = -speed;
            facingRight = false;
            isMoving = true;
        }
        else if (input.isHeld(KeyEvent.VK_RIGHT)) {
            velX = speed;
            facingRight = true;
            isMoving = true;
        }
        else {
            velX = 0;
        }

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

        x = Math.max(0, Math.min(x, Constants.WINDOW_WIDTH - width));

        // ── Platform collision
        onGround = false;

        for (Platform p : World.platforms) {

            Rectangle pb = p.getBounds();

            float myBot = y + height;
            float prevBot = myBot - velY * dt;

            if (getBounds().intersects(pb) && prevBot <= pb.y + 4 && velY >= 0) {

                y = pb.y - height;
                velY = 0;
                onGround = true;
            }
        }

        if (y + height >= Constants.WINDOW_HEIGHT) {
            y = Constants.WINDOW_HEIGHT - height;
            velY = 0;
            onGround = true;
        }

        if (y < 0) {
            y = 0;
            velY = 0;
        }

        // ── Choose animation row
        if (!onGround) {
            currentRow = 3;   // jump row
        }
        else if (isMoving) {
            currentRow = 1;   // run row
        }
        else {
            currentRow = 0;   // idle row
        }

        // ── Animate frames
        animationTimer += dt;

        if (animationTimer > FRAME_SPEED) {

            animationTimer = 0;

            currentFrame++;

            if (currentFrame >= FRAME_COUNT)
                currentFrame = 0;
        }
    }

    // ───────────────────────────────────────────────────────────
    // SHOOT
    // ───────────────────────────────────────────────────────────
    private void shoot() {

        float sx = facingRight ? x + width : x - 14;
        float sy = y + height / 2f - 3;

        float vx = facingRight ? Constants.BULLET_SPEED : -Constants.BULLET_SPEED;

        pendingBullets.add(new Bullet(sx, sy, vx, 0, true));

        if (doubleShotTimer > 0)
            pendingBullets.add(new Bullet(sx, sy + 8, vx, 0, true));
    }

    // ───────────────────────────────────────────────────────────
    // RENDER
    // ───────────────────────────────────────────────────────────
    @Override
    public void render(Graphics2D g) {

        if (frames != null) {

            BufferedImage frame = frames[currentRow][currentFrame];

            if (facingRight)
                g.drawImage(frame, (int)x, (int)y, width, height, null);
            else
                g.drawImage(frame, (int)x + width, (int)y, -width, height, null);
        }

        // muzzle flash
        if (gunCooldown > Constants.GUN_COOLDOWN_MS - 80) {

            float mx = facingRight ? x + width + 2 : x - 10;

            g.setColor(new Color(255,220,80,180));

            g.fillOval((int)mx, (int)(y + height / 2f - 6), 12, 12);
        }
    }

    // ───────────────────────────────────────────────────────────
    // DAMAGE
    // ───────────────────────────────────────────────────────────
    public void takeDamage(int amt) {

        if (invincibilityTimer > 0 || shieldTimer > 0)
            return;

        health = Math.max(0, health - amt);

        invincibilityTimer = Constants.DAMAGE_INVINCIBILITY_MS;
    }

    // ───────────────────────────────────────────────────────────
    // POWERUPS
    // ───────────────────────────────────────────────────────────
    public void activateDoubleShot(long durationMs) { doubleShotTimer = durationMs; }
    public void activateShield(long durationMs) { shieldTimer = durationMs; }
    public void activateSpeedBoost(long durationMs) { speedBoostTimer = durationMs; }

    // ───────────────────────────────────────────────────────────
    // COLLISION
    // ───────────────────────────────────────────────────────────
    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x,(int)y,width,height);
    }

    @Override
    public void onCollision(GameObject other) {

        if (other instanceof Enemy)
            takeDamage(((Enemy)other).getDamage());

        if (other instanceof Bullet) {

            Bullet b = (Bullet)other;

            if (!b.isFriendly())
                takeDamage(b.getDamage());
        }
    }

    // ───────────────────────────────────────────────────────────
    // GETTERS
    // ───────────────────────────────────────────────────────────
    public int getHealth(){ return health; }
    public int getMaxHealth(){ return Constants.PLAYER_MAX_HEALTH; }
    public boolean isDead(){ return health <= 0; }

    public List<Bullet> consumePendingBullets(){

        List<Bullet> out = new ArrayList<>(pendingBullets);

        pendingBullets.clear();

        return out;
    }

    // ───────────────────────────────────────────────────────────
    // POWER-UP STATE GETTERS (used by UI)
    // ───────────────────────────────────────────────────────────

    public boolean isDoubleShotActive() {
        return doubleShotTimer > 0;
    }

    public boolean isShieldActive() {
        return shieldTimer > 0;
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

    public void reset(){

        x = 60;
        y = 560;

        velX = velY = 0;

        health = Constants.PLAYER_MAX_HEALTH;

        doubleShotTimer = shieldTimer = speedBoostTimer = invincibilityTimer = gunCooldown = 0;

        active = true;

        lastTime = System.currentTimeMillis();
    }
}