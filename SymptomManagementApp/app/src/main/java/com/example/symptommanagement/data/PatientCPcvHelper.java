package com.example.symptommanagement.data;

import android.content.ContentValues;

/**
 * Helper class to create ContentValues objects for patient-related data.
 * These objects are used for inserting or updating data in the ContentProvider.
 */
public class PatientCPcvHelper {

    /**
     * Create a ContentValues object for patient data.
     * Used for updating patient data in the ContentProvider.
     *
     * @param id      The patient ID.
     * @param patient The Patient object containing patient data.
     * @return ContentValues object with patient data.
     */
    public static ContentValues createValuesObject(String id, Patient patient) {
        ContentValues cv = new ContentValues();
        cv.put(PatientCPContract.PatientEntry._ID, patient.getDbId());
        cv.put(PatientCPContract.PatientEntry.COLUMN_PATIENT_ID, id);
        cv.put(PatientCPContract.PatientEntry.COLUMN_LAST_LOGIN, patient.getLastLogin());
        cv.put(PatientCPContract.PatientEntry.COLUMN_LAST_NAME, patient.getLastName());
        cv.put(PatientCPContract.PatientEntry.COLUMN_FIRST_NAME, patient.getFirstName());
        cv.put(PatientCPContract.PatientEntry.COLUMN_BIRTHDATE, patient.getBirthdate());
        return cv;
    }

    /**
     * Create a ContentValues object for patient data.
     * Used for inserting patient data into the ContentProvider.
     *
     * @param id      The patient ID.
     * @param patient The Patient object containing patient data.
     * @return ContentValues object with patient data.
     */
    public static ContentValues createInsertValuesObject(String id, Patient patient) {
        ContentValues cv = new ContentValues();
        cv.put(PatientCPContract.PatientEntry.COLUMN_PATIENT_ID, id);
        cv.put(PatientCPContract.PatientEntry.COLUMN_LAST_LOGIN, patient.getLastLogin());
        cv.put(PatientCPContract.PatientEntry.COLUMN_LAST_NAME, patient.getLastName());
        cv.put(PatientCPContract.PatientEntry.COLUMN_FIRST_NAME, patient.getFirstName());
        cv.put(PatientCPContract.PatientEntry.COLUMN_BIRTHDATE, patient.getBirthdate());
        return cv;
    }

    public static ContentValues createValuesObject(String id, UserCredential credential) {
        ContentValues cv = new ContentValues();
        cv.put(PatientCPContract.CredentialEntry._ID, credential.getDbId());
        cv.put(PatientCPContract.CredentialEntry.COLUMN_USER_ID, id);
        cv.put(PatientCPContract.CredentialEntry.COLUMN_LAST_LOGIN, credential.getLastLogin());
        cv.put(PatientCPContract.CredentialEntry.COLUMN_USER_NAME, credential.getUserName());
        cv.put(PatientCPContract.CredentialEntry.COLUMN_PASSWORD, credential.getPassword());
        cv.put(PatientCPContract.CredentialEntry.COLUMN_USER_TYPE_VALUE, credential.getUserRoleValue());
        return cv;
    }

    /**
     * Creates a ContentValues object for inserting UserCredential data into the database.
     *
     * @param id         The ID of the patient associated with the credential.
     * @param credential The UserCredential object containing the data to be added to the ContentValues.
     * @return The ContentValues object containing the UserCredential data for insertion.
     */
    public static ContentValues createInsertValuesObject(String id, UserCredential credential) {
        ContentValues cv = new ContentValues();
        cv.put(PatientCPContract.CredentialEntry.COLUMN_USER_ID, id);
        cv.put(PatientCPContract.CredentialEntry.COLUMN_LAST_LOGIN, credential.getLastLogin());
        cv.put(PatientCPContract.CredentialEntry.COLUMN_USER_NAME, credential.getUserName());
        cv.put(PatientCPContract.CredentialEntry.COLUMN_PASSWORD, credential.getPassword());
        cv.put(PatientCPContract.CredentialEntry.COLUMN_USER_TYPE_VALUE, credential.getUserRoleValue());
        return cv;
    }

    /**
     * Creates a ContentValues object for inserting MedicationLog data into the database.
     *
     * @param id  The ID of the patient associated with the medication log.
     * @param log The MedicationLog object containing the data to be added to the ContentValues.
     * @return The ContentValues object containing the MedicationLog data for insertion.
     */
    public static ContentValues createValuesObject(String id, MedicationLog log) {
        ContentValues cv = new ContentValues();
        cv.put(PatientCPContract.MedLogEntry.COLUMN_MED_NAME, log.getMed().getName());
        cv.put(PatientCPContract.MedLogEntry.COLUMN_MED_ID, log.getMed().getId());
        cv.put(PatientCPContract.MedLogEntry.COLUMN_PATIENT_ID, id);
        cv.put(PatientCPContract.MedLogEntry.COLUMN_TAKEN, log.getTaken());
        cv.put(PatientCPContract.MedLogEntry.COLUMN_CHECKIN_ID, log.getCheckinId());
        long thisTime = log.getCreated();
        if (thisTime <= 0L) {
            thisTime = System.currentTimeMillis();
        }
        cv.put(PatientCPContract.MedLogEntry.COLUMN_CREATED, thisTime);
        return cv;
    }

