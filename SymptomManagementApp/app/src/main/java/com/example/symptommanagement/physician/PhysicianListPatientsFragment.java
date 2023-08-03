package com.example.symptommanagement.physician;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.example.symptommanagement.LoginUtility;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.Patient;
import com.example.symptommanagement.data.Physician;

import java.util.Collection;
import java.util.HashSet;

/**
 * A fragment to display a list of patients assigned to a physician.
 * Implements the ListFragment to provide a standard list UI with patient items.
 * Uses the Callbacks interface to communicate with the hosting activity.
 */
public class PhysicianListPatientsFragment extends ListFragment {

    private static final String LOG_TAG = PhysicianListPatientsFragment.class.getSimpleName();
    public final static String FRAGMENT_TAG = "fragment_patient_list";

    /**
     * Interface to establish communication between the PhysicianListPatientsFragment and its hosting activity.
     * The hosting activity must implement this interface to handle patient selection and provide physician information.
     */
    public interface Callbacks {

        /**
         * Called when a patient is selected from the list of patients.
         *
         * @param physicianId The ID of the physician associated with the patient list.
         * @param patient     The selected patient object.
         */
        void onItemSelected(String physicianId, Patient patient);

        /**
         * Retrieves the Physician object associated with the patient list.
         * The hosting activity should implement this method and return the current Physician object.
         *
         * @return The Physician object representing the current physician associated with the patient list.
         */
        Physician getPhysicianForPatientList();
    }

    static String physicianId;
    static Physician physician;
    static Patient[] patientList = new Patient[0];
    static Collection<Patient> tempList = new HashSet<>();

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private int activatedPosition = ListView.INVALID_POSITION;

    /**
     * Called when the view hierarchy associated with the fragment's UI is created. It sets up
     * the view for the ListFragment and handles the restoration of the activated position (selected
     * item) in the patient list when the fragment is recreated after configuration changes (e.g., screen rotation).
     *
     * @param view               The View returned by {@link ListFragment#onCreateView}
     * @param savedInstanceState A Bundle containing the saved state of the fragment.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retain the fragment instance across configuration changes
        setRetainInstance(true);

        // Restore the activated position if it was saved in the savedInstanceState
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }


    /**
     * Called when the activity's onCreate() method has completed execution. This method is used
     * to set up the ListFragment's UI after the activity is created. It sets the empty text to be
     * displayed when the patient list is empty and configures the ListView to allow single-item selection
     * (CHOICE_MODE_SINGLE) instead of multiple selections.
     *
     * @param savedInstanceState A Bundle containing the saved state of the fragment.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set the text to be displayed when the patient list is empty
        setEmptyText(getString(R.string.empty_list_text));

        // Configure the ListView to allow single-item selection (CHOICE_MODE_SINGLE)
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    /**
     * Called when the fragment is attached to an activity. It ensures that the hosting activity implements
     * the Callbacks interface, which is required for communication between the fragment and the activity.
     *
     * @param activity The activity to which the fragment is attached.
     * @throws IllegalStateException if the hosting activity does not implement the Callbacks interface.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Ensure that the hosting activity implements the Callbacks interface
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(activity.getString(R.string.callbacks_message));
        }
    }

    /**
     * Called when the fragment is resumed (visible to the user). It retrieves the physician from the
     * hosting activity using the Callbacks interface and displays the patient list associated with the physician.
     * If the physician is not null, it calls the displayPatientList method to show the list of patients.
     */
    @Override
    public void onResume() {
        super.onResume();

        // Get the physician from the hosting activity and display the patient list
        physician = ((Callbacks) getActivity()).getPhysicianForPatientList();
        if (physician != null) {
            displayPatientList(physician);
        }
    }

    /**
     * Method to update the physician and display the patient list. It takes a Physician object as a parameter,
     * and if the provided physician is not null, it updates the static variable physician in the
     * PhysicianListPatientsFragment class and calls the displayPatientList method to show the updated patient list.
     *
     * @param physician The updated Physician object to be displayed in the patient list.
     */
    public void updatePhysician(Physician physician) {
        if (physician == null) {
            Log.e(LOG_TAG, "Trying to set physician to null.");
            return;
        }

        // Log the arrival of the new physician and update the static variable
        Log.d(LOG_TAG, "New Physician has arrived! " + physician);
        PhysicianListPatientsFragment.physician = physician;

        // Display the patient list associated with the updated physician
        displayPatientList(PhysicianListPatientsFragment.physician);
    }

