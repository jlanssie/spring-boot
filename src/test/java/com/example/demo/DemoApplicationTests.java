package com.example.demo;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.net.URI;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class DemoApplicationTests {

    String username = "sarah1";
    String password = "abc123";
    String credentials = username + ":" + password;
    String encodedAuth = Base64.getEncoder().encodeToString(credentials.getBytes());
    String authHeader = "Basic " + encodedAuth;

    @Autowired
    private RestTestClient client;

    @Test
    @DirtiesContext
    void createDemo() {
        Demo demo = new Demo(null, 250.00, "sarah1");

        EntityExchangeResult<Void> postResult = client.post()
                .uri("/demo")
                .contentType(MediaType.APPLICATION_JSON)
                .body(demo)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Void.class)
                .returnResult();

        assertThat(postResult.getStatus().value()).isEqualTo(HttpStatus.CREATED.value());

        URI location = postResult.getResponseHeaders().getLocation();
        assertThat(location).isNotNull();

        EntityExchangeResult<String> getResult = client.get()
                .uri(location)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
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
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(String.class)
                .returnResult();

        String responseBody = result.getResponseBody();

        String expected = "{\"id\":99,\"amount\":123.45,\"owner\":\"sarah1\"}";

        assertThat(responseBody).isEqualTo(expected);
    }

    @Test
    void readDemo_404() {
        EntityExchangeResult<String> result = client.get()
                .uri("/demo/1000")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
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
                .header(HttpHeaders.AUTHORIZATION, authHeader)
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
                .header(HttpHeaders.AUTHORIZATION, authHeader)
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
                .header(HttpHeaders.AUTHORIZATION, authHeader)
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
                .header(HttpHeaders.AUTHORIZATION, authHeader)
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

    @Test
    void unauthenticated() {
        String faulty_username = "faulty-user";
        String faulty_password = "faulty-password";
        String faulty_credentials = faulty_username + ":" + faulty_password;
        String faulty_encodedAuth = Base64.getEncoder().encodeToString(faulty_credentials.getBytes());
        String faulty_authHeader = "Basic " + faulty_encodedAuth;

        EntityExchangeResult<String> result = client.get()
                .uri("/demo/99")
                .header(HttpHeaders.AUTHORIZATION, faulty_authHeader)
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody(String.class)
                .returnResult();

        assertThat(result.getStatus().value())
                .as("Checking HTTP Status Code")
                .isEqualTo(HttpStatus.UNAUTHORIZED.value());

        String responseBody = result.getResponseBody();

        assertThat(responseBody).isNullOrEmpty();
    }

    @Test
    void unauthorized() {
        String faulty_username = "hank-owns-no-cards";
        String faulty_password = "qrs456";
        String faulty_credentials = faulty_username + ":" + faulty_password;
        String faulty_encodedAuth = Base64.getEncoder().encodeToString(faulty_credentials.getBytes());
        String faulty_authHeader = "Basic " + faulty_encodedAuth;

        EntityExchangeResult<String> result = client.get()
                .uri("/demo/99")
                .header(HttpHeaders.AUTHORIZATION, faulty_authHeader)
                .exchange()
                .expectStatus()
                .isForbidden()
                .expectBody(String.class)
                .returnResult();

        assertThat(result.getStatus().value())
                .as("Checking HTTP Status Code")
                .isEqualTo(HttpStatus.FORBIDDEN.value());

        String responseBody = result.getResponseBody();

        assertThat(responseBody).isNullOrEmpty();
    }

    @Test
    void ownership() {
        EntityExchangeResult<String> result = client.get()
                .uri("/demo/102")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(String.class)
                .returnResult();

        assertThat(result.getStatus().value())
                .as("Checking HTTP Status Code")
                .isEqualTo(HttpStatus.NOT_FOUND.value());

        String responseBody = result.getResponseBody();

        assertThat(responseBody).isNullOrEmpty();
    }
}
