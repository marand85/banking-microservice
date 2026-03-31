package com.example.microservice.transactions.controller;

import com.example.microservice.transactions.model.Account;
import com.example.microservice.transactions.model.Transaction;
import com.example.microservice.transactions.service.TransactionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/report")
    public List<Account> report(@RequestBody List<Transaction> transactions) {
        return transactionService.generateReport(transactions);
    }
}
