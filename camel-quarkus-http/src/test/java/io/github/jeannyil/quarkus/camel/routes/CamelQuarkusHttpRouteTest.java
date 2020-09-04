/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jeannyil.quarkus.camel.routes;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * JVM mode tests.
 */
@QuarkusTest
public class CamelQuarkusHttpRouteTest {

    @Test
    public void fruits() {

        /* Assert the initial fruits are there */
        given()
                .when().get("/fruits")
                .then()
                .statusCode(200)
                .body(
                        "$.size()", is(4),
                        "name", containsInAnyOrder("Apple", "Pineapple", "Mango", "Banana"),
                        "description", containsInAnyOrder("Winter fruit", "Tropical fruit", "Tropical fruit", "Tropical fruit"));

        /* Add a new fruit */
        given()
                .body("{\"name\": \"Pear\", \"description\": \"Winter fruit\"}")
                .header("Content-Type", "application/json")
                .when()
                .post("/fruits")
                .then()
                .statusCode(200)
                .body(
                        "$.size()", is(5),
                        "name", containsInAnyOrder("Apple", "Pineapple", "Mango", "Banana", "Pear"),
                        "description", containsInAnyOrder("Winter fruit", "Tropical fruit", "Tropical fruit", "Tropical fruit", "Winter fruit"));
    }

    @Test
    public void legumes() {
        given()
                .when().get("/legumes")
                .then()
                .statusCode(200)
                .body("$.size()", is(2),
                        "name", containsInAnyOrder("Carrot", "Zucchini"),
                        "description", containsInAnyOrder("Root vegetable, usually orange", "Summer squash"));
    }

}
