package com.patonki.datatypes;

import com.patonki.interfaces.BeloClass;

/**
 * BeloScriptin luku arvo
 */
public class BeloDouble extends DataType {
    private final double value;

    public BeloDouble(double value) {
        this.value = value;
    }

    @Override
    public BeloClass getReference() {
        return new BeloDouble(value);
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(BeloClass another) {
        return value == another.doubleValue();
    }

    @Override
    public boolean lessThan(BeloClass another) {
        return value < another.doubleValue();
    }

    @Override
    public boolean moreThan(BeloClass another) {
        return value > another.doubleValue();
    }

    @Override
    public BeloClass pow(BeloClass another) {
        return new BeloDouble(Math.pow(value,another.doubleValue()));
    }

    @Override
    public BeloClass multiply(BeloClass another) {
        return new BeloDouble(value * another.doubleValue());
    }

    @Override
    public BeloClass divide(BeloClass another) {
        return new BeloDouble(value / another.doubleValue());
    }

    @Override
    public BeloClass add(BeloClass another) {
        try {
            return new BeloDouble(value + another.doubleValue());
        } catch (IllegalStateException e) {
            return new BeloString(value + another.toString());
        }
    }

    @Override
    public BeloClass substract(BeloClass another) {
        try {
            return new BeloDouble(value - another.doubleValue());
        } catch (IllegalStateException e) {
            return new BeloDouble(value- Double.parseDouble(another.toString()));
        }
    }

    @Override
    public BeloClass remainder(BeloClass another) {
        return new BeloDouble(value % another.doubleValue());
    }
}
