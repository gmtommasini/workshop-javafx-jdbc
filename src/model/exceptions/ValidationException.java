package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private Map<String,String> errors = new HashMap<>(); // field name and error message
	
	public ValidationException(String msg) {
		super(msg);
	}
	
	// GEtters and Setters
	public Map<String, String> getErrors() {
		return errors;
	}

	public void addError(String field, String msg) {
		errors.put(field, msg);
	}


}
