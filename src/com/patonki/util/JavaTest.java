package com.patonki.util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Käytetään, kun vertaillaan javan suorituskykyä BeloScriptin suorituskykyyn
 */
public class JavaTest {
    private static final Logger LOGGER = LogManager.getLogger(JavaTest.class);
    public static void main(String[] args) {
        LOGGER.debug("debug");
        LOGGER.info("info");
        LOGGER.warn("warning");
        LOGGER.error("Error");
        LOGGER.fatal("FATAL");
    }
}
