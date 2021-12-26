package com.patonki.execution;

/**
 * Data luokka, joka sisältää hyödyllistä tietoa siitä miten koodi pystyy hyppimään paikasta toiseen.
 * Esimerkiksi continue metodia käytettäessä tämä on hyödyllistä, sillä silloin koodi tietää heti
 *  minne mennä.
 */
public class Line {
    public int nextEnd;
    public int lastStart;
    public int nextLoopEnd;
    public int lastLoopStart;

    public Line(int nextEnd, int lastStart, int nextLoopEnd, int lastLoopStart) {
        this.nextEnd = nextEnd;
        this.lastStart = lastStart;
        this.nextLoopEnd = nextLoopEnd;
        this.lastLoopStart = lastLoopStart;
    }
}
