package com.patonki.datatypes;

import com.patonki.interfaces.BeloClass;

import java.util.Arrays;

/**
 * Taulukko
 */
public class Array extends DataType{
    private final BeloClass[] array;

    /**
     * Parametrit:
     *      1. Taulukon koko
     *      2. Oletus arvo
     *      3... aloitusarvot
     */
    public Array(BeloClass[] params) {
        if (params.length>=1) {
            array = new BeloClass[(int) params[0].doubleValue()];
            if (params.length>= 2) Arrays.fill(array,params[1].getReference());
        } else throw new IllegalArgumentException("Array needs a size!");
        for (int i = 0; i+2<params.length; i++) {
            array[i] = params[i+2].getReference();
        }
    }

    @Override
    public BeloClass index(BeloClass beloClass) {
        return array[(int) beloClass.doubleValue()];
    }
}
