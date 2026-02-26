package com.galacticdefender.objects;

import java.awt.*;
import java.util.Random;

/**
 * Particle-based explosion effect spawned on enemy death.
 * Self-deactivates once the animation completes.
 */
public class ExplosionEffect extends GameObject {

    private int frame;
    private static final int MAX_FRAMES = 25;
    private static final int NUM_PARTICLES = 18;

    private final float[] px, py, pvx, pvy;
    private static final Color[] COLORS = {
            new Color(255, 200, 50), new Color(255, 120, 30),
            new Color(255, 60, 20), new Color(230, 230, 230),
            new Color(255, 160, 60), new Color(200, 210, 255)
    };

    public ExplosionEffect(float cx, float cy) {
        super(cx - 50, cy - 50, 100, 100);
        px = new float[NUM_PARTICLES];
        py = new float[NUM_PARTICLES];
        pvx = new float[NUM_PARTICLES];
        pvy = new float[NUM_PARTICLES];
        Random rng = new Random();
        for (int i = 0; i < NUM_PARTICLES; i++) {
            px[i] = cx;
            py[i] = cy;
            double angle = rng.nextDouble() * Math.PI * 2;
            float speed = rng.nextFloat() * 180 + 60;
            pvx[i] = (float) Math.cos(angle) * speed;
            pvy[i] = (float) Math.sin(angle) * speed;
        }
    }

    @Override
    public void update(float dt) {
        frame++;
        for (int i = 0; i < NUM_PARTICLES; i++) {
            px[i] += pvx[i] * dt;
            py[i] += pvy[i] * dt;
            pvy[i] += 300 * dt; // gravity on particles
            pvx[i] *= 0.97f; // air friction
        }
        if (frame >= MAX_FRAMES)
            active = false;
    }

    @Override
    public void render(Graphics2D g) {
        float t = (float) frame / MAX_FRAMES;
        float alpha = Math.max(0f, 1f - t);
        Composite prev = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        for (int i = 0; i < NUM_PARTICLES; i++) {
            int sz = Math.max(2, (int) (14 - 10 * t));
            g.setColor(COLORS[i % COLORS.length]);
            g.fillOval((int) px[i] - sz / 2, (int) py[i] - sz / 2, sz, sz);
        }
        // Flash ring in first few frames
        if (frame < 7) {
            int r = frame * 11;
            g.setColor(new Color(255, 235, 120, (int) (180 * alpha)));
            Stroke old = g.getStroke();
            g.setStroke(new BasicStroke(3f));
            g.drawOval((int) (x + 50) - r, (int) (y + 50) - r, r * 2, r * 2);
            g.setStroke(old);
        }
        g.setComposite(prev);
    }
}
