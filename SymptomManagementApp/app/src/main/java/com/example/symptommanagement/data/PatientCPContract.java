package com.example.symptommanagement.data;


import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This class represents the contract for the content provider used in the PatientCP app.
 * It defines the schema of the database, including table names and column names for various data entities.
 * The contract also provides content URIs for accessing different data types within the content provider.
 * Each inner class within the contract represents a table in the database with its respective columns.
 */
public class PatientCPContract {

    /**
     * The content authority uniquely identifies the content provider.
     */
    public static final String CONTENT_AUTHORITY = "com.example.symptommanagement";

    /**
     * The content authority uniquely identifies the content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Constant paths representing different tables in the database.
     */
    public final static String PATIENT_PATH = "patient";
    public final static String PRESCRIPTION_PATH = "prescription";
    public final static String PHYSICIAN_PATH = "physician";
    public final static String PAIN_LOG_PATH = "painlog";
    public final static String MED_LOG_PATH = "medlog";
    public final static String STATUS_LOG_PATH = "statuslog";
    public final static String REMINDER_PATH = "reminder";
    public final static String PREFS_PATH = "pref";
    public final static String CREDENTIAL_PATH = "credential";
    public final static String CHECK_IN_LOG_PATH = "checkinlog";

    /*
     * Inner class representing the "patient" table in the database.
     * It defines the column names and provides content URIs for accessing patient data.
     */
    public static final class PatientEntry implements BaseColumns {

        /*
         * The content URI for accessing patient data.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATIENT_PATH).build();

        /*
         * The MIME type for a list of patient records.
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATIENT_PATH;

        /*
         * The MIME type for a single patient record.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATIENT_PATH;

        /*
         * The name of the table in the database.
         */
        public static final String TABLE_NAME = "patient";

        /*
         * Column names in the "patient" table.
         */
        public static final String COLUMN_PATIENT_ID = "patient_id"; // Server ID
        public static final String COLUMN_LAST_LOGIN = "last_login";
        public static final String COLUMN_BIRTHDATE = "birthdate";
        public static final String COLUMN_ACTIVE = "active";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_PROCESSED = "processed";
        public static final String COLUMN_PROCESS_STATUS = "process_status";

        /*
         * Builds a content URI for a specific patient record with the given ID.
         *
         * @param id The ID of the patient record.
         * @return The content URI for the specific patient record.
         */
        public static Uri buildPatientEntryUriWithPatientId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /*
     * Inner class representing the "credential" table in the database.
     * It defines the column names and provides content URIs for accessing credential data.
     */
    public static final class CredentialEntry implements BaseColumns {

        /*
         * The content URI for accessing credential data.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(CREDENTIAL_PATH).build();

        /*
         * The MIME type for a list of credential records.
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + CREDENTIAL_PATH;

        /*
         * The MIME type for a single credential record.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + CREDENTIAL_PATH;

        /*
         * The name of the table in the database.
         */
        public static final String TABLE_NAME = "credential";

        /*
         * Column names in the "credential" table.
         */
        public static final String COLUMN_USER_ID = "user_id"; // Patient or physician ID
        public static final String COLUMN_LAST_LOGIN = "last_login";
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_USER_TYPE_VALUE = "user_type_value";

        /*
         * Builds a content URI for a specific credential record with the given ID.
         *
         * @param id The ID of the credential record.
         * @return The content URI for the specific credential record.
         */
        public static Uri buildCredentialEntryUriWithDBId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /*
     * Inner class representing the "prescriptions" table in the database.
     * It defines the column names and provides content URIs for accessing prescription data.
     */
    public static final class PrescriptionEntry implements BaseColumns {

        /*
         * The content URI for accessing prescription data.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PRESCRIPTION_PATH).build();

        /*
         * The MIME type for a list of prescription records.
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PRESCRIPTION_PATH;

        /*
         * The MIME type for a single prescription record.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PRESCRIPTION_PATH;

        /*
         * The name of the table in the database.
         */
        public static final String TABLE_NAME = "prescriptions";

        /*
         * Column names in the "prescriptions" table.
         */
        public static final String COLUMN_PATIENT_ID = "patient_id"; // Server ID
        public static final String COLUMN_MEDICATION_ID = "medication_id"; // Server ID
        public static final String COLUMN_NAME = "name";

