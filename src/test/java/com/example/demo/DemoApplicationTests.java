package com.example.demo;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    void shouldReturnACashCardWhenDataIsSaved() {
        String responseBody = client.get()
                .uri("/demo/99")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        String expected = "{\"id\":99,\"amount\":123.45}";

        assertThat(responseBody).isEqualTo(expected);
    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
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
    void shouldCreateANewCashCard() {
        Demo demo = new Demo(null, 250.00);

        EntityExchangeResult<Void> result = client.post()
                .uri("/demo")
                .contentType(MediaType.APPLICATION_JSON)
                .body(demo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Void.class)
                .returnResult();

        assertThat(result.getStatus().value()).isEqualTo(HttpStatus.CREATED.value());

        URI loc = result.getResponseHeaders().getLocation();
        assertThat(loc).isNotNull();

        String responseBody = client.get()
                .uri(loc)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        DocumentContext documentContext = JsonPath.parse(responseBody);
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");

        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(250.00);
    }
}
