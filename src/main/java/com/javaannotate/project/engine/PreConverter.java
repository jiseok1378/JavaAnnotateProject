package com.javaannotate.project.engine;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreConverter {
    // //<-삭제
    public static boolean annotationFlag = false;
    public static int StringNum = 0;
    public static int charNum = 0;
    public static HashMap<String, String> fileStringMap= new HashMap<>();
    public static HashMap<String, String> fileCharMap= new HashMap<>();
    public static String preconverter(String line) {
        line = removeOneLineAnno(line);
        line = removeMultiAnno(line);
        line = removeMultiAnno2(line);
        line = removeChar(line);
        line = removeString(line);
        return line;
    }
    public static String removeString(String line){
        Pattern pattern = Pattern.compile("\".*\"");
        Matcher matcher = pattern.matcher(line);
        if(matcher.find()) {
            line = line.replaceAll("\".*\"","\"__REPLACE__STRING"+ StringNum +"__\"");
            StringNum ++;
        }
        return line;
    }
    public static String removeChar(String line){
        Pattern pattern = Pattern.compile("'(\\{|})'");
        Matcher matcher = pattern.matcher(line);
        if(matcher.find()){
            line = line.replaceAll("'(\\{|})'", "'__REPLACE__CHAR" + charNum + "__'");
            charNum ++;
        }
        return line;
    }
    // /* */ 삭제
    public static String removeOneLineAnno(String line){
        Pattern pattern = Pattern.compile("\".*//.*\"");
        Matcher matcher = pattern.matcher(line);
        if (line.contains("//")) {
            if (!matcher.find()) {
                line = line.split("//")[0];
            }
        }
        return line;
    }
    public static String removeMultiAnno(String line) {
        Pattern pattern = Pattern.compile("\".*/\\*.*\"");
        Matcher matcher = pattern.matcher(line);
        if (line.contains("/*")) {
            if (!matcher.find()) {
                annotationFlag = true;
            }
        }
        pattern = Pattern.compile("\".*\\*/.*\"");
        matcher = pattern.matcher(line);
        if(line.contains("*/")){
            if (!matcher.find()) {
                annotationFlag = false;
                line = line.replaceAll(".*\\*\\/", "");
            }
        }
        return line;
    }
    public static String removeMultiAnno2(String line) {
        Pattern pattern = Pattern.compile("\".*/\\*\\*.*\"");
        Matcher matcher = pattern.matcher(line);
        if (line.contains("/**")) {
            if (!matcher.find()) {
                annotationFlag = true;
            }
        }
        pattern = Pattern.compile("\".*\\*/.*\"");
        matcher = pattern.matcher(line);
        if(line.contains("*/")){
            if (!matcher.find()) {
                annotationFlag = false;
                line = line.replaceAll(".*\\*\\/", "");
            }
        }
        return line;
    }
}