        /*
         * Builds a content URI for a specific prescription record with the given ID.
         *
         * @param id The ID of the prescription record.
         * @return The content URI for the specific prescription record.
         */
        public static Uri buildPrescriptionEntryUriWithPrescriptionId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /*
     * Inner class representing the "physicians" table in the database.
     * It defines the column names and provides content URIs for accessing physician data.
     */
    public static final class PhysicianEntry implements BaseColumns {

        /*
         * The content URI for accessing physician data.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PHYSICIAN_PATH).build();

        /*
         * The MIME type for a list of physician records.
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PHYSICIAN_PATH;

        /*
         * The MIME type for a single physician record.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PHYSICIAN_PATH;

        /*
         * The name of the table in the database.
         */
        public static final String TABLE_NAME = "physicians";

        /*
         * Column names in the "physicians" table.
         */
        public static final String COLUMN_PATIENT_ID = "patient_id"; // Server ID
        public static final String COLUMN_PHYSICIAN_ID = "physician_id"; // Server ID
        public static final String COLUMN_NAME = "name";

        /*
         * Builds a content URI for a specific physician record with the given ID.
         *
         * @param id The ID of the physician record.
         * @return The content URI for the specific physician record.
         */
        public static Uri buildPhysicianEntryUriWithPhysicianId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /*
     * Inner class representing the "checkinlogs" table in the database.
     * It defines the column names and provides content URIs for accessing check-in log data.
     */
    public static final class CheckInLogEntry implements BaseColumns {

        /*
         * The content URI for accessing check-in log data.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(CHECK_IN_LOG_PATH).build();

        /*
         * The MIME type for a list of check-in log records.
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + CHECK_IN_LOG_PATH;

        /*
         * The MIME type for a single check-in log record.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + CHECK_IN_LOG_PATH;

        /*
         * The name of the table in the database.
         */
        public static final String TABLE_NAME = "checkinlogs";

        /*
         * Column names in the "checkinlogs" table.
         */
        public static final String COLUMN_PATIENT_ID = "patient_id"; // Server ID
        public static final String COLUMN_CHECKIN_ID = "checkinId"; // Connects to pain & med logs
        public static final String COLUMN_CREATED = "created";

        /*
         * Builds a content URI for a specific check-in log record with the given ID.
         *
         * @param id The ID of the check-in log record.
         * @return The content URI for the specific check-in log record.
         */
        public static Uri buildCheckinLogEntryUriWithLogId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /*
     * Inner class representing the "painlogs" table in the database.
     * It defines the column names and provides content URIs for accessing pain log data.
     */
    public static final class PainLogEntry implements BaseColumns {

        /*
         * The content URI for accessing pain log data.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PAIN_LOG_PATH).build();

        /*
         * The MIME type for a list of pain log records.
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PAIN_LOG_PATH;

        /*
         * The MIME type for a single pain log record.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PAIN_LOG_PATH;

        /*
         * The name of the table in the database.
         */
        public static final String TABLE_NAME = "painlogs";

        /*
         * Column names in the "painlogs" table.
         */
        public static final String COLUMN_PATIENT_ID = "patient_id"; // Server ID
        public static final String COLUMN_PAIN_LOG_ID = "log_id"; // Server ID
        public static final String COLUMN_SEVERITY = "severity";
        public static final String COLUMN_EATING = "eating";
        public static final String COLUMN_CHECKIN_ID = "checkinId"; // Connects to checkin & med logs
        public static final String COLUMN_CREATED = "created";

        /*
         * Builds a content URI for a specific pain log record with the given ID.
         *
         * @param id The ID of the pain log record.
         * @return The content URI for the specific pain log record.
         */
        public static Uri buildPainLogEntryUriWithLogId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /*
     * Inner class representing the "medlogs" table in the database.
     * It defines the column names and provides content URIs for accessing medication log data.
     */
    public static final class MedLogEntry implements BaseColumns {

        /*
         * The content URI for accessing medication log data.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(MED_LOG_PATH).build();

        /*
         * The MIME type for a list of medication log records.
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + MED_LOG_PATH;

        /*
         * The MIME type for a single medication log record.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + MED_LOG_PATH;

        /*
         * The name of the table in the database.
         */
        public static final String TABLE_NAME = "medlogs";

