package com.example.symptommanagement.data;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a medication entity in the symptom management application.
 * Medications are prescribed to patients for managing their symptoms.
 */
@Data
@NoArgsConstructor
public class Medication {

    /**
     * Unique identifier for the medication.
     */
    private String id;

    /**
     * The name of the medication.
     */
    private String name;

    /**
     * Constructor to create a new Medication object with a given name.
     *
     * @param name The name of the medication.
     */
    public Medication(String name) {
        this.name = name;
    }

    /**
     * Generates a debug-friendly string representation of the Medication object.
     *
     * @return The debug string representing the Medication object.
     */
    public String toDebugString() {
        return "Medication [id=" + id + ", name=" + name + "]";
    }
}
