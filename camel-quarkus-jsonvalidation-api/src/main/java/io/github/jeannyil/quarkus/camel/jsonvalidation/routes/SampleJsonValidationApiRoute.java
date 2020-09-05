package io.github.jeannyil.quarkus.camel.jsonvalidation.routes;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;

import io.github.jeannyil.quarkus.camel.jsonvalidation.models.ErrorResponse;

/* Exposes the Sample JSON Validation RESTful API

/!\ The @ApplicationScoped annotation is required for @Inject and @ConfigProperty to work in a RouteBuilder. 
	Note that the @ApplicationScoped beans are managed by the CDI container and their life cycle is thus a bit 
	more complex than the one of the plain RouteBuilder. 
	In other words, using @ApplicationScoped in RouteBuilder comes with some boot time penalty and you should 
	therefore only annotate your RouteBuilder with @ApplicationScoped when you really need it. */
public class SampleJsonValidationApiRoute extends RouteBuilder {

	private static String logName = SampleJsonValidationApiRoute.class.getName();
	
	@Override
	public void configure() throws Exception {
		
		/**
		 * Catch unexpected exceptions
		 */
		onException(Exception.class).id("handle-all-other-exceptions")
			.handled(true)
			.maximumRedeliveries(0)
			.log(LoggingLevel.ERROR, logName, ">>> ${routeId} - Caught exception: ${exception.stacktrace}").id("log-api-unexpected")
			.to("direct:common-500").id("to-common-500")
			.log(LoggingLevel.INFO, logName, ">>> ${routeId} - OUT: headers:[${headers}] - body:[${body}]").id("log-api-unexpected-response")
		;
		
		
		/**
		 * REST configuration with Camel servlet component
		 */
		restConfiguration()
			.component("platform-http")
			.enableCORS(true)
			.bindingMode(RestBindingMode.off) // RESTful responses will be explicitly marshaled for logging purposes
			.dataFormatProperty("prettyPrint", "true")
			.scheme("http")
			.host("0.0.0.0")
			.port("8080")
			.contextPath("/")
			.clientRequestValidation(true)
			// Add information for the generated Open API Specification
            .apiContextPath("/validateMembershipJSON/api-doc")
            	.apiContextRouteId("api-doc-route")
				.apiProperty("api.title", "Sample JSON Validation API")
				.apiProperty("api.version", "1.0.0-SNAPSHOT")
				.apiProperty("api.description", "A simple API to test the Camel json-schema-validator component")
				.apiProperty("api.contact.name", "Jean Nyilimbibi")
				.apiProperty("api.contact.email", "jean.nyilimbibi@gmail.com")
				.apiProperty("api.license.name", "MIT License")
				.apiProperty("api.license.url", "https://opensource.org/licenses/MIT")
				.apiProperty("cors", "true")
				.apiProperty("openapi.version", "3.0")
				.apiProperty("api.specification.contentType.json", "application/json")
				.apiProperty("api.specification.contentType.yaml", "text/yaml")
		;
		
		/**
		 * REST endpoint for the Sample JSON Validation RESTful API 
		 */
		rest().id("sample-json-validation-restapi")
			.consumes(MediaType.APPLICATION_JSON)
			.produces(MediaType.APPLICATION_JSON)
				
			// Validates a `Membership` JSON instance
			.post("/validateMembershipJSON")
				.id("json-validation-api-route")
				.description("Validates a `Membership` JSON instance")
				.param()
					.name("body")
					.type(RestParamType.body)
					.description("A `Membership` JSON instance to be validated.")
					.dataType("string")
					.required(true)
					.example(MediaType.APPLICATION_JSON,
							 "{\n    \"requestType\": \"API\",\n    \"requestID\": 5948,\n    \"memberID\": 85623617,\n    \"status\": \"A\",\n    \"enrolmentDate\": \"2019-06-16\",\n    \"changedBy\": \"jeanNyil\",\n    \"forcedLevelCode\": \"69\",\n    \"vipOnInvitation\": \"Y\",\n    \"startDate\": \"2019-06-16\",\n    \"endDate\": \"2100-06-16\"\n}")
				.endParam()
				.responseMessage()
					.code(Response.Status.OK.getStatusCode())
					.message(Response.Status.OK.getReasonPhrase())
					.responseModel(io.github.jeannyil.quarkus.camel.jsonvalidation.models.ValidationResult.class)
					.example(MediaType.APPLICATION_JSON, 
							 "{\n    \"validationResult\": {\n        \"status\": \"OK\"\n    }\n}")
				.endResponseMessage()
				.responseMessage()
					.code(Response.Status.BAD_REQUEST.getStatusCode())
					.message(Response.Status.BAD_REQUEST.getReasonPhrase())
					.responseModel(io.github.jeannyil.quarkus.camel.jsonvalidation.models.ValidationResult.class)
					.example(MediaType.APPLICATION_JSON, 
							 "{\n    \"validationResult\": {\n        \"status\": \"KO\",\n        \"errorMessage\": \"6 errors found\"\n    }\n}")
				.endResponseMessage()
				.responseMessage()
					.code(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
					.message(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase())
					.responseModel(ErrorResponse.class)
					.example(MediaType.APPLICATION_JSON, 
							 "{\n\t\"error\": {\n\t\t\"id\": \"500\",\n\t\t\"description\": \"Internal Server Error\",\n\t\t\"messages\": [\n\t\t\t\"java.lang.Exception: Mocked error message\"\n\t\t]\n\t}\n}")
				.endResponseMessage()
				// call the ValidateMembershipJSONRoute
				.to("direct:validateMembershipJSON")
		;
			
	}

}
