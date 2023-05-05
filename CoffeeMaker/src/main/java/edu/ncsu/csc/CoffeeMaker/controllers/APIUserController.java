package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Customer;
import edu.ncsu.csc.CoffeeMaker.models.Employee;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * This is the controller that holds the REST endpoints that handle add and
 * login operations for the users.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Natasha Benson (ngbenso2) & Sam Stone (sjstone3)
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIUserController extends APIController {

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the User model
     */
    @Autowired
    private UserService service;

    /**
     * REST API method to provide POST access to the User model. This is used to
     * create a new User by automatically converting the JSON RequestBody
     * provided to a User object. Invalid JSON will fail.
     *
     * @param user
     *            user to be added to system
     *
     * @return ResponseEntity indicating success if the User could be saved to
     *         database
     */
    @PostMapping ( BASE_PATH + "/users" )
    public ResponseEntity addUser ( @RequestBody final Customer user ) {
        final String username = user.getUsername();
        final String password = user.getPassword();
        final String role = user.getRole();
        // Checks if user already exists in system
        if ( null != service.findByUsername( username ) ) {
            return new ResponseEntity( errorResponse( "User with the username " + username + " already exists" ),
                    HttpStatus.CONFLICT );
        }

        // Adds new Employee or Customer to database
        if ( "employee".equalsIgnoreCase( role ) ) {
            final Employee newEmployee = new Employee( username, password );
            service.save( newEmployee );
            return new ResponseEntity( successResponse( "Employee " + username + " successfully added" ),
                    HttpStatus.OK );
        }
        else if ( "customer".equalsIgnoreCase( role ) ) {
            final Customer newCustomer = new Customer( username, password );
            service.save( newCustomer );
            return new ResponseEntity( successResponse( "Customer " + username + " successfully added" ),
                    HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "Invalid user role" ), HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * REST API method to provide POST access to the User model. This is used to
     * validate login credentials
     *
     * @param username
     *            user's username
     * @param user
     *            user logging in
     * @return ResponseEntity indicating success if the User can access the
     *         system
     */
    @PostMapping ( BASE_PATH + "/users/{username}" )
    public ResponseEntity login ( @PathVariable ( "username" ) final String username,
            @RequestBody final Customer user ) {
        final String password = user.getPassword();

        String role = "";
        if ( null != service.findByUsername( username ) ) {
            role = service.findByUsername( username ).getRole();
        }

        // First, check if someone is already logged into CoffeeMaker
        for ( final User userCheck : service.findAll() ) {
            if ( userCheck.getIsCurrentUser() ) {
                return new ResponseEntity( errorResponse( "Cannot have more than one user logged in at once" ),
                        HttpStatus.BAD_REQUEST );
            }

        }
        // Pulls employee info from database
        if ( "employee".equalsIgnoreCase( role ) ) {
            if ( null == service.findByUsername( username ) ) {
                return new ResponseEntity( errorResponse( "No user found with the username " + username ),
                        HttpStatus.BAD_REQUEST );
            }

            // Hash for password entered on frontend, will be compared to
            // stored hash
            final String inputPassHash = ( new Employee( username, password ) ).getPassword();

            // Compares hash from input form and hash from database
            if ( inputPassHash.equals( service.findByUsername( username ).getPassword() ) ) {
                final User foundUser = service.findByUsername( username );
                foundUser.setIsCurrentUser( true );
                service.save( foundUser );
                return new ResponseEntity( successResponse( "employee" ), HttpStatus.OK );
            }
        }
        // Pulls customer info from database
        if ( "customer".equalsIgnoreCase( role ) ) {
            if ( null == service.findByUsername( username ) ) {
                return new ResponseEntity( errorResponse( "No user found with the username " + username ),
                        HttpStatus.BAD_REQUEST );
            }

            // Hash for password entered on frontend, will be compared to
            // stored hash
            final String inputPassHash = ( new Customer( username, password ) ).getPassword();

            // Compares hash from input form and hash from database
            if ( inputPassHash.equals( service.findByUsername( username ).getPassword() ) ) {
                final User foundUser = service.findByUsername( username );
                foundUser.setIsCurrentUser( true );
                service.save( foundUser );
                return new ResponseEntity( successResponse( "customer" ), HttpStatus.OK );
            }
        }

        return new ResponseEntity( errorResponse( "Invalid login information" ), HttpStatus.BAD_REQUEST );
    }

    /**
     * REST API method to provide GET access to the User model. This is used to
     * see which User is currently logged into CoffeeMaker
     *
     * @return ResponseEntity indicating the username and role of the User
     *         logged in
     */
    @GetMapping ( BASE_PATH + "/users" )
    public ResponseEntity getCurrentUser () {
        // find which user is logged into CoffeeMaker
        for ( final User user : service.findAll() ) {
            if ( user.getIsCurrentUser() ) {
                return new ResponseEntity( user, HttpStatus.OK );
            }

        }
        // No users are currently logged into CoffeeMaker
        return new ResponseEntity( errorResponse( "No users are logged in" ), HttpStatus.NOT_FOUND );

    }

    /**
     * REST API method to provide PUT access to the User model. This is used to
     * log the user out of CoffeeMaker
     *
     * @param username
     *            the User logging out
     * @return ResponseEntity indicating success when if the user can log out
     */
    @PutMapping ( BASE_PATH + "/users/{username}" )
    public ResponseEntity logout ( @PathVariable ( "username" ) final String username ) {
        System.out.println( username );
        if ( null == service.findByUsername( username ) ) {
            return new ResponseEntity( errorResponse( username + "is not a valid user" ), HttpStatus.NOT_FOUND );
        }
        else {
            final User foundUser = service.findByUsername( username );
            if ( foundUser.getIsCurrentUser() ) {
                foundUser.setIsCurrentUser( false );
                service.save( foundUser );
                return new ResponseEntity( successResponse( username + " logged out of CoffeeMaker" ), HttpStatus.OK );
            }
            // not signed in, so why are you logging out?
            else {
                return new ResponseEntity( errorResponse( username + " is already logged out of CoffeeMaker" ),
                        HttpStatus.BAD_REQUEST );
            }

        }
    }

}
