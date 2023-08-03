package com.example.symptommanagement.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;
import com.example.symptommanagement.LoginActivity;
import com.example.symptommanagement.LoginUtility;
import com.example.symptommanagement.R;
import com.example.symptommanagement.client.CallableTask;
import com.example.symptommanagement.client.SymptomManagementApi;
import com.example.symptommanagement.client.SymptomManagementService;
import com.example.symptommanagement.client.TaskCallback;
import com.example.symptommanagement.data.Alert;
import com.example.symptommanagement.data.Patient;
import com.example.symptommanagement.data.PatientDataManager;
import com.example.symptommanagement.data.UserCredential;

import java.util.Collection;

/**
 * Custom implementation of AbstractThreadedSyncAdapter to handle data synchronization
 * between the local database and the remote server.
 */
public class SymptomManagementSyncAdapter extends AbstractThreadedSyncAdapter {

    private final static String LOG_TAG = SymptomManagementSyncAdapter.class.getSimpleName();
    private static final int SYMPTOM_MANAGEMENT_NOTIFICATION_ID = 1111;
    private final Context context;
    private static final int SYNC_INTERVAL = 60 * 20;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private String patientId;
    private Patient patient;
    private String physicianId;
    private static Collection<Alert> alerts;

    public SymptomManagementSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
    }

    /**
     * Get the sync account used for data synchronization.
     *
     * @param context The application context.
     * @return The sync account, or null if it couldn't be created.
     */
    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create a new sync account using the app name and account type
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // Check if the account already exists
        if (null == accountManager.getPassword(newAccount)) {
            // If the account doesn't exist, add it explicitly
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                // Return null if adding the account failed
                return null;
            }
            // Call the method to perform additional actions after the account is created
            onAccountCreated(newAccount, context);
        }

        // Return the sync account
        return newAccount;
    }

    /**
     * Perform additional actions after the sync account is created.
     *
     * @param newAccount The newly created sync account.
     * @param context    The application context.
     */
    private static void onAccountCreated(Account newAccount, Context context) {
        // Configure periodic sync
        configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        // Enable automatic sync for the sync account
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        // Initiate an immediate sync
        syncImmediately(context);

        // Show a toast message to indicate data retrieval
        Toast.makeText(context, "Retrieving Data...", Toast.LENGTH_LONG).show();
    }

    /**
     * Request an immediate sync with the server.
     *
     * @param context The application context.
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    /**
     * Configure the periodic sync interval and flex time.
     *
     * @param context      The application context.
     * @param syncInterval The time interval in seconds between syncs.
     * @param flexTime     The flex time in seconds to adjust the sync time.
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        SyncRequest request = new SyncRequest.Builder().
                syncPeriodic(syncInterval, flexTime).
                setSyncAdapter(account, authority).build();
        ContentResolver.requestSync(request);
    }

    /**
     * Initialize the SyncAdapter by creating a new Sync Account.
     *
     * @param context The application context.
     */
    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Called when a data synchronization is triggered.
     *
     * @param account               The Sync Account.
     * @param bundle                Bundle containing additional sync information.
     * @param authority             The authority string for the content provider.
     * @param contentProviderClient The content provider client.
     * @param syncResult            The result of the sync operation.
     */
    @Override
    public void onPerformSync(Account account, Bundle bundle, String authority,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting onPerformSync");

        // Check if the user is logged in
        if (LoginUtility.isLoggedIn(getContext())) {
            // Determine the role of the user (patient or physician) and process the appropriate sync
            if (LoginUtility.getUserRole(getContext()) == UserCredential.UserRole.PATIENT) {
                Log.d(LOG_TAG, "SYNC Processing for PATIENT.");
                processPatientSync();
            } else if (LoginUtility.getUserRole(getContext()) == UserCredential.UserRole.PHYSICIAN) {
                Log.d(LOG_TAG, "SYNC Processing for PHYSICIAN.");
                processPhysicianSync();
            }
        } else {
            Log.d(LOG_TAG, "Not Logged In, no SYNC needed.");
        }
    }

    /**
     * Process data synchronization for a patient user.
     */
    private void processPatientSync() {
        // Check if the user is logged in and is a patient
        if (LoginUtility.isLoggedIn(getContext())
                && LoginUtility.getUserRole(getContext()) == UserCredential.UserRole.PATIENT) {
            // Get the patient ID for the logged-in patient user
            patientId = LoginUtility.getLoginId(context);
        } else {
            // If the user is not logged in or not a patient, return without proceeding with sync
            return;
        }

        // Log that the patient sync is being processed
        Log.d(LOG_TAG, "Logged In and Processing Patient sync : " + patientId);

        // Initiate retrieval of patient record from the cloud
        getPatientRecordFromCloud();
    }

    /**
     * Get the patient's record from the cloud server.
     *
     * @return The Patient object retrieved from the server.
     */
    private Patient getPatientRecordFromCloud() {
        // Check if the user is logged in and has the role of a patient
        if (LoginUtility.isLoggedIn(getContext())
                && LoginUtility.getUserRole(getContext()) == UserCredential.UserRole.PATIENT) {
            // Get the patient ID for the logged-in patient user
            patientId = LoginUtility.getLoginId(context);
            physicianId = null;
        } else {
            // If the user is not logged in or is not a patient, return null without proceeding with the sync
            return null;
        }

        // Log the patient ID being used for the cloud request
        Log.d(LOG_TAG, "getPatientRecordFromCloud - Patient ID is : " + patientId);

        // Get the SymptomManagementApi instance to perform the cloud request
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();

        // Check if the SymptomManagementApi instance is available
        if (symptomManagementApi != null) {
            // Use CallableTask to perform the asynchronous request to retrieve patient data from the cloud
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "getting single Patient id : " + patientId);
                Patient result = null;
                try {
                    // Perform the request to get the patient record using the patient ID
                    result = symptomManagementApi.getPatient(patientId);
                } catch (Exception e) {
                    Log.d(LOG_TAG, "Service Failed getting Patient from the cloud. Keep on going.");
                    //e.printStackTrace();
                }
                return result;
            }, new TaskCallback<Patient>() {
                @Override
                public void success(Patient result) {
                    if (result == null) {
                        return;
                    }
                    // Log the retrieved patient data
                    Log.d(LOG_TAG, "Found Patient: " + result);
                    // Store the retrieved patient object in the 'patient' member field
                    patient = result;
                    Log.d(LOG_TAG, "Got a patient, now we can process.");
                    // Proceed with processing the patient data from the cloud
                    processPatientFromCloud(patient);
                }

                @Override
                public void error(Exception e) {
                    Log.e(LOG_TAG, "Sync unable to UPDATE Patient record from the internet." +
                            " No Internet? Try again later because all data is stored locally.");
                }
            });
        } else {
            Log.d(LOG_TAG, "NO SERVICE... is the internet offline?");
        }
        // Return the retrieved patient object (can be null if not logged in or not a patient)
        return patient;
    }

    /**
     * Process the patient's record retrieved from the cloud.
     *
     * @param patient The Patient object retrieved from the cloud server.
     */
    private void processPatientFromCloud(Patient patient) {
        // Process and store the retrieved patient data into the local ContentProvider
        PatientDataManager.processPatientToCP(context, patient);

        // Process and update the patient object with data from the local ContentProvider
        PatientDataManager.processCPtoPatient(context, patient);

        // Send the patient record back to the cloud server
        sendPatientRecordToCloud(patient);
    }

    /**
     * Send the patient's record to the cloud server.
     *
     * @param patientRecord The Patient object to be sent to the cloud server.
     */
    private void sendPatientRecordToCloud(final Patient patientRecord) {
        // Get the patient ID of the logged-in user
        patientId = LoginUtility.getLoginId(context);

        // Get the SymptomManagementApi instance for making API calls to the cloud server
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        if (symptomManagementApi != null) {
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "Updating single Patient id : " + patientId);
                Log.v(LOG_TAG, "Last Login SET to before Sent to Cloud: " + patientRecord.getLastLogin());

                // Make an API call to update the patient's record on the cloud server
                return symptomManagementApi.updatePatient(patientId, patientRecord);
            }, new TaskCallback<Patient>() {

                @Override
                public void success(Patient result) {
                    // The API call was successful, and the updated patient data is returned
                    Log.d(LOG_TAG, "Returned Patient from Server: " + result.toString());

                    // Update the local patient object with the latest data returned from the server
                    patient = result;
                }

                @Override
                public void error(Exception e) {
                    // There was an error making the API call, either due to no internet connectivity
                    // or other server-side issues
                    Log.e(LOG_TAG, "Sync unable to UPDATE Patient record to the Internet." +
                            " Maybe no internet? Try again later. All data stored locally, so it's OK.");
                }
            });
        } else {
            // The SymptomManagementApi instance is null, indicating no internet connectivity or
            // other issues with the API service
            Log.d(LOG_TAG, "NO SERVICE available? Is the internet gone?");
        }
    }

    /**
     * Process data synchronization for a physician user.
     */
    private void processPhysicianSync() {
        // Check if the user is logged in and has the role of a physician
        if (LoginUtility.isLoggedIn(getContext()) &&
                LoginUtility.getUserRole(getContext()) == UserCredential.UserRole.PHYSICIAN) {
            // Retrieve the physician ID of the logged-in physician user
            physicianId = LoginUtility.getLoginId(context);
            // Set patientId to null since this is physician sync
            patientId = null;
        } else {
            // User is not logged in as a physician, so return and do nothing
            return;
        }

        Log.d(LOG_TAG, "Logged In and Processing Physician sync: " + physicianId);
        // Trigger the retrieval of physician alerts from the cloud server
        getPhysicianAlerts();
    }

    /**
     * Get the alerts for the physician from the cloud server.
     */
    private void getPhysicianAlerts() {
        // Check if the user is logged in and has the role of a physician
        if (LoginUtility.isLoggedIn(getContext()) &&
                LoginUtility.getUserRole(getContext()) == UserCredential.UserRole.PHYSICIAN) {
            // Retrieve the physician ID of the logged-in physician user
            physicianId = LoginUtility.getLoginId(context);
            // Set patientId to null since this is physician sync
            patientId = null;
        } else {
            // User is not logged in as a physician, so return and do nothing
            return;
        }

        Log.d(LOG_TAG, "Get Alerts for Physician: " + physicianId);
        // Retrieve the SymptomManagementApi instance
        final SymptomManagementApi symptomManagementApi = SymptomManagementService.getService();
        if (symptomManagementApi != null) {
            // Invoke a CallableTask to asynchronously retrieve the physician's alerts from the cloud server
            CallableTask.invoke(() -> {
                Log.d(LOG_TAG, "Getting patient alerts for physician: " + physicianId);
                return symptomManagementApi.getPatientAlerts(physicianId);
            }, new TaskCallback<Collection<Alert>>() {
                @Override
                public void success(Collection<Alert> result) {
                    // Handle the successful retrieval of alerts from the cloud
                    if (result != null) {
                        Log.d(LOG_TAG, "Found Alerts: " + result.size());
                    }
                    // Store the retrieved alerts in the 'alerts' variable
                    alerts = result;
                    // Create a notification for the physician based on the retrieved alerts
                    createPhysicianNotification(alerts);
                }

                @Override
                public void error(Exception e) {
                    // Handle any errors that occurred during the retrieval of alerts
                    Log.e(LOG_TAG, "Sync unable to get physician alerts from the internet." +
                            " Internet may not be available. Check your internet connection.");
                }
            });
        } else {
            // SymptomManagementApi instance is not available, likely due to a lack of internet connection
            Log.d(LOG_TAG, "No SERVICE available? Is the internet gone?");
        }
    }

    /**
     * Create a notification for the physician based on the patient alerts.
     *
     * @param alerts Collection of patient alerts.
     */
    private void createPhysicianNotification(Collection<Alert> alerts) {
        if (alerts == null || alerts.size() == 0) {
            return;
        }

        String contentText;
        if (alerts.size() == 1) {
            // If there is only one alert
            Alert a = alerts.iterator().next();
            if (a != null && a.getPhysicianContacted() <= 0L) {
                // Check if the physician has not turned off the alert
                contentText = a.getFormattedMessage();
            } else {
                // Physician has turned off the alert, so return without creating a notification
                Log.d(LOG_TAG, "Found an alert but the physician turned it off.");
                return;
            }
        } else {
            // If there are multiple alerts
            int count = 0;
            int turnedOff = 0;
            for (Alert a : alerts) {
                if (a.getPhysicianContacted() <= 0L) {
                    // Count the number of severe patients requiring attention (alerts not turned off)
                    count++;
                } else {
                    // Count the number of patients whose alerts have been turned off
                    turnedOff++;
                }
            }
            if (count == 0) {
                // All alerts are turned off, so return without creating a notification
                Log.d(LOG_TAG, "All the alerts are turned off.");
                return;
            }
            contentText = count + " severe patients require attention. ";
            if (turnedOff > 0) {
                // Include the number of patients whose alerts have been turned off in the notification
                contentText += turnedOff + " patients contacted.";
            }
        }

        Log.d(LOG_TAG, "SENDING ALERT message: " + contentText);
        // Create the notification with the specified content text
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Symptom Management")
                        .setContentText(contentText)
                        .setOnlyAlertOnce(true)
                        .setAutoCancel(true);

        // Set the content intent to open the LoginActivity when the notification is clicked
        builder.setContentIntent(
                TaskStackBuilder.create(getContext())
                        .addParentStack(LoginActivity.class)
                        .addNextIntent(new Intent(getContext(), LoginActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));

        // Show the notification using the NotificationManager
        ((NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(SYMPTOM_MANAGEMENT_NOTIFICATION_ID, builder.build());
    }

    /**
     * Find the severity level for the patient's alert.
     *
     * @param patient The Patient object.
     * @return The severity level for the patient's alert.
     */
    public static synchronized int findPatientAlertSeverityLevel(Patient patient) {
        Log.d(LOG_TAG, "Checking Patient for Severity Level: " + patient.getId());
        if (alerts == null || alerts.size() == 0) {
            return Alert.PAIN_SEVERITY_LEVEL_0;
        }
        for (Alert a : alerts) {
            Log.d(LOG_TAG, "Checking Alert for Patient ID: " + a.getPatientId());
            if (a.getPatientId().contentEquals(patient.getId())) {
                Log.d(LOG_TAG, "MATCHES PATIENT .. High Severity Level found: " + a.getSeverityLevel());
                patient.setSeverityLevel(a.getSeverityLevel());
                return a.getSeverityLevel();
            }
        }
        return Alert.PAIN_SEVERITY_LEVEL_0;
    }
}
