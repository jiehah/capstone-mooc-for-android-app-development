package com.example.symptommanagement.client;


import com.example.symptommanagement.data.*;
import retrofit.http.*;

import java.util.Collection;

/**
 * The SymptomManagementApi interface defines the endpoints for making API calls to the Symptom Management system.
 * It uses Retrofit annotations to specify the HTTP methods and paths for each endpoint.
 */
public interface SymptomManagementApi {

    // Constants for API paths
    String TOKEN_PATH = "/oauth/token";
    String PATIENT_PATH = "/patient";
    String PHYSICIAN_PATH = "/physician";
    String MEDICATION_PATH = "/medication";
    String ALERT_PATH = "/alert";
    String CREDENTIAL_PATH = "/credential";

    // Constants for query parameters and path parameters
    String NAME_PARAMETER = "name";
    String LAST_NAME_PARAMETER = "lastname";
    String ID_PATH = "/{id}";
    String ID_PARAMETER = "id";
    String SEARCH_PATH = "/find";
    String PATIENT_SEARCH_PATH = PATIENT_PATH + SEARCH_PATH;
    String PHYSICIAN_SEARCH_PATH = PHYSICIAN_PATH + SEARCH_PATH;
    String MEDICATION_SEARCH_PATH = MEDICATION_PATH + SEARCH_PATH;
    String CREDENTIAL_SEARCH_PATH = CREDENTIAL_PATH + SEARCH_PATH;
    String PHYSICIAN_ALERT_PATH = PHYSICIAN_PATH + ID_PATH + ALERT_PATH;

    /**
     * Get a collection of Patient objects from the server.
     *
     * @return A Collection of Patient objects representing the patient list.
     */
    @GET(PATIENT_PATH)
    Collection<Patient> getPatientList();

    /**
     * Get a specific Patient object from the server by its ID.
     *
     * @param id The ID of the patient to retrieve.
     * @return The Patient object representing the retrieved patient.
     */
    @GET(PATIENT_PATH + ID_PATH)
    Patient getPatient(@Path(ID_PARAMETER) String id);

    /**
     * Add a new Patient object to the server.
     *
     * @param patient The Patient object to add to the server.
     * @return The Patient object representing the newly added patient.
     */
    @POST(PATIENT_PATH)
    Patient addPatient(@Body Patient patient);

    /**
     * Update an existing Patient object on the server.
     *
     * @param id      The ID of the patient to update.
     * @param patient The updated Patient object to save on the server.
     * @return The Patient object representing the updated patient.
     */
    @PUT(PATIENT_PATH + ID_PATH)
    Patient updatePatient(@Path(ID_PARAMETER) String id, @Body Patient patient);

    /**
     * Delete a specific Patient object from the server by its ID.
     *
     * @param id The ID of the patient to delete.
     * @return The Patient object representing the deleted patient.
     */
    @DELETE(PATIENT_PATH + ID_PATH)
    Patient deletePatient(@Path(ID_PARAMETER) String id);

    /**
     * Search for patients by their name.
     *
     * @param name The name of the patient to search for.
     * @return A Collection of Patient objects matching the search criteria.
     */
    @GET(PATIENT_SEARCH_PATH)
    Collection<Patient> findByPatientName(@Query(NAME_PARAMETER) String name);

    /**
     * Get a collection of Physician objects from the server.
     *
     * @return A Collection of Physician objects representing the physician list.
     */
    @GET(PHYSICIAN_PATH)
    Collection<Physician> getPhysicianList();

    /**
     * Get a specific Physician object from the server by its ID.
     *
     * @param id The ID of the physician to retrieve.
     * @return The Physician object representing the retrieved physician.
     */
    @GET(PHYSICIAN_PATH + ID_PATH)
    Physician getPhysician(@Path(ID_PARAMETER) String id);

    /**
     * Add a new Physician object to the server.
     *
     * @param physician The Physician object to add to the server.
     * @return The Physician object representing the newly added physician.
     */
    @POST(PHYSICIAN_PATH)
    Physician addPhysician(@Body Physician physician);

