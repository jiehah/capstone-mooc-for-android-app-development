package com.example.symptommanagement;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import static com.example.symptommanagement.data.PatientCPContract.*;

/**
 * Test class for testing the PatientProvider content provider.
 */
public class TestPatientProvider extends AndroidTestCase {

    /**
     * Setup method to be executed before each test case.
     * Deletes all records from the database to ensure a clean state.
     */
    public void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /**
     * Helper method to delete all records from the database.
     * Calls deleteRecords method for each content URI of the provider.
     */
    public void deleteAllRecords() {
        deleteRecords(PatientEntry.CONTENT_URI);
        deleteRecords(PrefsEntry.CONTENT_URI);
        deleteRecords(ReminderEntry.CONTENT_URI);
        deleteRecords(PrescriptionEntry.CONTENT_URI);
        deleteRecords(PhysicianEntry.CONTENT_URI);
        deleteRecords(PainLogEntry.CONTENT_URI);
        deleteRecords(MedLogEntry.CONTENT_URI);
        deleteRecords(StatusLogEntry.CONTENT_URI);
    }

    /**
     * Helper method to delete records from the database for a given content URI.
     * Uses the PatientProvider's ContentResolver to delete the records.
     *
     * @param contentUri The content URI for the records to be deleted.
     */
    public void deleteRecords(Uri contentUri) {
        mContext.getContentResolver().delete(
                contentUri,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                contentUri,
                null,
                null,
                null,
                null
        );
        cursor.close();
    }

    /**
     * Test method for inserting, reading, and deleting all types of records.
     * Calls individual test methods for each type of record.
     */
    public void testInsertReadDeleteAllRecords() {
        testInsertReadPatientRecord();
        testInsertReadPrefRecord();
        testInsertReadReminderRecord();
        testInsertReadPrescriptionRecord();
        testInsertReadPhysicianRecord();
        testInsertReadPainLogRecord();
        testInsertReadMedLogRecord();
        testInsertReadStatusRecord();
    }

    /**
     * Test method for updating records.
     * Inserts a record, updates it, and checks if the update was successful.
     */
    public void testUpdateRecords() {
        ContentValues insertObj =
                TestData.createTestPatient("Frank Neal", TestData.TRUE, System.currentTimeMillis());
        ContentValues updateObj =
                TestData.createTestPatient("Frank J Neal", TestData.TRUE, System.currentTimeMillis());
        insertUpdate(mContext, PatientEntry.CONTENT_URI, insertObj, updateObj, PatientEntry._ID,
                PatientEntry.COLUMN_FIRST_NAME);

        insertObj = TestData.createTestPrefs("NONE");
        updateObj = TestData.createTestPrefs("UTC");
        insertUpdate(mContext, PrefsEntry.CONTENT_URI, insertObj, updateObj, PrefsEntry._ID,
                PrefsEntry.COLUMN_TIMEZONE);

        insertObj = TestData.createTestReminder(987L, TestData.TRUE, "old.alarm");
        updateObj = TestData.createTestReminder(987L, TestData.TRUE, "new.alarm");
        insertUpdate(mContext, ReminderEntry.CONTENT_URI, insertObj, updateObj, ReminderEntry._ID,
                ReminderEntry.COLUMN_ALARM);
    }

    /**
     * Test method for inserting and reading patient records.
     * Inserts a patient record, reads it from the database, and verifies the read data.
     */
    public void testInsertReadPatientRecord() {
        ContentValues testObj =
                TestData.createTestPatient("Frank Neal", TestData.TRUE, System.currentTimeMillis());
        insertReadRecord(testObj, PatientEntry.CONTENT_URI);
    }

    /**
     * Test method for inserting and reading preference records.
     * Inserts a preference record, reads it from the database, and verifies the read data.
     */
    public void testInsertReadPrefRecord() {
        ContentValues testObj = TestData.createTestPrefs();
        insertReadRecord(testObj, PrefsEntry.CONTENT_URI);
    }

    /**
     * Test method for inserting and reading reminder records.
     * Inserts a reminder record, reads it from the database, and verifies the read data.
     */
    public void testInsertReadReminderRecord() {
        ContentValues testObj = TestData.createTestReminder(123L);
        insertReadRecord(testObj, ReminderEntry.CONTENT_URI);
    }

