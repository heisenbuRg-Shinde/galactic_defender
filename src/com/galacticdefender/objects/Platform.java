package com.galacticdefender.objects;

import java.awt.*;

/**
 * A static collidable platform tile that makes up the level geometry.
 * Rendered with an industrial metal aesthetic.
 */
public class Platform extends GameObject {

    public Platform(float x, float y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void update(float dt) {
        /* static – no movement */ }

    @Override
    public void render(Graphics2D g) {
        // Gradient body
        Paint prev = g.getPaint();
        g.setPaint(new GradientPaint(0, y, new Color(62, 68, 82),
                0, y + height, new Color(38, 42, 52)));
        g.fillRect((int) x, (int) y, width, height);
        g.setPaint(prev);

        // Top-edge highlight
        g.setColor(new Color(120, 128, 148));
        g.fillRect((int) x, (int) y, width, 4);

        // Outline
        g.setColor(new Color(90, 98, 118));
        g.drawRect((int) x, (int) y, width - 1, height - 1);

        // Rivets
        g.setColor(new Color(155, 162, 180));
        for (int ox = 14; ox < width - 10; ox += 48) {
            g.fillOval((int) x + ox, (int) y + 7, 5, 5);
        }
    }
}
