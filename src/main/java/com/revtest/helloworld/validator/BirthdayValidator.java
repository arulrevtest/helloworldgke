package com.revtest.helloworld.validator;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class BirthdayValidator{
	public void validate(String dateOfBirth, Errors errors) {
		String pattern = "yyyy-MM-dd";
		DateFormat format = new SimpleDateFormat(pattern);
		format.setLenient(false);
		try {
            Date birthdate = format.parse(dateOfBirth);
            Date today = new Date();
            if (birthdate.compareTo(today)>=0) {
            	errors.reject("Date " + dateOfBirth + " must be a date before the today date");
            }
        } catch (ParseException e) {
        	errors.reject("Date " + dateOfBirth + " is not valid according to " +
                    ((SimpleDateFormat) format).toPattern() + " pattern.");
        }
    }
}
