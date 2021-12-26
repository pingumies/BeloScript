package com.patonki.interfaces;

import com.patonki.datatypes.Variable;

/**
 * Jokainen BeloScriptin arvo on BeloClass.
 * BeloClass sisältää kaikki toiminnallisuudet mitä BeloClass objekti voi tehdä.
 *  Jopa rivit jota ohjelma ajaa ovat BeloClass tyyppisiä
 */
public interface BeloClass {
    /**
     * Palauttaa double tyyppisen arvon
     * @return double
     */
    default double doubleValue() {
        throw new IllegalStateException("Double value not defined for this class");
    }

    /**
     * Kutsutaan, kun käytetään [] operaattoria
     * @param beloClass indeksi
     * @return elementti indeksissä
     */
    default BeloClass index(BeloClass beloClass) {throw new IllegalStateException("[] operator not defined for this class");}

    /**
     * Kutsutaan, kun käytetään = operaattoria
     * @param beloClass uusi arvo
     */
    default void set(BeloClass beloClass) {
        throw new IllegalStateException("Can not set this variable "+getClass().getName());
    }

    /**
     * Palauttaa itsensä, jos viittaustyyppinen arvo, mutta esim. BeloDouble palauttaa arvonsa.
     * @return Arvo tai viittaus
     */
    default BeloClass getReference() {return this;}

    /**
     * Palauttaa muuttujan johon arvo on tallennettu. Luokka {@link com.patonki.datatypes.DataType}
     * hoitaa yleensä tämän. Jos arvo ei ole tallennettu muuttujaan, palauttaa itsensä.
     * @return Muuttuja johon asetettu.
     */
    default BeloClass getVar() {return this;}

    /**
     * Asettaa muuttujan, johon arvo on tallennettu. Luokka {@link com.patonki.datatypes.DataType}
     * hoitaa yleensä tämän.
     * @param var Muuttuja johon arvo tallennetaan
     */
    default void setVar(Variable var) {}

    /**
     * Kutsutaan, kun käytetään operaattoria .
     * @param params 1. itse arvo, 2. merkkijono komento. 3... arvot
     * @return Luokka metodin palautusarvo
     */
    default BeloClass classFunction(BeloClass[] params) {throw new IllegalStateException(". operator not defined for this class");}

    /**
     * Kutsutaan, kun luokan arvo halutaan tietää. Ei ikinä kutsuta itse. Tätä funktiota kutsutaan
     * vain sisäisesti. Palauttaa oletuksena itsensä.
     * @return Arvo
     */
    default BeloClass update() {
        return this;
    }

    /**
     * Kutsutaan, kun käytetään operaattoria ++. Mutta se ei ole vielä lisätty ohjelmointikieleen.
     * @return Arvo lisäämisen jälkeen
     */
    default BeloClass plusplus() {throw new IllegalStateException("++ operator not defined for this class");}
    /**
     * Kutsutaan, kun käytetään operaattoria --. Mutta se ei ole vielä lisätty ohjelmointikieleen.
     * @return Arvo vähentämisen jälkeen
     */
    default BeloClass minusminus() {throw new IllegalStateException("-- operator not defined for this class");}

    /**
     * Kutsutaan, kun käytetään operaattoria ==.
     * @param another verrattava luokka
     * @return BeloDouble &gt;=1 = true, muuten false
     */
    default boolean equals(BeloClass another) {
        throw new IllegalStateException("== not defined for this class!");
    }

    /**
     * Kutsutaan, kun käytetään operaattoria &lt;.
     * @param another verrattava luokka
     * @return BeloDouble &gt;=1 = true, muuten false
     */
    default boolean lessThan(BeloClass another) {
        throw new IllegalStateException("< not defined for this class!");
    }
    /**
     * Kutsutaan, kun käytetään operaattoria &gt;.
     * @param another verrattava luokka
     * @return BeloDouble &gt;=1 = true, muuten false
     */
    default boolean moreThan(BeloClass another) {
        throw new IllegalStateException("> not defined for this class!");
    }

    /**
     * Kutsutaan, kun käytetään operaattoria: ^
     * @param another Toinen arvo
     * @return toinen arvo ^ tämä arvo
     */
    default BeloClass pow(BeloClass another) {
        throw new IllegalStateException("^ not defined for this class!");
    }
    /**
     * Kutsutaan, kun käytetään operaattoria: *
     * @param another Toinen arvo
     * @return toinen arvo * tämä arvo
     */
    default BeloClass multiply(BeloClass another) {
        throw new IllegalStateException("* not defined for this class!"+getClass().getName());
    }
    /**
     * Kutsutaan, kun käytetään operaattoria: /
     * @param another Toinen arvo
     * @return toinen arvo / tämä arvo
     */
    default BeloClass divide(BeloClass another) {
        throw new IllegalStateException("/ not defined for this class!");
    }
    /**
     * Kutsutaan, kun käytetään operaattoria: +
     * @param another Toinen arvo
     * @return toinen arvo + tämä arvo
     */
    default BeloClass add(BeloClass another) {
        throw new IllegalStateException("+ not defined for this class!");
    }
    /**
     * Kutsutaan, kun käytetään operaattoria: -
     * @param another Toinen arvo
     * @return toinen arvo - tämä arvo
     */
    default BeloClass substract(BeloClass another) {
        throw new IllegalStateException("- not defined for this class!");
    }
    /**
     * Kutsutaan, kun käytetään operaattoria: %
     * @param another Toinen arvo
     * @return toinen arvo % tämä arvo
     */
    default BeloClass remainder(BeloClass another) {
        throw new IllegalStateException("% not defined for this class!");
    }
}
