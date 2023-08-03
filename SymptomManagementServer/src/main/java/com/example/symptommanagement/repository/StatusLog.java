package com.example.symptommanagement.repository;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigInteger;

/**
 * Represents a status log entity in the symptom management application.
 * Status logs record notes and image locations related to a patient's condition.
 */
@Data
public class StatusLog {

    /**
     * The unique identifier for the status log entry.
     */
    @Id
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
}
