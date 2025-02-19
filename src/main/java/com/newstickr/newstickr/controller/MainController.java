package com.newstickr.newstickr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// test, 기능 x
@Controller
public class MainController {

    @GetMapping("/")
    @ResponseBody
    public String mainAPI() {
        return "main route";
    }
}