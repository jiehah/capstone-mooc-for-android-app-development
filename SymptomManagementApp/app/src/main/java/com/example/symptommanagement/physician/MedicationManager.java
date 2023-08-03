package com.example.symptommanagement.physician;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.example.symptommanagement.client.CallableTask;
import com.example.symptommanagement.client.SymptomManagementApi;
import com.example.symptommanagement.client.SymptomManagementService;
import com.example.symptommanagement.client.TaskCallback;
import com.example.symptommanagement.data.Medication;

import java.util.Collection;

/**
 * The MedicationManager class handles saving and retrieving medication information using an API service.
 * It provides methods to save medication information to the server and retrieve the list of all medications.
 */
public class MedicationManager {

    private static final String LOG_TAG = PhysicianManager.class.getSimpleName();

    /**
     * Callback interface for notifying the activity about changes in the medication list.
     */
    public interface Callbacks {
        /**
         * Sets the list of medications in the hosting activity.
         *
         * @param medications The collection of medications to be set in the activity.
         */
        void setMedicationList(Collection<Medication> medications);
    }

    /**
     * Save medication information to the server. This method is synchronized to ensure thread safety.
     *
     * @param activity   The context of the hosting activity.
     * @param medication The Medication object to be saved.
     */
    public static synchronized void saveMedication(final Context activity, final Medication medication) {
        // Check if the medication name is empty or null
        if (medication.getName() == null || medication.getName().isEmpty()) {
            // Log an error and return if the medication name is not provided
            Log.e(LOG_TAG, "No medication name was given. Unable to Save Medication.");
            return;
        }

        // Get the SymptomManagementApi instance
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        if (symptomManagementApi != null) {
            // Invoke the API service asynchronously using CallableTask
            CallableTask.invoke(() -> {
                // If the medication ID is not provided, it's a new medication (add)
                // Otherwise, update the existing medication
                if (medication.getId() == null || medication.getId().isEmpty()) {
                    Log.d(LOG_TAG, "adding medication: " + medication.toDebugString());
                    // Call the API to add medication
                    return symptomManagementApi.addMedication(medication);
                } else {
                    Log.d(LOG_TAG, "updating medication: " + medication.toDebugString());
                    // Call the API to update medication
                    return symptomManagementApi.updateMedication(medication.getId(), medication);
                }
            }, new TaskCallback<Medication>() {
                @Override
                public void success(Medication result) {
                    // If the medication change was successful, get all medications from the server
                    Log.d(LOG_TAG, "Medication change was successful. Going back to the server to get the list of all medications.");
                    getAllMedications(activity); // Call the method to fetch all medications
                }

                @Override
                public void error(Exception e) {
                    // Show a toast with an error message if there's an issue saving the medication
                    Toast.makeText(activity,
                            "Unable to SAVE Medication. Please check the Internet connection and try again.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Retrieve the list of all medications from the server. This method is synchronized to ensure thread safety.
     *
     * @param activity The context of the hosting activity.
     */
    public static synchronized void getAllMedications(final Context activity) {
        // Get the SymptomManagementApi instance
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        if (symptomManagementApi != null) {
            // Invoke the API service asynchronously using CallableTask
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "Getting the list of all medications");
                // Call the API to get all medications
                return symptomManagementApi.getMedicationList();
            }, new TaskCallback<Collection<Medication>>() {
                // Callback for handling the success result
                @Override
                public void success(Collection<Medication> result) {
                    // Notify the activity about the updated list of medications
                    Log.d(LOG_TAG, "Obtained the list of medications");
                    if (result != null) {
                        ((Callbacks) activity).setMedicationList(result); // Call the activity's setMedicationList() method
                    }
                }

                // Callback for handling errors
                @Override
                public void error(Exception e) {
                    // Show a toast with an error message if there's an issue fetching medications
                    Toast.makeText(
                            activity,
                            "Unable to fetch the Medications. Please check the Internet connection.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
