package com.example.symptommanagement.admin.Patient;

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
import com.example.symptommanagement.data.Patient;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A fragment that displays a list of patients using a ListFragment.
 * It communicates with the activity through the Callbacks interface.
 */
public class PatientListFragment extends ListFragment {

    private static final String LOG_TAG = PatientListFragment.class.getSimpleName();
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Interface to communicate with the calling activity
     */
    public interface Callbacks {
        /**
         * Called when a patient is selected from the list.
         *
         * @param id The ID of the selected patient.
         */
        void onPatientSelected(String id);

        /**
         * Called when the user wants to add a new patient.
         */
        void onAddPatient();
    }

    /**
     * Called when the fragment's view is created. It initializes the fragment's view and sets
     * important properties like retain instance state and options menu handling.
     * If there is a saved instance state, it restores the activated position in the ListView.
     *
     * @param view               The fragment's root view.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        setEmptyText(getString(R.string.empty_list_text));

        // Restore the activated position in the ListView if available
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    /**
     * Called when the activity that hosts the fragment has completed its own onCreate method.
     * It is used for further initialization of the fragment after the activity has been fully created.
     * In this case, it sets the empty text for the ListView using the string resource 'R.string.empty_list_text'.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getString(R.string.empty_list_text));
    }

    /**
     * Called when the fragment is attached to its hosting activity.
     * It is used to verify that the hosting activity implements the required Callbacks interface
     * for communication with the fragment.
     *
     * @param activity The hosting activity.
     * @throws IllegalStateException if the hosting activity does not implement the required Callbacks interface.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Ensure the activity implements the Callbacks interface
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
    }

    /**
     * Initialize the options menu for the fragment.
     * Inflate the menu resource and add menu items to it.
     *
     * @param menu     The menu to be initialized.
     * @param inflater The MenuInflater object used to inflate the menu resource.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.admin_add_menu, menu);
    }

    /**
     * Handle the selection of menu items in the options menu.
     *
     * @param item The selected menu item.
     * @return true if the menu item was handled successfully, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            // Handle the "Add" option click by notifying the activity
            ((Callbacks) getActivity()).onAddPatient();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * Refresh the list of patients when the fragment resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        refreshAllPatients();
    }

    /**
     * Called when an item in the ListView is clicked.
     * Handle a patient selection from the list.
     *
     * @param listView The ListView where the click happened.
     * @param view     The view within the ListView that was clicked.
     * @param position The position of the clicked item in the ListView.
     * @param id       The row id of the clicked item.
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        // Handle a patient selection from the list
        Patient patient = (Patient) getListAdapter().getItem(position);
        Log.d(LOG_TAG, "Patient name selected is " + patient.getName() + " id is : " + patient.getId());
        String patientId = patient.getId();
        Log.d(LOG_TAG, " String id value is : " + patientId);
        // Notify the activity about the selected patient
        ((Callbacks) getActivity()).onPatientSelected(patientId);
        setActivatedPosition(position);
    }

    /**
     * Called to save the current state of the fragment.
     * Serialize and persist the activated item position if it's valid.
     *
     * @param outState The Bundle where the state information should be stored.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Serialize and persist the activated item position.
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Sets the choice mode for list items click.
     * If activateOnItemClick is true, it enables single choice mode for list items,
     * allowing only one item to be selected at a time.
     * If activateOnItemClick is false, it disables the choice mode.
     *
     * @param activateOnItemClick True to enable single choice mode, false to disable.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    /**
     * Sets the activated position in the list.
     * If position is ListView.INVALID_POSITION, it clears the activated item selection.
     *
     * @param position The position of the item to be activated.
     */
    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            // Clear the activated item selection
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            // Set the item at 'position' as activated
            getListView().setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }

    /**
     * Refreshes the list of all patients by fetching data from the API.
     * The list is displayed using an ArrayAdapter.
     */
    private void refreshAllPatients() {
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        if (symptomManagementApi != null) {
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "getting all patients");
                return symptomManagementApi.getPatientList();
            }, new TaskCallback<Collection<Patient>>() {

                @Override
                public void success(Collection<Patient> result) {
                    // Display the list of patients using ArrayAdapter
                    setListAdapter(new ArrayAdapter<>(
                            getActivity(),
                            android.R.layout.simple_list_item_activated_1,
                            android.R.id.text1,
                            new ArrayList<>(result)));
                }

                @Override
                public void error(Exception e) {
                    // Show an error toast if unable to fetch patients
                    Toast.makeText(
                            getActivity(),
                            "Unable to fetch the Patients. Please check Internet connection.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
