package com.patonki.compiler;

import com.sun.org.apache.bcel.internal.classfile.Code;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Muuttaa lähdekoodia hieman helpommin käsiteltävään muotoon.
 */
public class CodePreprocessor {
    private boolean insideString = false;
    private boolean insideComment = false;
    private final StringUtil su = new StringUtil();
    private int forLoopUniqueIdentifier;
    private boolean[] done;
    private HashMap<String,Boolean> seenImports = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(CodePreprocessor.class);

    /**
     * Muokkaa koodin helpommin käsiteltävään muotoon.
     * Esimerkiksi for silmukat muutetaan while-silmukoiksi.
     * Katso esimerkki tulokset.
     * @param code Käsiteltävä koodi
     * @return Käsitelty koodi
     */
    public String preprocess(String code) {
        LOGGER.info("Preprocessing\n"+code);

        //Poistetaan kommentit
        char[] chars = code.toCharArray();
        insideString = false;
        forLoopUniqueIdentifier = 0;
        insideComment = false;
        StringBuilder builder = new StringBuilder();
        //Käsitellään jokainen merkki processCharacter() funktion avulla
        for (int i = 0; i < chars.length; i++) {
            String addable = processCharacter(chars,i);
            builder.append(addable);
        }
        String result = builder.toString();
        //Jaetaan tulos rivien mukaan
        ArrayList<String> splitted = su.split(result,';');
        done = new boolean[splitted.size()*10];
        //import lausekkeet esim: import kirjasto
        importit(splitted);
        LOGGER.info("Imports done");
        //for silmukat esim: for ( i : list)
        forLoops(splitted);
        LOGGER.info("ForLoops done");
        //while silmukat esim: while ( true )
        whileLoops(splitted);
        LOGGER.info("WhileLoops done");
        //funktiot esim: function hello(param1)
        funktiot(splitted);
        LOGGER.info("Functions done");
        builder.setLength(0); //tyhjennetään StringBuilder
        for (String s : splitted) {
            //Kootaan lista takaisin yhdeksi merkkijonoksi
            if (s.isEmpty()) continue;
            builder.append(s).append(";");
        }
        return builder.toString();
    }

