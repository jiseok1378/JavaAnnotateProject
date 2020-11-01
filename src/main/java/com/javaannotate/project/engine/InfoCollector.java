package com.javaannotate.project.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoCollector extends fileUtil {
    public enum CollectorFlag {
        VARIABLE, FUNCTION, ENUM, CLASS
    }

    public static final String REGEX_CLASS = "(((\\s*)(public|private|protected)?(\\s+)(abstract(\\s+))?(class|interface)\\s+\\S+\\s*)((extends|implements)\\s+((\\S*\\s*,\\s*)*)?\\S*\\s*)?((implements)\\s+((\\S*\\s*,\\s*)*)?\\S*\\s*)?)\\{";
    public static final String REGEX_FUNCTION = "(public|private|protected)(((\\s*(static|final)\\s+)?)*)\\s*\\S+\\s*(<.*>)?\\s+\\S+\\s*(\\(.*?\\))";
    public static final String REGEX_ENUM = "enum\\s+\\S+\\s*\\{";
    public static final String REGEX_VARIABLE = "[A-z|1-9]+\\s+[A-z|1-9]+\\s+=\\s+.+;";
    //public static final String REGEX_VARIABLE = "(((public|private|protected)\\s*)?)((((static|final)\\s*){2})?)(\\w+\\s*(<.*>)?(\\[([0-9])?\\])?\\s+)(\\w+\\s*)(=(\\s*\\(.*\\))*?\\s*(new\\s+)?\\S+(\\s*<.*>\\s*\\(.*\\))?\\s*\\S*\\s*)?;";
    //public static final String REGEX_VARIABLE = "(\\w*\\s*)(\\[.*\\])?(<.*>)?\\s+\\w+\\s*[^+\\-/*](=(\\s*\\S+.*)?[^{])?;";
    public String preconvFileContents = "";
    public String path;
    public InfoCollector(String path) {
        super(path);
        this.path = path;
        PreConverter preConverter = new PreConverter();
        for (String line : readFile().split("\r\n")) {
            line = preConverter.preconverter(line);
            if (PreConverter.annotationFlag == true) {
                continue;
            }

            preconvFileContents += line + "\r\n";
        }

        preconvFileContents = preConverter.removeLineBreak(preconvFileContents);
        preconvFileContents = preConverter.removeLineSpace(preconvFileContents);
        Print.print(preconvFileContents);
    }
    public HashMap<String, Object> AutoCollector(){
        InfoCollector infoCollector = new InfoCollector(this.path);
        ArrayList<Object> classList = classCollector();
        ArrayList<Object> a = variableCollector();
        HashMap<String, Object> inFileClassFuntion = new HashMap<>();
/*
        for (String className : classList) {
            String classBody = SeparatorCalc.calc(preconvFileContents, className);
            ArrayList<String> functionList = functionCollector(classBody);
            HashMap<String, Object> inClassFuntion = new HashMap<>();
            for(String functionName : functionList){

                String functionBody = SeparatorCalc.calc(classBody,functionName);
                inClassFuntion.put(functionName, functionBody);


                //preconvFileContents = preconvFileContents.replaceAll(Pattern.quote(functionName), "")
                //        .replaceAll(Pattern.quote(functionBody), "");
                ArrayList<String> varList = variableCollector(functionBody);
                Print.print(varList.toString());
            }
            inFileClassFuntion.put(className, inClassFuntion);
        }

*/
        return inFileClassFuntion;
    }
    public ArrayList<Object> classCollector() {
        ArrayList<Object> classList = getPatternMatch(preconvFileContents, REGEX_CLASS, CollectorFlag.CLASS);
        return classList;
    }

    public ArrayList<Object> enumCollector() {
        ArrayList<Object> enumList = getPatternMatch(preconvFileContents, REGEX_ENUM, CollectorFlag.ENUM);
        return enumList;
    }

    public ArrayList<Object> functionCollector() {
        ArrayList<Object> functionList = getPatternMatch(preconvFileContents, REGEX_FUNCTION, CollectorFlag.FUNCTION);
        return functionList;
    }

    public ArrayList<Object> variableCollector() {
        ArrayList<Object> variableList = getPatternMatch(preconvFileContents, REGEX_VARIABLE, CollectorFlag.VARIABLE);
        return variableList;
    }

    public ArrayList<Object> getPatternMatch(String fileContents, String regex, CollectorFlag FLAG) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileContents);
        ArrayList<Object> patternArray = new ArrayList<>();

        while (matcher.find()) {
            HashMap<String, Object> map = new HashMap<>();
            if (FLAG == CollectorFlag.CLASS) {
                map.put("className", matcher.group().replaceAll("\\{", "").trim());
                map.put("classAnnotate", "");
                patternArray.add(map);
            }
            else if(FLAG == CollectorFlag.ENUM) {
                map.put("enumName", matcher.group().replaceAll("\\{", "").trim());
                map.put("enumAnnotate", "");
                patternArray.add(map);
            }
            else if (FLAG == CollectorFlag.FUNCTION){
                if(!matcher.group().contains("class") || !matcher.group().contains("interface") || !matcher.group().contains("enum")){
                    map.put("functionName", matcher.group().replaceAll("\\{", "").trim());
                    map.put("functionAnnotate", "");
                    patternArray.add(map);
                }
            }
            else if (FLAG == CollectorFlag.VARIABLE) {
                if (matcher.group().contains("=") && matcher.group()
                        .trim().split("=")[0]
                        .split(" ").length == 1) {
                    continue;
                }
                else {
                    if(matcher.group().contains("return") ||matcher.group().contains("continue")||matcher.group().contains("break")){
                        continue;
                    }
                    map.put("variableName", matcher.group().trim());
                    map.put("variableAnnotate", "");
                    patternArray.add(map);
                }

            }
        }
        return patternArray;
    }

}
