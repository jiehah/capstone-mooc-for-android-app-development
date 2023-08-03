package com.example.symptommanagement.admin;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.symptommanagement.admin.Patient.AdminPatientListActivity;
import com.example.symptommanagement.admin.Physician.AdminPhysicianListActivity;
import com.example.symptommanagement.databinding.FragmentAdminMainBinding;

/**
 * Fragment class representing the main screen for the Admin user.
 * This fragment is responsible for displaying the main screen of the Admin user,
 * including buttons to edit patients and physicians.
 */
public class AdminMainFragment extends Fragment {

    private FragmentAdminMainBinding binding;

    /**
     * Called to create the view hierarchy associated with the fragment.
     * This method is responsible for inflating the fragment's layout
     * and binding the layout to the FragmentAdminMainBinding object.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state,
     *                           or null if this is the first creation.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment using the provided inflater
        // and bind the FragmentAdminMainBinding object to the inflated view
        binding = FragmentAdminMainBinding.inflate(inflater, container, false);

        // Retain the fragment instance across configuration changes
        setRetainInstance(true);

        // Return the root view of the inflated layout
        return binding.getRoot();
    }

    /**
     * Called when the fragment's view has been created.
     * This method is responsible for setting up the view and attaching click listeners to buttons.
     *
     * @param view               The root view of the fragment's layout.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state,
     *                           or null if this is the first creation.
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Call the superclass method to perform necessary setup for the fragment's view
        super.onViewCreated(view, savedInstanceState);

        // Set click listeners for the "Edit Patients" and "Edit Physicians" buttons

        // "Edit Patients" button click listener
        binding.editPatientsButton.setOnClickListener(v ->
                // Start the AdminPatientListActivity when the button is clicked
                startActivity(new Intent(getActivity(), AdminPatientListActivity.class)));

        // "Edit Physicians" button click listener
        binding.editPhysiciansButton.setOnClickListener(v ->
                // Start the AdminPhysicianListActivity when the button is clicked
                startActivity(new Intent(getActivity(), AdminPhysicianListActivity.class)));
    }
}
