package com.revtest.helloworld.exception;

public class UserNotFoundException extends Exception {
	private long user_id;

	public UserNotFoundException(long user_id) {
		super(String.format("User is not found with id : '%s'", user_id));
	}
}
