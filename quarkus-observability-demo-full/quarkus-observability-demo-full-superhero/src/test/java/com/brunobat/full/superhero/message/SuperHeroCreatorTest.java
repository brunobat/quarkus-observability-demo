package com.brunobat.full.superhero.message;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

@QuarkusTest
class SuperHeroCreatorTest {

    @Test
    void create() {
        given()
                .when().get("/heroes")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", is(0));

        given()
                .body("carrot")
                .when().post("/heroes/legume")
                .then()
                .statusCode(201);

        given()
                .when().get("/heroes")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", is(1),
                        "name", containsInAnyOrder("SUPER-carrot"));
    }
}