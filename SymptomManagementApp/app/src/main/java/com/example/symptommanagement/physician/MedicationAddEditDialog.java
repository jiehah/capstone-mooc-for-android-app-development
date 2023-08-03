package com.example.symptommanagement.physician;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.Medication;
import com.example.symptommanagement.databinding.DialogMedicationAddEditBinding;

/**
 * A custom DialogFragment class to show a dialog for adding or editing medication information.
 */
public class MedicationAddEditDialog extends DialogFragment {

    public final static String FRAGMENT_TAG = "fragment_medication_dialog";

    /**
     * Callback interface to handle dialog results.
     */
    public interface Callbacks {
        /**
         * Called when the user saves medication information.
         *
         * @param medication The Medication object containing the updated medication information.
         */
        void onSaveMedicationResult(Medication medication);

        /**
         * Called when the user cancels the medication addition/editing process.
         */
        void onCancelMedicationResult();
    }

    private DialogMedicationAddEditBinding binding;

    private Medication medication = new Medication();

    /**
     * Titles for the dialog (Edit and Add)
     */
    private static String title = "Edit Medication";
    private static String addTitle = "Add Medication";

    /**
     * Keys for storing medication ID and name in the arguments Bundle
     */
    private static final String medIdKey = "med_id";
    private static final String medNameKey = "med_name";

    /**
     * Static method to create a new instance of the dialog with medication information.
     *
     * @param medication The Medication object representing the medication to be edited (can be null for adding new medication).
     * @return A new instance of MedicationAddEditDialog with arguments set.
     */
    public static MedicationAddEditDialog newInstance(Medication medication) {
        // Decide the title based on whether the medication has an ID (edit) or not (add)
        if (medication != null && (medication.getId() == null || medication.getId().isEmpty())) {
            title = addTitle;
        }
        MedicationAddEditDialog frag = new MedicationAddEditDialog();
        Bundle args = new Bundle();
        args.putString(medIdKey, medication != null ? medication.getId() : null);
        args.putString(medNameKey, medication != null ? medication.getName() : null);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Called when the DialogFragment is being created.
     *
     * @param savedInstanceState The saved state of the fragment (not used in this method).
     * @return The created Dialog instance.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Retrieve medication information from the arguments Bundle
        medication.setId(getArguments().getString(medIdKey));
        medication.setName(getArguments().getString(medNameKey));

        // Inflate the dialog layout using data binding
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        binding = DialogMedicationAddEditBinding.inflate(layoutInflater);
        View view = binding.getRoot();

        // If the medication has an ID, pre-fill the medication name in the input field
        if (medication.getId() != null && !medication.getId().isEmpty()) {
            binding.medicationAddEditName.setText(medication.getName());
        }

        // Set up the dialog using an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title) // Set the dialog title based on the title variable
                .setView(view) // Set the dialog content view
                .setPositiveButton(getActivity().getString(R.string.Ok_button_text),
                        // Positive button (OK button) click listener
                        (dialogInterface, i) -> {
                            // Check if the medication name input field is not null and not empty
                            if (binding.medicationAddEditName != null &&
                                    binding.medicationAddEditName.getText() != null) {
                                // Update the medication name with the input field value
                                medication.setName(binding.medicationAddEditName.getText().toString());
                                // Notify the hosting activity or fragment about the save action
                                ((Callbacks) getActivity()).onSaveMedicationResult(medication);
                            } else {
                                // If the input field is empty, notify about the cancel action
                                ((Callbacks) getActivity()).onCancelMedicationResult();
                            }
                        })
                .setNegativeButton(getActivity().getString(R.string.cancel_button_text),
                        // Negative button (Cancel button) click listener
                        (dialogInterface, i) -> {
                            // Notify the hosting activity or fragment about the cancel action
                            ((Callbacks) getActivity()).onCancelMedicationResult();
                        });

        // Create and return the dialog
        return builder.create();
    }
}
