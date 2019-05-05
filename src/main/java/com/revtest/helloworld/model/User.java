package com.revtest.helloworld.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "users")
public class User {
	@Id
    @GeneratedValue
    private Long id;
	
	@JsonProperty
	private String userName;
	
	@JsonProperty
	private String dateOfBirth;

	public User() {
		super();
	}

	public User(Long id, String user_name, String dateOfBirth) {
		super();
		this.userName = user_name;
		this.dateOfBirth = dateOfBirth;
	}	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}	

}