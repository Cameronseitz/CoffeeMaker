package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

/**
 * Customer for the coffee maker. Customer is tied to the database using
 * Hibernate libraries. See UserRepository and UserService for the other two
 * pieces used for database support. Customer extends the functionality for a
 * particular type of user
 *
 * @author Gabriela Kote
 */
@Entity
public class Customer extends User {

    /** list of orders */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private final List<BevOrder> bevOrders;

    /**
     * This is an empty constructor for the customer
     */
    public Customer () {
        bevOrders = new ArrayList<BevOrder>();
    }

    /**
     * This constructor sets the user values for the customer
     *
     * @param password
     *            of the user
     * @param username
     *            of the user
     */
    public Customer ( final String username, final String password ) {
        super( username, password, "customer" );
        bevOrders = new ArrayList<BevOrder>();
    }

    /**
     * This method will add the order that the customer place to the list of
     * orders that is associated with the customer.
     *
     * @param bevOrder
     *            that will be placed
     */
    public void placeOrder ( final BevOrder bevOrder ) {
        if ( bevOrder.getStatus().equals( "In Progress" ) ) {
            if ( bevOrder.getOwner() != null ) {
                bevOrders.add( bevOrder );
            }
            else {
                throw new IllegalArgumentException( "Order must have an owner." );
            }
        }
        else {
            throw new IllegalArgumentException(
                    "Order must have a status of (In Progress) before adding it to the list." );
        }

    }

    /**
     * This finds the order from the list and then changes its status to
     * pickedup
     *
     * @param bevOrder
     *            that is to be picked up
     */
    public void pickUpOrder ( final BevOrder bevOrder ) {
        boolean orderExists = false;
        for ( int i = 0; i < bevOrders.size(); i++ ) {
            if ( bevOrder.equals( bevOrders.get( i ) ) ) {
                if ( bevOrder.getStatus().equals( "Ready" ) ) {
                    bevOrders.get( i ).setStatus( "Picked Up" );
                    orderExists = true;
                }
                else {
                    throw new IllegalArgumentException( "Status must be ready to pickup." );
                }
            }
        }
        if ( !orderExists ) {
            throw new IllegalArgumentException( "Order must already exist to pick it up" );
        }
    }

    /**
     * Canceling the given order in the orders list parameter An order is
     * cancelled when the customer places an order an there is insufficient
     * inventory OR when an employee tries to fulfill an order and there is
     * insufficient inventory
     *
     * @param bevOrder
     *            the order being cancelled
     */
    public void cancelOrder ( final BevOrder bevOrder ) {
        boolean orderExists = false;
        for ( int i = 0; i < bevOrders.size(); i++ ) {
            if ( bevOrder.equals( bevOrders.get( i ) ) ) {
                if ( bevOrder.getStatus().equals( "In Progress" ) ) {
                    bevOrders.get( i ).setStatus( "Cancelled" );
                    orderExists = true;
                }
                else {
                    throw new IllegalArgumentException( "Status must be In Progress to cancel." );
                }
            }
        }
        if ( !orderExists ) {
            throw new IllegalArgumentException( "Order must already exist to cancel it" );
        }
    }

    /**
     * Setting the given Order's status to ready in the Customers orders field
     *
     * @param order
     *            the order being set to Ready status
     */
    public void readyOrder ( final BevOrder order ) {
        boolean orderExists = false;
        for ( int i = 0; i < bevOrders.size(); i++ ) {
            if ( order.equals( bevOrders.get( i ) ) ) {
                if ( order.getStatus().equals( "In Progress" ) ) {
                    bevOrders.get( i ).setStatus( "Ready" );
                    orderExists = true;
                }
                else {
                    throw new IllegalArgumentException( "Status must be In Progress to be Ready." );
                }
            }
        }
        if ( !orderExists ) {
            throw new IllegalArgumentException( "Order must already exist to pick it up" );
        }
    }

    /**
     * This is used to get the orders of the customer for testing.
     *
     * @return list of orders
     */
    public List<BevOrder> getOrders () {
        return bevOrders;
    }

}
