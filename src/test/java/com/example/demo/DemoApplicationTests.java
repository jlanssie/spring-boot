package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

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
}
