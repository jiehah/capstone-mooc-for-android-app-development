package com.example.symptommanagement.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Collection;

/**
 * Represents a reminder entity in the symptom management application.
 * Reminders are used to schedule notifications for patients.
 */
@Data
public class Physician {

    /**
     * The unique identifier for the physician.
     */
    @Id
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
     * Returns the full name of the physician.
     *
     * @return the full name of the physician
     */
    @JsonIgnore
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
     * Generates the username for the physician (first name + "." + last name).
     *
     * @return the username of the physician
     */
    @JsonIgnore
    public String getUserName() {
        String name = "";
        if (firstName != null && !firstName.isEmpty()) {
            name += firstName;
        }
        if (!name.isEmpty()) {
            name += ".";
        }
        if (lastName != null && !lastName.isEmpty()) {
            name += lastName;
        }
        return name;
    }
}
