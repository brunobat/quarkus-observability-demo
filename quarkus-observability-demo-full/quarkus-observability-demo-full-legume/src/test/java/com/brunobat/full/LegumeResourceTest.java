package com.brunobat.full;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

@QuarkusTest
public class LegumeResourceTest {

    private static final int PORT = 9000;  // The port for the other service
    private static final String BASE_URL = "http://localhost:" + PORT;

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void setup() {
        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        // Stub the POST endpoint
        stubFor(post(urlEqualTo("/heroes/legume"))
                .withRequestBody(matching("\\b(?:Carrot|Zucchini)\\b")) // regex to match either Carrot or Zucchini
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBody("SUPER-Carrot")));
    }

    @AfterAll
    public static void teardown() {
        wireMockServer.stop();
    }

    @Test
    public void testProvisionAndList() {
        given()
                .header("Content-Type", "application/json; encoding=utf8; charset=utf8")
                .when().post("/legumes/init")
                .then()
                .statusCode(201);

        given()
                .when().get("/legumes")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", is(2),
                        "name", containsInAnyOrder("Carrot", "Zucchini"),
                        "description", containsInAnyOrder("Root vegetable, usually orange", "Summer squash"));
    }
}
