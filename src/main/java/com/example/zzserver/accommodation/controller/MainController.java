package com.example.zzserver.accommodation.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.zzserver.member.dto.request.Hotel;

@Controller
@RequestMapping("/mains")
public class MainController {

    @GetMapping("/main")
    public String home(Model model) {
        List<Hotel> hotels = getHotelList(); // 호텔 목록을 가져오는 메서드
        model.addAttribute("hotels", hotels);
        return "mains/main"; // home.html 뷰를 반환
    }

    private List<Hotel> getHotelList() {
        return Arrays.asList(new Hotel("호텔 A", "서울", "5성급", 4.5, "150,000원", null, null, null, null),
                new Hotel("호텔 B", "부산", "4성급", 4.0, "120,000원", null, null, null, null));
    }
}
