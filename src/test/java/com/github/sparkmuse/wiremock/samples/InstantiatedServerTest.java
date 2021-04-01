package com.github.sparkmuse.wiremock.samples;

import com.github.sparkmuse.wiremock.Wiremock;
import com.github.sparkmuse.wiremock.WiremockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(WiremockExtension.class)
class InstantiatedServerTest {

    @Wiremock
    private final WireMockServer postsServer = new WireMockServer(9000);

    @Test
    @DisplayName("uses values from instance wiremock server")
    void posts() throws Exception {

        postsServer.stubFor(get(urlEqualTo("/posts")).willReturn(aResponse().withStatus(200)));

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9000/posts"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }
}
