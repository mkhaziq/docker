package com.lst.vrp.controller;

import com.lst.vrp.service.GHService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SimpleController {
    @Value("${spring.application.name}")
    String appName;

    @Autowired
    GHService service;

    // @GetMapping("/")
    // public String homePage(Model model) {
    // model.addAttribute("appName", appName);
    // return "home";
    // }

    @RequestMapping(value = "/") // <2>
    public String index() {
        return "index"; // <3>
    }
}
