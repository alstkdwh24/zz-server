package com.example.zzserver.member.controller;

import org.springframework.ui.Model;import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/login")
public class LoginController {
    @Value("${kakao.KakaoLoginJavaScriptKey}")
    private String kakaoLoginJavaScriptKey;

    @Value("${naver.naverClientId}")
    private String naverClientId;


    @GetMapping("/jo-login")
    public String logins(Model model) {

        model.addAttribute("kakaoLoginJavaScriptKey", kakaoLoginJavaScriptKey);
        model.addAttribute("naverClientId", naverClientId);
        System.out.println(naverClientId);
        return "login/jo-login";
    }


}
