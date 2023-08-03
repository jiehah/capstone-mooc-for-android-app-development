package com.example.symptommanagement;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;
import com.example.symptommanagement.admin.AdminMain;
import com.example.symptommanagement.client.CallableTask;
import com.example.symptommanagement.client.SymptomManagementApi;
import com.example.symptommanagement.client.SymptomManagementService;
import com.example.symptommanagement.client.TaskCallback;
import com.example.symptommanagement.data.PatientDataManager;
import com.example.symptommanagement.data.UserCredential;
import com.example.symptommanagement.R;
import com.example.symptommanagement.databinding.ActivityLoginMainBinding;
import com.example.symptommanagement.patient.PatientMainActivity;
import com.example.symptommanagement.physician.PhysicianListPatientsActivity;
import com.example.symptommanagement.sync.SymptomManagementSyncAdapter;

import java.util.Collection;

/**
 * LoginActivity handles the user login process. It allows users to enter their username and
 * password and attempts to authenticate them by executing a background login task (UserLoginTask).
 * If the user is already logged in, it redirects to the appropriate activity based on their role.
 * If the login is successful, the user is redirected to the corresponding home screen.
 * If the login fails, appropriate error messages are displayed to the user.
 */
public class LoginActivity extends Activity {

    // Tag used for logging purposes
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    // Key for storing and accessing the Physician ID in Intent extras
    private static String PHYSICIAN_ID_KEY;

    // AsyncTask used for handling the user login process
    private UserLoginTask authTask = null;

    // View binding for the login activity
    private ActivityLoginMainBinding binding;

