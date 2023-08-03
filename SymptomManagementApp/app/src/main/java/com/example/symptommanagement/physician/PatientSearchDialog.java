package com.example.symptommanagement.physician;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.example.symptommanagement.R;
import com.example.symptommanagement.databinding.DialogPatientSearchBinding;

/**
 * A dialog fragment for searching patients by name.
 */
public class PatientSearchDialog extends DialogFragment {

    /**
     * Tag for identification of the fragment.
     */
    public final static String FRAGMENT_TAG = "fragment_patient_search_dialog";

    // View binding for the dialog
    private DialogPatientSearchBinding binding;

    /**
     * Callback interface to handle name selection.
     */
    public interface Callbacks {
        /**
         * Called when a name is selected.
         *
         * @param lastName  The selected last name.
         * @param firstName The selected first name.
         */
        void onNameSelected(String lastName, String firstName);
    }

    /**
     * The last name used for the search.
     */
    private String lastName = "";
    /**
     * The first name used for the search.
     */
    private String firstName = "";

    /**
     * The title of the dialog.
     */
    private static final String title = "Search All Patients By Name";

    /**
     * Create a new instance of PatientSearchDialog.
     *
     * @return A new instance of PatientSearchDialog.
     */
    public static PatientSearchDialog newInstance() {
        return new PatientSearchDialog();
    }

    /**
     * Lifecycle method: Called when the dialog is created.
     *
     * @param savedInstanceState The saved instance state.
     * @return The created dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the dialog layout using ViewBinding
        binding = DialogPatientSearchBinding.inflate(LayoutInflater.from(getActivity()));
        View view = binding.getRoot();

        // Set the last name and first name in the input fields
        binding.patientSearchLastName.setText(lastName);
        binding.patientSearchFirstName.setTag(firstName);

        // Create an AlertDialog using the AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(view)
                .setPositiveButton(getActivity().getString(R.string.Ok_button_text),
                        // Positive button click listener
                        (dialogInterface, i) -> {
                            // Get the last name and first name from the input fields
                            lastName = binding.patientSearchLastName.getText().toString();
                            firstName = binding.patientSearchFirstName.getText().toString();
                            // Notify the hosting activity about the selected name
                            ((Callbacks) getActivity()).onNameSelected(lastName, firstName);
                        })
                .setNegativeButton(getActivity().getString(R.string.cancel_button_text),
                        // Negative button click listener
                        (dialogInterface, i) -> {
                            // Do nothing here as the dialog will be dismissed.
                        });

        // Create and return the dialog
        return builder.create();
    }
}
