package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

/**
 * Class used to test Ingredient class
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class IngredientTest {

	/**
	 * service object used throughout this test class as a means of interacting 
	 * with the db
	 */
    @Autowired
    private IngredientService ingredientService;
    
    /**
     * used to clear the ingredient table in the db before each test
     */
    @BeforeEach
    public void setup () {
        ingredientService.deleteAll();
    }

    /**
     * Method used to test ingredient initialization logic
     */
    @Test
    public void testConstructor() {
    	
    	//initialize a proper ingredient, this should not throw an exception
    	assertDoesNotThrow(() -> new Ingredient("sugar", 1));
    	
    	//ingredients should not be able to have null names
    	assertThrows(IllegalArgumentException.class, () -> new Ingredient(null, 1));
    	
    	//ingredients should not be able to have negative amounts or null
    	assertThrows(IllegalArgumentException.class, () -> new Ingredient("sugar", -1));
    	assertThrows(IllegalArgumentException.class, () -> new Ingredient("sugar", null));

    	//an ingredient's amount can be 0
    	assertDoesNotThrow(() -> new Ingredient("sugar", 0));
    	
    	//both parameters being invalid should also throw an exception
    	assertThrows(IllegalArgumentException.class, () -> new Ingredient(null, -1));
    }
    
    /**
     * Method used to test setName logic
     */
    @Test
    public void testSetName() {
    	//setup
    	Ingredient ingredient = new Ingredient("milk", 5);
    	
    	//you should be able to set to a non null name
    	assertDoesNotThrow(() -> ingredient.setName("hi"));
    	
    	//you should not be able to set to a null name
    	assertThrows(IllegalArgumentException.class, () -> ingredient.setName(null));
    }

    /**
     * Method used to test setAmount logic
     */
    @Test
    public void testSetAmount() {
    	//setup
    	Ingredient ingredient = new Ingredient("milk", 5);
    	
    	//setting amount to 0 or positive number should work
    	assertDoesNotThrow(() -> ingredient.setAmount(0));
    	assertDoesNotThrow(() -> ingredient.setAmount(1));
    	
    	//setting amount to null or a negative number should not work
    	assertThrows(IllegalArgumentException.class, () -> ingredient.setAmount(null));
    	assertThrows(IllegalArgumentException.class, () -> ingredient.setAmount(-1));
    }
    
    /**
     * Test that ingredients are persisted correctly
     */
    @Test
    @Transactional
    public void testCreateIngredient() {
    	//create ingredient and save it to the db
    	String ingredientName = "chocolate syrup";
    	Integer ingredientAmount = 5;
    	
    	Ingredient ingredient = new Ingredient(ingredientName, ingredientAmount);
    	ingredientService.save(ingredient);
    	
    	//check that the ingredient was successfully persisted
    	Ingredient persistedIngredient = ingredientService.findByName(ingredientName);
    	assertNotNull(persistedIngredient);
    	assertEquals(ingredientName, ingredient.getName());
    	assertEquals(ingredientAmount, ingredient.getAmount());
    }
    
    /**
     * Test that ingredients are updated correctly in db
     */
    @Test
    @Transactional
    public void testEditIngredient () {
    	
    	final Ingredient ingredient1 = new Ingredient("sugar", 1);
        ingredientService.save( ingredient1 );
        
        final Ingredient retrieved = ingredientService.findByName( "sugar" );

        Assertions.assertEquals( 1, (int) retrieved.getAmount() );
        Assertions.assertEquals( "sugar", retrieved.getName() );

        Assertions.assertEquals( 1, ingredientService.count(), "Editing an ingredient shouldn't duplicate it" );

    }

    /**
     * Tests the Delete use case for one recipe out of a list
     */
    @Test
    @Transactional
    public void testDeleteRecipe () {

        // delete one from list
        final Ingredient ingredient1 = new Ingredient("sugar", 1);
        final Ingredient ingredient2 = new Ingredient("cream", 1);
        ingredientService.save( ingredient1 );
        ingredientService.save( ingredient2 );

        ingredientService.delete( ingredient1 );
        assertEquals( 1, ingredientService.findAll().size() );
        assertEquals( "cream", ingredientService.findAll().get( 0 ).getName() );
        
    }
}
