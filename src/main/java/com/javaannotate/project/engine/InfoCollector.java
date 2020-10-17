package com.javaannotate.project.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoCollector extends fileUtil {
    public enum CollectorFlag {
        VARIABLE, FUNCTION, OTHERS
    }

    public static final String REGEX_CLASS = "(((\\s*)(public|private|protected)?(\\s+)(abstract(\\s+))?(class|interface)\\s+\\S+\\s*)((extends|implements)\\s+((\\S*\\s*,\\s*)*)?\\S*\\s*)?((implements)\\s+((\\S*\\s*,\\s*)*)?\\S*\\s*)?)\\{";
    public static final String REGEX_FUNCTION = "(public|private|protected)(((\\s*(static|final)\\s+)?)*)\\s*\\S+\\s*(<.*>)?\\s+\\S+\\s*(\\(.*?\\))";
    public static final String REGEX_ENUM = "((public|private|protected)\\s*)?enum\\s*\\S*\\s*\\{";
    //public static final String REGEX_VARIABLE = "(((public|private|protected)\\s*)?)((((static|final)\\s*){2})?)(\\w+\\s*(<.*>)?(\\[([0-9])?\\])?\\s+)(\\w+\\s*)(=(\\s*\\(.*\\))*?\\s*(new\\s+)?\\S+(\\s*<.*>\\s*\\(.*\\))?\\s*\\S*\\s*)?;";
    public static final String REGEX_VARIABLE = "(\\w*\\s*)(\\[.*\\])?(<.*>)?\\s+\\w+\\s*[^+\\-/*](=(\\s*\\S+.*)?[^{])?;";
    public String preconvFileContents = "";
    public String path;
    public String name;
    public InfoCollector(String path, String name) {
        super(path, name);
        this.path = path;
        this.name = name;
        for (String line : readFile().split("\n")) {
            line = PreConverter.preconverter(line);
            if (PreConverter.annotationFlag == true) {
                continue;
            }
            preconvFileContents += line + "\n";
        }
    }
    public HashMap<String, Object> AutoCollector(){
        InfoCollector infoCollector = new InfoCollector(this.path, this.name);
        ArrayList<String> classList = classCollector(preconvFileContents);
        HashMap<String, Object> inFileClassFuntion = new HashMap<>();

        for (String className : classList) {
            String classBody = SeparatorCalc.calc(preconvFileContents, className);
            ArrayList<String> functionList = functionCollector(classBody);
            HashMap<String, Object> inClassFuntion = new HashMap<>();
            for(String functionName : functionList){
                String functionBody = SeparatorCalc.calc(classBody,functionName);
                inClassFuntion.put(functionName, functionBody);

                preconvFileContents = preconvFileContents.replaceAll(Pattern.quote(functionName), "")
                        .replaceAll(Pattern.quote(functionBody), "");
            }

            inFileClassFuntion.put(className, inClassFuntion);
        }
        System.out.println(preconvFileContents);
        return inFileClassFuntion;
    }
    public ArrayList<String> classCollector(String preconvFileContents) {
        ArrayList<String> classList = getPatternMatch(preconvFileContents, REGEX_CLASS, CollectorFlag.OTHERS);
        System.out.println(classList);
        return classList;
    }

    public ArrayList<String> enumCollector(String preconvFileContents) {
        ArrayList<String> enumList = getPatternMatch(preconvFileContents, REGEX_ENUM, CollectorFlag.OTHERS);
        System.out.println(enumList);
        return enumList;
    }

    public ArrayList<String> functionCollector(String preconvFileContents) {
        ArrayList<String> functionList = getPatternMatch(preconvFileContents, REGEX_FUNCTION, CollectorFlag.FUNCTION);
        System.out.println(functionList);
        return functionList;
    }

    public ArrayList<String> variableCollector(String preconvFileContents) {
        ArrayList<String> variableList = getPatternMatch(preconvFileContents, REGEX_VARIABLE, CollectorFlag.VARIABLE);
        System.out.println(variableList);
        return variableList;
    }

    public static ArrayList<String> getPatternMatch(String fileContents, String regex, CollectorFlag FLAG) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileContents);
        ArrayList<String> patternArray = new ArrayList<>();

        while (matcher.find()) {

            if (FLAG == CollectorFlag.OTHERS) {
                patternArray.add(matcher.group().replaceAll("\\{", "").trim());
            }
            else if (FLAG == CollectorFlag.FUNCTION){
                if(!matcher.group().contains("class") || !matcher.group().contains("interface") || !matcher.group().contains("enum")){
                    patternArray.add(matcher.group().replaceAll("\\{", "").trim());
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
                    patternArray.add(matcher.group().trim());
                }

            }
        }
        return patternArray;
    }

}
