package com.galacticdefender.objects;

import com.galacticdefender.utils.Constants;

import java.awt.*;

/** Activates a temporary invincibility shield. */
public class ShieldPowerUp extends PowerUp {
    public ShieldPowerUp(float x, float y) {
        super(x, y);
        this.label = "SH";
        this.color = new Color(80, 160, 255);
    }

    @Override
    public void applyEffect(Player player) {
        player.activateShield(Constants.SHIELD_DURATION);
    }
}
