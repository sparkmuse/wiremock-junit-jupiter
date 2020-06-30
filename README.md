
# Wiremock Junit Jupiter
[![Build Status](https://travis-ci.org/sparkmuse/wiremock-junit-jupiter.svg?branch=master)](https://travis-ci.org/sparkmuse/wiremock-junit-jupiter)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=wiremock-junit-jupiter&metric=alert_status)](https://sonarcloud.io/dashboard?id=wiremock-junit-jupiter)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=wiremock-junit-jupiter&metric=coverage)](https://sonarcloud.io/dashboard?id=wiremock-junit-jupiter)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.sparkmuse/wiremock-junit-jupiter.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.sparkmuse/wiremock-junit-jupiter)

Project user Wiremock servers with Junit5.


## Install

All needed to start using the project is to add the dependency to the POM and that's it.

```xml
<dependency>
    <groupId>com.github.sparkmuse</groupId>
    <artifactId>wiremock-junit-jupiter</artifactId>
    <version>${version}</version>
</dependency>
```

or to gradle

```groovy
compile 'com.github.sparkmuse:properties-mapper:${version}'
```

## Usage

### Simple example
1. Extend the test class with ```@ExtendWith(WiremockExtension.class) ```
2. Annotate the WireMockServer with ```@Wiremock```

```java
@ExtendWith(WiremockExtension.class)
public class SimpleServerTest {

    @Wiremock
    private WireMockServer server;

    @Test
    @DisplayName("simple test")
    void posts() {

        server.stubFor(get(urlEqualTo("/posts")).willReturn(aResponse().withStatus(200)));

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> postsResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/posts"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(postsResponse.statusCode()).isEqualTo(200);
    }
}
```

### Multiple servers
Multiple servers can be set to improve code readability

```java
@ExtendWith(WiremockExtension.class)
public class MultipleServerTest {

    @Wiremock(port = 9000)
    private WireMockServer postsServer;

    @Wiremock(port = 8000)
    private WireMockServer statistics;

    @Test
    @DisplayName("uses multiple wiremock servers to improve readability")
    void posts() {
        // rest of the test
    }
}
```

### Customize it your way
If the simple options that come with the  @Wiremock annotation feel free to add your own configuration options. The instance will be managed automatically

```java
@ExtendWith(WiremockExtension.class)
public class InstantiatedOptionsServerTest {

    @Wiremock
    private WireMockServer postsServer = new WireMockServer(
            WireMockConfiguration.options()
                    .port(9000)
                    .containerThreads(20));

    @Test
    @DisplayName("uses values from instance wiremock server")
    void posts() {
        // rest of the test
    }
}
```
