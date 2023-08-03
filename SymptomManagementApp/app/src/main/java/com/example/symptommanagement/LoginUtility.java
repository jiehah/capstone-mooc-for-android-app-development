package com.example.symptommanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.example.symptommanagement.client.SymptomManagementService;
import com.example.symptommanagement.data.PatientDataManager;
import com.example.symptommanagement.data.UserCredential;
import com.example.symptommanagement.patient.Reminder.ReminderManager;

/**
 * LoginUtility is a utility class that provides methods for managing user login-related data
 * and preferences. It includes methods for setting and retrieving login credentials, user roles,
 * check-in status, and other user-specific data. It also handles saving and retrieving data
 * from SharedPreferences.
 */
public class LoginUtility {

    /**
     * Tag for logging purposes
     */
    private static final String LOG_TAG = LoginUtility.class.getSimpleName();

    /**
     * Static variables for storing user-related data
     */
    static String username;
    static String loginId;
    static UserCredential.UserRole role;
    static UserCredential credential = null;

    /**
     * Sets the user's login credentials and other related data after a successful login attempt.
     * It saves the provided UserCredential in the static variable `credential` and updates
     * the username, loginId, and user role. If the credentials are valid, it starts reminders
     * for a PATIENT user and returns true. Otherwise, it returns false.
     *
     * @param context    The context for accessing shared preferences and other resources.
     * @param credential The UserCredential object containing the user's login information.
     * @return True if the login credentials are valid and set successfully; otherwise, false.
     */
    public static synchronized boolean setLoggedIn(Context context, UserCredential credential) {
        // Checking if the provided UserCredential is valid
        Log.d(LOG_TAG, "Setting Logged In Values for credential: " + credential.toString());
        if (credential == null ||
                credential.getUserName() == null ||
                credential.getUserName().isEmpty() ||
                credential.getUserRole() == null ||
                credential.getUserRole() == UserCredential.UserRole.NOT_ASSIGNED ||
                (credential.getUserRole() != UserCredential.UserRole.ADMIN &&
                        (credential.getUserId() == null || credential.getUserId().isEmpty()))) {
            Log.e(LOG_TAG, "Invalid Credentials, so they are not getting set!!!");
            return false;
        }

        // Saving the provided UserCredential and updating username, loginId, and user role
        LoginUtility.credential = credential;
        username = setUsername(context, credential.getUserName());
        loginId = setLoginId(context, credential.getUserId());
        int roleValue = setUserRoleValue(context, credential.getUserRoleValue());
        Log.d(LOG_TAG, "User Role Value saved is: " + roleValue);
        role = getUserRole(context);
        Log.d(LOG_TAG, "User Role is: " + role);

        // Starting reminders for PATIENT user
        if (role == UserCredential.UserRole.PATIENT) {
            Log.d(LOG_TAG, "Starting Patient Reminders.");
            ReminderManager.startPatientReminders(context, loginId);
        }
        return true;
    }

    /**
     * Checks if the user is logged in by verifying the presence of valid login credentials,
     * a non-empty username, and a non-empty loginId. It also checks if a valid user role
     * (other than NOT_ASSIGNED) is assigned to the user.
     *
     * @param context The context for accessing shared preferences and other resources.
     * @return True if the user is logged in; otherwise, false.
     */
    public static boolean isLoggedIn(Context context) {
        username = getUsername(context);
        loginId = getLoginId(context);
        role = UserCredential.UserRole.findByValue(LoginUtility.getUserRoleValue(context));
        return credential != null &&
                !username.isEmpty() &&
                !loginId.isEmpty() &&
                role != UserCredential.UserRole.NOT_ASSIGNED;
    }

    /**
     * Performs the logout process for the current user. It clears all user-related data and
     * preferences, effectively logging the user out of the application. This method cancels
     * any active patient reminders if the user role is set to PATIENT.
     *
     * @param context The context for accessing shared preferences and other resources.
     */
    public static synchronized void logout(Context context) {
        // Cancel patient reminders if the user role is PATIENT
        if (role == UserCredential.UserRole.PATIENT) {
            Log.d(LOG_TAG, "Cancelling Patient Reminders.");
            ReminderManager.cancelPatientReminders(context);
        }

        // Clear all user-related data and preferences
        username = "";
        loginId = "";
        role = UserCredential.UserRole.NOT_ASSIGNED;
        credential = null;
        setUsername(context, username);
        setLoginId(context, loginId);
        setUserRoleValue(context, role.getValue());

        // Reset the SymptomManagementService to its initial state
        SymptomManagementService.reset();
    }

    /**
     * Saves the user's login credentials to the local storage. This method is used specifically for
     * a PATIENT user role to store their credentials in the content provider (CP).
     *
     * @param context    The context for accessing shared preferences and other resources.
     * @param credential The UserCredential object containing the user's login information.
     */
    public static synchronized void savePatientCredential(Context context, UserCredential credential) {
        if (role == UserCredential.UserRole.PATIENT) {
            Log.d(LOG_TAG, "Saving the PATIENT credentials to CP.");
            PatientDataManager.addCredentialToCP(context, credential);
        } else {
            Log.d(LOG_TAG, "Not saving these credentials because it's not a PATIENT.");
        }
    }

