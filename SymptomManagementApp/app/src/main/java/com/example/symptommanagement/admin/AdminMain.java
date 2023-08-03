package com.example.symptommanagement.admin;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.example.symptommanagement.LoginActivity;
import com.example.symptommanagement.R;

/**
 * Activity class representing the main screen for the Admin user.
 * This class is responsible for displaying the main screen of the Admin user,
 * including the AdminMainFragment.
 */
public class AdminMain extends Activity {
    /**
     * Called when the activity is first created.
     * This method is responsible for setting up the initial state of the activity,
     * including loading the layout, configuring the action bar, and adding the AdminMainFragment if it's the first launch.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, or null if this is the first launch.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Call the superclass method to perform necessary setup for the activity
        super.onCreate(savedInstanceState);

        // Load the activity layout from the XML resource file
        setContentView(R.layout.activity_admin_main);

        // Get a reference to the action bar
        ActionBar actionBar = getActionBar();

        // Check if the action bar is available
        if (actionBar != null) {
            // Disable the home button in the action bar (Up button)
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        // Check if the activity is first created (savedInstanceState is null)
        if (savedInstanceState == null) {
            // If it's the first launch, add the AdminMainFragment to the container using a FragmentTransaction
            getFragmentManager().beginTransaction()
                    .add(R.id.admin_main_container, new AdminMainFragment())
                    .commit();
        }
    }

    /**
     * Initialize the options menu for the activity.
     * This method is called to inflate the menu resource file and add items to the action bar if it is present.
     *
     * @param menu The options menu to be initialized.
     * @return true to display the menu, false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu resource file to create the options menu
        getMenuInflater().inflate(R.menu.admin_main, menu);
        return true;
    }

    /**
     * Handle action bar item clicks.
     * This method is called when an item in the options menu is selected.
     *
     * @param item The selected menu item.
     * @return true if the selected item is handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get the ID of the selected menu item
        int id = item.getItemId();

        // Check if the "Logout" option is selected from the menu
        if (id == R.id.action_logout) {
            // If the "Logout" option is selected, restart the login activity
            LoginActivity.restartLoginActivity(this);
        }

        // Return the result of the super method, which may handle other menu items
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the device's back button is pressed.
     * This method is responsible for defining the behavior when the back button is pressed.
     * If the current fragment is the AdminMainFragment, the activity will be sent to the home screen.
     * Otherwise, the default behavior of the back button will be executed.
     */
    @Override
    public void onBackPressed() {
        // Check if the current fragment is the AdminMainFragment
        if (getFragmentManager().findFragmentById(R.id.admin_main_container) instanceof AdminMainFragment) {
            // If the current fragment is the AdminMainFragment, send the activity to the home screen
            startActivity(new Intent()
                    .setAction(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_HOME));
        } else {
            // If the current fragment is not the AdminMainFragment, perform the default back button behavior
            super.onBackPressed();
        }
    }
}
