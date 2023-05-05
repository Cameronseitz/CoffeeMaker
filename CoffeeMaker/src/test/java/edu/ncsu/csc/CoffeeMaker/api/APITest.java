package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

/**
 * Tests get inventory and recipe objects through routes
 *
 * @author Team 1
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc

public class APITest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    /**
     * web context
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * service object used throughout this test class as a means of interacting
     * with the db
     */
    @Autowired
    private InventoryService      iService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Tests a recipe can take ingredients and be posted and gotten through api
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void APIRecipeTest () throws Exception {
        final String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        if ( !recipe.contains( "Mocha" ) ) {
            final Recipe r = new Recipe();
            final Ingredient chocolate = new Ingredient( "Chocolate", 5 );
            final Ingredient coffee = new Ingredient( "Coffee", 3 );
            final Ingredient milk = new Ingredient( "Milk", 4 );
            final Ingredient sugar = new Ingredient( "Sugar", 8 );
            r.addIngredient( chocolate );
            r.addIngredient( coffee );
            r.addIngredient( milk );
            r.addIngredient( sugar );
            r.setPrice( 10 );
            r.setName( "Mocha" );
            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );
        }
        final String recipe2 = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( recipe2.contains( "Mocha" ) );

        // update inventory
        final Inventory inventoryToUpdate = iService.getInventory();
        final Ingredient ichocolate = new Ingredient( "Chocolate", 50 );
        final Ingredient icoffee = new Ingredient( "Coffee", 50 );
        final Ingredient imilk = new Ingredient( "Milk", 50 );
        final Ingredient isugar = new Ingredient( "Sugar", 50 );
        inventoryToUpdate.addIngredient( ichocolate );
        inventoryToUpdate.addIngredient( icoffee );
        inventoryToUpdate.addIngredient( imilk );
        inventoryToUpdate.addIngredient( isugar );
        // inventoryToUpdate.addIngredients( 50, 50, 50, 50 );
        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( inventoryToUpdate ) ) ).andExpect( status().isOk() );

        // make coffee
        mvc.perform( post( "/api/v1/makecoffee/Mocha" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 10 ) ) ).andExpect( status().isOk() );

    }

    /**
     * tests that an ingredient gets added to inventory
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testGetInventoryAPI () throws Exception {
        final Inventory inventoryToUpdate = iService.getInventory();
        final Ingredient ichocolate = new Ingredient( "Chocolate", 50 );
        inventoryToUpdate.addIngredient( ichocolate );

        final String inventory = mvc.perform( get( "/api/v1/inventory" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        assertTrue( inventory.contains( "Chocolate" ) );
    }

}
