package com.example.symptommanagement.admin.Patient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.symptommanagement.client.CallableTask;
import com.example.symptommanagement.client.SymptomManagementApi;
import com.example.symptommanagement.client.SymptomManagementService;
import com.example.symptommanagement.client.TaskCallback;
import com.example.symptommanagement.data.Patient;
import com.example.symptommanagement.data.Physician;
import com.example.symptommanagement.databinding.FragmentAdminPatientAddEditBinding;

import java.util.*;

/**
 * A fragment that allows users to add or edit patient information.
 */
public class PatientAddEditFragment extends Fragment {
    private static final String LOG_TAG = PatientAddEditFragment.class.getSimpleName();
    private final static String PATIENT_ID_KEY = AdminPatientListActivity.PATIENT_ID_KEY;
    private static Patient patient;
    private String patientId;
    private FragmentAdminPatientAddEditBinding binding;

    /**
     * Called when the activity that hosts the fragment has been created.
     * This method sets up the UI components and click listeners after the activity has been created.
     *
     * @param savedInstanceState The saved state of the fragment, if available.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Restore patientId from the saved instance state, if available
        if (savedInstanceState != null) {
            patientId = savedInstanceState.getString(PATIENT_ID_KEY);
        }

        // Retrieve patientId from the fragment's arguments bundle, if present
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(PATIENT_ID_KEY)) {
            patientId = getArguments().getString(PATIENT_ID_KEY);
        }

        // Set click listeners for the save, addPhysician, and pickBirthdate buttons
        binding.savePatientButton.setOnClickListener(v -> savePatient());
        binding.addPhysicianButton.setOnClickListener(v -> addPhysician());
        binding.pickBirthdate.setOnClickListener(v -> showDatePickerDialog());
    }

    /**
     * Called when the fragment is being created.
     * This method retrieves the patientId from the fragment's arguments bundle, if present.
     *
     * @param savedInstanceState The saved state of the fragment, if available.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve patientId from the fragment's arguments bundle, if present
        if (getArguments().containsKey(PATIENT_ID_KEY)) {
            patientId = getArguments().getString(PATIENT_ID_KEY);
        }
    }

    /**
     * Called when the fragment's view is being created.
     * This method inflates the fragment's layout using data binding and initializes the patient object.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState The saved state of the fragment, if available.
     * @return The root view of the fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Retain the instance of the fragment to keep it alive during configuration changes
        setRetainInstance(true);

        // Enable options menu in the fragment
        setHasOptionsMenu(true);

        // Inflate the fragment's layout using data binding
        binding = FragmentAdminPatientAddEditBinding.inflate(inflater, container, false);

        // Initialize the patient object
        patient = new Patient();

        // Retrieve patientId from the fragment's arguments bundle, if present
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(PATIENT_ID_KEY)) {
            patientId = getArguments().getString(PATIENT_ID_KEY);
        }

        // Return the root view of the fragment's layout
        return binding.getRoot();
    }

    /**
     * Called when the fragment is resumed.
     * This method is responsible for handling the fragment's resumption logic,
     * including fetching and displaying patient data from the API if a patientId is available.
     */
    @Override
    public void onResume() {
        super.onResume();

        // Retrieve patientId from the fragment's arguments bundle, if present
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(PATIENT_ID_KEY)) {
            patientId = getArguments().getString(PATIENT_ID_KEY);

            // Fetch patient data from the API if a patientId is available
            loadPatientFromAPI();
        }
    }

    /**
     * Called to save the state of the fragment across configuration changes.
     * This method saves the patientId in the instance state bundle.
     *
     * @param outState The Bundle in which to place the saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save the patientId in the instance state bundle
        outState.putString(PATIENT_ID_KEY, patientId);
        super.onSaveInstanceState(outState);
    }

    /**
     * Fetches patient details from the API using the provided patientId.
     * If patientId is null, no data will be fetched.
     * This method uses an asynchronous API call and updates the UI with the fetched data on success.
     */
    private void loadPatientFromAPI() {
        if (patientId == null) return;

        // Log the patientId for debugging purposes
        Log.d(LOG_TAG, "Physician ID Key is : " + patientId);

        // Retrieve the SymptomManagementApi instance
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        if (symptomManagementApi != null) {
            // Invoke the API call asynchronously using CallableTask
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "getting single patient with id : " + patientId);
                return symptomManagementApi.getPatient(patientId);
            }, new TaskCallback<Patient>() {

                @Override
                public void success(Patient result) {
                    // Display the fetched patient data in the UI on success
                    Log.d(LOG_TAG, "Found Patient :" + result.toString());
                    patient = result;
                    binding.editFirstName.setText(patient.getFirstName());
                    binding.editLastName.setText(patient.getLastName());
                    binding.displayBirthdate.setText(patient.getBirthdate());
                    displayPhysicians(patient.getPhysicians());
                }

                @Override
                public void error(Exception e) {
                    // Show an error toast and navigate back if fetching fails
                    Toast.makeText(
                            getActivity(),
                            "Unable to fetch Patient for editing. Please check Internet connection.",
                            Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }
            });
        }
    }

    /**
     * Saves or updates the patient's information in the API.
     * This method performs input validation for first name and last name before making the API call.
     * If the first name or last name is empty, it shows an error dialog and cancels the operation.
     * If patientId is null, a new patient is added; otherwise, the existing patient is updated.
     */
    public void savePatient() {
        // Check if first name and last name are valid
        if (binding.editFirstName.getText().toString().trim().length() == 0 &&
                binding.editLastName.getText().toString().trim().length() == 0) {
            // Show an error dialog if the names are not valid
            DialogFragment errorSaving = new DialogFragment() {
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(getActivity());
                    builder.setMessage("Unable to Save patient. Please Enter a valid first and last name.");
                    builder.setPositiveButton("OK", null);
                    return builder.create();
                }
            };
            errorSaving.show(getFragmentManager(), "Error saving/updating patient");
            return;
        }

        // Retrieve the SymptomManagementApi instance
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        final String successMsg = (patientId == null ? "ADDED" : "UPDATED");
        if (symptomManagementApi != null) {
            // Invoke the API call asynchronously using CallableTask
            CallableTask.invoke(() -> {
                // Set the patient's ID, first name, and last name for the API call
                patient.setId(patientId);
                patient.setFirstName(binding.editFirstName.getText().toString());
                patient.setLastName(binding.editLastName.getText().toString());

                // Check if it's a new patient or an update
                if (patientId == null) {
                    // Add the new patient using the API
                    Log.d(LOG_TAG, "adding patient :" + patient.toString());
                    return symptomManagementApi.addPatient(patient);
                } else {
                    // Update the existing patient using the API
                    Log.d(LOG_TAG, "updating patient :" + patient.toString());
                    return symptomManagementApi.updatePatient(patientId, patient);
                }
            }, new TaskCallback<Patient>() {

                @Override
                public void success(Patient result) {
                    // Show a success toast and navigate back on successful save/update
                    Toast.makeText(
                            getActivity(),
                            "Patient [" + result.getName() + "] " + successMsg + " successfully.",
                            Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }

                @Override
                public void error(Exception e) {
                    // Show an error toast and navigate back on failure
                    Toast.makeText(
                            getActivity(),
                            "Unable to SAVE Patient. Please check Internet connection.",
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
    private void displayPhysicians(Collection<Physician> physicians) {
        if (physicians == null || physicians.size() == 0) {
            // Create a list with a single "No Physicians" placeholder entry
            final List<Physician> emptyList = new ArrayList<>();
            Physician emptyPhysician = new Physician("No Physicians for this Patient.", "");
            emptyList.add(emptyPhysician);
            Physician[] plist = emptyList.toArray(new Physician[1]);

            // Set the adapter with the placeholder list
            binding.adminPatientPhysicianListview.setAdapter(new PhysicianEditListAdapter(getActivity(), plist));
        } else {
            // Convert the collection of physicians to an array and set the adapter
            Physician[] plist = physicians.toArray(new Physician[0]);
            binding.adminPatientPhysicianListview.setAdapter(new PhysicianEditListAdapter(getActivity(), plist));
        }
    }

    /**
     * Updates the patient's information in the API.
     * If patientId is null or empty, it adds a new patient; otherwise, it updates the existing patient.
     * This method performs the API call asynchronously using CallableTask and shows a toast on success.
     * If the update is successful, the patient data is refreshed, and the fragment's UI is updated.
     */
    public void updatePatient() {
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        if (symptomManagementApi != null) {
            // Invoke the API call asynchronously using CallableTask
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "updating patient :" + patient.toString());

                // Check if it's a new patient or an update
                if (patientId == null || patient.getId() == null ||
                        patientId.isEmpty() || patient.getId().isEmpty()) {
                    // Add the new patient using the API
                    return symptomManagementApi.addPatient(patient);
                } else {
                    // Update the existing patient using the API
                    return symptomManagementApi.updatePatient(patientId, patient);
                }
            }, new TaskCallback<Patient>() {

                @Override
                public void success(Patient result) {
                    // Show a success toast on successful update
                    Toast.makeText(
                            getActivity(),
                            "Patient [" + result.getName() + "] added/updated successfully.",
                            Toast.LENGTH_SHORT).show();

                    // Refresh patient data and update UI
                    patient = result;
                    patientId = patient.getId();
                    onResume();
                }

                @Override
                public void error(Exception e) {
                    // Show an error toast on update failure
                    Toast.makeText(
                            getActivity(),
                            "Unable to Update Patient. Please check Internet connection.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Launches the PatientPhysicianListActivity to allow adding a new physician for the patient.
     */
    public void addPhysician() {
        Intent intent = new Intent(getActivity(), PatientPhysicianListActivity.class);
        startActivityForResult(intent, 2);
    }

    /**
     * Called when the result from another activity is received.
     * This method is used to handle the result of launching the PatientPhysicianListActivity.
     * If the requestCode is 2 (result from adding a physician), the new physician information is retrieved
     * from the intent data and added to the patient's physician list.
     * If the physician information is valid (non-empty ID, first name, and last name), the patient data is updated.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult().
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent that carries the result data.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(LOG_TAG, "OnActivityResult for Request code: " + requestCode);

        // Check if the result is from adding a physician (requestCode == 2)
        if (requestCode == 2) {
            Log.v(LOG_TAG, "Saving the new physician information to patient record.");
            Physician p = new Physician();
            p.setId(data.getStringExtra(PatientPhysicianListActivity.PHYSICIAN_ID_KEY));
            p.setFirstName(data.getStringExtra(PatientPhysicianListActivity.PHYSICIAN_FIRST_NAME_KEY));
            p.setLastName(data.getStringExtra(PatientPhysicianListActivity.PHYSICIAN_LAST_NAME_KEY));

            // Add the new physician to the patient's physician list
            if (patient.getPhysicians() != null) {
                patient.getPhysicians().add(p);
            } else {
                final Set<Physician> newSet = new HashSet<>();
                newSet.add(p);
                patient.setPhysicians(newSet);
            }

            // Check if the physician information is valid (non-empty ID, first name, and last name)
            if (p.getId() != null && !p.getId().isEmpty()
                    && p.getFirstName() != null && !p.getFirstName().isEmpty()
                    && p.getLastName() != null && !p.getLastName().isEmpty()) {
                // Update the patient's information in the API with the new physician
                updatePatient();
            }
        }
    }

    /**
     * Shows the date picker dialog to allow the user to select a birthdate for the patient.
     * This method creates and shows a BirthdateDialog fragment with the current birthdate as a parameter.
     */
    public void showDatePickerDialog() {
        BirthdateDialog newFragment = BirthdateDialog.newInstance(patient.getBirthdate());
        newFragment.show(getFragmentManager(), "birthdayPicker");
    }

    /**
     * Called when the user selects a positive result (date) from the date picker dialog.
     * This method sets the selected birthdate for the patient and updates the UI with the new birthdate.
     *
     * @param bday The selected birthdate.
     */
    public void onPositiveResult(String bday) {
        patient.setBirthdate(bday);
        binding.displayBirthdate.setText(patient.getBirthdate());
    }

    /**
     * Called when the user cancels the date picker dialog.
     * This method sets the patient's birthdate back to its original value and updates the UI accordingly.
     */
    public void onNegativeResult() {
        binding.displayBirthdate.setText(patient.getBirthdate());
    }
}
