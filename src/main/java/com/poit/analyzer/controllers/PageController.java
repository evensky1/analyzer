package com.poit.analyzer.controllers;

import com.poit.analyzer.model.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PageController {
    @GetMapping
    public String page(Model model) {
        model.addAttribute("inputCode", new Code());
        return "mainPage";
    }
    @PostMapping
    public String page(@ModelAttribute("inputCode") Code code, Model model){
        //строка передаётся в класс-модель Code, в котором следует организовать логику парсера
        System.out.println(code.getCode()); //тестирование
        //пока не разобрался как отставить код программы в textarea после отправки
        return "mainPage";
    }
}
