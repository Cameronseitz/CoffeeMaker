package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import edu.ncsu.csc.CoffeeMaker.models.Customer;
import edu.ncsu.csc.CoffeeMaker.models.Employee;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Tests APIUserController
 *
 * @author Natasha Benson (ngbenso2) & Sam Stone (sjstone3)
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIUserTest {

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
    private UserService           service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    /**
     * Adds valid customers and employees to the database
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void addUsersValid () throws Exception {

        // Adding an employee
        final Employee e1 = new Employee( "bestWorker", "securePassword!" );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( e1 ) ) ).andExpect( status().isOk() );
        Assertions.assertEquals( 1, (int) service.count() );
        List<User> users = service.findAll();
        Assertions.assertEquals( "bestWorker", users.get( 0 ).getUsername() );
        Assertions.assertEquals( "employee", users.get( 0 ).getRole() );

        // Adding another employee
        final Employee e2 = new Employee( "coff33worker", "seurePassword1!" );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( e2 ) ) ).andExpect( status().isOk() );
        Assertions.assertEquals( 2, (int) service.count() );
        users = service.findAll();
        Assertions.assertEquals( "coff33worker", users.get( 1 ).getUsername() );
        Assertions.assertEquals( "employee", users.get( 1 ).getRole() );

        // Adding a customer
        final Customer c1 = new Customer( "coffee4life", "securePassword!" );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );
        Assertions.assertEquals( 3, (int) service.count() );
        users = service.findAll();
        Assertions.assertEquals( "coffee4life", users.get( 2 ).getUsername() );
        Assertions.assertEquals( "customer", users.get( 2 ).getRole() );

        // Adding another customer
        final Customer c2 = new Customer( "dragonslayer", "seurePassword1!" );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c2 ) ) ).andExpect( status().isOk() );
        Assertions.assertEquals( 4, (int) service.count() );
        users = service.findAll();
        Assertions.assertEquals( "dragonslayer", users.get( 3 ).getUsername() );
        Assertions.assertEquals( "customer", users.get( 3 ).getRole() );
    }

    /**
     * Attempts to add invalid users to the database
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void addUsersInvalid () throws Exception {

        // Adding an employee
        final Employee e1 = new Employee( "bestWorker", "securePassword!" );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( e1 ) ) ).andExpect( status().isOk() );
        Assertions.assertEquals( 1, (int) service.count() );

        // Adding an employee with same username
        final Employee e2 = new Employee( "bestWorker", "securePassword!" );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( e2 ) ) ).andExpect( status().isConflict() );
        Assertions.assertEquals( 1, (int) service.count() );

        // Adding a customer with same username
        final Customer c1 = new Customer( "bestWorker", "securePassword!" );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isConflict() );
        Assertions.assertEquals( 1, (int) service.count() );

        // Adding an customer
        final Customer c2 = new Customer( "coffeetoffee", "secure_Pass03!" );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c2 ) ) ).andExpect( status().isOk() );
        Assertions.assertEquals( 2, (int) service.count() );

        // Adding an customer with same username
        final Customer c3 = new Customer( "coffeetoffee", "secure_Pass03!" );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c3 ) ) ).andExpect( status().isConflict() );
        Assertions.assertEquals( 2, (int) service.count() );

        // Adding user with invalid role
        final Customer c4 = new Customer( "coffeetoffee", "secure_Pass03!" );
        c4.setRole( "bad role" );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c4 ) ) ).andExpect( status().is4xxClientError() );
        Assertions.assertEquals( 2, (int) service.count() );
    }

    /**
     * Tests customer and employee login
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void validLogin () throws Exception {

        // Adds customers and employees to database
        final Employee e1 = new Employee( "bestWorker", "securePassword!" );
        final Employee e2 = new Employee( "lazyWorker", "securepass" );
        final Customer c1 = new Customer( "coffee4life", "NotsecurePassword!" );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( e1 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( e2 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );
        Assertions.assertEquals( 3, (int) service.count() );

        final Employee e1Login = new Employee( "bestWorker", "securePassword!" );
        Assertions.assertEquals( e1.getPassword(), e1Login.getPassword() );
        final String response = mvc
                .perform( post( String.format( "/api/v1/users/%s", e1Login.getUsername() ) )
                        .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( e1Login ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( response.contains( "employee" ) );
        Assertions.assertTrue( service.findByUsername( e1.getUsername() ).getIsCurrentUser() );

        // Try logging in another user at the same time
        final Employee e2Login = new Employee( "lazyWorker", "securepass" );
        Assertions.assertEquals( e2.getPassword(), e2Login.getPassword() );
        final String response2 = mvc
                .perform( post( String.format( "/api/v1/users/%s", e2Login.getUsername() ) )
                        .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( e2Login ) ) )
                .andExpect( status().isBadRequest() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( response2.contains( "Cannot have more than one user logged in at once" ) );
        //
        // final Customer c1Login = new Customer( "coffee4life",
        // "NotsecurePassword!" );
        // Assertions.assertEquals( c1.getPassword(), c1Login.getPassword() );
        // final String response3 = mvc
        // .perform( post( String.format( "/api/v1/users/%s",
        // c1Login.getUsername() ) )
        // .contentType( MediaType.APPLICATION_JSON ).content(
        // TestUtils.asJsonString( c1Login ) ) )
        // .andExpect( status().isOk()
        // ).andReturn().getResponse().getContentAsString();
        // Assertions.assertTrue( response3.contains( "customer" ) );

    }

    /**
     * Tests customer and employee login
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testGetCurrentUser () throws Exception {
        // Adds customers and employees to database
        final Employee e1 = new Employee( "bestWorker", "securePassword!" );
        final Employee e2 = new Employee( "lazyWorker", "securepass" );
        final Customer c1 = new Customer( "coffee4life", "NotsecurePassword!" );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( e1 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( e2 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );
        Assertions.assertEquals( 3, (int) service.count() );

        // checking that no users are logged into CoffeeMaker
        final String response1 = mvc.perform( get( "/api/v1/users" ) ).andExpect( status().isNotFound() ).andReturn()
                .getResponse().getContentAsString();
        Assertions.assertTrue( response1.contains( "No users are logged in" ) );

        // test logging in an employee sets them as the current user
        final Employee e1Login = new Employee( "bestWorker", "securePassword!" );
        mvc.perform( post( String.format( "/api/v1/users/%s", e1Login.getUsername() ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( e1Login ) ) )
                .andExpect( status().isOk() );
        final String response2 = mvc.perform( get( "/api/v1/users" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        Assertions.assertTrue( response2.contains( "\"username\":\"bestWorker\",\"role\":\"employee\"" ) );
    }

    /**
     * Tests logging a customer out of CoffeeMaker
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testLogout () throws Exception {
        // Adds customers and employees to database
        final Employee e1 = new Employee( "bestWorker", "securePassword!" );
        final Employee e2 = new Employee( "lazyWorker", "securepass" );
        final Customer c1 = new Customer( "coffee4life", "NotsecurePassword!" );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( e1 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( e2 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( c1 ) ) ).andExpect( status().isOk() );
        Assertions.assertEquals( 3, (int) service.count() );

        // checking that no users are logged into CoffeeMaker
        final String response1 = mvc.perform( get( "/api/v1/users" ) ).andExpect( status().isNotFound() ).andReturn()
                .getResponse().getContentAsString();
        Assertions.assertTrue( response1.contains( "No users are logged in" ) );

        // test logging in an employee sets them as the current user
        final Employee e1Login = new Employee( "bestWorker", "securePassword!" );
        mvc.perform( post( String.format( "/api/v1/users/%s", e1Login.getUsername() ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( e1Login ) ) )
                .andExpect( status().isOk() );
        final String response2 = mvc.perform( get( "/api/v1/users" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        Assertions.assertTrue( response2.contains( "\"username\":\"bestWorker\",\"role\":\"employee\"" ) );

        // log out the employee and make sure no users are logged in
        final String response3 = mvc
                .perform( put( String.format( "/api/v1/users/%s", e1Login.getUsername() ) )
                        .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( e1Login ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( response3.contains( "bestWorker logged out of CoffeeMaker" ) );
        final String response4 = mvc.perform( get( "/api/v1/users" ) ).andExpect( status().isNotFound() ).andReturn()
                .getResponse().getContentAsString();
        Assertions.assertTrue( response4.contains( "No users are logged in" ) );

        // test logging in a customer sets them as the current user
        final Customer c1Login = new Customer( "coffee4life", "NotsecurePassword!" );
        mvc.perform( post( String.format( "/api/v1/users/%s", c1Login.getUsername() ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( c1Login ) ) )
                .andExpect( status().isOk() );
        final String response5 = mvc.perform( get( "/api/v1/users" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        Assertions.assertTrue( response5.contains( "\"username\":\"coffee4life\",\"role\":\"customer\"" ) );

    }

}
