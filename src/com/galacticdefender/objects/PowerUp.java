package com.galacticdefender.objects;

import com.galacticdefender.utils.Collidable;
import com.galacticdefender.utils.Constants;
import com.galacticdefender.utils.World;

import java.awt.*;

/**
 * Abstract base for all collectible power-ups.
 * Implements Collidable so CollisionManager can detect player pickup.
 * Subclasses override applyEffect() – demonstrates polymorphism.
 */
public abstract class PowerUp extends GameObject implements Collidable {

    protected String label;
    protected Color color;
    private float pulse = 0f;

    protected PowerUp(float x, float y) {
        super(x, y, 34, 34);
        this.velY = 70f; // falls from spawn point
    }

    /** Apply this power-up's effect to the player. Overridden by subclasses. */
    public abstract void applyEffect(Player player);

    @Override
    public void update(float dt) {
        velY += Constants.GRAVITY * dt;
        y += velY * dt;
        // Land on platforms
        for (Platform p : World.platforms) {
            Rectangle pb = p.getBounds();
            if (getBounds().intersects(pb) && velY >= 0
                    && (y + height) - velY * dt <= pb.y + 4) {
                y = pb.y - height;
                velY = 0;
            }
        }
        if (y + height >= Constants.WINDOW_HEIGHT) {
            y = Constants.WINDOW_HEIGHT - height;
            velY = 0;
        }
        pulse += dt * 4f;
    }

    @Override
    public void render(Graphics2D g) {
        float bob = (float) Math.sin(pulse) * 3f;
        int rx = (int) x, ry = (int) (y + bob);

        // Glow aura
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 55));
        g.fillOval(rx - 8, ry - 8, 50, 50);

        // Spinning ring
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(2f));
        g.setColor(color);
        g.drawOval(rx - 4, ry - 4, 42, 42);
        g.setStroke(old);

        // Badge background
        g.setColor(new Color(28, 32, 44, 220));
        g.fillRoundRect(rx, ry, width, height, 10, 10);
        g.setColor(color);
        g.drawRoundRect(rx, ry, width, height, 10, 10);

        // Label text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 11));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label,
                rx + (width - fm.stringWidth(label)) / 2,
                ry + (height + fm.getAscent() - fm.getDescent()) / 2);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    @Override
    public void onCollision(GameObject other) {
        if (other instanceof Player) {
            applyEffect((Player) other);
            active = false;
        }
    }
}
