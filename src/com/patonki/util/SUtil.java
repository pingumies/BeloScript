package com.patonki.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Sisältää paljon string funktioita, joita ei kaikkia edes käytetä
 * Siirretään myöhemmin StringUtils luokkaan.
 */
//TODO siirrä StringUtils luokkaan
public class SUtil {
    public static boolean insideString(String line, int index) {
        char[] array = line.toCharArray();
        boolean inside=false;
        for (int i = 0; i <= index; i++) {
            char c = array[i];
            if (c == '"') inside = !inside;
        }
        return inside;
    }
    public static boolean contains(String line, String test) {
        line = removeStrings(line);
        return line.contains(test);
    }

    public static String removeStrings(String line) {
        char[] array = line.toCharArray();
        StringBuilder builder = new StringBuilder();
        boolean inside=false;
        for (char c : array) {
            if (c == '"') {
                inside = !inside;
                continue;
            }
            if (!inside) {
                builder.append(c);
            }
        }
        return builder.toString();
    }
    //Not alphabetic or _ or number
    private static boolean notNormal(char c) {
        return !(Character.isAlphabetic(c) | Character.isDigit(c) | c == '_');
    }

    /**
     * Return the index of character. Characters inside strings are ignored.
     */
    public static int indexOf(String line, char search,int start) {
        char[] ar = line.toCharArray();
        boolean str=false;
        for (int i = start; i < ar.length; i++) {
            char c = ar[i];
            if ( c == '"') str = !str;
            if (c == search && !str) return i;
        }
        return -1;
    }
    //[if,i>0&&[clc,s,"get",i-1]==s.get(i),8]
    public static int indexOfDot(String s) {
        char[] ar = s.toCharArray();
        boolean str=false;
        char last=0;
        for (int i = 0; i < ar.length; i++) {
            char c = ar[i];
            if ( c == '"') {
                str = !str;
            }
            if (c == '=' && !str && ar[i+1] != '=' && ar[i-1] != '=') {
                return -1;
            }
            if (c == '.' && !str && !Character.isDigit(last)) {
                return i;
            }
            last = c;
        }
        return -1;
    }
    public static int endOfParams(String s, int start) {
        char[] ar = s.toCharArray();
        int deep=0;
        for (int i = start; i < ar.length; i++) {
            char c = ar[i];
            if (c == '(') deep++;
            if (c == ')') {
                deep--;
                if (deep==0) return i;
            }
        }
        return -1;
    }
    public static String cutParameter(String line, int start) {
        char[] ar = line.toCharArray();
        int deep=0;
        int beginning=0;
        int end = ar.length;
        for (int i = start; i >= 0; i--) {
            char c = ar[i];
            if (c == ']') deep++;
            if (c == '[') deep--;
            if ((c == ',' && deep==0 ) | (c== '[' && deep==-1)) {
                beginning = i + 1;
                break;
            }
        }
        deep=0;
        for (int i = start; i <ar.length; i++) {
            char c = ar[i];
            if (c == ']') deep--;
            if (c == '[') deep++;
            if ((c == ',' && deep==0 ) | (c== ']' && deep==-1)) {
                end = i ;
                break;
            }
        }
        return line.substring(beginning,end);
    }
    //return the length of the string if not found
    public static int firstSpecialCharacter(String s, int start) {
        char[] ar = s.toCharArray();
        for (int i = start; i < ar.length; i++) {
            char c = ar[i];
            if (notNormal(c)) {
                return i;
            }
        }
        return s.length();
    }
    public static int lastSpecialCharacter(String s, int start) {
        char[] ar = s.toCharArray();
        for (int i = start; i >= 0; i--) {
            char c = ar[i];
            if (notNormal(c)) {
                return i;
            }
        }
        return 0;
    }
    //[clc,s,"get",i-1]==[clc,s,"get",i]
    public static String[] findPartsOfComparison(String statement) {
        char[] ar =statement.toCharArray();
        String first=null;
        String second=null;
        String operator=null;
        int deep=0;
        for (int i = 0; i < ar.length; i++) {
            char c = ar[i];
            if (c=='[') deep++;
            else if (c == ']') deep--;
            else if (c == '<' | c=='>' | c == '!' | c=='=' | c=='?') {
                if (deep==0) {
                    first = statement.substring(0,i);
                    operator = String.valueOf(c);
                    if (ar[i+1] == '=') {
                        operator+='=';
                        i+=1;
                    }
                    second = statement.substring(i+1);
                    break;
                }
            }
        }
        if (operator == null) {
            throw new IllegalArgumentException(statement);
        }
        return new String[] {operator,first,second};
    }
    public static String removeWhitespace(String s) {
        StringBuilder builder = new StringBuilder();
        char[] ar = s.toCharArray();
        boolean insideString = false;
        for (char c : ar) {
            if (c == '"') {
                insideString = !insideString;
            }
            if (!Character.isWhitespace(c) | insideString) {
                builder.append(c);
            }
        }
        return builder.toString();
    }
    public static List<String> split(String line) {
        int deepness = 0;
        boolean insideString = false;
        ArrayList<String> parts = new ArrayList<>();
        char[] array = line.toCharArray();
        int lastCut = 0;
        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            switch (c) {
                case '[':
                    deepness++;
                    break;
                case ']':
                    deepness--;
                    break;
                case '"':
                    insideString = !insideString;
                    break;
                case ',':
                    if (deepness == 0 && !insideString) {
                        parts.add(line.substring(lastCut,i));
                        lastCut = i+1;
                    }
                    break;
            }
        }
        parts.add(line.substring(lastCut));
        return parts;
    }
    public static int affectOnLineCount(String line) {
        if (line.equals("}")) {
            return -1;
        }
        return 0;
    }
    public static int endOfSection(int start, List<String> lines) {
        int deep = 0;
        for (int i = start; i < lines.size(); i++) {
            String line = lines.get(i);
            if (contains(line,"}")) {
                deep--;
                if (deep==-1) {
                    throw new IllegalArgumentException("no "+start+" "+i+" "+lines.get(start));
                }
                if (deep==0) {
                    return i;
                }
            }
            if (contains(line,"{")) {
                deep++;
            }
        }
        throw new IllegalArgumentException("No closing bracket!");
    }

    public static int indexOfAny(String line, char[] chars) {
        for (char aChar : chars) {
            int index = indexOf(line, aChar, 0);
            if (index != -1) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Replaces every ; with linebreak
     * @return list of lines
     */
    public static List<String> splitToRows(String line) {
        ArrayList<String> lines = new ArrayList<>();
        int i;
        while ((i = indexOf(line,';',0)) != -1) {
            lines.add(line.substring(0,i));
            line = line.substring(i+1);
        }
        lines.add(line);
        return lines;
    }
    public static String removeComments(String line) {
        int i = removeStrings(line).indexOf("//");
        if (i != -1) {
            line = line.substring(0,i);
        }
        return line;
    }

    public static int endOfBracket(String s, int start) {
        char[] ar = s.toCharArray();
        int deep=0;
        for (int i = start; i < ar.length; i++) {
            char c = ar[i];
            if (c == '[') deep++;
            if (c == ']') {
                deep--;
                if (deep==0) return i;
            }
        }
        return -1;
    }
    private static int names = 0;
    public static String forLoops(String line) {
        if (line.startsWith("for(")) {
            int i = SUtil.indexOf(line,':',0);
            String list = line.substring(i+1,line.length()-2);
            String variable = line.substring(4,i);
            if (list.startsWith("range(")) {
                String name = "range" + (names++) + "list";
                line = name+"="+list+";itr=-1;while(itr<"+name+".length()-1){;itr++;"+variable+"="+name+"[itr]";
            } else {
                line = "itr=-1;while(itr<"+list+".length()-1){;itr++;"+variable+"="+list+"[itr]";
            }
        }
        return line;
    }

    public static String beforeStuff(String line) {
        if (line.startsWith("swap(")) { //swap a,b
            int seperator = line.indexOf(',');
            String first = line.substring(5,seperator);
            String second = line.substring(seperator+1,line.indexOf(')'));
            line = "te87mp="+first+";"+first+"="+second+";"+second+"=te87mp";
        }
        return line;
    }
}
