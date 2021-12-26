package com.test;

import com.patonki.execution.BeloScript5;
import com.patonki.compiler.Compiler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
    private Compiler compiler;
    @BeforeEach
    void setUp() {
        compiler = new Compiler();
    }
    @Test
    void file() throws Exception {
        Scanner scanner = new Scanner(Paths.get("input.txt"));
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine()).append("\n");
        }
        String result = compiler.compile(builder.toString());
        BeloScript5 beloScript5 = new BeloScript5();
        beloScript5.load(result);
        beloScript5.getExecutor().run(beloScript5.getLines(),beloScript5.getInfo());
    }

    @Test
    void fileTests() throws Exception {
        File file = new File("test/final");
        File[] files = file.listFiles();
        if (files == null) throw new IllegalStateException("File not found");
        Arrays.sort(files, Comparator.comparingInt(f -> Integer.parseInt(f.getName())));
        for (File f : files) {
            FileWriter clearer = new FileWriter("debug.txt");
            clearer.write("");
            clearer.close();
            String num = f.getName();
            System.out.println("Test number: "+num);
            Scanner scanner = new Scanner(Paths.get("test/final/"+num+"/"+num+".bel"));
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine()).append("\n");
            }
            BeloScript5 beloScript5 = new BeloScript5();
            Compiler compiler = new Compiler();
            String result = compiler.compile(builder.toString());
            FileWriter writer = new FileWriter("compiled.belo");
            writer.write(result);
            writer.close();
            beloScript5.load(result);
            long start = System.currentTimeMillis();
            beloScript5.getExecutor().run(beloScript5.getLines(),beloScript5.getInfo());
            long end = System.currentTimeMillis();

            TestUtil.filesEqual("test/final/"+num+"/"+num+".txt","debug.txt");
            System.out.println("SUCCESS! "+(end-start)+" millis");
        }
    }

    @Test
    void compile() {
        String code;
        String result;
        String expected;

        code = "print( (a & b) | c)";
        result = compiler.compile(code);
        expected = "{print,{or,{and,a,b},c}};";
        assertEquals(expected,result);

        code = "print( i += 3 == 8)";
        result = compiler.compile(code);
        expected = "{print,{pset,i,{equal,3,8}}};";
        assertEquals(expected,result);
        //c -= l*4*4 + l*6*6 + 8*l*l
        code = "c -= l*4*4 + l*6*6 + 8*l*l";
        result = compiler.compile(code);
        expected = "{mset,c,{add,{multiply,l,{multiply,4,4}},{add,{multiply,l,{multiply,6,6}},{multiply,8,{multiply,l,l}}}}};";
        assertEquals(expected,result);

        code = "print(list.size(8))";
        result = compiler.compile(code);
        expected = "{print,{clc,list,\"size\",8}};";
        assertEquals(expected,result);

        code = "deb(\"helou\".length())";
        result = compiler.compile(code);
        expected = "{deb,{clc,\"helou\",\"length\",}};";
        assertEquals(expected,result);

        code = "s[i+9]=11";
        result = compiler.compile(code);
        expected = "{set,{index,s,{add,i,9}},11};";
        assertEquals(expected,result);

        code = "-8";
        result = compiler.compile(code);
        expected = "-8;";
        assertEquals(expected,result);

        code = "itr0=0;while(itr0<list.size()) {i=list[itr0];itr0+=1;}";
        result = compiler.compile(code);
        expected = "{set,itr0,0};lstart;{while,{less,itr0,{clc,list,\"size\",}}};start;{set,i,{index,list,itr0}};{pset,itr0,1};;lend;end;";
        assertEquals(expected,result);

        code = "print(\"Ikäsi: \"+(2022-syntymavuosi))";
        result = compiler.compile(code);
        expected = "{print,{add,\"Ikäsi: \",{substract,2022,syntymavuosi}}};";
        assertEquals(expected,result);
    }
}