package com.example.symptommanagement.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Represents an alert entity in the symptom management application.
 * An alert is generated to notify physicians about a patient's condition severity.
 */
@Data
public class Alert {

    /**
     * Unique identifier for the alert.
     */
    @Id
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

    // Predefined constants for pain severity levels.
    public static final int PAIN_SEVERITY_LEVEL_0 = 0;
    public static final int PAIN_SEVERITY_LEVEL_1 = 10;
    public static final int PAIN_SEVERITY_LEVEL_2 = 30;
    public static final int PAIN_SEVERITY_LEVEL_3 = 90;
    public static final int PAIN_SEVERITY_LEVEL_4 = 100;
}
