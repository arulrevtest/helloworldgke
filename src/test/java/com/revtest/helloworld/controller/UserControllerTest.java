package com.revtest.helloworld.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.revtest.helloworld.model.User;
import com.revtest.helloworld.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
 
    @MockBean
    private UserRepository userRepository;
 
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void userBirthdayNotToday() {
        // given
        given(userRepository.findByUserName("Arul"))
                .willReturn(new User(1L, "Arul", "1967-06-12"));
 
        // when
        ResponseEntity<?> controllerResponse = restTemplate.getForEntity("/hello/Arul", String.class);
 
        // then
        assertThat(controllerResponse.getBody().toString().contains("Hello Arul Your birthday is in "));
    }
    
    @Test
    public void userBirthdayToday() {
    	
    	Date date = Calendar.getInstance().getTime();  
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
        String strDate = dateFormat.format(date);  
        
        // given
        given(userRepository.findByUserName("Arul"))
                .willReturn(new User(1L, "Arul", strDate));
 
        // when
        ResponseEntity<?> controllerResponse = restTemplate.getForEntity("/hello/Arul", String.class);
 
        // then        
        assertThat(controllerResponse.getBody().toString().contains("Hello Arul Happy Birthday!"));
    }
    
    
    @Test
    public void userBirthdayInvalid() { 
        // when
        ResponseEntity<?> controllerResponse = restTemplate.postForEntity("/hello/Arulk", 
        		new User(1L, "Arulk", "2019-06-12"), UserController.class);
 
        // then
        assertThat(controllerResponse.getBody().toString().contains("Date 2019-06-12 must be a date before the today date"));
    }
    
    @Test
    public void userNameInvalid() {
    	// when
        ResponseEntity<?> controllerResponse = restTemplate.postForEntity("/hello/Arul2", 
        		new User(1L, "Arul2", "1967-06-12"), UserController.class);
 
        // then
        assertThat(controllerResponse.getBody().toString().contains("Arul2 must contains only letters"));
    }
    
}
 

