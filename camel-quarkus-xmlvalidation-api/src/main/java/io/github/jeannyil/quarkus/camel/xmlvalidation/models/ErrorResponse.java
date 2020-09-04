
package io.github.jeannyil.quarkus.camel.xmlvalidation.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Root Type for error
 * <p>
 * Error response
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "error"
})
@RegisterForReflection // Lets Quarkus register this class for reflection during the native build
public class ErrorResponse {

    @JsonProperty("error")
    private Error error;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ErrorResponse() {
    }

    /**
     * 
     * @param error
     */
    public ErrorResponse(Error error) {
        super();
        this.error = error;
    }

    @JsonProperty("error")
    public Error getError() {
        return error;
    }

    @JsonProperty("error")
    public void setError(Error error) {
        this.error = error;
    }

}
