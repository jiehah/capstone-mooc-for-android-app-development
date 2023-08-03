package com.example.symptommanagement.patient;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.MedicationLog;
import com.example.symptommanagement.databinding.ListItemPatientMedicationLogBinding;

/**
 * ArrayAdapter to display a list of MedicationLog objects in a ListView.
 */
public class MedicationLogListAdapter extends ArrayAdapter<MedicationLog> {

    /**
     * Callbacks interface for handling date and time requests.
     */
    public interface Callbacks {
        /**
         * Called when a date and time selection is requested for a medication log.
         *
         * @param position The position of the medication log in the list.
         */
        void onRequestDateTime(int position);
    }

    private final Activity activity;
    private final Context context;
    private final MedicationLog[] logs;

    private ListItemPatientMedicationLogBinding binding;

    /**
     * ViewHolder class to hold the views of a list item for efficient recycling.
     * <p>
     * This class is used within the MedicationLogListAdapter for efficient view management
     * in the ListView. It helps in reusing existing views and avoids frequent calls to
     * findViewById(), improving the performance of the ListView.
     */
    public static class ViewHolder {
        /*
         * CheckBox view for medication taken status.
         */
        CheckBox isTaken;

        /*
         * TextView for displaying the medication question.
         */
        TextView question;

        /*
         * TextView for displaying summary text related to the medication log.
         */
        TextView summary;

        /*
         * Position of the item in the list.
         */
        int savePosition;
    }

    /**
     * Constructor for the MedicationLogListAdapter.
     *
     * @param activity The parent activity.
     * @param logs     An array of MedicationLog objects to display in the list.
     */
    public MedicationLogListAdapter(Activity activity, MedicationLog[] logs) {
        super(activity.getApplicationContext(), R.layout.list_item_patient_medication_log, logs);
        this.context = activity.getApplicationContext();
        this.logs = logs;
        this.activity = activity;
    }

    /**
     * Get the view for a specific position in the list.
     * <p>
     * This method is responsible for creating or reusing a view for a specific position
     * in the list. It uses a ViewHolder pattern to efficiently manage the views and avoid
     * frequent calls to findViewById(), thus improving the performance of the ListView.
     *
     * @param position    The position of the item in the list.
     * @param convertView The recycled view to populate.
     * @param parent      The parent view group.
     * @return The populated view for the item at the given position.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        // Check if the convertView is null, if so, inflate the layout and create a new ViewHolder
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            binding = ListItemPatientMedicationLogBinding.inflate(inflater, parent, false);
            view = binding.getRoot();

            holder = new ViewHolder();
            holder.question = view.findViewById(R.id.patient_medication_check_question);
            holder.summary = view.findViewById(R.id.patient_medication_check_summary);
            holder.isTaken = view.findViewById(R.id.patient_medication_check_answer);

            // Set an OnClickListener for the checkbox
            holder.isTaken.setOnClickListener(view1 -> {
                MedicationLog log = (MedicationLog) holder.isTaken.getTag();
                if (holder.isTaken.isChecked()) {
                    // Notify the activity when the checkbox is checked
                    ((Callbacks) activity).onRequestDateTime(position);
                    if (log.getTaken() > 0) {
                        String summaryText = "Taken on " +
                                log.getTakenDateFormattedString("E, MMM d yyyy 'at' hh:mm a");
                        holder.summary.setText(summaryText);
                    }
                } else {
                    log.setTaken(0L);
                    holder.summary.setText("");
                }
            });

            view.setTag(holder);
            holder.isTaken.setTag(logs[position]);
        } else {
            // If convertView is not null, reuse the ViewHolder
            view = convertView;
            holder = (ViewHolder) view.getTag();
            holder.isTaken.setTag(logs[position]);
        }

        holder.savePosition = position;

        // Set the question text for the current log
        String question = "Did you take " + logs[position].getMed().getName() + "?";
        holder.question.setText(question);
        holder.summary.setText("");
        if (logs[position].getTaken() > 0) {
            String summaryText = "Taken " +
                    logs[position].getTakenDateFormattedString("E, MMM d yyyy 'at' hh:mm a");
            holder.summary.setText(summaryText);
            holder.isTaken.setVisibility(View.INVISIBLE);
        }

        // Set the checkbox state based on whether the medication was taken or not
        holder.isTaken.setChecked(logs[position].getTaken() > 0);

        return view;
    }
}