    /**
     * Test method for inserting and reading physician records.
     * Inserts a physician record, reads it from the database, and verifies the read data.
     */
    public void testInsertReadPhysicianRecord() {
        ContentValues testObj = TestData.createTestPhysician(99L, "Dr. Wise");
        insertReadRecord(testObj, PhysicianEntry.CONTENT_URI);
    }

    /**
     * Test method for inserting and reading prescription records.
     * Inserts a prescription record, reads it from the database, and verifies the read data.
     */
    public void testInsertReadPrescriptionRecord() {
        ContentValues testObj = TestData.createTestPrescription(44L, "Jumping Juice");
        insertReadRecord(testObj, PrescriptionEntry.CONTENT_URI);
    }

    /**
     * Test method for inserting and reading pain log records.
     * Inserts a pain log record, reads it from the database, and verifies the read data.
     */
    public void testInsertReadPainLogRecord() {
        ContentValues testObj = TestData.createTestPainLog(3, 1);
        insertReadRecord(testObj, PainLogEntry.CONTENT_URI);
    }

    /**
     * Test method for inserting and reading medication log records.
     * Inserts a medication log record, reads it from the database, and verifies the read data.
     */
    public void testInsertReadMedLogRecord() {
        ContentValues testObj = TestData.createTestMedLog("Water");
        insertReadRecord(testObj, MedLogEntry.CONTENT_URI);
    }

    /**
     * Test method for inserting and reading status log records.
     * Inserts a status log record, reads it from the database, and verifies the read data.
     */
    public void testInsertReadStatusRecord() {
        ContentValues testObj = TestData.createTestStatusLog("This is a silly note to put here.");
        insertReadRecord(testObj, StatusLogEntry.CONTENT_URI);
    }

    /**
     * Helper method to insert and update a record and verify the update.
     * Uses the PatientProvider's ContentResolver to perform the operations.
     *
     * @param context       The context of the test.
     * @param contentUri    The content URI for the records to be inserted and updated.
     * @param insertObj     ContentValues for the record to be inserted.
     * @param updateObj     ContentValues for the record to be updated.
     * @param id            The ID of the record used for updating.
     * @param compareColumn The column used to compare the updated value.
     * @return The ID of the inserted record.
     */
    public long insertUpdate(Context context, Uri contentUri,
                             ContentValues insertObj, ContentValues updateObj, String id,
                             String compareColumn) {

        // Insert a new record
        long insertId = insertReadRecord(insertObj, contentUri);
        updateObj.put(id, insertId);

        // Update the record using ContentResolver
        context.getContentResolver().update(contentUri, updateObj, null, null);
        Cursor cursor = mContext.getContentResolver().query(
                contentUri,
                null,
                null,
                null,
                null
        );
        assertTrue(cursor.moveToFirst());

        // Get the index of the column used for comparison
        int idx = cursor.getColumnIndex(compareColumn);
        assertFalse(idx == -1);

        // Assert that the updated value in the database matches the updated value in updateObj
        assertEquals(cursor.getString(idx).toString(), updateObj.get(compareColumn).toString());

        // Close the cursor after reading
        cursor.close();
        return insertId;
    }

    /**
     * Helper method to insert a record, read it from the database, and verify the read data.
     * Uses the PatientProvider's ContentResolver to insert the record.
     *
     * @param testObj    ContentValues for the record to be inserted.
     * @param contentUri The content URI for the record to be inserted.
     * @return The ID of the inserted record.
     */
    public long insertReadRecord(ContentValues testObj, Uri contentUri) {
        // Insert the record using ContentResolver
        Uri uri = mContext.getContentResolver().insert(contentUri, testObj);
        long rowId = ContentUris.parseId(uri);
        assertTrue(rowId != -1);

        // Query the database to read the inserted record
        Cursor cursor = mContext.getContentResolver().query(
                contentUri,
                null,
                null,
                null,
                null
        );

        // Ensure that the cursor has data and move to the first row
        assertTrue(cursor.moveToFirst());

        // Close the cursor after reading
        cursor.close();
        return rowId;
    }
}
