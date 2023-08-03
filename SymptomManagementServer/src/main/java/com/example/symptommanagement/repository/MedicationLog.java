package com.example.symptommanagement.repository;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigInteger;

/**
 * The MedicationLog class represents a medication log entry, which records information
 * about a medication taken by a patient at a specific time.
 */
@Data
public class MedicationLog {

    /**
     * The ID of the medication log entry.
     */
    private BigInteger id;

    /**
     * The timestamp when the medication log entry was created.
     */
    private long created;

    /**
     * The medication associated with the log entry.
     */
    private Medication med;

    /**
     * The timestamp when the medication was taken.
     */
    private long taken;

    /**
     * The ID of the associated check-in entry.
     */
    private long checkinId;
}

