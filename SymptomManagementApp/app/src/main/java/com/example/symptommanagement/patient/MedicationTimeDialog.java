package com.example.symptommanagement.patient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import com.example.symptommanagement.R;

import java.util.Calendar;

/**
 * A dialog fragment that allows the user to select a medication time and date.
 */
public class MedicationTimeDialog extends DialogFragment {

    public final static String FRAGMENT_TAG = "patient_medication_time_dialog";
    private final static String logPositionKey = "logPosition";

    /**
     * A callback interface used by the MedicationTimeDialog to communicate with the hosting activity.
     */
    public interface Callbacks {

        /**
         * Called when the user confirms the selected medication time and date.
         *
         * @param msTime   The selected time and date in milliseconds.
         * @param position The position of the medication log.
         */
        void onPositiveResult(long msTime, int position);

        /**
         * Called when the user cancels the selection of medication time and date.
         *
         * @param msTime   The selected time and date in milliseconds (0 if canceled).
         * @param position The position of the medication log.
         */
        void onNegativeResult(long msTime, int position);
    }

    private TimePicker timePicker;
    private DatePicker datePicker;
    private long msTime = 0L;
    private int mLogPosition;

    /**
     * Creates a new instance of the MedicationTimeDialog with the specified position.
     *
     * @param position The position of the medication log.
     * @return A new instance of the MedicationTimeDialog.
     */
    public static MedicationTimeDialog newInstance(int position) {
        MedicationTimeDialog frag = new MedicationTimeDialog();
        Bundle args = new Bundle();
        args.putInt(logPositionKey, position);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Creates and returns an instance of the Medication Time Dialog.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return The newly created Medication Time Dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mLogPosition = getArguments().getInt(logPositionKey);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_medication_taken_entry, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getString(R.string.med_time_taken_question))
                .setView(view)
                .setPositiveButton(getActivity().getString(R.string.Ok_button_text),
                        (dialogInterface, i) -> {
                            msTime = convertSelectionToMilliseconds();
                            ((Callbacks) getActivity()).onPositiveResult(msTime, mLogPosition);
                        })
                .setNegativeButton(getActivity().getString(R.string.cancel_button_text),
                        (dialogInterface, i) -> {
                            msTime = 0L;
                            ((Callbacks) getActivity()).onNegativeResult(msTime, mLogPosition);
                        });

        // Set up the widgets to get the selections from
        timePicker = view.findViewById(R.id.medication_timePicker);
        datePicker = view.findViewById(R.id.medication_datePicker);

        return builder.create();
    }

    /**
     * Converts the user's selected time and date to milliseconds.
     *
     * @return The selected time and date in milliseconds.
     */
    private long convertSelectionToMilliseconds() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
        return calendar.getTimeInMillis();
    }
}
