package com.example.symptommanagement.physician;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.Medication;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A ListFragment to display a list of medications.
 */
public class MedicationListFragment extends ListFragment {

    private static final String LOG_TAG = MedicationListFragment.class.getSimpleName();
    public final static String FRAGMENT_TAG = "fragment_medication_list";

    /**
     * A collection to hold the medications to be displayed in the list
     */
    private static Collection<Medication> medications;

    /**
     * Callback interface to handle interactions with the list of medications.
     */
    public interface Callbacks {
        /**
         * Called when a medication is selected from the list
         */
        void onMedicationSelected(Medication medication);

        /**
         * Called when the "Add Medication" action is triggered
         */
        void onAddMedication();

        /**
         * Determines whether to show the "Add Medication" option in the options menu
         */
        boolean showAddMedicationOptionsMenu();

        /**
         * Get the current list of medications from the hosting activity
         */
        Collection<Medication> getMedications();
    }

    /**
     * Called when the view is created.
     *
     * @param view               The view of the fragment.
     * @param savedInstanceState The saved state of the fragment.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Save fragment across configuration changes
        setRetainInstance(true);

        // Check if the activity wants to display the "Add Medication" icon in the options menu
        setHasOptionsMenu(((Callbacks) getActivity()).showAddMedicationOptionsMenu());
    }

    /**
     * Called when the hosting activity's onCreate() method has completed.
     *
     * @param savedInstanceState The saved state of the fragment, if available.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set the text to be displayed when the list is empty
        setEmptyText(getString(R.string.empty_list_text));

        // Save the fragment across configuration changes to retain its instance
        // This ensures that the fragment is not destroyed and recreated during
        // configuration changes like screen rotation or keyboard visibility change
        setRetainInstance(true);
    }

    /**
     * Called when the fragment is attached to the hosting activity.
     *
     * @param activity The hosting activity.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Check if the activity implements the Callbacks interface
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(activity.getString(R.string.callbacks_message));
        }
    }

    /**
     * Called when the options menu is being created.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater The MenuInflater to inflate the menu.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the options menu with the "Add" item
        inflater.inflate(R.menu.admin_add_menu, menu);
    }

    /**
     * Called when an item in the options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle options menu item clicks

        // Check if the clicked item's ID is the "Add" item's ID
        if (item.getItemId() == R.id.action_add) {
            // Call the hosting activity's onAddMedication() method
            // This method will be implemented in the hosting activity to perform the "Add Medication" action
            ((Callbacks) getActivity()).onAddMedication();
            return true;
        }

        // Return false to allow normal menu processing to proceed
        // (This means that if the item ID doesn't match the "Add" item ID,
        // the click will be handled by other parts of the system)
        return super.onOptionsItemSelected(item);
    }

    /**
     * Attempt to display the list of medications when the fragment resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Get the current list of medications from the hosting activity
        medications = ((Callbacks) getActivity()).getMedications();
        // Display the medications in the list
        displayMedications(medications);
    }

    /**
     * Called by the hosting activity to update the medication list from the server.
     *
     * @param meds List of medications to be displayed.
     */
    public void updateMedications(Collection<Medication> meds) {
        medications = meds;
        // Update the displayed medications in the list
        displayMedications(medications);
    }

    /**
     * Called when an item in the list is clicked.
     *
     * @param listView The ListView containing the clicked item.
     * @param view     The clicked item view.
     * @param position The position of the clicked item in the list.
     * @param id       The id of the clicked item.
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        // Get the selected medication from the list
        Medication med = (Medication) getListAdapter().getItem(position);
        // Call the hosting activity's onMedicationSelected() method
        ((Callbacks) getActivity()).onMedicationSelected(med);
    }

    /**
     * Display the list of medications in the ListView.
     *
     * @param medications The collection of medications to be displayed.
     */
    public void displayMedications(final Collection<Medication> medications) {
        if (medications == null) {
            // Log an error if the medication list is null
            Log.e(LOG_TAG, "Trying to display a null medication list.");
            return;
        }
        // Create and set a new ArrayAdapter to display the medications in the ListView
        setListAdapter(new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                new ArrayList<>(medications)));
    }
}
