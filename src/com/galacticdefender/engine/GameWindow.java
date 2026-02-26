package com.galacticdefender.engine;

import com.galacticdefender.utils.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Application window (JFrame wrapper).
 * Sets up the frame, adds the GamePanel, and configures rendering hints.
 */
public class GameWindow extends JFrame {

    private final GamePanel panel;

    public GameWindow(GamePanel panel, InputHandler input) {
        super("Galactic Defender");
        this.panel = panel;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
        addKeyListener(input);
        setVisible(true);

        // Create BufferStrategy for active rendering
        createBufferStrategy(2);
    }

    /**
     * Render one frame via the BufferStrategy (active rendering with
     * double-buffering).
     */
    public void renderFrame(GamePanel gamePanel) {
        var bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(2);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        gamePanel.renderFrame(g);

        g.dispose();
        bs.show();
        Toolkit.getDefaultToolkit().sync();
    }
}
