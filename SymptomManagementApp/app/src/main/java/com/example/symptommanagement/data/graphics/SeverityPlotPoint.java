package com.example.symptommanagement.data.graphics;

import lombok.Getter;
import lombok.Setter;

/**
 * A class representing a specific data point for severity.
 * Extends the {@link TimePoint} class to inherit time-related properties.
 */
@Getter
@Setter
public class SeverityPlotPoint extends TimePoint {

    /**
     * Severity value
     */
    private int severityValue;

    /**
     * Constructor to create a SeverityPlotPoint with the given timeValue and severityValue.
     *
     * @param timeValue     The time value in milliseconds representing the data point's timestamp.
     * @param severityValue The severity value associated with this data point.
     */
    public SeverityPlotPoint(long timeValue, int severityValue) {
        // Call the constructor of the superclass (TimePoint) to set the time-related properties.
        super(timeValue);

        // Set the severityValue field using the provided value.
        this.severityValue = severityValue;
    }
}