    /**
     * Creates a ContentValues object for inserting PainLog data into the database.
     *
     * @param id  The ID of the patient associated with the pain log.
     * @param log The PainLog object containing the data to be added to the ContentValues.
     * @return The ContentValues object containing the PainLog data for insertion.
     */
    public static ContentValues createValuesObject(String id, PainLog log) {
        ContentValues cv = new ContentValues();
        cv.put(PatientCPContract.PainLogEntry.COLUMN_EATING, log.getEating().getValue());
        cv.put(PatientCPContract.PainLogEntry.COLUMN_SEVERITY, log.getSeverity().getValue());
        cv.put(PatientCPContract.PainLogEntry.COLUMN_PATIENT_ID, id);
        cv.put(PatientCPContract.PainLogEntry.COLUMN_CHECKIN_ID, log.getCheckinId());
        long thisTime = log.getCreated();
        if (thisTime <= 0L) {
            thisTime = System.currentTimeMillis();
        }
        cv.put(PatientCPContract.PainLogEntry.COLUMN_CREATED, thisTime);
        return cv;
    }

    /**
     * Creates a ContentValues object for inserting CheckInLog data into the database.
     *
     * @param id  The ID of the patient associated with the check-in log.
     * @param log The CheckInLog object containing the data to be added to the ContentValues.
     * @return The ContentValues object containing the CheckInLog data for insertion.
     */
    public static ContentValues createValuesObject(String id, CheckInLog log) {
        ContentValues cv = new ContentValues();
        long checkinId = log.getCheckinId();
        if (checkinId <= 0L) {
            checkinId = System.currentTimeMillis();
        }
        cv.put(PatientCPContract.CheckInLogEntry.COLUMN_CHECKIN_ID, checkinId);
        cv.put(PatientCPContract.CheckInLogEntry.COLUMN_PATIENT_ID, id);
        cv.put(PatientCPContract.CheckInLogEntry.COLUMN_CREATED, checkinId);
        return cv;
    }

    /**
     * Creates a ContentValues object for inserting StatusLog data into the database.
     *
     * @param id  The ID of the patient associated with the status log.
     * @param log The StatusLog object containing the data to be added to the ContentValues.
     * @return The ContentValues object containing the StatusLog data for insertion.
     */
    public static ContentValues createValuesObject(String id, StatusLog log) {
        ContentValues cv = new ContentValues();
        cv.put(PatientCPContract.StatusLogEntry.COLUMN_NOTE, log.getNote());
        cv.put(PatientCPContract.StatusLogEntry.COLUMN_IMAGE, log.getImage_location());
        cv.put(PatientCPContract.StatusLogEntry.COLUMN_PATIENT_ID, id);
        long thisTime = log.getCreated();
        if (thisTime <= 0L) {
            thisTime = System.currentTimeMillis();
        }
        cv.put(PatientCPContract.StatusLogEntry.COLUMN_CREATED, thisTime);
        return cv;
    }

    /**
     * Creates a ContentValues object for inserting Reminder data into the database.
     *
     * @param id  The ID of the patient associated with the reminder.
     * @param rem The Reminder object containing the data to be added to the ContentValues.
     * @return The ContentValues object containing the Reminder data for insertion.
     */
    public static ContentValues createValuesObject(String id, Reminder rem) {
        ContentValues cv = new ContentValues();
        cv.put(PatientCPContract.ReminderEntry._ID, rem.getDbId());
        cv.put(PatientCPContract.ReminderEntry.COLUMN_ON, (rem.isOn() ? 1 : 0));
        cv.put(PatientCPContract.ReminderEntry.COLUMN_HOUR, rem.getHour());
        cv.put(PatientCPContract.ReminderEntry.COLUMN_PATIENT_ID, id);
        cv.put(PatientCPContract.ReminderEntry.COLUMN_MINUTES, rem.getMinutes());
        cv.put(PatientCPContract.ReminderEntry.COLUMN_NAME, rem.getName());
        long thisTime = rem.getCreated();
        if (thisTime <= 0L) {
            thisTime = System.currentTimeMillis();
        }
        cv.put(PatientCPContract.StatusLogEntry.COLUMN_CREATED, thisTime);
        return cv;
    }

    /**
     * Creates a ContentValues object for inserting Reminder data into the database.
     * Note: This method is used specifically for inserting new Reminder records.
     *
     * @param id  The ID of the patient associated with the reminder.
     * @param rem The Reminder object containing the data to be added to the ContentValues.
     * @return The ContentValues object containing the Reminder data for insertion.
     */
    public static ContentValues createInsertValuesObject(String id, Reminder rem) {
        ContentValues cv = new ContentValues();
        cv.put(PatientCPContract.ReminderEntry.COLUMN_ON, (rem.isOn() ? 1 : 0));
        cv.put(PatientCPContract.ReminderEntry.COLUMN_HOUR, rem.getHour());
        cv.put(PatientCPContract.ReminderEntry.COLUMN_PATIENT_ID, id);
        cv.put(PatientCPContract.ReminderEntry.COLUMN_MINUTES, rem.getMinutes());
        cv.put(PatientCPContract.ReminderEntry.COLUMN_NAME, rem.getName());
        long thisTime = rem.getCreated();
        if (thisTime <= 0L) {
            thisTime = System.currentTimeMillis();
        }
        cv.put(PatientCPContract.StatusLogEntry.COLUMN_CREATED, thisTime);
        return cv;
    }

    /**
     * Creates a ContentValues object for inserting Medication data into the database.
     * Note: This method is used specifically for inserting new Medication records.
     *
     * @param id  The ID of the patient associated with the medication.
     * @param med The Medication object containing the data to be added to the ContentValues.
     * @return The ContentValues object containing the Medication data for insertion.
     */
    public static ContentValues createValuesObject(String id, Medication med) {
        ContentValues cv = new ContentValues();
        cv.put(PatientCPContract.PrescriptionEntry.COLUMN_NAME, med.getName());
        cv.put(PatientCPContract.PrescriptionEntry.COLUMN_MEDICATION_ID, med.getId());
        cv.put(PatientCPContract.PrescriptionEntry.COLUMN_PATIENT_ID, id);
        return cv;
    }
}
