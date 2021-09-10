package com.poit.analyzer.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping
    public String page(){
        return "mainPage";
    }
    @GetMapping("/start")
    public String testCall(){
        return "mainPage2";
    }
}
