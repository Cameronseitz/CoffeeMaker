package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

/**
 * Tests the api routes relating to the ingredient functionality
 *
 * @author Team 1
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIIngredientTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    /**
     * context
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * service object used throughout this test class as a means of interacting
     * with the db
     */
    @Autowired
    private IngredientService     service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    @Test
    @Transactional
    public void ensureIngredient () throws Exception {
        final Ingredient chocolate = new Ingredient( "Chocolate", 5 );
        final Ingredient coffee = new Ingredient( "Coffee", 3 );
        final Ingredient milk = new Ingredient( "Milk", 4 );
        final Ingredient sugar = new Ingredient( "Sugar", 8 );

        service.deleteAll();

        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( chocolate ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( coffee ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( milk ) ) ).andExpect( status().isOk() );
    }

    @Test
    @Transactional
    public void testIngredientAPI () throws Exception {
        final Ingredient chocolate = new Ingredient( "Chocolate", 10 );
        final Ingredient coffee = new Ingredient( "Coffee", 20 );
        final Ingredient milk = new Ingredient( "Milk", 5 );

        service.deleteAll();

        // test that ingredients get posted successfully
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( chocolate ) ) );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( coffee ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( milk ) ) ).andExpect( status().isOk() );

        // there should be 3 ingredients now
        Assertions.assertEquals( 3, (int) service.count() );

        // test getting with the api
        mvc.perform( get( "/api/v1/ingredients/Milk" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( jsonPath( "$.name" ).value( "Milk" ) );

    }

    @Test
    @Transactional
    public void testAddIngredient2 () throws Exception {

        /*
         * Tests a ingredient with a duplicate name to make sure it's rejected
         */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Ingredients in the CoffeeMaker" );
        final Ingredient i = createIngredient( "Vanilla", 4 );
        //
        // service.save( r1 );
        //
        // final Ingredient r2 = createRecipe( name, 50, 3, 1, 1, 0 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Ingredients in the CoffeeMaker" );
        final Ingredient i1 = createIngredient( "Vanilla", 2 );
        final Ingredient i2 = createIngredient( "Hazelnut", 2 );
        final Ingredient i3 = createIngredient( "Sweeteners", 4 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i1 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i3 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 4, service.findAll().size() );
    }

    @Test
    @Transactional
    public void testAddIngredient15 () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Ingredients in the CoffeeMaker" );
        final Ingredient chocolate = new Ingredient( "Chocolate", 5 );
        final Ingredient coffee = new Ingredient( "Coffee", 3 );
        final Ingredient milk = new Ingredient( "Milk", 4 );
        final Ingredient sugar = new Ingredient( "Sugar", 8 );
        service.save( chocolate );
        service.save( coffee );
        service.save( milk );
        service.save( sugar );

        Assertions.assertEquals( 4, service.count(),
                "Creating three recipes should result in three recipes in the database" );

        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( chocolate ) ) ).andExpect( status().isOk() );

        // Assertions.assertEquals( 3, service.count(), "Creating a fourth
        // recipe should not get saved" );
    }

    private Ingredient createIngredient ( final String name, final Integer amt ) {
        final Ingredient newIngredient = new Ingredient( name, amt );
        return newIngredient;
    }

    @Test
    @Transactional
    public void testDeleteIngredient () throws Exception {
        final Ingredient chocolate = new Ingredient( "Chocolate", 10 );
        final Ingredient vanilla = new Ingredient( "Vanilla", 4 );
        final Ingredient milk = new Ingredient( "Milk", 5 );
        final Ingredient sugar = new Ingredient( "Sugar", 1 );
        service.deleteAll();
        //
        // final Recipe recipe = new Recipe();
        // recipe.setName( "Coffee2" );
        // recipe.addIngredient( chocolate );
        // recipe.addIngredient( coffee );
        // recipe.addIngredient( milk );
        // recipe.addIngredient( sugar );

        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( vanilla ) ) ).andExpect( status().isOk() );
        // service.delete( chocolate );
        mvc.perform( delete( "/api/v1/ingredients/Vanilla" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( vanilla ) ) ).andExpect( status().isOk() );

        mvc.perform( delete( "/api/v1/ingredients/Vanilla" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( null ) ) ).andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 0, service.findAll().size() );

    }

}
