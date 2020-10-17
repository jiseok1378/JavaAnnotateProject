package com.javaannotate.project.controller;

import com.javaannotate.project.engine.InfoCollector;
import com.javaannotate.project.engine.SeparatorCalc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class JavaAnnotateController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public HashMap<String, Object> home(HttpServletRequest request, HttpServletResponse response) throws Exception{
        InfoCollector infoCollector = new InfoCollector(System.getProperty("user.home"), "\\testCode.java");
        return infoCollector.AutoCollector();
    }
}
