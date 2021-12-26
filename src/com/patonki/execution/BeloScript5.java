package com.patonki.execution;

import com.patonki.Import;
import com.patonki.commands.*;
import com.patonki.compiler.StringUtil;
import com.patonki.datatypes.BeloDouble;
import com.patonki.datatypes.BeloString;
import com.patonki.datatypes.Function;
import com.patonki.datatypes.Variable;
import com.patonki.execution.Executor;
import com.patonki.execution.Line;
import com.patonki.interfaces.BeloClass;
import com.patonki.interfaces.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Muuttaa tiedoston merkkijono komennot BeloClass objekti taulukoksi, jota on huomattavasti
 * nopeampi suorittaa kuin, jos aina joutuisi käsittelemään mitä merkkijono käskee tekemään.
 */
public class BeloScript5 {
    private final StringUtil su = new StringUtil();
    private final Executor executor = new Executor(); //Luokka, joka lopulta suorittaa koodin
    private BeloClass[] lines; //rivit, jotka executor pystyy ajamaan
    private int deepness = 0; //lohkon syvyys
    private Line[] info; //Jokainen rivi sisältää hieman lisä infoa
    private int lastStart=-1; //Viimeisin lohkon alku
    private ArrayList<Integer> lastLoopStarts = new ArrayList<>();
    private ArrayList<String> splitted; //Compilattu koodi riveittäin
    private int index; //Rivi indeksi, jota käsitellään
    //Sisältää tiedon viimeisimmistä if tai elif lausekkeista.
    //Tarvitaan, jotta else lausekkeille voidaan syöttää if lausekkeet, joiden kuuluu olla false
    //, jotta else lausekkeen vois suorittaa
    private final HashMap<Integer, ArrayList<Integer>> lastIf = new HashMap<>();
    private HashMap<String, Command> functions; //sisältää kaikki funktiot
    private final HashMap<Integer,ArrayList<String>> vars = new HashMap<>();
    private final HashMap<String,BeloClass> variables = new HashMap<>(); //sisältää muuttujat
    private static final Logger LOGGER = LogManager.getLogger(BeloScript5.class);

