package com.example.symptommanagement;

import android.content.ContentValues;

import static com.example.symptommanagement.data.PatientCPContract.*;


/**
 * The TestData class provides convenient methods for creating test data in the form of {@link ContentValues}
 * which can be used for testing and populating databases. It contains static methods for generating
 * test data for different entities such as patients, prescriptions, preferences, physicians, reminders,
 * pain logs, medication logs, and status logs.
 * <p>
 * The class includes a set of public static final constants that represent predefined values used in
 * the generated test data, such as TRUE and FALSE for boolean values.
 * <p>
 * Note: This class assumes that the constants and column names for the various entries (e.g., PatientEntry,
 * PrescriptionEntry, PrefsEntry, etc.) are defined and accessible elsewhere in the codebase.
 */
public class TestData {

    /**
     * Unique ID for the test data
     */
    private static final long id = 1111L;

    /**
     * Representing true value for boolean fields
     */
    public static final int TRUE = 1;

    /**
     * Representing false value for boolean fields
     */
    public static final int FALSE = 0;

    /**
     * Creates test data for a patient entry.
     *
     * @param name      The patient's name.
     * @param active    The active status of the patient (1 for active, 0 for inactive).
     * @param lastLogin The timestamp of the last login.
     * @return ContentValues containing the test patient data.
     */
    public static ContentValues createTestPatient(String name, int active, long lastLogin) {
        ContentValues values = new ContentValues();
        // Set patient's first name
        values.put(PatientEntry.COLUMN_FIRST_NAME, name);
        // Set patient's active status
        values.put(PatientEntry.COLUMN_ACTIVE, active);
        // Set patient's last login timestamp
        values.put(PatientEntry.COLUMN_LAST_LOGIN, lastLogin);
        // Set patient ID
        values.put(PatientEntry.COLUMN_PATIENT_ID, id);
        // Set process status for the patient
        values.put(PatientEntry.COLUMN_PROCESS_STATUS, 1);
        // Set current timestamp for processed field
        values.put(PatientEntry.COLUMN_PROCESSED, System.currentTimeMillis());
        return values;
    }

    /**
     * Creates test data for a prescription entry.
     *
     * @param medId The medication ID.
     * @param name  The name of the prescription.
     * @return ContentValues containing the test prescription data.
     */
    public static ContentValues createTestPrescription(long medId, String name) {
        ContentValues values = new ContentValues();
        // Set patient ID for the prescription
        values.put(PrescriptionEntry.COLUMN_PATIENT_ID, id);
        // Set medication ID
        values.put(PrescriptionEntry.COLUMN_MEDICATION_ID, medId);
        // Set prescription name
        values.put(PrescriptionEntry.COLUMN_NAME, name);
        return values;
    }

    /**
     * Creates test data for preferences entry with default values.
     *
     * @return ContentValues containing the test preferences data.
     */
    public static ContentValues createTestPrefs() {
        ContentValues values = new ContentValues();
        // Enable notifications by default
        values.put(PrefsEntry.COLUMN_NOTIFICATION, 1);
        // Set patient ID for the preferences
        values.put(PrefsEntry.COLUMN_PATIENT_ID, id);
        // Set current timestamp for created field
        values.put(PrefsEntry.COLUMN_CREATED, System.currentTimeMillis());
        return values;
    }

    /**
     * Creates test data for preferences entry with timezone setting.
     *
     * @param timezoneString The timezone setting for the patient.
     * @return ContentValues containing the test preferences data with the timezone setting.
     */
    public static ContentValues createTestPrefs(String timezoneString) {
        ContentValues values = createTestPrefs();
        // Set the timezone setting
        values.put(PrefsEntry.COLUMN_TIMEZONE, timezoneString);
        return values;
    }

