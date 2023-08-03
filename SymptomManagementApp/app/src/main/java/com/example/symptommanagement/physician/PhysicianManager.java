package com.example.symptommanagement.physician;

import android.content.Context;
import android.util.Log;
import com.example.symptommanagement.client.CallableTask;
import com.example.symptommanagement.client.SymptomManagementApi;
import com.example.symptommanagement.client.SymptomManagementService;
import com.example.symptommanagement.client.TaskCallback;
import com.example.symptommanagement.data.Patient;
import com.example.symptommanagement.data.Physician;
import com.example.symptommanagement.data.StatusLog;

import java.util.HashSet;

/**
 * This class manages interactions with the server for Physician-related operations.
 */
public class PhysicianManager {

    /**
     * Interface for callbacks related to Physician operations.
     */
    public interface Callbacks {
        /**
         * Callback method to set the retrieved Physician object.
         *
         * @param physician The Physician object to be set.
         */
        void setPhysician(Physician physician);
    }

    private static final String LOG_TAG = PhysicianManager.class.getSimpleName();

    /**
     * Attach a StatusLog to a specific Physician and Patient.
     *
     * @param physician The Physician to attach the StatusLog to.
     * @param patientId The ID of the Patient.
     * @param statusLog The StatusLog to be attached.
     * @return True if the StatusLog was added successfully, false otherwise.
     */
    public static synchronized boolean attachPhysicianStatusLog(Physician physician, String patientId,
                                                                StatusLog statusLog) {
        String s = statusLog.getNote() + " [" + physician.getName() + "] ";
        statusLog.setNote(s);
        boolean added = false;
        for (Patient p : physician.getPatients()) {
            if (p.getId().contentEquals(patientId)) {
                if (p.getStatusLog() == null) {
                    p.setStatusLog(new HashSet<>());
                }
                p.getStatusLog().add(statusLog);
                added = true;
                break;
            }
        }
        return added;
    }

    /**
     * Retrieve a specific Physician using its ID from the server.
     *
     * @param activity The context of the calling activity.
     * @param id       The ID of the Physician to retrieve.
     */
    public static synchronized void getPhysician(final Context activity, final String id) {
        if (id == null) {
            Log.e(LOG_TAG, "Tried to get physician without a valid ID.");
            return;
        }
        Log.d(LOG_TAG, "Getting Physician ID Key is : " + id);
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        if (symptomManagementApi != null) {
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "getting single physician with id : " + id);
                return symptomManagementApi.getPhysician(id);
            }, new TaskCallback<Physician>() {
                @Override
                public void success(Physician result) {
                    Log.d(LOG_TAG, "Found Physician :" + result.toString());
                    ((Callbacks) activity).setPhysician(result);
                }

                @Override
                public void error(Exception e) {
                    Log.d(LOG_TAG,
                            "Unable to fetch Physician to update the status logs. " +
                                    "Please check Internet connection.");
                }
            });
        }
    }

    /**
     * Save a Physician object to the server.
     *
     * @param activity  The context of the calling activity.
     * @param physician The Physician object to be saved.
     */
    public static synchronized void savePhysician(final Context activity, final Physician physician) {
        if (physician == null) {
            Log.e(LOG_TAG, "Tried to update physician with null object.");
            return;
        }
        Log.d(LOG_TAG, "Updating Physician to Cloud. ID Key is : " + physician.getId());
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        if (symptomManagementApi != null) {
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "Saving physician with status notes : " + physician.getId());
                return symptomManagementApi.updatePhysician(physician.getId(), physician);
            }, new TaskCallback<Physician>() {
                @Override
                public void success(Physician result) {
                    Log.d(LOG_TAG, "Updated Physician :" + result.toString());
                    ((Callbacks) activity).setPhysician(result);
                }

                @Override
                public void error(Exception e) {
                    Log.d(LOG_TAG,
                            "Unable to update status logs for the Physician. " +
                                    "Please check Internet connection.");
                }
            });
        }
    }
}
