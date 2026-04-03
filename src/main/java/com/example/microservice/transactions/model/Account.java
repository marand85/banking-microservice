package com.example.microservice.transactions.model;

import java.math.BigDecimal;

public record Account(String account, int debitCount, int creditCount, BigDecimal balance) {}
