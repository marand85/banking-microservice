package com.example.microservice.onlinegame.model;

import java.util.List;

public record Players(int groupCount, List<Clan> clans) {
}
