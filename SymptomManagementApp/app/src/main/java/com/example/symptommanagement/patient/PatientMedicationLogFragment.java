package com.example.symptommanagement.patient;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.symptommanagement.LoginUtility;
import com.example.symptommanagement.data.Medication;
import com.example.symptommanagement.data.MedicationLog;
import com.example.symptommanagement.data.PatientCPContract.MedLogEntry;
import com.example.symptommanagement.data.PatientCPcvHelper;
import com.example.symptommanagement.data.PatientDataManager;
import com.example.symptommanagement.databinding.FragmentMedicationLogBinding;
import com.example.symptommanagement.sync.SymptomManagementSyncAdapter;

import java.util.Collection;
import java.util.HashSet;

/**
 * A Fragment for handling the medication log entries of a patient.
 */
public class PatientMedicationLogFragment extends Fragment {

    private final static String LOG_TAG = PatientMedicationLogFragment.class.getSimpleName();
    public final static String FRAGMENT_TAG = "patient_med_log_fragment";

    /**
     * Callback interface to handle interactions with the PatientMedicationLogFragment.
     */
    public interface Callbacks {
        /**
         * Callback method to notify the hosting activity when the medication log is complete.
         *
         * @return True if the medication log is part of a check-in process, false otherwise.
         */
        boolean onMedicationLogComplete();
    }

    private MedicationLogListAdapter mAdapter;
    private Collection<MedicationLog> medicationLogs;
    private MedicationLog[] mLogList;
    private long mCheckInId = 0L;
    private FragmentMedicationLogBinding binding;

    /**
     * Called when the fragment is created.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Called when the view for the fragment is created.
     *
     * @param inflater           The layout inflater.
     * @param container          The container for the fragment's view.
     * @param savedInstanceState The saved instance state.
     * @return The root view for the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMedicationLogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called when the fragment is resumed.
     */
    @Override
    public void onResume() {
        super.onResume();
        mCheckInId = LoginUtility.getCheckInLogId(getActivity());
        loadMedicationLogList();
    }

    /**
     * Called when the fragment is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
        LoginUtility.setCheckin(getActivity(), false);
    }

    /**
     * Loads the list of medications and sets up the adapter for the medication log ListView.
     */
    private void loadMedicationLogList() {
        // Get the list of medications for the patient from the content provider
        Collection<Medication> prescriptions = PatientDataManager.getPrescriptionsFromCP(
                getActivity(),
                LoginUtility.getLoginId(getActivity())
        );

        // If the medication log list is not yet initialized, create empty logs for each medication
        if (mLogList == null) {
            createEmptyLogsList(prescriptions);
        }

        // Set up the adapter for the medication log ListView
        mAdapter = new MedicationLogListAdapter(getActivity(), mLogList);
        binding.patientMedicationCheckList.setAdapter(mAdapter);
    }

    /**
     * Creates an initial empty list of medication logs for each prescribed medication.
     *
     * @param medications The collection of prescribed medications.
     */
    private void createEmptyLogsList(Collection<Medication> medications) {
        Log.d(LOG_TAG, "Setting all medication logs with a Check-in id of : " + mCheckInId);

        // Initialize a HashSet to hold the medication logs
        medicationLogs = new HashSet<>();

        // Create an empty log for each prescribed medication and add it to the HashSet
        for (Medication m : medications) {
            MedicationLog ml = new MedicationLog();
            ml.setMed(m);
            ml.setCheckinId(mCheckInId);
            medicationLogs.add(ml);
        }

        // Convert the HashSet to an array of MedicationLog objects
        mLogList = medicationLogs.toArray(new MedicationLog[0]);
    }

    /**
     * Updates the time taken for a medication log entry and saves it to the database.
     *
     * @param msTime   The time taken in milliseconds.
     * @param position The position of the medication log entry in the list.
     */
    public void updateMedicationLogTimeTaken(long msTime, int position) {
        // Set the time taken for the medication log entry at the specified position
        mLogList[position].setTaken(msTime);

        // Get the patient ID
        String mPatientId = LoginUtility.getLoginId(getActivity());

        // Create a ContentValues object to store the updated medication log entry
        ContentValues cv = PatientCPcvHelper.createValuesObject(mPatientId, mLogList[position]);

        // Insert the updated medication log entry into the database
        Log.d(LOG_TAG, "Saving this Med Log : " + mLogList[position].toString());
        Uri uri = getActivity().getContentResolver().insert(MedLogEntry.CONTENT_URI, cv);
        long objectId = ContentUris.parseId(uri);

        // Check if the insertion was successful
        if (objectId < 0) {
            Log.e(LOG_TAG, "Medication Log Insert Failed.");
        } else {
            // Notify the activity that the medication log update is complete
            ((Callbacks) getActivity()).onMedicationLogComplete();
        }

        // Immediately sync the changes with the server
        SymptomManagementSyncAdapter.syncImmediately(getActivity());

        // Notify the adapter that the dataset has changed so that the UI can be updated
        mAdapter.notifyDataSetChanged();
    }
}