    /**
     * Update an existing Physician object on the server.
     *
     * @param id        The ID of the physician to update.
     * @param physician The updated Physician object to save on the server.
     * @return The Physician object representing the updated physician.
     */
    @PUT(PHYSICIAN_PATH + ID_PATH)
    Physician updatePhysician(@Path(ID_PARAMETER) String id, @Body Physician physician);

    /**
     * Delete a specific Physician object from the server by its ID.
     *
     * @param userId The ID of the physician to delete.
     * @return The Physician object representing the deleted physician.
     */
    @DELETE(PHYSICIAN_PATH + ID_PATH)
    Physician deletePhysician(@Path(ID_PARAMETER) String userId);

    /**
     * Search for physicians by their last name.
     *
     * @param lastName The last name of the physician to search for.
     * @return A Collection of Physician objects matching the search criteria.
     */
    @GET(PHYSICIAN_SEARCH_PATH)
    Collection<Physician> findByPhysicianLastName(@Query(NAME_PARAMETER) String lastName);

    /**
     * Get a collection of Alert objects associated with a specific physician.
     *
     * @param id The ID of the physician for which to retrieve alerts.
     * @return A Collection of Alert objects associated with the physician.
     */
    @GET(PHYSICIAN_ALERT_PATH)
    Collection<Alert> getPatientAlerts(@Path(ID_PARAMETER) String id);

    /**
     * Get a collection of Alert objects from the server.
     *
     * @return A Collection of Alert objects representing the alert list.
     */
    @GET(ALERT_PATH)
    Collection<Alert> getAlertList();

    /**
     * Add a new Alert object to the server.
     *
     * @param alert The Alert object to add to the server.
     * @return The Alert object representing the newly added alert.
     */
    @POST(ALERT_PATH)
    Alert addAlert(@Body Alert alert);

    /**
     * Delete a specific Alert object from the server by its ID.
     *
     * @param alertId The ID of the alert to delete.
     * @return The Alert object representing the deleted alert.
     */
    @DELETE(ALERT_PATH + ID_PATH)
    Alert deleteAlert(@Path(ID_PARAMETER) String alertId);

    /**
     * Get a specific Medication object from the server by its ID.
     *
     * @param id The ID of the medication to retrieve.
     * @return The Medication object representing the retrieved medication.
     */
    @GET(MEDICATION_PATH + ID_PATH)
    Medication getMedication(@Path(ID_PARAMETER) String id);

    /**
     * Get a collection of Medication objects from the server.
     *
     * @return A Collection of Medication objects representing the medication list.
     */
    @GET(MEDICATION_PATH)
    Collection<Medication> getMedicationList();

    /**
     * Add a new Medication object to the server.
     *
     * @param medication The Medication object to add to the server.
     * @return The Medication object representing the newly added medication.
     */
    @POST(MEDICATION_PATH)
    Medication addMedication(@Body Medication medication);

    /**
     * Update an existing Medication object on the server.
     *
     * @param id         The ID of the medication to update.
     * @param medication The updated Medication object to save on the server.
     * @return The Medication object representing the updated medication.
     */
    @PUT(MEDICATION_PATH + ID_PATH)
    Medication updateMedication(@Path(ID_PARAMETER) String id, @Body Medication medication);

    /**
     * Delete a specific Medication object from the server by its ID.
     *
     * @param id The ID of the medication to delete.
     * @return The Medication object representing the deleted medication.
     */
    @DELETE(MEDICATION_PATH + ID_PATH)
    Medication deleteMedication(@Path(ID_PARAMETER) String id);

    /**
     * Search for medications by their name.
     *
     * @param name The name of the medication to search for.
     * @return A Collection of Medication objects matching the search criteria.
     */
    @GET(MEDICATION_SEARCH_PATH)
    Collection<Medication> findByMedicationName(@Query(NAME_PARAMETER) String name);

    /**
     * Search for user credentials by their username.
     *
     * @param username The username to search for.
     * @return A Collection of UserCredential objects matching the search criteria.
     */
    @GET(CREDENTIAL_SEARCH_PATH)
    Collection<UserCredential> findByUserName(@Query(NAME_PARAMETER) String username);
}
