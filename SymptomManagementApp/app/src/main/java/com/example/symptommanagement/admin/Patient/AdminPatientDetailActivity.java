package com.example.symptommanagement.admin.Patient;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.example.symptommanagement.R;

/**
 * Activity to display patient details and handle interactions with PatientDetailFragment and BirthdateDialog.
 */
public class AdminPatientDetailActivity extends Activity
        implements PatientDetailFragment.Callbacks, BirthdateDialog.Callbacks {

    private static final String LOG_TAG = AdminPatientDetailActivity.class.getSimpleName();
    private final static String PATIENT_ID_KEY = AdminPatientListActivity.PATIENT_ID_KEY;

    /**
     * Called when the activity is being created. Sets up the activity's layout and initializes
     * the appropriate fragment depending on whether it is in "View" or "Edit" mode.
     *
     * @param savedInstanceState A Bundle containing the saved state of the activity, which may be null
     *                           if the activity is created for the first time.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_patient_detail);

        // Enable the "Up" button in the action bar to navigate back to the parent activity
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            Bundle arguments = new Bundle();
            String id = getIntent().getStringExtra(PATIENT_ID_KEY);
            Log.d(LOG_TAG, "Patient ID Key is : " + id);
            Fragment fragment;
            if (id != null) {
                // If the activity is launched with a patient ID, show the PatientDetailFragment
                arguments.putString(PATIENT_ID_KEY, id);
                fragment = new PatientDetailFragment();
            } else {
                // If no patient ID is provided, show the PatientAddEditFragment for adding a new patient
                fragment = new PatientAddEditFragment();
            }
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.adminpatient_detail_container, fragment)
                    .commit();
        }
    }

    /**
     * Called when a menu item is selected from the activity's options menu. Handles the selection
     * of the "Up" button in the action bar to navigate back to the parent activity.
     *
     * @param item The selected menu item.
     * @return true if the menu item selection was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Handle the "Up" button click by navigating up to the parent activity
            navigateUpTo(new Intent(this, AdminPatientListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method called when the "Edit" option is selected from PatientDetailFragment's options menu.
     * It switches out the fragment to the PatientAddEditFragment for editing patient information.
     *
     * @param id The ID of the patient to edit.
     */
    @Override
    public void onEditPatient(String id) {
        Bundle arguments = new Bundle();
        arguments.putString(PATIENT_ID_KEY, id);
        PatientAddEditFragment fragment = new PatientAddEditFragment();
        fragment.setArguments(arguments);
        getFragmentManager().beginTransaction()
                .replace(R.id.adminpatient_detail_container, fragment)
                .commit();
    }

    /**
     * Callback method called when the user selects a date from BirthdateDialog.
     * It passes the selected date to the PatientAddEditFragment for further processing.
     *
     * @param date The selected date in "MM-dd-yyyy" format.
     */
    @Override
    public void onPositiveResult(String date) {
        PatientAddEditFragment fragment = (PatientAddEditFragment) getFragmentManager()
                .findFragmentById(R.id.adminpatient_detail_container);
        fragment.onPositiveResult(date);
    }

    /**
     * Callback method called when the user cancels the BirthdateDialog.
     * It notifies the PatientAddEditFragment about the negative result.
     */
    @Override
    public void onNegativeResult() {
        PatientAddEditFragment fragment = (PatientAddEditFragment) getFragmentManager()
                .findFragmentById(R.id.adminpatient_detail_container);
        fragment.onNegativeResult();
    }
}
