package com.example.symptommanagement.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service responsible for managing data synchronization using the SymptomManagementSyncAdapter.
 */
public class SymptomManagementSyncService extends Service {
    /**
     * A synchronization lock object to ensure thread safety during syncAdapter initialization.
     */
    private static final Object syncAdapterLock = new Object();

    /**
     * The instance of the SymptomManagementSyncAdapter that handles data synchronization.
     */
    private static SymptomManagementSyncAdapter syncAdapter = null;

    /**
     * Called when the service is first created.
     * Initializes the SymptomManagementSyncAdapter if it's not already initialized.
     */
    @Override
    public void onCreate() {
        synchronized (syncAdapterLock) {
            if (syncAdapter == null) {
                // Create a new instance of SymptomManagementSyncAdapter with the application context.
                syncAdapter = new SymptomManagementSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    /**
     * Called when a component (such as an activity) binds to the service.
     * Returns the IBinder interface of the SymptomManagementSyncAdapter for communication.
     *
     * @param intent The intent that was used to bind to this service.
     * @return The IBinder interface of the SymptomManagementSyncAdapter.
     */
    @Override
    public IBinder onBind(Intent intent) {
        // Return the IBinder interface of the SymptomManagementSyncAdapter.
        return syncAdapter.getSyncAdapterBinder();
    }
}
