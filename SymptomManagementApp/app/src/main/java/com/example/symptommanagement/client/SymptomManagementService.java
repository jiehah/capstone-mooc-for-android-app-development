package com.example.symptommanagement.client;

import android.content.Context;
import android.util.Log;
import com.example.symptommanagement.LoginActivity;
import com.example.symptommanagement.client.oauth.SecuredRestBuilder;
import com.example.symptommanagement.client.oauth.unsafe.EasyHttpClient;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;

/**
 * SymptomManagementService is a utility class that provides access to the SymptomManagementApi service
 * for communicating with the server. It handles the initialization of the service, login, and provides
 * methods to get an instance of the service.
 */
public class SymptomManagementService {
    private static final String LOG_TAG = SymptomManagementService.class.getSimpleName();
    private static SymptomManagementApi symptomManagementApi;
    public static final String CLIENT_ID = "mobile";
    public final static String SERVER_ADDRESS = "https://10.0.2.2:8443";
    private static String user = "";
    private static String password = "";

    /**
     * Retrieves the SymptomManagementApi service instance or shows the login screen if not available.
     * This method is used when the service is already initialized.
     *
     * @param context The application context.
     * @return The SymptomManagementApi service instance if available, otherwise null.
     */
    public static synchronized SymptomManagementApi getServiceOrShowLogin(Context context) {
        if (symptomManagementApi != null) {
            Log.d(LOG_TAG, "We do have a service... no need to Login. Yeah!");
            return symptomManagementApi;
        } else {
            Log.d(LOG_TAG, "We do not have a service so we need to LOGIN!");
            LoginActivity.restartLoginActivity(context);
            return null;
        }
    }

    /**
     * Initializes the SymptomManagementApi service with the provided username and password.
     *
     * @param username The username to be used for login.
     * @param password The password to be used for login.
     * @return The initialized SymptomManagementApi service instance if successful, otherwise null.
     */
    public static synchronized SymptomManagementApi getService(String username, String password) {
        if (username == null || username.isEmpty() || password == null) {
            Log.e(LOG_TAG, "INVALID username or password. Unable to login.");
            symptomManagementApi = null;
            return null;
        }
        user = username;
        SymptomManagementService.password = password;
        Log.d(LOG_TAG, "Attempting to INIT the service with a new username and password.");
        return init(SERVER_ADDRESS, username, password);
    }

    /**
     * Initializes the SymptomManagementApi service with the provided server address.
     * This method is used when the service is already initialized.
     *
     * @param server The server address to be used for initializing the service.
     * @return The initialized SymptomManagementApi service instance if successful, otherwise null.
     */
    public static synchronized SymptomManagementApi getService(String server) {
        if (symptomManagementApi != null) {
            Log.d(LOG_TAG, "GETTING the service.");
            return symptomManagementApi;
        } else {
            Log.d(LOG_TAG, "Attempting to INIT the service.");
            return init(server, user, password);
        }
    }

    /**
     * Retrieves the SymptomManagementApi service instance.
     * This method is used when the service is already initialized.
     *
     * @return The SymptomManagementApi service instance if available, otherwise null.
     */
    public static synchronized SymptomManagementApi getService() {

        if (symptomManagementApi != null) {
            Log.d(LOG_TAG, "GETTING the service.");
            return symptomManagementApi;
        } else {
            Log.d(LOG_TAG, "Attempting to INIT the service with previously set username and password.");
            return init(SERVER_ADDRESS, user, password);
        }
    }

    /**
     * Initializes the SymptomManagementApi service with the provided server address, username, and password.
     * This method performs the actual login at the server using OAuth.
     *
     * @param server The server address to be used for initializing the service.
     * @param user   The username to be used for login.
     * @param pass   The password to be used for login.
     * @return The initialized SymptomManagementApi service instance if successful, otherwise null.
     */
    public static synchronized SymptomManagementApi init(String server, String user, String pass) {
        Log.d(LOG_TAG, "Getting service Server : " + server + " mUser : " + user + " mPassword : " + pass);
        symptomManagementApi = new SecuredRestBuilder()
                .setLoginEndpoint(server + SymptomManagementApi.TOKEN_PATH)
                .setUsername(user)
                .setPassword(pass)
                .setClientId(CLIENT_ID)
                .setClient(new ApacheClient(new EasyHttpClient()))
                .setEndpoint(server).setLogLevel(RestAdapter.LogLevel.FULL).build()
                .create(SymptomManagementApi.class);

        if (symptomManagementApi == null) {
            Log.d(LOG_TAG, "Server login FAILED!!!");
        }
        return symptomManagementApi;
    }

    /**
     * Resets the SymptomManagementApi service and clears the stored username and password.
     */
    public static synchronized void reset() {
        Log.d(LOG_TAG, "RESETTING the service and username and password.");
        password = "";
        user = "";
        symptomManagementApi = null;
    }
}
