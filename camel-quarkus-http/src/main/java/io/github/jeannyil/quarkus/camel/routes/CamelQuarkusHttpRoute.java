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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import io.github.jeannyil.quarkus.camel.models.Fruit;
import io.github.jeannyil.quarkus.camel.models.Legume;

/* Camel route definition

/!\ The @ApplicationScoped annotation is required for @Inject and @ConfigProperty to work in a RouteBuilder. 
	Note that the @ApplicationScoped beans are managed by the CDI container and their life cycle is thus a bit 
	more complex than the one of the plain RouteBuilder. 
	In other words, using @ApplicationScoped in RouteBuilder comes with some boot time penalty and you should 
	therefore only annotate your RouteBuilder with @ApplicationScoped when you really need it. */
public class CamelQuarkusHttpRoute extends RouteBuilder {
    private final Set<Fruit> fruits = Collections.synchronizedSet(new LinkedHashSet<>());
    private final Set<Legume> legumes = Collections.synchronizedSet(new LinkedHashSet<>());

    public CamelQuarkusHttpRoute() {

        /* Let's add some initial fruits */
        this.fruits.add(new Fruit("Apple", "Winter fruit"));
        this.fruits.add(new Fruit("Pineapple", "Tropical fruit"));
        this.fruits.add(new Fruit("Mango", "Tropical fruit"));
        this.fruits.add(new Fruit("Banana", "Tropical fruit"));

        /* Let's add some initial legumes */
        this.legumes.add(new Legume("Carrot", "Root vegetable, usually orange"));
        this.legumes.add(new Legume("Zucchini", "Summer squash"));
    }

    @Override
    public void configure() throws Exception {
        from("platform-http:/fruits?httpMethodRestrict=GET,POST")
            .routeId("fruits-restful-route")
            .choice()
                .when(simple("${header.CamelHttpMethod} == 'GET'"))
                    .log(LoggingLevel.INFO, "====> Processing GET fruits request...")
                    .setBody()
                        .constant(fruits)
                .endChoice()
                .when(simple("${header.CamelHttpMethod} == 'POST'"))
                    .log(LoggingLevel.INFO, "====> Processing POST fruits request: ${body}")
                    .unmarshal()
                        .json(JsonLibrary.Jackson, Fruit.class)
                    .process()
                        .body(Fruit.class, fruits::add)
                    .setBody()
                        .constant(fruits)
                .endChoice()
            .end()
            .marshal().json(JsonLibrary.Jackson, true)
            .choice()
                .when(simple("${header.CamelHttpMethod} == 'GET'"))
                    .log(LoggingLevel.INFO, "====> Sending GET fruits response: ${body}")
                .endChoice()
                .when(simple("${header.CamelHttpMethod} == 'POST'"))
                    .log(LoggingLevel.INFO, "====> Processing POST fruits DONE: ${body}")
                .endChoice()
            .end()
        ;

        from("platform-http:/legumes?httpMethodRestrict=GET")
            .routeId("legumes-restful-route")
            .log(LoggingLevel.INFO, "====> Processing GET legumes request...")
            .setBody().constant(legumes)
            .marshal().json(JsonLibrary.Jackson, true)
            .log(LoggingLevel.INFO, "====> Sending GET legumes response: ${body}")
        ;

    }
}
