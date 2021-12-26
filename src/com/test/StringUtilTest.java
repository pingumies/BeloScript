package com.test;

import com.patonki.compiler.StringUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {
    StringUtil su = new StringUtil();
    String test; String result;
    @Test
    void leftSide() {
        test = "{mset,c,{add,8*l*l,{add,b,c}}}";
        result = su.leftSide(test,test.indexOf('*'));
        assertEquals("8",result);

        test = "print(89==3)";
        result = su.leftSide(test,test.indexOf('='));
        assertEquals("89",result);

        test = "test(98,func(76,84),\"98\"==9)";
        result = su.leftSide(test,test.indexOf('='));
        assertEquals("\"98\"", result);
    }

    @Test
    void rightSide() {
        test = "{mset,c,{add,8*l*l,{add,b,c}}}";
        result = su.rightSide(test,test.indexOf('*'));
        assertEquals("l*l",result);

        test = "print(89==3)";
        result = su.rightSide(test,test.indexOf('=')+1);
        assertEquals("3",result);

        test = "test(98,func(76,84),\"98\"==9)";
        result = su.rightSide(test,test.indexOf('=')+1);
        assertEquals("9", result);
    }

    @Test
    void split() {
        test = "print(\";;;\");start;{;;}";
        ArrayList<String> result = su.split(test,';');
        String[] expected = new String[] {"print(\";;;\")", "start", "{;;}"};
        assertEquals(result.size(),expected.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i],result.get(i));
        }
    }

    @Test
    void replace() {
        test = "===\"==\"=";
        result = su.replace(test,"==","¤¤");
        assertEquals("¤¤=\"==\"=",result);
    }

    @Test
    void indexOf() {
        test = "func(\"jou\",98,jou)";
        int result = su.indexOf(test,"jou",0);
        assertEquals(14,result);
    }

    @Test
    void startOfStatement() {
        test = "{mset,c,{add,8*l*l,{add,b,c}}}";
        int result = su.startOfStatement(test,test.indexOf('*'));
        assertEquals(test.indexOf('8')-1,result);

        test = "print(89==3)";
        result = su.startOfStatement(test,test.indexOf('='));
        assertEquals(test.indexOf('('),result);

        test = "test(98,func(76,84),\"98\"==9)";
        result = su.startOfStatement(test,test.indexOf('='));
        assertEquals(test.indexOf('"')-1, result);
    }

    @Test
    void endOfStatement() {
        test = "{mset,c,{add,8*l*l,{add,b,c}}}";
        int result = su.endOfStatement(test,test.indexOf('*'));
        assertEquals(test.indexOf('l')+3,result);

        test = "print(89==3)";
        result = su.endOfStatement(test,test.indexOf('=') + 1);
        assertEquals(test.indexOf(')'),result);

        test = "test(98,func(76,84),\"98\"==9)";
        result = su.endOfStatement(test,test.indexOf('=')+1);
        assertEquals(test.length()-1, result);
    }

    @Test
    void sulkeet() {
        test = "print(func(87,\"hello\"))";
        result = su.sulkeet(test,test.indexOf('('));
        assertEquals("(func(87,\"hello\"))",result);
        result = su.sulkeet(test,test.indexOf("c(")+1);
        assertEquals("(87,\"hello\")",result);
    }

    @Test
    void leftCounterPart() {
        test = "print(func(87,4),\"()Hello World\")";
        int result = su.leftCounterPart(test,test.length()-1);
        assertEquals(test.indexOf('('), result);
    }

    @Test
    void rightCounterPart() {
        test = "print(func(87,4),\"()Hello World\")";
        int result = su.rightCounterPart(test,test.indexOf('('));
        assertEquals(test.length()-1, result);
    }
}