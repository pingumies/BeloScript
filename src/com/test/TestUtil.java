package com.test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtil {
    public static void filesEqual(String file, String file2) throws IOException {
        Scanner scanner = new Scanner(Paths.get(file));
        Scanner scanner2 = new Scanner(Paths.get(file2));
        StringBuilder b1 = new StringBuilder();
        StringBuilder b2 = new StringBuilder();
        while (scanner.hasNextLine()) {
            b1.append(scanner.nextLine()).append("\n");
        }
        while (scanner2.hasNextLine()) {
            b2.append(scanner2.nextLine()).append("\n");
        }
        assertEquals(b1.toString(),b2.toString());
    }
}
