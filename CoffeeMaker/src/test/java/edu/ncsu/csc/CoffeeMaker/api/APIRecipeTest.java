package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Tests the api routes for recipe
 *
 * @author Team 1
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIRecipeTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    /**
     * webcontext
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * service object used throughout this test class as a means of interacting
     * with the db
     */
    @Autowired
    private RecipeService         service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    /**
     * Creates a new recipe and uses post
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void ensureRecipe () throws Exception {
        final Ingredient chocolate = new Ingredient( "Chocolate", 5 );
        final Ingredient coffee = new Ingredient( "Coffee", 3 );
        final Ingredient milk = new Ingredient( "Milk", 4 );
        final Ingredient sugar = new Ingredient( "Sugar", 8 );

        service.deleteAll();

        final Recipe r = new Recipe();
        r.addIngredient( chocolate );
        r.addIngredient( coffee );
        r.addIngredient( milk );
        r.addIngredient( sugar );
        r.setPrice( 10 );
        r.setName( "Mocha" );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

    }

    /**
     * Uses post to create a recipe and makes sure it is saved to repository
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testRecipeAPI () throws Exception {
        final Ingredient chocolate = new Ingredient( "Chocolate", 10 );
        final Ingredient coffee = new Ingredient( "Coffee", 20 );
        final Ingredient milk = new Ingredient( "Milk", 5 );
        final Ingredient sugar = new Ingredient( "Sugar", 1 );

        service.deleteAll();

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );
        recipe.addIngredient( chocolate );
        recipe.addIngredient( coffee );
        recipe.addIngredient( milk );
        recipe.addIngredient( sugar );

        recipe.setPrice( 5 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) );

        Assertions.assertEquals( 1, (int) service.count() );

    }

    /**
     * Tests using the get command with recipe
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testgetRecipe () throws Exception {
        final Ingredient chocolate = new Ingredient( "Chocolate", 10 );
        final Ingredient coffee = new Ingredient( "Coffee", 20 );
        final Ingredient milk = new Ingredient( "Milk", 5 );
        final Ingredient sugar = new Ingredient( "Sugar", 1 );

        service.deleteAll();

        final Recipe recipe = new Recipe();
        recipe.setName( "Not-Coffee" );
        recipe.addIngredient( chocolate );
        recipe.addIngredient( coffee );
        recipe.addIngredient( milk );
        recipe.addIngredient( sugar );

        recipe.setPrice( 5 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) );

        Assertions.assertEquals( 1, (int) service.count() );

        final String sameRecipe = mvc.perform( get( "/api/v1/recipes/Not-Coffee" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( sameRecipe.contains( "Chocolate" ) );

        mvc.perform( get( "/api/v1/recipes/Coffee" ) ).andExpect( status().isNotFound() ).andReturn().getResponse()
                .getContentAsString();

    }

    /**
     * Tests a recipe with a duplicate name to make sure it's rejected
     */
    @Test
    @Transactional
    public void testAddRecipe2 () throws Exception {

        /* Tests a recipe with a duplicate name to make sure it's rejected */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 0 );

        service.save( r1 );

        final Recipe r2 = createRecipe( name, 50, 3, 1, 1, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
    }

    /** Tests to make sure that our cap of 3 recipes is enforced */
    @Test
    @Transactional
    public void testAddRecipe15 () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

        final Recipe r4 = createRecipe( "Hot Chocolate", 75, 0, 2, 1, 2 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r4 ) ) ).andExpect( status().isInsufficientStorage() );

        Assertions.assertEquals( 3, service.count(), "Creating a fourth recipe should not get saved" );
    }

    private Recipe createRecipe ( final String name, final Integer price, final Integer coffee, final Integer milk,
            final Integer sugar, final Integer chocolate ) {
        final Ingredient ichocolate = new Ingredient( "Chocolate", chocolate );
        final Ingredient icoffee = new Ingredient( "Coffee", coffee );
        final Ingredient imilk = new Ingredient( "Milk", milk );
        final Ingredient isugar = new Ingredient( "Sugar", sugar );
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );
        recipe.addIngredient( ichocolate );
        recipe.addIngredient( icoffee );
        recipe.addIngredient( imilk );
        recipe.addIngredient( isugar );

        return recipe;
    }

    /**
     * Tests a recipe that has no ingredients that is fails
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testNoIngredients () throws Exception {
        service.deleteAll();

        final Recipe recipe = new Recipe();
        recipe.setName( "Coffee2" );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) ).andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 0, service.findAll().size(), "There should no recipe in the CoffeeMaker" );

    }

    /**
     * Tests that a recipe can be deleted
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testDeleteRecipe () throws Exception {
        final Ingredient chocolate = new Ingredient( "Chocolate", 10 );
        final Ingredient coffee = new Ingredient( "Coffee", 20 );
        final Ingredient milk = new Ingredient( "Milk", 5 );
        final Ingredient sugar = new Ingredient( "Sugar", 1 );
        service.deleteAll();

        final Recipe recipe = new Recipe();
        recipe.setName( "Coffee2" );
        recipe.addIngredient( chocolate );
        recipe.addIngredient( coffee );
        recipe.addIngredient( milk );
        recipe.addIngredient( sugar );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) ).andExpect( status().isOk() );

        mvc.perform( delete( "/api/v1/recipes/Coffee2" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 0, service.findAll().size(), "There should no recipe in the CoffeeMaker" );

    }

    /**
     * Tests that a recipe can be created and found
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testNotDeleteRecipe () throws Exception {
        final Ingredient chocolate = new Ingredient( "Chocolate", 10 );
        final Ingredient coffee = new Ingredient( "Coffee", 20 );
        final Ingredient milk = new Ingredient( "Milk", 5 );
        final Ingredient sugar = new Ingredient( "Sugar", 1 );

        final Recipe recipe = new Recipe();
        recipe.setName( "Coffee2" );
        recipe.addIngredient( chocolate );
        recipe.addIngredient( coffee );
        recipe.addIngredient( milk );
        recipe.addIngredient( sugar );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should no recipe in the CoffeeMaker" );

    }

    /**
     * Tests to make sure a recipe can be updated via the put command
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testUpdateRecipe () throws Exception {
        final Recipe r1 = createRecipe( "name1", 5, 5, 5, 5, 5 );
        final Recipe r2 = createRecipe( "name2", 6, 6, 6, 6, 6 );

        // create r1
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );

        // update r1 with r2
        mvc.perform( put( "/api/v1/recipes/name1" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should 1 recipe in the CoffeeMaker" );

    }

    /**
     * Tests to make sure a recipe that can't be found cannot be updated via the
     * put command
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testDoNotUpdateRecipe () throws Exception {
        final Recipe r1 = createRecipe( "name1", 5, 5, 5, 5, 5 );
        final Recipe r2 = createRecipe( "name2", 6, 6, 6, 6, 6 );

        // create r1
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );

        // update a nonexistent recipe
        mvc.perform( put( "/api/v1/recipes/name3" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().isNotFound() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should 1 recipe in the CoffeeMaker" );

    }

}
