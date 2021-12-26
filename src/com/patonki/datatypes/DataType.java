package com.patonki.datatypes;

import com.patonki.interfaces.BeloClass;

/**
 * Kaikki datatyypit perivät tämän luokan, koska tämä luokka mahdollistaa
 * arvojen tallentamisen muuttujiin.
 */
public class DataType implements BeloClass {
    private Variable var;
    @Override
    public BeloClass getVar() {
        if (var == null) return this;
        return var;
    }
    @Override
    public void setVar(Variable var) {
        this.var = var;
    }
}
