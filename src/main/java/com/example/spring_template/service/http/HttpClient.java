package com.example.spring_template.service.http;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class HttpClient {

//    Example on how to call and handle the response
//    var response = this.httpClient.processPost(URL, requestBody.toString());
//    return this.parse(response.body());
//
//    private Competition parse(Object collection) {
//
//        if(collection == null) return null;
//
//        HashMap<String, Object> collection1 = (HashMap<String, Object>) JSONUtils.convertToObject(Object.class, (String) collection);
//
//        ObjectMapper mapper = new ObjectMapper();
//        return mapper.convertValue(collection1, new TypeReference<>() {});
//    }

    private final java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

    public HttpResponse<String> processPost(String url, String body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        return sendRequest(request);
    }

    public HttpResponse<String> processGet(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        return sendRequest(request);
    }

    public HttpResponse<String> processPut(String url, String body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        return sendRequest(request);
    }

    public HttpResponse<String> processDelete(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .header("Accept", "application/json")
                .build();

        return sendRequest(request);
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupt status
            throw new RuntimeException("HTTP request failed: " + e.getMessage(), e);
        }
    }
}
