package com.patonki.datatypes;

import com.patonki.interfaces.BeloClass;

/**
 * Datatyyppi, jonka arvoa voi muuttaa.
 */
public class Variable extends DataType {
    private BeloClass value;

    @Override
    public void set(BeloClass beloClass) {
        this.value = beloClass;
        if (this.value instanceof Variable) throw new IllegalStateException("Not supposed to happen!");
        this.value.setVar(this);
    }

    @Override
    public BeloClass getReference() {
        return this.value.getReference();
    }

    @Override
    public BeloClass update() {
        if (value == null) return this; //ei vielä määritelty
        return value;
    }
}
