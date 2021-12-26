package com.patonki.datatypes;

import com.patonki.interfaces.BeloClass;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Lista.
 * Tarjoaa toiminnallisuudet lisäämiseen ja poistamiseen.
 */
public class List extends DataType {
    private final ArrayList<BeloClass> values;

    public List(BeloClass[] initialValues) {
        values = new ArrayList<>();
        Collections.addAll(values, initialValues);
    }

    @Override
    public BeloClass index(BeloClass beloClass) {
        return values.get((int) beloClass.doubleValue());
    }

    @Override
    public BeloClass classFunction(BeloClass[] params) {
        String command = params[1].toString();
        switch (command) {
            case "size":
                return new BeloDouble(values.size());
            case "add":
                values.add(params[2].getReference());
                return params[2];
        }
        throw new IllegalArgumentException("Not a class method/value: "+ command);
    }
}
