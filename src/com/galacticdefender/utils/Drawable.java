package com.galacticdefender.utils;

import java.awt.Graphics2D;

/**
 * Interface for any object that can be drawn to the screen.
 * Demonstrates interface-based abstraction (Drawable contract).
 */
public interface Drawable {
    void draw(Graphics2D g);
}
