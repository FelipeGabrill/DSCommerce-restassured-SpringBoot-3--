package com.dv.dscommerce.controllers;

import static io.restassured.RestAssured.baseURI;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;


public class ProductControllerRA {
	
	private Long existingProductId, nonExistingProductId;
	
	private String productName;
	
	private Map<String, Object> postProductInstance;
	
	@BeforeEach
	public void setUp() {
		baseURI = "http://localhost:8080";
		productName = "Macbook";
		postProductInstance = new HashMap<>();
		postProductInstance.put("name", "Meu novo pedido");
		postProductInstance.put("description", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim");
		postProductInstance.put("imgUrl", "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
		postProductInstance.put("price", 110.0);

		List<Map<String, Object>> categories = new ArrayList<>();
		
		Map<String, Object> category1 = new HashMap<>();
		Map<String, Object> category2 = new HashMap<>();
		
		category1 .put("id", 2);
		category1 .put("id", 3);
		
		categories.add(category1);		
		categories.add(category2);
		
		postProductInstance.put("categories", categories);

	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists() {
		existingProductId = 2L;
		
		given()
			.get("/products/{id}", existingProductId)
		.then() 
				.statusCode(200)
				.body("id", is(2))
				.body("name", equalTo("Smart TV"))
				.body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
				.body("price", is(2190.0F))
				.body("categories.id", hasItems(2,3))
				.body("categories.name", hasItems("Eletrônicos", "Computadores"));

	}
	
	@Test
	public void findAllShouldReturnPageProductWhenProductNameIsEmpty() {
		
		given()
			.get("/products?page=0")
		.then() 
				.statusCode(200)
				.body("content.name", hasItems("Macbook Pro", "PC Gamer Tera" ));
	}
	
	@Test
	public void findAllShouldReturnPageProductWhenProductNameIsNotEmpty() {
		given()
		.get("/products?name={productName}", productName)
		.then() 
			.statusCode(200)
			.body("content.id[0]", is(3))
			.body("content.name[0]", equalTo("Macbook Pro"))
			.body("content.price[0]", is(1250.0F))
			.body("content.imgUrl[0]", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
	}
	
	@Test
	public void findAllShouldReturnPagedProductsWithPriceGreaterThen2000() {
		given()
			.get("/products?size=25")
		.then()
			.statusCode(200)
			.body("content.findAll { it.price > 2000 }.name", hasItems("Smart TV", "PC Gamer Max"));
	}
	
	@Test
	public void insertShouldReturnProductCreatedWhenAdminLogged() {
		
		JSONObject newProduct = new JSONObject(postProductInstance);
		
		String adminToken = "";
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.body(newProduct)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/products")
		.then()
			.statusCode(201)
			.body("name", equalTo("Meu novo pedido"))
			.body("price", is(110.0F))
			.body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"))
			.body("description", equalTo("Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim"))
			.body("categories", hasItems(2, 3));
			
	}
}
