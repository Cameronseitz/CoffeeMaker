package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;

/**
 * Recipe for the coffee maker. Recipe is tied to the database using Hibernate
 * libraries. See RecipeRepository and RecipeService for the other two pieces
 * used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Recipe extends DomainObject {

    /** Recipe id */
    @Id
    @GeneratedValue
    private Long                   id;

    /** Recipe name */
    private String                 name;

    /** Recipe price */
    @Min ( 0 )
    private Integer                price;

    /** Recipe's ingredients */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private final List<Ingredient> ingredients;

    /**
     * Creates a default recipe for the coffee maker.
     */
    public Recipe () {
        this.name = "";
        ingredients = new ArrayList<Ingredient>();
    }

    /**
     * Adds the given ingredient if it does not already exist in the list. If it
     * does, add the amount. Throws IAE if the ingredient's amount is less than
     * 0.
     *
     * @param ingredient the ingredient to add to the recipe
     */
    public void addIngredient ( final Ingredient ingredient ) {
        if ( ingredient.getAmount() < 0 ) {
            throw new IllegalArgumentException( "Amount cannot be negative" );
        }
        final Ingredient ingredientInRecipe = getIngredientByName( ingredient.getName() );
        if ( ingredientInRecipe == null ) {
            ingredients.add( ingredient );
        }
        else {
            ingredientInRecipe.setAmount( ingredientInRecipe.getAmount() + ingredient.getAmount() );
        }
    }

    /**
     * Return the list of ingredients in the recipe
     * @return ingredients in the recipe
     */
    public List<Ingredient> getIngredients () {
        return ingredients;
    }

    /**
     * Return the ingredient according to the name passed in
     * @param name the name of the ingredient to find
     * @return ingredients
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
     * Get the ID of the Recipe
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Recipe (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns name of the recipe.
     *
     * @return Returns the name.
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the recipe name.
     *
     * @param name
     *            The name to set.
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Returns the price of the recipe.
     *
     * @return Returns the price.
     */
    public Integer getPrice () {
        return price;
    }

    /**
     * Sets the recipe price.
     *
     * @param price
     *            The price to set.
     */
    public void setPrice ( final Integer price ) {
        this.price = price;
    }

    /**
     * Updates the fields to be equal to the passed in recipe's list of
     * ingredients and price
     *
     * @param r
     *            the recipe to get the information from
     *
     */
    public void updateRecipe ( final Recipe r ) {
        ingredients.clear();
        for ( final Ingredient ingredient : r.getIngredients() ) {
            addIngredient( ingredient );
        }
        setPrice( r.getPrice() );
    }

    /**
     * Returns the ingredients.
     *
     * @return String
     */
    @Override
    public String toString () {
        return ingredients.toString();
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        Integer result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        return result;
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
        final Recipe other = (Recipe) obj;
        if ( name == null ) {
            if ( other.name != null ) {
                return false;
            }
        }
        else if ( !name.equals( other.name ) ) {
            return false;
        }
        return true;
    }

}
