package com.example.symptommanagement.patient;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.Reminder;
import com.example.symptommanagement.databinding.ListItemReminderBinding;

import java.util.Calendar;

/**
 * ReminderListAdapter is an ArrayAdapter for displaying Reminder items in a ListView.
 */
public class ReminderListAdapter extends ArrayAdapter<Reminder> {

    private final static String LOG_TAG = ReminderAddEditDialog.class.getSimpleName();
    private ListItemReminderBinding binding;

    /**
     * Callbacks interface to handle interactions with the list items.
     */
    public interface Callbacks {
        void onRequestReminderEdit(int position, Reminder reminder);

        void onReminderDelete(int position, Reminder reminder);

        void onRequestReminderActivate(Reminder reminder);
    }

    private final Activity activity;
    private final Context context;
    private final Reminder[] reminders;

    /**
     * The ReminderHolder class is used to hold a Reminder object and its position in the list.
     */
    public static class ReminderHolder {
        /**
         * The Reminder object to be held
         */
        Reminder reminder;

        /**
         * The position of the Reminder object in the list
         */
        int position;
    }


    /**
     * The ViewHolder class is used to hold views for each item in the list in the ReminderListAdapter.
     * It represents the layout of a single item in the list and holds references to its views.
     */
    public static class ViewHolder {
        /**
         * Switch view for toggling the reminder on/off
         */
        Switch isActive;

        /**
         * TextView to display the reminder name
         */
        TextView reminderName;

        /**
         * TextView to display the reminder time summary
         */
        TextView reminderSummary;

        /**
         * ImageView for the delete button to remove the reminder
         */
        ImageView deleteView;

        /**
         * ImageView for the edit button to modify the reminder
         */
        ImageView editView;

        /**
         * ReminderHolder object to store the Reminder and its position in the list
         */
        ReminderHolder reminderHolder;
    }

    /**
     * Constructor for the ReminderListAdapter.
     *
     * @param activity  The activity context.
     * @param reminders An array of Reminder objects to be displayed in the list.
     */
    public ReminderListAdapter(Activity activity, Reminder[] reminders) {
        super(activity.getApplicationContext(), R.layout.list_item_reminder, reminders);
        this.context = activity.getApplicationContext();
        this.reminders = reminders;
        this.activity = activity;
    }

    /**
     * Overrides the getView method to provide a custom view for each item in the list.
     *
     * @param position    The position of the item in the list.
     * @param convertView The old view to reuse if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return The custom view for the item at the given position.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            binding = ListItemReminderBinding.inflate(inflater, parent, false);
            view = binding.getRoot();

            holder = new ViewHolder();
            holder.reminderHolder = new ReminderHolder();
            holder.reminderName = view.findViewById(R.id.reminder_name);
            holder.reminderSummary = view.findViewById(R.id.reminder_time_summary);
            holder.isActive = view.findViewById(R.id.reminder_switch);
            holder.isActive.setChecked(reminders[position].isOn());

            holder.isActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Reminder reminder = (Reminder) buttonView.getTag();
                if (reminder.isOn() != isChecked) {
                    reminder.setOn(isChecked);
                    Log.d(LOG_TAG, "Reminder switch has changed ... it is now "
                            + (reminder.isOn() ? "ON" : "OFF"));
                    ((Callbacks) activity).onRequestReminderActivate(reminder);
                }
            });

            holder.deleteView = view.findViewById(R.id.reminder_delete_button);
            holder.deleteView.setOnClickListener(view1 -> {
                ReminderHolder remHolder = (ReminderHolder) holder.deleteView.getTag();
                ((Callbacks) activity).onReminderDelete(remHolder.position, remHolder.reminder);
            });

            holder.editView = view.findViewById(R.id.reminder_edit_button);
            holder.editView.setOnClickListener(view12 -> {
                ReminderHolder remHolder = (ReminderHolder) holder.editView.getTag();
                ((Callbacks) activity).onRequestReminderEdit(remHolder.position, remHolder.reminder);
            });

            view.setTag(holder);
            holder.isActive.setTag(reminders[position]);
            holder.editView.setTag(holder.reminderHolder);
            holder.deleteView.setTag(holder.reminderHolder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
            holder.isActive.setTag(reminders[position]);
        }

        // Set data for the views in the ViewHolder
        holder.reminderHolder.position = position;
        holder.reminderHolder.reminder = reminders[position];
        holder.reminderName.setText(reminders[position].getName());

        String summary = "";
        if (reminders[position].getHour() >= 0 && reminders[position].getMinutes() >= 0) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, reminders[position].getHour());
            cal.set(Calendar.MINUTE, reminders[position].getMinutes());
            int hour = cal.get(Calendar.HOUR);
            if (hour == 0 || hour == 23) {
                hour = 12;
            }
            int min = cal.get(Calendar.MINUTE);
            String minString = "";
            if (min < 10) {
                minString += "0";
            }
            minString += Integer.toString(min);
            int am_pm = cal.get(Calendar.AM_PM);
            summary = hour + ":" + minString + (am_pm == 1 ? "PM" : "AM");
        }

        holder.reminderSummary.setText(summary);
        holder.isActive.setChecked(reminders[position].isOn());

        return view;
    }
}
