package com.example.microservice.atmservice.service;

import com.example.microservice.atmservice.model.ATM;
import com.example.microservice.atmservice.model.RequestType;
import com.example.microservice.atmservice.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ATMService {

    public List<ATM> calculateOrder(List<Task> tasks) {
        // TreeMap sorts regions ascending; LinkedHashMap preserves first-seen order per ATM.
        Map<Integer, Map<Integer, RequestType>> regionMap = new TreeMap<>();

        for (Task task : tasks) {
            regionMap
                .computeIfAbsent(task.region(), k -> new LinkedHashMap<>())
                .merge(task.atmId(), task.requestType(), (existing, incoming) ->
                    existing.getPriority() <= incoming.getPriority() ? existing : incoming);
        }

        List<ATM> result = new ArrayList<>();

        for (Map.Entry<Integer, Map<Integer, RequestType>> regionEntry : regionMap.entrySet()) {
            int region = regionEntry.getKey();

            List<Map.Entry<Integer, RequestType>> atms = new ArrayList<>(regionEntry.getValue().entrySet());
            // Stable sort: ATMs with equal priority keep their first-seen order.
            atms.sort(Comparator.comparingInt(e -> e.getValue().getPriority()));

            for (Map.Entry<Integer, RequestType> atm : atms) {
                result.add(new ATM(region, atm.getKey()));
            }
        }

        return result;
    }
}
