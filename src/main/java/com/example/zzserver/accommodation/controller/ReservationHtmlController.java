package com.example.zzserver.accommodation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reservation")
public class ReservationHtmlController {

    @GetMapping("/beforeReservation")
    public String reservation(){
        return "reservation/beforeReservation";
    }

    @GetMapping("/reservationCancel")
    public String reservationCancel(){
        return "reservation/reservationCancel";
    }

    @GetMapping("/afterReservation")
    public String afterReservation(){
        return "reservation/afterReservation";
    }

    @GetMapping("/reservationDetail")
    public String reservationDetail(){
        return "reservation/reservationDetail";
    }
}
