package com.galacticdefender.ui;

import com.galacticdefender.utils.Constants;

import java.awt.*;

/**
 * Game-over overlay with final score and restart prompt.
 */
public class GameOverScreen {

    private float alpha = 0f;

    public void reset() {
        alpha = 0f;
    }

    public void update(float dt) {
        alpha = Math.min(1f, alpha + dt * 1.8f);
    }

    public void render(Graphics2D g, int finalScore, int highScore) {
        int W = Constants.WINDOW_WIDTH, H = Constants.WINDOW_HEIGHT;
        Composite prev = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.82f));
        g.setColor(new Color(5, 5, 10));
        g.fillRect(0, 0, W, H);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // GAME OVER title
        g.setFont(new Font("Consolas", Font.BOLD, 72));
        String go = "GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        int tx = (W - fm.stringWidth(go)) / 2;
        g.setColor(new Color(160, 30, 30));
        g.drawString(go, tx + 4, 222);
        g.setColor(new Color(230, 50, 50));
        g.drawString(go, tx, 218);

        // Scores
        g.setFont(new Font("Consolas", Font.PLAIN, 24));
        g.setColor(new Color(220, 220, 240));
        center(g, "Final Score :  " + finalScore, W, 290);
        g.setColor(new Color(255, 190, 50));
        center(g, "High Score  :  " + highScore, W, 322);

        if (finalScore >= highScore && finalScore > 0) {
            g.setFont(new Font("Consolas", Font.BOLD, 17));
            g.setColor(new Color(100, 230, 120));
            center(g, "★  NEW HIGH SCORE  ★", W, 356);
        }

        // Prompt
        float blink = (System.currentTimeMillis() / 540) % 2 == 0 ? 1.0f : 0.3f;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * blink));
        g.setFont(new Font("Consolas", Font.BOLD, 20));
        g.setColor(new Color(100, 220, 130));
        center(g, "[ Press  R  to  Restart ]", W, 420);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setFont(new Font("Consolas", Font.PLAIN, 14));
        g.setColor(new Color(130, 130, 160));
        center(g, "Press  ESC  to  Quit", W, 450);

        g.setComposite(prev);
    }

    private void center(Graphics2D g, String s, int W, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, (W - fm.stringWidth(s)) / 2, y);
    }
}
