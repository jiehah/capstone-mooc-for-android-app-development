package com.example.symptommanagement.patient;

import android.app.*;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.symptommanagement.LoginActivity;
import com.example.symptommanagement.LoginUtility;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.*;
import com.example.symptommanagement.databinding.ActivityPatientMainBinding;
import com.example.symptommanagement.patient.Reminder.ReminderManager;
import com.example.symptommanagement.physician.HistoryLogFragment;
import com.example.symptommanagement.sync.SymptomManagementSyncAdapter;

/**
 * Main activity for the patient user. Manages fragments for medication log, pain log, status log,
 * reminders, and history log. Handles user interactions, data retrieval, and UI updates.
 */
public class PatientMainActivity
        extends Activity
        implements
        PatientMainFragment.Callbacks,
        MedicationLogListAdapter.Callbacks,
        MedicationTimeDialog.Callbacks,
        PatientPainLogFragment.Callbacks,
        PatientMedicationLogFragment.Callbacks,
        PatientStatusLogFragment.Callbacks,
        ReminderFragment.Callbacks,
        ReminderAddEditDialog.Callbacks,
        ReminderListAdapter.Callbacks,
        HistoryLogFragment.Callbacks {

    private final static String LOG_TAG = PatientMainActivity.class.getSimpleName();
    private String patientId;
    private Patient patient;
    private Context context;
    private ActivityPatientMainBinding binding;

    /**
     * Lifecycle callback method for the activity. Called when the activity is being created.
     * Initializes the activity's UI using data binding, sets up the action bar, and handles the
     * display of appropriate fragments based on the check-in status.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using data binding
        binding = ActivityPatientMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the application context
        context = this;

        // Set up the action bar
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        // Retrieve patient information from the local database or server
        getPatient();

        // Load the appropriate fragment based on check-in status
        if (savedInstanceState == null) {
            // Check if the patient is currently in the check-in process
            Log.d(LOG_TAG, "Are we doing Checkin? " + (LoginUtility.isCheckin(this) ? "YES" : "NO"));
            if (LoginUtility.isCheckin(this)) {
                // If in check-in, display the PatientPainLogFragment
                getFragmentManager().beginTransaction()
                        .add(R.id.patient_main_container,
                                new PatientPainLogFragment(),
                                PatientPainLogFragment.FRAGMENT_TAG)
                        .commit();
            } else {
                // Otherwise, display the main PatientMainFragment
                getFragmentManager().beginTransaction()
                        .add(R.id.patient_main_container,
                                new PatientMainFragment(),
                                PatientMainFragment.FRAGMENT_TAG)
                        .commit();
            }
        }
    }

    /**
     * Called just before the options menu is displayed. This method is used to dynamically modify
     * the options menu based on the current state of the app. In this case, it checks whether
     * certain fragments (HistoryLogFragment and ReminderFragment) are currently displayed and, if so,
     * it removes the "Patient History Log" item from the option's menu.
     *
     * @param menu The options menu in which items are placed.
     * @return True if the menu has been modified, false otherwise.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Find the HistoryLogFragment and ReminderFragment by their tags
        Fragment historyFragment = getFragmentManager().findFragmentByTag(HistoryLogFragment.FRAGMENT_TAG);
        Fragment reminderFragment = getFragmentManager().findFragmentByTag(ReminderFragment.FRAGMENT_TAG);

        // If either of the fragments is currently displayed, remove the "Patient History Log" item
        if (historyFragment != null || reminderFragment != null) {
            menu.removeItem(R.id.action_patient_history_log);
        }

        // Call the superclass method to proceed with menu preparation
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Initialize the contents of the activity's options menu. This method is called only once during
     * the activity's creation. It inflates the XML resource (patient_main.xml) defining the menu items
     * and adds them to the option's menu.
     *
     * @param menu The options menu in which items are placed.
     * @return True for the menu to be displayed, false for it to be hidden.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu resource (patient_main.xml) to create the options menu
        getMenuInflater().inflate(R.menu.patient_main, menu);

        // Make the "Settings" item visible in the options menu
        menu.findItem(R.id.action_settings).setVisible(true);

        // Return true to display the menu, or false to hide it
        return true;
    }

    /**
     * Called when a menu item in the options menu is selected. This method is responsible for handling
     * the actions associated with each menu item. Depending on the item selected, it performs different
     * actions. For example, if the "Settings" item is selected, it replaces the current fragment with
     * the ReminderFragment, and if the "Patient History Log" item is selected, it replaces the current
     * fragment with the HistoryLogFragment, passing the backup key in the arguments to indicate that
     * the fragment is a backup. If the "Logout" item is selected, it restarts the LoginActivity to
     * log the patient out.
     *
     * @param item The selected menu item.
     * @return True if the item selection is handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get the ID of the selected menu item
        int id = item.getItemId();

        // Handle the selected menu item based on its ID
        if (id == R.id.action_settings) {
            // Replace the current fragment with the ReminderFragment
            getFragmentManager().beginTransaction()
                    .replace(R.id.patient_main_container,
                            new ReminderFragment(),
                            ReminderFragment.FRAGMENT_TAG)
                    .commit();
            return true;
        } else if (id == R.id.action_patient_history_log) {
            // Replace the current fragment with the HistoryLogFragment (backup mode)
            Bundle arguments = new Bundle();
            arguments.putBoolean(HistoryLogFragment.BACKUP_KEY, true);
            HistoryLogFragment historyLogFragment = new HistoryLogFragment();
            historyLogFragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.patient_main_container,
                            historyLogFragment,
                            HistoryLogFragment.FRAGMENT_TAG)
                    .commit();
            return true;
        } else if (id == R.id.patient_logout) {
            // Logout the patient by restarting the LoginActivity
            LoginActivity.restartLoginActivity(this);
        }

        // Call the superclass method to handle other item selections
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the device's back button is pressed. This method overrides the default behavior of
     * the back button press. If the current fragment in the container is the PatientMainFragment, it
     * sends the app to the home screen by starting the home activity. Otherwise, it performs the default
     * back button action by calling the superclass method.
     */
    @Override
    public void onBackPressed() {
        if (getFragmentManager().findFragmentById(R.id.patient_main_container)
                instanceof PatientMainFragment) {
            // If the current fragment is PatientMainFragment, go to the home screen
            startActivity(new Intent()
                    .setAction(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_HOME));
        } else {
            // Perform the default back button action
            super.onBackPressed();
        }
    }

    /**
     * Retrieves the patient information for the current user and sets it to the 'patient' instance
     * variable. It first checks if the user is logged in as a patient, and if so, it gets the patient ID
     * from the login utility and retrieves the patient information from the data manager. If the patient
     * information is not found, it triggers an immediate sync with the server using the
     * SymptomManagementSyncAdapter to update the patient data.
     */
    private void getPatient() {
        if (LoginUtility.isLoggedIn(this) &&
                LoginUtility.getUserRole(this) == UserCredential.UserRole.PATIENT) {
            // Get the patient ID from the login utility
            patientId = LoginUtility.getLoginId(context);
        } else {
            // Patient not logged in or login properties are incorrect
            Log.d(LOG_TAG, "UNABLE to get Patient because login properties are " +
                    "not completed or they are not correct.");
        }

        // Initialize patient instance variable
        patient = null;

        if (patientId != null && !patientId.isEmpty()) {
            // Retrieve the patient information using the patient ID
            patient = PatientDataManager.findPatient(context, patientId);
        }

        if (patient == null) {
            // If patient information not found, trigger an immediate sync with the server
            SymptomManagementSyncAdapter.syncImmediately(this);
        }
    }

    /**
     * Callback method called when the pain log is completed. This method handles the action to be taken
     * after completing the pain log. If it's a check-in session (indicated by LoginUtility.isCheckin),
     * it creates a check-in log entry and replaces the current fragment with the
     * PatientMedicationLogFragment. If it's not a check-in session, it updates the last logged timestamp
     * for the patient.
     *
     * @param checkinId The ID of the completed check-in log.
     * @return True if the pain log is completed during a check-in session, false otherwise.
     */
    @Override
    public boolean onPainLogComplete(long checkinId) {
        if (LoginUtility.isCheckin(getApplication())) {
            // If it's a check-in session, create a check-in log entry and go to the medication log fragment
            createCheckInLog(checkinId);
            getFragmentManager().beginTransaction()
                    .replace(R.id.patient_main_container,
                            new PatientMedicationLogFragment(),
                            PatientMedicationLogFragment.FRAGMENT_TAG)
                    .commit();
            return true;
        }

        // If not a check-in session, update the last logged timestamp
        setLastLoggedTimestamp();
        return false;
    }

    /**
     * Creates a check-in log entry with the given check-in ID and saves it to the database.
     *
     * @param checkinId The ID of the completed check-in log.
     */
    private void createCheckInLog(long checkinId) {
        CheckInLog cLog = new CheckInLog();
        cLog.setCheckinId(checkinId);
        cLog.setCreated(checkinId);
        ContentValues cv = PatientCPcvHelper.createValuesObject(patientId, cLog);
        Log.d(LOG_TAG, "Saving this Checkin Log : " + cLog.toString());
        Uri uri = getContentResolver().insert(PatientCPContract.CheckInLogEntry.CONTENT_URI, cv);
        long objectId = ContentUris.parseId(uri);
        if (objectId < 0) {
            Log.e(LOG_TAG, "Check-in Log Insert Failed.");
        }
    }

    /**
     * Callback method called when the status log is completed. This method is not currently used in the
     * implementation and always returns true.
     *
     * @return Always returns true.
     */
    @Override
    public boolean onStatusLogComplete() {
        return true;
    }

    /**
     * Callback method called when the medication log is completed. This method is not currently used in
     * the implementation and always returns true.
     *
     * @return Always returns true.
     */
    @Override
    public boolean onMedicationLogComplete() {
        return true;
    }

    /**
     * Sets the last logged timestamp in the patient record to the current time.
     */
    private void setLastLoggedTimestamp() {
        Log.d(LOG_TAG, "Should be updating the last logged timestamp in the patient record.");
        getPatient();
        if (patient != null) {
            patient.setLastLogin(System.currentTimeMillis());
            PatientDataManager.updateLastLoginFromCP(context, patient);
        } else {
            Log.d(LOG_TAG, "Could not update the last login ... no patient found.");
        }
    }

    /**
     * Callback method called when a date and time for medication log is requested. This method opens
     * the MedicationTimeDialog to allow the user to select the time for the medication log entry.
     *
     * @param position The position of the medication log entry in the list.
     */
    @Override
    public void onRequestDateTime(int position) {
        FragmentManager fm = getFragmentManager();
        MedicationTimeDialog timeDialog = MedicationTimeDialog.newInstance(position);
        timeDialog.show(fm, MedicationTimeDialog.FRAGMENT_TAG);
    }

    /**
     * Callback method called when a positive result is received from the MedicationTimeDialog. This
     * method updates the time taken for a medication log entry with the given time in milliseconds.
     *
     * @param msTime   The time taken for the medication in milliseconds.
     * @param position The position of the medication log entry in the list.
     */
    @Override
    public void onPositiveResult(long msTime, int position) {
        PatientMedicationLogFragment frag = (PatientMedicationLogFragment) getFragmentManager()
                .findFragmentById(R.id.patient_main_container);
        frag.updateMedicationLogTimeTaken(msTime, position);
    }

    /**
     * Callback method called when a negative result is received from the MedicationTimeDialog. This
     * method sets the time taken for a medication log entry to 0 (indicating that the medication was not taken).
     *
     * @param msTime   The time taken for the medication in milliseconds. This parameter is not used in this method.
     * @param position The position of the medication log entry in the list.
     */
    @Override
    public void onNegativeResult(long msTime, int position) {
        PatientMedicationLogFragment frag = (PatientMedicationLogFragment) getFragmentManager()
                .findFragmentById(R.id.patient_main_container);
        frag.updateMedicationLogTimeTaken(0L, position);
    }

    /**
     * Callback method called when a request to add a reminder is received. This method opens the
     * ReminderAddEditDialog with a new reminder object for adding a new reminder.
     *
     * @param reminder The new reminder to be added.
     */
    @Override
    public void onRequestReminderAdd(Reminder reminder) {
        FragmentManager fragmentManager = getFragmentManager();
        ReminderAddEditDialog reminderDialog = ReminderAddEditDialog.newInstance(-1, reminder);
        reminderDialog.show(fragmentManager, ReminderAddEditDialog.FRAGMENT_TAG);
    }

    /**
     * Callback method called when a request to edit a reminder is received. This method opens the
     * ReminderAddEditDialog with the given reminder object for editing the reminder.
     *
     * @param position The position of the reminder in the list.
     * @param reminder The reminder to be edited.
     */
    @Override
    public void onRequestReminderEdit(int position, Reminder reminder) {
        FragmentManager fragmentManager = getFragmentManager();
        ReminderAddEditDialog reminderDialog = ReminderAddEditDialog.newInstance(position, reminder);
        reminderDialog.show(fragmentManager, ReminderAddEditDialog.FRAGMENT_TAG);
    }

    /**
     * Callback method called when a new reminder is added using the ReminderAddEditDialog. This
     * method notifies the ReminderFragment to add the new reminder to the list.
     *
     * @param newReminder The new reminder to be added.
     */
    @Override
    public void onReminderAdd(Reminder newReminder) {
        ReminderFragment frag = (ReminderFragment) getFragmentManager()
                .findFragmentById(R.id.patient_main_container);
        frag.addReminder(newReminder);
    }

    /**
     * Callback method called when an existing reminder is updated using the ReminderAddEditDialog. This
     * method notifies the ReminderFragment to update the reminder in the list.
     *
     * @param position The position of the reminder in the list.
     * @param reminder The updated reminder.
     */
    @Override
    public void onReminderUpdate(int position, Reminder reminder) {
        ReminderFragment frag = (ReminderFragment) getFragmentManager()
                .findFragmentById(R.id.patient_main_container);
        frag.updateReminder(position, reminder);
    }

    /**
     * Callback method called when a reminder is deleted from the ReminderFragment. This method shows
     * an alert dialog to confirm the deletion of the reminder. If the user confirms the deletion, the
     * reminder is removed from the list of reminders in the ReminderFragment.
     *
     * @param position The position of the reminder in the list to be deleted.
     * @param reminder The reminder to be deleted.
     */
    @Override
    public void onReminderDelete(final int position, Reminder reminder) {
        AlertDialog alert = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_reminder_delete_title))
                .setMessage(getString(R.string.confirm_reminder_delete_message))
                .setPositiveButton(getString(R.string.answer_yes),
                        (dialog, which) -> {
                            // Get the ReminderFragment and delete the reminder at the specified position
                            ReminderFragment frag =
                                    (ReminderFragment) getFragmentManager()
                                            .findFragmentById(R.id.patient_main_container);
                            frag.deleteReminder(position);
                            dialog.dismiss();
                        })
                .setNegativeButton(getString(R.string.answer_no),
                        (dialog, which) -> {
                            // Dismiss the dialog when the user chooses not to delete the reminder
                            dialog.dismiss();
                        }).create();
        alert.show();
    }

    /**
     * Callback method called when a request to activate or deactivate a reminder is received. This
     * method activates or deactivates the alarm for the given reminder.
     *
     * @param reminder The reminder to be activated or deactivated.
     */
    @Override
    public void onRequestReminderActivate(Reminder reminder) {
        if (reminder == null) {
            Log.e(LOG_TAG, "Null Reminder value attempting to be activated.");
            return;
        }

        // Log the attempt to activate/deactivate the reminder
        Log.d(LOG_TAG, "Attempting to " + (reminder.isOn() ? "activate" : "deactivate") +
                " the alarm for reminder " + reminder.getName());

        // Check the reminder status and activate or deactivate the alarm accordingly
        if (reminder.isOn()) {
            Log.d(LOG_TAG, "Activating Reminder " + reminder.getName());
            ReminderManager.cancelSingleReminderAlarm(this, reminder);
            ReminderManager.setSingleReminderAlarm(this, reminder);
        } else {
            Log.d(LOG_TAG, "Deactivating Reminder " + reminder.getName());
            ReminderManager.cancelSingleReminderAlarm(this, reminder);
        }

        // Print the updated list of alarms and update the reminder in the data manager
        ReminderManager.printAlarms(this, LoginUtility.getLoginId(this));
        PatientDataManager.updateSingleReminder(context, patientId, reminder);
    }

    /**
     * Callback method to get the patient object for the PatientMainFragment.
     *
     * @return The patient object for the PatientMainFragment.
     */
    public Patient getPatientCallback() {
        return PatientDataManager.findPatient(context, patientId);
    }

    /**
     * Callback method to get the patient object for the HistoryLogFragment.
     * This method is used to display the patient's history logs.
     *
     * @return The patient object for the HistoryLogFragment.
     */
    @Override
    public Patient getPatientForHistory() {
        // Check if the user is logged in as a patient
        if (LoginUtility.isLoggedIn(this) && LoginUtility.getUserRole(this) == UserCredential.UserRole.PATIENT) {
            // Get the patient ID from the login credentials
            patientId = LoginUtility.getLoginId(context);
        } else {
            // If the login properties are not complete or not correct, return null
            Log.d(LOG_TAG, "UNABLE to get Patient because login properties are not completed or they are not correct.");
            return null;
        }

        // Create a new patient object with the patient ID
        Patient patient = new Patient();
        patient.setId(patientId);

        // Get the patient's logs from the content provider
        PatientDataManager.getLogsFromCP(this, patient);

        // Return the patient object with logs
        return patient;
    }
}
