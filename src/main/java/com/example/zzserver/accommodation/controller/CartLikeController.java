package com.example.zzserver.accommodation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
public class CartLikeController {
    @GetMapping("/cart")
    public String cart(){
        return "cart/cart";
    }
}
