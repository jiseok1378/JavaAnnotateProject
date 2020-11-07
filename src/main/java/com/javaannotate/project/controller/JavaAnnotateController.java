package com.javaannotate.project.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPHeaderCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.javaannotate.project.engine.InfoCollector;
import com.javaannotate.project.engine.Print;
import com.javaannotate.project.engine.SeparatorCalc;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

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
