package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.ArrayList;
import java.util.List;

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
import edu.ncsu.csc.CoffeeMaker.models.BevOrder;
import edu.ncsu.csc.CoffeeMaker.models.Customer;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Class used to test Customer class
 *
 * @author gkote
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class CustomerTest {

    /**
     * Service object used throughout this test class as a means of interacting
     * with the database for user
     */
    @Autowired
    private UserService      userService;

    /**
     * service object used throughout this test class as a means of interacting
     * with the db for inventory
     */
    @Autowired
    private InventoryService inventoryService;

    /**
     * service object used throughout this test class as a means of interacting
     * with the db for order
     */
    @Autowired
    private OrderService     orderService;

    /**
     * service object used throughout this test class as a means of interacting
     * with the db for recipe
     */
    @Autowired
    private RecipeService    recipeService;

    /**
     * This is used to clear the user table in the database before each test
     */
    @BeforeEach
    public void setup () {
        userService.deleteAll();
        orderService.deleteAll();
        recipeService.deleteAll();

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
     * Method used to test customer initialization logic
     */
    @Test
    @Transactional
    public void testConstructor () {

        // initialize a proper user, this should not throw an exception
        assertDoesNotThrow( () -> new Customer( "gkote", "password" ) );

        // customer should not be able to have null names
        assertThrows( IllegalArgumentException.class, () -> new Customer( null, "pass" ) );

        // customer should not be able have a null password
        assertThrows( IllegalArgumentException.class, () -> new Customer( "gkote", null ) );

        // both parameters being invalid should also throw an exception
        assertThrows( IllegalArgumentException.class, () -> new Customer( null, null ) );
    }

    /**
     * Method used to test setUsername and getUsername logic
     */
    @Test
    @Transactional
    public void testSetUsername () {
        // setup
        final Customer c = new Customer( "gkote", "pass" );
        Assertions.assertEquals( c.getUsername(), "gkote" );
        userService.save( c );

        // check that the ingredient was successfully persisted
        final User c1 = userService.findByUsername( "gkote" );
        assertNotNull( c1 );
        assertEquals( "gkote", c1.getUsername() );

        // you should be able to set to a non null name
        assertDoesNotThrow( () -> c.setUsername( "gabriela" ) );
        Assertions.assertEquals( c.getUsername(), "gabriela" );

    }

    /**
     * Method used to test getRole logic
     */
    @Test
    @Transactional
    public void testGetRole () {
        // setup
        final Customer c = new Customer( "gkote", "pass" );
        Assertions.assertEquals( c.getRole(), "customer" );
    }

    /**
     * Method used to test hashPassword logic
     */
    @Test
    @Transactional
    public void testHashPassword () {
        // setup
        final Customer c = new Customer( "gkote", "pass" );
        final String newPass = c.hashPassword( "pass" );

        Assertions.assertEquals( c.getPassword(), newPass );
        userService.save( c );

        final User c1 = userService.findByUsername( "gkote" );
        assertEquals( newPass, c1.getPassword() );

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
     * Method used to test placeOrder logic
     */
    @Test
    @Transactional
    public void testPlaceOrder () {
        // setup
        final Customer c = new Customer( "gkote", "pass" );
        Assertions.assertEquals( 0, c.getOrders().size() );
        userService.save( c );

        final Recipe r1 = createRecipe( "Coffee", 50 );
        final Ingredient ingredient1 = new Ingredient( "vanilla", 1 );
        final Ingredient ingredient2 = new Ingredient( "chai", 1 );
        final Ingredient ingredient3 = new Ingredient( "oat", 1 );
        final Ingredient ingredient4 = new Ingredient( "syrup", 1 );
        r1.addIngredient( ingredient1 );
        r1.addIngredient( ingredient2 );
        r1.addIngredient( ingredient3 );
        r1.addIngredient( ingredient4 );
        recipeService.save( r1 );

        final List<Recipe> bevs = new ArrayList<Recipe>();
        bevs.add( r1 );

        final BevOrder o1 = new BevOrder( "gkote", "In Progress", bevs );
        orderService.save( o1 );

        c.placeOrder( o1 );
        Assertions.assertEquals( o1, c.getOrders().get( 0 ) );
        userService.save( c );

        final Customer c1 = (Customer) userService.findByUsername( "gkote" );
        assertEquals( 1, c1.getOrders().size() );

        final Recipe r2 = createRecipe( "Coffee", 50 );
        final Ingredient ingredient5 = new Ingredient( "vanilla", 1 );
        final Ingredient ingredient6 = new Ingredient( "chai", 1 );
        final Ingredient ingredient7 = new Ingredient( "oat", 1 );
        final Ingredient ingredient8 = new Ingredient( "syrup", 1 );
        r1.addIngredient( ingredient5 );
        r1.addIngredient( ingredient6 );
        r1.addIngredient( ingredient7 );
        r1.addIngredient( ingredient8 );
        recipeService.save( r2 );

        final List<Recipe> bevs1 = new ArrayList<Recipe>();
        bevs1.add( r2 );

        final BevOrder o2 = new BevOrder( "gkote", "Ready", bevs1 );

        orderService.save( o2 );

        assertThrows( IllegalArgumentException.class, () -> c.placeOrder( o2 ) );

    }

    /**
     * Method used to test pickUpOrder logic
     */
    @Test
    @Transactional
    public void testPickUpOrder () {
        // setup
        final Customer c = new Customer( "gkote", "pass" );
        Assertions.assertEquals( 0, c.getOrders().size() );
        userService.save( c );

        final Recipe r1 = createRecipe( "Coffee", 50 );
        final Ingredient ingredient1 = new Ingredient( "vanilla", 1 );
        final Ingredient ingredient2 = new Ingredient( "chai", 1 );
        final Ingredient ingredient3 = new Ingredient( "oat", 1 );
        final Ingredient ingredient4 = new Ingredient( "syrup", 1 );
        r1.addIngredient( ingredient1 );
        r1.addIngredient( ingredient2 );
        r1.addIngredient( ingredient3 );
        r1.addIngredient( ingredient4 );
        recipeService.save( r1 );

        final List<Recipe> bevs = new ArrayList<Recipe>();
        bevs.add( r1 );

        final BevOrder o1 = new BevOrder( "gkote", "In Progress", bevs );
        orderService.save( o1 );

        c.placeOrder( o1 );
        Assertions.assertEquals( o1, c.getOrders().get( 0 ) );
        userService.save( c );

        // picking up the order
        o1.setStatus( "Ready" );
        c.pickUpOrder( o1 );
        Assertions.assertEquals( o1.getStatus(), c.getOrders().get( 0 ).getStatus() );
        userService.save( c );

        final Customer c1 = (Customer) userService.findByUsername( "gkote" );
        assertTrue( c1.getOrders().get( 0 ).getStatus().equals( "Picked Up" ) );

        final Recipe r2 = createRecipe( "Coffee", 50 );
        final Ingredient ingredient5 = new Ingredient( "vanilla", 1 );
        final Ingredient ingredient6 = new Ingredient( "chai", 1 );
        final Ingredient ingredient7 = new Ingredient( "oat", 1 );
        final Ingredient ingredient8 = new Ingredient( "syrup", 1 );
        r1.addIngredient( ingredient5 );
        r1.addIngredient( ingredient6 );
        r1.addIngredient( ingredient7 );
        r1.addIngredient( ingredient8 );
        recipeService.save( r2 );

        final List<Recipe> bevs1 = new ArrayList<Recipe>();
        bevs1.add( r2 );

        final BevOrder o2 = new BevOrder( "gkote", "Ready", bevs1 );
        orderService.save( o2 );

        assertThrows( IllegalArgumentException.class, () -> c.pickUpOrder( o2 ) );

    }

}
