package com.github.sparkmuse.wiremock.samples;

import com.github.sparkmuse.wiremock.Wiremock;
import com.github.sparkmuse.wiremock.WiremockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(WiremockExtension.class)
class ParameterizedServerTest {

    @Wiremock
    private WireMockServer server;

    @ParameterizedTest
    @ValueSource(strings = {"1", "2"})
    @DisplayName("simple test")
    void posts(String value) throws Exception {

        server.stubFor(get(urlEqualTo("/posts/" + value)).willReturn(aResponse().withStatus(200)));

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> postsResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/posts/" + value))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(postsResponse.statusCode()).isEqualTo(200);
    }
}
