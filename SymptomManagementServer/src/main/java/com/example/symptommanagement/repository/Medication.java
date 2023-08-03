package com.example.symptommanagement.repository;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

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
    @Id
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
}
