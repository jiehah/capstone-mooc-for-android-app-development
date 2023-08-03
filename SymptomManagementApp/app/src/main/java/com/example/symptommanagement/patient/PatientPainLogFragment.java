package com.example.symptommanagement.patient;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.symptommanagement.LoginUtility;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.PainLog;
import com.example.symptommanagement.data.PatientCPContract.PainLogEntry;
import com.example.symptommanagement.data.PatientCPcvHelper;
import com.example.symptommanagement.databinding.FragmentPainLogBinding;
import com.example.symptommanagement.sync.SymptomManagementSyncAdapter;

/**
 * Fragment class that allows patients to log their pain level and eating status.
 */
public class PatientPainLogFragment extends Fragment {

    private final static String LOG_TAG = PatientPainLogFragment.class.getSimpleName();
    public final static String FRAGMENT_TAG = "patient_pain_log_fragment";

    /**
     * Callback interface to handle interactions with the PatientPainLogFragment.
     * The hosting activity should implement this interface to receive notifications
     * from the PatientPainLogFragment when the pain log entry is completed and saved.
     */
    public interface Callbacks {
        /**
         * Callback method invoked when a pain log entry is completed and saved.
         *
         * @param checkinId The ID of the check-in log associated with the pain log.
         * @return True if the log entry is part of a check-in process, False otherwise.
         */
        boolean onPainLogComplete(long checkinId);
    }

    private final PainLog log;
    private String patientId;

    private FragmentPainLogBinding binding;

    /**
     * Default constructor for the PatientPainLogFragment.
     * Initializes the log object with default values for pain severity and eating status.
     */
    public PatientPainLogFragment() {
        log = new PainLog();
        log.setSeverity(PainLog.Severity.NOT_DEFINED);
        log.setEating(PainLog.Eating.NOT_DEFINED);
    }

    /**
     * Called when the fragment is being created. Sets up the ActionBar for the fragment.
     *
     * @param savedInstanceState The saved instance state of the fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Called when the fragment's UI is being created. Inflates the layout and sets up the views.
     *
     * @param inflater           The LayoutInflater used to inflate the layout.
     * @param container          The parent view that the fragment UI should be attached to.
     * @param savedInstanceState The saved instance state of the fragment.
     * @return The root view of the fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using the ViewBinding
        binding = FragmentPainLogBinding.inflate(inflater, container, false);
        // Retain the instance of the fragment to persist data across configuration changes
        this.setRetainInstance(true);
        return binding.getRoot();
    }

    /**
     * Called after the fragment's view hierarchy is created. Sets up click listeners
     * for the radio buttons and the "Done" button.
     *
     * @param view               The root view of the fragment's layout.
     * @param savedInstanceState The saved instance state of the fragment.
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set up click listeners for the pain severity radio buttons
        binding.wellControlledButton.setOnClickListener(this::onSeverityRadioGroup);
        binding.moderateButton.setOnClickListener(this::onSeverityRadioGroup);
        binding.severeButton.setOnClickListener(this::onSeverityRadioGroup);

        // Set up click listeners for the eating status radio buttons
        binding.eatingOkButton.setOnClickListener(this::onEatingRadioGroup);
        binding.eatingSomeButton.setOnClickListener(this::onEatingRadioGroup);
        binding.notEatingButton.setOnClickListener(this::onEatingRadioGroup);

        // Set up click listener for the "Done" button
        binding.painLogDoneButton.setOnClickListener(v -> savePainLog());
    }

    /**
     * Callback method to handle clicks on the pain severity radio buttons.
     * Updates the log object with the selected pain severity.
     *
     * @param v The View representing the clicked radio button.
     */
    public void onSeverityRadioGroup(View v) {
        switch (v.getId()) {
            case R.id.well_controlled_button:
                log.setSeverity(PainLog.Severity.WELL_CONTROLLED);
                break;
            case R.id.moderate_button:
                log.setSeverity(PainLog.Severity.MODERATE);
                break;
            case R.id.severe_button:
                log.setSeverity(PainLog.Severity.SEVERE);
                break;
        }
    }

    /**
     * Callback method to handle clicks on the eating status radio buttons.
     * Updates the log object with the selected eating status.
     *
     * @param v The View representing the clicked radio button.
     */
    public void onEatingRadioGroup(View v) {
        switch (v.getId()) {
            case R.id.eating_ok_button:
                log.setEating(PainLog.Eating.EATING);
                break;
            case R.id.eating_some_button:
                log.setEating(PainLog.Eating.SOME_EATING);
                break;
            case R.id.not_eating_button:
                log.setEating(PainLog.Eating.NOT_EATING);
                break;
        }
    }

    /**
     * Saves the pain log entry to the database and performs necessary actions upon saving.
     * This method is called when the user clicks the "Done" button after answering the pain log questions.
     */
    public void savePainLog() {
        // Check if the patient has answered both severity and eating questions
        if (log.getSeverity() == PainLog.Severity.NOT_DEFINED || log.getEating() == PainLog.Eating.NOT_DEFINED) {
            Toast.makeText(getActivity(), "Please answer both questions.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the patient ID and set the check-in ID for the pain log entry
        patientId = LoginUtility.getLoginId(getActivity());
        log.setCheckinId(LoginUtility.getCheckInLogId(getActivity()));

        // Create ContentValues to store the pain log data
        ContentValues cv = PatientCPcvHelper.createValuesObject(patientId, log);
        Log.d(LOG_TAG, "Saving this Pain Log : " + log);

        // Insert the pain log entry into the database
        Uri uri = getActivity().getContentResolver().insert(PainLogEntry.CONTENT_URI, cv);
        long objectId = ContentUris.parseId(uri);

        // Check if the insertion was successful
        if (objectId < 0) {
            Log.e(LOG_TAG, "Pain Log Insert Failed.");
        }

        // Trigger an immediate sync with the server to update data
        SymptomManagementSyncAdapter.syncImmediately(getActivity());

        // Notify the hosting activity that the pain log entry is complete and provide the check-in ID
        boolean isCheckIn = ((Callbacks) getActivity()).onPainLogComplete(log.getCheckinId());
        Log.d(LOG_TAG, "Status log and Med logs should have this same id: " + log.getCheckinId());

        // If the pain log entry is not part of a check-in process, navigate back to the previous screen
        if (!isCheckIn) {
            getActivity().onBackPressed();
        }
    }
}
