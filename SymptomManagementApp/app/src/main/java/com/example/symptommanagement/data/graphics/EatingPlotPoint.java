package com.example.symptommanagement.data.graphics;

import lombok.Getter;
import lombok.Setter;

/**
 * A class representing a specific data point for eating.
 * Extends the {@link TimePoint} class to inherit time-related properties.
 */
@Getter
@Setter
public class EatingPlotPoint extends TimePoint {

    /**
     * Eating value
     */
    private int eatingValue;

    /**
     * Constructor to create an EatingPlotPoint with the given timeValue and eatingValue.
     *
     * @param timeValue   The time value in milliseconds representing the data point's timestamp.
     * @param eatingValue The eating value associated with this data point.
     */
    public EatingPlotPoint(long timeValue, int eatingValue) {
        // Call the constructor of the superclass (TimePoint) to set the time-related properties.
        super(timeValue);

        // Set the eatingValue field using the provided value.
        this.eatingValue = eatingValue;
    }
}
