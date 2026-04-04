package com.example.microservice.atmservice.controller;

import com.example.microservice.atmservice.model.ATM;
import com.example.microservice.atmservice.model.Task;
import com.example.microservice.atmservice.service.ATMService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/atms")
public class ATMController {

    private final ATMService atmService;

    public ATMController(ATMService atmService) {
        this.atmService = atmService;
    }

    @PostMapping("/calculateOrder")
    public List<ATM> calculateOrder(@RequestBody List<Task> tasks) {
        return atmService.calculateOrder(tasks);
    }
}
