package com.example.symptommanagement.admin.Patient;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.example.symptommanagement.R;
import com.example.symptommanagement.client.CallableTask;
import com.example.symptommanagement.client.SymptomManagementApi;
import com.example.symptommanagement.client.SymptomManagementService;
import com.example.symptommanagement.client.TaskCallback;
import com.example.symptommanagement.data.Patient;
import com.example.symptommanagement.data.Physician;
import com.example.symptommanagement.databinding.FragmentAdminPatientDetailBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A fragment that displays the details of a specific patient.
 * This fragment is used in the admin section to view patient details.
 * It retrieves patient information from the API and displays it in the UI.
 * The user can also choose to edit or delete the patient using options in the menu.
 */
public class PatientDetailFragment extends Fragment {

    private static final String LOG_TAG = PatientDetailFragment.class.getSimpleName();

    /**
     * Key for the patient ID received from the calling activity
     */
    private final static String PATIENT_ID_KEY = AdminPatientListActivity.PATIENT_ID_KEY;
    private String patientId;
    private Patient patient;

    /**
     * Binding object for the fragment layout
     */
    private FragmentAdminPatientDetailBinding binding;

    /**
     * Interface to communicate with the calling activity
     */
    public interface Callbacks {
        /**
         * Called when the user selects the "Edit" option from the options menu.
         * This method is implemented by the calling activity to handle the edit action for the patient.
         *
         * @param id The ID of the patient to be edited.
         */
        void onEditPatient(String id);
    }

    /**
     * Creates and returns the view for the fragment.
     * Initializes the fragment's UI, retrieves patient ID from arguments or saved instance state,
     * and sets up options menu.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A Bundle containing previously saved state.
     * @return The created view for the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            // Retrieve patient ID from arguments
            patientId = arguments.getString(PATIENT_ID_KEY);
        } else if (savedInstanceState != null) {
            // Restore patient ID from saved instance state
            patientId = savedInstanceState.getString(PATIENT_ID_KEY);
        }

        // Inflate the fragment layout and set up options menu
        binding = FragmentAdminPatientDetailBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        return binding.getRoot();
    }

    /**
     * Creates the options menu for the fragment.
     * Inflates the menu layout containing "Edit" and "Delete" options.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater The MenuInflater object that can be used to inflate any views in the menu.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.admin_edit_delete_menu, menu);
    }

    /**
     * Handles selection of items from the options menu.
     * If "Edit" option is selected, it notifies the calling activity to edit the patient.
     * If "Delete" option is selected, it deletes the patient using the API and shows a toast on success.
     *
     * @param item The menu item that was selected.
     * @return true if the item was handled successfully, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                // Notify the calling activity to edit the patient
                ((Callbacks) getActivity()).onEditPatient(patientId);
                return true;
            case R.id.action_delete:
                // Delete the patient using the API
                deletePatient();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the fragment is resumed.
     * If the patient ID is available, it retrieves the patient information from the API and updates the UI.
     */
    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(PATIENT_ID_KEY)) {
            // Retrieve patient ID from arguments and load patient information from the API
            patientId = arguments.getString(PATIENT_ID_KEY);
            loadPatientFromAPI();
        }
    }

    /**
     * Saves the current state of the fragment.
     * It saves the patient ID to be restored later in case the fragment is destroyed and recreated.
     *
     * @param outState Bundle in which to place saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(PATIENT_ID_KEY, patientId);
        super.onSaveInstanceState(outState);
    }

    /**
     * Retrieves the patient information from the API using the patient ID.
     * It displays the patient's name, birthdate, and physician list in the UI.
     */
    private void loadPatientFromAPI() {
        Log.d(LOG_TAG, "Patient ID is : " + patientId);
        final SymptomManagementApi svc = SymptomManagementService.getService();
        if (svc != null) {
            // Invoke the API call asynchronously using CallableTask
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "getting single Patient id : " + patientId);
                return svc.getPatient(patientId);
            }, new TaskCallback<Patient>() {

                @Override
                public void success(Patient result) {
                    // Display the retrieved patient information in the UI
                    Log.d(LOG_TAG, "Found Patient :" + result.toString());
                    patient = result;
                    binding.adminPatientDetail.setText(patient.getName());
                    binding.adminPatientDetailBirthdate.setText(patient.getBirthdate());
                    displayPhysicianList(patient.getPhysicians());
                }

                @Override
                public void error(Exception e) {
                    // Show an error toast and navigate back on failure
                    Toast.makeText(
                            getActivity(),
                            "Unable to fetch Selected Patient. Please check Internet connection.",
                            Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }
            });
        }
    }

    /**
     * Displays the list of physicians in the UI.
     * If the provided collection of physicians is empty or null, it displays a placeholder
     * indicating that there are no physicians for this patient.
     *
     * @param physicians A collection of Physician objects to be displayed.
     */
    private void displayPhysicianList(Collection<Physician> physicians) {
        if (physicians == null || physicians.size() == 0) {
            // If no physicians available, display a placeholder physician with a message
            final List<Physician> emptyList = new ArrayList<>();
            Physician emptyPhysician = new Physician("No Physicians for this Patient.", "");
            emptyList.add(emptyPhysician);
            binding.patientPhysiciansList.setAdapter(new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text1,
                    new ArrayList<>(emptyList)));
        } else {
            // Display the list of physicians using ArrayAdapter
            binding.patientPhysiciansList.setAdapter(new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text1,
                    new ArrayList<>(physicians)));
        }
    }

    /**
     * Deletes the patient using the API and shows a toast message on success.
     * If there is an error while deleting, it shows an error toast and navigates back to the previous screen.
     */
    public void deletePatient() {
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        if (symptomManagementApi != null) {
            // Invoke the API call asynchronously using CallableTask
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "deleting Physician id : " + patientId);
                return symptomManagementApi.deletePatient(patientId);
            }, new TaskCallback<Patient>() {

                @Override
                public void success(Patient result) {
                    // Show a success toast and navigate back on successful deletion
                    Toast.makeText(
                            getActivity(),
                            "Patient [" + result.getName() + "] deleted successfully.",
                            Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }

                @Override
                public void error(Exception e) {
                    // Show an error toast and navigate back on failure
                    Toast.makeText(
                            getActivity(),
                            "Unable to delete Patient. Please check Internet connection.",
                            Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }
            });
        }
    }
}
