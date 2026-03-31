package com.example.microservice.transactions.service;

import com.example.microservice.transactions.model.Account;
import com.example.microservice.transactions.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    public List<Account> generateReport(List<Transaction> transactions) {
        // account number -> mutable state: [debitCount, creditCount, balance]
        Map<String, double[]> state = new HashMap<>();

        for (Transaction tx : transactions) {
            state.computeIfAbsent(tx.debitAccount(), k -> new double[3]);
            state.computeIfAbsent(tx.creditAccount(), k -> new double[3]);

            double[] debit = state.get(tx.debitAccount());
            debit[0]++;              // debitCount
            debit[2] -= tx.amount(); // balance

            double[] credit = state.get(tx.creditAccount());
            credit[1]++;              // creditCount
            credit[2] += tx.amount(); // balance
        }

        List<Account> result = new ArrayList<>(state.size());
        for (Map.Entry<String, double[]> entry : state.entrySet()) {
            double[] s = entry.getValue();
            result.add(new Account(entry.getKey(), (int) s[0], (int) s[1], s[2]));
        }

        result.sort((a, b) -> a.account().compareTo(b.account()));
        return result;
    }
}
