package com.example.symptommanagement.physician;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.Medication;
import com.example.symptommanagement.data.Patient;

import java.util.Collection;
import java.util.HashSet;

/**
 * A fragment class to display and manage a list of prescriptions for a patient.
 */
public class PatientMedicationFragment extends ListFragment {

    /**
     * Tag for logging purposes.
     */
    private static final String LOG_TAG = PatientMedicationFragment.class.getSimpleName();

    /**
     * Fragment tag for identification.
     */
    public final static String FRAGMENT_TAG = "fragment_patient_medication";

    /**
     * Interface for callback methods that the hosting activity must implement.
     */
    public interface Callbacks {
        /**
         * Called when the user requests to add a new prescription.
         */
        void onRequestPrescriptionAdd();

        /**
         * Get the patient object to display prescriptions for.
         *
         * @return The patient object.
         */
        Patient getPatientForPrescriptions();
    }

    /**
     * The current patient whose prescriptions are being displayed.
     */
    private static Patient patient;

    /**
     * The array of medications representing the prescriptions for the current patient.
     */
    private static Medication[] meds;

    /**
     * Lifecycle method: Called when the fragment is created.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set to have an options menu
        setHasOptionsMenu(true);
    }

    /**
     * Lifecycle method: Called when the fragment is attached to an activity.
     *
     * @param activity The hosting activity.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Check if the hosting activity implements the Callbacks interface
        if (!(activity instanceof Callbacks)) {
            // Throw an IllegalStateException if the activity does not implement the required interface
            throw new IllegalStateException(activity.getString(R.string.callbacks_message));
        }
    }

    /**
     * Lifecycle method: Called to create the options menu.
     *
     * @param menu     The options menu.
     * @param inflater The menu inflater.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the medication_add_menu.xml to add items to the options menu
        inflater.inflate(R.menu.medication_add_menu, menu);
    }

    /**
     * Lifecycle method: Called when an options menu item is selected.
     *
     * @param item The selected menu item.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            // If the "Add" menu item is clicked, call the onRequestPrescriptionAdd() method on the hosting activity
            ((Callbacks) getActivity()).onRequestPrescriptionAdd();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Lifecycle method: Called when the activity's onCreate() method has returned.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set the empty text to display when the list is empty
        setEmptyText(getString(R.string.empty_list_text));
        // Retain the instance of the fragment across configuration changes
        setRetainInstance(true);
    }

    /**
     * Lifecycle method: Called when the fragment is resumed.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Get the patient object for displaying prescriptions from the hosting activity
        patient = ((Callbacks) getActivity()).getPatientForPrescriptions();
        // Update the displayed list of prescriptions for the patient
        displayPrescriptions(patient);
    }

    /**
     * Update the patient data and refresh the display.
     *
     * @param patient The new patient object.
     */
    public void updatePatient(Patient patient) {
        if (patient == null) {
            // Log an error if the provided patient is null
            Log.e(LOG_TAG, "Trying to set patient medication patient to null.");
            return;
        }
        // Log the arrival of a new patient
        Log.d(LOG_TAG, "New Patient has arrived!" + patient);
        // Update the patient data and refresh the displayed list
        PatientMedicationFragment.patient = patient;
        displayPrescriptions(PatientMedicationFragment.patient);
    }

    /**
     * Display the list of prescriptions for a patient.
     *
     * @param patient The patient object.
     */
    private void displayPrescriptions(Patient patient) {
        if (patient == null) {
            // Log an error if the provided patient is null
            Log.e(LOG_TAG, "Trying to display prescriptions for null patient.");
            return;
        }
        // Log that the display list for prescriptions is being updated
        Log.d(LOG_TAG, "We are updating the display list for Prescriptions.");
        // Initialize an empty set of prescriptions if the patient's prescription set is null
        if (patient.getPrescriptions() == null)
            patient.setPrescriptions(new HashSet<>());
        // Convert the patient's prescription set to an array of medications
        if (patient.getPrescriptions() != null) {
            meds = patient.getPrescriptions().toArray(new Medication[0]);
        }
        // Set the PrescriptionAdapter as the adapter for the ListFragment to display the prescriptions
        setListAdapter(new PrescriptionAdapter(getActivity(), meds));
    }

    /**
     * Add a new prescription for the patient.
     *
     * @param medication The new medication to be added.
     */
    public void addPrescription(Medication medication) {
        if (patient == null || medication == null) {
            // Log a message if there is no current patient or medication to process
            Log.d(LOG_TAG, "No current patient or medication to process.");
            return;
        }
        // Initialize the prescriptions set for the patient if it is null
        if (patient.getPrescriptions() == null) {
            patient.setPrescriptions(new HashSet<>());
        }
        // Add the new medication to the patient's prescriptions set
        patient.getPrescriptions().add(medication);
        // Log the updated patient data and send it to the server
        Log.d(LOG_TAG, "Sending this updated patient to the server" + patient.toString());
        PatientManager.updatePatient(getActivity(), patient);
    }

    /**
     * Delete a prescription at the given position from the patient's list of prescriptions.
     *
     * @param position The position of the prescription to be deleted.
     */
    public void deletePrescription(int position) {
        Collection<Medication> p = patient.getPrescriptions();
        // Remove the medication at the specified position from the patient's prescriptions set
        p.remove(meds[position]);
        // Update the patient's prescriptions set with the modified set of prescriptions
        patient.setPrescriptions(new HashSet<>(p));
        // Log the updated patient data and send it to the server
        Log.d(LOG_TAG, "Sending this updated patient to the server" + patient.toString());
        PatientManager.updatePatient(getActivity(), patient);
    }
}
