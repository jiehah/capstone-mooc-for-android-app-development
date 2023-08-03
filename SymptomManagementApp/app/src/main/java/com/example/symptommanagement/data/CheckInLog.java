package com.example.symptommanagement.data;

import lombok.Data;

/**
 * Represents a check-in log entity in the symptom management application.
 * Check-in logs are used to connect pain and medication logs to a patient.
 */
@Data
public class CheckInLog {

    /**
     * Unique identifier for the check-in log.
     * Use this ID to connect to pain and medication logs.
     */
    private long checkinId;

    /**
     * Timestamp indicating when the check-in log was created.
     */
    private long created;
}
