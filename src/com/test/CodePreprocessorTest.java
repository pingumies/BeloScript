package com.test;

import com.patonki.compiler.CodePreprocessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class CodePreprocessorTest {
    private CodePreprocessor codePreprocessor;
    @BeforeEach
    void setUp() {
        codePreprocessor = new CodePreprocessor();
    }
    @Test
    void file() throws IOException {
        Scanner scanner = new Scanner(Paths.get("codeTest/preprocessor/test.bel"));
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine()).append("\n");
        }
        String result = codePreprocessor.preprocess(builder.toString());
        FileWriter writer = new FileWriter("codeTest/preprocessor/result.belo");
        writer.write(result);
        writer.close();
        TestUtil.filesEqual("codeTest/preprocessor/expected.belo","codeTest/preprocessor/result.belo");
    }
    @Test
    void preprocess() {
        String[] lines = codePreprocessor.preprocess("print (\"Hello World\"); i++").split(";");
        String result = lines[0];
        assertEquals(result, "print(\"Hello World\")");
        assertEquals(lines.length, 2);
    }
}