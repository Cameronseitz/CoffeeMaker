package edu.ncsu.csc.CoffeeMaker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class TestDatabaseInteraction {
	
	/**
	 * service object used throughout this test class as a means of interacting 
	 * with the db
	 */
    @Autowired
    private RecipeService recipeService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        recipeService.deleteAll();
    }

    /**
     * Tests the RecipeService class
     */
    @Test
    @Transactional
    public void testRecipes () {
        final Recipe r = new Recipe();
        r.setName( "Black Coffee" );
        r.setPrice( 1 );
        final Ingredient i = new Ingredient( "vanilla", 1 );
        r.addIngredient( i );
        recipeService.save( r );

        final List<Recipe> dbRecipes = recipeService.findAll();

        assertEquals( 1, dbRecipes.size() );

        final Recipe dbRecipe = dbRecipes.get( 0 );

        assertEquals( r.getName(), dbRecipe.getName() );
        assertEquals( r.getPrice(), dbRecipe.getPrice() );

        final Recipe r2 = recipeService.findByName( "Black Coffee" );

        assertEquals( r2.getName(), dbRecipe.getName() );
        assertEquals( r2.getPrice(), dbRecipe.getPrice() );
        assertEquals( r2.getIngredients().size(), 1 );

        // edit the fields of the single recipe object
        dbRecipe.setPrice( 15 );
        dbRecipe.addIngredient( new Ingredient( "chai", 3 ) );
        recipeService.save( dbRecipe );

        // assert that there is still only one record in the db
        final List<Recipe> recipeList = recipeService.findAll();
        assertEquals( 1, recipeList.size() );

        // check that the correct fields actually changed (sugar and price)
        assertEquals( 15, (int) recipeList.get( 0 ).getPrice() );
        assertEquals( 2, recipeList.get( 0 ).getIngredients().size() );
    }

    /**
     * Tests the Delete use case
     */
    @Test
    @Transactional
    public void TestDelete () {

        // delete one
        final Recipe r = new Recipe();
        r.setName( "Black Coffee" );
        r.setPrice( 1 );
        recipeService.save( r );

        recipeService.delete( r );
        final List<Recipe> dbRecipes = recipeService.findAll();

        assertEquals( 0, dbRecipes.size() );

        // delete all
        final Recipe r2 = new Recipe();
        r2.setName( "Black Coffee" );
        r2.setPrice( 1 );
        recipeService.save( r2 );

        final Recipe r3 = new Recipe();
        r3.setName( "Black" );
        r3.setPrice( 1 );
        recipeService.save( r3 );

        recipeService.deleteAll();
        assertEquals( 0, dbRecipes.size() );

    }

    /**
     * Tests the service's findById method
     */
    @Test
    @Transactional
    public void TestFindRecipeById () {

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );
        recipeService.save( r1 );

        // passing in null for id should return null
        assertNull( recipeService.findById( null ) );

        // finding a recipe id that doesn't exist returns null
        final long badID = -1;
        assertNull( recipeService.findById( badID ) );

        // get id and find recipe by same id
        final Long insertedItemID = recipeService.findAll().get( 0 ).getId();
        assertEquals( r1, recipeService.findById( insertedItemID ) );

    }

}
