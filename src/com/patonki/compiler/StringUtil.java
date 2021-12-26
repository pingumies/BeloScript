package com.patonki.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Sisältää apu metodeja, jotka auttavat koodin lukemisessa ja käsittelyssä
 */
public class StringUtil {
    private final HashMap<Character,Character> vastinOsat = new HashMap<>();

    /**
     * Palauttaa True, jos kirjain sopii muuttujan tai funktion nimeen
     * Sopivat kirjaimet: a-z, A-Z, 0-9 ja _
     * @param c tarkastettava kirjain
     * @return true, jos kirjain käy nimeen
     */
    public boolean onNimiKirjain(char c) {
        if (c >= 'a' && c <= 'z') return true;
        if (c >= 'A' && c <= 'Z') return true;
        if (c >= '0' && c <= '9') return true;
        if (c == '_') return true;
        return false;
    }

    /**
     * Jakaa koodin osiin ottaen huomioon merkkijonot ja functiot
     * Etsittävää kirjainta ei oteta huomioon, jos se on merkkijonon sisällä tai function sisällä
     * Toimii suunnilleen samalla tavalla, kuin String.split()
     * Esim.
     * s = "hei,", getNimi(format,nimi,aika)
     * split = ','
     * palauttaa: "hei," ja getNimi(format,nimi,aika)
     * @param s Jaettava merkkijono
     * @param split Kirjain, jonka kohdalta jaetaan
     * @return lista, joka sisältää merkkijonoja
     */
    public ArrayList<String> split(String s, char split) {
        //Str
        boolean insideString = false;
        // Tallentaa lopulliset jaetut merkkijonot
        ArrayList<String> list = new ArrayList<>();
        //Muistetaan kohta, josta viimeksi leikattiin
        int lastSplit = -1;
        // Pitää lukua siitä kuinka syvällä funktioissa ollaan
        int deepness = 0;
        // Käy läpi jokaisen kirjaimen alusta alkaen
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{') deepness++; //Functio alkaa
            if (c == '}') deepness--; //Functio loppuu
            if (c == '"') insideString = !insideString;
            if (insideString) continue; //Merkkijonojen sisäisiä asioita ei huomioida
            if (c == split && deepness == 0) {
                list.add(s.substring(lastSplit+1, i));
                lastSplit = i;
            }
        }
        // Viimeistä kohtaa ei leikata, koska se ei lopu haluttuun kirjaimeen
        if (lastSplit != s.length()-1) {
            list.add(s.substring(lastSplit+1));
        }
        return list;
    }