    /**
     * Muuttaa merkkijono ohjeet java luokiksi (BeloClass), jotka voidaan suorittaa tehokkaasti
     * vuoronperään.
     * @param compiledCode Compilattu koodi
     */
    public void load(String compiledCode) {
        LOGGER.info("Starting loading!");
        //Importataan kaikk funktiot kirjastoista
        try {
            functions = new Import().importAll(executor);
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        splitted = su.split(compiledCode, ';');

        lines = new BeloClass[splitted.size()];
        info = new Line[splitted.size()];
        //Käsitellään jokainen rivi buildCommand funktiolla
        for (index = 0; index < splitted.size(); index++) {
            LOGGER.info("Building line: "+index);
            String s = splitted.get(index);
            lines[index] = buildCommand(s);

            if (info[index] == null) {
                int nextEnd = lastStart == -1 ? -1 : info[lastStart].nextEnd;
                int last = lastLoopStarts.size()-1;
                int nextLoopend = (lastLoopStarts.size()==0) ? -1 : info[lastLoopStarts.get(last)].nextLoopEnd;
                info[index] = new Line(nextEnd,lastStart,nextLoopend,last == -1 ? -1 : lastLoopStarts.get(last));
            }
        }
    }

    public Executor getExecutor() {
        return executor;
    }

    private BeloClass buildCommand(String line) {
        //TODO siirrä tämä compile vaiheeseen
        if (line.equals("else")) {
            line = "{else,}";
        }
        //Arvo on esimääritelty merkkijono
        if (line.startsWith("\"")) return new BeloString(line.substring(1, line.length() - 1));
        //Arvo on esimääritelty numero
        try {
            double d = Double.parseDouble(line);
            return new BeloDouble(d);
        } catch (NumberFormatException ignored) {
        }
        //Lohkon alku
        if (line.equals("start")) {
            lastStart = index; //Edellinen lohkon alku
            //Lohkon loppu
            int end = su.getEnd(index,splitted);
            //Jos edellistä silmukan alkua ei ole, ei ole seuraavaa silmukan loppuakaan.
            //Muussa tapauksessa silmukan alku sisältää tiedon siitä missä silmukka loppuu
            int last = lastLoopStarts.size()-1;
            int nextLoop = lastLoopStarts.size()== 0 ? -1 : info[last].nextLoopEnd;
            //Tallennetaan tiedot
            info[index] = new Line(end,lastStart,nextLoop,last == -1 ? -1: lastLoopStarts.get(last));
            //Siirrytään syvempään lohkoon
            deepness++;
            return new Start();
        }
        if (line.equals("lstart")) {
            lastLoopStarts.add(index);
            //etsitään silmukan loppu
            int end = su.getLoopEnd(index,splitted);
            //Jos edellistä lohkon alkua ei ole, ei ole seuraavaa lohkon loppuakaan.
            //Muussa tapauksessa lohkon alku sisältää tiedon siitä missä lohko loppuu
            int nextEnd = lastStart == -1 ? -1 : info[lastStart].nextEnd;
            info[index] = new Line(nextEnd,lastStart,end,index);
            return new Empty();
        }
        if (line.equals("end")) {
            //Lohko loppuu, jolloin poistetaan kaikki lohkon sisäiset muuttujat
            if (vars.containsKey(deepness)) {
                for (String s : vars.get(deepness)) {
                    variables.remove(s);
                }
            }
            deepness--;
            return new End();
        }
        if (line.equals("lend")) {
            //Silmukka loppuu
            int nextEnd = lastStart == -1 ? -1 : info[lastStart].nextEnd;
            info[index] = new Line(nextEnd,lastStart,index,lastLoopStarts.get(lastLoopStarts.size()-1));
            lastLoopStarts.remove(lastLoopStarts.size()-1);
            return new LEnd(executor);
        }
        if (line.equals("true") || line.equals("false")) {
            //Kyseessä on ennaltamääritetty boolean arvo
            return new BeloDouble(line.equals("true") ? 1 : 0);
        }
        if (line.startsWith("{")) {
            return funktion(line);
        }
        for (int i = deepness; i >= 0; i--) {
            if (variables.containsKey(line+i)) {
                return variables.get(line+i);
            }
        }
        return new Empty();
    }
    private BeloClass funktion(String line) {
        //funktion nimi
        String name = line.substring(1,line.indexOf(","));
        //funktion parametrit
        ArrayList<String> parameters = su.split(line.substring(line.indexOf(",") + 1, line.length() - 1), ',');
        //Funktion komento
        Command command = functions.get(name);
        if (command == null) throw new IllegalStateException("Not a function: "+name);
        //Erityistilanteet:
        if (name.equals("function")) {
            //Funktion parametrit käsittelyn jälkeen
            Variable[] params = new Variable[parameters.size()-1];
            String functionName = parameters.get(0);
            for (int i = 1; i < parameters.size(); i++) {
                //Lisätään funktion parametrit seuraavaan lohkoon
                String varName = parameters.get(i);
                int deepness = this.deepness+1;
                Variable var = new Variable();
                variables.put(varName + deepness, var);
                vars.put(deepness, new ArrayList<>());
                vars.get(deepness).add(varName + deepness);
                params[i-1] = var;
            }
            //Funktion aloitus ja lopetus indeksit
            int start = index+1;
            int end = su.getEnd(start,splitted);
            functions.put(functionName, new UserDefinedFunction(params,executor,start,end));
            return new Function(command,params);
        }
        if (name.equals("set")) {
            String varName = parameters.get(0);
            boolean contains = false;
            for (int i = deepness; i>=0; i--) {
                if (variables.containsKey(varName + i)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                variables.put(varName+deepness, new Variable());
                if (!vars.containsKey(deepness)) vars.put(deepness,new ArrayList<>());
                vars.get(deepness).add(varName+deepness);
            }
        }
        if (name.equals("if")) {
            //Luodaan lista johon kerätään kaikki if-ketjun if-komennot
            ArrayList<Integer> list = new ArrayList<>();
            list.add(index);
            lastIf.put(deepness,list);
        }
        if (name.equals("elif")) {
            //Lisätään kaikki aiemmat if lausekkeet
            //joiden täytyy olla false, että tämä if-lauseke voidaan suorittaa
            for (int i : lastIf.get(deepness)) {
                parameters.add(String.valueOf(i));
            }
            lastIf.get(deepness).add(index);
        }
        if (name.equals("else")) {
            for (int i : lastIf.get(deepness)) {
                parameters.add(String.valueOf(i));
            }
        }
        //Funktion parametrit käsittelyn jälkeen
        BeloClass[] params = new BeloClass[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            //Kutsutaan funktiota rekursiivisesti jokaisella parametrillä
            params[i] = buildCommand(parameters.get(i));
        }
        return new Function(command, params);
    }

    public BeloClass[] getLines() {
        return lines;
    }

    public Line[] getInfo() {
        return info;
    }
}
