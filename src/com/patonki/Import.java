package com.patonki;

import com.patonki.execution.Executor;
import com.patonki.interfaces.Command;
import com.patonki.interfaces.Library;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Hoitaa kirjastojen importaamisen ja funktioiden määrittämisen.
 * Importtaa kaiken mitä lukee imports.txt tiedostossa.
 * Hakee importettavat tiedostot libraries kansiosta.
 */
public class Import {
    /**
     * Lukee tiedoston imports.txt ja lisää kaikki sen määräämät jar-tiedostot ohjelmaan.
     * @param executor Ohjelman suorittaja
     * @return Hajautustaulu, jossa on komennon nimi ja komento
     */
    public HashMap<String, Command> importAll(Executor executor) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        HashMap<String,Command> commands = new HashMap<>();
        Scanner scanner = new Scanner(Paths.get("imports.txt"));
        //Ohjelman oma class loader
        ClassLoader main = Import.class.getClassLoader();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            //Jar tiedostot voi erottaa välilyönneillä
            String[] split = line.split(" ");
            //Muut jar-tiedostot ovat kirjastoja, joita mainJar tarvitsee
            String mainJar = "libraries/"+split[0]+".jar";
            URL[] urls = new URL[split.length];
            //Lisätään kaikki jar-tiedostot
            for (int i = 0; i < split.length; i++) {
                urls[i] = new File("libraries/"+split[i]+".jar").toURI().toURL();
            }
            //Luodaan URL classloader
            ClassLoader urc = URLClassLoader.newInstance(urls,main);
            //Luetaan jar-tiedosto
            JarFile jarFile = new JarFile(new File(mainJar));
            Enumeration<JarEntry> e = jarFile.entries();

            while (e.hasMoreElements()) {
                //Käydään läpi kaikki .class tiedostot jar-tiedostosta
                JarEntry je = e.nextElement();
                if (je.isDirectory()) {
                    continue;
                }
                if (je.getName().endsWith(".class")) {
                    //.class pois
                    String className = je.getName().substring(0,je.getName().length()-6);
                    className = className.replace('/', '.');
                    //Viimeisen pisteenjälkeen
                    String simpleName = className.substring(className.lastIndexOf(".")+1);
                    if (!className.contains("$") && simpleName.startsWith("BeloLib")) {
                        Class<?> clazz = urc.loadClass(className);
                        Library newClass = (Library) clazz.newInstance();
                        //Ladataan funktiot luokasta
                        newClass.load(commands);
                        newClass.setExecutor(executor);
                    }
                }
            }
        }
        return commands;
    }
}