    /**
     * Toimii hyvin samalla tavalla, kuin normaali String.replace()
     * , mutta tämä funktio ottaa huomioon merkkijonot merkkijonon sisällä.
     * Esim: ""==",=="  replace("==", "**"), Korvaa vain jälkimmäisen
     * , koska se ei ole merkkijonon sisällä
     * @param s Merkkijono, jossa korvaus tehdään
     * @param replaceable korvattava
     * @param replacer korvaaja
     * @return merkkijonon, jossa korvattavat on korvattu korvaajalla
     */
    public String replace(String s, String replaceable, String replacer) {
        int times = 0;
        //Korvattava ei voi olla sama kuin korvaaja
        if (replaceable.equals(replacer)) throw new IllegalArgumentException("Replacer can't equal replacer");
        int start = 0;
        //Korvaa korvattavia, kunnes niitä ei enää löydy
        while (true) {
            times++; if (times == 100000) throw new IllegalArgumentException("Endless loop!");
            //Tallentaa merkkijonon alkuperäisen tilan
            String before = s;
            //Kuinka iso osa korvattavasta merkkijonosta ollaan löydetty
            int progress = 0;
            boolean insideString = false;
            for (int i = start; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '"') {
                    if (!insideString) { //Jos siirrytään merkkijonon sisälle, aloitetaan korvattavan etsiminen alusta
                        insideString = true;
                        progress = 0;
                    } else insideString = false;
                }
                if (insideString) continue; // Jätetään huomioimatta kaikki merkkijonojen sisällä olevat

                if (c == replaceable.charAt(progress)) progress++;
                else progress = 0;

                if (progress == replaceable.length()) {
                    start = i-progress+1+replacer.length();
                    s = s.substring(0,i-progress+1)+replacer+s.substring(i+1);
                    break; // Aloitetaan uusi looppi, koska merkkijonon pituus muuttuu
                }
            }
            //Jos merkkijono ei muuttunut silmukan aikana, palautetaan merkkijono
            if (before.equals(s)) break;
        }
        return s;
    }

    /**
     * Toimii muuten samalla tavalla kuin String.indexOf(), mutta ei huomio merkkejä, jotka ovat lainausmerkkien sisällä
     * @param inputString Merkkijono, josta etsiä
     * @param toSearchFor Etsittävä merkkijono
     * @param start indeksi, josta aloitetaan
     * @return Merkkijonon alku indeksi
     */
    public int indexOf(String inputString, String toSearchFor, int start) {
        // Kertoo kuinka iso osa etsittävästä merkkijonosta on löydetty
        int progress = 0;
        boolean insideString = false;
        for (int i = start; i < inputString.length(); i++) {
            char c = inputString.charAt(i);
            if (c == '"') {
                if (!insideString) {
                    insideString = true;
                    progress = 0;
                } else insideString = false;
            }
            if (insideString) continue; //Lainausmerkkien sisällä olevia merkkejä ei huomioida
            if (c == toSearchFor.charAt(progress)) progress++;
            else progress = 0;

            if (progress == toSearchFor.length()) return i-toSearchFor.length()+1;
        }
        return -1; // Ei löytynyt
    }

    /**
     * Sama kuin {@link #indexOf(String, String, int)}, mutta menee eri suuntaan
     */
    public int lastIndexOf(String inputString, String toSearchFor, int start) {
        // Kertoo kuinka iso osa etsittävästä merkkijonosta on löydetty
        int progress = toSearchFor.length()-1;
        boolean insideString = false;
        for (int i = start; i >= 0; i--) {
            char c = inputString.charAt(i);
            if (c == '"') {
                if (!insideString) {
                    insideString = true;
                    progress = 0;
                } else insideString = false;
            }
            if (insideString) continue; //Lainausmerkkien sisällä olevia merkkejä ei huomioida
            if (c == toSearchFor.charAt(progress)) progress--;
            else progress = toSearchFor.length()-1;

            if (progress == -1) return i-toSearchFor.length()+1;
        }
        return -1; // Ei löytynyt
    }
    /**
     * Palauttaa lauseen vasemman puolen jakaen operaattorin kohdalta.
     * Esim. operaattori: ==, 89==3, palauttaa: 89
     * @param s Jaettava merkkijono
     * @param end operaattorin alku indeksi (89==3), indeksi:2
     * @return Lauseen vasen puoli
     */
    public String leftSide(String s, int end) {
        int start = startOfStatement(s,end);
        return s.substring(start+1,end);
    }

    /**
     * Toimii samalla tavalla kuin leftSide(), mutta oikea puoli
     * Tällä kertaa funktiolle annetaan operaattorin viimeinen indeksi.
     * @see #leftSide(String, int)
     */
    public String rightSide(String s, int start) {
        int end = endOfStatement(s,start+1);
        return s.substring(start+1,end);
    }

    /**
     * Palauttaa kohdan, josta lauseke alkaa.
     * Esim. print(89==3) indeksillä 8, palauttaa 5, eli kohdan, jossa on alkava sulku
     * Katso testit
     * @param s Merkkijono, josta etsitään
     * @param i Indeksi, josta aletaan mennä merkkijonon alkua kohti
     * @return Indeksi ennen lausekkeen alkua, indeksi+1 on ensimmäinen kirjain, joka kuuluu lausekkeeseen
     */
    public int startOfStatement(String s, int i) {
        int start = i;
        int deepness = 0;
        boolean insideString = false;
        i--;
        while (i > 0) {
            char c =s.charAt(i);
            if (c == '"') insideString = !insideString;
            if (!insideString) {
                if (c == '(') { //Jos löydetään funktion reuna, joka ei lopu ennen alkua
                    //Esim print(89==3) lausekkeesta saatetaan haluta erottaa 89==3 käsittelyä varten
                    //Jolloin lauseke alkaa aukeavasta sulkeesta
                    //Katso testit
                    if (rightCounterPart(s,i) > start) return i;
                }
                //sama kuin ylempänä
                if (c == '[') {
                    if (rightCounterPart(s,i) > start) return i;
                }
                if (c == '}') deepness++;
                if (c == '{') { //Sama homma kuin aukeavalla normaalilla sulkeella
                    if (deepness == 0) return i;
                    else deepness--;
                }
                if (c == ';') return i; // ;-merkki tarkoittaa rivivaihtoa
                if (c == ',' && deepness == 0) return i; //pilkku tarkoittaa että parametri vaihtuu
            }
            i--;
        }
        return -1;
    }

    /**
     * Sama kuin {@link #startOfStatement(String, int)}, mutta arvot vaihtuneet
     * Katso testit
     * @see #startOfStatement(String, int)
     */
    public int endOfStatement(String s, int i) {
        int start = i;
        int deepness = 0;
        boolean insideString = false;
        while (i < s.length()) {
            char c =s.charAt(i);
            if (c == '"') insideString = !insideString;
            if (!insideString) {
                if (c == ')') {
                    if (leftCounterPart(s,i) < start) return i;
                }
                //sama kuin ylempänä
                if (c == ']') {
                    if (leftCounterPart(s,i) < start) return i;
                }
                if (c == '{') deepness++;
                if (c == '}') {
                    if (deepness == 0) return i;
                    else deepness--;
                }
                if (c == ';') return i;
                if (c == ',' && deepness == 0) return i;
            }
            i++;
        }
        return s.length();
    }

    /**
     * Palauttaa merkkijonoista sulkujen sisäisen osan
     * Esim. hdj(hello)fds, palauttaa: (hello)
     * @param line Merkkijono, josta etsiä
     * @param start Avautuvan sulun indeksi
     * @return Sulkujen sisällä oleva osa ja sulut sen ympärillä
     */
    public String sulkeet(String line, int start) {
        int end = rightCounterPart(line,start);
        return line.substring(start,end+1);
    }
    public StringUtil() {
        // Määrittää vastinosat tietyille merkkijonoille
        vastinOsat.put('[',']');
        vastinOsat.put(']','[');
        vastinOsat.put('(',')');
        vastinOsat.put(')','(');
        vastinOsat.put('{','}');
        vastinOsat.put('}','{');
    }

    /**
     * Etsii vasemmalta vastinosan alkaen indeksistä. Esim. Avautuvan sulun vastinosa on sulkeutuva sulku.
     * Ei huomioi lainausmerkkien sisällä olevia merkkejä.
     */
    public int leftCounterPart(String s, int start) {
        return getCounterPartIndex(s,start,-1);
    }

    /**
    * @see #leftCounterPart(String, int)
     */
    public int rightCounterPart(String s, int start) {
        return getCounterPartIndex(s,start,1);
    }
    //Palauttaa indeksin, josta vastin osa on kulkien jompaankumpaan suuntaan
    private int getCounterPartIndex(String s,int start, int direction) {
        char c = s.charAt(start);
        if (vastinOsat.get(c) == null) throw new IllegalArgumentException("Ei vastinosaa merkille:" +c);
        char counter = vastinOsat.get(c);
        int same = 0;
        boolean insideString = false; // Ei huomioida lainausmerkkien sisällä olevia merkkejä
        while (start >= 0 && start < s.length()) {
            if (s.charAt(start) == '"') insideString = !insideString;
            if (!insideString) {
                if (s.charAt(start) == c) same++;
                if (s.charAt(start) == counter) if (--same == 0) return start;
            }
            start += direction;
        }
        return -1; //Ei löytynyt
    }

    /**
     * Palauttaa rivi numeron, jossa silmukka loppuu.
     * @param start Kohta, josta silmukka alkaa. Eli kohta, jossa on lstart.
     * @param splitted rivit
     * @return riviIndeksi, jossa lend on
     */
    public int getLoopEnd(int start, List<String> splitted) {
        int deepness = 0; //silmukan sisällä saattaa olla sisäkkäisiä silmukoita
        for (int i = start; i <= splitted.size(); i++) {
            String s = splitted.get(i);
            if (s.equals("lstart")) {
                deepness++;
            }
            if (s.equals("lend")) {
                deepness--;
                if (deepness == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Etsii lohkon loppu indeksin.
     * @param start indeksi, josta lohko alkaa. Eli rivi, jossa on start
     * @param splitted rivit
     * @return Indeksin, jossa on end
     */
    public int getEnd(int start,List<String> splitted) {
        int deepness = 0;
        for (int i = start; i <= splitted.size(); i++) {
            String s = splitted.get(i);
            if (s.equals("start")) deepness++;
            if (s.equals("end")) deepness--;
            if (deepness == 0) return i;
        }
        return -1;
    }
}
