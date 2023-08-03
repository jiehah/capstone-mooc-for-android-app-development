package com.example.symptommanagement.data;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * The Physician class represents a physician's information, including personal details
 * and the collection of patients associated with the physician.
 */
@Data
@NoArgsConstructor
public class Physician {

    /**
     * The unique identifier for the physician.
     */
    private String id;

    /**
     * The first name of the physician.
     */
    private String firstName;

    /**
     * The last name of the physician.
     */
    private String lastName;

    /**
     * The collection of patients associated with the physician.
     */
    private Collection<Patient> patients;

    /**
     * Constructor for the Physician class with first name and last name provided.
     *
     * @param firstName The first name of the physician.
     * @param lastName  The last name of the physician.
     */
    public Physician(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patients = null;
    }

    /**
     * Get the full name of the physician.
     *
     * @return The full name of the physician (first name + last name).
     */
    public String getName() {
        String name = "";
        if (firstName != null && !firstName.isEmpty()) {
            name += firstName;
        }
        if (!name.isEmpty()) {
            name += " ";
        }
        if (lastName != null && !lastName.isEmpty()) {
            name += lastName;
        }
        return name;
    }

    /**
     * Get a debug string representation of the physician's full name.
     *
     * @return A string representation of the physician's full name for debugging purposes.
     */
    public String toDebugString() {
        return getName();
    }

    /**
     * Override toString for ListAdapter
     *
     * @return A string representation of the physician's full name for ListAdapter
     */
    @Override
    public String toString() {
        return getName();
    }
}
