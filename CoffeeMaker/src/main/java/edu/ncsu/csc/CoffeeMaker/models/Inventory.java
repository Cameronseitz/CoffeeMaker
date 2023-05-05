package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Inventory for the coffee maker. Inventory is tied to the database using
 * Hibernate libraries. See InventoryRepository and InventoryService for the
 * other two pieces used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Inventory extends DomainObject {

    /** id for inventory entry */
    @Id
    @GeneratedValue
    private Long                   id;

    /** amount of coffee */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private final List<Ingredient> ingredients;

    /**
     * Empty constructor for Hibernate
     */
    public Inventory () {
        // Intentionally empty so that Hibernate can instantiate
        // Inventory object.
        ingredients = new ArrayList<Ingredient>();
    }

    /**
     * Returns the ID of the entry in the DB
     *
     * @return long
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Checks that the given String parameter amount is a positive integer
     *
     * @param amount
     *            the String to check
     * @return if the String can be parsed to an Integer, then it returns that,
     *         otherwise an error is thrown
     * @throws IllegalArgumentException
     *             is thrown if the given amount is not a positive integer
     */
    public Integer checkAmount ( final String amount ) throws IllegalArgumentException {
        Integer returnAmount = 0;
        try {
            returnAmount = Integer.parseInt( amount );
        }
        catch ( final NumberFormatException e ) {
            throw new IllegalArgumentException( "Units of ingredient must be a positive integer" );
        }
        if ( returnAmount < 0 ) {
            throw new IllegalArgumentException( "Units of ingredient must be a positive integer" );
        }

        return returnAmount;
    }

    /**
     * Returns true if there are enough ingredients to make the beverage.
     *
     * @param r
     *            recipe to check if there are enough ingredients
     * @return true if enough ingredients to make the beverage
     */
    public boolean enoughIngredients ( final Recipe r ) {
        for ( final Ingredient recipeIngredient : r.getIngredients() ) {
            if ( getIngredientByName( recipeIngredient.getName() ).getAmount() < recipeIngredient.getAmount() ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes the ingredients used to make the specified recipe, if the
     * inventory does not have sufficient ingredients, false is returned
     *
     * @param r
     *            recipe to make
     * @return true if recipe is made.
     */
    public boolean useIngredients ( final Recipe r ) {
        if ( !enoughIngredients( r ) ) {
            return false;
        }

        for ( final Ingredient recipeIngredient : r.getIngredients() ) {
            final Ingredient ingredientInInventory = getIngredientByName( recipeIngredient.getName() );
            ingredientInInventory.setAmount( ingredientInInventory.getAmount() - recipeIngredient.getAmount() );
        }
        return true;
    }

    /**
     * Adds the given ingredient if it does not already exist in the list. If it
     * does, add the amount. Throws IAE if the ingredient's amount is less than
     * 0.
     *
     * @param ingredient
     *            the ingredient to add to inventory
     */
    public void addIngredient ( final Ingredient ingredient ) {
        if ( ingredient.getAmount() < 0 ) {
            throw new IllegalArgumentException( "Amount cannot be negative" );
        }
        final Ingredient ingredientInInventory = getIngredientByName( ingredient.getName() );
        if ( ingredientInInventory == null ) {
            ingredients.add( ingredient );
        }
        else {
            ingredientInInventory.setAmount( ingredientInInventory.getAmount() + ingredient.getAmount() );
        }
    }

    /**
     * Adds the parameter ingredients to inventory
     *
     * @param ingredients
     *            the ingredients to add
     */
    public void addIngredients ( final List<Ingredient> ingredients ) {
        for ( final Ingredient ingredient : ingredients ) {
            addIngredient( ingredient );
        }
    }

    /**
     * Get the list of ingredients
     *
     * @return ingredients
     */
    public List<Ingredient> getIngredients () {
        return ingredients;
    }

    /**
     * Returns the ingredient based on its string name
     *
     * @param name
     *            the name of the ingredient to get from inventory
     * @return the ingredient with the matching name
     */
    public Ingredient getIngredientByName ( final String name ) {
        for ( final Ingredient ingredient : ingredients ) {
            if ( ingredient.getName().equals( name ) ) {
                return ingredient;
            }
        }
        return null;
    }

    /**
     * Deletes an ingredient from this inventory object based on its name
     *
     * @param name
     *            the name of the ingredient to delete
     * @return the ingredient that was deleted. Null if no ingredient was
     *         deleted
     */
    public Ingredient deleteIngredient ( final String name ) {
        for ( int i = 0; i < ingredients.size(); i++ ) {
            if ( ingredients.get( i ).getName().equals( name ) ) {
                return ingredients.remove( i );
            }
        }
        return null;
    }

    /**
     * Returns a string describing the current contents of the inventory.
     *
     * @return String
     */
    @Override
    public String toString () {
        final StringBuffer buf = new StringBuffer();
        for ( final Ingredient ingredient : ingredients ) {
            buf.append( ingredient.getName() );
            buf.append( ": " );
            buf.append( ingredient.getAmount() );
            buf.append( "\n" );
        }
        return buf.toString();
    }
}
