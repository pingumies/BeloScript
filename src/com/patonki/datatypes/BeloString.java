package com.patonki.datatypes;

import com.patonki.interfaces.BeloClass;

/**
 * BeloScriptin merkkijono arvo
 */
public class BeloString extends DataType {
    private final String value;
    public BeloString(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return value;
    }

    @Override
    public BeloClass getReference() {
        return new BeloString(this.value);
    }

    @Override
    public BeloClass index(BeloClass beloClass) {
        return new BeloString(String.valueOf(value.charAt((int) beloClass.doubleValue())));
    }

    @Override
    public BeloClass add(BeloClass another) {
        return new BeloString(value.concat(another.toString()));
    }

    @Override
    public BeloClass classFunction(BeloClass[] params) {
        String classFunc = params[1].toString();
        switch (classFunc) {
            case "length":
                return new BeloDouble(value.length());
            case "capitalize":
                return new BeloString(value.substring(0,1).toUpperCase()+value.substring(1));
        }
        throw new IllegalArgumentException("Not a class member/method: "+classFunc);
    }
}
