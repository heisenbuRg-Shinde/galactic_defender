package com.galacticdefender.engine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton-style utility for loading and caching image assets from disk.
 * Demonstrates encapsulation and file-handling exception management.
 */
public final class ResourceLoader {
    private ResourceLoader() {
    }

    private static final Map<String, BufferedImage> cache = new HashMap<>();

    /**
     * Load an image from disk (cached after first load).
     * Returns null and logs a warning if the file cannot be read.
     */
    public static BufferedImage loadImage(String path) {
        if (cache.containsKey(path))
            return cache.get(path);
        try {
            File f = new File(path);
            if (!f.exists())
                throw new IOException("Missing: " + path);
            BufferedImage img = ImageIO.read(f);
            cache.put(path, img);
            return img;
        } catch (IOException e) {
            System.err.println("[ResourceLoader] " + e.getMessage());
            return null;
        }
    }

    /**
     * Scale a BufferedImage to the target width × height using bicubic
     * interpolation.
     */
    public static BufferedImage scaleImage(BufferedImage src, int w, int h) {
        if (src == null)
            return null;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return out;
    }

    public static void clearCache() {
        cache.clear();
    }
}
