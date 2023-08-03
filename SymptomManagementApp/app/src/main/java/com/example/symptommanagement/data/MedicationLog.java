package com.example.symptommanagement.data;

import lombok.Data;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;


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

    /**
     * Formats the taken date of the medication log entry into a human-readable string representation.
     *
     * @param dateFormat The desired format pattern for the date.
     * @return The formatted taken date string.
     */
    public String getTakenDateFormattedString(String dateFormat) {
        Date date = new Date(taken);
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(date);
    }
}

