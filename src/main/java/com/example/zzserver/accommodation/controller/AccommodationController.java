package com.example.zzserver.accommodation.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/list")
public class AccommodationController {

    @Value("${kakao.KakaoLoginJavaScriptKey}")
    private String kakaoMap;

    @GetMapping("/accommodation/accommodationList")
    public String accommodationList(){
        return "list/accommodation/accommodationList";
    }


    @GetMapping("/accommodation/accommodationDetail")
    public String accommodationDetail(Model model){
        model.addAttribute("kakaoMap", kakaoMap);
        System.out.println("kakaoMap" + kakaoMap);
        return "list/accommodation/accommodationDetail";
    }
}
