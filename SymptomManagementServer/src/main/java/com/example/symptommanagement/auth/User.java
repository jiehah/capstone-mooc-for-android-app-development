package com.example.symptommanagement.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * The User class implements the UserDetails interface provided by Spring Security.
 * It represents a custom user object with the necessary user details for authentication and authorization.
 */
public class User implements UserDetails {

    /**
     * Create and return a new User instance with the specified username, password, and authorities (roles) as strings.
     *
     * @param username    The username of the user.
     * @param password    The password of the user.
     * @param authorities The authorities (roles) of the user as strings.
     * @return A new User instance with the specified user details.
     */
    public static UserDetails create(String username, String password, String... authorities) {
        return new User(username, password, authorities);
    }

    /**
     * Create and return a new User instance with the specified username, password, and authorities (roles) as a collection of GrantedAuthority.
     *
     * @param username    The username of the user.
     * @param password    The password of the user.
     * @param authorities The authorities (roles) of the user as a collection of GrantedAuthority.
     * @return A new User instance with the specified user details.
     */
    public static UserDetails create(String username, String password, Collection<GrantedAuthority> authorities) {
        return new User(username, password, authorities);
    }

    // The collection of granted authorities (roles) associated with the user
    private final Collection<GrantedAuthority> authorities;
    // The user's password
    private final String password;
    // The user's username
    private final String username;

    /**
     * Private constructor to create a new User instance with the specified username, password, and authorities (roles) as strings.
     * The constructor is only called internally by the static create() method.
     *
     * @param username    The username of the user.
     * @param password    The password of the user.
     * @param authorities The authorities (roles) of the user as strings.
     */
    private User(String username, String password, String... authorities) {
        this.username = username;
        this.password = password;
        this.authorities = AuthorityUtils.createAuthorityList(authorities);
    }

    /**
     * Private constructor to create a new User instance with the specified username, password, and authorities (roles) as a collection of GrantedAuthority.
     * The constructor is only called internally by the static create() method.
     *
     * @param username    The username of the user.
     * @param password    The password of the user.
     * @param authorities The authorities (roles) of the user as a collection of GrantedAuthority.
     */
    private User(String username, String password, Collection<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Get the collection of granted authorities (roles) associated with the user.
     *
     * @return The collection of GrantedAuthority representing the user's authorities.
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Get the user's password.
     *
     * @return The user's password.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Get the user's username.
     *
     * @return The user's username.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Check if the user account is non-expired (always returns true in this implementation).
     *
     * @return True, indicating the user account is non-expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Check if the user account is non-locked (always returns true in this implementation).
     *
     * @return True, indicating the user account is non-locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Check if the user credentials are non-expired (always returns true in this implementation).
     *
     * @return True, indicating the user credentials are non-expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Check if the user is enabled (always returns true in this implementation).
     *
     * @return True, indicating the user is enabled.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
