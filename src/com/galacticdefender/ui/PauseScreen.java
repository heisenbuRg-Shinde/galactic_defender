package com.galacticdefender.ui;

import com.galacticdefender.utils.Constants;

import java.awt.*;

/**
 * Semi-transparent pause overlay with resume and quit options.
 */
public class PauseScreen {

    public void render(Graphics2D g) {
        int W = Constants.WINDOW_WIDTH, H = Constants.WINDOW_HEIGHT;
        Composite prev = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.62f));
        g.setColor(new Color(8, 10, 20));
        g.fillRect(0, 0, W, H);
        g.setComposite(AlphaComposite.SrcOver);

        // Panel
        g.setColor(new Color(22, 27, 42, 230));
        g.fillRoundRect(W / 2 - 160, H / 2 - 100, 320, 200, 16, 16);
        g.setColor(new Color(80, 100, 160, 180));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(W / 2 - 160, H / 2 - 100, 320, 200, 16, 16);
        g.setStroke(new BasicStroke(1f));

        g.setFont(new Font("Consolas", Font.BOLD, 36));
        g.setColor(new Color(200, 210, 255));
        center(g, "PAUSED", W, H / 2 - 42);

        g.setFont(new Font("Consolas", Font.PLAIN, 16));
        g.setColor(new Color(160, 170, 200));
        center(g, "P  ·  Resume", W, H / 2 + 8);
        center(g, "ESC  ·  Quit to Desktop", W, H / 2 + 36);

        g.setComposite(prev);
    }

    private void center(Graphics2D g, String s, int W, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, (W - fm.stringWidth(s)) / 2, y);
    }
}
