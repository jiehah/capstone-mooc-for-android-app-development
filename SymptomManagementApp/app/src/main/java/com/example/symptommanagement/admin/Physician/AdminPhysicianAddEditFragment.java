package com.example.symptommanagement.admin.Physician;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
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
import com.example.symptommanagement.data.Physician;
import com.example.symptommanagement.databinding.FragmentAdminPhysicianAddEditBinding;

/**
 * A fragment for adding or editing physician details.
 */
public class AdminPhysicianAddEditFragment extends Fragment {
    private static final String LOG_TAG = AdminPhysicianAddEditFragment.class.getSimpleName();

    /**
     * Key for accessing physician ID from arguments or saved instance state
     */
    private final static String PHYSICIAN_ID_KEY = AdminPhysicianListActivity.PHYSICIAN_ID_KEY;
    private Physician physician;
    private String physicianId;
    private FragmentAdminPhysicianAddEditBinding binding;

    /**
     * Called when the activity's onCreate() method has been completed.
     * This method is responsible for initializing the fragment's UI components and setting up click listeners.
     *
     * @param savedInstanceState The saved state of the fragment, containing the previous state data if available.
     *                           This bundle is used to restore the physicianId if the fragment is being recreated.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Restore the physicianId from the saved instance state, if available
        if (savedInstanceState != null) {
            physicianId = savedInstanceState.getString(PHYSICIAN_ID_KEY);
        }

        // Check if the fragment was created with arguments containing the physicianId
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(PHYSICIAN_ID_KEY)) {
            physicianId = getArguments().getString(PHYSICIAN_ID_KEY);
        }

        // Set a click listener for the "Save" button to handle physician saving
        binding.savePhysicianButton.setOnClickListener(v -> savePhysician());
    }

    /**
     * Called when the fragment is being created.
     * This method is responsible for initializing the fragment and extracting the physicianId from the arguments.
     *
     * @param savedInstanceState The saved state of the fragment, containing the previous state data if available.
     *                           This bundle is used to retrieve the physicianId from the arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the fragment was created with arguments containing the physicianId
        if (getArguments().containsKey(PHYSICIAN_ID_KEY)) {
            physicianId = getArguments().getString(PHYSICIAN_ID_KEY);
        }
    }

    /**
     * Called when the fragment's UI is being created.
     * This method inflates the fragment's layout and prepares the UI elements for display.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState The saved state of the fragment, containing the previous state data if available.
     * @return The root View of the inflated layout for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminPhysicianAddEditBinding.inflate(inflater, container, false);

        // Retain the fragment instance across configuration changes
        setRetainInstance(true);

        // Enable options menu handling in the fragment
        setHasOptionsMenu(true);

        // Initialize the physician object
        physician = new Physician();

        // Check if the fragment was created with arguments containing the physicianId,
        // and if the physicianId is available, load the physician details from the API
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(PHYSICIAN_ID_KEY) && physicianId != null) {
            physicianId = getArguments().getString(PHYSICIAN_ID_KEY);
            loadPhysicianFromAPI();
        }

        return binding.getRoot();
    }

    /**
     * Called when the fragment is resumed.
     * This method is responsible for handling any setup or data retrieval tasks that need to be performed
     * when the fragment is brought back to the foreground of the activity.
     */
    @Override
    public void onResume() {
        super.onResume();

        // Check if the fragment was created with arguments containing the physicianId,
        // and if the physicianId is available, load the physician details from the API
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(PHYSICIAN_ID_KEY) && physicianId != null) {
            physicianId = getArguments().getString(PHYSICIAN_ID_KEY);
            loadPhysicianFromAPI();
        }
    }

    /**
     * Called to save the current state of the fragment.
     * This method saves the physicianId to the saved instance state bundle,
     * so that it can be restored later if the fragment is recreated.
     *
     * @param outState The Bundle in which to place the saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(PHYSICIAN_ID_KEY, physicianId);
        super.onSaveInstanceState(outState);
    }

    /**
     * Fetches the details of a single physician from the API using the specified `physicianId`.
     * The method queries the API to get the details of the physician with the provided ID and
     * updates the UI elements to display the fetched information.
     * If the API call is successful, the physician's first name and last name are displayed in the UI.
     * If the API call fails, a toast message is shown indicating the failure, and the activity is
     * navigated back to the previous screen.
     */
    private void loadPhysicianFromAPI() {
        // If physicianId is null, there's no valid physician to fetch, so return
        if (physicianId == null) return;

        // Log the physician ID to debug the process
        Log.d(LOG_TAG, "LoadFromAPI - Physician ID Key is : " + physicianId);

        // Get the SymptomManagementApi instance
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();

        // Check if the API instance is available
        if (symptomManagementApi != null) {
            // Use CallableTask to invoke the API call in a background thread
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "getting single physician with id : " + physicianId);
                // Invoke the API method to get the physician details
                return symptomManagementApi.getPhysician(physicianId);
            }, new TaskCallback<Physician>() {
                @Override
                public void success(Physician result) {
                    // If the API call is successful, update the UI with the retrieved physician details
                    Log.d(LOG_TAG, "Found Physician :" + result.toString());
                    physician = result;
                    binding.adminPhysicianEditFirstName.setText(physician.getFirstName());
                    binding.adminPhysicianEditLastName.setText(physician.getLastName());
                }

                @Override
                public void error(Exception e) {
                    // If the API call fails, show a toast message indicating the failure
                    // and navigate back to the previous screen
                    Toast.makeText(
                            getActivity(),
                            "Unable to fetch Physician for editing. Please check Internet connection.",
                            Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }
            });
        }
    }

    /**
     * Saves or updates the details of the physician based on the input data.
     * The method checks if the first name and last name fields are empty. If they are,
     * it displays a dialog informing the user to enter a physician name to save.
     * If the fields are not empty, the physician information is retrieved from the UI,
     * and an API call is made to either add a new physician (if `physicianId` is null) or update
     * an existing physician (if `physicianId` is not null).
     * If the API call is successful, a toast message is shown indicating the success of the operation,
     * and the activity is navigated back to the previous screen.
     * If the API call fails, a toast message is shown indicating the failure, and the activity is
     * navigated back to the previous screen.
     */
    private void savePhysician() {
        // Check if the first name and last name fields are empty
        if (binding.adminPhysicianEditFirstName.getText().toString().trim().length() == 0 &&
                binding.adminPhysicianEditLastName.getText().toString().trim().length() == 0) {
            // Show an error dialog to prompt the user to enter a physician name
            DialogFragment errorSaving = new DialogFragment() {
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Please Enter a Physician Name to Save.");
                    builder.setPositiveButton("OK", null);
                    return builder.create();
                }
            };
            errorSaving.show(getFragmentManager(), "error saving physician");
            return;
        }

        // Get the SymptomManagementApi instance
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        // Determine the success message based on whether the physician is being added or updated
        final String successMsg = (physicianId == null ? "ADDED" : "UPDATED");

        // Check if the API instance is available
        if (symptomManagementApi != null) {
            // Use CallableTask to invoke the API call in a background thread
            CallableTask.invoke(() -> {
                // Set the physician ID and other information from the UI input
                physician.setId(physicianId);
                physician.setFirstName(binding.adminPhysicianEditFirstName.getText().toString());
                physician.setLastName(binding.adminPhysicianEditLastName.getText().toString());

                // Check if the physician is being added or updated
                if (physicianId == null) {
                    // If the physician is being added, log the information and call the API method to add
                    Log.d(LOG_TAG, "adding physician :" + physician.toDebugString());
                    return symptomManagementApi.addPhysician(physician);
                } else {
                    // If the physician is being updated, log the information and call the API method to update
                    Log.d(LOG_TAG, "updating physician :" + physician.toDebugString());
                    return symptomManagementApi.updatePhysician(physicianId, physician);
                }
            }, new TaskCallback<Physician>() {
                @Override
                public void success(Physician result) {
                    // If the API call is successful, show a toast message indicating success
                    // and navigate back to the previous screen
                    Toast.makeText(
                            getActivity(),
                            "Physician [" + result.getName() + "] " + successMsg + " successfully.",
                            Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }

                @Override
                public void error(Exception e) {
                    // If the API call fails, show a toast message indicating failure
                    // and navigate back to the previous screen
                    Toast.makeText(
                            getActivity(),
                            "Unable to SAVE Physician. Please check Internet connection.",
                            Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }
            });
        }
    }
}
