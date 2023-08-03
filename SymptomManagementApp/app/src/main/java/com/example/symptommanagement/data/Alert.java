package com.example.symptommanagement.data;

import lombok.Data;

/**
 * Represents an alert entity in the symptom management application.
 * An alert is generated to notify physicians about a patient's condition severity.
 */
@Data
public class Alert {

    /**
     * Unique identifier for the alert.
     */
    String id;

    /**
     * ID of the physician associated with this alert.
     */
    String physicianId;

    /**
     * ID of the patient associated with this alert.
     */
    String patientId;

    /**
     * Name of the patient associated with this alert.
     */
    String patientName;

    /**
     * Timestamp indicating when the alert was created.
     */
    long created;

    /**
     * Severity level of the alert (0 to 100).
     */
    int severityLevel;

    /**
     * Timestamp indicating when the physician was contacted.
     */
    long physicianContacted = 0L;

    /**
     * Predefined constants for pain severity levels.
     */
    public static final int PAIN_SEVERITY_LEVEL_0 = 0;
    public static final int PAIN_SEVERITY_LEVEL_1 = 10;
    public static final int PAIN_SEVERITY_LEVEL_2 = 30;
    public static final int PAIN_SEVERITY_LEVEL_3 = 90;
    public static final int PAIN_SEVERITY_LEVEL_4 = 100;

    /**
     * Constructs a formatted message describing severe symptoms for a patient.
     *
     * @return The formatted message with the patient's name and symptom severity.
     */
    public String getFormattedMessage() {
        return patientName + " has severe symptoms.";
    }
}
