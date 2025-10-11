package com.example.zzserver.payment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/sample")
public class PaymentSampleController {


    @GetMapping("/payment")
    public ModelAndView samplePaymentPage() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/portone-sample");
        return mv;
    }
}
