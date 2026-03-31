package com.example.microservice.transactions.model;

public record Transaction(String debitAccount, String creditAccount, double amount) {}
