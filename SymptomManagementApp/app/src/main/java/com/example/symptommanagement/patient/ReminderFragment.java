package com.example.symptommanagement.patient;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import com.example.symptommanagement.LoginUtility;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.PatientCPContract.ReminderEntry;
import com.example.symptommanagement.data.PatientCPcvHelper;
import com.example.symptommanagement.data.PatientDataManager;
import com.example.symptommanagement.data.Reminder;
import com.example.symptommanagement.databinding.FragmentReminderBinding;
import com.example.symptommanagement.patient.Reminder.ReminderManager;

import java.util.Collection;

/**
 * A fragment that displays a list of reminders for the user.
 */
public class ReminderFragment extends Fragment {

    // Tag for logging purposes
    public final static String LOG_TAG = ReminderFragment.class.getSimpleName();
    public final static String FRAGMENT_TAG = "reminder_fragment";

    /**
     * Callback interface to handle interactions with the ReminderFragment.
     */
    public interface Callbacks {
        /**
         * Called when the user requests to add a new reminder.
         *
         * @param reminder The Reminder object representing the new reminder to be added.
         */
        void onRequestReminderAdd(Reminder reminder);
    }

    ReminderListAdapter adapter;
    private Collection<Reminder> reminders;
    Reminder[] remindersArr;
    FragmentReminderBinding binding;

    /**
     * Called when the fragment is being created.
     *
     * @param savedInstanceState A Bundle containing the saved state of the fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.setRetainInstance(true);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Called when the fragment's view is being created.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A Bundle containing the saved state of the fragment.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReminderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called to create the options menu for the fragment.
     *
     * @param menu     The menu in which you place your items.
     * @param inflater The MenuInflater object that can be used to inflate the menu.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_settings).setVisible(false);
        inflater.inflate(R.menu.reminder_add_menu, menu);
    }

    /**
     * Called when a menu item in the options menu is selected.
     *
     * @param item The selected MenuItem.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            ((Callbacks) getActivity()).onRequestReminderAdd(new Reminder());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the fragment is becoming visible to the user.
     * It is called after the onCreateView() and before onStart().
     * This method is used to resume any operations that were paused
     * or stopped in onPause().
     */
    @Override
    public void onResume() {
        super.onResume();
        loadReminderList();
    }

    /**
     * Loads the list of reminders from the data source and sets up the adapter for the ListView.
     */
    private void loadReminderList() {
        // Check if the reminders array is null (not yet loaded)
        if (remindersArr == null) {
            // Load the sorted list of reminders from the data manager using the current patient's login ID
            reminders = PatientDataManager.loadSortedReminderList(getActivity(), LoginUtility.getLoginId(getActivity()));

            // Convert the reminders collection to an array
            remindersArr = reminders.toArray(new Reminder[0]);
        }

        // Create a new ReminderListAdapter with the loaded reminders array
        adapter = new ReminderListAdapter(getActivity(), remindersArr);

        // Set the adapter for the ListView to display the reminders
        binding.reminderList.setAdapter(adapter);
    }

    /**
     * Adds a new reminder to the database and updates the reminder list.
     *
     * @param newReminder The new Reminder object to be added.
     */
    public void addReminder(Reminder newReminder) {
        // Add to the database first
        String mPatientId = LoginUtility.getLoginId(getActivity());
        newReminder.setCreated(System.currentTimeMillis());

        // Create ContentValues object for inserting the reminder data into the database
        ContentValues cv = PatientCPcvHelper.createInsertValuesObject(mPatientId, newReminder);

        // Insert the reminder data into the database
        Uri uri = getActivity().getContentResolver().insert(ReminderEntry.CONTENT_URI, cv);
        long objectId = ContentUris.parseId(uri);

        // Check if the insert was successful
        if (objectId < 0) {
            Log.e(LOG_TAG, "New Reminder Insert Failed.");
            Toast.makeText(getActivity(), "Failed to Add Reminder.", Toast.LENGTH_LONG).show();
        } else {
            // Set the database ID for the new reminder
            newReminder.setDbId(objectId);

            // Add the new reminder to the local reminders collection
            reminders.add(newReminder);

            // Convert the reminders collection to an array
            remindersArr = reminders.toArray(new Reminder[0]);

            Log.d(LOG_TAG, "Adding a Reminder: " + newReminder.getName());

            // Print the alarms for debugging purposes
            ReminderManager.printAlarms(getActivity(), mPatientId);

            // Set the alarm for the new reminder
            ReminderManager.setSingleReminderAlarm(getActivity(), newReminder);

            // Print the alarms again for debugging purposes
            ReminderManager.printAlarms(getActivity(), mPatientId);

            // Create a new ReminderListAdapter with the updated reminders array
            adapter = new ReminderListAdapter(getActivity(), remindersArr);

            // Set the adapter for the ListView to display the updated reminders
            binding.reminderList.setAdapter(adapter);
        }
    }

