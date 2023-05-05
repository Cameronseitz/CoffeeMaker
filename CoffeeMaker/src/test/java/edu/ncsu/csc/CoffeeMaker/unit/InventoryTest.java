package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertEquals;
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
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

/**
 * Class for testing inventory persistence
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class InventoryTest {

	/**
	 * service object used throughout this test class as a means of interacting 
	 * with the db
	 */
    @Autowired
    private InventoryService inventoryService;

    /**
     * Setup method for junit
     */
    @BeforeEach
    public void setup () {
        final Inventory ivt = inventoryService.getInventory();

        final Ingredient i1 = new Ingredient( "vanilla", 500 );
        final Ingredient i2 = new Ingredient( "chai", 500 );
        final Ingredient i3 = new Ingredient( "oat", 500 );
        final Ingredient i4 = new Ingredient( "syrup", 500 );

        ivt.addIngredient( i1 );
        ivt.addIngredient( i2 );
        ivt.addIngredient( i3 );
        ivt.addIngredient( i4 );

        inventoryService.save( ivt );
    }

    /**
     * Test using ingredients from inventory with a recipe
     */
    @Test
    @Transactional
    public void testConsumeInventory () {
        final Inventory i = inventoryService.getInventory();

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );
        final Ingredient i1 = new Ingredient( "vanilla", 10 );
        final Ingredient i2 = new Ingredient( "chai", 20 );
        final Ingredient i3 = new Ingredient( "oat", 5 );
        final Ingredient i4 = new Ingredient( "syrup", 1 );

        recipe.addIngredient( i1 );
        recipe.addIngredient( i2 );
        recipe.addIngredient( i3 );
        recipe.addIngredient( i4 );
        recipe.setPrice( 5 );

        i.useIngredients( recipe );

        /*
         * Make sure that all of the inventory fields are now properly updated
         */
        Assertions.assertEquals( 490, (int) i.getIngredientByName( "vanilla" ).getAmount() );
        Assertions.assertEquals( 480, (int) i.getIngredientByName( "chai" ).getAmount() );
        Assertions.assertEquals( 495, (int) i.getIngredientByName( "oat" ).getAmount() );
        Assertions.assertEquals( 499, (int) i.getIngredientByName( "syrup" ).getAmount() );
    }

    /**
     * Test adding valid ingredients
     */
    @Test
    @Transactional
    public void testAddInventory1 () {
        Inventory ivt = inventoryService.getInventory();

        final Ingredient i1 = new Ingredient( "vanilla", 500 );
        final Ingredient i2 = new Ingredient( "chai", 500 );
        final Ingredient i3 = new Ingredient( "oat", 500 );
        final Ingredient i4 = new Ingredient( "syrup", 500 );

        ivt.addIngredient( i1 );
        ivt.addIngredient( i2 );
        ivt.addIngredient( i3 );
        ivt.addIngredient( i4 );

        /* Save and retrieve again to update with DB */
        inventoryService.save( ivt );

        ivt = inventoryService.getInventory();

        Assertions.assertEquals( 1000, (int) ivt.getIngredientByName( "vanilla" ).getAmount(),
                "Adding to the inventory should result in correctly-updated values for vanilla" );
        Assertions.assertEquals( 1000, (int) ivt.getIngredientByName( "vanilla" ).getAmount(),
                "Adding to the inventory should result in correctly-updated values for chai" );
        Assertions.assertEquals( 1000, (int) ivt.getIngredientByName( "vanilla" ).getAmount(),
                "Adding to the inventory should result in correctly-updated values oat" );
        Assertions.assertEquals( 1000, (int) ivt.getIngredientByName( "vanilla" ).getAmount(),
                "Adding to the inventory should result in correctly-updated values syrup" );

    }

    /**
     * Test adding negative ingredient
     */
    @Test
    @Transactional
    public void testAddInventory2 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            final Ingredient i4 = new Ingredient( "syrup", -1 );

            ivt.addIngredient( i4 );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.getIngredientByName( "vanilla" ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values for vanilla" );
            Assertions.assertEquals( 500, (int) ivt.getIngredientByName( "vanilla" ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values for chai" );
            Assertions.assertEquals( 500, (int) ivt.getIngredientByName( "vanilla" ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values oat" );
            Assertions.assertEquals( 500, (int) ivt.getIngredientByName( "vanilla" ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values syrup" );
        }
    }

    /**
     * Test checkAmount method
     */
    @Test
    @Transactional
    public void testCheckAmount () {
        final Inventory ivt = inventoryService.getInventory();
        final Exception e1 = assertThrows( IllegalArgumentException.class, () -> ivt.checkAmount( "not a number" ) );
        assertEquals( "Units of ingredient must be a positive integer", e1.getMessage() );

        final Exception e2 = assertThrows( IllegalArgumentException.class, () -> ivt.checkAmount( "-1" ) );
        assertEquals( "Units of ingredient must be a positive integer", e2.getMessage() );

        final Integer amountOfUnit = assertDoesNotThrow( () -> ivt.checkAmount( "1" ) );
        assertEquals( 1, (int) amountOfUnit );
    }

    /**
     * Test False Use Inventory
     */
    @Test
    @Transactional
    public void testUseInventoryFalse () {

        final Inventory ivt = inventoryService.getInventory();

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );
        final Ingredient i1 = new Ingredient( "vanilla", 500 );
        final Ingredient i2 = new Ingredient( "chai", 500 );
        final Ingredient i3 = new Ingredient( "oat", 500 );
        final Ingredient i4 = new Ingredient( "syrup", 500 );

        recipe.addIngredient( i1 );
        recipe.addIngredient( i2 );
        recipe.addIngredient( i3 );
        recipe.addIngredient( i4 );
        recipe.setPrice( 5 );

        ivt.useIngredients( recipe );

        inventoryService.save( ivt );

        final Inventory i = inventoryService.getInventory();

        Assertions.assertFalse( i.useIngredients( recipe ) );

    }

}
