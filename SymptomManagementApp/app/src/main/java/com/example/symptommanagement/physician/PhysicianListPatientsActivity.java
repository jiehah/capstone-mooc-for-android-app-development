package com.example.symptommanagement.physician;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.Medication;
import com.example.symptommanagement.data.Patient;
import com.example.symptommanagement.sync.SymptomManagementSyncAdapter;

/**
 * This activity displays a list of patients for the physician and allows interactions
 * with patient details when using a two-pane layout (dual pane).
 * Extends the PhysicianActivity which provides basic functionality for a physician.
 */
public class PhysicianListPatientsActivity extends PhysicianActivity {

    /**
     * Tag for logging purposes
     */
    public final static String LOG_TAG = PhysicianListPatientsActivity.class.getSimpleName();

    /**
     * Boolean flag to determine if the layout is in two-pane mode (dual pane) or not
     */
    private boolean twoPane;

    /**
     * Called when the activity is starting or restarting.
     * Responsible for initializing the activity and inflating the layout.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the action bar and disable the display of the "up" button (back button)
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        // Set the default value for the twoPane flag
        twoPane = false;

        // Set the activity's layout to the specified XML layout resource
        setContentView(R.layout.activity_physician_patient_list);

        // Check if the layout has a container for the patient details fragment (dual pane)
        if (findViewById(R.id.physician_patient_detail_container) != null) {
            // Dual pane mode (two-pane layout)
            twoPane = true;

            // Set the screen orientation to landscape for better two-pane layout display
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            // Enable item click activation for the patient list
            ((PhysicianListPatientsFragment) getFragmentManager()
                    .findFragmentById(R.id.physician_patient_list))
                    .setActivateOnItemClick(true);

            // Check if this is the first creation of the activity
            if (savedInstanceState == null) {
                // Create fragments for patient details and patient graphics
                Bundle arguments = new Bundle();
                arguments.putString(PHYSICIAN_ID_KEY, physicianId);
                PhysicianPatientDetailFragment detailsFragment = new PhysicianPatientDetailFragment();
                detailsFragment.setArguments(arguments);
                PatientGraphicsFragment graphicsFragment = new PatientGraphicsFragment();
                graphicsFragment.setArguments(arguments);

                // Replace the detail and graphics fragments in their corresponding containers
                getFragmentManager().beginTransaction()
                        .replace(R.id.physician_patient_detail_container,
                                detailsFragment,
                                PhysicianPatientDetailFragment.FRAGMENT_TAG)
                        .replace(R.id.patient_graphics_container,
                                graphicsFragment,
                                PatientGraphicsFragment.FRAGMENT_TAG)
                        .commit();
            }
        }
    }

    /**
     * Prepare the options menu with the correct items based on the active fragment.
     *
     * @param menu The menu to be prepared.
     * @return Always returns true to display the menu.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Get the currently active fragment
        Fragment frag = getActiveFragment();

        // Check if the active fragment is found
        if (frag == null) {
            Log.e(LOG_TAG, "Active Fragment is not found!");
            return super.onPrepareOptionsMenu(menu);
        }

        // Check the active fragment type and remove the corresponding menu item
        if (frag instanceof PatientMedicationFragment) {
            menu.removeItem(R.id.action_medication_list);
        } else if (frag instanceof HistoryLogFragment) {
            menu.removeItem(R.id.action_history_log);
        } else if (frag instanceof MedicationListFragment) {
            menu.removeItem(R.id.action_medication_list);
        } else if (frag instanceof PatientGraphicsFragment) {
            menu.removeItem(R.id.action_chart);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Create the options menu based on the layout mode (single-pane or two-pane).
     *
     * @param menu The menu to be inflated.
     * @return Always returns true to display the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu based on the two-pane mode
        if (twoPane) {
            getMenuInflater().inflate(R.menu.physician_patient_twopane_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.physician_patient_list_menu, menu);
        }
        return true;
    }

    /**
     * Handle options menu item selections.
     *
     * @param item The selected menu item.
     * @return Always returns true if the item selection is handled.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_patient_search) {
            // Show patient search dialog when the corresponding menu item is selected
            FragmentManager fm = getFragmentManager();
            PatientSearchDialog searchDialog = PatientSearchDialog.newInstance();
            searchDialog.show(fm, PatientSearchDialog.FRAGMENT_TAG);
        } else if (id == R.id.action_sync_alerts) {
            // Perform immediate synchronization of symptom management alerts
            SymptomManagementSyncAdapter.syncImmediately(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Override the back button behavior to go to the home screen instead of the previous activity.
     */
    @Override
    public void onBackPressed() {
        startActivity(new Intent()
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME));
    }

    /**
     * Handle the selection of a patient in the list.
     *
     * @param physicianId The ID of the physician.
     * @param patient     The selected patient.
     */
    @Override
    public void onItemSelected(String physicianId, Patient patient) {
        // Handle the selection of a patient based on the two-pane mode
        if (twoPane) {
            super.onItemSelected(physicianId, patient);
        } else {
            // Launch the PhysicianPatientDetailActivity with patient details when in single pane mode
            Bundle arguments = new Bundle();
            arguments.putString(PHYSICIAN_ID_KEY, physicianId);
            arguments.putString(PATIENT_ID_KEY, patient.getId());
            Intent detailIntent = new Intent(this, PhysicianPatientDetailActivity.class);
            detailIntent.putExtras(arguments);
            startActivity(detailIntent);
        }
    }

    /**
     * Handle the selection of a medication for a patient.
     *
     * @param medication The selected medication.
     */
    public void onMedicationSelected(Medication medication) {
        getFragmentManager().beginTransaction()
                .replace(R.id.patient_graphics_container,
                        new PatientMedicationFragment(),
                        PatientMedicationFragment.FRAGMENT_TAG)
                .commit();

        // Get the PatientMedicationFragment instance and add the selected prescription to it
        PatientMedicationFragment frag =
                (PatientMedicationFragment) getFragmentManager()
                        .findFragmentByTag(PatientMedicationFragment.FRAGMENT_TAG);
        if (frag != null) {
            frag.addPrescription(medication);
        } else {
            Log.e(LOG_TAG, "Unable to add patient prescription.");
        }
    }

    /**
     * Handle the selection of a patient by name.
     *
     * @param lastName  The last name of the patient.
     * @param firstName The first name of the patient.
     */
    @Override
    public void onNameSelected(String lastName, String firstName) {
        Log.e(LOG_TAG, "THE NAME SELECTED IS : " + getName(lastName, firstName));
        PatientManager.findPatientByName(this, getName(lastName, firstName));
    }

    /**
     * Handle a successful patient search based on the two-pane mode.
     *
     * @param patient The patient found through the search.
     */
    @Override
    public void successfulSearch(Patient patient) {
        // Handle a successful patient search based on the two-pane mode
        if (twoPane) {
            setPatient(patient);
            Fragment frag = getFragmentManager()
                    .findFragmentByTag(PhysicianListPatientsFragment.FRAGMENT_TAG);
            if (frag != null) {
                ((PhysicianListPatientsFragment) frag).temporaryAddToList(patient);
            }
        } else {
            // Launch the PhysicianPatientDetailActivity with the search result in single pane mode
            Intent detailIntent = new Intent(getApplication(), PhysicianPatientDetailActivity.class);
            detailIntent.putExtra(PATIENT_ID_KEY, patient.getId());
            detailIntent.putExtra(PHYSICIAN_ID_KEY, physicianId);
            startActivity(detailIntent);
        }
    }
}