    /**
     * Sets the check-in status for the user in the shared preferences. It saves a boolean value
     * indicating whether the user is checked-in or not.
     *
     * @param context The context for accessing shared preferences and other resources.
     * @param value   The boolean value representing the check-in status.
     * @return The updated check-in status after saving.
     */
    public static synchronized boolean setCheckin(Context context, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isCheckin", value);
        editor.apply();
        setCheckInLogId(context, (value) ? System.currentTimeMillis() : 0L);
        return isCheckin(context);
    }

    /**
     * Retrieves the check-in status of the user from shared preferences.
     *
     * @param context The context for accessing shared preferences and other resources.
     * @return True if the user is checked-in; otherwise, false.
     */
    public static boolean isCheckin(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("isCheckin", false);
    }

    /**
     * Sets the log ID for the user's check-in event in shared preferences. The log ID represents
     * a timestamp (in milliseconds) when the user checked in.
     *
     * @param context The context for accessing shared preferences and other resources.
     * @param value   The long value representing the check-in log ID (timestamp).
     * @return The updated check-in log ID after saving.
     */
    public static synchronized boolean setCheckInLogId(Context context, long value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("checkin_log_id", value);
        editor.apply();
        return isCheckin(context);
    }

    /**
     * Retrieves the log ID for the user's check-in event from shared preferences.
     *
     * @param context The context for accessing shared preferences and other resources.
     * @return The log ID (timestamp) representing the user's check-in event.
     */
    public static long getCheckInLogId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong("checkin_log_id", 0L);
    }

    /**
     * Sets the login ID for the user in shared preferences. The login ID uniquely identifies
     * the user and is used during the login process to maintain a session.
     *
     * @param context The context for accessing shared preferences and other resources.
     * @param value   The String value representing the login ID.
     * @return The updated login ID after saving.
     */
    public static synchronized String setLoginId(Context context, String value) {
        if (value == null) value = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("login_id", value);
        editor.apply();
        return getLoginId(context);
    }

    /**
     * Retrieves the login ID of the user from shared preferences.
     *
     * @param context The context for accessing shared preferences and other resources.
     * @return The login ID of the user.
     */
    public static String getLoginId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("login_id", "");
    }

    /**
     * Sets the username for the user in shared preferences. The username is used to identify
     * the user during the login process.
     *
     * @param context The context for accessing shared preferences and other resources.
     * @param value   The String value representing the username.
     * @return The updated username after saving.
     */
    public static synchronized String setUsername(Context context, String value) {
        if (value == null) value = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", value.toLowerCase());
        editor.apply();
        return getUsername(context);
    }

    /**
     * Retrieves the username of the user from shared preferences.
     *
     * @param context The context for accessing shared preferences and other resources.
     * @return The username of the user.
     */
    public static String getUsername(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("username", "").toLowerCase();
    }

    /**
     * Sets the user role value in shared preferences. The user role value represents the
     * numerical representation of the user's role (e.g., ADMIN, PATIENT, etc.).
     *
     * @param context The context for accessing shared preferences and other resources.
     * @param value   The int value representing the user role.
     * @return The updated user role value after saving.
     */
    public static synchronized int setUserRoleValue(Context context, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("role_value", value);
        editor.apply();
        return getUserRoleValue(context);
    }

    /**
     * Retrieves the user role value from shared preferences.
     *
     * @param context The context for accessing shared preferences and other resources.
     * @return The user role value as an int.
     */
    public static int getUserRoleValue(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt("role_value", UserCredential.UserRole.NOT_ASSIGNED.getValue());
    }

    /**
     * Retrieves the user role of the user from shared preferences.
     *
     * @param context The context for accessing shared preferences and other resources.
     * @return The user role of the user.
     */
    public static UserCredential.UserRole getUserRole(Context context) {
        return UserCredential.UserRole.findByValue(getUserRoleValue(context));
    }

    /**
     * Sets the "Remember Me" option for the user in shared preferences. It saves a boolean value
     * indicating whether the "Remember Me" option is enabled or not.
     *
     * @param context The context for accessing shared preferences and other resources.
     * @param value   The boolean value representing the "Remember Me" option status.
     * @return The updated "Remember Me" option status after saving.
     */
    public static synchronized boolean setRememberMe(Context context, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("remember_me", value);
        editor.apply();
        return getRememberMe(context);
    }

    /**
     * Retrieves the status of the "Remember Me" option from shared preferences.
     *
     * @param context The context for accessing shared preferences and other resources.
     * @return True if the "Remember Me" option is enabled; otherwise, false.
     */
    public static boolean getRememberMe(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("remember_me", false);
    }

    /**
     * Sets the timestamp for the user's last device login in shared preferences. The timestamp
     * is represented as a long value (in milliseconds).
     *
     * @param context The context for accessing shared preferences and other resources.
     * @param value   The long value representing the timestamp of the last device login.
     * @return The updated timestamp of the last device login after saving.
     */
    public static synchronized long setLastDeviceLogin(Context context, long value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("last_device_login", value);
        editor.apply();
        return getLastDeviceLogin(context);
    }

    /**
     * Retrieves the timestamp of the user's last device login from shared preferences.
     *
     * @param context The context for accessing shared preferences and other resources.
     * @return The timestamp of the last device login.
     */
    public static long getLastDeviceLogin(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong("last_device_login", 0L);
    }
}
