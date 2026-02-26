package com.galacticdefender.objects;

import com.galacticdefender.utils.Constants;

import java.awt.*;

/** Temporarily increases player movement speed by 1.5×. */
public class SpeedBoostPowerUp extends PowerUp {
    public SpeedBoostPowerUp(float x, float y) {
        super(x, y);
        this.label = ">>";
        this.color = new Color(50, 230, 130);
    }

    @Override
    public void applyEffect(Player player) {
        player.activateSpeedBoost(Constants.SPEED_BOOST_DURATION);
    }
}
