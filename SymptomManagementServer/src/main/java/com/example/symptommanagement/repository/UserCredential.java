package com.example.symptommanagement.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Represents user credentials in the symptom management application.
 * User credentials are used for authentication and role-based access control.
 */
@Data
public class UserCredential {

    /**
     * Enumeration for user roles.
     */
    public enum UserRole {
        NOT_ASSIGNED(-1),   // Role not assigned
        ADMIN(500),         // Administrator role
        PHYSICIAN(300),     // Physician role
        PATIENT(200);       // Patient role

        private int value;

        UserRole(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public static UserRole findByValue(int val) {
            for (UserRole role : values()) {
                if (role.getValue() == val) {
                    return role;
                }
            }
            return NOT_ASSIGNED;
        }
    }

    /**
     * The unique identifier for the user credentials.
     */
    @Id
    private String id;

    /**
     * The user ID associated with these credentials.
     */
    private String userId;

    /**
     * The username of the user.
     */
    private String userName;

    /**
     * The role of the user (ADMIN, PHYSICIAN, PATIENT, or NOT_ASSIGNED).
     */
    private UserRole userRole = UserRole.NOT_ASSIGNED;

    /**
     * The integer value representing the user's role.
     */
    private int userRoleValue = UserRole.NOT_ASSIGNED.getValue();

    /**
     * The user's password (JsonIgnore to prevent serialization).
     */
    @JsonIgnore
    private String password;

    /**
     * Sets the user's role and updates the user role value accordingly.
     *
     * @param userRole The UserRole enum representing the user's role.
     */
    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
        setUserRoleValue(userRole.getValue());
    }
}