    /**
     * Deletes a reminder from the database and updates the reminder list.
     *
     * @param position The position of the reminder to be deleted in the list.
     */
    public void deleteReminder(int position) {
        // Check if the reminder has a valid database ID
        if (remindersArr[position].getDbId() >= 0) {
            // Create the selection string for deleting the reminder from the database
            String selection = ReminderEntry._ID + "=" + remindersArr[position].getDbId();

            // Delete the reminder from the database
            int rowsDeleted = getActivity().getContentResolver()
                    .delete(ReminderEntry.CONTENT_URI, selection, null);

            // Log the number of rows deleted for debugging purposes
            Log.v(LOG_TAG, "Reminder rows deleted: " + rowsDeleted);
        }

        // Log the deletion of the reminder
        Log.d(LOG_TAG, "Deleting a Reminder: " + remindersArr[position].getName());

        // Print the alarms for debugging purposes
        ReminderManager.printAlarms(getActivity(), LoginUtility.getLoginId(getActivity()));

        // Cancel the alarm for the deleted reminder
        ReminderManager.cancelSingleReminderAlarm(getActivity(), remindersArr[position]);

        // Print the alarms again for debugging purposes
        ReminderManager.printAlarms(getActivity(), LoginUtility.getLoginId(getActivity()));

        // Remove the deleted reminder from the local reminders collection
        reminders.remove(remindersArr[position]);

        // Convert the updated reminders collection to an array
        remindersArr = reminders.toArray(new Reminder[0]);

        // Create a new ReminderListAdapter with the updated reminders array
        adapter = new ReminderListAdapter(getActivity(), remindersArr);

        // Set the adapter for the ListView to display the updated reminders
        binding.reminderList.setAdapter(adapter);

        // Notify the adapter that the data has changed to update the ListView
        adapter.notifyDataSetChanged();
    }

    /**
     * Updates a reminder in the database and updates the reminder list.
     *
     * @param position The position of the reminder to be updated in the list.
     * @param temp     The updated Reminder object.
     */
    public void updateReminder(int position, Reminder temp) {
        // Check if the reminder has a valid database ID
        if (remindersArr[position].getDbId() >= 0) {
            // Set the database ID of the updated reminder to match the existing reminder
            temp.setDbId(remindersArr[position].getDbId());

            // Update the reminder in the database
            int rowsUpdated = PatientDataManager.updateSingleReminder(getActivity(),
                    LoginUtility.getLoginId(getActivity()), temp);

            // Log the number of rows updated for debugging purposes
            Log.v(LOG_TAG, "Reminder rows updated: " + rowsUpdated);
        }

        // Log the update of the reminder
        Log.d(LOG_TAG, "Updating a Reminder: " + remindersArr[position].getName());

        // Print the alarms for debugging purposes
        ReminderManager.printAlarms(getActivity(), LoginUtility.getLoginId(getActivity()));

        // Cancel the current alarm for the reminder
        ReminderManager.cancelSingleReminderAlarm(getActivity(), remindersArr[position]);

        // Set a new alarm for the updated reminder
        ReminderManager.setSingleReminderAlarm(getActivity(), remindersArr[position]);

        // Print the alarms again for debugging purposes
        ReminderManager.printAlarms(getActivity(), LoginUtility.getLoginId(getActivity()));

        // Notify the adapter that the data has changed to update the ListView
        adapter.notifyDataSetChanged();
    }
}
