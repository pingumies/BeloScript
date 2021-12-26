package com.patonki.compiler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Yksinkertainen luokka, joka käyttää luokkia {@link CodePreprocessor} ja {@link CompileObject}
 * tiedoston compile prosessiin
 */
public class Compiler {
    private final CodePreprocessor codePreprocessor = new CodePreprocessor();
    private final CompileObject compileObject = new CompileObject();
    private static final Logger LOGGER = LogManager.getLogger(Compiler.class);

    public String compile(String rawCode) {
        LOGGER.info("Starting compilation process!");
        LOGGER.info("Passing to preprocessor:");
        String line = codePreprocessor.preprocess(rawCode);
        LOGGER.info("Passing argument to CompileObject");
        line = compileObject.processLine(line);
        LOGGER.info("Compilation successfully completed:\n"+line.replace(";","\n")+"\n\n\n");
        return line;
    }
}
