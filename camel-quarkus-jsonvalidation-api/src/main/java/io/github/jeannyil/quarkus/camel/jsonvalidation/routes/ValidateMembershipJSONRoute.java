package io.github.jeannyil.quarkus.camel.jsonvalidation.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import javax.ws.rs.core.Response;

/* Route that validates a sample JSON instance against the Membership JSON schema.
   Expects the sample JSON as a Camel message body.

/!\ The @ApplicationScoped annotation is required for @Inject and @ConfigProperty to work in a RouteBuilder. 
	Note that the @ApplicationScoped beans are managed by the CDI container and their life cycle is thus a bit 
	more complex than the one of the plain RouteBuilder. 
	In other words, using @ApplicationScoped in RouteBuilder comes with some boot time penalty and you should 
	therefore only annotate your RouteBuilder with @ApplicationScoped when you really need it. */
public class ValidateMembershipJSONRoute extends RouteBuilder {
	
	private static String logName = ValidateMembershipJSONRoute.class.getName();

	@Override
	public void configure() throws Exception {
		
		/**
		 * Catch unexpected exceptions
		 */
		onException(Exception.class)
			.handled(true)
			.maximumRedeliveries(0)
			.log(LoggingLevel.ERROR, logName, ">>> ${routeId} - Caught exception: ${exception.stacktrace}").id("log-validateMembershipJSON-unexpected")
			.to("direct:common-500").id("to-validateMembershipJSON-500")
			.log(LoggingLevel.INFO, logName, ">>> ${routeId} - OUT: headers:[${headers}] - body:[${body}]").id("log-validateMembershipJSON-unexpected-response")
		;
		
		/**
		 * Catch the org.apache.camel.component.jsonvalidator.JsonValidationException exception
		 */
		onException(org.apache.camel.component.jsonvalidator.JsonValidationException.class)
			.handled(true)
			.maximumRedeliveries(0)
			.log(LoggingLevel.ERROR, logName, ">>> ${routeId} - Caught exception after JSON Schema Validation: ${exception.stacktrace}").id("log-validateMembershipJSON-exception")
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(Response.Status.BAD_REQUEST.getStatusCode()))
			.setProperty(Exchange.HTTP_RESPONSE_TEXT, constant(Response.Status.BAD_REQUEST.getReasonPhrase()))
			.setBody()
				.method("validationResultHelper", "generateKOValidationResult(${exception.message})")
				.id("set-KO-validationResult")
			.marshal().json(JsonLibrary.Jackson, true).id("marshal-KO-validationResult-to-json")
			.log(LoggingLevel.INFO, logName, ">>> ${routeId} - validateMembership response: headers:[${headers}] - body:[${body}]").id("log-validateMembershipJSON-KO-response")
		;
		
		/**
		 * Validates a sample JSON data against the Membership JSON schema.
		 * Expects the sample JSON as a Camel message body.
		 */
		from("direct:validateMembershipJSON")
			.routeId("validate-membership-json-route")
			.log(LoggingLevel.INFO, logName, ">>> ${routeId} - Before JSON Schema Validation - Camel Exchange message: in.headers[${headers}] - in.body[${body}]")
			.to("json-validator:json-schema/membership-schema.json?synchronous=true")
			.log(LoggingLevel.INFO, logName, ">>> ${routeId} - JSON Schema validation is successful")
			.setBody()
				.method("validationResultHelper", "generateOKValidationResult()")
				.id("set-OK-validationResult")
			.marshal().json(JsonLibrary.Jackson, true).id("marshal-OK-validationResult-to-json")
			.log(LoggingLevel.INFO, logName, ">>> ${routeId} - validateMembership response: headers:[${headers}] - body:[${body}]").id("log-validateMembershipJSON-response")
		;
		
	}

}