    /**
     * Called when the activity is created. Initializes the layout, view binding, and listeners.
     * Also retrieves necessary resources and sets up action listeners for the input fields.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using ViewBinding
        binding = ActivityLoginMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the PHYSICIAN_ID_KEY with the value from resources
        PHYSICIAN_ID_KEY = getString(R.string.physician_id_key);

        // Set up an action listener for the password input field
        binding.password.setOnEditorActionListener((textView, id, keyEvent) -> {
            // When the user clicks the login action or the Enter key on the keyboard
            if (id == R.id.goto_login || id == EditorInfo.IME_NULL) {
                // Attempt the login process
                attemptLogin();
                return true;
            }
            return false;
        });

        // Set up a click listener for the sign-in button
        binding.usernameSignInButton.setOnClickListener(v -> attemptLogin());
    }

    /**
     * Called when the activity is starting. Checks if the user is already logged in. If logged in,
     * it redirects to the appropriate activity based on the user's role. If not logged in, it
     * performs a logout to ensure a clean login state.
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Check if the user is already logged in
        if (LoginUtility.isLoggedIn(this)) {
            // If logged in, redirect to the appropriate activity based on user role
            Log.d(LOG_TAG, "We are already logged in so we just need to redirect.");
            processLoginRedirect(LoginUtility.getUserRole(this));
        } else {
            // If not logged in, perform a logout to ensure a clean login state
            LoginUtility.logout(this);
        }
    }

    /**
     * Restarts the login activity by logging out the current user and launching a new instance
     * of the LoginActivity with clearing the back stack.
     *
     * @param context The context from which this method is called.
     */
    public static void restartLoginActivity(Context context) {
        // Log out the current user to ensure a clean login state
        LoginUtility.logout(context);

        // Start the login activity with a new task and clear the back stack
        context.startActivity(new Intent(context, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    /**
     * Attempts to log in the user with the provided username and password. If the login task
     * is already running, it does nothing. If the input is valid, it initiates the UserLoginTask.
     * Otherwise, it displays appropriate error messages to the user.
     */
    public void attemptLogin() {
        // Log the attempt and the state of the authTask
        Log.d(LOG_TAG, "Attempting to Login  mAuthTask is " + ((authTask != null) ? "Not NULL" : "NULL"));

        // If there is an ongoing login task, return and do nothing
        if (authTask != null) {
            return;
        }

        // Clear any previous errors from the input fields
        binding.username.setError(null);
        binding.password.setError(null);

        // Get the user's input for username and password
        String username = binding.username.getText().toString().toLowerCase();
        String password = binding.password.getText().toString();
        Log.d(LOG_TAG, "Username: " + username + " Password: NOT PRINTED");

        // Initialize variables for input validation
        boolean cancel = false;
        View focusView = null;

        // Validate the password
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            binding.password.setError(getString(R.string.error_invalid_password));
            focusView = binding.password;
            cancel = true;
        }

        // Validate the username
        if (TextUtils.isEmpty(username)) {
            binding.username.setError(getString(R.string.error_field_required));
            focusView = binding.username;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            binding.username.setError("This username is invalid.");
            focusView = binding.username;
            cancel = true;
        }

        // If there are errors, focus on the first problematic input field
        if (cancel) {
            focusView.requestFocus();
        } else {
            // If input is valid, show progress (e.g., loading spinner) and initiate login task
            showProgress(true);
            authTask = new UserLoginTask(username, password);
            authTask.execute((Void) null);
        }
    }

    /**
     * Validates the username. The username must not be null or empty.
     *
     * @param username The username to be validated.
     * @return True if the username is valid, otherwise false.
     */
    private boolean isUsernameValid(String username) {
        return (username != null && !username.isEmpty());
    }

    /**
     * Validates the password. The password must not be null.
     *
     * @param password The password to be validated.
     * @return True if the password is valid, otherwise false.
     */
    private boolean isPasswordValid(String password) {
        return (password != null);
    }

    /**
     * Shows or hides the progress UI. This method is used to display a loading spinner during
     * the login process to indicate that the login is in progress.
     *
     * @param show If true, the progress UI will be shown; otherwise, it will be hidden.
     */
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        // Fade out or fade in the login progress view based on the 'show' parameter
        binding.loginProgress.setVisibility(show ? View.GONE : View.VISIBLE);
        binding.loginProgress.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        binding.loginProgress.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

        // Fade out or fade in the login form view based on the 'show' parameter
        binding.loginForm.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.loginForm.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        binding.loginForm.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }

    /**
     * UserLoginTask is an AsyncTask that handles the user login process in a background thread.
     * It attempts to connect to the server using the provided username and password and checks
     * if the connection is successful. If the connection is successful, it retrieves the user's
     * credentials from the server and redirects the user to the appropriate activity. If the
     * connection fails, it checks for locally stored credentials on the device and attempts
     * to log in using those credentials. If local credentials are not found or the login still
     * fails, it displays an error message to the user and restarts the login activity.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        /**
         * Username and password for the login attempt
         */
        private final String username;
        private final String password;

        /**
         * Constructor for the UserLoginTask. Initializes the task with the provided username
         * and password. It also logs the creation of the thread.
         *
         * @param username The username for the login attempt.
         * @param password The password for the login attempt.
         */
        UserLoginTask(String username, String password) {
            Log.d(LOG_TAG, "creating thread with username "
                    + username + " and password NOT PRINTED");
            this.username = username.toLowerCase();
            this.password = password;
        }

        /**
         * The background task of the UserLoginTask. It attempts to log in to the server using
         * the provided username and password. It connects to the SymptomManagementApi and checks
         * if the service is successfully connected.
         *
         * @param params Not used in this implementation.
         * @return True if the connection to the server is successful; otherwise, false.
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(LOG_TAG, "attempting an actual login to the server with username "
                    + username + " password NOT PRINTED");
            SymptomManagementApi svc = SymptomManagementService.getService(username, password);
            Log.d(LOG_TAG, "Service is: " +
                    (svc == null ? "NOT connected!" : "Successfully connected."));
            return (svc != null);
        }

        /**
         * This method is called when the background login task is completed. It updates the
         * UI based on the success of the login attempt. If the login is successful, it stores
         * the user's username, retrieves their credentials from the server, and redirects
         * the user to the appropriate activity. If the login fails, it checks for locally
         * stored credentials and attempts to log in using them. If local credentials are not
         * found or the login still fails, it displays an error message and restarts the login activity.
         *
         * @param success True if the server connection was successful; otherwise, false.
         */
        @Override
        protected void onPostExecute(final Boolean success) {
            authTask = null;
            showProgress(false);

            if (success) {
                Log.d(LOG_TAG, "Server connection was SUCCESSFUL! But we still need the credentials.");
                LoginUtility.setUsername(getApplicationContext(), username);
                getCredentialsAndRedirect(username, password);
            } else {
                Log.d(LOG_TAG, "Server connection FAILED!");
                Log.d(LOG_TAG, "Checking for local storage of the credentials.");
                UserCredential credential = checkCredentialsOnDevice(username, password);
                if (credential != null) {
                    Log.d(LOG_TAG, "Found credential in CP: " + credential);
                    LoginUtility.setUsername(getApplicationContext(), username);
                    LoginUtility.setLoggedIn(getApplicationContext(), credential);
                } else {
                    // Display an error message to the user and restart the login activity
                    binding.password.setError(getString(R.string.error_incorrect_password));
                    binding.password.requestFocus();
                    restartLoginActivity(getApplicationContext());
                }
            }
        }

        /**
         * This method is called when the background login task is canceled. It updates the UI
         * by hiding the progress view and sets the authTask to null.
         */
        @Override
        protected void onCancelled() {
            authTask = null;
            showProgress(false);
        }
    }

