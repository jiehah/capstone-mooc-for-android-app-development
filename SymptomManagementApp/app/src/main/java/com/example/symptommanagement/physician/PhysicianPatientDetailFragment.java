package com.example.symptommanagement.physician;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.Patient;
import com.example.symptommanagement.data.StatusLog;
import com.example.symptommanagement.databinding.FragmentPhysicianPatientDetailLargeBinding;

/**
 * This fragment displays detailed information about a specific patient for the physician.
 * It communicates with the hosting activity through the Callbacks interface to get the patient's data.
 */
public class PhysicianPatientDetailFragment extends Fragment {

    private final static String LOG_TAG = PhysicianPatientDetailFragment.class.getSimpleName();

    /**
     * Fragment tag used to identify this fragment
     */
    public final static String FRAGMENT_TAG = "fragment_details";

    /**
     * ViewBinding variable for the layout
     */
    private FragmentPhysicianPatientDetailLargeBinding binding;

    /**
     * Interface for communication with the hosting activity.
     */
    public interface Callbacks {
        /**
         * Get the Patient object to display details for.
         *
         * @return The Patient object containing patient details.
         */
        Patient getPatientForDetails();

        /**
         * Notify the hosting activity that the patient has been contacted with a status log.
         *
         * @param patientId The ID of the patient being contacted.
         * @param statusLog The StatusLog object representing the patient contact.
         */
        void onPatientContacted(String patientId, StatusLog statusLog);
    }

    // Static variable to hold the patient data
    private static Patient patient;

    /**
     * Called when the fragment is being created.
     * Enable options menu and retain the instance state to preserve data across configuration changes.
     *
     * @param savedInstanceState A Bundle containing the saved state of the fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable options menu
        setHasOptionsMenu(true);
        // Retain the instance state to preserve data across configuration changes
        setRetainInstance(true);
    }

    /**
     * Called when the fragment is being attached to an activity.
     * Check if the hosting activity implements the Callbacks interface and throw an exception if not.
     *
     * @param activity The hosting activity.
     * @throws IllegalStateException if the hosting activity does not implement the Callbacks interface.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Check if the hosting activity implements the Callbacks interface
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(activity.getString(R.string.callbacks_message));
        }
    }

    /**
     * Called when the fragment's view is being created.
     * Inflate the large layout using ViewBinding and return the root view.
     *
     * @param inflater           The LayoutInflater used to inflate the layout.
     * @param container          The parent ViewGroup.
     * @param savedInstanceState A Bundle containing the saved state of the fragment.
     * @return The root view of the fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the large layout using ViewBinding
        binding = FragmentPhysicianPatientDetailLargeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called when the fragment is becoming visible to the user.
     * Get the patient data from the hosting activity and display it in the UI.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Get the patient data from the hosting activity and display it
        patient = ((Callbacks) getActivity()).getPatientForDetails();
        displayPatient();
    }

    /**
     * Update the patient data and display it on the UI.
     *
     * @param patient The Patient object containing updated patient details.
     */
    public void updatePatient(Patient patient) {
        if (patient == null) {
            Log.e(LOG_TAG, "Trying to set details patient to null.");
            return;
        }
        Log.d(LOG_TAG, "New Patient has arrived! " + patient.toString());
        PhysicianPatientDetailFragment.patient = patient;
        displayPatient();
    }

    /**
     * Display the patient details in the UI.
     * If the patient is not null, update the display with patient data, such as name, birthdate, and medical ID.
     */
    private void displayPatient() {
        if (patient != null) {
            // Update the display with patient data
            binding.physicianPatientDetailName.setText(patient.getName());
            if (binding.physicianPatientDetailBirthdate != null) {
                binding.physicianPatientDetailBirthdate.setText(patient.getBirthdate());
            }
            if (binding.patientMedicalId != null) {
                binding.patientMedicalId.setText(patient.getId());
            }
        }
    }

    /**
     * Inflate the options menu with the "Contacted Patient" menu item.
     *
     * @param menu     The options menu in which the menu items are placed.
     * @param inflater The MenuInflater object to inflate the menu layout.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.physician_patient_contact_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Handle menu item clicks.
     *
     * @param item The selected menu item.
     * @return true if the menu item is handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Do nothing if there is no patient
        if (patient == null) return true;

        int id = item.getItemId();
        if (id == R.id.action_add_status) {
            // Display a confirmation dialog when adding a status log
            if (patient != null) {
                AlertDialog alert = new AlertDialog.Builder(getActivity())
                        .setTitle(getActivity().getString(R.string.confirm_patient_contacted_title))
                        .setMessage(getActivity().getString(R.string.ask_patient_contacted))
                        .setPositiveButton(getActivity().getString(R.string.answer_yes),
                                (dialog, which) -> {
                                    // Tell the hosting activity to handle adding a status log
                                    ((Callbacks) getActivity()).onPatientContacted(
                                            patient.getId(),
                                            new StatusLog(
                                                    getActivity().getString(R.string.patient_contact_status),
                                                    System.currentTimeMillis()
                                            )
                                    );
                                    dialog.dismiss();
                                })
                        .setNegativeButton(getActivity().getString(R.string.answer_no),
                                (dialog, which) -> { /* Do nothing */
                                    dialog.dismiss();
                                })
                        .create();
                alert.show();
            } else {
                Log.e(LOG_TAG, "No patient loaded for physician to contact.");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
