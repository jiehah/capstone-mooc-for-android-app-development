package com.example.symptommanagement.repository;

import lombok.Data;

import java.math.BigInteger;
import java.util.Collection;
import java.util.TimeZone;

/**
 * Represents the preferences of a patient in the symptom management application.
 * Patient preferences include timezone, reminders, and notification settings.
 */
@Data
public class PatientPrefs {

    /**
     * The unique identifier for the patient's preferences.
     */
    private BigInteger id;

    /**
     * The timezone preferred by the patient.
     */
    private TimeZone timezone;

    /**
     * The collection of reminders set by the patient.
     */
    private Collection<Reminder> reminders;

    /**
     * Indicates whether notifications are turned on for the patient.
     */
    private boolean notificationOn;
}
