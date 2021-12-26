package com.patonki;

import com.patonki.util.Ui;
import javafx.application.Platform;

import java.util.Scanner;

/**
 * Sisältää main metodin, josta ohjelman suoritus alkaa.
 */
public class Main {
    /**
     * Lukee komentorivi argumentit ja aloittaa suorituksen.
     * Avaa myös käyttöliittymä threadin.
     */
    public static void main(String[] args) throws Exception {
        String fileName = null; // suoritettava / käännettävä tiedosto
        boolean justCompile = false; // suoritetaanko tiedosto kääntämisen jälkeen
        // Huom. tuplaklikatessa tiedostoa, tiedoston sijainnin löytää ensimmäisestä argumentista
        if (args.length >= 1) fileName = args[0];
        if (args.length >= 2) justCompile = Boolean.parseBoolean(args[1]);
        //Aloitetaan käyttöliittymä eri threadissa, jotta se ei haittaisi muuta suoritusta
        new Thread(Ui::startTheApplication).start();
        if (fileName != null) { // käyttäjä on valinnut tiedoston
            if (fileName.endsWith(".bel")) { // ei käännetty tiedosto käännetään
                BeloScript.compileBeloScriptToFile(fileName);
                fileName+="o";
            }
            if (!justCompile) { // Jos halutaan vain kääntää, ei suoriteta
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    BeloScript.executeBeloScriptFile(fileName);
                    System.out.println("Continue (y/n)");
                    String input = scanner.nextLine();
                    if (input.endsWith("n")) System.exit(0);
                }
            } else System.exit(0);
        } else {
            // Odotetaan, kunnes käyttöliittymä on latautunut
            while (!Ui.ready) Thread.sleep(100);
            // Ajetaan tiedoston valinta käyttöliittymän threadissa
            Platform.runLater(Ui::selectBeloScriptFile);
            // Odotetaan, kunnes käyttäjä on valinnut tiedoston
            while (Ui.selectedPath == null) {
                Thread.sleep(100);
            }
            String path = Ui.selectedPath;
            Ui.selectedPath = null; // asetetaan tiedostoon taas null
            if (path.isEmpty()) { // käyttäjä ei valinnut mitään, vaan painoi rastia
                System.out.println("You didn't select a file ._.");
            } else {
                //Ajetaan tiedosto
                if (path.endsWith(".bel")) { // Jos ei käännetty, käännetään
                    BeloScript.compileBeloScriptToFile(path);
                    path += "o";
                }
                BeloScript.executeBeloScriptFile(path);
            }
        }
    }
}
