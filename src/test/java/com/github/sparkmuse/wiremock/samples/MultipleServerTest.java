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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(WiremockExtension.class)
class MultipleServerTest {

    @Wiremock(port = 9000)
    private WireMockServer postsServer;

    @Wiremock(port = 8000)
    private WireMockServer statistics;

    @Test
    @DisplayName("uses multiple wiremock servers to improve readability")
    void posts() throws Exception {

        postsServer.stubFor(get(urlEqualTo("/posts")).willReturn(aResponse().withStatus(200)));
        statistics.stubFor(get(urlEqualTo("/statistics")).willReturn(aResponse().withStatus(200)));

        HttpClient client = HttpClient.newBuilder().build();

        HttpResponse<String> postsResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9000/posts"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> statsResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/statistics"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(postsResponse.statusCode()).isEqualTo(200);
        assertThat(statsResponse.statusCode()).isEqualTo(200);
    }
}
