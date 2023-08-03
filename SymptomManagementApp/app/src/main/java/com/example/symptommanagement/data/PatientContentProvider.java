package com.example.symptommanagement.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.example.symptommanagement.data.PatientCPContract.*;

/**
 * The PatientContentProvider class is a content provider used to interact with the app's database.
 * It provides CRUD (Create, Read, Update, Delete) operations for different data tables in the database.
 */
public class PatientContentProvider extends ContentProvider {

    /**
     * Define the UriMatcher for matching URIs to their corresponding integer codes
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Database Helper instance
     */
    private PatientDBHelper openHelper;

    /**
     * Define integer codes for each table to be used by the UriMatcher
     */
    private static final int PATIENT = 100;
    private static final int PRESCRIPTION = 200;
    private static final int PRESCRIPTION_ID = 210;
    private static final int PHYSICIAN = 300;
    private static final int PHYSICIAN_ID = 310;
    private static final int PREF = 400;
    private static final int REMINDER = 500;
    private static final int REMINDER_ID = 510;
    private static final int PAIN_LOG = 600;
    private static final int PAIN_LOG_ID = 610;
    private static final int MED_LOG = 700;
    private static final int MED_LOG_ID = 710;
    private static final int STATUS_LOG = 800;
    private static final int STATUS_LOG_ID = 810;
    private static final int CREDENTIAL = 900;
    private static final int CREDENTIAL_ID = 910;
    private static final int CHECK_IN_LOG = 1100;
    private static final int CHECK_IN_LOG_ID = 1111;

    /**
     * Called when the content provider is created.
     * Initializes the database helper.
     *
     * @return true if the content provider was successfully created.
     */
    @Override
    public boolean onCreate() {
        openHelper = new PatientDBHelper(getContext());
        return true;
    }

