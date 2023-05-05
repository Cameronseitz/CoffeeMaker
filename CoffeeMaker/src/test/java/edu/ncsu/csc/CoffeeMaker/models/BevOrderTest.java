package edu.ncsu.csc.CoffeeMaker.models;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.TestConfig;

/**
 * Tests the BevOrder Class
 *
 * @author Sam Stone
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
class BevOrderTest {

    @BeforeEach
    void setUp () throws Exception {
    }

    /**
     * Tests the constructor for BevOrder and the getters and setters
     */
    @Test
    void testSetConstructor () {
        final Ingredient i1 = new Ingredient( "Chocolate", 3 );
        final Ingredient i2 = new Ingredient( "Milk", 4 );
        final Ingredient i3 = new Ingredient( "Tea", 5 );
        final Ingredient i4 = new Ingredient( "Sugar", 1 );
        final Ingredient i5 = new Ingredient( "Coffee", 1 );

        final Recipe r1 = new Recipe();
        r1.setName( "Mocha" );
        r1.setPrice( 2 );
        r1.addIngredient( i1 );
        r1.addIngredient( i5 );
        r1.addIngredient( i2 );

        final Recipe r2 = new Recipe();
        r1.setName( "Chai" );
        r2.setPrice( 5 );
        r1.addIngredient( i3 );
        r1.addIngredient( i2 );

        final Recipe r3 = new Recipe();
        r1.setName( "Latte" );
        r3.setPrice( 7 );
        r1.addIngredient( i4 );
        r1.addIngredient( i5 );
        r1.addIngredient( i1 );

        final ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
        recipeList.add( r3 );
        recipeList.add( r2 );
        recipeList.add( r1 );
        final BevOrder b1 = new BevOrder( "owner1", "In Progress", recipeList );

        Assertions.assertEquals( "owner1", b1.getOwner() );
        Assertions.assertEquals( "In Progress", b1.getStatus() );
        Assertions.assertEquals( 14, b1.getCost() );

        // BevOrders can still equal each other even if status' are different
        // this is so the status can be updated in the Customer's methods
        final BevOrder b2 = b1;
        b2.setStatus( "Cancelled" );
        Assertions.assertTrue( b2.equals( b1 ) );

        // check the valid and invalid statuses
        assertDoesNotThrow( () -> b1.setStatus( "In Progress" ) );
        assertDoesNotThrow( () -> b1.setStatus( "Cancelled" ) );
        assertDoesNotThrow( () -> b1.setStatus( "Ready" ) );
        assertDoesNotThrow( () -> b1.setStatus( "Picked Up" ) );

        assertThrows( IllegalArgumentException.class, () -> b1.setStatus( "Invalid Status" ) );

        Assertions.assertEquals( recipeList, b1.getBeverages() );
    }
}
