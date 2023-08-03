package com.example.symptommanagement.physician;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.HistoryLog;
import com.example.symptommanagement.databinding.ListItemPatientHistoryLogBinding;

/**
 * Custom ArrayAdapter for displaying HistoryLog objects in a ListView.
 * This adapter efficiently handles view recycling using the ViewHolder pattern.
 */
public class HistoryLogAdapter extends ArrayAdapter<HistoryLog> {

    private final Context context;
    private final HistoryLog[] logs;
    private ListItemPatientHistoryLogBinding binding;

    /**
     * ViewHolder class to hold views for each list item
     */
    public static class ViewHolder {

        /**
         * Icon representing the log type
         */
        public final ImageView typeIcon;

        /**
         * Information about the log
         */
        public final TextView info;

        /**
         * Timestamp of when the log was created
         */
        public final TextView created;

        public ViewHolder(View view) {
            typeIcon = view.findViewById(R.id.log_type_icon);
            info = view.findViewById(R.id.history_log_info);
            created = view.findViewById(R.id.history_log_created);
        }

        /**
         * The associated HistoryLog object
         */
        HistoryLog log;
    }

    /**
     * Constructor for the HistoryLogAdapter.
     *
     * @param context The application context.
     * @param logs    An array of HistoryLog objects to be displayed.
     */
    public HistoryLogAdapter(Context context, HistoryLog[] logs) {
        super(context, R.layout.list_item_patient_history_log, logs);
        this.context = context;
        this.logs = logs;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            // Inflating the custom list item layout using LayoutInflater
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            binding = ListItemPatientHistoryLogBinding.inflate(inflater, parent, false);
            View rowView = binding.getRoot();

            // Initializing the ViewHolder with views and setting the corresponding HistoryLog object
            final ViewHolder viewHolder = new ViewHolder(rowView);
            viewHolder.log = logs[position];
            viewHolder.info.setText(logs[position].getInfo());
            viewHolder.created.setText(logs[position].getFormattedCreatedDate());
            viewHolder.typeIcon.setImageResource(getImageResourceForLogType(logs[position].getType()));

            // Setting the ViewHolder as a tag for the rowView to enable efficient view recycling
            rowView.setTag(viewHolder);
            view = rowView;
        } else {
            // If convertView is not null (recycling view), retrieve the ViewHolder from its tag
            view = convertView;
        }

        // Updating the ViewHolder with the appropriate HistoryLog object's data
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.info.setText(logs[position].getInfo());
        holder.created.setText(logs[position].getFormattedCreatedDate());
        holder.log = logs[position];
        holder.typeIcon.setImageResource(getImageResourceForLogType(logs[position].getType()));

        return view;
    }

    /**
     * Get the resource ID of the icon corresponding to the given log type.
     *
     * @param type The log type for which to get the icon.
     * @return The resource ID of the corresponding icon.
     */
    public static synchronized int getImageResourceForLogType(HistoryLog.LogType type) {
        // Return the appropriate icon resource ID based on the log type
        if (type == HistoryLog.LogType.PAIN_LOG) {
            return R.drawable.ic_action_pain_history;
        }
        if (type == HistoryLog.LogType.CHECK_IN_PAIN_LOG) {
            return R.drawable.ic_action_pain_history_ci;
        }
        if (type == HistoryLog.LogType.MED_LOG) {
            return R.drawable.ic_action_green_pill;
        }
        if (type == HistoryLog.LogType.CHECK_IN_MED_LOG) {
            return R.drawable.ic_action_green_pill_ci;
        }
        if (type == HistoryLog.LogType.STATUS_LOG) {
            return R.drawable.ic_action_brown_log;
        }
        if (type == HistoryLog.LogType.CHECK_IN_LOG) {
            return R.drawable.ic_action_check_in;
        }
        // Default icon if the log type is not recognized
        return R.drawable.ic_action_pain_history;
    }
}
