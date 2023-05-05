package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

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
import edu.ncsu.csc.CoffeeMaker.models.BevOrder;
import edu.ncsu.csc.CoffeeMaker.models.Customer;
import edu.ncsu.csc.CoffeeMaker.models.Employee;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Tests APIOrderController
 *
 * @author Natasha Benson (ngbenso2)
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIOrderTest {

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
    private OrderService          orderService;

    /**
     * service object used throughout this test class as a means of interacting
     * with the db
     */
    @Autowired
    private UserService           userService;

    /**
     * service object used throughout this test class as a means of interacting
     * with the db
     */
    @Autowired
    private InventoryService      inventoryService;

    /**
     * service object used throughout this test class as a means of interacting
     * with the db
     */
    @Autowired
    private RecipeService         recipeService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        orderService.deleteAll();
        final Inventory i = new Inventory();
        i.addIngredient( new Ingredient( "Chocolate", 10 ) );
        i.addIngredient( new Ingredient( "Sugar", 10 ) );
        i.addIngredient( new Ingredient( "Milk", 10 ) );
        inventoryService.save( i );
        final Customer c1 = new Customer( "dragonslayer", "securePassword1!" );
        userService.save( c1 );
        final Customer c2 = new Customer( "coffeelover", "securePassword1!" );
        userService.save( c2 );
        final Employee e1 = new Employee( "favoriteEmployee", "Password2!" );
        userService.save( e1 );
        final Recipe r1 = new Recipe();
        r1.setName( "Mocha" );
        r1.setPrice( 3 );
        r1.addIngredient( new Ingredient( "Chocolate", 5 ) );
        r1.addIngredient( new Ingredient( "Sugar", 5 ) );
        recipeService.save( r1 );
        final Recipe r2 = new Recipe();
        r2.setName( "Latte" );
        r2.setPrice( 3 );
        r2.addIngredient( new Ingredient( "Milk", 5 ) );
        r2.addIngredient( new Ingredient( "Sugar", 5 ) );
        recipeService.save( r2 );
        final Recipe r3 = new Recipe();
        r3.setName( "Hot Chocolate" );
        r3.setPrice( 3 );
        r3.addIngredient( new Ingredient( "Chocolate", 2 ) );
        r3.addIngredient( new Ingredient( "Milk", 2 ) );
        recipeService.save( r3 );
    }

    /**
     * Tests adding orders to database
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testAddOrders () throws Exception {
        // Get recipes from repository
        List<Recipe> beverages = recipeService.findAll();
        final BevOrder o1 = new BevOrder( "dragonslayer", "In Progress", beverages );

        final String response = mvc
                .perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( o1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertEquals( 1, (int) orderService.count() );
        final List<BevOrder> orders = orderService.findAll();
        Assertions.assertEquals( 3, orders.get( 0 ).getBeverages().size() );
        Assertions.assertTrue( response.contains( "Order placed" ) );

        beverages = new ArrayList<Recipe>();
        final BevOrder o2 = new BevOrder( "dragonslayer", "In Progress", beverages );
        final String response2 = mvc
                .perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( o2 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertEquals( 2, (int) orderService.count() );
        Assertions.assertTrue( response2.contains( "Order placed" ) );
    }

    /**
     * Gets all orders from database
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testEditOrders () throws Exception {
        // Get recipes from repository
        final List<Recipe> recipes = recipeService.findAll();
        List<Recipe> beverages = new ArrayList<Recipe>();
        beverages.add( recipes.get( 0 ) );
        beverages.add( recipes.get( 1 ) );
        final BevOrder o1 = new BevOrder( "dragonslayer", "In Progress", beverages );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o1 ) ) ).andExpect( status().isOk() );

        final BevOrder o2 = new BevOrder( "coffeelover", "In Progress", beverages );
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o2 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 2, (int) orderService.count() );

        // Selects order to change from "In Progress" to "Ready"
        List<BevOrder> orders = orderService.findAll();
        Assertions.assertEquals( "In Progress", orders.get( 0 ).getStatus() );
        orders.get( 0 ).setStatus( "Ready" );

        // Make order, use inventory, and change status
        mvc.perform( put( String.format( "/api/v1/orders/%d", orders.get( 0 ).getId() ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( orders.get( 0 ) ) ) )
                .andExpect( status().isOk() );
        orders = orderService.findAll();

        // Check status and inventory changes
        Assertions.assertEquals( "Ready", orders.get( 0 ).getStatus() );
        List<Ingredient> i = inventoryService.findAll().get( 0 ).getIngredients();
        Assertions.assertEquals( 5, i.get( 0 ).getAmount() );
        Assertions.assertEquals( 0, i.get( 1 ).getAmount() );
        Assertions.assertEquals( 5, i.get( 2 ).getAmount() );

        // Make another order, should be cancelled and no changes to inventory
        Assertions.assertEquals( "In Progress", orders.get( 1 ).getStatus() );
        orders.get( 1 ).setStatus( "Ready" );
        final String cancelledOrder = mvc
                .perform( put( String.format( "/api/v1/orders/%d", orders.get( 1 ).getId() ) )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( orders.get( 1 ) ) ) )
                .andExpect( status().isBadRequest() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( cancelledOrder.contains( "Cancelled" ) );
        orders = orderService.findAll();

        // Check status and ensure no inventory changes
        Assertions.assertEquals( "Cancelled", orders.get( 1 ).getStatus() );
        i = inventoryService.findAll().get( 0 ).getIngredients();
        Assertions.assertEquals( 5, i.get( 0 ).getAmount() );
        Assertions.assertEquals( 0, i.get( 1 ).getAmount() );
        Assertions.assertEquals( 5, i.get( 2 ).getAmount() );

        beverages = new ArrayList<Recipe>();
        beverages.add( recipes.get( 2 ) );
        final BevOrder o3 = new BevOrder( "coffeelover", "In Progress", beverages );
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o3 ) ) ).andExpect( status().isOk() );

        // Place 3rd order
        orders = orderService.findAll();
        Assertions.assertEquals( "In Progress", orders.get( 2 ).getStatus() );
        orders.get( 2 ).setStatus( "Ready" );
        mvc.perform( put( String.format( "/api/v1/orders/%d", orders.get( 2 ).getId() ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( orders.get( 2 ) ) ) )
                .andExpect( status().isOk() );
        orders = orderService.findAll();

        // Check status and inventory changes
        Assertions.assertEquals( "Ready", orders.get( 2 ).getStatus() );
        i = inventoryService.findAll().get( 0 ).getIngredients();
        Assertions.assertEquals( 3, i.get( 0 ).getAmount() );
        Assertions.assertEquals( 0, i.get( 1 ).getAmount() );
        Assertions.assertEquals( 3, i.get( 2 ).getAmount() );

        // Customer picked up order
        orders = orderService.findAll();
        Assertions.assertEquals( "Ready", orders.get( 0 ).getStatus() );
        final Employee e = new Employee();
        final BevOrder o = e.pickupOrder( orders.get( 0 ) );
        mvc.perform( put( String.format( "/api/v1/orders/%d", orders.get( 0 ).getId() ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( o ) ) )
                .andExpect( status().isOk() );
        orders = orderService.findAll();

        // Check status and inventory changes
        Assertions.assertEquals( "Picked Up", orders.get( 0 ).getStatus() );
    }

    /**
     * Gets all orders from database
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testgetPast () throws Exception {
        // Get recipes from repository
        final List<Recipe> recipes = recipeService.findAll();
        final List<Recipe> beverages = new ArrayList<Recipe>();
        beverages.add( recipes.get( 0 ) );
        beverages.add( recipes.get( 1 ) );

        final BevOrder o1 = new BevOrder( "dragonslayer", "In Progress", beverages );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o1 ) ) ).andExpect( status().isOk() );

        final BevOrder o2 = new BevOrder( "coffeelover", "In Progress", beverages );
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( o2 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 2, (int) orderService.count() );

        // Selects order to change from "In Progress" to "Ready"
        List<BevOrder> orders = orderService.findAll();
        Assertions.assertEquals( "In Progress", orders.get( 0 ).getStatus() );
        orders.get( 0 ).setStatus( "Ready" );

        // Make order, use inventory, and change status
        mvc.perform( put( String.format( "/api/v1/orders/%d", orders.get( 0 ).getId() ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( orders.get( 0 ) ) ) )
                .andExpect( status().isOk() );
        orders = orderService.findAll();

        // Checks current orders
        String orderString = mvc.perform( get( "/api/v1/orders" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();

        Assertions.assertTrue( orderString.contains( "In Progress" ) );
        Assertions.assertTrue( orderString.contains( "Ready" ) );

        // Checks past orders
        orderString = mvc.perform( get( "/api/v1/orders/history" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        Assertions.assertFalse( orderString.contains( "In Progress" ) );
        Assertions.assertTrue( orderString.contains( "Ready" ) );

        // Make another order, should be cancelled and no changes to inventory
        Assertions.assertEquals( "In Progress", orders.get( 1 ).getStatus() );
        orders.get( 1 ).setStatus( "Ready" );
        final String cancelledOrder = mvc
                .perform( put( String.format( "/api/v1/orders/%d", orders.get( 1 ).getId() ) )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( orders.get( 1 ) ) ) )
                .andExpect( status().isBadRequest() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( cancelledOrder.contains( "Cancelled" ) );

        // Checks current orders
        orderString = mvc.perform( get( "/api/v1/orders" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                .getContentAsString();

        Assertions.assertFalse( orderString.contains( "In Progress" ) );
        Assertions.assertTrue( orderString.contains( "Ready" ) );

        // Checks past orders
        orderString = mvc.perform( get( "/api/v1/orders/history" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        Assertions.assertFalse( orderString.contains( "In Progress" ) );
        Assertions.assertTrue( orderString.contains( "Ready" ) );

    }

}
