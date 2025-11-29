package com.example.demo;

import com.example.demo.data.Demo;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class DemoTest {

    @Autowired
    private JacksonTester<Demo> json;

    @Autowired
    private JacksonTester<Demo[]> jsonList;

    private Demo[] demos;

    @BeforeEach
    void setUp() {
        demos = Arrays.array(
                new Demo(99L, 123.45, "sarah1"),
                new Demo(100L, 1.00, "sarah1"),
                new Demo(101L, 150.00, "sarah1")
        );
    }

    @Test
    void demoSerializationTest() throws IOException {
        Demo demo = demos[0];
        assertThat(json.write(demo)).isStrictlyEqualToJson("demo.json");
        assertThat(json.write(demo)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(demo)).extractingJsonPathNumberValue("@.id").isEqualTo(99);
        assertThat(json.write(demo)).hasJsonPathNumberValue("@.amount");
        assertThat(json.write(demo)).extractingJsonPathNumberValue("@.amount").isEqualTo(123.45);
    }

    @Test
    void demoDeserializationTest() throws IOException {
        String expected = """
                {
                    "id": 99,
                    "amount": 123.45,
                    "owner": "sarah1"
                }
                """;
        assertThat(json.parse(expected)).isEqualTo(new Demo(99L, 123.45, "sarah1"));
        assertThat(json.parseObject(expected).id()).isEqualTo(99);
        assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
    }

    @Test
    void demoListSerializationTest() throws IOException {
        assertThat(jsonList.write(demos)).isStrictlyEqualToJson("demos.json");
    }

    @Test
    void demoListDeserializationTest() throws IOException {
        String expected="""
         [
            { "id": 99, "amount": 123.45, "owner": "sarah1" },
            { "id": 100, "amount": 1.00, "owner": "sarah1" },
            { "id": 101, "amount": 150.00, "owner": "sarah1" }
         ]
         """;
        assertThat(jsonList.parse(expected)).isEqualTo(demos);
    }
}
