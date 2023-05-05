package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Tests the recipe model
 *
 * @author Team 1
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class RecipeTest {

    /**
     * service object used throughout this test class as a means of interacting
     * with the db
     */
    @Autowired
    private RecipeService service;

    /**
     * Delete at recipes at beginning of each test
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    /**
     * successfully add two recipes to service
     */
    @Test
    @Transactional
    public void testAddRecipe () {

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        service.save( r2 );

        final List<Recipe> recipes = service.findAll();
        Assertions.assertEquals( 2, recipes.size(),
                "Creating two recipes should result in two recipes in the database" );

        Assertions.assertEquals( r1, recipes.get( 0 ), "The retrieved recipe should match the created one" );
    }

    /**
     * Try adding a recipe with negative price unsuccessfully.
     */
    @Test
    @Transactional
    public void testNoRecipes () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        r1.setPrice( -12 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );

        final List<Recipe> recipes = List.of( r1, r2 );

        try {
            service.saveAll( recipes );
            Assertions.assertEquals( 0, service.count(),
                    "Trying to save a collection of elements where one is invalid should result in neither getting saved" );
        }
        catch ( final Exception e ) {
            Assertions.assertTrue( e instanceof ConstraintViolationException );
        }

    }

    /**
     * Tests adding one recipe to coffee maker
     */
    @Test
    @Transactional
    public void testAddRecipe1 () {

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50 );

        service.save( r1 );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
        Assertions.assertNotNull( service.findByName( name ) );

    }

    /**
     * Test2 is done via the API for different validation
     */
    @Test
    @Transactional
    public void testAddRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, -50 );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative price" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    /**
     * Tests creating two recipes and saving them
     */
    @Test
    @Transactional
    public void testAddRecipe13 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50 );
        service.save( r2 );

        Assertions.assertEquals( 2, service.count(),
                "Creating two recipes should result in two recipes in the database" );

    }

    /**
     * Create three recipes
     */
    @Test
    @Transactional
    public void testAddRecipe14 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

    }

    /**
     * Tests deleting one recipe from service.
     */
    @Test
    @Transactional
    public void testDeleteRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50 );
        service.save( r1 );

        Assertions.assertEquals( 1, service.count(), "There should be one recipe in the database" );

        service.delete( r1 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
    }

    /**
     * Tests deleting all recipes in the service
     */
    @Test
    @Transactional
    public void testDeleteRecipe2 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        service.deleteAll();

        Assertions.assertEquals( 0, service.count(), "`service.deleteAll()` should remove everything" );

    }

    /**
     * Tests updating the service after saving it and retrieving it
     */
    @Test
    @Transactional
    public void testUpdateRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50 );
        final Recipe r2 = createRecipe( "For the Ingredients", 70 );
        r2.addIngredient( new Ingredient( "vanilla", 2 ) );
        service.save( r1 );

        r1.updateRecipe( r2 );

        service.save( r1 );

        final Recipe retrieved = service.findByName( "Coffee" );

        Assertions.assertEquals( 70, (int) retrieved.getPrice() );
        Assertions.assertEquals( 1, retrieved.getIngredients().size() );

        Assertions.assertEquals( 1, service.count(), "Updating a recipe shouldn't duplicate it" );

    }

    /**
     * Tests updating a recipe without duplicating it.
     */
    @Test
    @Transactional
    public void testUpdateRecipe2 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50 );
        final Recipe r2 = createRecipe( "For the Ingredients", 70 );
        r1.addIngredient( new Ingredient( "vanilla", 2 ) );
        r2.addIngredient( new Ingredient( "vanilla", 3 ) );
        r2.addIngredient( new Ingredient( "chai", 2 ) );
        service.save( r1 );

        final Recipe retrieved = service.findByName( "Coffee" );

        retrieved.updateRecipe( r2 );
        service.save( retrieved );

        Assertions.assertEquals( 70, (int) retrieved.getPrice() );
        Assertions.assertEquals( 2, retrieved.getIngredients().size() );
        Assertions.assertEquals( 3, retrieved.getIngredientByName( "vanilla" ).getAmount() );

        Assertions.assertEquals( 1, service.count(), "Updating a recipe shouldn't duplicate it" );

    }

    /**
     * Tests that changing the fields of a recipe doesn't duplicate it
     */
    @Test
    @Transactional
    public void testEditRecipe1 () {

        final Recipe r1 = createRecipe( "Coffee", 50 );

        service.save( r1 );

        r1.addIngredient( new Ingredient( "vanilla", 2 ) );

        service.save( r1 );

        final Recipe retrieved = service.findByName( "Coffee" );

        Assertions.assertEquals( 50, (int) retrieved.getPrice() );
        Assertions.assertEquals( 1, retrieved.getIngredients().size() );

        Assertions.assertEquals( 1, service.count(), "Editing a recipe shouldn't duplicate it" );

    }

    /**
     * Helper method to create a recipe with name and price
     *
     * @param name
     *            name of new recipe
     * @param price
     *            price of new recipe
     * @return recipe
     */
    private Recipe createRecipe ( final String name, final Integer price ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );

        return recipe;
    }

    /**
     * Tests the Delete use case for one recipe out of a list
     */
    @Test
    @Transactional
    public void testDeleteSingleRecipe () {

        // delete one from list
        final Recipe r2 = new Recipe();
        r2.setName( "Black Coffee" );
        r2.setPrice( 1 );
        service.save( r2 );

        final Recipe r3 = new Recipe();
        r3.setName( "Black" );
        r3.setPrice( 1 );
        service.save( r3 );

        service.delete( r2 );
        assertEquals( 1, service.findAll().size() );
        assertEquals( "Black", service.findAll().get( 0 ).getName() );

    }

    /**
     * Tests Recipe's Equals method
     */
    @Test
    @Transactional
    public void testRecipeEquals () {

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );
        service.save( r1 );

        final Recipe diffName = new Recipe();
        diffName.setName( "Different Name" );
        diffName.setPrice( 1 );
        service.save( diffName );

        final Recipe sameName = new Recipe();
        sameName.setName( "Black Coffee" );
        sameName.setPrice( 3 );
        service.save( sameName );

        assertFalse( r1.equals( null ) );

        assertFalse( r1.equals( service ) );

        assertFalse( diffName.equals( r1 ) );

        assertTrue( sameName.equals( r1 ) );

        // have one name be null
        sameName.setName( null );
        assertFalse( sameName.equals( r1 ) );

        // have both names be null
        diffName.setName( null );
        assertTrue( sameName.equals( diffName ) );

    }

}
