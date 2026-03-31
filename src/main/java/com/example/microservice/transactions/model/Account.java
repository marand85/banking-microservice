package com.example.microservice.transactions.model;

public record Account(String account, int debitCount, int creditCount, double balance) {}
