package com.example.microservice.transactions.service;

import com.example.microservice.transactions.model.Account;
import com.example.microservice.transactions.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    // Helper class for accumulating per-account state during transaction processing.
    // Intentionally nested — it is an implementation detail of this service, not a domain model.
    private static class AccountState {
        int debitCount;
        int creditCount;
        BigDecimal balance = BigDecimal.ZERO;
    }

    public List<Account> generateReport(List<Transaction> transactions) {
        // HashMap gives O(1) put; explicit sort at the end is O(n log n).
        // This is faster in practice than TreeMap (O(log n) put) while being asymptotically equivalent.
        Map<String, AccountState> state = new HashMap<>();

        for (Transaction tx : transactions) {
            state.computeIfAbsent(tx.debitAccount(), k -> new AccountState());
            state.computeIfAbsent(tx.creditAccount(), k -> new AccountState());

            AccountState debit = state.get(tx.debitAccount());
            debit.debitCount++;
            debit.balance = debit.balance.subtract(tx.amount());

            AccountState credit = state.get(tx.creditAccount());
            credit.creditCount++;
            credit.balance = credit.balance.add(tx.amount());
        }

        List<Account> result = new ArrayList<>(state.size());
        for (Map.Entry<String, AccountState> entry : state.entrySet()) {
            AccountState s = entry.getValue();
            result.add(new Account(entry.getKey(), s.debitCount, s.creditCount, s.balance));
        }

        result.sort((a, b) -> a.account().compareTo(b.account()));
        return result;
    }
}
