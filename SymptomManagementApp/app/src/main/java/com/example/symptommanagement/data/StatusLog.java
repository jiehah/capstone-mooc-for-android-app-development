package com.example.symptommanagement.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;


/**
 * Represents a status log entity in the symptom management application.
 * Status logs record notes and image locations related to a patient's condition.
 */
@Data
@NoArgsConstructor
public class StatusLog {

    /**
     * The unique identifier for the status log entry.
     */
    private BigInteger id;

    /**
     * The timestamp of when the status log entry was created.
     */
    private long created;

    /**
     * A note or description associated with the status log entry.
     */
    private String note;

    /**
     * The location of an image related to the status log entry.
     */
    private String image_location;

    /**
     * Creates a new instance of the StatusLog class with the specified note and creation timestamp.
     *
     * @param note    The note or status description associated with this log.
     * @param created The timestamp when the status log was created.
     */
    public StatusLog(String note, long created) {
        this.created = created;
        this.note = note;
    }
}
