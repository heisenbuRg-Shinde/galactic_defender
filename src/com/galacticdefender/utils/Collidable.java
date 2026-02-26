package com.galacticdefender.utils;

import com.galacticdefender.objects.GameObject;
import java.awt.Rectangle;

/**
 * Interface marking a game object as participating in collision detection.
 * Demonstrates interface usage and polymorphism.
 */
public interface Collidable {
    /** Returns the axis-aligned bounding rectangle used for collision checks. */
    Rectangle getBounds();

    /** Called when this object collides with another. */
    void onCollision(GameObject other);
}
