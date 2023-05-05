package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;

/**
 * Employee for the coffee maker. Employee is tied to the database using
 * Hibernate libraries. See UserRepository and UserService for the other two
 * pieces used for database support. Employee extends the functionality for a
 * particular type of user
 *
 * @author Sam Stone, Gabriela Kote
 */
@Entity
public class Employee extends User {

    /**
     * This is an empty constructor for the employee
     */
    public Employee () {

    }

    /**
     * This constructor sets the user values for the employee
     *
     * @param password
     *            of the user
     * @param username
     *            of the user
     */
    public Employee ( final String username, final String password ) {
        super( username, password, "employee" );
    }

    /**
     * We change the status to fulfilled order.
     *
     * @param bevOrder
     *            that is to be fulfilled
     * 
     * @return order with ready status
     */
    public BevOrder fulfillOrder ( final BevOrder bevOrder ) {
        bevOrder.setStatus( "Ready" );
        return bevOrder;
    }

    /**
     * We change the status to cancel the order
     *
     * @param bevOrder
     *            that is to be cancelled due to not enough inventory
     *
     * @return order with cancelled status
     */
    public BevOrder cancelOrder ( final BevOrder bevOrder ) {
        bevOrder.setStatus( "Cancelled" );
        return bevOrder;
    }

    /**
     * We change the status to pick for the order
     *
     * @param bevOrder
     *            that has been picked up
     * @return order with picked up status
     */
    public BevOrder pickupOrder ( final BevOrder bevOrder ) {
        bevOrder.setStatus( "Picked Up" );
        return bevOrder;
    }

}
