package com.example.symptommanagement.admin.Physician;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.example.symptommanagement.R;

/**
 * Activity to display the details of a physician or to add/edit a physician.
 * This activity hosts two fragments: AdminPhysicianDetailFragment and AdminPhysicianAddEditFragment.
 * If a physician ID is passed to the activity,
 * AdminPhysicianDetailFragment is shown to display the details of the selected physician.
 * If no physician ID is passed,
 * AdminPhysicianAddEditFragment is shown to add/edit a new physician.
 */
public class AdminPhysicianDetailActivity extends Activity implements AdminPhysicianDetailFragment.Callbacks {

    private static final String LOG_TAG = AdminPhysicianDetailActivity.class.getSimpleName();

    /**
     * Key to retrieve the physician ID from the intent extras
     */
    private final static String PHYSICIAN_ID_KEY = AdminPhysicianListActivity.PHYSICIAN_ID_KEY;

    /**
     * Called when the activity is created.
     * This method sets up the activity layout, enables the "Up" button in the action bar to navigate
     * up to the parent activity, and determines which fragment to display based on the physician ID
     * passed to the activity.
     *
     * @param savedInstanceState A Bundle containing the saved state of the activity, or null if there is no saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the activity layout
        setContentView(R.layout.activity_admin_physician_detail);

        // Enable the "Up" button in the action bar to navigate up to the parent activity
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // Get the physician ID from the intent extras
            Bundle arguments = new Bundle();
            String physicianId = getIntent().getStringExtra(PHYSICIAN_ID_KEY);
            Log.d(LOG_TAG, "Physician ID Key is : " + physicianId);
            Fragment fragment;
            if (physicianId != null) {
                // If the physician ID is available, create AdminPhysicianDetailFragment
                arguments.putString(PHYSICIAN_ID_KEY, physicianId);
                fragment = new AdminPhysicianDetailFragment();
            } else {
                // If the physician ID is not available, create AdminPhysicianAddEditFragment
                fragment = new AdminPhysicianAddEditFragment();
            }
            fragment.setArguments(arguments);
            // Replace the current fragment in the adminphysician_detail_container with the new fragment
            getFragmentManager().beginTransaction()
                    .add(R.id.adminphysician_detail_container, fragment)
                    .commit();
        }
    }

    /**
     * Called when an options menu item is selected.
     * This method handles menu item clicks in the activity's options menu. In this case, it specifically
     * handles clicks on the "Up" button (the app's home button) in the action bar, which allows the user
     * to navigate up to the parent activity (AdminPhysicianListActivity).
     *
     * @param item The selected menu item.
     * @return true if the event was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Handle "Up" button click to navigate up to the parent activity
            navigateUpTo(new Intent(this, AdminPhysicianListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method from AdminPhysicianDetailFragment.
     * Handles the event when the "Edit" option is selected for a physician.
     * Switches out the current fragment with AdminPhysicianAddEditFragment to edit the physician.
     *
     * @param id The ID of the physician to edit.
     */
    @Override
    public void onEditPhysician(String id) {
        // Prepare the arguments to pass to the AdminPhysicianAddEditFragment
        Bundle arguments = new Bundle();
        arguments.putString(PHYSICIAN_ID_KEY, id);

        // Create a new instance of AdminPhysicianAddEditFragment and set the arguments
        AdminPhysicianAddEditFragment fragment = new AdminPhysicianAddEditFragment();
        fragment.setArguments(arguments);

        // Replace the current fragment in the adminphysician_detail_container with the new fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.adminphysician_detail_container, fragment)
                .commit();
    }
}