        /*
         * Column names in the "medlogs" table.
         */
        public static final String COLUMN_PATIENT_ID = "patient_id"; // Server ID
        public static final String COLUMN_MED_ID = "med_id"; // Server ID
        public static final String COLUMN_MED_NAME = "med_name";
        public static final String COLUMN_TAKEN = "taken";
        public static final String COLUMN_CHECKIN_ID = "checkinId"; // Connects to pain & checkin logs
        public static final String COLUMN_CREATED = "created";

        /*
         * Builds a content URI for a specific medication log record with the given ID.
         *
         * @param id The ID of the medication log record.
         * @return The content URI for the specific medication log record.
         */
        public static Uri buildMedLogEntryUriWithLogId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /*
     * Inner class representing the "statuslogs" table in the database.
     * It defines the column names and provides content URIs for accessing status log data.
     */
    public static final class StatusLogEntry implements BaseColumns {

        /*
         * The content URI for accessing status log data.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(STATUS_LOG_PATH).build();

        /*
         * The MIME type for a list of status log records.
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + STATUS_LOG_PATH;

        /*
         * The MIME type for a single status log record.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + STATUS_LOG_PATH;

        /*
         * The name of the table in the database.
         */
        public static final String TABLE_NAME = "statuslogs";

        /*
         * Column names in the "statuslogs" table.
         */
        public static final String COLUMN_PATIENT_ID = "patient_id"; // Server ID
        public static final String COLUMN_STATUS_LOG_ID = "log_id"; // Server ID
        public static final String COLUMN_NOTE = "note";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_CREATED = "created";

        /*
         * Builds a content URI for a specific status log record with the given ID.
         *
         * @param id The ID of the status log record.
         * @return The content URI for the specific status log record.
         */
        public static Uri buildStatusLogEntryUriWithLogId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /*
     * Inner class representing the "reminders" table in the database.
     * It defines the column names and provides content URIs for accessing reminder data.
     */
    public static final class ReminderEntry implements BaseColumns {

        /*
         * The content URI for accessing reminder data.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(REMINDER_PATH).build();

        /*
         * The MIME type for a list of reminder records.
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + REMINDER_PATH;

        /*
         * The MIME type for a single reminder record.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + REMINDER_PATH;

        /*
         * The name of the table in the database.
         */
        public static final String TABLE_NAME = "reminders";

        /*
         * Column names in the "reminders" table.
         */
        public static final String COLUMN_PATIENT_ID = "patient_id"; // Server ID
        public static final String COLUMN_REMINDER_ID = "reminder_id"; // Server ID
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DAY = "day";
        public static final String COLUMN_HOUR = "hour";
        public static final String COLUMN_MINUTES = "minutes";
        public static final String COLUMN_ALARM = "alarm";
        public static final String COLUMN_ON = "isOn";
        public static final String COLUMN_CREATED = "created";

        /*
         * Builds a content URI for a specific reminder record with the given ID.
         *
         * @param id The ID of the reminder record.
         * @return The content URI for the specific reminder record.
         */
        public static Uri buildReminderEntryUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /*
     * Inner class representing the "prefs" table in the database.
     * It defines the column names and provides content URIs for accessing preferences data.
     */
    public static final class PrefsEntry implements BaseColumns {

        /*
         * The content URI for accessing preferences data.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PREFS_PATH).build();

        /*
         * The MIME type for a list of preferences records.
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PREFS_PATH;

        /*
         * The MIME type for a single preferences record.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PREFS_PATH;

        /*
         * The name of the table in the database.
         */
        public static final String TABLE_NAME = "prefs";

        /*
         * Column names in the "prefs" table.
         */
        public static final String COLUMN_PATIENT_ID = "patient_id"; // Server ID
        public static final String COLUMN_PREF_ID = "pref_id"; // Server ID
        public static final String COLUMN_NOTIFICATION = "notification";
        public static final String COLUMN_TIMEZONE = "timezone";
        public static final String COLUMN_CREATED = "created";

        /*
         * Builds a content URI for a specific preferences record with the given ID.
         *
         * @param id The ID of the preferences record.
         * @return The content URI for the specific preferences record.
         */
        public static Uri buildPrefsEntryUriWithPrefId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
