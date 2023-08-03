package com.example.symptommanagement.data;

import lombok.Data;

/**
 * UserCredential represents the credentials and role associated with a user.
 * It contains information such as user ID, username, user role, and password.
 */
@Data
public class UserCredential {

    /**
     * The unique identifier for the user credential.
     */
    private String id;

    /**
     * The database identifier for the user credential (marked as transient, not to be serialized).
     */
    private transient long dbId;

    /**
     * The user ID associated with the user credential.
     */
    private String userId;

    /**
     * The username associated with the user credential.
     */
    private String userName;

    /**
     * The user role assigned to the user.
     */
    private UserRole userRole = UserRole.NOT_ASSIGNED;

    /**
     * The integer value representing the user role (using UserRole enum constants).
     */
    private int userRoleValue = UserRole.NOT_ASSIGNED.getValue();

    /**
     * The password associated with the user credential.
     */
    private String password;

    /**
     * The timestamp of the user's last login.
     */
    private volatile long lastLogin;

    /**
     * Enum representing different user roles with associated integer values.
     * It helps map integer values to specific user roles and vice versa.
     */
    public enum UserRole {
        NOT_ASSIGNED(-1),   // Default value for an unassigned role
        ADMIN(500),         // User role for administrators
        PHYSICIAN(300),     // User role for physicians
        PATIENT(200);       // User role for patients

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

        /**
         * Find and return the UserRole corresponding to the given integer value.
         *
         * @param val The integer value representing the user role.
         * @return The corresponding UserRole, or NOT_ASSIGNED if no match is found.
         */
        public static UserRole findByValue(int val) {
            for (UserRole r : values()) {
                if (r.getValue() == val) {
                    return r;
                }
            }
            return NOT_ASSIGNED;
        }
    }

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
