package com.example.symptommanagement.admin.Physician;

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
import com.example.symptommanagement.databinding.FragmentAdminPhysicianDetailBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A fragment that displays the details of a physician in the admin panel.
 * This fragment is responsible for showing the details of a selected physician in the admin panel.
 */
public class AdminPhysicianDetailFragment extends Fragment {

    private static final String LOG_TAG = AdminPhysicianDetailFragment.class.getSimpleName();
    private final static String PHYSICIAN_ID_KEY = AdminPhysicianListActivity.PHYSICIAN_ID_KEY;
    private String mPhysicianId;
    private Physician mPhysician;
    private FragmentAdminPhysicianDetailBinding binding;

    /**
     * Interface for communication with the parent activity.
     * This interface must be implemented by the parent activity to handle the edit physician event.
     */
    public interface Callbacks {
        /**
         * Called when the "Edit" option is selected for a physician.
         *
         * @param id The ID of the physician to be edited.
         */
        void onEditPhysician(String id);
    }

    /**
     * Called to create the view for this fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The view for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment using the provided inflater
        // and bind the FragmentAdminPhysicianDetailBinding object to the inflated view
        binding = FragmentAdminPhysicianDetailBinding.inflate(inflater, container, false);

        // Get the physician ID from the arguments or saved instance state
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPhysicianId = arguments.getString(PHYSICIAN_ID_KEY);
        } else if (savedInstanceState != null) {
            mPhysicianId = savedInstanceState.getString(PHYSICIAN_ID_KEY);
        }

        // Enable options menu in the action bar
        setHasOptionsMenu(true);

        // Retain the fragment instance across configuration changes
        setRetainInstance(true);

        // Return the root view of the inflated layout
        return binding.getRoot();
    }

    /**
     * Initialize the contents of the options menu.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater The MenuInflater object that can be used to inflate the options menu.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.admin_edit_delete_menu, menu);
    }

    /**
     * Called when an item in the options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return true if the event was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                // Handle "Edit" option click by notifying the parent activity to edit the physician
                ((Callbacks) getActivity()).onEditPhysician(mPhysicianId);
                return true;
            case R.id.action_delete:
                // Handle "Delete" option click by deleting the physician
                deletePhysician();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the fragment resumes.
     * This method is responsible for loading the physician details from the API when the fragment resumes.
     */
    @Override
    public void onResume() {
        super.onResume();

        // Get the physician ID from the arguments
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(PHYSICIAN_ID_KEY)) {
            mPhysicianId = arguments.getString(PHYSICIAN_ID_KEY);

            // Load the physician details from the API
            loadPhysicianFromAPI();
        }
    }

    /**
     * Called to save the current instance state of the fragment.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(PHYSICIAN_ID_KEY, mPhysicianId);
        super.onSaveInstanceState(outState);
    }

    /**
     * Load the physician details from the API.
     * This method fetches the details of the physician with the given ID from the API
     * and updates the UI with the retrieved information.
     */
    private void loadPhysicianFromAPI() {
        // Log the physician ID for debugging purposes
        Log.d(LOG_TAG, "Physician ID is : " + mPhysicianId);

        // Get the SymptomManagementApi instance
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        if (symptomManagementApi != null) {
            // Use CallableTask to fetch the physician details from the API asynchronously
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "getting single Physician id : " + mPhysicianId);
                return symptomManagementApi.getPhysician(mPhysicianId);
            }, new TaskCallback<Physician>() {
                @Override
                public void success(Physician result) {
                    // Successfully retrieved the physician details from the API
                    Log.d(LOG_TAG, "Found Physician :" + result.toString());

                    // Update the fragment's local physician instance
                    mPhysician = result;

                    // Update the UI with the physician's information
                    binding.adminPhysicianDetail.setText(mPhysician.toString());

                    // Display the list of patients associated with this physician
                    displayPatientList(mPhysician.getPatients());
                }

                @Override
                public void error(Exception e) {
                    // An error occurred while fetching the physician details
                    Toast.makeText(
                            getActivity(),
                            "Unable to fetch Selected Physician. Please check Internet connection.",
                            Toast.LENGTH_LONG).show();
                    // Navigate back to the previous screen
                    getActivity().onBackPressed();
                }
            });
        }
    }

    /**
     * Delete the physician from the API.
     * This method deletes the physician with the given ID from the API
     * and shows a toast message indicating the success or failure of the operation.
     */
    public void deletePhysician() {
        // Get the SymptomManagementApi instance
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        if (symptomManagementApi != null) {
            // Use CallableTask to delete the physician from the API asynchronously
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "deleting Physician id : " + mPhysicianId);
                return symptomManagementApi.deletePhysician(mPhysicianId);
            }, new TaskCallback<Physician>() {
                @Override
                public void success(Physician result) {
                    // Physician was successfully deleted
                    Toast.makeText(
                            getActivity(),
                            "Physician [" + result.getName() + "] deleted successfully.",
                            Toast.LENGTH_SHORT).show();
                    // Re-GET the physicians list as the deleted physician should not be in the list anymore
                    getActivity().onBackPressed();
                }

                @Override
                public void error(Exception e) {
                    // An error occurred while deleting the physician
                    Toast.makeText(
                            getActivity(),
                            "Unable to delete Physician. Please check Internet connection.",
                            Toast.LENGTH_LONG).show();
                    // Re-GET the physicians list as the deleted physician should still be in the list
                    getActivity().onBackPressed();
                }
            });
        }
    }

    /**
     * Display the list of patients associated with the physician.
     * This method displays the list of patients associated with the physician in a ListView.
     *
     * @param patients The collection of patients associated with the physician.
     */
    private void displayPatientList(Collection<Patient> patients) {
        if (patients == null || patients.size() == 0) {
            // If there are no patients associated with the physician, display a message in the ListView
            final List<Patient> emptyList = new ArrayList<>();
            Patient emptyPatient = new Patient("No Patients for this Physician.", "");
            emptyList.add(emptyPatient);
            binding.physicianPatientsList.setAdapter(new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text1,
                    new ArrayList<>(emptyList)));
        } else {
            // If there are patients associated with the physician, display them in the ListView
            binding.physicianPatientsList.setAdapter(new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text1,
                    new ArrayList<>(patients)));
        }
    }
}
