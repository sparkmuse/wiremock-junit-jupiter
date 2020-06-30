package com.github.sparkmuse.wiremock.samples;

import com.github.sparkmuse.wiremock.Wiremock;
import com.github.sparkmuse.wiremock.WiremockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
public class NestedServerTest {

    @Wiremock(port = 9000)
    private WireMockServer parentSever;

    @Test
    @DisplayName("uses values from instance wiremock server")
    void posts() throws Exception {

        parentSever.stubFor(get(urlEqualTo("/parent")).willReturn(aResponse().withStatus(200)));

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9000/parent"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Nested
    class NestedClass {

        @Wiremock(port = 5000)
        private WireMockServer nestedServer;

        @Test
        @DisplayName("uses parent and nested wiremock server")
        void posts() throws Exception {

            parentSever.stubFor(get(urlEqualTo("/parent")).willReturn(aResponse().withStatus(200)));
            nestedServer.stubFor(get(urlEqualTo("/nested")).willReturn(aResponse().withStatus(200)));

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> parentResponse = client.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:9000/parent"))
                    .GET()
                    .build(), HttpResponse.BodyHandlers.ofString());

            HttpResponse<String> nestedResponse = client.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5000/nested"))
                    .GET()
                    .build(), HttpResponse.BodyHandlers.ofString());

            assertThat(parentResponse.statusCode()).isEqualTo(200);
            assertThat(nestedResponse.statusCode()).isEqualTo(200);
        }
    }
}
