package com.patonki.util;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 * Ei vielä käytössä...
 */
public class GlobalKeyListener implements NativeKeyListener {
    private int releasedKey = 0;
    private boolean changed = false;
    public GlobalKeyListener() {

    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        releasedKey = nativeKeyEvent.getKeyCode();
        changed = true;
    }

    public int getReleasedKey() {
        if (changed) {
            changed = false;
            return releasedKey;
        }
        return 0;
    }
}