    /**
     * Creates test data for a physician entry.
     *
     * @param drId The physician's ID.
     * @param name The physician's name.
     * @return ContentValues containing the test physician data.
     */
    public static ContentValues createTestPhysician(long drId, String name) {
        ContentValues values = new ContentValues();
        // Set patient ID for the physician
        values.put(PhysicianEntry.COLUMN_PATIENT_ID, id);
        // Set physician's name
        values.put(PhysicianEntry.COLUMN_NAME, name);
        // Set physician's ID
        values.put(PhysicianEntry.COLUMN_PHYSICIAN_ID, drId);
        return values;
    }

    /**
     * Creates test data for a reminder entry.
     *
     * @param reminderId The reminder's ID.
     * @param isOn       The reminder status (1 for on, 0 for off).
     * @param alarm      The alarm setting for the reminder.
     * @return ContentValues containing the test reminder data.
     */
    public static ContentValues createTestReminder(long reminderId, int isOn, String alarm) {
        ContentValues values = new ContentValues();
        // Set patient ID for the reminder
        values.put(ReminderEntry.COLUMN_PATIENT_ID, id);
        // Set reminder ID
        values.put(ReminderEntry.COLUMN_REMINDER_ID, reminderId);
        // Set current timestamp for created field
        values.put(ReminderEntry.COLUMN_CREATED, System.currentTimeMillis());
        // Set reminder status (on/off)
        values.put(ReminderEntry.COLUMN_ON, isOn);
        // Set alarm setting for the reminder
        values.put(ReminderEntry.COLUMN_ALARM, alarm);
        return values;
    }

    /**
     * Creates test data for a reminder entry with default alarm setting.
     *
     * @param reminderId The reminder's ID.
     * @return ContentValues containing the test reminder data with default alarm setting.
     */
    public static ContentValues createTestReminder(long reminderId) {
        // Use TRUE as default for reminder status and "test.alarm" as default alarm setting
        return createTestReminder(reminderId, TRUE, "test.alarm");
    }

    /**
     * Creates test data for a pain log entry.
     *
     * @param severity The severity level of the pain.
     * @param eating   The eating status (1 for yes, 0 for no).
     * @return ContentValues containing the test pain log data.
     */
    public static ContentValues createTestPainLog(int severity, int eating) {
        ContentValues values = new ContentValues();
        // Set patient ID for the pain log
        values.put(PainLogEntry.COLUMN_PATIENT_ID, id);
        // Set severity level of the pain
        values.put(PainLogEntry.COLUMN_SEVERITY, severity);
        // Set eating status (yes/no)
        values.put(PainLogEntry.COLUMN_EATING, eating);
        // Set current timestamp for created field
        values.put(PainLogEntry.COLUMN_CREATED, System.currentTimeMillis());
        return values;
    }

    /**
     * Creates test data for a medication log entry.
     *
     * @param med The name of the medication.
     * @return ContentValues containing the test medication log data.
     */
    public static ContentValues createTestMedLog(String med) {
        ContentValues values = new ContentValues();
        // Set patient ID for the medication log
        values.put(MedLogEntry.COLUMN_PATIENT_ID, id);
        // Set name of the medication
        values.put(MedLogEntry.COLUMN_MED_NAME, med);
        // Set current timestamp for taken field
        values.put(MedLogEntry.COLUMN_TAKEN, System.currentTimeMillis());
        // Set current timestamp for created field
        values.put(MedLogEntry.COLUMN_CREATED, System.currentTimeMillis());
        return values;
    }

    /**
     * Creates test data for a status log entry.
     *
     * @param note The status log note.
     * @return ContentValues containing the test status log data.
     */
    public static ContentValues createTestStatusLog(String note) {
        ContentValues values = new ContentValues();
        // Set patient ID for the status log
        values.put(StatusLogEntry.COLUMN_PATIENT_ID, id);
        // Set status log note
        values.put(StatusLogEntry.COLUMN_NOTE, note);
        // Set image location for the status log
        values.put(StatusLogEntry.COLUMN_IMAGE, "image_location.jpg");
        // Set current timestamp for created field
        values.put(StatusLogEntry.COLUMN_CREATED, System.currentTimeMillis());
        return values;
    }
}
