package com.example.microservice.onlinegame.controller;

import com.example.microservice.onlinegame.model.Clan;
import com.example.microservice.onlinegame.model.Players;
import com.example.microservice.onlinegame.service.OnlineGameService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/onlinegame")
public class OnlineGameController {

    private final OnlineGameService onlineGameService;

    public OnlineGameController(OnlineGameService onlineGameService) {
        this.onlineGameService = onlineGameService;
    }

    @PostMapping("/calculate")
    public List<List<Clan>> calculate(@RequestBody Players players) {
        return onlineGameService.calculate(players);
    }
}
