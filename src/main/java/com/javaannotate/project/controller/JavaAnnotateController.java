package com.javaannotate.project.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.javaannotate.project.engine.InfoCollector;
import com.javaannotate.project.engine.Print;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

@RestController
public class JavaAnnotateController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public HashMap<String, Object> home(HttpServletRequest request, HttpServletResponse response) throws Exception{
        InfoCollector infoCollector = new InfoCollector(System.getProperty("user.home")+ "\\testCode.java");
        return infoCollector.AutoCollector();
    }
    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    public JSONObject collect(HttpServletRequest request, HttpServletResponse response, @RequestBody String json) throws  Exception{
        JSONParser jp = new JSONParser();
        JSONObject map = (JSONObject) jp.parse(json);
        ArrayList<String> fileList = (ArrayList<String>)map.get("fileList");

        ArrayList<Object> collectingFileList = new ArrayList<>();
        for(String filePath : fileList){
            Print.print(filePath);
            String[] fileNametemp = filePath.split("\\\\");
            String fileName = fileNametemp[fileNametemp.length -1];
            InfoCollector infoCollector = new InfoCollector(filePath);
            JSONObject collecting = new JSONObject();
            collecting.put("filePath", filePath);
            collecting.put("enumList", infoCollector.enumCollector());
            collecting.put("functionList",infoCollector.functionCollector());
            collecting.put("variableList",infoCollector.variableCollector());
            collecting.put("classList", infoCollector.classCollector());
            collectingFileList.add(collecting);
        }
        JSONObject result = new JSONObject();
        result.put("result", collectingFileList);
        return result;
    }
    public String csvStringBuilder(List<String> list){
        if(list.size() == 0){
            return "";
        }
        else{
            String buildingString = "";

            for(String i : list){
                buildingString += "%s,";
            }
            buildingString = buildingString.substring(0,buildingString.length() - 1);
            return String.format(buildingString, list.toArray());
        }
    }
    @RequestMapping(value = "/csv", method = RequestMethod.POST)
    public String CSVCreate(HttpServletRequest request, HttpServletResponse response, @RequestBody String json) throws Exception{

        JSONParser jp = new JSONParser();
        JSONObject map = (JSONObject) jp.parse(json);
        String projectPath = (String)map.get("projectPath");
        StringBuilder sb = new StringBuilder();
        sb.append("filePath,className,Annotate,functionName,Annotate,enumName,Annotate,variableName,Annotate\n");
        int i = 0;
        for(Object a : (JSONArray)map.get("data")){
            List<String> sbData = new ArrayList<>();
            JSONObject data = (JSONObject)a;
            String path = ((String)data.get("filePath")).replaceAll(Pattern.quote(projectPath), "");
            sb.append(path + ",");

            ArrayList<Object> enumList = (ArrayList)data.get("enumList");
            ArrayList<Object> functionList = (ArrayList)data.get("functionList");
            ArrayList<Object> classList = (ArrayList)data.get("classList");
            ArrayList<Object> variableList = (ArrayList)data.get("variableList");

            ArrayList sizeList = new ArrayList();
            sizeList.add(enumList.size());
            sizeList.add(functionList.size());
            sizeList.add(classList.size());
            sizeList.add(variableList.size());
            Integer maxSize = (Integer)Collections.max(sizeList);

            Print.print(maxSize.toString());
            for (int j = 0; j <= maxSize; j++){
                if(classList.size() > j ){
                    JSONObject classInfo = (JSONObject) classList.get(j);
                    if((Boolean) classInfo.get("delete")){
                        sbData.add("");
                        sbData.add("");
                    }
                    else{
                        sbData.add(((String)classInfo.get("className")).replaceAll(",", "，").replaceAll("\n",""));
                        sbData.add(((String)classInfo.get("classAnnotate")).replaceAll(",", "，").replaceAll("\n",""));
                    }
                }
                else{
                    sbData.add("");
                    sbData.add("");
                }
                if(functionList.size() > j){
                    JSONObject functionInfo = (JSONObject) functionList.get(j);
                    if((Boolean) functionInfo.get("delete")){
                        sbData.add("");
                        sbData.add("");
                    }
                    else{
                        sbData.add(((String)functionInfo.get("functionName")).replaceAll(",", "，").replaceAll("\n",""));
                        sbData.add(((String)functionInfo.get("functionAnnotate")).replaceAll(",", "，").replaceAll("\n",""));
                    }
                }
                else{
                    sbData.add("");
                    sbData.add("");
                }

                if(enumList.size() > j){
                    JSONObject enumInfo = (JSONObject) enumList.get(j);
                    if((Boolean) enumInfo.get("delete")){
                        sbData.add("");
                        sbData.add("");
                    }
                    else{
                        sbData.add(((String)enumInfo.get("enumName")).replaceAll(",", "，").replaceAll("\n",""));
                        sbData.add(((String)enumInfo.get("enumAnnotate")).replaceAll(",", "，").replaceAll("\n",""));
                    }
                }
                else{
                    sbData.add("");
                    sbData.add("");
                }

                if(variableList.size() > j) {
                    JSONObject variableInfo = (JSONObject) variableList.get(j);
                    if ((Boolean) variableInfo.get("delete")) {
                        sbData.add("");
                        sbData.add("");
                    }
                    else {
                        sbData.add(((String) variableInfo.get("variableName")).replaceAll(",", "，").replaceAll("\n", ""));
                        sbData.add(((String) variableInfo.get("variableAnnotate")).replaceAll(",", "，").replaceAll("\n", ""));
                    }
                }
                else {
                    sbData.add("");
                    sbData.add("");
                }
                AtomicBoolean t = new AtomicBoolean(false);
                sbData.stream().parallel().forEach(x->{
                    if(!x.equals("")){
                        t.set(true);
                    };
                });
                if(t.get()){
                    sbData.add("\n");
                }
                else{
                    sbData = new ArrayList<>();
                }
            }
            sb.append(csvStringBuilder(sbData)+"\n");
        }
        Print.print(sb.toString());
        return sb.toString();
    }
    @RequestMapping(value = "/pdf")
    public String pdfCreate(HttpServletRequest req) throws Exception {
        String fileName="";
        String dir=System.getProperty("user.home");
        fileName = "simple_table.pdf";

        File directory = new File(dir);
        if(!directory.exists()) directory.mkdirs(); //파일경로 없으면 생성

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(dir+"/"+fileName));

        document.open();
        PdfPTable table = new PdfPTable(4);
        document.add(new Paragraph("FileName"));


        document.newPage();
        document.add(new Paragraph("Test2"));
        document.close();
        return "/techmng/tech02";
    }
}
