package com.example.symptommanagement.physician;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import com.example.symptommanagement.client.CallableTask;
import com.example.symptommanagement.client.SymptomManagementApi;
import com.example.symptommanagement.client.SymptomManagementService;
import com.example.symptommanagement.client.TaskCallback;
import com.example.symptommanagement.data.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * The PatientManager class provides static methods to interact with patient data and perform operations
 * related to patients, such as fetching patient information from the cloud, updating patient records,
 * and searching for patients by name. It also includes a method to create a sorted list of history logs
 * for a specific patient.
 */
public class PatientManager {

    private static final String LOG_TAG = PatientManager.class.getSimpleName();

    /**
     * The Callbacks interface defines callback methods that should be implemented by the caller of
     * PatientManager methods to receive data or notifications related to patient operations.
     */
    public interface Callbacks {
        void setPatient(Patient patient);

        void failedSearch(String message);

        void successfulSearch(Patient patient);
    }

    /**
     * Retrieves a patient's information from the cloud using the patient ID.
     *
     * @param activity  The activity or fragment calling this method.
     * @param patientId The ID of the patient to retrieve from the cloud.
     */
    public static synchronized void getPatient(final Activity activity, final String patientId) {
        // Check for invalid patientId
        if (patientId == null) {
            Log.e(LOG_TAG, "NO PATIENT identified.. unable to get data from cloud.");
            return;
        }
        Log.d(LOG_TAG, "getting Patient ID : " + patientId);
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        if (SymptomManagementService.getService() != null) {
            CallableTask.invoke(() -> symptomManagementApi.getPatient(patientId), new TaskCallback<Patient>() {
                @Override
                public void success(Patient result) {
                    Log.d(LOG_TAG, "Found Patient :" + result.toString());
                    ((Callbacks) activity).setPatient(result);
                }

                @Override
                public void error(Exception e) {
                    Toast.makeText(activity,
                            "Unable to fetch the Patient data. " +
                                    "Please check Internet connection and try again.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Updates a patient's information on the cloud.
     *
     * @param activity      The activity or fragment calling this method.
     * @param patientRecord The updated patient object containing the changes to be saved.
     */
    public static synchronized void updatePatient(final Activity activity, final Patient patientRecord) {
        // Check for invalid patientRecord
        if (patientRecord == null || patientRecord.getId() == null ||
                patientRecord.getId().isEmpty()) {
            Log.e(LOG_TAG, "Trying to update a patient that is null or has no server id.");
            return;
        }
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        if (symptomManagementApi != null) {
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "Updating single Patient id : " + patientRecord.getId());
                return symptomManagementApi.updatePatient(patientRecord.getId(), patientRecord);
            }, new TaskCallback<Patient>() {
                @Override
                public void success(Patient result) {
                    Log.d(LOG_TAG, "Returned Updated Patient from Server:" + result.toDebugString());
                    ((Callbacks) activity).setPatient(result);
                }

                @Override
                public void error(Exception e) {
                    Log.e(LOG_TAG, "Unable to UPDATE Patient record to Internet." +
                            "Patient changes did not save. Try again later");
                }
            });
        }
    }

    /**
     * Searches for patients by their full name on the cloud and returns the matched patient (if found).
     *
     * @param activity The activity or fragment calling this method.
     * @param fullName The full name of the patient to search for.
     */
    public static synchronized void findPatientByName(final Activity activity, final String fullName) {
        // Check for invalid parameters
        if (activity == null || fullName == null) {
            Log.e(LOG_TAG, "Invalid parameters for the findByPatientName search.");
            return;
        }
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        if (symptomManagementApi != null) {
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "Searching for full name on the server : " + fullName);
                return symptomManagementApi.findByPatientName(fullName);
            }, new TaskCallback<Collection<Patient>>() {
                @Override
                public void success(Collection<Patient> result) {
                    // Check for first name match
                    Patient patient = null;
                    if (result == null || result.size() == 0) {
                        ((Callbacks) activity).failedSearch("No patients match that name.");
                        return;
                    }
                    for (Patient p : result) {
                        Log.d(LOG_TAG, "Found at least one match using the first one");
                        patient = p;
                    }
                    ((Callbacks) activity).successfulSearch(patient);
                }

                @Override
                public void error(Exception e) {
                    ((Callbacks) activity).failedSearch("No patients with that name.");
                }
            });
        }
    }

