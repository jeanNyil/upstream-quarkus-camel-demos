
package io.github.jeannyil.quarkus.camel.xmlvalidation.models;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "validationResult"
})
@RegisterForReflection // Lets Quarkus register this class for reflection during the native build
public class ValidationResult {

    @JsonProperty("validationResult")
    private ValidationResult_ validationResult;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("validationResult")
    public ValidationResult_ getValidationResult() {
        return validationResult;
    }

    @JsonProperty("validationResult")
    public void setValidationResult(ValidationResult_ validationResult) {
        this.validationResult = validationResult;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
