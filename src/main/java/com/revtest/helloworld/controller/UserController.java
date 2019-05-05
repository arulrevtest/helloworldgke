package com.revtest.helloworld.controller;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.revtest.helloworld.model.User;

import com.revtest.helloworld.repository.UserRepository;
import com.revtest.helloworld.validator.BirthdayValidator;
import com.revtest.helloworld.validator.UserNameValidator;

@RestController
public class UserController {
	@Autowired
	UserRepository userRepository;
	
	@PersistenceContext
	EntityManager entityManager;
	
	private final UserNameValidator userNameValidator;
	private final BirthdayValidator birthdayValidator;

    @Autowired
    public UserController(UserNameValidator userNameValidator, BirthdayValidator birthdayValidator) {
        this.userNameValidator = userNameValidator;
        this.birthdayValidator = birthdayValidator;
    }
    
    @GetMapping(value = "/healthcheck", produces = "application/json; charset=utf-8")
	public String getHealthCheck()
	{
		return "{ \"isWorking\" : true }";
	}    

	
	@GetMapping("/hello/{user_name}")
	public ResponseEntity<?> getUser(@PathVariable(value = "user_name") String userName) {
		
		User user = userRepository.findByUserName(userName);
		
		return new ResponseEntity<>(createMessageString(user),HttpStatus.OK);
	}


	@PutMapping("/hello/{user_name}")
	public ResponseEntity<?>  updateUser(@PathVariable(value = "user_name") String userName, @Valid @RequestBody User userDetails, Errors errors) {
		userNameValidator.validate(userName, errors);
        if (errors.hasErrors()) {
            return new ResponseEntity<>(createErrorString(errors), HttpStatus.BAD_REQUEST);
        }
        birthdayValidator.validate(userDetails.getDateOfBirth(), errors);
        if (errors.hasErrors()) {
            return new ResponseEntity<>(createErrorString(errors), HttpStatus.BAD_REQUEST);
        }
		User user = userRepository.findByUserName(userName);
		if (user != null) {
			user.setUserName(userName);
			user.setDateOfBirth(userDetails.getDateOfBirth());
			userRepository.save(user);
		} else {
			userDetails.setUserName(userName);
			userRepository.save(userDetails);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}	
	
	private String createErrorString(Errors errors) {
        return errors.getAllErrors().stream().map(ObjectError::toString).collect(Collectors.joining(","));
    }
	
	private String createMessageString(User user) {
		LocalDate dob = LocalDate.parse(user.getDateOfBirth());
		LocalDate now = LocalDate.now();
		LocalDate dobCurrentYear = LocalDate.of(now.getYear(), dob.getMonth(), dob.getDayOfMonth());
		LocalDate dobNextYear = LocalDate.of(now.getYear()+1, dob.getMonth(), dob.getDayOfMonth());
		long daysBetween = 0;
		
		if (dobCurrentYear.isEqual(now)) {
			return ("Hello "+ user.getUserName() + " Happy Birthday!");
		} else {
			if (dobCurrentYear.isBefore(now)) {				
				daysBetween = ChronoUnit.DAYS.between(now, dobNextYear);
			} else if (dobCurrentYear.isAfter(now)){
				daysBetween = ChronoUnit.DAYS.between(now, dobCurrentYear);
			}			
		}
		return "Hello " + user.getUserName() + " Your birthday is in "+daysBetween+ " Day(s)";
    }
	
	

}
