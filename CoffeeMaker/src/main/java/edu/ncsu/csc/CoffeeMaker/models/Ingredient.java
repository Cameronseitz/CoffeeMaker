package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

/**
 * This class represents an object that can be put into a recipe as well as into
 * inventory. It contains a name of the thing that it is representing, as well 
 * as an amount associated with that.
 *
 */
@Entity
public class Ingredient extends DomainObject {

    /** Ingredient id */
    @Id
    @GeneratedValue
    private Long    id;

    /** ingredient name */
    private String  name;

    /** Ingredient amount */
    @Min ( 0 )
    private Integer amount;

    /**
     * Constructor for ingredient object that requires a name and an amount
     * @param name the name of the ingredient
     * @param amount the amount of the ingredient
     */
    public Ingredient ( final String name, @Min ( 0 ) final Integer amount ) {
    	if (name == null || amount == null || amount < 0) {
    		throw new IllegalArgumentException("name cannot be null and amount must be a positive integer or zero.");
    	}
        this.name = name;
        this.amount = amount;
    }

    /**
     * Constructor with no params
     */
    public Ingredient () {
        this.name = "";
    }

    /**
     * basic getter
     * @return the id
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * basic setter
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * basic getter
     * @return the ingredient name
     */
    public String getName () {
        return name;
    }

    /**
     * basic setter
     * @param name
     *            the name to set. Cannot be null
     */
    public void setName ( final String name ) {
    	if (name == null) {
    		throw new IllegalArgumentException("name cannot be null");
    	}
        this.name = name;
    }

    /**
     * basic getter
     * @return the amount
     */
    public Integer getAmount () {
        return amount;
    }

    /**
     * basic setter
     * @param amount
     *            the amount to set. Cannot be null or less than 0
     */
    public void setAmount ( final Integer amount ) {
    	if (amount == null || amount < 0) {
    		throw new IllegalArgumentException("amount must be a positive integer or zero.");
    	}
        this.amount = amount;
    }

    @Override
    public String toString () {
        return "Ingredient [id=" + id + ", name=" + name + "]";
    }
}
