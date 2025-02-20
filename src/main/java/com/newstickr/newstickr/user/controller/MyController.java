package com.newstickr.newstickr.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// test, 기능 x
@Controller
public class MyController {

    @GetMapping("/my")
    @ResponseBody
    public String myAPI() {
        return "my route";
    }
}