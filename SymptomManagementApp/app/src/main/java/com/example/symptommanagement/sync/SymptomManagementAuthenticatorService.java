package com.example.symptommanagement.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service that provides an instance of the SymptomManagementAuthenticator for the Account Manager.
 */
public class SymptomManagementAuthenticatorService extends Service {

    private SymptomManagementAuthenticator mAuthenticator;

    /**
     * Called when the service is created.
     */
    @Override
    public void onCreate() {
        // Create a new instance of the SymptomManagementAuthenticator
        mAuthenticator = new SymptomManagementAuthenticator(this);
    }

    /**
     * Called when a client binds to the service.
     *
     * @param intent The intent used to bind to the service.
     * @return The IBinder interface for the SymptomManagementAuthenticator.
     */
    @Override
    public IBinder onBind(Intent intent) {
        // Return the IBinder interface for the SymptomManagementAuthenticator
        return mAuthenticator.getIBinder();
    }
}
