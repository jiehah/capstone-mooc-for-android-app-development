package com.example.symptommanagement.admin.Patient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import com.example.symptommanagement.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A custom DialogFragment that allows the user to pick a birthdate using a DatePicker.
 */
public class BirthdateDialog extends DialogFragment {
    private final static String LOG_TAG = BirthdateDialog.class.getSimpleName();

    /**
     * Interface to communicate with the calling activity
     */
    public interface Callbacks {
        /**
         * Callback method to be implemented by the activity.
         * Notifies the activity with the selected birthdate in string format (MM-dd-yyyy).
         *
         * @param bday The selected birthdate in string format.
         */
        void onPositiveResult(String bday);

        /**
         * Callback method to be implemented by the activity.
         * Notifies the activity that the dialog was canceled.
         */
        void onNegativeResult();
    }

    /**
     * DatePicker widget used in the dialog
     */
    private DatePicker datePicker;

    /**
     * The selected or stored birthdate in string format (MM-dd-yyyy)
     */
    private String birthday = "";

    /**
     * Static method to create a new instance of the dialog with arguments.
     *
     * @param birthdate The initial birthdate to be displayed in the dialog.
     * @return The new instance of BirthdateDialog with the provided birthdate as arguments.
     */
    public static BirthdateDialog newInstance(String birthdate) {
        BirthdateDialog frag = new BirthdateDialog();
        Bundle args = new Bundle();
        args.putString("bday", birthdate);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Create the dialog with its UI elements and setup.
     *
     * @param savedInstanceState The saved state of the dialog (if any).
     * @return The created Dialog instance.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the initial birthdate value from the arguments
        birthday = getArguments().getString("bday");

        // Inflate the dialog view from layout file
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_birthday_entry, null);

        // Create the AlertDialog with necessary configurations
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Enter Patient's Birthday")
                .setView(view)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    // Handle the positive button click (Ok)
                    // Convert the selected date to string format and notify the activity
                    birthday = convertSelectionToString();
                    ((Callbacks) getActivity()).onPositiveResult(birthday);
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    // Handle the negative button click (Cancel)
                    // Set the birthday to an empty string and notify the activity about the cancellation
                    birthday = "";
                    ((Callbacks) getActivity()).onNegativeResult();
                });

        // Get the DatePicker widget from the view
        datePicker = view.findViewById(R.id.birthday_date_picker);

        // Set the datePicker to display the birthday if available
        convertStringToSelection(birthday);

        return builder.create();
    }

    /**
     * Convert the selected date in the DatePicker to a string format (MM-dd-yyyy).
     *
     * @return The selected birthdate in string format (MM-dd-yyyy).
     */
    private String convertSelectionToString() {
        long dateTime = datePicker.getCalendarView().getDate();
        Date date = new Date(dateTime);
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        return dateFormat.format(date);
    }

    /**
     * Convert the stored string birthdate to date components and set the DatePicker accordingly.
     *
     * @param day The stored birthdate in string format (MM-dd-yyyy).
     */
    private void convertStringToSelection(String day) {
        if (day == null || day.isEmpty()) return;

        Date theDate;
        try {
            // Parse the stored string birthdate to a Date object
            theDate = new SimpleDateFormat("MM-dd-yyyy").parse(day);
        } catch (ParseException e) {
            // Log an error if the birthdate string is not in the expected format
            Log.d(LOG_TAG, "Birthdate String not in expected format MM-dd-yyyy. Ignoring.");
            return;
        }

        // Set the DatePicker to display the components (year, month, day) of the parsed Date
        Calendar cal = Calendar.getInstance();
        cal.setTime(theDate);
        datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }
}
