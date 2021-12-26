package com.patonki.util;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ei vielä käytössä
 */
public class Listener implements Runnable{
    private GlobalKeyListener globalKeyListener;
    @Override
    public void run() {
        try {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.WARNING);
            logger.setUseParentHandlers(false);
            GlobalScreen.registerNativeHook();
            globalKeyListener = new GlobalKeyListener();
            GlobalScreen.addNativeKeyListener(globalKeyListener);
            //GlobalScreen.addNativeMouseListener(new GlobalMouseListener(script));
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }
    public int getKey() {
        return globalKeyListener.getReleasedKey();
    }
}
