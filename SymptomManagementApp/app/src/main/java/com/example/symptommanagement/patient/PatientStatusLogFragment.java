package com.example.symptommanagement.patient;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.symptommanagement.LoginUtility;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.PatientCPContract.StatusLogEntry;
import com.example.symptommanagement.data.PatientCPcvHelper;
import com.example.symptommanagement.data.StatusLog;
import com.example.symptommanagement.databinding.FragmentStatusLogEntryBinding;
import com.example.symptommanagement.sync.SymptomManagementSyncAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A fragment for logging the status of a patient, including the option to add an image.
 */
public class PatientStatusLogFragment extends Fragment {

    private final static String LOG_TAG = PatientStatusLogFragment.class.getSimpleName();
    public final static String FRAGMENT_TAG = "patient_status_Log_fragment";

    private StatusLog log;

    /**
     * Callback interface to handle interactions with the PatientStatusLogFragment.
     */
    public interface Callbacks {
        /**
         * Called when the status log is complete.
         *
         * @return True if the log is complete, false otherwise.
         */
        boolean onStatusLogComplete();
    }

    private FragmentStatusLogEntryBinding binding;
    private Uri imagePath;
    private Uri imageFile = null;
    private Uri imagePathFinal = null;
    private static final int CAMERA_PIC_REQUEST = 99;
    private static boolean showCameraButton = true;

    /**
     * Called when the fragment is being created.
     *
     * @param savedInstanceState The saved instance state of the fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable the "Up" navigation button in the ActionBar
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Called when the view for the fragment is being created.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views
     *                           in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState The saved instance state of the fragment's view.
     * @return The root view of the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatusLogEntryBinding.inflate(inflater, container, false);
        this.setRetainInstance(true);
        return binding.getRoot();
    }

    /**
     * Called when the view of the fragment has been created.
     * This method sets up click listeners for the image capture button and the save button.
     *
     * @param view               The root view of the fragment.
     * @param savedInstanceState The saved instance state of the fragment's view.
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up click listeners for the image capture button and the save button
        binding.statusImageButton.setOnClickListener(v -> processImage());
        binding.statusSaveButton.setOnClickListener(v -> saveStatusLog());
    }

    /**
     * Called when the fragment is resumed and visible to the user.
     * This method initializes the view with the status log data if available.
     */
    @Override
    public void onResume() {
        super.onResume();

        // Initialize the status log object if it's not already created
        if (log == null) {
            log = new StatusLog();
        } else {
            // Hide the image view and show the image capture button if there's no image
            binding.imageStatus.setVisibility(View.GONE);
            binding.statusImageButton.setVisibility(showCameraButton ? View.VISIBLE : View.GONE);

            // Check if there's an image associated with the status log and display it if available
            if (log != null && log.getImage_location() != null && !log.getImage_location().isEmpty()) {
                Picasso.with(getActivity())
                        .load(log.getImage_location())
                        .resize(600, 600)
                        .centerInside()
                        .into(binding.cameraPictureView);
                binding.cameraPictureView.setVisibility(View.VISIBLE);
                binding.statusImageButton.setVisibility(View.GONE);
                showCameraButton = false;
            }
        }
    }

    /**
     * Process the image capture button click.
     * This method is called when the user clicks the image capture button to capture an image.
     * It checks if there's already an image associated with the status log, and if not,
     * it starts the process to capture an image.
     */
    public void processImage() {
        if (log.getImage_location() == null) {
            addImage();
        }
    }

