package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
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
import edu.ncsu.csc.CoffeeMaker.models.Employee;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Class used to test Employee class
 *
 * @author gkote
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class EmployeeTest {
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
     * Method used to test user initialization logic
     */
    @Test
    @Transactional
    public void testConstructor () {

        // initialize a proper user, this should not throw an exception
        assertDoesNotThrow( () -> new Employee( "user1", "password1" ) );

        // cannot have null username
        assertThrows( IllegalArgumentException.class, () -> new Employee( null, "password2" ) );

        // cannot have null password
        assertThrows( IllegalArgumentException.class, () -> new Employee( "user3", null ) );

        // role must be employee
        final Employee e1 = new Employee( "user4", "password4" );
        Assertions.assertEquals( "employee", e1.getRole() );

        // username and password are returned correctly
        Assertions.assertEquals( "user4", e1.getUsername() );
        final String hashedPassword = e1.hashPassword( "password4" );
        Assertions.assertEquals( hashedPassword, e1.getPassword() );
    }

    /**
     * Test that employee are persisted correctly
     */
    @Test
    @Transactional
    public void testCreateEmployee () {
        final Employee e1 = new Employee( "user1", "password1" );

        userService.save( e1 );

        // check that the user was successfully persisted
        final User persistedEmployee = userService.findByUsername( "user1" );
        Assertions.assertNotNull( persistedEmployee );
        Assertions.assertEquals( "user1", persistedEmployee.getUsername() );

        final String hashedPassword = e1.hashPassword( "password1" );
        Assertions.assertEquals( hashedPassword, persistedEmployee.getPassword() );
        Assertions.assertEquals( "employee", persistedEmployee.getRole() );
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
     * Testing for fulfillOrder
     */
    @Test
    @Transactional
    public void testFulfillOrder () {
        // setup
        final Employee e = new Employee( "gkote", "pass" );
        userService.save( e );

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

        final BevOrder given = e.fulfillOrder( o1 );
        assertEquals( "Ready", given.getStatus() );
        orderService.save( o1 );

        final BevOrder c1 = orderService.findAll().get( 0 );
        assertEquals( "Ready", c1.getStatus() );

    }

    /**
     * Test for cancelOrder
     */
    @Test
    @Transactional
    public void testCancelOrder () {
        // setup
        final Employee e = new Employee( "gkote", "pass" );
        userService.save( e );

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

        final BevOrder given = e.cancelOrder( o1 );
        assertEquals( "Cancelled", given.getStatus() );
        orderService.save( o1 );

        final BevOrder c1 = orderService.findAll().get( 0 );
        assertEquals( "Cancelled", c1.getStatus() );
    }

}
