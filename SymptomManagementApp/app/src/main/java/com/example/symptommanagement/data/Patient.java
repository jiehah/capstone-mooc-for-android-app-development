package com.example.symptommanagement.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * The Patient class represents a patient's information, including personal details, medical records,
 * and logs associated with the patient's condition and interactions with the system.
 */
@Data
@NoArgsConstructor
public class Patient {

    /**
     * The unique identifier for the patient (medical record ID).
     */
    private String id;

    /**
     * Local transient DB Id
     */
    private transient long dbId;

    /**
     * The first name of the patient.
     */
    private String firstName;

    /**
     * The last name of the patient.
     */
    private String lastName;

    /**
     * The birthdate of the patient.
     */
    private String birthdate;

    /**
     * The timestamp of the patient's last login.
     */
    private long lastLogin;

    /**
     * Indicates if the patient is active or not.
     */
    private Boolean active;

    /**
     * The severity level of the patient's condition.
     */
    private int severityLevel;

    /**
     * The patient's preferences.
     */
    private PatientPrefs prefs;

    /**
     * The set of medications prescribed to the patient.
     */
    private Set<Medication> prescriptions;

    /**
     * The set of physicians associated with the patient.
     */
    private Set<Physician> physicians;

    /**
     * The set of pain logs recorded for the patient.
     */
    private Set<PainLog> painLog;

    /**
     * The set of medication logs recorded for the patient.
     */
    private Set<MedicationLog> medLog;

    /**
     * The set of status logs recorded for the patient.
     */
    private Set<StatusLog> statusLog;

    /**
     * The set of check-in logs recorded for the patient.
     */
    private Set<CheckInLog> checkinLog;

    /**
     * Constructor for the Patient class with first name and last name provided.
     *
     * @param firstName The first name of the patient.
     * @param lastName  The last name of the patient.
     */
    public Patient(String firstName, String lastName) {
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.birthdate = "";
    }

    /**
     * Get the full name of the patient.
     *
     * @return The full name of the patient (first name + last name).
     */
    public String getName() {
        String name = "";
        if (firstName != null && !firstName.isEmpty()) name += firstName;
        if (!name.isEmpty()) name += " ";
        if (lastName != null && !lastName.isEmpty()) name += lastName;
        return name;
    }

    /**
     * Generate a user name for the patient based on first name and last name.
     *
     * @return The user name generated from the first name and last name (e.g., "John.Doe").
     */
    public String getUserName() {
        String name = "";
        if (firstName != null && !firstName.isEmpty()) name += firstName;
        if (!name.isEmpty()) name += ".";
        if (lastName != null && !lastName.isEmpty()) name += lastName;
        return name;
    }

    /**
     * Get a debug string representation of the patient object.
     *
     * @return A string representation of the patient object for debugging purposes.
     */
    public String toDebugString() {
        return this.toString();
    }

    /**
     * Formats the given timestamp (dt) into a human-readable date string using the provided format (fmt).
     *
     * @param dt  The timestamp to be formatted.
     * @param fmt The desired format pattern for the date.
     * @return The formatted date string.
     */
    public String getFormattedDate(long dt, String fmt) {
        if (dt <= 0L) return "";
        Date date = new Date(dt);
        SimpleDateFormat format = new SimpleDateFormat(fmt);
        return format.format(date);
    }

    /**
     * Get the patient's last login timestamp in a human-readable formatted string.
     *
     * @return The formatted last login date string.
     */
    public String getFormattedLastLogged() {
        return getFormattedDate(this.lastLogin, "E, MMM d yyyy 'at' hh:mm a");
    }

    /**
     * Override toString for ListAdapter
     *
     * @return A string representation of the physician's full name for ListAdapter
     */
    @Override
    public String toString() {
        return getName();
    }
}