    /**
     * Starts the process to capture an image.
     * This method is called when the user clicks the image capture button.
     * It creates an intent to launch the camera app to capture an image.
     */
    public void addImage() {
        Intent launchCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFile = getOutputMediaFileUri();
        if (imageFile != null) {
            launchCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFile);
            startActivityForResult(launchCameraIntent, CAMERA_PIC_REQUEST);
        } else {
            Toast.makeText(getActivity(), "Unable to Store Images on this device.", Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Called when the camera app finishes capturing an image and returns the result.
     *
     * @param requestCode The request code that was used to start the camera app.
     * @param resultCode  The result code returned by the camera app.
     * @param data        The data returned by the camera app.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "onActivityResult called. requestCode: "
                + requestCode + " resultCode:" + resultCode + "data:" + data);

        // Check if the result is from the camera app and the operation was successful
        if (requestCode == CAMERA_PIC_REQUEST) {
            if (resultCode == PatientMainActivity.RESULT_OK) {
                // Get the image path and update the status log with the image location
                if (imagePath == null) {
                    imagePath = getImagePath();
                }
                imagePathFinal = imagePath;
                Log.d(LOG_TAG, "File path being saved is: " + imagePathFinal.toString());
                log.setImage_location(imagePathFinal.toString());

                // Hide the image view and show the captured image in the ImageView
                binding.imageStatus.setVisibility(View.GONE);
                binding.statusImageButton.setVisibility(View.GONE);
                showCameraButton = false;
                Log.d(LOG_TAG, "file path being opened in view : " + log.getImage_location());
                Picasso.with(getActivity())
                        .load(log.getImage_location())
                        .resize(400, 400)
                        .centerInside()
                        .into(binding.cameraPictureView);
                binding.cameraPictureView.setVisibility(View.VISIBLE);
            } else if (resultCode == PatientMainActivity.RESULT_CANCELED) {
                // If image capture is canceled, remove the image from the status log
                Log.e(LOG_TAG, "Image Capture Was Cancelled by User.");
                log.setImage_location(null);
                binding.statusImageButton.setImageResource(android.R.drawable.ic_menu_camera);
                binding.statusImageButton.setVisibility(View.VISIBLE);
                binding.cameraPictureView.setVisibility(View.GONE);
                showCameraButton = true;
            } else {
                // If image capture failed, remove the image from the status log
                Log.e(LOG_TAG, "Image Capture Failed.");
                log.setImage_location(null);
                binding.statusImageButton.setImageResource(android.R.drawable.ic_menu_camera);
                showCameraButton = true;
                binding.statusImageButton.setVisibility(View.VISIBLE);
                binding.cameraPictureView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Saves the status log entry to the database and triggers synchronization with the server.
     * Also, notifies the host activity that the status log is complete.
     */
    public void saveStatusLog() {
        log.setNote(binding.symptomNote.getText().toString());
        String mPatientId = LoginUtility.getLoginId(getActivity());
        ContentValues cv = PatientCPcvHelper.createValuesObject(mPatientId, log);
        Log.d(LOG_TAG, "Saving this status : " + log.toString());

        // Insert the status log entry into the database
        Uri uri = getActivity().getContentResolver().insert(StatusLogEntry.CONTENT_URI, cv);
        long objectId = ContentUris.parseId(uri);

        // Check if the insertion was successful
        if (objectId < 0) {
            Log.e(LOG_TAG, "Status Log Insert Failed.");
        } else {
            // Notify the host activity that the status log is complete
            ((Callbacks) getActivity()).onStatusLogComplete();
        }

        // Trigger immediate synchronization with the server
        SymptomManagementSyncAdapter.syncImmediately(getActivity());

        // Go back to the previous activity
        getActivity().onBackPressed();
    }

    /**
     * Returns the URI of the captured image file.
     *
     * @return The URI of the captured image file.
     */
    private Uri getOutputMediaFileUri() {
        File newFile = getOutputMediaFile();
        if (newFile != null) {
            return Uri.fromFile(newFile);
        } else {
            return null;
        }
    }

    /**
     * Creates and returns a new media file to store the captured image.
     *
     * @return A File representing the newly created media file, or null if external storage is not writable.
     */
    private File getOutputMediaFile() {
        if (!isExternalStorageWritable()) {
            return null;
        }

        // Get the directory for storing media files
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),
                getActivity().getString(R.string.image_folder_name));

        // Create the directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(LOG_TAG, "Failed to create directory");
                return null;
            }
        }

        // Generate a unique timestamp for the file name
        String timeStamp =
                new SimpleDateFormat(getActivity().getString(R.string.media_file_date_format),
                        Locale.US).format(new Date());

        // Create the file using the media storage directory and the timestamp
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }

    /**
     * Returns the image file URI for the captured image.
     *
     * @return The URI of the image file, or null if no image file is available.
     */
    public Uri getImagePath() {
        return imageFile;
    }

    /**
     * Returns whether external storage is writable or not.
     *
     * @return true if external storage is writable, false otherwise.
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Returns whether external storage is readable or not.
     *
     * @return true if external storage is readable, false otherwise.
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}