    private void importit(ArrayList<String> splitted) {
        for (int i = 0; i < splitted.size(); i++) {
            String s = splitted.get(i);
            if (s.startsWith("import")) { //muutettava rivi löydetty
                String file = s.substring("import".length()); //importettava tiedosto
                if (seenImports.containsKey(file)) continue; //samaa kirjastoa ei importata montaa kertaa
                seenImports.put(file,true);
                //poistetaan import rivi
                splitted.remove(i);
                try {
                    //Lisätään paikalle lisättävässä tiedostossa olevat rivit prosessoituna
                    Scanner scanner = new Scanner(Paths.get(file+".bel"));
                    StringBuilder builder = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        builder.append(scanner.nextLine()).append("\n");
                    }
                    String preprocessed = new CodePreprocessor().preprocess(builder.toString());
                    splitted.add(i,preprocessed);
                    done[i] = true; //Jo käsitelty rivi, ei tarvitse käsitellä uudestaan
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void funktiot(ArrayList<String> splitted) {
        //Käydään rivit läpi ja muutetaan käyttäjän määrittelemän function syntaksia
        for (int i = 0; i < splitted.size(); i++) {
            if (done[i]) continue; // on jo käsitelty loppuun
            String s = splitted.get(i);
            if (s.startsWith("function")) { //muutettava rivi löydetty
                int len = "function".length();
                int aukeavaSulje = s.indexOf('(');
                String nimi = s.substring(len,aukeavaSulje);
                //muutetaan muodosta: function hello()
                //muotoon: function(hello,)
                splitted.set(i,"function("+nimi+","+s.substring(aukeavaSulje+1,s.indexOf(')'))+")");
            }
        }
    }
    private void whileLoops(ArrayList<String> splitted) {
        //Käydään rivit läpi
        for (int j=0; j < splitted.size(); j++) {
            if (done[j]) continue; // On jo käsitelty loppuun
            //Jos löytyy while silmukka etsitään while silmukan loppu ja merkitään se
            if (splitted.get(j).startsWith("while(")) {
                //Merkitään silmukan alku ja loppu
                loops(splitted,j); j++; //lisätään yksi, koska muuten alkaisi loputon silmukka
            }
        }
    }
    //muutetaan for-silmukat while silmukoiksi
    private void forLoops(ArrayList<String> splitted) {
        for (int i = splitted.size()-1; i>= 0; i--) { //Huom. aloitetaan lopusta
            if (done[i]) continue; // On jo käsitelty loppuun
            String s = splitted.get(i);
            if (s.startsWith("for(")) { //muutettava rivi
                //Sulkujen sisällä
                String inside = s.substring(4,su.rightCounterPart(s,3));
                int firstMark = inside.indexOf(':');
                //Kaksi : merkkiä
                if (su.indexOf(inside,":",firstMark+1) != -1) { //indeksöity forloop
                    //Jaetaan :-merkin mukaan
                    List<String> parts = su.split(inside, ':');
                    //Muuttujan määritys vaihe:
                    List<String> first = su.split(parts.get(0),'=');
                    String var = first.get(0); //muuttuja on määrittelyssä ennen yhtäsuuruus merkkiä
                    //ehto on toinen osa for lauseketta
                    String condition = parts.get(1);
                    //luodaan uniikki iteraattori, jota ohjelmoija ei ikinä vahingossa kirjoita
                    String iterator = "itrBeloScriptUnique"+(forLoopUniqueIdentifier++);
                    //Muuttujan arvon muuttaminen
                    String update = parts.get(2); update = su.replace(update,var,iterator);
                    //Korvataan ehdossa muuttujan tilalle iteraattori
                    condition = su.replace(condition,var,iterator);
                    //Ensin laitetaan muuttujan arvoksi iteraattori
                    //Siitten pävitetään iteraattorin arvoa
                    splitted.add(i+2, update);
                    splitted.add(i+2,var+"="+iterator);
                    //Asetetaan for silmukan tilalle while silmukka
                    splitted.set(i, "while("+condition+")");
                    //Määritellään iteraattori
                    splitted.add(i,iterator+"="+first.get(1));
                } else { //for-each loop
                    List<String> two = su.split(inside,':');
                    String var = two.get(0); //muuttuja
                    String list = two.get(1); // iteroitava lista
                    //Uniikki iteraattori, jota ohjelmoija ei vahingossa itse kirjoita
                    String iterator = "itrBeloScriptUnique"+(forLoopUniqueIdentifier++);
                    //Ensin laitetaan muuttujan arvoksi iteraattorin indeksi listasta
                    //Sitten siirretään iteraattoria yksi oikealle
                    splitted.add(i+2, iterator+"+=1");
                    splitted.add(i+2, var+"="+list+"["+iterator+"]");
                    //Muutetaan for-silmukka while-silmukaksi
                    splitted.set(i,"while("+iterator+"<"+list+".size())");
                    //Määritellääm iteraattori
                    splitted.add(i,iterator+"=0");
                }
            }
        }
    }

    /**
     * Käsittelee yhden merkin merkkijonosta ja palauttaa merkkijonon
     * , joka korvaa kyseisen merkin
     * @param chars Koko merkkijono
     * @param i Kirjaimen indeksi
     * @return Uusi arvo, joka korvaa merkin
     */
    private String processCharacter(char[] chars, int i) {
        char c = chars[i];
        String result = c+""; //muutetaan merkki merkkijonoksi
        if (c == '"') {
            insideString = !insideString;
        }

        if (insideString) return result; //merkkijonon sisällä olevia merkkejä ei muuteta
        if (c=='#') insideComment = true;
        //Korvataan rivinvaihdot merkillä ;
        //Rivinvaihto lopettaa kommentin
        if (c == '\n' | c==';') {
            result = ";";
            insideComment = false;
        }
        if (insideComment) return "";
        //Poistetaan tyhjä tila
        if (c == ' ' || c == '\t') result = "";
        //Korvataan } komennolla end ja lisätään rivinvaihtoja ympärille jos tarvetta
        if (c == '}') {
            if (i+1 == chars.length || chars[i+1] == '\n') result = "end";
            else result = "end;";
            result = ';'+result;
        }
        //Korvataan { komennolla start ja lisätään rivinvaihtoja ympärille jos tarvetta
        if (c == '{') {
            if (i+1 == chars.length || chars[i+1] == '\n') result = "start";
            else result = "start;";
            result = ';'+result;
        }

        return result;
    }
    //Etsii silmukan loppu kohdan ja merkitsee sen "lend" merkkijonolla. Lisäksi merkitään myös silmukan alku.
    //Tämä kaikki tehdään, jotta esim. break ja continue komennot toimisivat.
    //Esim continue komento etsii viimeisimmän "lstart" merkkijonon koodista ja siirtyy sinne
    private void loops(ArrayList<String> splitted, int start) {
        int deepness = 0;
        for (int i = start+1; i < splitted.size(); i++) {
            String s = splitted.get(i);
            if (s.equals("start")) deepness++;
            if (s.equals("end")) deepness--;
            if (deepness == 0) {
                splitted.add(i,"lend");
                break;
            }
        }
        splitted.add(start,"lstart");
    }
}
