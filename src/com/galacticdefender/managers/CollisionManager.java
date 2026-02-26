package com.galacticdefender.managers;

import com.galacticdefender.objects.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Detects all collisions each frame using AABB (Rectangle.intersects).
 * Resolves: Bullet-Enemy, Player-Enemy, Player-Bullet, Player-PowerUp.
 * Returns lists of new GameObjects (explosions) to be added.
 * Demonstrates encapsulation of collision logic in one dedicated class.
 */
public class CollisionManager {

    /**
     * Run a full collision pass over all active objects.
     * 
     * @param objects All currently active GameObjects (enemies, bullets, power-ups,
     *                effects).
     * @param player  The player instance.
     * @param score   ScoreManager to credit kills.
     * @return List of new GameObjects (explosions) to add after this pass.
     */
    public List<GameObject> checkAll(List<GameObject> objects, Player player, ScoreManager score) {
        List<GameObject> toAdd = new ArrayList<>();

        List<Bullet> bullets = new ArrayList<>();
        List<Enemy> enemies = new ArrayList<>();
        List<PowerUp> powerUps = new ArrayList<>();

        // Categorise
        for (GameObject obj : objects) {
            if (!obj.isActive())
                continue;
            if (obj instanceof Bullet)
                bullets.add((Bullet) obj);
            if (obj instanceof Enemy)
                enemies.add((Enemy) obj);
            if (obj instanceof PowerUp)
                powerUps.add((PowerUp) obj);
        }

        // ── Player bullets vs Enemies ──────────────────────────
        for (Bullet b : bullets) {
            if (!b.isFriendly() || !b.isActive())
                continue;
            for (Enemy e : enemies) {
                if (!e.isActive())
                    continue;
                if (b.getBounds().intersects(e.getBounds())) {
                    e.onCollision(b);
                    b.onCollision(e);
                    if (!e.isActive()) {
                        score.addScore(e.getScoreValue());
                        toAdd.add(new ExplosionEffect(e.getX() + e.getWidth() / 2f,
                                e.getY() + e.getHeight() / 2f));
                    }
                }
            }
        }

        // ── Enemy bullets vs Player ────────────────────────────
        for (Bullet b : bullets) {
            if (b.isFriendly() || !b.isActive())
                continue;
            if (b.getBounds().intersects(player.getBounds())) {
                player.onCollision(b);
                b.onCollision(player);
            }
        }

        // ── Enemies vs Player (contact damage) ────────────────
        for (Enemy e : enemies) {
            if (!e.isActive())
                continue;
            if (e.getBounds().intersects(player.getBounds())) {
                player.onCollision(e);
            }
        }

        // ── PowerUps vs Player ─────────────────────────────────
        for (PowerUp pu : powerUps) {
            if (!pu.isActive())
                continue;
            if (pu.getBounds().intersects(player.getBounds())) {
                pu.onCollision(player);
            }
        }

        return toAdd;
    }
}
