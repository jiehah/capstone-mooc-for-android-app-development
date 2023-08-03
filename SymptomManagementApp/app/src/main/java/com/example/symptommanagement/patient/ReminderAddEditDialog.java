package com.example.symptommanagement.patient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.Reminder;

/**
 * DialogFragment class for adding or editing a reminder.
 */
public class ReminderAddEditDialog extends DialogFragment {

    private final static String LOG_TAG = ReminderAddEditDialog.class.getSimpleName();
    public final static String FRAGMENT_TAG = "reminder_add_edit_dialog";

    /**
     * Callback interface to handle interactions with the ReminderAddEditDialog class.
     */
    public interface Callbacks {
        /**
         * Called when the user clicks the "OK" button in the dialog to add a new reminder.
         *
         * @param newReminder The new Reminder object to be added.
         */
        void onReminderAdd(Reminder newReminder);

        /**
         * Called when the user clicks the "OK" button in the dialog to update an existing reminder.
         *
         * @param position The position of the reminder to be updated in the list.
         * @param reminder The updated Reminder object containing the user's changes.
         */
        void onReminderUpdate(int position, Reminder reminder);
    }

    private TimePicker timePicker;
    private EditText reminderName;
    private static int hour;
    private static int minute;
    private static String name;
    private static long dbId;
    private static Reminder reminder;
    private static int position;

    /**
     * Creates a new instance of the ReminderAddEditDialog with initial values set based on the provided Reminder object.
     *
     * @param position The position of the reminder in the list.
     * @param reminder The Reminder object containing the initial values.
     * @return A new instance of the ReminderAddEditDialog.
     */
    public static ReminderAddEditDialog newInstance(int position, Reminder reminder) {
        ReminderAddEditDialog frag = new ReminderAddEditDialog();

        // Setting the initial values for the dialog based on the provided Reminder object.
        ReminderAddEditDialog.reminder = reminder;
        ReminderAddEditDialog.position = position;
        ReminderAddEditDialog.hour = reminder.getHour();
        ReminderAddEditDialog.minute = reminder.getMinutes();
        ReminderAddEditDialog.name = reminder.getName();
        ReminderAddEditDialog.dbId = reminder.getDbId();

        return frag;
    }

    /**
     * Creates and returns the dialog that allows the user to add or edit a reminder.
     *
     * @param savedInstanceState The saved state of the fragment.
     * @return The created Dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the dialog layout
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_reminder_add_edit, null);

        // Create the dialog builder and set the title, view, and button listeners
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getString(R.string.reminder_configuration_title))
                .setView(view)
                .setPositiveButton(getActivity().getString(R.string.Ok_button_text),
                        (dialogInterface, i) -> {
                            // Get the user's selections from the views
                            name = reminderName.getText().toString();
                            hour = timePicker.getCurrentHour();
                            minute = timePicker.getCurrentMinute();

                            // Check if the reminder is being added or updated
                            if (reminder.getHour() == -1 && reminder.getMinutes() == -1) {
                                // Update the reminder object with the new data
                                reminder.setHour(hour);
                                reminder.setMinutes(minute);
                                reminder.setName(name);
                                // Add the new reminder
                                ((Callbacks) getActivity()).onReminderAdd(reminder);
                            } else {
                                // Update the reminder object with the new data
                                reminder.setHour(hour);
                                reminder.setMinutes(minute);
                                reminder.setName(name);
                                // Update the existing reminder
                                reminder.setDbId(dbId);
                                ((Callbacks) getActivity()).onReminderUpdate(position, reminder);
                            }
                        })
                .setNegativeButton(getActivity().getString(R.string.cancel_button_text),
                        (dialogInterface, i) -> {
                            // Do nothing when the "Cancel" button is clicked
                        });

        // Initialize the views to get the user's selections from
        timePicker = view.findViewById(R.id.reminder_timePicker);
        if (hour >= 0) {
            timePicker.setCurrentHour(hour);
        }
        if (minute >= 0) {
            timePicker.setCurrentMinute(minute);
        }

        reminderName = view.findViewById(R.id.reminder_name_edit);
        if (name != null) {
            reminderName.setText(name);
        }

        return builder.create();
    }
}
