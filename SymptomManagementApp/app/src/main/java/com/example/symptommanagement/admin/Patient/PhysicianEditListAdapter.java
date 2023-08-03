package com.example.symptommanagement.admin.Patient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.Physician;
import com.example.symptommanagement.databinding.ListItemPhysicianEditBinding;

/**
 * Custom ArrayAdapter for displaying a list of physicians in an editable list view.
 * The adapter is responsible for binding the data from the array of physicians to the list view.
 * It also handles the view recycling and updates the UI with the physician information.
 */
public class PhysicianEditListAdapter extends ArrayAdapter<Physician> {
    private final Context context;
    private final Physician[] physicians;
    private ListItemPhysicianEditBinding binding;

    /**
     * ViewHolder pattern to improve the performance of the list view by recycling views.
     * The ViewHolder holds references to the views in the list item layout to avoid frequent lookups.
     * It also stores the corresponding Physician object for the list item.
     */
    private static class ViewHolder {
        private final ImageView deleteView;
        private final TextView textView;
        private Physician physician;

        /**
         * Constructor for the ViewHolder class.
         *
         * @param view The view representing a single item in the list view.
         */
        public ViewHolder(View view) {
            // Initialize the views in the list item layout

            // Find the ImageView with the ID "physician_list_delete_item" in the list item layout
            // This view is used to display the delete icon/button for the physician item in the list
            deleteView = view.findViewById(R.id.physician_list_delete_item);

            // Find the TextView with the ID "physician_list_name_item" in the list item layout
            // This view is used to display the name of the physician in the list
            textView = view.findViewById(R.id.physician_list_name_item);
        }
    }

    /**
     * Constructor for the PhysicianEditListAdapter.
     *
     * @param context    The context of the activity or fragment using the adapter.
     * @param physicians The array of Physician objects to be displayed in the list view.
     */
    public PhysicianEditListAdapter(Context context, Physician[] physicians) {
        // Call the superclass constructor to initialize the adapter with the array of physicians
        super(context, R.layout.list_item_physician_edit, physicians);
        this.context = context;
        this.physicians = physicians;
    }

    /**
     * This method is responsible for rendering each item in the list view.
     *
     * @param position    The position of the item in the array.
     * @param convertView The recycled view to be used for the current list item.
     * @param parent      The parent view group of the list view.
     * @return The view for the current list item.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            // If the convertView is null, inflate the list item layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            binding = ListItemPhysicianEditBinding.inflate(inflater, parent, false);
            View rowView = binding.getRoot();

            // Create a ViewHolder and associate it with the views in the list item layout
            final ViewHolder viewHolder = new ViewHolder(rowView);
            viewHolder.physician = physicians[position];
            viewHolder.textView.setText(physicians[position].toString());

            // Set the ViewHolder as the tag for the rowView to enable recycling of views
            rowView.setTag(viewHolder);
            viewHolder.deleteView.setTag(viewHolder.physician);

            view = rowView;
        } else {
            // If the convertView is not null, reuse the existing view
            view = convertView;
        }

        // Retrieve the ViewHolder from the recycled view and update its data with the current physician
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.textView.setText(physicians[position].getName());
        holder.physician = physicians[position];

        return view;
    }
}
