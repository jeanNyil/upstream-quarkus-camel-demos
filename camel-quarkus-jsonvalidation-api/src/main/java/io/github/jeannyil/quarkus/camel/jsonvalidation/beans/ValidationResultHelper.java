package io.github.jeannyil.quarkus.camel.jsonvalidation.beans;

import io.github.jeannyil.quarkus.camel.jsonvalidation.models.ValidationResult;
import io.github.jeannyil.quarkus.camel.jsonvalidation.models.ValidationResult_;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * 
 * Error Response helper bean 
 *
 */
@ApplicationScoped
@Named("validationResultHelper")
@RegisterForReflection // Lets Quarkus register this class for reflection during the native build
public class ValidationResultHelper {
	
	/**
	 * Generates a successful ValidationResult
	 * @return successful ValidationResult
	 */
	public ValidationResult generateOKValidationResult() {
		ValidationResult validationResult = new ValidationResult();
		ValidationResult_ validationAttributes = new ValidationResult_();
		validationAttributes.setStatus("OK");
		validationResult.setValidationResult(validationAttributes);
		return validationResult;
	}
	
	/**
	 * Generates a KO ValidationResult
	 * @param errorMessage
	 * @return KO ValidationResult
	 */
	public ValidationResult generateKOValidationResult(String errorMessage) {
		ValidationResult validationResult = new ValidationResult();
		ValidationResult_ validationAttributes = new ValidationResult_();
		validationAttributes.setStatus("KO");
		validationAttributes.setErrorMessage(errorMessage);
		validationResult.setValidationResult(validationAttributes);
		return validationResult;
	}

}
