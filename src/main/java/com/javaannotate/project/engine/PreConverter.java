package com.javaannotate.project.engine;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreConverter {
    // //<-삭제
    public static boolean LineBreakFlag = false;
    public static boolean annotationFlag = false;
    public int StringNum = 0;
    public int charNum = 0;
    public HashMap<String, String> fileStringMap= new HashMap<>();
    public HashMap<String, String> fileCharMap= new HashMap<>();
    public String preconverter(String line) {
        line = removeChar(line);
        line = removeString(line);
        line = removeOneLineAnno(line);
        line = removeMultiAnno(line);
        line = removeMultiAnno2(line);

        return line;
    }
    public String removeLineSpace(String fileContents){
        return fileContents.replaceAll(" +", " ");
    }
    public String removeLineBreak(String fileContents){
        String[] fileLineArray = fileContents.split("\r\n");
        String removeLineBreakContents = "";
        for(String line : fileLineArray){
            if(line.contains(";") || line.contains("{") || line.contains("}")){
                removeLineBreakContents += line + "\r\n";
                continue;
            }
            else{
                removeLineBreakContents += line;
            }
        }
        return removeLineBreakContents;
    }
    public String removeString(String line){
        Pattern pattern = Pattern.compile("\".*\"");
        Matcher matcher = pattern.matcher(line);
        if(matcher.find()) {
            line = line.replaceAll("\".*\"","\"__REPLACE__STRING"+ StringNum +"__\"");
            StringNum ++;
        }
        return line;
    }
    public String removeChar(String line){
        Pattern pattern = Pattern.compile("'(\\{|})'");
        Matcher matcher = pattern.matcher(line);
        if(matcher.find()){
            line = line.replaceAll("'(\\{|})'", "'__REPLACE__CHAR" + charNum + "__'");
            charNum ++;
        }
        return line;
    }
    // /* */ 삭제
    public String removeOneLineAnno(String line){
        Pattern pattern = Pattern.compile("\".*//.*\"");
        Matcher matcher = pattern.matcher(line);
        if (line.contains("//")) {
            if (!matcher.find()) {
                line = line.replaceAll("//.*", "");
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
                line = line.replaceAll("/\\*.*\\*\\/", "");
            }
        }
        return line;
    }
    public String removeMultiAnno2(String line) {
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
                line = line.replaceAll("\\/\\*\\*.*\\*\\/", "");
            }
        }
        return line;
    }
}
