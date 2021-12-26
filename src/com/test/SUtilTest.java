package com.test;

import com.patonki.util.SUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SUtilTest {
    @Test
    void split() {
        String s1 = "set,i,87";
        String s2 = "set,[spawn,44,4],98";
        List<String> list = SUtil.split(s1);
        List<String> list2 = SUtil.split(s2);
        assertEquals(3,list.size());
        assertEquals(3,list2.size());
    }
    @Test
    void removeWhiteSpace() {
        String s = "    if (i < 10 000) {  ";
        String result = SUtil.removeWhitespace(s);
        assertEquals("if(i<10000){",result);
    }

    @Test
    void cutParameter() {
        String s = "set,i,i<9";
        String s2 = "[if,89<34]{";
        String result = SUtil.cutParameter(s,7);
        String result2 = SUtil.cutParameter(s2,6);
        assertEquals("i<9",result);
        assertEquals("89<34",result2);
    }
}