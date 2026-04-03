package com.example.microservice.transactions.model;

import java.math.BigDecimal;

public record Transaction(String debitAccount, String creditAccount, BigDecimal amount) {}
