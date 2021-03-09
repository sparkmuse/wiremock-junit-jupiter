package com.github.sparkmuse.wiremock.samples;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.sparkmuse.wiremock.Wiremock;
import com.github.sparkmuse.wiremock.WiremockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;

@ExtendWith(WiremockExtension.class)
public class TestsAreIsolatedTest {

    @Wiremock
    private WireMockServer server;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/test")).GET().build();

    @Test
    @DisplayName("test without stubs defined equals number of unmatched requests")
    void test_one() throws Exception {

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(2, server.findAllUnmatchedRequests().size());
    }

    @Test
    @DisplayName("test without stubs defined equals number of unmatched requests AGAIN")
    void same_as_test_one() throws Exception {

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(2, server.findAllUnmatchedRequests().size());
    }
}
