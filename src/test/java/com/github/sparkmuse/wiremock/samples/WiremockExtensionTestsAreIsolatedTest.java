package com.github.sparkmuse.wiremock.samples;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.sparkmuse.wiremock.Wiremock;
import com.github.sparkmuse.wiremock.WiremockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;

@ExtendWith(WiremockExtension.class)
public class WiremockExtensionTestsAreIsolatedTest {

    @Wiremock
    private WireMockServer wiremock;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/test")).GET().build();

    @Test
    void test_one() throws Exception {
        // this test doesn't have any stubs defined
        // any request will be treated as unmatched

        // we make only two requests
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // and as a result we have exactly two unmatched requests, not four
        // as tests are isolated and wiremock.resetAll() is called between the tests
        // same_as_test_one has no impact on this test
        Assertions.assertEquals(2, wiremock.findAllUnmatchedRequests().size());
    }

    @Test
    void same_as_test_one() throws Exception {
        // this test doesn't have any stubs defined
        // any request will be treated as unmatched

        // we make only two requests
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // and as a result we have exactly two unmatched requests, not four
        // as tests are isolated and wiremock.resetAll() is called between the tests
        // test_one has no impact on this test
        Assertions.assertEquals(2, wiremock.findAllUnmatchedRequests().size());
    }
}
