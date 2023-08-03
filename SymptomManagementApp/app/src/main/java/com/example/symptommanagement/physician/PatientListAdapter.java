package com.example.symptommanagement.physician;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.Alert;
import com.example.symptommanagement.data.Patient;
import com.example.symptommanagement.databinding.ListItemPatientListBinding;
import com.example.symptommanagement.sync.SymptomManagementSyncAdapter;

/**
 * The PatientListAdapter is an ArrayAdapter used to populate a list of patients with their
 * information in a custom layout. It efficiently manages the list view and uses a ViewHolder
 * pattern for better performance during scrolling.
 */
public class PatientListAdapter extends ArrayAdapter<Patient> {

    private final Context context;
    private final Patient[] patients;
    private ListItemPatientListBinding binding;

    /**
     * ViewHolder class to hold references to the views of each item in the list, making it more
     * efficient to reuse views when scrolling through the list.
     */
    public static class ViewHolder {
        public final ImageView alertIcon;
        public final TextView patientName;
        public final TextView lastLog;
        public final LinearLayout thisLayout;

        public ViewHolder(View view) {
            // Initialize the views for each item in the list
            alertIcon = view.findViewById(R.id.patient_list_alert_icon);
            patientName = view.findViewById(R.id.patient_list_name);
            lastLog = view.findViewById(R.id.patient_list_last_log);
            thisLayout = view.findViewById(R.id.patient_list_item);
        }

        Patient patient; // Reference to the associated Patient object for this ViewHolder
    }

    /**
     * Constructor for creating the PatientListAdapter.
     *
     * @param context  The context of the activity or fragment using this adapter.
     * @param patients An array of Patient objects to display in the list.
     */
    public PatientListAdapter(Context context, Patient[] patients) {
        super(context, R.layout.list_item_patient_list, patients);
        this.context = context;
        this.patients = patients;
    }

    /**
     * This method is called for each list item when the list is being displayed or scrolled.
     * It handles creating and recycling views efficiently using the ViewHolder pattern.
     *
     * @param position    The position of the item in the list.
     * @param convertView The recycled view to be used, if available.
     * @param parent      The parent ViewGroup for the item.
     * @return The view for the specific list item.
     */
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            // Inflate the custom layout for each list item using LayoutInflater
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            binding = ListItemPatientListBinding.inflate(inflater, parent, false);
            View rowView = binding.getRoot();
            final ViewHolder viewHolder = new ViewHolder(rowView);
            viewHolder.patient = patients[position];
            viewHolder.patientName.setText(patients[position].getName());
            viewHolder.lastLog.setText(patients[position].getFormattedLastLogged());
            // Set the visibility and background color based on the patient's alert severity level
            int severity = SymptomManagementSyncAdapter.findPatientAlertSeverityLevel(patients[position]);
            viewHolder.alertIcon.setVisibility(severity > Alert.PAIN_SEVERITY_LEVEL_0
                    ? ImageView.VISIBLE : ImageView.INVISIBLE);
            int bgColor = (severity > Alert.PAIN_SEVERITY_LEVEL_0
                    ? getContext().getResources().getColor(R.color.sm_pale_yellow)
                    : getContext().getResources().getColor(R.color.white));
            viewHolder.thisLayout.setBackgroundColor(bgColor);
            rowView.setTag(viewHolder);
            view = rowView;
        } else {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        // Update the views with the patient's information
        holder.patientName.setText(patients[position].getName());
        holder.lastLog.setText(patients[position].getFormattedLastLogged());
        holder.patient = patients[position];
        // Update the visibility and background color based on the patient's alert severity level
        int severity = SymptomManagementSyncAdapter.findPatientAlertSeverityLevel(patients[position]);
        holder.alertIcon.setVisibility(severity > Alert.PAIN_SEVERITY_LEVEL_0
                ? ImageView.VISIBLE : ImageView.INVISIBLE);
        int bgColor = (severity > Alert.PAIN_SEVERITY_LEVEL_0
                ? getContext().getResources().getColor(R.color.sm_pale_yellow)
                : getContext().getResources().getColor(R.color.white));
        holder.thisLayout.setBackgroundColor(bgColor);
        return view;
    }
}
