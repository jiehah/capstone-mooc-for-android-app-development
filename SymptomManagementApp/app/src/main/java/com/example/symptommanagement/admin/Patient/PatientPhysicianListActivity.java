package com.example.symptommanagement.admin.Patient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.example.symptommanagement.admin.Physician.PhysicianListFragment;
import com.example.symptommanagement.databinding.ActivityAdminPhysicianListBinding;
import com.example.symptommanagement.databinding.ActivityPhysicianPatientListBinding;

/**
 * The PatientPhysicianListActivity displays a list of physicians for a patient to choose from.
 * It implements the PhysicianListFragment.Callbacks interface to handle interactions with the list of physicians.
 */
public class PatientPhysicianListActivity extends Activity implements PhysicianListFragment.Callbacks {

    /**
     * Tag for logging purposes.
     */
    private final String LOG_TAG = PatientPhysicianListActivity.class.getSimpleName();

    /**
     * Key used to pass the selected physician's ID to the calling activity.
     */
    public static final String PHYSICIAN_ID_KEY = "physician_id";

    /**
     * Key used to pass the selected physician's first name to the calling activity.
     */
    public static final String PHYSICIAN_FIRST_NAME_KEY = "physician_first_name";

    /**
     * Key used to pass the selected physician's last name to the calling activity.
     */
    public static final String PHYSICIAN_LAST_NAME_KEY = "physician_last_name";

    /**
     * The binding object for the activity layout.
     */
    private ActivityAdminPhysicianListBinding binding;

    /**
     * Called when the activity is being created.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if any.
     *                           It is null if the activity is being created for the first time.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout for this activity using the provided inflater
        // and bind the ActivityAdminPhysicianListBinding object to the inflated view
        binding = ActivityAdminPhysicianListBinding.inflate(getLayoutInflater());

        // Set the root view of the inflated layout as the content view of the activity
        setContentView(binding.getRoot());
    }

    /**
     * Called when a physician is selected from the list.
     *
     * @param physicianId The ID of the selected physician.
     * @param firstName   The first name of the selected physician.
     * @param lastName    The last name of the selected physician.
     */
    @Override
    public void onPhysicianSelected(String physicianId, String firstName, String lastName) {
        // Handle the event when a physician is selected from the list

        // Log the selected physician's details for debugging purposes
        Log.d(LOG_TAG, "id selected is " + physicianId + " name is " + firstName + " " + lastName);

        // Prepare the data to be sent back to the calling activity
        Intent intent = new Intent();
        intent.putExtra(PHYSICIAN_ID_KEY, physicianId);
        intent.putExtra(PHYSICIAN_FIRST_NAME_KEY, firstName);
        intent.putExtra(PHYSICIAN_LAST_NAME_KEY, lastName);

        // Set the result of the activity as "RESULT_OK" with the data intent
        setResult(RESULT_OK, intent);

        // Finish the activity and return to the calling activity
        onBackPressed();
    }

    /**
     * Determines if the "Add Physician" option should be shown in the options menu.
     *
     * @return Always returns false as this activity does not support adding physicians.
     */
    @Override
    public boolean showAddPhysicianOptionsMenu() {
        // This activity does not support adding physicians, so return false
        return false;
    }

    /**
     * Called when the "Add Physician" option is selected from the options menu.
     */
    @Override
    public void onAddPhysician() {
        // This method is not used in this activity since it does not support adding physicians
    }
}
