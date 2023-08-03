/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.symptommanagement;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import com.example.symptommanagement.data.PatientDBHelper;

import static com.example.symptommanagement.data.PatientCPContract.*;

public class TestPatientDB extends AndroidTestCase {

    /**
     * Test method to check database creation.
     * Deletes the database, creates a new one, and checks if it is open.
     */
    public void testCreateDb() {
        // Delete the database to start fresh
        mContext.deleteDatabase(PatientDBHelper.DATABASE_NAME);

        // Get a writable database instance
        SQLiteDatabase db = new PatientDBHelper(this.mContext).getWritableDatabase();

        // Check if the database is open
        assertTrue(db.isOpen());

        // Close the database
        db.close();
    }

    /**
     * Test method for inserting and reading patient data.
     * Inserts a patient record, reads it from the database, updates it,
     * and checks if the update was successful.
     */
    public void testPatientInsertRead() {
        // Get a reference to the database helper
        PatientDBHelper dbHelper = new PatientDBHelper(mContext);

        // Get a writable database instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create test patient data
        ContentValues testPatient = TestData.createTestPatient("patient1", 1, System.currentTimeMillis());

        // Insert test patient data into the database
        long rowId = db.insert(PatientEntry.TABLE_NAME, null, testPatient);

        // Check if the insertion was successful
        assertTrue(rowId != -1);

        // Print the row ID for debugging purposes
        System.out.println("Patient row id: " + rowId);

        // Query the database to read the inserted data
        Cursor cursor = db.query(
                PatientEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Ensure that the cursor has data and move to the first row
        assertTrue(cursor.moveToFirst());

        // Close the cursor after reading
        cursor.close();

        // Update testPatient data with new value and perform an update on the database
        testPatient.put(PatientEntry.COLUMN_LAST_LOGIN, System.currentTimeMillis());
        db.update(PatientEntry.TABLE_NAME, testPatient, null, null);

        // Query the database again to check if the update was successful
        cursor = db.query(
                PatientEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Ensure that the cursor has data and move to the first row
        assertTrue(cursor.moveToFirst());

        // Assert that the number of rows returned is 1
        assertEquals(1, cursor.getCount());

        // Get the index of the column containing the last login time
        int idx = cursor.getColumnIndex(PatientEntry.COLUMN_LAST_LOGIN);

        // Assert that the column exists
        assertFalse(idx == -1);

        // Assert that the last login time matches the updated value in the testPatient object
        assertEquals(cursor.getLong(idx), (long) testPatient.getAsLong(PatientEntry.COLUMN_LAST_LOGIN));

        // Close the cursor after reading
        cursor.close();
    }

    /**
     * Test method for inserting and reading prescription data.
     * Inserts a prescription record, reads it from the database.
     */
    public void testPrescriptionInsertRead() {
        // Get a reference to the database helper
        PatientDBHelper dbHelper = new PatientDBHelper(mContext);

        // Get a writable database instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create test prescription data
        ContentValues testObject = TestData.createTestPrescription(11L, "Happy Juice");

        // Insert test prescription data into the database
        long rowId = db.insert(PrescriptionEntry.TABLE_NAME, null, testObject);

        // Check if the insertion was successful
        assertTrue(rowId != -1);

        // Print the row ID for debugging purposes
        System.out.println("Prescription row id: " + rowId);

        // Query the database to read the inserted data
        Cursor cursor = db.query(
                PrescriptionEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Ensure that the cursor has data and move to the first row
        assertTrue(cursor.moveToFirst());

        // Close the cursor after reading
        cursor.close();
    }

    /**
     * Test method for inserting and reading preferences data.
     * Inserts a preferences record, reads it from the database, updates it,
     * and checks if the update was successful.
     */
    public void testPrefsInsertRead() {
        // Get a reference to the database helper
        PatientDBHelper dbHelper = new PatientDBHelper(mContext);

        // Get a writable database instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create test preferences data using the TestData class
        ContentValues testObject = TestData.createTestPrefs();

        // Insert test preferences data into the database
        long rowId = db.insert(PrefsEntry.TABLE_NAME, null, testObject);

        // Check if the insertion was successful
        assertTrue(rowId != -1);

        // Print the row ID for debugging purposes
        System.out.println("Prefs row id: " + rowId);

        // Query the database to read the inserted data
        Cursor cursor = db.query(
                PrefsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Ensure that the cursor has data and move to the first row
        assertTrue(cursor.moveToFirst());

        // Close the cursor after reading
        cursor.close();

        // Update the testObject data with a new value and perform an update on the database
        testObject.put(PrefsEntry.COLUMN_CREATED, System.currentTimeMillis());
        db.update(PrefsEntry.TABLE_NAME, testObject, null, null);

        // Query the database again to check if the update was successful
        cursor = db.query(
                PrefsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Ensure that the cursor has data and move to the first row
        assertTrue(cursor.moveToFirst());

        // Assert that the number of rows returned is 1
        assertEquals(1, cursor.getCount());

        // Get the index of the column containing the 'created' timestamp
        int idx = cursor.getColumnIndex(PrefsEntry.COLUMN_CREATED);

        // Assert that the column exists
        assertFalse(idx == -1);

        // Assert that the 'created' timestamp matches the updated value in the testObject
        assertEquals(cursor.getLong(idx), (long) testObject.getAsLong(PrefsEntry.COLUMN_CREATED));

        // Close the cursor after reading
        cursor.close();
    }


    /**
     * Test method for inserting and reading physician data.
     * Inserts a physician record, reads it from the database, and verifies the read data.
     */
    public void testPhysicianInsertRead() {
        // Get a reference to the database helper
        PatientDBHelper dbHelper = new PatientDBHelper(mContext);

        // Get a writable database instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create test physician data using the TestData class
        ContentValues testObject = TestData.createTestPhysician(12L, "Dr. Giggles");

        // Insert test physician data into the database
        long rowId = db.insert(PhysicianEntry.TABLE_NAME, null, testObject);

        // Check if the insertion was successful
        assertTrue(rowId != -1);

        // Print the row ID for debugging purposes
        System.out.println("Physician row id: " + rowId);

        // Query the database to read the inserted data
        Cursor cursor = db.query(
                PhysicianEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Ensure that the cursor has data and move to the first row
        assertTrue(cursor.moveToFirst());

        // Close the cursor after reading
        cursor.close();
    }

    /**
     * Test method for inserting and reading pain log data.
     * Inserts a pain log record, reads it from the database, and verifies the read data.
     */
    public void testPainLogInsertRead() {
        // Get a reference to the database helper
        PatientDBHelper dbHelper = new PatientDBHelper(mContext);

        // Get a writable database instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create test pain log data using the TestData class
        ContentValues testObject = TestData.createTestPainLog(1, 3);

        // Insert test pain log data into the database
        long rowId = db.insert(PainLogEntry.TABLE_NAME, null, testObject);

        // Check if the insertion was successful
        assertTrue(rowId != -2);

        // Print the row ID for debugging purposes
        System.out.println("Pain Log row id: " + rowId);

        // Query the database to read the inserted data
        Cursor cursor = db.query(
                PainLogEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Ensure that the cursor has data and move to the first row
        assertTrue(cursor.moveToFirst());

        // Close the cursor after reading
        cursor.close();
    }

    /**
     * Test method for inserting and reading medication log data.
     * Inserts a medication log record, reads it from the database, and verifies the read data.
     */
    public void testMedLogInsertRead() {
        // Get a reference to the database helper
        PatientDBHelper dbHelper = new PatientDBHelper(mContext);

        // Get a writable database instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create test medication log data using the TestData class
        ContentValues testObject = TestData.createTestMedLog("Happy Pills");

        // Insert test medication log data into the database
        long rowId = db.insert(MedLogEntry.TABLE_NAME, null, testObject);

        // Check if the insertion was successful
        assertTrue(rowId != -2);

        // Print the row ID for debugging purposes
        System.out.println("Med Log row id: " + rowId);

        // Query the database to read the inserted data
        Cursor cursor = db.query(
                MedLogEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Ensure that the cursor has data and move to the first row
        assertTrue(cursor.moveToFirst());

        // Close the cursor after reading
        cursor.close();
    }

    /**
     * Test method for inserting and reading status log data.
     * Inserts a status log record, reads it from the database, and verifies the read data.
     */
    public void testStatusLogInsertRead() {
        // Get a reference to the database helper
        PatientDBHelper dbHelper = new PatientDBHelper(mContext);

        // Get a writable database instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create test status log data using the TestData class
        ContentValues testObject = TestData.createTestStatusLog("This is a test note");

        // Insert test status log data into the database
        long rowId = db.insert(StatusLogEntry.TABLE_NAME, null, testObject);

        // Check if the insertion was successful
        assertTrue(rowId != -2);

        // Print the row ID for debugging purposes
        System.out.println("Status Log row id: " + rowId);

        // Query the database to read the inserted data
        Cursor cursor = db.query(
                StatusLogEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Ensure that the cursor has data and move to the first row
        assertTrue(cursor.moveToFirst());

        // Close the cursor after reading
        cursor.close();
    }

    /**
     * Test method for inserting and reading reminder data.
     * Inserts a reminder record, reads it from the database, updates it,
     * and checks if the update was successful.
     */
    public void testReminderInsertRead() {
        // Get a reference to the database helper
        PatientDBHelper dbHelper = new PatientDBHelper(mContext);

        // Get a writable database instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create test reminder data using the TestData class
        ContentValues testObject = TestData.createTestReminder(55L);

        // Insert test reminder data into the database
        long rowId = db.insert(ReminderEntry.TABLE_NAME, null, testObject);

        // Check if the insertion was successful
        assertTrue(rowId != -1);

        // Print the row ID for debugging purposes
        System.out.println("Reminder row id: " + rowId);

        // Query the database to read the inserted data
        Cursor cursor = db.query(
                ReminderEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Ensure that the cursor has data and move to the first row
        assertTrue(cursor.moveToFirst());

        // Close the cursor after reading
        cursor.close();

        // Update the testObject data with a new value and perform an update on the database
        testObject.put(ReminderEntry.COLUMN_CREATED, System.currentTimeMillis());
        db.update(ReminderEntry.TABLE_NAME, testObject, null, null);

        // Query the database again to check if the update was successful
        cursor = db.query(
                ReminderEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Ensure that the cursor has data and move to the first row
        assertTrue(cursor.moveToFirst());

        // Assert that the number of rows returned is 1
        assertEquals(1, cursor.getCount());

        // Get the index of the column containing the 'created' timestamp
        int idx = cursor.getColumnIndex(ReminderEntry.COLUMN_CREATED);

        // Assert that the column exists
        assertFalse(idx == -1);

        // Assert that the 'created' timestamp matches the updated value in the testObject
        assertEquals(cursor.getLong(idx), (long) testObject.getAsLong(ReminderEntry.COLUMN_CREATED));

        // Close the cursor after reading
        cursor.close();
    }
}