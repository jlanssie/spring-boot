package com.example.demo;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {

    RestTestClient client;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        client = RestTestClient.bindToApplicationContext(context).build();
    }

    @Test
    @DirtiesContext
    void createDemo() {
        Demo demo = new Demo(null, 250.00);

        EntityExchangeResult<Void> postResult = client.post()
                .uri("/demo")
                .contentType(MediaType.APPLICATION_JSON)
                .body(demo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Void.class)
                .returnResult();

        assertThat(postResult.getStatus().value()).isEqualTo(HttpStatus.CREATED.value());

        URI loc = postResult.getResponseHeaders().getLocation();
        assertThat(loc).isNotNull();

        EntityExchangeResult<String> getResult = client.get()
                .uri(loc)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult();

        String responseBody = getResult.getResponseBody();

        DocumentContext documentContext = JsonPath.parse(responseBody);
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");

        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(250.00);
    }

    @Test
    void readDemo() {
        EntityExchangeResult<String> result = client.get()
                .uri("/demo/99")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(String.class)
                .returnResult();

        String responseBody = result.getResponseBody();

        String expected = "{\"id\":99,\"amount\":123.45}";

        assertThat(responseBody).isEqualTo(expected);
    }

    @Test
    void readDemo_404() {
        EntityExchangeResult<String> result = client.get()
                .uri("/demo/1000")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(String.class)
                .returnResult();

        assertThat(result.getStatus().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getResponseBody()).isNullOrEmpty();
    }

    @Test
    void readDemos() {
        EntityExchangeResult<String> result = client.get()
                .uri("/demo")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .returnResult();

        assertThat(result.getStatus().value())
                .as("Checking HTTP Status Code")
                .isEqualTo(HttpStatus.OK.value());

        assertThat(result.getResponseBody()).isNotNull();

        String responseBody = result.getResponseBody();
        DocumentContext documentContext = JsonPath.parse(responseBody);
        int demoCount = documentContext.read("$.length()");
        assertThat(demoCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.0, 150.00);
    }

    @Test
    void readDemos_pagination() {
        EntityExchangeResult<String> result = client.get()
                .uri("/demo?page=0&size=1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .returnResult();

        String responseBody = result.getResponseBody();
        DocumentContext documentContext = JsonPath.parse(responseBody);
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);
    }

    @Test
    void readDemos_sorting() {
        EntityExchangeResult<String> result = client.get()
                .uri("/demo?page=0&size=1&sort=amount,desc")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .returnResult();

        String responseBody = result.getResponseBody();

        DocumentContext documentContext = JsonPath.parse(responseBody);
        JSONArray read = documentContext.read("$[*]");
        assertThat(read.size()).isEqualTo(1);

        double amount = documentContext.read("$[0].amount");
        assertThat(amount).isEqualTo(150.00);
    }

    @Test
    void readDemos_default() {
        EntityExchangeResult<String> result = client.get()
                .uri("/demo")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .returnResult();

        String responseBody = result.getResponseBody();

        DocumentContext documentContext = JsonPath.parse(responseBody);
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(3);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactly(1.00, 123.45, 150.00);

    }
}