    private UserCredential checkCredentialsOnDevice(String username, String password) {
        Log.d(LOG_TAG, "Looking for username " + username + " password NOT PRINTED");
        return PatientDataManager.getUserCredentials(getApplicationContext(), username, password);
    }

    public void getCredentialsAndRedirect(String username, String password) {
        if (LoginUtility.isLoggedIn(this)) {
            processLoginRedirect(LoginUtility.getUserRole(this));
        } else {
            Log.d(LOG_TAG, "We have to retrieve the user credential FROM THE SERVICE first.");
            getUserCredentialsAndProcessLogin(username, password);
        }
    }

    /**
     * Redirects the user to the appropriate screen flow based on their assigned role after successful login.
     * If the user's role is ADMIN, it starts the AdminMain activity. If the role is PATIENT, it starts
     * the PatientMainActivity activity and immediately initiates a data sync. If the role is PHYSICIAN,
     * it starts the PhysicianListPatientsActivity activity with the physician's ID passed as an extra
     * in the Intent, and also initiates a data sync. If the role is not recognized or invalid, it displays
     * an error message, restarts the login activity, and redirects the user to login again.
     *
     * @param role The user's assigned role (ADMIN, PATIENT, or PHYSICIAN).
     */
    private void processLoginRedirect(UserCredential.UserRole role) {
        Log.d(LOG_TAG, "Process Login REDIRECTing to appropriate screen flow for " + role.toString());
        if (role == UserCredential.UserRole.ADMIN) {
            Log.d(LOG_TAG, "Starting Admin screen flow");
            LoginUtility.setCheckin(getApplicationContext(), false);
            startActivity(new Intent(this, AdminMain.class));
        } else if (role == UserCredential.UserRole.PATIENT) {
            SymptomManagementSyncAdapter.syncImmediately(this);
            startActivity(new Intent(this, PatientMainActivity.class));
        } else if (role == UserCredential.UserRole.PHYSICIAN) {
            Log.d(LOG_TAG, "Starting Doctor screen flow");
            LoginUtility.setCheckin(getApplicationContext(), false);
            // pass the physician's id to the next activity
            String id = LoginUtility.getLoginId(this);
            Bundle arguments = new Bundle();
            arguments.putString(PHYSICIAN_ID_KEY, id);
            Intent physicianIntent = new Intent(this, PhysicianListPatientsActivity.class);
            physicianIntent.putExtras(arguments);
            startActivity(physicianIntent);
            SymptomManagementSyncAdapter.syncImmediately(this);
        } else {
            Log.d(LOG_TAG, "INVALID ROLE ASSIGNED!!!");
            Toast.makeText(
                    this,
                    "Invalid Login. Please See Your Administrator for assistance.",
                    Toast.LENGTH_LONG).show();
            restartLoginActivity(this);
        }
    }

