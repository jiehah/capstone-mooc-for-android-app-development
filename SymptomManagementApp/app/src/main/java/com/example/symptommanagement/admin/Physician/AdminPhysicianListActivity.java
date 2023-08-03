package com.example.symptommanagement.admin.Physician;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.example.symptommanagement.R;
import com.example.symptommanagement.databinding.ActivityAdminPhysicianListBinding;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;

/**
 * Activity class for displaying the list of physicians in the Admin user interface.
 * This activity implements the PhysicianListFragment.Callbacks and AdminPhysicianDetailFragment.Callbacks interfaces
 * to handle interactions with the list of physicians and the details of each physician.
 */
public class AdminPhysicianListActivity extends Activity
        implements PhysicianListFragment.Callbacks, AdminPhysicianDetailFragment.Callbacks {

    private final String LOG_TAG = AdminPhysicianListActivity.class.getSimpleName();
    public static final String PHYSICIAN_ID_KEY = "physician_id";
    private ActivityAdminPhysicianListBinding binding;

    /**
     * Called when the activity is starting.
     * This method is responsible for initializing the activity and setting up its user interface.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout for this activity using the provided inflater
        // and bind the ActivityAdminPhysicianListBinding object to the inflated view
        binding = ActivityAdminPhysicianListBinding.inflate(getLayoutInflater());

        // Set the root view of the inflated layout as the content view of the activity
        setContentView(binding.getRoot());

        // Enable the "Up" button in the action bar to navigate up to the parent activity
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Called when an options menu item is selected.
     * This method is responsible for handling action bar item clicks.
     * The action bar will automatically handle clicks on the Home/Up button,
     * as long as you specify a parent activity in AndroidManifest.xml.
     *
     * @param item The menu item that was selected.
     * @return Return true if the event was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        // The action bar will automatically handle clicks on the Home/Up button,
        // so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Handle "Up" button click to navigate up to the parent activity
            navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a physician is selected from the list.
     * This method handles the event when a physician is selected from the list of physicians.
     *
     * @param physicianId The ID of the selected physician.
     * @param firstName   The first name of the selected physician.
     * @param lastName    The last name of the selected physician.
     */
    @Override
    public void onPhysicianSelected(String physicianId, String firstName, String lastName) {
        // Handle the event when a physician is selected from the list

        // Log the selected physician's ID for debugging purposes
        Log.d(LOG_TAG, "Saving Physician ID: " + physicianId);

        // Start the AdminPhysicianDetailActivity to show the details of the selected physician
        Intent detailIntent = new Intent(this, AdminPhysicianDetailActivity.class);
        detailIntent.putExtra(PHYSICIAN_ID_KEY, physicianId);
        startActivity(detailIntent);
    }

    /**
     * Determine whether to show the "Add Physician" option in the option's menu.
     *
     * @return True to show the "Add Physician" option, false otherwise.
     */
    @Override
    public boolean showAddPhysicianOptionsMenu() {
        // Show the "Add Physician" option in the options menu
        return true;
    }

    /**
     * Called when the "Add Physician" option is selected from the options menu.
     * This method handles the event when the "Add Physician" option is selected from the options menu.
     */
    @Override
    public void onAddPhysician() {
        // Handle the event when the "Add Physician" option is selected from the options menu

        // Log a message to indicate that the activity is changing to the Add/Edit Fragment
        Log.d(LOG_TAG, "Changing to Add/Edit Fragment");

        // Start the AdminPhysicianDetailActivity to add/edit a physician
        Intent detailIntent = new Intent(this, AdminPhysicianDetailActivity.class);
        startActivity(detailIntent);
    }

    /**
     * Called when the "Edit" option is selected for a physician.
     * This method handles the event when the "Edit" option is selected for a physician from the list.
     *
     * @param id The ID of the physician to be edited.
     */
    @Override
    public void onEditPhysician(String id) {
        // Handle the event when the "Edit" option is selected for a physician

        // Switch out the current fragment with AdminPhysicianAddEditFragment to edit the physician

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
