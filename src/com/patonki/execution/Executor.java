package com.patonki.execution;

import com.patonki.interfaces.BeloClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Luokka, jonka tehtävä on ajaa BeloClass taulukoita.
 * .belo tiedoston voi muuttaa BeloClass taulukoksi {@link BeloScript5} luokan avulla.
 */
public class Executor {
    private int index = 0;
    private boolean wait = false;
    private boolean stop=false;
    private BeloClass[] lines;
    private Line[] info;
    private static final Logger LOGGER = LogManager.getLogger(Executor.class);

    /**
     * Funktio, joka aloittaa ohjelman suorittamisen
     * @param lines BeloClass taulukko
     * @param info Riveihin liittyvä tieto
     * @throws Exception Jokin meni pieleen
     */
    public void run(BeloClass[] lines, Line[] info) throws Exception {
        LOGGER.info("Executor running!");
        this.info = info;
        this.lines = lines;
        //Results tallentaa sen mitä funktiot palauttavat, mutta eivät tallenna minnekään muualle
        results = new BeloClass[lines.length];
        execute(0,lines.length);
        LOGGER.info("Executor stopped running\n\n\n");
    }

    public int getIndex() {
        return index;
    }

    private BeloClass[] results;

    public BeloClass getResult(int index) {
        return results[index];
    }

    /**
     * Suorittaa ohjelmaa rivi kerrallaan annetusta indeksistä toiseen
     * annettuun indeksiin. Odottaa niin kauan, kun wait muuttuja on true ja lopettaa
     * , jos stop muuttuja on true. Käyttää BeloClassin tarjoamaa metodia .update()
     * @param start ensimmäinen rivi, joka ajetaan
     * @param limit seuraava rivi viimeisen rivin jälkeen
     * @throws Exception Jokin meni pieleen
     */
    public void execute(int start, int limit) throws Exception {
        int ret = index; //indeksi mihin palata suorittamisen jälkeen
        index = start;
        while (index != limit) {
            while (wait) Thread.sleep(100);
            if (stop) {
                stop = false;
                break;
            }
            try {
                results[index] = lines[index].update();
            } catch (Exception e) {
                LOGGER.fatal("Error at line: "+index);
                throw e;
            }
            index++;
        }
        index = ret;
    }

    /**
     * Siirtää koodin suorituksen lohkon loppuun
     */
    public void skip() {
        setIndex(info[index+1].nextEnd);
    }

    /**
     * Siirtyy silmukan alkuun
     */
    public void continueLoop() {
        int lastLoopStart =info[index].lastLoopStart;
        setIndex(lastLoopStart);
    }

    /**
     * Siirtyy silmukan loppuun
     */
    public void breakLoop() {
        int lastLoopStart = info[index].lastLoopStart;
        int nextStop = info[lastLoopStart].nextLoopEnd;
        setIndex(nextStop);
    }
    public void setIndex(int index) {
        this.index = index;
    }
}
