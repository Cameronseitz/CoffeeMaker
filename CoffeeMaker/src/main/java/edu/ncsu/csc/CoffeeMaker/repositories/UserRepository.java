package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * UserRepository is used to provide CRUD operations for the User model. Spring
 * will generate appropriate code with JPA.
 *
 * @author Sam Stone
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds an User object with the provided name. Spring will generate code to
     * make this happen.
     *
     * @param username
     *            of the user Name of the ingredient
     * @return Found User, null if none.
     */
    User findByUsername ( String username );

}
