package com.example.symptommanagement.physician;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.HistoryLog;
import com.example.symptommanagement.data.Patient;

/**
 * Fragment for displaying a list of history logs associated with a patient.
 * This fragment extends ListFragment for easy list handling.
 */
public class HistoryLogFragment extends ListFragment {

    private static final String LOG_TAG = HistoryLogFragment.class.getSimpleName();
    public final static String FRAGMENT_TAG = "fragment_history_log";
    public final static String BACKUP_KEY = "allow_back";

    /**
     * Callbacks interface to communicate between the HistoryLogFragment and its hosting activity.
     */
    public interface Callbacks {

        /**
         * This method should be implemented by the hosting activity.
         * It is used to get the Patient object whose history logs need to be displayed in the fragment.
         *
         * @return The Patient object associated with the history logs.
         */
        Patient getPatientForHistory();
    }

    /**
     * The patient whose history logs are displayed
     */
    private static Patient patient;

    /**
     * Flag to determine if the fragment allows backing up
     */
    private static boolean allowBackup = false;

    /**
     * Array of history logs to be displayed
     */
    private static HistoryLog[] logList;

    /**
     * Called when the fragment is being created.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Check if the fragment should allow backing up
            boolean found = getArguments().getBoolean(BACKUP_KEY);
            if (found) {
                allowBackup = true;
            }
        } else {
            allowBackup = false;
        }

        // Set the "Up" button on the action bar to allow navigation to the parent activity
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(allowBackup);
        }

        // Retain the instance state to prevent recreation on configuration changes
        this.setRetainInstance(true);
    }

    /**
     * Called when the activity's layout has been created.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getString(R.string.empty_list_text));
        this.setRetainInstance(true);
    }

    /**
     * Called when the fragment is attached to the activity.
     *
     * @param activity The hosting activity.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Check if the activity implements the Callbacks interface
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(activity.getString(R.string.callbacks_message));
        }
    }

    /**
     * Called when the fragment is resumed.
     * This is where the patient for history logs is obtained from the activity
     * and the list of history logs is displayed.
     */
    @Override
    public void onResume() {
        super.onResume();
        patient = ((Callbacks) getActivity()).getPatientForHistory();
        if (patient != null) Log.d(LOG_TAG, "History Patient is " + patient.toString());
        displayLogList(patient);
    }

    /**
     * Method to update the patient whose history logs are displayed.
     *
     * @param patient The new patient to be displayed.
     */
    public void updatePatient(Patient patient) {
        if (patient == null) {
            Log.e(LOG_TAG, "Trying to set history log patient to null.");
            return;
        }
        Log.d(LOG_TAG, "New Patient has arrived!" + patient.toString());
        HistoryLogFragment.patient = patient;
        displayLogList(HistoryLogFragment.patient);
    }

    /**
     * Displays the list of history logs associated with the given patient.
     *
     * @param patient The patient whose history logs are to be displayed.
     */
    private void displayLogList(Patient patient) {
        if (patient != null) {
            // Create the array of history logs for the patient
            logList = PatientManager.createLogList(HistoryLogFragment.patient);
            if (logList != null) {
                try {
                    // Set the list adapter to display the history logs
                    setListAdapter(new HistoryLogAdapter(getActivity(), logList));
                } catch (Exception e) {
                    // Handle the case when a null pointer exception occurs on rotation
                    Log.e(LOG_TAG, "This gets a null pointer on rotation sometimes. sigh!");
                    logList = new HistoryLog[0];
                    setListAdapter(new HistoryLogAdapter(getActivity(), logList));
                }
            }
        }
    }
}
