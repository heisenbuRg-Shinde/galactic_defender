package com.galacticdefender.ui;

import com.galacticdefender.objects.Player;
import com.galacticdefender.managers.ScoreManager;
import com.galacticdefender.managers.LevelManager;
import com.galacticdefender.utils.Constants;

import java.awt.*;

/**
 * Renders the in-game HUD overlay:
 * health bar, score, high score, level indicator, and active power-up timers.
 */
public class GameScreen {

    public void render(Graphics2D g, Player player, ScoreManager score, LevelManager level) {
        int W = Constants.WINDOW_WIDTH;

        // ── Health Bar ─────────────────────────────────────────
        int hx = 16, hy = 14, hw = 200, hh = 16;
        g.setColor(new Color(20, 8, 8, 210));
        g.fillRoundRect(hx, hy, hw, hh, 6, 6);

        float ratio = (float) player.getHealth() / player.getMaxHealth();
        Color hpColor = ratio > 0.6f ? new Color(60, 210, 80)
                : ratio > 0.3f ? new Color(240, 180, 40)
                        : new Color(230, 45, 45);
        g.setPaint(new GradientPaint(hx, 0, hpColor.brighter(), hx + (int) (hw * ratio), 0, hpColor));
        g.fillRoundRect(hx, hy, (int) (hw * ratio), hh, 6, 6);
        g.setPaint(null);
        g.setColor(new Color(180, 190, 210));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(hx, hy, hw, hh, 6, 6);
        g.setStroke(new BasicStroke(1f));

        g.setFont(new Font("Consolas", Font.BOLD, 11));
        g.setColor(Color.WHITE);
        g.drawString("HP  " + player.getHealth() + " / " + player.getMaxHealth(), hx + 4, hy + hh - 2);

        // ── Score panel ────────────────────────────────────────
        g.setColor(new Color(15, 18, 28, 200));
        g.fillRoundRect(W / 2 - 90, 8, 180, 28, 8, 8);
        g.setFont(new Font("Consolas", Font.BOLD, 14));
        g.setColor(new Color(255, 200, 60));
        String sc = "SCORE  " + score.getScore();
        FontMetrics fm = g.getFontMetrics();
        g.drawString(sc, W / 2 - fm.stringWidth(sc) / 2, 26);

        // High score
        g.setFont(new Font("Consolas", Font.PLAIN, 11));
        g.setColor(new Color(160, 170, 200));
        String hs = "BEST  " + score.getHighScore();
        fm = g.getFontMetrics();
        g.drawString(hs, W / 2 - fm.stringWidth(hs) / 2, 44);

        // ── Level badge ────────────────────────────────────────
        g.setColor(new Color(15, 18, 28, 200));
        g.fillRoundRect(W - 110, 8, 96, 28, 8, 8);
        g.setFont(new Font("Consolas", Font.BOLD, 14));
        g.setColor(new Color(100, 200, 255));
        String lv = level.isBossLevel() ? "BOSS!" : "LEVEL  " + level.getLevel();
        fm = g.getFontMetrics();
        g.drawString(lv, W - 110 + (96 - fm.stringWidth(lv)) / 2, 26);

        // ── Active power-up timers ─────────────────────────────
        int px = 16, py = 42;
        if (player.isDoubleShotActive()) {
            drawPowerTimer(g, "2x", new Color(255, 200, 50), player.getDoubleShotTimer(),
                    Constants.DOUBLE_BULLET_DURATION, px, py);
            px += 68;
        }
        if (player.isShieldActive()) {
            drawPowerTimer(g, "SH", new Color(80, 160, 255), player.getShieldTimer(),
                    Constants.SHIELD_DURATION, px, py);
            px += 68;
        }
        if (player.isSpeedBoostActive()) {
            drawPowerTimer(g, ">>", new Color(50, 230, 130), player.getSpeedBoostTimer(),
                    Constants.SPEED_BOOST_DURATION, px, py);
        }
    }

    private void drawPowerTimer(Graphics2D g, String lbl, Color c,
            long remaining, long total, int x, int y) {
        g.setColor(new Color(18, 22, 34, 200));
        g.fillRoundRect(x, y, 60, 20, 6, 6);
        float ratio = (float) remaining / total;
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 180));
        g.fillRoundRect(x, y, (int) (60 * ratio), 20, 6, 6);
        g.setColor(c);
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x, y, 60, 20, 6, 6);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Consolas", Font.BOLD, 11));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(lbl, x + (60 - fm.stringWidth(lbl)) / 2, y + 14);
    }
}
