package com.example.symptommanagement.data.graphics;

import lombok.Getter;
import lombok.Setter;

/**
 * A class representing a specific data point for medication.
 * Extends the {@link TimePoint} class to inherit time-related properties.
 */
@Getter
@Setter
public class MedicationPlotPoint extends TimePoint {

    /**
     * Medication ID
     */
    private String medId;

    /**
     * Medication name
     */
    private String name;

    /**
     * Constructor to create a MedicationPlotPoint with the given timeValue, medId, and name.
     *
     * @param timeValue The time value in milliseconds representing the data point's timestamp.
     * @param medId     The ID of the medication associated with this data point.
     * @param name      The name of the medication associated with this data point.
     */
    public MedicationPlotPoint(long timeValue, String medId, String name) {
        // Call the constructor of the superclass (TimePoint) to set the time-related properties.
        super(timeValue);

        // Set the medId and name fields using the provided values.
        this.medId = medId;
        this.name = name;
    }
}
