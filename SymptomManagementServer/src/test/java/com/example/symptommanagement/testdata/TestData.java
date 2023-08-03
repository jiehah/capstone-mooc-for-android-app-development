package com.example.symptommanagement.testdata;

import com.example.symptommanagement.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;

/**
 * TestData class provides utility methods for generating random test data and performing operations on data objects.
 */
public class TestData {

    /**
     * ObjectMapper instance for JSON serialization and deserialization.
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Variable to store the current random date value.
     */
    private static long randomDate = System.currentTimeMillis();

    /**
     * Generates a random Patient object with the given first name, last name, and birthday.
     *
     * @param first    The first name of the patient.
     * @param last     The last name of the patient.
     * @param birthday The birthday of the patient in the format "MM/dd/yyyy".
     * @return A new random Patient object.
     */
    public static Patient randomPatient(String first, String last, String birthday) {
        Patient p = new Patient();
        p.setFirstName(first);
        p.setLastName(last);
        p.setBirthdate(birthday);
        return p;
    }

    /**
     * Resets the random date value to the current system time.
     */
    public static void resetRandomDate1() {
        randomDate = System.currentTimeMillis();
    }

    /**
     * Generates a random Physician object with the given first name and last name.
     *
     * @param first The first name of the physician.
     * @param last  The last name of the physician.
     * @return A new random Physician object.
     */
    public static Physician randomPhysician(String first, String last) {
        Physician physician = new Physician();
        physician.setFirstName(first);
        physician.setLastName(last);
        return physician;
    }

    /**
     * Generates a random Medication object with the given name.
     *
     * @param name The name of the medication.
     * @return A new random Medication object.
     */
    public static Medication randomMedication(String name) {
        return new Medication(name);
    }

    /**
     * Generates a random PainLog object with random severity and eating values.
     *
     * @return A new random PainLog object.
     */
    public static PainLog randomPainLog() {
        PainLog log = new PainLog();
        randomDate = getHoursFromNow(randomDate, -6);
        log.setCreated(randomDate);
        log.setSeverity(getRandomSeverity());
        log.setEating(getRandomEating());
        return log;
    }

    /**
     * Gets a random Eating value for the PainLog.
     *
     * @return A random Eating value.
     */
    private static PainLog.Eating getRandomEating() {
        int r = randInt(1, 3);
        r *= 100;
        return PainLog.Eating.findByValue(r);
    }

    /**
     * Gets a random Severity value for the PainLog.
     *
     * @return A random Severity value.
     */
    private static PainLog.Severity getRandomSeverity() {
        int r = randInt(1, 3);
        r *= 100;
        return PainLog.Severity.findByValue(r);
    }

    /**
     * Generates a random MedicationLog object for the given Medication.
     *
     * @param med The Medication for which the log is generated.
     * @return A new random MedicationLog object.
     */
    public static MedicationLog randomMedLog(Medication med) {
        MedicationLog log = new MedicationLog();
        randomDate = getHoursFromNow(randomDate, -1);
        log.setCreated(randomDate);
        log.setTaken(randomDate);
        log.setMed(med);
        return log;
    }

    /**
     * Adds a Physician to the Patient's set of physicians.
     *
     * @param physician The Physician to add to the Patient.
     * @param patient   The Patient to whom the Physician is added.
     * @return The updated Patient object.
     */
    public static Patient addPhysicianToPatient(Physician physician, Patient patient) {
        if (patient.getPhysicians() == null) {
            patient.setPhysicians(new HashSet<>());
        }
        patient.getPhysicians().add(physician);
        return patient;
    }

    /**
     * Adds a Medication to the Patient's set of prescriptions.
     *
     * @param medication The Medication to add to the Patient.
     * @param patient    The Patient to whom the Medication is added.
     * @return The updated Patient object.
     */
    public static Patient addPrescriptionToPatient(Medication medication, Patient patient) {
        if (patient.getPrescriptions() == null) {
            patient.setPrescriptions(new HashSet<>());
        }
        patient.getPrescriptions().add(medication);
        return patient;
    }

    /**
     * Adds a PainLog to the Patient's set of pain logs.
     *
     * @param log     The PainLog to add to the Patient.
     * @param patient The Patient to whom the PainLog is added.
     * @return The updated Patient object.
     */
    public static Patient addPainLogToPatient(PainLog log, Patient patient) {
        if (patient.getPainLog() == null) {
            patient.setPainLog(new HashSet<>());
        }
        patient.getPainLog().add(log);
        return patient;
    }

    /**
     * Adds a MedicationLog to the Patient's set of medication logs.
     *
     * @param log     The MedicationLog to add to the Patient.
     * @param patient The Patient to whom the MedicationLog is added.
     * @return The updated Patient object.
     */
    public static Patient addMedLogToPatient(MedicationLog log, Patient patient) {
        if (patient.getMedLog() == null) {
            patient.setMedLog(new HashSet<>());
        }
        patient.getMedLog().add(log);
        return patient;
    }

    /**
     * Adds a StatusLog to the Patient's set of status logs.
     *
     * @param log     The StatusLog to add to the Patient.
     * @param patient The Patient to whom the StatusLog is added.
     * @return The updated Patient object.
     */
    public static Patient addStatusLogToPatient(StatusLog log, Patient patient) {
        if (patient.getStatusLog() == null) {
            patient.setStatusLog(new HashSet<>());
        }
        patient.getStatusLog().add(log);
        return patient;
    }

    /**
     * Calculates the date that is the specified number of hours before or after the given base date.
     *
     * @param date  The base date in milliseconds since the epoch.
     * @param hours The number of hours to add (if positive) or subtract (if negative) from the base date.
     * @return The date in milliseconds since the epoch after adding or subtracting the specified hours.
     */
    public static long getHoursFromNow(long date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.add(Calendar.HOUR, hours);
        return cal.getTimeInMillis();
    }

    /**
     * Generates a random integer within the specified range (inclusive).
     *
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
     * @return A random integer within the specified range.
     */
    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * Converts the given object to its JSON representation.
     *
     * @param o The object to be converted to JSON.
     * @return The JSON representation of the object as a string.
     * @throws Exception If an error occurs during the JSON serialization.
     */
    public static String toJson(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    /**
     * Resets the value of the static variable `randomDate` to the current system time in milliseconds.
     * This is useful to ensure that each time this method is called, the `randomDate` is updated to the current time.
     */
    public static void resetRandomDate() {
        randomDate = System.currentTimeMillis();
    }
}
