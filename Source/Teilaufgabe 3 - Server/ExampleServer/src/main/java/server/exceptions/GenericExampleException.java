package server.exceptions;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Note, this is just a quick minimal example to show the generic exception handling
 * functionality in spring. Typically I would recommend that you create individual exception types
 * for each unique kind of exception (e.g., individual exceptions for different kinds of rule errors)
 * 
 * While doing so, follow the tips given in the Error Handling Tutorial.
 * 
 * For simplicity reasons, your own exceptions could, e.g., inherit from the one given below
 */
public class GenericExampleException extends RuntimeException {
	

	private static final Logger logger = LoggerFactory.getLogger(GenericExampleException.class);

	private final String errorName;

	public GenericExampleException(String errorName, String errorMessage) {
		super(errorMessage);
		this.errorName = errorName;	
		
		logger.error(errorName + ": " +errorMessage);
	}

	public String getErrorName() {
		return errorName;
	}
}
