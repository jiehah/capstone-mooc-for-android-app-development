package com.example.symptommanagement.admin.Physician;

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
import android.widget.Toast;
import com.example.symptommanagement.R;
import com.example.symptommanagement.client.CallableTask;
import com.example.symptommanagement.client.SymptomManagementApi;
import com.example.symptommanagement.client.SymptomManagementService;
import com.example.symptommanagement.client.TaskCallback;
import com.example.symptommanagement.data.Physician;

import java.util.ArrayList;
import java.util.Collection;

/**
 * PhysicianListFragment displays a list of physicians.
 * This fragment is responsible for showing a list of physicians and handling user interactions
 * such as selecting a physician or adding a new physician. It also communicates with the
 * hosting activity through the Callbacks interface.
 */
public class PhysicianListFragment extends ListFragment {
    private static final String LOG_TAG = PhysicianListFragment.class.getSimpleName();
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private int activatedPosition = ListView.INVALID_POSITION;

    /**
     * Callbacks interface to communicate with the hosting activity.
     * The hosting activity must implement this interface to handle interactions
     * with the PhysicianListFragment.
     */
    public interface Callbacks {
        /**
         * Called when a physician is selected from the list.
         *
         * @param physicianId The ID of the selected physician.
         * @param firstName   The first name of the selected physician.
         * @param lastName    The last name of the selected physician.
         */
        void onPhysicianSelected(String physicianId, String firstName, String lastName);

        /**
         * Called when the user wants to add a new physician.
         * The hosting activity should handle the addition of a new physician.
         */
        void onAddPhysician();

        /**
         * Check whether to show the "Add Physician" option in the options menu.
         *
         * @return True if the "Add Physician" option should be shown, false otherwise.
         */
        boolean showAddPhysicianOptionsMenu();
    }

    /**
     * Initialize the fragment view and restore the saved instance state.
     *
     * @param view               The fragment view.
     * @param savedInstanceState The saved instance state.
     */
    /**
     * Called when the view hierarchy is created for the fragment.
     * This method is called after the fragment's layout is inflated and the view hierarchy is ready.
     * It sets up the initial state of the fragment, including retaining the instance, setting up options menu,
     * and checking for the activated position in the saved instance state.
     *
     * @param view               The fragment's root view.
     * @param savedInstanceState The saved instance state, which contains information about the previous state of the fragment.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Call the superclass method to perform necessary setup for the fragment's view
        super.onViewCreated(view, savedInstanceState);

        // Retain the instance of the fragment during configuration changes (e.g., screen rotation)
        setRetainInstance(true);

        // Set whether the fragment should have options menu (e.g., "Add Physician" menu item)
        setHasOptionsMenu(((Callbacks) getActivity()).showAddPhysicianOptionsMenu());

        // Set the text to be displayed when the list is empty
        setEmptyText(getString(R.string.empty_list_text));

        // Check if there is a saved instance state and if it contains the activated position
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            // Restore the activated position from the saved instance state
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    /**
     * Called when the activity's onCreate() method has completed.
     * Set the empty text for the list and restore the saved instance state.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getString(R.string.empty_list_text));
    }

    /**
     * Called when the fragment is attached to an activity.
     * Verify that the hosting activity implements the Callbacks interface.
     *
     * @param activity The hosting activity.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
    }

    /**
     * Inflate the options menu with the "Add Physician" option.
     *
     * @param menu     The option's menu.
     * @param inflater The menu inflater.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.admin_add_menu, menu);
    }

    /**
     * Called when an options menu item is selected.
     * Handle the "Add Physician" menu item click by notifying the hosting activity.
     *
     * @param item The selected menu item.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            ((Callbacks) getActivity()).onAddPhysician();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the fragment is resumed.
     * Refresh the list of all physicians when the fragment resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        refreshAllPhysicians();
    }

    /**
     * Called when a list item is clicked.
     * Handle the click event on the list item by getting the selected Physician object,
     * logging its name and ID, notifying the hosting activity with the selected physician's details,
     * and setting the clicked item as activated.
     *
     * @param listView The ListView containing the item views.
     * @param view     The clicked item view.
     * @param position The position of the clicked item in the list.
     * @param id       The ID of the clicked item.
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        // Call the superclass method to perform the default click behavior
        super.onListItemClick(listView, view, position, id);

        // Get the selected Physician object from the ListAdapter based on the position
        Physician physician = (Physician) getListAdapter().getItem(position);

        // Log the name and ID of the selected physician
        Log.d(LOG_TAG, "Physician name selected is " + physician.getName()
                + " id is : " + physician.getId());

        // Get the ID of the selected physician
        String physicianId = physician.getId();

        // Log the ID value
        Log.d(LOG_TAG, "String id value is : " + physicianId);

        // Notify the hosting activity with the selected physician's details
        ((Callbacks) getActivity()).onPhysicianSelected(physicianId,
                physician.getFirstName(), physician.getLastName());

        // Set the clicked item as activated (highlighted)
        setActivatedPosition(position);
    }

    /**
     * Save the instance state by serializing and persisting the activated item position.
     *
     * @param outState The bundle to save the instance state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (activatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, activatedPosition);
        }
    }

    /**
     * Set whether the list items are activated on click.
     * When setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
     * This means that when a list item is clicked, it will appear highlighted (activated).
     *
     * @param activateOnItemClick True to activate items on click, false otherwise.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // Determine the choice mode based on the given boolean parameter
        int choiceMode = activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE;

        // Set the choice mode of the ListView to either CHOICE_MODE_SINGLE or CHOICE_MODE_NONE
        getListView().setChoiceMode(choiceMode);
    }


    /**
     * Set the activated position of the list item.
     * When an item is activated, it will have a highlighted state, indicating it is selected.
     *
     * @param position The position of the item to be set as activated.
     *                 Use ListView.INVALID_POSITION to deactivate any previously activated item.
     */
    private void setActivatedPosition(int position) {
        // Check if the position is ListView.INVALID_POSITION, indicating that no item should be activated
        if (position == ListView.INVALID_POSITION) {
            // Deactivate any previously activated item by setting its checked state to false
            getListView().setItemChecked(activatedPosition, false);
        } else {
            // Activate the item at the given position by setting its checked state to true
            getListView().setItemChecked(position, true);
        }

        // Update the activatedPosition variable to the new position
        activatedPosition = position;
    }

    /**
     * Refresh the list of all physicians.
     * This method fetches the list of physicians from the server and updates the list view accordingly.
     * If the server call is successful, it populates the list view with the fetched physicians.
     * If there is an error in fetching the data, it displays an error toast and navigates back to the previous screen.
     */
    private void refreshAllPhysicians() {
        // Check if the SymptomManagementApi is available
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();

        if (symptomManagementApi != null) {
            // Fetch the list of physicians from the server using CallableTask
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "getting all physicians");
                return symptomManagementApi.getPhysicianList();
            }, new TaskCallback<Collection<Physician>>() {
                @Override
                public void success(Collection<Physician> result) {
                    // On success, create a new ArrayAdapter and set it as the ListAdapter
                    Log.d(LOG_TAG, "creating list of all physicians");
                    setListAdapter(new ArrayAdapter<>(
                            getActivity(),
                            android.R.layout.simple_list_item_activated_1,
                            android.R.id.text1,
                            new ArrayList<>(result)));
                }

                @Override
                public void error(Exception e) {
                    // On error, show a Toast with an error message and navigate back to the previous screen
                    Toast.makeText(
                            getActivity(),
                            "Unable to fetch the Physicians please check Internet connection.",
                            Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }
            });
        }
    }
}
