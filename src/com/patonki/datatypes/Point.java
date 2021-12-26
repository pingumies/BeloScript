package com.patonki.datatypes;

import com.patonki.interfaces.BeloClass;

/**
 * Tallentaa kolme arvoa x,y ja z
 */
public class Point extends DataType{
    private BeloDouble x,y,z;

    public Point(BeloClass[] params) {
        if (params.length>= 1) x = new BeloDouble(params[0].doubleValue());
        if (params.length>= 2) y = new BeloDouble(params[1].doubleValue());
        if (params.length== 3) z = new BeloDouble(params[2].doubleValue());
        if (params.length > 3) {
            throw new IllegalArgumentException("Too many parameters for point class: "+params.length+" expected max: "+3);
        }
    }

    @Override
    public BeloClass classFunction(BeloClass[] params) {
        String command = params[1].toString();
        switch (command) {
            case "x":
                return x;
            case "y":
                return y;
            case "z":
                return z;
        }
        throw new IllegalArgumentException("Not a class member/value: "+command+" class: BeloPoint");
    }
}
