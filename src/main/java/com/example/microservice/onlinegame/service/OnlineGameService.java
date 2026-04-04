package com.example.microservice.onlinegame.service;

import com.example.microservice.onlinegame.model.Clan;
import com.example.microservice.onlinegame.model.Players;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class OnlineGameService {

    public List<List<Clan>> calculate(Players players) {
        List<Clan> sorted = new ArrayList<>(players.clans());
        sorted.sort(Comparator
            .comparingInt(Clan::points).reversed()
            .thenComparingInt(Clan::numberOfPlayers));

        LinkedList<Clan> remaining = new LinkedList<>(sorted);
        List<List<Clan>> groups = new ArrayList<>();

        while (!remaining.isEmpty()) {
            List<Clan> group = new ArrayList<>();
            int capacity = players.groupCount();

            Iterator<Clan> it = remaining.iterator();
            while (it.hasNext()) {
                Clan clan = it.next();
                if (clan.numberOfPlayers() <= capacity) {
                    group.add(clan);
                    capacity -= clan.numberOfPlayers();
                    it.remove();
                    if (capacity == 0) break;
                }
            }

            groups.add(group);
        }

        return groups;
    }
}
