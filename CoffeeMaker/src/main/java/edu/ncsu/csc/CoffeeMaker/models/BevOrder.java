package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;

/**
 * Customer for the coffee maker. Order is tied to the database using Hibernate
 * libraries. See OrderRepository and OrderService for the other two pieces used
 * for database support. Order represents the Order's that a Customer will place
 * and what the Employee's will fulfill
 *
 * @author Sam Stone
 */
@Entity
public class BevOrder extends DomainObject {

    /** The unique id for the order */
    @Id
    @GeneratedValue
    private Long         id;

    /**
     * The current status of the Order can be: "In Progress", "Ready", "Picked
     * Up", and "Cancelled"
     */
    private String       status;

    /** The username of the Customer who placed the order */
    private String       owner;

    /** The total cost of the beverages in the order */
    private int          cost;

    /** The list of beverages in one order */
    @ManyToMany ( fetch = FetchType.EAGER )
    @JoinTable(
    	    name = "bev_order_beverages",
    	    joinColumns = @JoinColumn(name = "order_id"),
    	    inverseJoinColumns = @JoinColumn(name = "recipe_id"),
    	    uniqueConstraints = {}
    	)
    private List<Recipe> beverages;

    /**
     * This is an empty constructor for the Order
     */
    public BevOrder () {
        this.owner = "";
        beverages = new ArrayList<Recipe>();
    }

    /**
     * This constructor sets the user values for the Order
     *
     * @param owner
     *            the Customer who placed the Order
     * @param status
     *            of the Order ("In Progress, "Ready", or "Picked Up", or
     *            "Cancelled")
     * @param beverages
     *            the List of beverages placed in the Order
     */
    public BevOrder ( final String owner, final String status, final List<Recipe> beverages ) {
        setOwner( owner );
        setStatus( status );
        this.beverages = new ArrayList<Recipe>();
        setBeverages( beverages );
        setCost();
    }

    /**
     * Gets the beverages in the Order
     *
     * @return the list of beverages
     */
    public List<Recipe> getBeverages () {
        return beverages;
    }

    /**
     * Sets the list of beverages in the order
     *
     * @param beverages
     *            the recipes of the beverages placed
     */
    public void setBeverages ( final List<Recipe> beverages ) {
        this.beverages = beverages;
    }

    /**
     * Sets the owner of the order
     *
     * @param owner
     *            the Order's owner
     */
    private void setOwner ( final String owner ) {
        this.owner = owner;

    }

    /**
     * Get the owner of the Order
     *
     * @return the owner String
     */
    public String getOwner () {
        return owner;
    }

    /**
     * Sets the owner of the Order
     *
     * @return the status of the Order
     */
    public String getStatus () {
        return status;
    }

    /**
     * Sets the status of the Order
     *
     * @param status
     *            of the Order
     */
    public void setStatus ( final String status ) {
        if ( ( "In Progress" ).equalsIgnoreCase( status ) || ( "Ready" ).equalsIgnoreCase( status )
                || ( "Picked Up" ).equalsIgnoreCase( status ) || ( "Cancelled" ).equalsIgnoreCase( status ) ) {
            this.status = status;
        }
        else {
            throw new IllegalArgumentException( "Invalid status String" );
        }

    }

    /**
     * Gets the Order's id
     *
     * @return the Order's id
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the Order's id
     *
     * @param id
     *            the Order's id
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Adds the prices of every beverage in the Order *
     *
     */
    public void setCost () {
        int totalCost = 0;
        for ( final Recipe recipe : beverages ) {
            totalCost += recipe.getPrice();
        }
        this.cost = totalCost;
    }

    /**
     * Gets the cost of the Order
     *
     * @return the Order's cost
     */
    public int getCost () {
        return cost;
    }

    @Override
    public int hashCode () {
        return Objects.hash( beverages, cost, id, owner );
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final BevOrder other = (BevOrder) obj;
        return Objects.equals( beverages, other.beverages ) && cost == other.cost && Objects.equals( id, other.id )
                && Objects.equals( owner, other.owner );
    }

}
