package com.patonki.compiler;

import com.patonki.util.SUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;


/**
 * Tekee viimeistelyn koodiin, jotta koodi on valmis suoritettavaksi.
 * HUOM: koodi, joka syötetään tänne, pitää ensin syöttää CodePreprocessorille
 */
public class CompileObject {
    private final StringUtil su = new StringUtil();
    private String line;
    private static final Logger LOGGER = LogManager.getLogger(CompileObject.class);
    //palauttaa seuraavan merkin
    private char next(int i) {
        if (i+1 >= line.length()) return 0;
        return line.charAt(i+1);
    }
    //palauttaa edellisen merkin
    private char prev(int i) {
        if (i - 1 < 0) return 0;
        return line.charAt(i-1);
    }
    //Muuttaa operaattorit komento muotoon, jossa ensimmäinen parametri on
    //operaattorin vasemmalla puolella oleva koodi ja toinen parametri oikealla oleva koodi
    private void edit(int start, int end, String command) {
        String leftSide = su.leftSide(line,start);
        String rightSide = su.rightSide(line,end);
        if (leftSide.isEmpty() | rightSide.isEmpty()) return;
        String combined = leftSide+line.substring(start,end+1)+rightSide;

        String replacer = "{"+command+","+leftSide+","+rightSide+"}";
        line = line.replace(combined,replacer);
    }
    private void sulut() {
        int i = -1;
        while ((i = SUtil.indexOf(line,'(',i+1)) != -1) {
            if (!su.onNimiKirjain(prev(i))) { //jos edellinen on nimiKirjain, kyseessä on funktio kutsu
                String sulkeet = su.sulkeet(line,i);
                //Luodaan uusi CompileObject, jolla prosessoidaan sulkeiden sisällä oleva osa
                String processed = new CompileObject().processLine(sulkeet.substring(1,sulkeet.length()-1));
                line = line.replace(sulkeet,processed);
            }
        }
    }
    //Jokainen funktio muutetaan muotoon {nimi,parametri1,parametri2}
    private void functions() {
        int i = -1;
        //Etsitään jokainen avautuva sulku
        while ((i = su.indexOf(line,"(",i+1)) != -1) {
            String sulkeet = su.sulkeet(line,i);
            //Kootaan funktion nimi kulkemalla takaisin päin, kunnes esiintyy
            //merkki, joka ei voi kuulua nimeen
            StringBuilder name = new StringBuilder();
            int l=i;
            while (su.onNimiKirjain(prev(l))) {
                name.append(prev(l));
                l--;
            }
            name.reverse(); //nimi käännetään, koska muuten se olisi väärin päin
            String processed = "{"+name.toString()+","+sulkeet.substring(1,sulkeet.length()-1)+"}";

            line = line.replace(name.toString()+sulkeet,processed);
        }
    }

    /**
     * Prosessoi yhden rivin uuteen muotoon.
     * Uudessa muodossa lauseke järjestetään muotoon, jossa sen voi suorittaa
     * sisimmästä sulusta uloimpaan sulkuun järjestyksessä. Siten itse ajoprosessissa ei tarvitse
     * miettiä mitä pitää ajaa ensin.
     * @param string Prosessoitava rivi
     * @return prosessoitu rivi
     */
    public String processLine(String string) {
        LOGGER.info("CompileObject starts\n"+ su.replace(string,";","\n"));
        this.line = string;
        //Ensin käsitellään sulut
        sulut();
        //Jokainen funktio muutetaan muotoon {nimi,parametri1,parametri2}
        functions();
        LOGGER.info("Functions done");
        //Korvataan vertailu operaattorit merkillä ¤
        //Jotta vertailu ei menisi sekaisin muuttujan määrittämisen kanssa
        line = su.replace(line,"==","¤¤");
        line = su.replace(line,"!=","!¤");
        line = su.replace(line,"<=","<¤");
        line = su.replace(line,">=",">¤");
        //TODO ++ -- merkinnät
        //Seuraavaksi koodi käsittelee operaattorit vähiten tärkeästä tärkeimpään
        //Esim plus laskut tulevat ennen kertolaskua
        //Jokaista operaattoria vastaa komento
        operators(
                new String[]{"=","+=","-=","*=","/=","^="},
                new String[]{"set","pset","mset","multiset","divset","powset"}
        );
        LOGGER.info("Setters done");
        operators(new String[]{"&","|"}, new String[]{"and","or"});
        operators(
                new String[]{"¤¤","!¤","<¤",">¤",">","<"},
                new String[]{"equal","nequal","lequal","mequal","more","less"}
                );
        LOGGER.info("Comparators done");
        operators(
                new String[]{"+","-"},
                new String[]{"add","substract"}
        );
        operators(
                new String[]{"*","/","%"},
                new String[]{"multiply","divide","remainder"}
        );
        operators(
                new String[]{"^"},
                new String[]{"pow"}
        );
        LOGGER.info("Calculations done");
        //Luokka metodit
        int i;
        while ((i = su.lastIndexOf(line,".",line.length()-1)) != -1) {
            String leftSide = su.leftSide(line,i);
            String rightSide = su.rightSide(line,i);
            String combined = leftSide+'.'+rightSide;
            if (rightSide.startsWith("{")) { //funktio
                int dot = su.indexOf(rightSide,",",0);
                String name = rightSide.substring(1,dot); //funktion nimi
                rightSide = "\""+name+"\""+rightSide.substring(dot,rightSide.length()-1);
            }else rightSide = "\""+rightSide+"\"";
            String replacer = "{clc,"+leftSide+","+rightSide+"}";
            line = line.replace(combined,replacer);
        }
        LOGGER.info("Class syntax done");
        // [] syntaksi
        while ((i = su.indexOf(line,"[",0)) != -1) {
            String leftSide = su.leftSide(line,i);
            String rightSide = su.rightSide(line,i);
            String combined = leftSide+'['+rightSide+']';
            String replacer = "{index,"+leftSide+","+rightSide+"}";
            line = line.replace(combined,replacer);
        }
        LOGGER.info("Index access syntax done");

        return this.line;
    }
    private void operators(String[] operators, String[] commands) {
        int[] indexes = new int[operators.length];
        int minI;
        while (true) {
            Arrays.fill(indexes,-1);
            minI = -1;
            //Etsitään lähin operaattori operators listasta
            for (int i = 0; i < indexes.length; i++) {
                indexes[i] = su.indexOf(line,operators[i], 0);
                if (indexes[i] != -1 && (minI == -1 || indexes[i] < indexes[minI])) minI = i;
            }
            if (minI == -1) break;
            int mi = indexes[minI];
            indexes[minI] = mi;
            //operaattori löytyi
            String command = commands[minI];
            //Laitetaan operaattorin vasemmalla puolella olevat asiat parametriin yksi
            //ja oikealla puolella olevat parametriin kaksi
            edit(indexes[minI],indexes[minI]+operators[minI].length()-1,command);
        }
    }
}
