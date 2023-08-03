package com.example.symptommanagement.physician;

import android.os.Bundle;
import android.view.Menu;
import com.example.symptommanagement.R;

/**
 * Activity class for displaying patient details and graphics to a physician.
 * This activity extends the PhysicianActivity, which provides shared functionality
 * for physician-related activities.
 */
public class PhysicianPatientDetailActivity extends PhysicianActivity {

    private static final String LOG_TAG = PhysicianPatientDetailActivity.class.getSimpleName();

    /**
     * Called when the activity is created. This method initializes the activity and sets the
     * content view to the "activity_physicianpatient_detail" layout. If the activity is newly created
     * (no saved instance state), it adds two fragments to display patient details and graphics.
     *
     * @param savedInstanceState A Bundle containing the saved instance state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view to the activity_physicianpatient_detail layout.
        setContentView(R.layout.activity_physicianpatient_detail);

        // Check if the activity is newly created (no saved instance state).
        if (savedInstanceState == null) {
            // If the activity is new, add two fragments to display patient details and graphics.

            // First, replace the content in the physician_patient_detail_container with the
            // PhysicianPatientDetailFragment.
            getFragmentManager().beginTransaction()
                    .replace(R.id.physician_patient_detail_container,
                            new PhysicianPatientDetailFragment(),
                            PhysicianPatientDetailFragment.FRAGMENT_TAG)
                    // Then, replace the content in the patient_graphics_container with the
                    // PatientGraphicsFragment.
                    .replace(R.id.patient_graphics_container,
                            new PatientGraphicsFragment(),
                            PatientGraphicsFragment.FRAGMENT_TAG)
                    .commit();
        }
    }

    /**
     * Inflate the options menu for the PhysicianPatientDetailActivity.
     * This method is called when the activity is creating its menu.
     * It inflates the menu resource physician_patient_detail_menu.xml,
     * which provides options specific to physician patient details.
     *
     * @param menu The menu to be populated.
     * @return true if the menu is to be displayed; false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu with the specified menu resource for physician patient details.
        getMenuInflater().inflate(R.menu.physician_patient_detail_menu, menu);
        return true;
    }
}
