package com.patonki.commands;

import com.patonki.interfaces.BeloClass;
import com.patonki.interfaces.Command;
import com.patonki.execution.Executor;
import com.patonki.datatypes.Variable;

/**
 * Koodissa määritelty funktio.
 * Suorittaessa siirtää indeksin funktion alkuun ja kutsuu Executorin funktiota execute
 */
public class UserDefinedFunction implements Command {
    private final Variable[] localVariables;
    private final Executor executor;
    private final int startIndex;
    private final int endIndex;

    public UserDefinedFunction(Variable[] localVariables, Executor executor, int startIndex, int endIndex) {
        this.localVariables = localVariables;
        this.executor = executor;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public BeloClass run(BeloClass[] parameters) {
        if (parameters.length != this.localVariables.length) throw new IllegalArgumentException("Not enough parameters");
        for (int i = 0; i < parameters.length; i++) {
            //asettaa muuttujat
            this.localVariables[i].set(parameters[i]);
        }
        try {
            //Siirtää indeksin funktion aloitus kohtaan.
            executor.execute(startIndex,endIndex);
            //lopettaa suorituksen, kun funktion loppuun saavutaan
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
