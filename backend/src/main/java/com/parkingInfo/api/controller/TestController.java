package com.parkingInfo.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    // 테스트
    @GetMapping("/test")
    public String test(){
        return "index";
    }
}
