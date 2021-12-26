package com.patonki;

import com.patonki.compiler.Compiler;
import com.patonki.execution.BeloScript5;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Luokka, joka tarjoaa apumetodeja BeloScriptin ajamiseen.
 */
public class BeloScript {

    /**
     * Suorittaa BeloScript tiedoston ja palauttaa sitten outputFilen sisällön
     * Huom. EI siis muokkaa millään tavalla BeloScript koodin sisältöä. Jos BeloScript koodi
     * ei kirjoita tiedostoon. Tiedosto tulee olemaan tyhjä
     * @param path Suoritettava tiedosto
     * @param outputFile Tiedosto, jonka sisältö luetaan suorituksen jälkeen
     * @return outputFilen sisältö suorituksen jälkeen
     * @throws Exception Tiedosto ei löydy tai muita tiedoston lukuongelmia
     */
    public static String executeBeloScriptFileWithFileOutput(String path, String outputFile) throws Exception {
        executeBeloScriptFile(path);

        StringBuilder result = new StringBuilder();
        Scanner scanner = new Scanner(Paths.get(outputFile));
        while (scanner.hasNextLine()) {
            result.append(scanner.nextLine()).append("\n");
        }
        if (result.length()==0) return "";
        else return result.substring(0,result.length()-1);
    }

    /**
     * Suorittaa BeloScipt tiedoston. Tiedosto voi olla .bel tai .belo formaatissa
     * @param path Tiedoston sijainti
     * @throws Exception Tiedosto ei löydy tai sen lukemisessa on ongelmia
     */
    public static void executeBeloScriptFile(String path) throws Exception {
        StringBuilder code = new StringBuilder();
        Scanner scanner = new Scanner(Paths.get(path));
        while (scanner.hasNextLine()) {
            code.append(scanner.nextLine()).append("\n");
        }
        scanner.close();
        if (path.endsWith(".bel")) {
            executeBeloScriptSourceCode(code.toString());
        }
        else if (path.endsWith(".belo")) {
            executeCompiledBeloScriptCode(code.toString());
        } else {
            throw new IllegalArgumentException("Not a BeloScript file. File extensions: .bel and .belo (compiled)");
        }
        System.out.println("Exit code 0");
    }

    /**
     * Suorittaa .bel formaatissa olevia merkkijonoja
     * @param code .bel koodi
     * @throws Exception Suorituksessa tapahtui jokin virhe
     */
    public static void executeBeloScriptSourceCode(String code) throws Exception {
        String compiled = compileBeloScript(code);
        executeCompiledBeloScriptCode(compiled);
    }

    /**
     * Suorittaa käännettyä BeloScript koodia
     * @param compiled Käännetty BeloScript koodi
     * @throws Exception Suorituksessa tapahtui jokin virhe
     */
    public static void executeCompiledBeloScriptCode(String compiled) throws Exception {
        BeloScript5 beloScript5 = new BeloScript5();
        beloScript5.load(compiled);
        beloScript5.getExecutor().run(beloScript5.getLines(),beloScript5.getInfo());
    }
    private static final Compiler compiler = new Compiler();

    /**
     * Kääntää BeloScript tiedoston ja tallentaa käännetyn koodin tiedostoon
     * @param path Tiedoston sijainti
     * @throws IOException kirjoittamisessa tai lukemisessa virhe
     */
    public static void compileBeloScriptToFile(String path) throws IOException {
        StringBuilder code = new StringBuilder();
        Scanner scanner = new Scanner(Paths.get(path));
        while (scanner.hasNextLine()) {
            code.append(scanner.nextLine()).append("\n");
        }
        String result = compileBeloScript(code.toString());
        FileWriter writer = new FileWriter(path+"o");
        writer.write(result);
        writer.close();
    }

    /**
     * Palauttaa käännetyn version annetusta BeloScript lähdekoodista
     * @param code lähdekoodi
     * @return käännetty koodi
     */
    public static String compileBeloScript(String code) {
        return compiler.compile(code);
    }
}
