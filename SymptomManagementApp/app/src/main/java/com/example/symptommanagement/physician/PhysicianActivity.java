package com.example.symptommanagement.physician;


import android.app.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.symptommanagement.LoginActivity;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.Medication;
import com.example.symptommanagement.data.Patient;
import com.example.symptommanagement.data.Physician;
import com.example.symptommanagement.data.StatusLog;

import java.util.Collection;
import java.util.HashSet;

/**
 * An abstract activity for physicians containing various fragments and callbacks.
 */
public abstract class PhysicianActivity
        extends Activity
        implements
        PhysicianListPatientsFragment.Callbacks,
        PhysicianPatientDetailFragment.Callbacks,
        PrescriptionAdapter.Callbacks,
        PatientMedicationFragment.Callbacks,
        MedicationListFragment.Callbacks,
        MedicationAddEditDialog.Callbacks,
        HistoryLogFragment.Callbacks,
        PatientGraphicsFragment.Callbacks,
        PhysicianManager.Callbacks,
        PatientManager.Callbacks,
        MedicationManager.Callbacks,
        PatientSearchDialog.Callbacks {

    private static final String LOG_TAG = PhysicianActivity.class.getSimpleName();

    protected static String PHYSICIAN_ID_KEY;
    protected static String PATIENT_ID_KEY;

    protected static String physicianId;
    protected static Physician physician = new Physician();

    protected static String patientId;
    protected static Patient patient = new Patient();

    protected static Collection<Medication> medications = new HashSet<>();


    /**
     * Called when the activity is starting. This is where most initialization should go:
     * calling setContentView(int) to inflate the activity's UI, initializing member variables, etc.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down, then this Bundle contains the data it most recently supplied
     *                           in onSaveInstanceState(Bundle). Note: Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the "up" navigation (back) button in the action bar
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        // Get keys for physician and patient IDs from resources
        PHYSICIAN_ID_KEY = getString(R.string.physician_id_key);
        PATIENT_ID_KEY = getString(R.string.patient_id_key);

        // Get the physician ID from the intent extras
        physicianId = getIntent().getStringExtra(PHYSICIAN_ID_KEY);

        // Check if the physician ID is missing, log an error if it is
        if (physicianId == null) {
            Log.e(LOG_TAG, "This activity should not have been started without the DOCTOR's id!!");
        }

        // Get the patient ID from the intent extras
        patientId = getIntent().getStringExtra(PATIENT_ID_KEY);

        // Check if the patient ID is missing, log a message if it is
        if (patientId == null) {
            Log.e(LOG_TAG, "IN case you are interested the patient id is Null.");
        }

        // If the activity is being recreated after a configuration change,
        // restore the physicianId and patientId from the saved instance state
        if (savedInstanceState != null && (physicianId == null || patientId == null)) {
            if (physicianId == null) {
                physicianId = savedInstanceState.getString(PHYSICIAN_ID_KEY);
            }
            if (patientId == null) {
                patientId = savedInstanceState.getString(PATIENT_ID_KEY);
            }
        }

        // Retrieve the physician information from the server using the physicianId
        PhysicianManager.getPhysician(this, physicianId);

        // Retrieve all medications from the server
        MedicationManager.getAllMedications(this);

        // If a patientId is provided, retrieve the patient information from the server using the patientId
        if (patientId != null) {
            Log.d(LOG_TAG, "onCreate is getting the patient from the server id :" + patientId);
            PatientManager.getPatient(this, patientId);
        } else {
            Log.d(LOG_TAG, "NO patient id so we don't need to go get it from the server.");
        }
    }

    /**
     * Called to save the current instance state of the activity. This method is called before the activity
     * is destroyed, allowing the current state to be saved and later restored.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the patientId and physicianId to the outState bundle to restore later
        if (patientId != null) {
            outState.putString(PATIENT_ID_KEY, patientId);
        }
        if (physicianId != null) {
            outState.putString(PHYSICIAN_ID_KEY, physicianId);
        }
    }

    /**
     * Prepare the options menu before it is displayed. This method is called whenever the menu is
     * invalidated (e.g., when the user opens the menu). In this method, we dynamically remove certain
     * menu items based on the active fragment displayed in the activity.
     *
     * @param menu The options menu.
     * @return True if the menu should be displayed; false otherwise.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Get the active fragment in the activity
        Fragment frag = getActiveFragment();

        if (frag == null) {
            // If the active fragment is not found, log an error and continue with default behavior
            Log.e(LOG_TAG, "Active Fragment is not found!");
            return super.onPrepareOptionsMenu(menu);
        }

        // Remove certain menu items based on the active fragment
        if (frag instanceof PatientMedicationFragment) {
            menu.removeItem(R.id.action_medication_list);
        } else if (frag instanceof HistoryLogFragment) {
            menu.removeItem(R.id.action_history_log);
        } else if (frag instanceof MedicationListFragment) {
            menu.removeItem(R.id.action_medication_list);
            menu.removeItem(R.id.action_history_log);
        } else if (frag instanceof PatientGraphicsFragment) {
            menu.removeItem(R.id.action_chart);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Get the currently active fragment in the activity.
     *
     * @return The active fragment, or null if no active fragment is found.
     */
    public Fragment getActiveFragment() {
        Fragment medicationFrag;
        medicationFrag = getFragmentManager().findFragmentByTag(PatientMedicationFragment.FRAGMENT_TAG);
        if (medicationFrag != null && medicationFrag.isVisible()) {
            Log.d(LOG_TAG, "Medication Frag is visible.");
            return medicationFrag;
        }

        Fragment graphicsFrag;
        graphicsFrag = getFragmentManager().findFragmentByTag(PatientGraphicsFragment.FRAGMENT_TAG);
        if (graphicsFrag != null && graphicsFrag.isVisible()) {
            Log.d(LOG_TAG, "Graphics Frag is visible.");
            return graphicsFrag;
        }

        Fragment historyFrag;
        historyFrag = getFragmentManager().findFragmentByTag(HistoryLogFragment.FRAGMENT_TAG);
        if (historyFrag != null && historyFrag.isVisible()) {
            Log.d(LOG_TAG, "History Frag is visible.");
            return historyFrag;
        }

        Fragment medicationListFrag;
        medicationListFrag = getFragmentManager().findFragmentByTag(MedicationListFragment.FRAGMENT_TAG);
        if (medicationListFrag != null && medicationListFrag.isVisible()) {
            Log.d(LOG_TAG, "Medication List Frag is visible.");
            return medicationListFrag;
        }

        return null;
    }

    /**
     * Called when an options menu item is selected. This method handles the selection of menu items
     * and performs the corresponding actions based on the item's ID.
     *
     * @param item The selected menu item.
     * @return True if the item selection is handled here, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_medication_list) {
            // Replace the current fragment with the PatientMedicationFragment and add the transaction to the back stack
            getFragmentManager().beginTransaction()
                    .replace(R.id.patient_graphics_container,
                            new PatientMedicationFragment(),
                            PatientMedicationFragment.FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();

            // Invalidate the options menu to update its state
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.action_history_log) {
            // Replace the current fragment with the HistoryLogFragment
            getFragmentManager().beginTransaction()
                    .replace(R.id.patient_graphics_container,
                            new HistoryLogFragment(),
                            HistoryLogFragment.FRAGMENT_TAG)
                    .commit();

            // Invalidate the options menu to update its state
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.action_chart) {
            // Replace the current fragment with the PatientGraphicsFragment
            getFragmentManager().beginTransaction()
                    .replace(R.id.patient_graphics_container,
                            new PatientGraphicsFragment(),
                            PatientGraphicsFragment.FRAGMENT_TAG)
                    .commit();

            // Invalidate the options menu to update its state
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.physician_logout) {
            // Perform the action to restart the LoginActivity for physician logout
            LoginActivity.restartLoginActivity(this);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set the currently selected physician. This method is used to update the selected physician in the
     * activity and notify the PhysicianListPatientsFragment to update its data.
     *
     * @param physician The physician object to be set as the current selected physician.
     */
    public void setPhysician(Physician physician) {
        if (physician == null) {
            Log.e(LOG_TAG, "Trying to set physician to null value");
            return;
        }

        Log.d(LOG_TAG, "Current Selected Physician is : " + physician);

        // Update the current selected physician in the activity
        PhysicianActivity.physician = physician;

        // Find the PhysicianListPatientsFragment and update its data with the new physician
        Fragment listFrag;
        listFrag = getFragmentManager().findFragmentByTag(PhysicianListPatientsFragment.FRAGMENT_TAG);
        if (listFrag != null && listFrag instanceof PhysicianListPatientsFragment) {
            ((PhysicianListPatientsFragment) listFrag).updatePhysician(PhysicianActivity.physician);
        }
    }

    /**
     * Get the currently selected physician. This method is called by the PhysicianListPatientsFragment
     * to retrieve the selected physician for displaying the patient list associated with that physician.
     *
     * @return The currently selected physician object.
     */
    @Override
    public Physician getPhysicianForPatientList() {
        Log.d(LOG_TAG, "I am now GETTING Physician for Patient list.");
        return physician;
    }

    /**
     * Set the currently selected patient. This method is used to update the selected patient in the
     * activity and send the updated patient data to all relevant fragments.
     *
     * @param patient The patient object to be set as the current selected patient.
     */
    public void setPatient(Patient patient) {
        if (patient == null) {
            Log.e(LOG_TAG, "Trying to set patient to null value");
            return;
        }

        Log.d(LOG_TAG, "Current Selected Patient is : " + patient);

        // Update the current selected patient in the activity
        PhysicianActivity.patient = patient;

        // Send the updated patient data to all relevant fragments
        sendPatientToFragments(PhysicianActivity.patient);
    }

    /**
     * Send the updated patient data to all relevant fragments. This method is used to update the
     * patient data in the visible fragments when the selected patient is changed.
     *
     * @param patient The patient object containing the updated patient data.
     */
    private void sendPatientToFragments(Patient patient) {
        Log.d(LOG_TAG, "Sending Patient to the detail frag+ ....");

        // Find the PhysicianPatientDetailFragment and update its patient data with the new patient
        Fragment detailFrag;
        detailFrag = getFragmentManager().findFragmentByTag(PhysicianPatientDetailFragment.FRAGMENT_TAG);
        if (detailFrag != null && detailFrag.isVisible()
                && detailFrag instanceof PhysicianPatientDetailFragment) {
            Log.d(LOG_TAG, "Detail Frag is visible.");
            ((PhysicianPatientDetailFragment) detailFrag).updatePatient(patient);
        }

        Log.d(LOG_TAG, "Sending Patient to the medication frag+ ....");

        // Find the PatientMedicationFragment and update its patient data with the new patient
        Fragment medicationFrag;
        medicationFrag = getFragmentManager().findFragmentByTag(PatientMedicationFragment.FRAGMENT_TAG);
        if (medicationFrag != null && medicationFrag.isVisible() &&
                medicationFrag instanceof PatientMedicationFragment) {
            Log.d(LOG_TAG, "Medication Frag is visible.");
            ((PatientMedicationFragment) medicationFrag).updatePatient(patient);
        }

        Log.d(LOG_TAG, "Sending Patient to the graphics frag+ ....");

        // Find the PatientGraphicsFragment and update its patient data with the new patient
        Fragment graphicsFrag;
        graphicsFrag = getFragmentManager().findFragmentByTag(PatientGraphicsFragment.FRAGMENT_TAG);
        if (graphicsFrag != null && graphicsFrag.isVisible() &&
                graphicsFrag instanceof PatientGraphicsFragment) {
            Log.d(LOG_TAG, "Graphics Frag is visible.");
            ((PatientGraphicsFragment) graphicsFrag).updatePatient(patient);
        }

        Log.d(LOG_TAG, "Sending Patient to the history frag+ ....");

        // Find the HistoryLogFragment and update its patient data with the new patient
        Fragment historyFrag;
        historyFrag = getFragmentManager().findFragmentByTag(HistoryLogFragment.FRAGMENT_TAG);
        if (historyFrag != null && historyFrag.isVisible() &&
                historyFrag instanceof HistoryLogFragment) {
            Log.d(LOG_TAG, "History Frag is visible.");
            ((HistoryLogFragment) historyFrag).updatePatient(patient);
        }

        Log.d(LOG_TAG, "Sending Patient to the fragments is DONE.");
    }

    /**
     * Get the currently selected patient for displaying patient details.
     *
     * @return The currently selected patient object.
     */
    public Patient getPatientForDetails() {
        Log.d(LOG_TAG, "GETTING Selected Patient for Details : " + patient);
        return patient;
    }

    /**
     * Get the currently selected patient for graphing patient data.
     *
     * @return The currently selected patient object.
     */
    public Patient getPatientDataForGraphing() {
        Log.d(LOG_TAG, "GETTING Selected Patient for Graphing : " + patient);
        return patient;
    }

    /**
     * Get the currently selected patient for displaying the patient's history log.
     *
     * @return The currently selected patient object.
     */
    public Patient getPatientForHistory() {
        Log.d(LOG_TAG, "GETTING Selected Patient for History Log : " + patient);
        return patient;
    }

    /**
     * Get the currently selected patient for displaying prescription information.
     *
     * @return The currently selected patient object.
     */
    public Patient getPatientForPrescriptions() {
        Log.d(LOG_TAG, "GETTING Selected Patient for Prescriptions : " + patient);
        return patient;
    }

    /**
     * Get the collection of medications associated with the currently selected patient.
     *
     * @return The collection of medications associated with the patient.
     */
    public Collection<Medication> getMedications() {
        return medications;
    }

    /**
     * Callback when an item is selected from the list of patients.
     *
     * @param physicianId The ID of the physician.
     * @param patient     The selected patient object.
     */
    @Override
    public void onItemSelected(String physicianId, Patient patient) {
        if (patient == null || physicianId == null) {
            Log.d(LOG_TAG, "Invalid item selected.");
            return;
        }
        patientId = patient.getId();
        PatientManager.getPatient(this, patientId);
    }

    /**
     * Callback when the physician contacts a patient.
     *
     * @param patientId The ID of the contacted patient.
     * @param statusLog The status log of the contact.
     */
    public void onPatientContacted(String patientId, StatusLog statusLog) {
        if (physician == null || physician.getPatients() == null
                || patientId == null || patientId.isEmpty()) {
            Log.e(LOG_TAG, "INVALID IDS -- Unable to update the Dr.'s Status Log");
            return;
        }
        if (PhysicianManager.attachPhysicianStatusLog(physician, patientId, statusLog)) {
            PhysicianManager.savePhysician(this, physician);
        }
    }

    /**
     * Callback when a prescription is deleted.
     *
     * @param position   The position of the prescription in the list.
     * @param medication The medication to be deleted.
     */
    public void onPrescriptionDelete(final int position, Medication medication) {
        AlertDialog alert = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete_title))
                .setMessage(getString(R.string.confirm_delete_prescription))
                .setPositiveButton(getString(R.string.answer_yes), (dialog, which) -> {
                    PatientMedicationFragment frag =
                            (PatientMedicationFragment) getFragmentManager()
                                    .findFragmentByTag(PatientMedicationFragment.FRAGMENT_TAG);
                    if (frag != null) {
                        frag.deletePrescription(position);
                    } else {
                        Log.e(LOG_TAG, "Bad error .. could not find the medication fragment!");
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.answer_no), (dialog, which) -> dialog.dismiss()).create();
        alert.show();
    }

    /**
     * Callback when a prescription is requested to be added.
     */
    public void onRequestPrescriptionAdd() {
        getFragmentManager().beginTransaction()
                .replace(R.id.patient_graphics_container,
                        new MedicationListFragment(), MedicationListFragment.FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Callback when a medication is selected.
     *
     * @param medication The selected medication object.
     */
    public void onMedicationSelected(Medication medication) {
        onBackPressed();
        PatientMedicationFragment frag =
                (PatientMedicationFragment) getFragmentManager()
                        .findFragmentByTag(PatientMedicationFragment.FRAGMENT_TAG);
        frag.addPrescription(medication);
    }

    /**
     * Determine whether to show the add medication options menu.
     *
     * @return True if the options menu should be shown, false otherwise.
     */
    public boolean showAddMedicationOptionsMenu() {
        Log.d(LOG_TAG, "Detail Activity is showing the add medication options menu.");
        return true;
    }

    /**
     * Callback when adding a medication.
     */
    public void onAddMedication() {
        FragmentManager fm = getFragmentManager();
        MedicationAddEditDialog medicationDialog = MedicationAddEditDialog.newInstance(new Medication());
        medicationDialog.show(fm, MedicationAddEditDialog.FRAGMENT_TAG);
    }

    /**
     * Callback when saving a medication.
     *
     * @param medication The medication to be saved.
     */
    public void onSaveMedicationResult(final Medication medication) {
        // no name to work with so we aren't gonna do anything here
        if (medication.getName() == null || medication.getName().isEmpty()) {
            Log.d(LOG_TAG, "The user didn't really put a valid name so we aren't doing anything.");
            return;
        }
        MedicationManager.saveMedication(this, medication);
    }

    /**
     * Callback when medication addition/edit is cancelled.
     */
    public void onCancelMedicationResult() {
        Log.d(LOG_TAG, "Add/Edit Medication was cancelled.");
    }

    /**
     * Set the medication list to be displayed.
     *
     * @param medications The collection of medications to be displayed.
     */
    public void setMedicationList(Collection<Medication> medications) {
        PhysicianActivity.medications = medications;
        Fragment frag;
        frag = getFragmentManager().findFragmentByTag(MedicationListFragment.FRAGMENT_TAG);
        if (frag != null) {
            ((MedicationListFragment) frag).updateMedications(medications);
        }
    }

    /**
     * Callback when a name is selected.
     *
     * @param lastName  The last name selected.
     * @param firstName The first name selected.
     */
    @Override
    public void onNameSelected(String lastName, String firstName) {
        Toast.makeText(getApplication(),
                "Name selected is " + getName(lastName, firstName),
                Toast.LENGTH_LONG).show();
    }

    /**
     * Display a toast when a successful search is performed.
     *
     * @param patient The patient found during the search.
     */
    public void successfulSearch(Patient patient) {
        Toast.makeText(getApplication(), patient.toString() + " Found.",
                Toast.LENGTH_LONG).show();
    }

    /**
     * Display a toast when a search fails.
     *
     * @param message The failure message to be displayed.
     */
    @Override
    public void failedSearch(String message) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * Get the full name from the given last name and first name.
     *
     * @param lastName  The last name of the person.
     * @param firstName The first name of the person.
     * @return The full name in the format "firstName lastName".
     */
    public static String getName(String lastName, String firstName) {
        String name = "";
        if (firstName != null && !firstName.isEmpty()) {
            name += firstName;
        }
        if (!name.isEmpty()) {
            name += " ";
        }
        if (lastName != null && !lastName.isEmpty()) {
            name += lastName;
        }
        return name;
    }
}
