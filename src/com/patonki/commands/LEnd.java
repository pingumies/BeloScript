package com.patonki.commands;

import com.patonki.interfaces.BeloClass;
import com.patonki.execution.Executor;

/**
 * Komento, joka suoritettaessa siirtää koodin suorituksen silmukan alkuun
 * LEnd = Loop end
 */
public class LEnd implements BeloClass {
    private final Executor executor;

    public LEnd(Executor executor) {
        this.executor = executor;
    }

    @Override
    public BeloClass update() {
        executor.continueLoop();
        return null;
    }
}
