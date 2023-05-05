package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Users for the coffee maker. User is tied to the database using Hibernate
 * libraries. See UserRepository and UserService for the other two pieces used
 * for database support.
 *
 * @author Gabriela Kote & Sam Stone
 */
@Entity
public abstract class User extends DomainObject {
    /** User id */
    @Id
    @GeneratedValue
    private Long    id;

    /** User name */
    private String  username;

    /** User password */
    private String  password;

    /** User name */
    private String  role;

    /** Keeps track if the User is currently logged into CoffeeMaker */
    private boolean isCurrentUser;

    /**
     * Creates a default user for the coffee maker.
     */
    public User () {
        this.username = "";
        this.setPassword( "" );
        this.role = "";
        this.setIsCurrentUser( false );
    }

    /**
     * This constructor sets the user values for the user
     *
     * @param username
     *            of the user
     * @param password
     *            of the user
     * @param role
     *            of the user
     */
    public User ( final String username, final String password, final String role ) {
        if ( username == null || password == null || role == null ) {
            throw new IllegalArgumentException( "Value cannot be null and amount must be a positive integer or zero." );
        }
        setUsername( username );
        setPassword( password );
        setRole( role );
        setIsCurrentUser( false );
    }

    /**
     * Get the ID of the User
     *
     * @return the ID
     */
    @Override
    public Serializable getId () {
        return id;
    }

    /**
     * Set the ID of the User (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * This method will hash the password and
     *
     * @param password
     *            of the user
     *
     * @return the hashed password
     */
    public String hashPassword ( final String password ) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance( "SHA-256" );
        }
        catch ( final NoSuchAlgorithmException e ) {
            e.printStackTrace();
        }
        final byte[] hashInBytes = md.digest( password.getBytes( StandardCharsets.UTF_8 ) );
        final String hashedPassword = Base64.getEncoder().encodeToString( hashInBytes );
        return hashedPassword;
    }

    /**
     * This is used to get the username of the user.
     *
     * @return user name
     */
    public String getUsername () {
        return username;
    }

    /**
     * This is used to set the user's username.
     *
     * @param username
     *            that is set
     */
    public void setUsername ( final String username ) {
        this.username = username;
    }

    /**
     * This allows you to return the password.
     *
     * @return the password of the user
     */
    @JsonIgnore
    @JsonProperty(value = "password")
    public String getPassword () {
        return password;
    }

    /**
     * This sets the password of the user.
     *
     * @param password
     *            of the user that is to be set
     */
    public void setPassword ( final String password ) {
        this.password = hashPassword( password );
    }

    /**
     * This method grabs the role of the user
     *
     * @return the user's role
     */
    public String getRole () {
        return role;
    }

    /**
     * This method sets the role of the user
     *
     * @param role
     *            of the user
     */
    public void setRole ( final String role ) {
        this.role = role;
    }

    /**
     * This method returns true if the user is currently logged into CoffeeMaker
     *
     * @return the login status of the User
     *
     */
    public boolean getIsCurrentUser () {
        return isCurrentUser;
    }

    /**
     * Sets the User's login status
     *
     * @param isCurrentUser
     *            the login status of the User, true if the User is currently
     *            logged in
     */
    public void setIsCurrentUser ( final boolean isCurrentUser ) {
        this.isCurrentUser = isCurrentUser;
    }
}
