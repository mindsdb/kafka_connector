package com.mindsdb.kafka.connect.client;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MindsDBApiException extends Exception {
    public MindsDBApiException(HttpRequest request, HttpResponse<String> response) {
        super(getMessage(request, response));
    }

    private static String getMessage(HttpRequest request, HttpResponse<String> response) {
        StringBuilder builder = new StringBuilder("Failed to call MindsDB API");
        builder.append("\n### REQUEST ###");
        builder.append("\nURL -> ").append(request.uri());
        builder.append("\nMethod ->").append(request.method());
        builder.append("\n### RESPONSE ###");
        builder.append("\nStatus code -> ").append(response.statusCode());
        builder.append("\nBody -> ").append(response.body());

        return builder.toString();
    }
}
