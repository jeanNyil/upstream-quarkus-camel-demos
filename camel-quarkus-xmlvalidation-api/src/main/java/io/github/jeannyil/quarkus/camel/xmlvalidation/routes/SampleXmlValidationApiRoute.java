package io.github.jeannyil.quarkus.camel.xmlvalidation.routes;

import javax.ws.rs.core.MediaType;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;

import io.github.jeannyil.quarkus.camel.xmlvalidation.models.ErrorResponse;

/* Exposes the Sample XML Validation RESTful API

/!\ The @ApplicationScoped annotation is required for @Inject and @ConfigProperty to work in a RouteBuilder. 
	Note that the @ApplicationScoped beans are managed by the CDI container and their life cycle is thus a bit 
	more complex than the one of the plain RouteBuilder. 
	In other words, using @ApplicationScoped in RouteBuilder comes with some boot time penalty and you should 
	therefore only annotate your RouteBuilder with @ApplicationScoped when you really need it. */
public class SampleXmlValidationApiRoute extends RouteBuilder {

	private static String logName = SampleXmlValidationApiRoute.class.getName();

	
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
            .apiContextPath("/validateMembershipXML/api-doc")
				.apiContextRouteId("api-doc-route")
				.apiProperty("api.title", "Sample XML Validation API")
				.apiProperty("api.version", "1.0.0-SNAPSHOT")
				.apiProperty("api.description", "A simple API to test the Camel XML validator component")
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
		 * REST endpoint for the Sample XML Validation RESTful API 
		 */
		rest().id("sample-xml-validation-restapi").description("Sample XML Validation Service")
			.consumes(MediaType.TEXT_XML)
			.produces(MediaType.APPLICATION_JSON)
				
			// Validates a `Membership` XML instance
			.post("/validateMembershipXML")
				.id("xml-validation-api-route")
				.description("Validates a `Membership` XML instance")
				.param()
					.name("body")
					.type(RestParamType.body)
					.description("A `Membership` XML instance to be validated.")
					.dataType("string")
					.required(true)
					.example(MediaType.TEXT_XML,
							 "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<p:membership xmlns:p=\"http://www.github.com/jeanNyil/schemas/membership/v1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n  <p:requestType>API</p:requestType>\n  <p:requestID>5948</p:requestID>\n  <p:memberID>85623617</p:memberID>\n  <p:status>A</p:status>\n  <p:enrolmentDate>2019-06-29</p:enrolmentDate>\n  <p:changedBy>jeanNyil</p:changedBy>\n  <p:forcedLevelCode>69</p:forcedLevelCode>\n  <p:vipOnInvitation>Y</p:vipOnInvitation>\n  <p:startDate>2019-06-29</p:startDate>\n  <p:endDate>2100-06-29</p:endDate>\n</p:membership>")
				.endParam()
				.responseMessage()
					.code("200")
					.message("OK")
					.responseModel(io.github.jeannyil.quarkus.camel.xmlvalidation.models.ValidationResult.class)
					.example(MediaType.APPLICATION_JSON, 
							 "{\n    \"validationResult\": {\n        \"status\": \"OK\"\n    }\n}")
				.endResponseMessage()
				.responseMessage()
					.code("404")
					.message("Bad Request")
					.responseModel(io.github.jeannyil.quarkus.camel.xmlvalidation.models.ValidationResult.class)
					.example(MediaType.APPLICATION_JSON, 
							 "{\n\t\"validationResult\": {\n\t\t\"status\": \"KO\",\n\t\t\"errorMessage\": \"Validation failed for: com.sun.org.apache.xerces.internal.jaxp.validation.SimpleXMLSchema@5f86796e\\nerrors: [\\norg.xml.sax.SAXParseException: cvc-datatype-valid.1.2.1: '20-06-29' is not a valid value for 'date'., Line : 7, Column : 46\\norg.xml.sax.SAXParseException: cvc-type.3.1.3: The value '20-06-29' of element 'p:enrolmentDate' is not valid., Line : 7, Column : 46\\n]. Exchange[ID-jeansmacbookair-home-1561803539861-1-1]\"\n\t}\n}")
				.endResponseMessage()
				.responseMessage()
					.code("500")
					.message("Internal Server Error")
					.responseModel(ErrorResponse.class)
					.example(MediaType.APPLICATION_JSON, 
							 "{\n\t\"error\": {\n\t\t\"id\": \"500\",\n\t\t\"description\": \"Internal Server Error\",\n\t\t\"messages\": [\n\t\t\t\"java.lang.Exception: Mocked error message\"\n\t\t]\n\t}\n}")
				.endResponseMessage()
				// call the ValidateMembershipXMLRoute
				.to("direct:validateMembershipXML")
		;
			
	}

}
