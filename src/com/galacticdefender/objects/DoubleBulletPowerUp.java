package com.galacticdefender.objects;

import com.galacticdefender.utils.Constants;

import java.awt.*;

/** Grants double-bullet mode for a fixed duration. */
public class DoubleBulletPowerUp extends PowerUp {
    public DoubleBulletPowerUp(float x, float y) {
        super(x, y);
        this.label = "2x";
        this.color = new Color(255, 200, 50);
    }

    @Override
    public void applyEffect(Player player) {
        player.activateDoubleShot(Constants.DOUBLE_BULLET_DURATION);
    }
}
