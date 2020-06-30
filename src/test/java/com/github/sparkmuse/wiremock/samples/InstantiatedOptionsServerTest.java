package com.github.sparkmuse.wiremock.samples;

import com.github.sparkmuse.wiremock.Wiremock;
import com.github.sparkmuse.wiremock.WiremockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(WiremockExtension.class)
class InstantiatedOptionsServerTest {

    @Wiremock
    private final WireMockServer postsServer = new WireMockServer(
            WireMockConfiguration.options()
                    .port(9000)
                    .containerThreads(20));

    @Test
    @DisplayName("uses values from instance wiremock server")
    void posts() throws Exception {

        postsServer.stubFor(get(urlEqualTo("/posts")).willReturn(aResponse().withStatus(200)));

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9000/posts"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }
}