    /**
     * Attempts to get the user's credentials from the server. If the server connection is successful,
     * it retrieves the user's credentials and processes the login redirect. If the server connection fails,
     * it checks for locally stored credentials on the device. If local credentials are found, it processes
     * the login redirect. If no credentials are found or the login still fails, it displays an error message,
     * restarts the login activity, and redirects the user to login again.
     *
     * @param username The user's username for the login attempt.
     * @param password The user's password for the login attempt.
     */
    private void getUserCredentialsAndProcessLogin(final String username, final String password) {
        Log.d(LOG_TAG, "Attempting to get the credentials to be fully logged in.");
        final SymptomManagementApi svc = SymptomManagementService.getService();
        if (svc != null) {
            CallableTask.invoke(() -> {
                String foundUsername = LoginUtility.getUsername(getApplicationContext());
                Log.d(LOG_TAG, "getting user credential for username: " + foundUsername);
                if (foundUsername == null || foundUsername.isEmpty()) {
                    return null;
                }
                return svc.findByUserName(foundUsername);

            }, new TaskCallback<Collection<UserCredential>>() {
                /**
                 * This method is called when the CallableTask successfully retrieves user credentials
                 * from the server. It processes the received credentials, sets the user as logged in,
                 * saves patient credentials (if applicable), and redirects the user to the appropriate
                 * activity based on their assigned role. If there is an issue saving the credentials,
                 * it displays an error message and restarts the login activity.
                 *
                 * @param result The collection of user credentials retrieved from the server.
                 */
                @Override
                public void success(Collection<UserCredential> result) {
                    Log.d(LOG_TAG, "GOT user credentials");
                    if (result != null && result.size() == 1) {
                        UserCredential cred = result.iterator().next();
                        cred.setUserRole(UserCredential.UserRole.findByValue(cred.getUserRoleValue()));
                        Log.d(LOG_TAG, "Credential Received is : " + cred);
                        if (LoginUtility.setLoggedIn(getApplicationContext(), cred)) {
                            LoginUtility.savePatientCredential(getApplicationContext(), cred);
                            processLoginRedirect(LoginUtility.getUserRole(getApplicationContext()));
                        } else {
                            Log.d(LOG_TAG, "ERROR saving the user credentials.");
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Invalid Login. Please See Your Administrator.",
                                    Toast.LENGTH_LONG).show();
                            restartLoginActivity(getApplicationContext());
                        }
                    } else {
                        Log.d(LOG_TAG, "ERROR getting user credentials.");
                        Toast.makeText(
                                getApplicationContext(),
                                "Invalid Login. Please Try Again.",
                                Toast.LENGTH_LONG).show();
                        restartLoginActivity(getApplicationContext());
                    }
                }

                /**
                 * This method is called when there is an error while retrieving user credentials from the server.
                 * If the server connection fails, it attempts to check locally stored credentials on the device.
                 * If local credentials are found, it processes the login redirect. If no credentials are found
                 * or the login still fails, it displays an error message, restarts the login activity, and
                 * redirects the user to login again.
                 *
                 * @param e The exception representing the error during the credential retrieval process.
                 */
                @Override
                public void error(Exception e) {
                    Log.d(LOG_TAG, "Cloud Credential Check FAILED... so we can try the CP!!");
                    UserCredential credential = checkCredentialsOnDevice(username, password);
                    if (credential != null) {
                        Log.d(LOG_TAG, "Found credential in CP: " + credential);
                        LoginUtility.setUsername(getApplicationContext(), username);
                        LoginUtility.setLoggedIn(getApplicationContext(), credential);
                        processLoginRedirect(LoginUtility.getUserRole(getApplicationContext()));
                    } else {
                        Log.d(LOG_TAG, "CP Credential CHECK failed TOO! Just have to restart app.");
                        Toast.makeText(
                                getApplicationContext(),
                                "Unable to Login. Please check Internet connection.",
                                Toast.LENGTH_LONG).show();
                        restartLoginActivity(getApplicationContext());
                    }
                }
            });
        }
    }
}
