package com.galacticdefender.objects;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Abstract base class for every entity in the game world.
 * Encapsulates position, size, velocity, and active state.
 * Enforces the update/render contract via abstract methods.
 *
 * Demonstrates: Abstraction, Encapsulation, and the foundation
 * for Inheritance and Polymorphism throughout the hierarchy.
 */
public abstract class GameObject {

    // Encapsulated fields – accessed only through getters/setters
    protected float x, y;
    protected int width, height;
    protected float velX, velY;
    protected boolean active;

    public GameObject(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.active = true;
    }

    /** Update logic called once per frame. dt = elapsed seconds. */
    public abstract void update(float dt);

    /** Render this object into the given graphics context. */
    public abstract void render(Graphics2D g);

    /** Default bounding rectangle – subclasses may override for tighter bounds. */
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    // ── Getters & Setters ──────────────────────────────────────
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean b) {
        active = b;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getVelX() {
        return velX;
    }

    public float getVelY() {
        return velY;
    }

    public void setVelX(float vx) {
        velX = vx;
    }

    public void setVelY(float vy) {
        velY = vy;
    }
}
