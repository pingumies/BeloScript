package com.patonki.interfaces;

import com.patonki.interfaces.BeloClass;

/**
 * BeloClass funktio, kuten print()
 */
public interface Command {
    BeloClass run(BeloClass[] parameters);
}
