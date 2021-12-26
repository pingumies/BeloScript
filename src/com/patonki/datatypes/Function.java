package com.patonki.datatypes;

import com.patonki.interfaces.BeloClass;
import com.patonki.interfaces.Command;

import java.time.temporal.ValueRange;

/**
 * Funktio datatyyppi. Arvo muodostuu suorittamalla funktio. Ei voi tallentaa muuttujaan.
 */
public class Function implements BeloClass {
    private final Command command;
    private final BeloClass[] parameters;
    private final BeloClass[] input;

    public Function(Command command, BeloClass[] parameters) {
        this.command = command;
        this.parameters = parameters;
        input = new BeloClass[parameters.length];
    }

    @Override
    public BeloClass update() {
        for (int i = 0; i < parameters.length; i++) {
            input[i] = parameters[i].update();
        }
        return this.command.run(input);
    }
}
