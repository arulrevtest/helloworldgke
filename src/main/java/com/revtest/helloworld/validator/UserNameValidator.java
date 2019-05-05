package com.revtest.helloworld.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class UserNameValidator {

    public void validate(String userName, Errors errors) {
    	if (userName.matches(".*\\d.*")) {
    		errors.reject(userName + " must contains only letters");
    	}
    }
}