    /**
     * Displays the patient list associated with the given physician. It takes a Physician object as a parameter,
     * and if the provided physician is not null, it retrieves the list of patients from the physician. It then
     * converts the patient list to an array, sets it as the data source for the ListFragment's ListView using
     * a custom adapter (PatientListAdapter), and scrolls to the first item in the list if it's not empty.
     *
     * @param physician The Physician object whose associated patient list is to be displayed.
     */
    private void displayPatientList(Physician physician) {
        if (physician == null) {
            activatedPosition = ListView.INVALID_POSITION;
            Log.e(LOG_TAG, "Trying to display a null physician.");
            return;
        }

        // Log the creation of the patient list for the physician
        Log.d(LOG_TAG, "Creating list of all patients assigned to physician");

        // Get the patient list from the physician
        if (physician.getPatients() != null) {
            tempList = physician.getPatients();
            patientList = tempList.toArray(new Patient[0]);
            activatedPosition = (tempList.size() > 0) ? 0 : ListView.INVALID_POSITION;
        }

        // Set the patient list as the data source for the ListFragment's ListView
        setListAdapter(new PatientListAdapter(getActivity(), patientList));

        // Scroll to the first item in the list if it's not empty
        if (activatedPosition != ListView.INVALID_POSITION) {
            getListView().smoothScrollToPosition(activatedPosition);
        }
    }

    /**
     * Adds a patient to the temporary patient list and updates the displayed patient list. It takes a Patient
     * object as a parameter and checks if the tempList (temporary patient list) and the provided patient are not null.
     * If they are not null, the patient is added to the tempList, and the patientList (array) is updated with the
     * contents of the tempList. The updated patient list is then set as the data source for the ListFragment's
     * ListView using the PatientListAdapter, and the ListView is scrolled to the newly added patient's position
     * in the list.
     *
     * @param patient The Patient object to be added to the temporary patient list and displayed.
     */
    public void temporaryAddToList(Patient patient) {
        // Use the current patient list and add one to it
        if (tempList != null && patient != null) {
            tempList.add(patient);
            patientList = tempList.toArray(new Patient[0]);
        }

        // Set the updated patient list as the data source for the ListFragment's ListView
        setListAdapter(new PatientListAdapter(getActivity(), patientList));

        // Scroll to the current position in the list
        if (patientList != null && tempList != null && tempList.size() > 0) {
            activatedPosition = tempList.size() - 1;
        }

        // Scroll to the position of the newly added patient in the list
        if (activatedPosition != ListView.INVALID_POSITION) {
            getListView().smoothScrollToPosition(activatedPosition);
        }
    }

    /**
     * Callback method invoked when an item in the patient list is clicked. It takes the position of the clicked
     * item and retrieves the corresponding Patient object from the ListAdapter. It then notifies the hosting activity
     * (if it implements the Callbacks interface) that a patient is selected by calling the onItemSelected method
     * with the physicianId and the selected patient as parameters.
     *
     * @param listView The ListView containing the patient list.
     * @param view     The clicked View.
     * @param position The position of the clicked item in the list.
     * @param id       The id of the clicked item.
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        setActivatedPosition(position);
        Patient patient = (Patient) getListAdapter().getItem(position);
        Log.d(LOG_TAG, "Patient selected is " + patient.toString());
        physicianId = LoginUtility.getLoginId(getActivity());
        // Notify the hosting activity that a patient is selected
        ((Callbacks) getActivity()).onItemSelected(physicianId, patient);
    }

    /**
     * Callback method to save the state of the fragment into the given outState. It saves the activatedPosition
     * (the position of the currently activated item in the list) into the outState bundle to be restored later.
     *
     * @param outState The Bundle to save the fragment's state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (activatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, activatedPosition);
        }
    }

    /**
     * Sets whether an item in the patient list should be activated on click. It takes a boolean parameter
     * activateOnItemClick, and based on its value, it sets the choice mode of the ListView to either
     * ListView.CHOICE_MODE_SINGLE (for single item selection) or ListView.CHOICE_MODE_NONE (for no item selection).
     *
     * @param activateOnItemClick True to enable item activation on click, false otherwise.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    /**
     * Sets the activated position in the patient list. It takes an integer position parameter, which represents
     * the position of the item to be activated. If the position is equal to ListView.INVALID_POSITION, it means
     * that no item should be activated, so it sets the previously activated item to not checked. Otherwise, it
     * sets the specified item as checked, representing its activation status in the list.
     *
     * @param position The position of the item to be activated in the patient list.
     */
    private void setActivatedPosition(int position) {
        Log.e(LOG_TAG, "Setting the activated list position to: " + position);
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(activatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }
        activatedPosition = position;
    }
}