    /**
     * Creates a sorted list of history logs for a specific patient. The history logs include
     * check-in logs, pain logs, medication logs, and status logs.
     *
     * @param mPatient The patient for whom to generate the history log list.
     * @return An array of HistoryLog objects sorted in descending order of created timestamp.
     */
    public static synchronized HistoryLog[] createLogList(Patient mPatient) {
        // Use a TreeSet with a custom comparator to sort the logs based on the created timestamp
        HistoryLogSorter sorter = new HistoryLogSorter();
        TreeSet<HistoryLog> sortedLogs = new TreeSet<>(Collections.reverseOrder(sorter));

        // Collect and create history logs for check-in logs
        Collection<CheckInLog> checkinLogs = mPatient.getCheckinLog();
        if (checkinLogs != null) {
            for (CheckInLog p : checkinLogs) {
                HistoryLog h = new HistoryLog();
                h.setCreated(p.getCreated());
                h.setType(HistoryLog.LogType.CHECK_IN_LOG);
                String info = "Checked In with Reminder.";
                h.setInfo(info);
                sortedLogs.add(h);
            }
        }

        // Collect and create history logs for pain logs
        Collection<PainLog> painLogs = mPatient.getPainLog();
        if (painLogs != null) {
            for (PainLog p : painLogs) {
                HistoryLog h = new HistoryLog();
                h.setCreated(p.getCreated());
                HistoryLog.LogType hType =
                        (p.getCheckinId() > 0L ? HistoryLog.LogType.CHECK_IN_PAIN_LOG : HistoryLog.LogType.PAIN_LOG);
                h.setType(hType);
                String severity = (p.getSeverity() == PainLog.Severity.SEVERE) ? "SEVERE"
                        : (p.getSeverity() == PainLog.Severity.MODERATE) ? "Moderate" : "Well-Controlled";
                String eating = (p.getEating() == PainLog.Eating.NOT_EATING) ? "NOT EATING"
                        : (p.getEating() == PainLog.Eating.SOME_EATING) ? "Some Eating" : "Eating";
                String info = "Pain : " + severity + " -- " + eating;
                h.setInfo(info);
                sortedLogs.add(h);
            }
        }

        // Collect and create history logs for medication logs
        Collection<MedicationLog> medLogs = mPatient.getMedLog();
        if (medLogs != null) {
            for (MedicationLog m : medLogs) {
                HistoryLog h = new HistoryLog();
                h.setCreated(m.getCreated());
                HistoryLog.LogType hType =
                        (m.getCheckinId() > 0L ? HistoryLog.LogType.CHECK_IN_MED_LOG : HistoryLog.LogType.MED_LOG);
                h.setType(hType);
                String name = m.getMed().getName();
                String taken = m.getTakenDateFormattedString(" hh:mm a 'on' E, MMM d yyyy");
                String info = name + " taken " + taken;
                h.setInfo(info);
                sortedLogs.add(h);
            }
        }

        // Collect and create history logs for status logs
        Collection<StatusLog> statusLogs = mPatient.getStatusLog();
        if (statusLogs != null) {
            for (StatusLog s : statusLogs) {
                HistoryLog h = new HistoryLog();
                h.setCreated(s.getCreated());
                h.setType(HistoryLog.LogType.STATUS_LOG);
                String image = (s.getImage_location() != null && !s.getImage_location().isEmpty())
                        ? " - Image Taken By Patient" : "";
                String info = "Note: " + s.getNote() + " " + image;
                h.setInfo(info);
                sortedLogs.add(h);
            }
        }

        // Convert the sorted logs to an array and return
        if (sortedLogs.size() == 0) {
            return new HistoryLog[0];
        }
        return sortedLogs.toArray(new HistoryLog[0]);
    }

    /**
     * The HistoryLogSorter class is a comparator used to sort HistoryLog objects based on their created timestamp.
     */
    public static class HistoryLogSorter implements Comparator<HistoryLog> {
        public int compare(HistoryLog x, HistoryLog y) {
            return Long.compare(x.getCreated(), y.getCreated());
        }
    }
}
