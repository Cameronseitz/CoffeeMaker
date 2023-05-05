package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.BevOrder;
import edu.ncsu.csc.CoffeeMaker.models.Customer;
import edu.ncsu.csc.CoffeeMaker.models.Employee;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * This is the controller that holds the REST endpoints that handle add and
 * update operations for the Inventory.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Natasha Benson (ngbenso2)
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIOrderController extends APIController {

    /**
     * OrderService object, to be autowired in by Spring to allow for
     * manipulating the Order model
     */
    @Autowired
    private OrderService     orderService;

    /**
     * UserService object, to be autowired in by Spring to allow for
     * manipulating the User model
     */
    @Autowired
    private UserService      userService;

    /**
     * UserService object, to be autowired in by Spring to allow for
     * manipulating the User model
     */
    @Autowired
    private InventoryService inventoryService;

    /**
     * REST API endpoint to provide GET access to the CoffeeMaker's singleton
     * orders. This will convert the orders to JSON.
     *
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/orders" )
    public ResponseEntity getCurrentOrders () {
        final List<BevOrder> allorders = orderService.findAll();
        return new ResponseEntity( allorders, HttpStatus.OK );
    }

    /**
     * REST API endpoint to provide GET access to the CoffeeMaker's singleton
     * orders. This will convert the orders to JSON.
     *
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/orders/history" )
    public ResponseEntity getPastOrders () {
        final List<BevOrder> allorders = orderService.findAll();
        final List<BevOrder> pastOrders = new ArrayList<BevOrder>();
        for ( final BevOrder o : allorders ) {
            if ( o.getStatus().equalsIgnoreCase( "Ready" ) || o.getStatus().equalsIgnoreCase( "Picked Up" )
                    || o.getStatus().equalsIgnoreCase( "Cancelled" ) ) {
                pastOrders.add( o );
            }
        }
        return new ResponseEntity( pastOrders, HttpStatus.OK );
    }

    /**
     * REST API endpoint to allow a single order to be added to orders
     *
     * @param bevOrder
     *            order to add to orders
     * @return response to the request
     */
    @PostMapping ( BASE_PATH + "/orders" )
    public ResponseEntity addOrder ( @RequestBody final BevOrder bevOrder ) {
        if ( bevOrder.getStatus() == null || bevOrder.getOwner() == null ) {
            return new ResponseEntity( errorResponse( "Cannot add order to system" ), HttpStatus.BAD_REQUEST );
        }
        if ( userService.findByUsername( bevOrder.getOwner() ) != null ) {
            final Customer c = (Customer) userService.findByUsername( bevOrder.getOwner() );
            c.placeOrder( bevOrder );
            orderService.save( bevOrder );
            return new ResponseEntity( successResponse( "Order placed" ), HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "Order could not be added" ), HttpStatus.CONFLICT );
        }
    }

    /**
     * REST API endpoint to allow a single order to be added to orders
     *
     * @param bevOrder
     *            order to be edited
     * @param id
     *            id of order
     * @return response to the request
     */
    @PutMapping ( BASE_PATH + "/orders/{id}" )
    public ResponseEntity editOrder ( @PathVariable final Long id, @RequestBody final BevOrder bevOrder ) {
        BevOrder order = orderService.findById( id );
        Inventory inventory = inventoryService.getInventory();
        final Employee employee = new Employee();
        // Current order is in progress, status being changed from "In progress"
        // to "Ready"
        if ( bevOrder.getStatus().equalsIgnoreCase( "Ready" ) ) {
            final List<Recipe> b = bevOrder.getBeverages();
            // Go through each recipe to make sure there is enough ingredients
            for ( final Recipe r : b ) {
            	if (!inventory.enoughIngredients(r)) {
            		// Order saved with "Cancelled" status on Employee side
                    order = employee.cancelOrder( order );
                    orderService.save( order );
            		return new ResponseEntity( errorResponse( "Cancelled" ), HttpStatus.BAD_REQUEST );
            	}

            }
            //actually use the ingredients if there is enough of all of them
            for ( final Recipe r : b ) {
                inventory.useIngredients( r );
            }
            // Inventory depleted after recipes successfully created
            inventoryService.save( inventory );
            // Changes order status to "Ready" and saves it
            order = employee.fulfillOrder( order );
            orderService.save( order );
            return new ResponseEntity( successResponse( "Order ready" ), HttpStatus.OK );
        }
        // Current order is in progress, status being changed from "Ready" to
        // "Picked Up"
        else if ( bevOrder.getStatus().equalsIgnoreCase( "Picked Up" ) ) {
            // Changes order status to "Picked Up" and saves it
            order = employee.pickupOrder( order );
            orderService.save( order );
            return new ResponseEntity( successResponse( "Order picked up" ), HttpStatus.OK );
        }
        return new ResponseEntity( errorResponse( "Unable to edit order" ), HttpStatus.BAD_REQUEST );
    }
}
