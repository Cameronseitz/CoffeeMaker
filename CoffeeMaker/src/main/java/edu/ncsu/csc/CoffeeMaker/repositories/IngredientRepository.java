package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;

/**
 * InventoryRepository is used to provide CRUD operations for the Inventory
 * model. Spring will generate appropriate code with JPA.
 *
 * @author Kai Presler-Marshall
 *
 */
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    /**
     * Finds an Ingredient object with the provided name. Spring will generate code
     * to make this happen.
     * 
     * @param name
     *            Name of the ingredient
     * @return Found Ingredient, null if none.
     */
    Ingredient findByName ( String name );

}
