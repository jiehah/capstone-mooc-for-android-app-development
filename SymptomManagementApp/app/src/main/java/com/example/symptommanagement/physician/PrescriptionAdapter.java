package com.example.symptommanagement.physician;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.Medication;
import com.example.symptommanagement.databinding.ListItemPrescriptionBinding;

/**
 * An ArrayAdapter to display a list of Prescription items.
 */
public class PrescriptionAdapter extends ArrayAdapter<Medication> {

    /**
     * Interface to handle the callback for deleting a prescription.
     */
    public interface Callbacks {
        /**
         * Callback method to be invoked when a prescription is deleted.
         *
         * @param position   The position of the deleted prescription in the list.
         * @param medication The Medication object representing the prescription.
         */
        void onPrescriptionDelete(int position, Medication medication);
    }

    private final Activity activity;
    private final Context context;
    private final Medication[] prescriptions;
    private ListItemPrescriptionBinding binding;

    /**
     * ViewHolder pattern to improve list item view performance.
     */
    public static class ViewHolder {
        public final ImageView deletePrescription;
        public final TextView prescriptionName;

        public ViewHolder(View view) {
            deletePrescription = view.findViewById(R.id.prescription_delete);
            prescriptionName = view.findViewById(R.id.prescription_name);
        }

        Medication prescription;
        int position;
    }

    /**
     * Constructor for the PrescriptionAdapter.
     *
     * @param activity      The activity context.
     * @param prescriptions The array of Medication objects representing prescriptions.
     */
    public PrescriptionAdapter(Activity activity, Medication[] prescriptions) {
        super(activity, R.layout.list_item_prescription, prescriptions);
        this.activity = activity;
        this.context = activity;
        this.prescriptions = prescriptions;
    }

    /**
     * Get the view for each prescription item in the list.
     *
     * @param position    The position of the item in the list.
     * @param convertView The recycled view to populate.
     * @param parent      The parent view group.
     * @return The view for the prescription item.
     */
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            // If the convertView is null, inflate the view using ViewBinding
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            binding = ListItemPrescriptionBinding.inflate(inflater, parent, false);
            View rowView = binding.getRoot();

            // Create a ViewHolder to store references to views for improved performance
            final ViewHolder viewHolder = new ViewHolder(rowView);

            // Set the prescription object and its name to the views in the layout
            viewHolder.prescription = prescriptions[position];
            viewHolder.prescriptionName.setText(prescriptions[position].getName());

            // Set an OnClickListener for the delete button to handle prescription deletion
            viewHolder.deletePrescription.setOnClickListener(view1 -> ((Callbacks) activity)
                    .onPrescriptionDelete(viewHolder.position, viewHolder.prescription));

            // Set the position of the item as a tag to the rowView for later reference
            viewHolder.position = position;
            rowView.setTag(viewHolder);

            // Set the prescription object as a tag to the deletePrescription button
            viewHolder.deletePrescription.setTag(prescriptions[position]);

            // Set the view to the rowView
            view = rowView;
        } else {
            // If the convertView is not null, reuse the existing view
            view = convertView;
        }

        // Get the ViewHolder from the view's tag for further reference
        ViewHolder holder = (ViewHolder) view.getTag();

        // Update the prescription name and object in the ViewHolder
        holder.prescriptionName.setText(prescriptions[position].getName());
        holder.prescription = prescriptions[position];
        holder.position = position;

        // Return the view for the prescription item
        return view;
    }
}
