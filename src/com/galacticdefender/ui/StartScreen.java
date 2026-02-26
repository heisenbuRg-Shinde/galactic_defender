package com.galacticdefender.ui;

import com.galacticdefender.utils.Constants;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Rendered when Scene == START.
 * Draws the title, instructional text and handles key input to start/exit.
 */
public class StartScreen {

    private float titleAlpha = 0f;
    private float scanlineY = 0f;

    public void update(float dt) {
        if (titleAlpha < 1f)
            titleAlpha = Math.min(1f, titleAlpha + dt * 1.4f);
        scanlineY = (scanlineY + 60 * dt) % Constants.WINDOW_HEIGHT;
    }

    public void render(Graphics2D g) {
        int W = Constants.WINDOW_WIDTH, H = Constants.WINDOW_HEIGHT;

        // Dark base overlay (drawn on top of the background)
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, W, H);

        // Moving scanline for CRT effect
        g.setColor(new Color(255, 255, 255, 12));
        for (int y = (int) scanlineY; y < H; y += 6)
            g.fillRect(0, y, W, 1);

        // Title
        Composite prev = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));

        g.setFont(new Font("Consolas", Font.BOLD, 68));
        String title = "GALACTIC DEFENDER";
        FontMetrics fm = g.getFontMetrics();
        int tx = (W - fm.stringWidth(title)) / 2;

        // Shadow
        g.setColor(new Color(200, 80, 0));
        g.drawString(title, tx + 3, 195);
        // Main
        g.setColor(new Color(255, 140, 0));
        g.drawString(title, tx, 192);

        // Subtitle
        g.setFont(new Font("Consolas", Font.PLAIN, 18));
        String sub = "Industrial Combat  ·  2D Side-Scroller";
        int sx = (W - g.getFontMetrics().stringWidth(sub)) / 2;
        g.setColor(new Color(190, 190, 210));
        g.drawString(sub, sx, 228);

        // Controls box
        drawPanel(g, W / 2 - 200, 280, 400, 200);
        g.setFont(new Font("Consolas", Font.BOLD, 15));
        g.setColor(new Color(255, 200, 80));
        center(g, "CONTROLS", W, 314);
        g.setFont(new Font("Consolas", Font.PLAIN, 13));
        g.setColor(new Color(200, 210, 230));
        center(g, "← → Arrow Keys  ·  Move", W, 340);
        center(g, "↑ Arrow Key      ·  Jump", W, 360);
        center(g, "SPACE           ·  Shoot", W, 380);
        center(g, "P                ·  Pause", W, 400);
        center(g, "R                ·  Restart (Game Over)", W, 420);

        // Press ENTER to start
        float blink = (System.currentTimeMillis() / 500) % 2 == 0 ? 1.0f : 0.45f;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, blink * titleAlpha));
        g.setFont(new Font("Consolas", Font.BOLD, 22));
        g.setColor(new Color(100, 230, 120));
        center(g, "[ PRESS  ENTER  TO  START ]", W, 520);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));
        g.setFont(new Font("Consolas", Font.PLAIN, 13));
        g.setColor(new Color(140, 140, 160));
        center(g, "Press  ESC  to  Exit", W, 550);

        g.setComposite(prev);
    }

    private void drawPanel(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(20, 24, 35, 200));
        g.fillRoundRect(x, y, w, h, 14, 14);
        g.setColor(new Color(80, 90, 130, 180));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x, y, w, h, 14, 14);
        g.setStroke(new BasicStroke(1f));
    }

    private void center(Graphics2D g, String s, int W, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, (W - fm.stringWidth(s)) / 2, y);
    }
}
