package com.patonki.interfaces;

import com.patonki.execution.Executor;

import java.util.HashMap;

/**
 * BeloScript kirjasto. Tarjoaa toiminnallisuuden lisätä funktioita.
 */
public interface Library {
    void load(HashMap<String, Command> commands);
    default void setExecutor(Executor executor) {

    }
}