    /**
     * Handles query (read) operations on the database.
     * Performs a database query based on the given URI, projection, selection, and sort order.
     *
     * @param uri           The URI to query.
     * @param projection    The list of columns to return.
     * @param selection     The selection criteria for the query.
     * @param selectionArgs The selection arguments for the query.
     * @param sortOrder     The sort order for the query results.
     * @return A Cursor containing the query results.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case PATIENT:
                retCursor = openHelper.getReadableDatabase().query(
                        PatientEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CREDENTIAL:
                retCursor = openHelper.getReadableDatabase().query(
                        CredentialEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CREDENTIAL_ID:
                retCursor = openHelper.getReadableDatabase().query(
                        CredentialEntry.TABLE_NAME,
                        projection,
                        CredentialEntry._ID + "=" + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            case PREF:
                retCursor = openHelper.getReadableDatabase().query(
                        PrefsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PRESCRIPTION:
                retCursor = openHelper.getReadableDatabase().query(
                        PrescriptionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PRESCRIPTION_ID:
                retCursor = openHelper.getReadableDatabase().query(
                        PrescriptionEntry.TABLE_NAME,
                        projection,
                        PrescriptionEntry._ID + "=" + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            case PHYSICIAN:
                retCursor = openHelper.getReadableDatabase().query(
                        PhysicianEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PHYSICIAN_ID:
                retCursor = openHelper.getReadableDatabase().query(
                        PhysicianEntry.TABLE_NAME,
                        projection,
                        PhysicianEntry._ID + "=" + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            case REMINDER:
                retCursor = openHelper.getReadableDatabase().query(
                        ReminderEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case REMINDER_ID:
                retCursor = openHelper.getReadableDatabase().query(
                        ReminderEntry.TABLE_NAME,
                        projection,
                        ReminderEntry._ID + "=" + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            case CHECK_IN_LOG:
                retCursor = openHelper.getReadableDatabase().query(
                        CheckInLogEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CHECK_IN_LOG_ID:
                retCursor = openHelper.getReadableDatabase().query(
                        CheckInLogEntry.TABLE_NAME,
                        projection,
                        CheckInLogEntry._ID + "=" + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            case PAIN_LOG:
                retCursor = openHelper.getReadableDatabase().query(
                        PainLogEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PAIN_LOG_ID:
                retCursor = openHelper.getReadableDatabase().query(
                        PainLogEntry.TABLE_NAME,
                        projection,
                        PainLogEntry._ID + "=" + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            case MED_LOG:
                retCursor = openHelper.getReadableDatabase().query(
                        MedLogEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MED_LOG_ID:
                retCursor = openHelper.getReadableDatabase().query(
                        MedLogEntry.TABLE_NAME,
                        projection,
                        MedLogEntry._ID + "=" + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            case STATUS_LOG:
                retCursor = openHelper.getReadableDatabase().query(
                        StatusLogEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case STATUS_LOG_ID:
                retCursor = openHelper.getReadableDatabase().query(
                        StatusLogEntry.TABLE_NAME,
                        projection,
                        StatusLogEntry._ID + "=" + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: + uri");

        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /**
     * Get the MIME type of the data associated with a given URI.
     * The MIME type describes the type of data that the content provider can return for the URI.
     *
     * @param uri The URI for which to retrieve the MIME type.
     * @return The MIME type of the data associated with the URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case PATIENT:
                return PatientEntry.CONTENT_ITEM_TYPE;
            case CREDENTIAL:
                return CredentialEntry.CONTENT_ITEM_TYPE;
            case CREDENTIAL_ID:
                return CredentialEntry.CONTENT_ITEM_TYPE;
            case PREF:
                return PrefsEntry.CONTENT_ITEM_TYPE;
            case PRESCRIPTION:
                return PrescriptionEntry.CONTENT_TYPE;
            case PRESCRIPTION_ID:
                return PrescriptionEntry.CONTENT_ITEM_TYPE;
            case PHYSICIAN:
                return PhysicianEntry.CONTENT_TYPE;
            case PHYSICIAN_ID:
                return PhysicianEntry.CONTENT_ITEM_TYPE;
            case REMINDER:
                return ReminderEntry.CONTENT_TYPE;
            case REMINDER_ID:
                return ReminderEntry.CONTENT_ITEM_TYPE;
            case CHECK_IN_LOG:
                return CheckInLogEntry.CONTENT_TYPE;
            case CHECK_IN_LOG_ID:
                return CheckInLogEntry.CONTENT_ITEM_TYPE;
            case PAIN_LOG:
                return PainLogEntry.CONTENT_TYPE;
            case PAIN_LOG_ID:
                return PainLogEntry.CONTENT_ITEM_TYPE;
            case MED_LOG:
                return MedLogEntry.CONTENT_TYPE;
            case MED_LOG_ID:
                return MedLogEntry.CONTENT_ITEM_TYPE;
            case STATUS_LOG:
                return StatusLogEntry.CONTENT_TYPE;
            case STATUS_LOG_ID:
                return StatusLogEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    /**
     * Insert a new row into the appropriate table in the database.
     *
     * @param uri           The URI representing the table where the data should be inserted.
     * @param contentValues The content values to be inserted into the database.
     * @return The URI of the newly inserted row.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        Uri returnUri;
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case PATIENT: {
                long _id = db.insert(PatientEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = PatientEntry.buildPatientEntryUriWithPatientId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case CREDENTIAL: {
                long _id = db.insert(CredentialEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = CredentialEntry.buildCredentialEntryUriWithDBId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case PREF: {
                long _id = db.insert(PrefsEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = PrefsEntry.buildPrefsEntryUriWithPrefId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case PRESCRIPTION: {
                long _id = db.insert(PrescriptionEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = PrescriptionEntry.buildPrescriptionEntryUriWithPrescriptionId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case PHYSICIAN: {
                long _id = db.insert(PhysicianEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = PhysicianEntry.buildPhysicianEntryUriWithPhysicianId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REMINDER: {
                long _id = db.insert(ReminderEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = ReminderEntry.buildReminderEntryUriWithId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case PAIN_LOG: {
                long _id = db.insert(PainLogEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = PainLogEntry.buildPainLogEntryUriWithLogId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case CHECK_IN_LOG: {
                long _id = db.insert(CheckInLogEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = CheckInLogEntry.buildCheckinLogEntryUriWithLogId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case MED_LOG: {
                long _id = db.insert(MedLogEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = MedLogEntry.buildMedLogEntryUriWithLogId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case STATUS_LOG: {
                long _id = db.insert(StatusLogEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = StatusLogEntry.buildStatusLogEntryUriWithLogId(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: + uri");

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    /**
     * Delete rows from the appropriate table in the database.
     *
     * @param uri           The URI representing the table from which data should be deleted.
     * @param selection     A filter declaring which rows should be deleted.
     * @param selectionArgs The values for the placeholders in the selection filter.
     * @return The number of rows deleted.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case PATIENT:
                rowsDeleted = db.delete(PatientEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CREDENTIAL:
                rowsDeleted = db.delete(CredentialEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PREF:
                rowsDeleted = db.delete(PrefsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRESCRIPTION:
                rowsDeleted = db.delete(PrescriptionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PHYSICIAN:
                rowsDeleted = db.delete(PhysicianEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REMINDER:
                rowsDeleted = db.delete(ReminderEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CHECK_IN_LOG:
                rowsDeleted = db.delete(CheckInLogEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PAIN_LOG:
                rowsDeleted = db.delete(PainLogEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MED_LOG:
                rowsDeleted = db.delete(MedLogEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STATUS_LOG:
                rowsDeleted = db.delete(StatusLogEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: + uri");

        }
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /**
     * Update rows in the appropriate table in the database.
     *
     * @param uri           The URI representing the table in which data should be updated.
     * @param values        The new values for the updated rows.
     * @param selection     A filter declaring which rows should be updated.
     * @param selectionArgs The values for the placeholders in the selection filter.
     * @return The number of rows updated.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated;
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case PATIENT:
                rowsUpdated = db.update(PatientEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CREDENTIAL:
            case CREDENTIAL_ID:
                rowsUpdated = db.update(CredentialEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PREF:
                rowsUpdated = db.update(PrefsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REMINDER:
            case REMINDER_ID:
                rowsUpdated = db.update(ReminderEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: + uri");
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * Perform a bulk insert operation.
     *
     * @param uri    The content URI representing the table to insert rows into.
     * @param values An array of ContentValues objects to be inserted.
     * @return The number of rows inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int returnCount = 0;
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case PRESCRIPTION:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(PrescriptionEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case PHYSICIAN:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(PhysicianEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case REMINDER:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ReminderEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case CHECK_IN_LOG:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(CheckInLogEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case PAIN_LOG:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(PainLogEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case MED_LOG:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MedLogEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case STATUS_LOG:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(StatusLogEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }

    /**
     * Builds the UriMatcher for the PatientContentProvider.
     * It matches each URI to the corresponding integer code.
     *
     * @return The UriMatcher.
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PatientCPContract.CONTENT_AUTHORITY;

        // Add URIs with specific patterns and associate them with integer codes.
        matcher.addURI(authority, PatientCPContract.PATIENT_PATH, PATIENT);
        matcher.addURI(authority, PatientCPContract.CREDENTIAL_PATH, CREDENTIAL);
        matcher.addURI(authority, PatientCPContract.CREDENTIAL_PATH + "/#", CREDENTIAL_ID);
        matcher.addURI(authority, PatientCPContract.PREFS_PATH, PREF);
        matcher.addURI(authority, PatientCPContract.PRESCRIPTION_PATH, PRESCRIPTION);
        matcher.addURI(authority, PatientCPContract.PRESCRIPTION_PATH + "/#", PRESCRIPTION_ID);
        matcher.addURI(authority, PatientCPContract.PHYSICIAN_PATH, PHYSICIAN);
        matcher.addURI(authority, PatientCPContract.PHYSICIAN_PATH + "/#", PHYSICIAN_ID);
        matcher.addURI(authority, PatientCPContract.REMINDER_PATH, REMINDER);
        matcher.addURI(authority, PatientCPContract.REMINDER_PATH + "/#", REMINDER_ID);
        matcher.addURI(authority, PatientCPContract.CHECK_IN_LOG_PATH, CHECK_IN_LOG);
        matcher.addURI(authority, PatientCPContract.CHECK_IN_LOG_PATH + "/#", CHECK_IN_LOG_ID);
        matcher.addURI(authority, PatientCPContract.PAIN_LOG_PATH, PAIN_LOG);
        matcher.addURI(authority, PatientCPContract.PAIN_LOG_PATH + "/#", PAIN_LOG_ID);
        matcher.addURI(authority, PatientCPContract.MED_LOG_PATH, MED_LOG);
        matcher.addURI(authority, PatientCPContract.MED_LOG_PATH + "/#", MED_LOG_ID);
        matcher.addURI(authority, PatientCPContract.STATUS_LOG_PATH, STATUS_LOG);
        matcher.addURI(authority, PatientCPContract.STATUS_LOG_PATH + "/#", STATUS_LOG_ID);

        return matcher;
    }
}
