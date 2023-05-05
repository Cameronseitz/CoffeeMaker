package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Tests the API for coffee making
 *
 * @author Team 1
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APICoffeeTest {

    /**
     * Mock MVC
     */
    @Autowired
    private MockMvc          mvc;

    /**
     * service object used throughout this test class as a means of interacting
     * with the db
     */
    @Autowired
    private RecipeService    service;

    /**
     * service object used throughout this test class as a means of interacting
     * with the db
     */
    @Autowired
    private InventoryService iService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
        iService.deleteAll();

        final Inventory ivt = iService.getInventory();
        final Ingredient chocolate = new Ingredient( "Chocolate", 15 );
        final Ingredient coffee = new Ingredient( "Coffee", 15 );
        final Ingredient milk = new Ingredient( "Milk", 15 );
        final Ingredient sugar = new Ingredient( "Sugar", 15 );

        ivt.addIngredient( chocolate );
        ivt.addIngredient( coffee );
        ivt.addIngredient( milk );
        ivt.addIngredient( sugar );

        iService.save( ivt );

        final Recipe recipe = new Recipe();
        recipe.setName( "Coffee" );
        recipe.setPrice( 50 );
        final Ingredient ichocolate = new Ingredient( "Chocolate", 0 );
        final Ingredient icoffee = new Ingredient( "Coffee", 3 );
        final Ingredient imilk = new Ingredient( "Milk", 1 );
        final Ingredient isugar = new Ingredient( "Sugar", 1 );
        ivt.addIngredient( ichocolate );
        ivt.addIngredient( icoffee );
        ivt.addIngredient( imilk );
        ivt.addIngredient( isugar );
        service.save( recipe );
    }

    /**
     * Successfully buy coffee
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testPurchaseBeverage1 () throws Exception {

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 60 ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( 10 ) );

    }

    /**
     * Tries buying coffee with not enough funds
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testPurchaseBeverage2 () throws Exception {
        /* Insufficient amount paid */

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 40 ) ) ).andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough money paid" ) );

    }

    /**
     * Buys a coffee after adding an ingredient
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testPurchaseBeverage3 () throws Exception {
        /* Insufficient inventory */

        final Ingredient coffee = new Ingredient( "Coffee", 4 );

        final Inventory ivt = iService.getInventory();
        ivt.addIngredient( coffee );
        iService.save( ivt );

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 50 ) ) ).andExpect( status().isOk() );

    }
}
