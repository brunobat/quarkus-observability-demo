package com.brunobat.activemq.superhero.message;

import com.brunobat.activemq.superhero.ArtemisTestResource;
import com.brunobat.activemq.superhero.model.Hero;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
@QuarkusTestResource(ArtemisTestResource.class)
class SuperHeroCreatorTest {

    @Inject
    TestMessageSender messageSender;

    @Test
    void receiveMessageAndAdd() {
        given()
                .when().get("/heroes")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", is(0));

        messageSender.send("carrot");

        await().until(() -> given()
                .when().get("/heroes")
                .then()
                .extract().body().as((new ArrayList()).getClass())
                .size() > 0);

        given()
                .when().get("/heroes")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", is(1),
                        "name", containsInAnyOrder("SUPER-carrot"));
    }

    @ApplicationScoped
    @Slf4j
    static public class TestMessageSender {
        @Inject
        ConnectionFactory connectionFactory;

        public void send(String str) {
            try (JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
                context.createProducer().send(context.createQueue("heroes"), str);
                log.info("sent message: " + str);
            }
        }
    }
}