package com.example.zzserver.accommodation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/list")
public class AccommodationListController {

    @GetMapping("/accommodation/accommodationList")
    public String accommodationList(){
        return "list/accommodation/accommodationList";
    }
}
