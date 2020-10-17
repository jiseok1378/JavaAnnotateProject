package com.javaannotate.project.engine;

public class SeparatorCalc {
    public static String calc(String fileContents, String target) {
        int startCount = 0;
        int endCount = 0;
        int startIndex = 0;
        int endIndex = 0;
        boolean checkStartCount = false;
        String temp = "";
        fileContents = fileContents.substring(fileContents.indexOf(target));
        for (String ch : fileContents.split("")) {
            if (ch.equals("{")) {
                startCount++;
                checkStartCount = true;
            }
            if (ch.equals("}")) {
                endCount++;
            }
            if(startCount == 1 && checkStartCount){
                startIndex = temp.length();
                checkStartCount = false;
            }
            if ((startCount != 0 || endCount != 0) && (startCount == endCount)) {
                endIndex = temp.length();
                break;
            }
            temp += ch;
        }
        return fileContents.substring(startIndex, endIndex + 1);
    }
}
