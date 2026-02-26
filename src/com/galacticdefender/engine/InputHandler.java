package com.galacticdefender.engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * KeyListener that tracks which keys are currently held and which were
 * just pressed this frame. Demonstrates encapsulation of input state.
 */
public class InputHandler extends KeyAdapter {

    private final Set<Integer> held = Collections.synchronizedSet(new HashSet<>());
    private final Set<Integer> justPressed = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void keyPressed(KeyEvent e) {
        if (!held.contains(e.getKeyCode()))
            justPressed.add(e.getKeyCode());
        held.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        held.remove(e.getKeyCode());
    }

    /** True while the key is held down. */
    public boolean isHeld(int keyCode) {
        return held.contains(keyCode);
    }

    /**
     * True only on the first frame the key was pressed; clears the flag
     * automatically.
     */
    public boolean isJustPressed(int keyCode) {
        return justPressed.remove(keyCode);
    }
}
