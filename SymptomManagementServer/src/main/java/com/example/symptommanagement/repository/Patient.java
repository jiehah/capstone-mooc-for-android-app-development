package com.example.symptommanagement.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Set;

/**
 * Represents a patient entity in the symptom management application.
 * Patients are individuals who experience symptoms and receive medical care.
 */
@Data
public class Patient {

    /**
     * The unique identifier for the patient (medical record ID).
     */
    @Id
    private String id;

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
     * Returns the full name of the patient.
     *
     * @return the full name of the patient
     */
    @JsonIgnore
    public String getName() {
        String name = "";
        if (firstName != null && !firstName.isEmpty()) {
            name += firstName;
        }
        if (!name.isEmpty()) {
            name += " ";
        }
        if (lastName != null && !lastName.isEmpty()) {
            name += lastName;
        }
        return name;
    }

    /**
     * Generates the username for the patient (first name + "." + last name).
     *
     * @return the username of the patient
     */
    @JsonIgnore
    public String getUserName() {
        String name = "";
        if (firstName != null && !firstName.isEmpty()) {
            name += firstName;
        }
        if (!name.isEmpty()) {
            name += ".";
        }
        if (lastName != null && !lastName.isEmpty()) {
            name += lastName;
        }
        return name;
    }

    /**
     * Creates a copy of the patient's data with only essential fields (id, name, and birthdate)
     * to be used in the physician's record.
     *
     * @param p patient to copy
     * @return copy of p with only id, name, and birthdate
     */
    public static synchronized Patient cloneForPhysician(Patient p) {
        Patient p2 = new Patient();
        p2.setId(p.getId());
        p2.setFirstName(p.getFirstName());
        p2.setLastName(p.getLastName());
        p2.setBirthdate(p.getBirthdate());
        return p2;
    }
}
