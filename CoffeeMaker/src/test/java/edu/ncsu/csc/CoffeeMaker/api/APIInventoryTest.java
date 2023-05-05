package edu.ncsu.csc.CoffeeMaker.api;

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
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

/**
 * Tests the api routes relating to inventory.
 *
 * @author Team 1
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIInventoryTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    /**
     * web context
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * service object used throughout this test class as a means of interacting
     * with the db
     */
    @Autowired
    private InventoryService      service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    /**
     * Tests adding an ingredient that doesn't exist yet and a duplicate
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testAddIngredientToInventory () throws Exception {
        final Ingredient vanilla = new Ingredient( "Vanilla", 4 );
        service.deleteAll();

        // add an ingredient to inventory that does not exist yet
        mvc.perform( put( "/api/v1/inventory/addIngredient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( vanilla ) ) ).andExpect( status().isOk() );

        // try adding it again, this should fail
        mvc.perform( put( "/api/v1/inventory/addIngredient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( vanilla ) ) ).andExpect( status().isConflict() );

        Assertions.assertEquals( 1, service.getInventory().getIngredients().size() );

    }

}
