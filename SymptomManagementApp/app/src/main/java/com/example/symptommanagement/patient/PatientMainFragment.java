package com.example.symptommanagement.patient;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.symptommanagement.LoginUtility;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.HistoryLog;
import com.example.symptommanagement.data.Patient;
import com.example.symptommanagement.data.PatientDataManager;
import com.example.symptommanagement.data.Reminder;
import com.example.symptommanagement.databinding.FragmentPatientMainBinding;
import com.example.symptommanagement.patient.Reminder.ReminderManager;
import com.example.symptommanagement.physician.PatientManager;

import java.util.Calendar;
import java.util.Collection;

/**
 * The main fragment for the patient's dashboard.
 */
public class PatientMainFragment extends Fragment {

    private final static String LOG_TAG = PatientMainFragment.class.getSimpleName();
    public final static String FRAGMENT_TAG = "patient_main_fragment";

    /**
     * Callbacks interface to communicate with the hosting activity.
     */
    public interface Callbacks {
        /**
         * Retrieves the current patient object from the hosting activity.
         *
         * @return The current patient object.
         */
        Patient getPatientCallback();
    }

    private FragmentPatientMainBinding binding;
    private Patient mPatient;
    private Collection<Reminder> reminders;

    /**
     * Called to create the view hierarchy associated with the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment using the data binding object "binding".
        binding = FragmentPatientMainBinding.inflate(inflater, container, false);

        // Retrieve the current patient object from the hosting activity using the Callbacks interface.
        mPatient = ((Callbacks) getActivity()).getPatientCallback();

        // Set the patient's display name
        String displayName = getActivity().getString(R.string.patient_welcome_message) +
                ((mPatient != null && mPatient.getName() != null) ? mPatient.toString() : "");
        binding.mainPatientName.setText(displayName);

        // Set the number of check-ins completed and the next check-in time
        binding.checkInsCompleted.setText(getNumCheckIns());
        binding.nextCheckIn.setText(getNextCheckIn());

        // Enable options menu in the fragment (if there are any).
        setHasOptionsMenu(true);

        // Retain the instance of the fragment when configuration changes (e.g., screen rotation).
        setRetainInstance(true);

        // Return the root view of the fragment.
        return binding.getRoot();
    }

    /**
     * Called immediately after the view hierarchy associated with the fragment has been created.
     * This is called after onCreateView and can be used to perform actions involving the UI.
     *
     * @param view               The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Call the superclass method to ensure any superclass initialization is done first.
        super.onViewCreated(view, savedInstanceState);

        // Set click listeners for the buttons in the UI.
        binding.painLogButton.setOnClickListener(v -> enterPainLog());
        binding.medicationLogButton.setOnClickListener(v -> enterMedicationLog());
        binding.statusLogButton.setOnClickListener(v -> enterStatusLog());
    }

    /**
     * Opens the PatientPainLogFragment to allow the user to enter a pain log.
     */
    public void enterPainLog() {
        // Replace the current fragment in the container with the PatientPainLogFragment.
        // Uses the FragmentManager to begin a transaction and replace the fragment.
        // Also assigns a tag to the fragment to be able to identify it later.
        getFragmentManager().beginTransaction()
                .replace(R.id.patient_main_container,
                        new PatientPainLogFragment(),
                        PatientPainLogFragment.FRAGMENT_TAG)
                .commit();
    }

    /**
     * Opens the PatientMedicationLogFragment to allow the user to enter a medication log.
     */
    public void enterMedicationLog() {
        // Replace the current fragment in the container with the PatientMedicationLogFragment.
        // Uses the FragmentManager to begin a transaction and replace the fragment.
        // Also assigns a tag to the fragment to be able to identify it later.
        getFragmentManager().beginTransaction()
                .replace(R.id.patient_main_container,
                        new PatientMedicationLogFragment(),
                        PatientMedicationLogFragment.FRAGMENT_TAG)
                .commit();
    }

    /**
     * Opens the PatientStatusLogFragment to allow the user to enter a status log.
     */
    public void enterStatusLog() {
        // Replace the current fragment in the container with the PatientStatusLogFragment.
        // Uses the FragmentManager to begin a transaction and replace the fragment.
        // Also assigns a tag to the fragment to be able to identify it later.
        getFragmentManager().beginTransaction()
                .replace(R.id.patient_main_container,
                        new PatientStatusLogFragment(),
                        PatientStatusLogFragment.FRAGMENT_TAG)
                .commit();
    }

    /**
     * Retrieves the time of the next check-in for the patient.
     *
     * @return A string containing the next check-in time information.
     */
    private String getNextCheckIn() {
        if (mPatient == null) {
            return "";
        }

        // Load the sorted list of reminders for the patient
        reminders = PatientDataManager.loadSortedReminderList(getActivity(), mPatient.getId());

        if (reminders == null || reminders.size() <= 0) {
            return "No Check-Ins are Scheduled.";
        }

        // Get the current time in minutes since midnight
        Calendar rightNow = Calendar.getInstance();
        int checkValue = rightNow.get(Calendar.HOUR_OF_DAY) * 60 + rightNow.get(Calendar.MINUTE);

        int hour = -1;
        int minute = -1;
        for (Reminder r : reminders) {
            // Find the next reminder that is turned on and has a time greater than the current time
            if ((r.getHour() * 60 + r.getMinutes()) > checkValue && r.isOn()) {
                hour = r.getHour();
                minute = r.getMinutes();
                break;
            }
        }

        if (hour < 0) {
            return getActivity().getString(R.string.todays_check_ins_complete_message);
        }

        // Format the next check-in time
        String am_pm = (hour < 12) ? " AM" : " PM";
        String hours = (hour <= 12) ? Integer.valueOf(hour).toString() : Integer.valueOf(hour - 12).toString();
        String minutes = (minute < 10) ? "0" + minute : Integer.toString(minute);
        return "Next Check-In Today is at " + hours + ":" + minutes + am_pm;
    }

    /**
     * Retrieves the number of check-ins completed by the patient today.
     *
     * @return A string containing the number of check-ins completed.
     */
    private String getNumCheckIns() {
        // Create a temporary patient object and set its ID to the patient's login ID
        Patient tempPatient = new Patient();
        String mPatientId = LoginUtility.getLoginId(getActivity());
        tempPatient.setId(mPatientId);

        // Retrieve the logs for the patient from the content provider and create a log list
        PatientDataManager.getLogsFromCP(getActivity(), tempPatient);
        HistoryLog[] logList = PatientManager.createLogList(tempPatient);

        // Get the start time of today in milliseconds since epoch
        long start_of_day = new ReminderManager().getStartOfToday();

        // Count the number of check-in logs completed today
        int count = 0;
        for (HistoryLog h : logList) {
            if (h.getType() == HistoryLog.LogType.CHECK_IN_LOG && h.getCreated() >= start_of_day) {
                count++;
            }
        }

        // Format and return the check-in count message
        if (count == 0) {
            return getActivity().getString(R.string.no_check_ins_today_message);
        }
        return "You have checked in " + count + " times today.";
    }
}

