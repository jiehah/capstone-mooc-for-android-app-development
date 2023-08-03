package com.example.symptommanagement.admin.Patient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.example.symptommanagement.R;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;

/**
 * Activity to display the list of patients and handle interactions with the PatientListFragment.
 */
public class AdminPatientListActivity extends Activity implements PatientListFragment.Callbacks {

    private final String LOG_TAG = AdminPatientListActivity.class.getSimpleName();

    /**
     * Key for passing patient ID to the detail activity
     */
    public static final String PATIENT_ID_KEY = "patient_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_patient_list);

        // Enable the "Up" button in the action bar to navigate back to the parent activity
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Handle the "Up" button click by navigating up to the parent activity
            navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method called when a patient is selected from the list in PatientListFragment.
     *
     * @param id The ID of the selected patient.
     */
    @Override
    public void onPatientSelected(String id) {
        // Start the AdminPatientDetailActivity to display the details of the selected patient
        Log.d(LOG_TAG, "Saving Patient ID: " + id);
        Intent detailIntent = new Intent(this, AdminPatientDetailActivity.class);
        detailIntent.putExtra(PATIENT_ID_KEY, id);
        startActivity(detailIntent);
    }

    /**
     * Callback method called when the "Add" button is clicked in PatientListFragment.
     * It opens the AdminPatientDetailActivity in Add/Edit mode.
     */
    @Override
    public void onAddPatient() {
        // Start the AdminPatientDetailActivity in Add/Edit mode
        Log.d(LOG_TAG, "Changing to Add/Edit Fragment");
        Intent detailIntent = new Intent(this, AdminPatientDetailActivity.class);
        startActivity(detailIntent);
    }
}
