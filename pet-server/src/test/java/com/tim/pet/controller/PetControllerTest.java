package com.tim.pet.controller;

import static com.jayway.restassured.RestAssured.basic;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.tim.PetServerApplication;
import com.tim.pet.dao.entity.Pet;
import com.tim.pet.dao.repo.PetRepository;

@RunWith(SpringJUnit4ClassRunner.class) 
@SpringApplicationConfiguration(classes = PetServerApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0") 
public class PetControllerTest {
	
	private Pet pet;

	@Value("${local.server.port}")
    int port;
	
	@Autowired
	private PetRepository petRepo;
	
	@Before
	public void setUp() {
		pet = buildPet("Blackie", "The dog");
		petRepo.save(pet);
		assertNotNull(pet.getId());
		
		RestAssured.port = port;
		RestAssured.authentication = basic("santo", "baby");
	}

	public void testGetPet() {
		when().
			get("pet/{id}", pet.getId()).
		then().
			statusCode(HttpStatus.SC_OK).
			body("name", is("Blackie")).
			body("description", is("The dog")).
			body("id", convertToInt(pet.getId()));
	}
	
	public void testPostPet() {
		Map<String, Object> newPet = new HashMap<>();
		newPet.put("name", "Rex");
		newPet.put("description", "Husky");
		
		given().
			contentType(ContentType.JSON).
			body(newPet).
		when().
			post("pet").
		then().
			statusCode(HttpStatus.SC_OK).
			body(notNullValue());
	}
	
	public void testDeletePet() {
		when().
			delete("pet/{id}", pet.getId()).
		then()
			.statusCode(HttpStatus.SC_OK);
	}

	/*
	 * Need to convert all longs to ints for assert to work
	 */
	private Matcher<Integer> convertToInt(Long value) {
		return is(Integer.valueOf(value.toString()));
	}
	
	private Pet buildPet(String name, String description) {
		Pet pet = new Pet();
		pet.setName(name);
		pet.setDescription(description);
		return pet;
	}

}
