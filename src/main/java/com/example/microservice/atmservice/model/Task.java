package com.example.microservice.atmservice.model;

public record Task(int region, RequestType requestType, int atmId) {
}
