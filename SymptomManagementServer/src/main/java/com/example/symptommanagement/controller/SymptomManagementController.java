package com.example.symptommanagement.controller;

import com.example.symptommanagement.client.SymptomManagementApi;
import com.example.symptommanagement.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

/**
 * The `SymptomManagementController` class is a Spring `@Controller` responsible for handling various API endpoints
 * related to symptom management for patients, physicians, medications, and alerts. It facilitates communication
 * between the frontend and the backend, allowing authorized users with different roles (ROLE_PATIENT, ROLE_PHYSICIAN,
 * ROLE_ADMIN) to perform specific actions on patients, physicians, medications, and alerts.
 * <p>
 * This controller implements several API methods that interact with different repositories to perform CRUD operations
 * on the database entities. It also utilizes Spring Security's `@PreAuthorize` annotation to enforce access control
 * based on user roles, ensuring that only users with the appropriate roles can access specific endpoints.
 * <p>
 * The class contains several helper methods that handle tasks like updating physician-patient lists, creating alerts
 * for physicians, checking patient severity based on pain logs, and sorting logs in reverse order by creation date.
 * These helper methods enhance the readability and maintainability of the main API methods.
 * <p>
 * Overall, the `SymptomManagementController` plays a crucial role in managing patient data, physician-patient
 * relationships, medications, and alerts within the symptom management system. Its implementation ensures that the
 * system operates securely and efficiently while providing a seamless experience for patients, physicians, and
 * administrators interacting with the system.
 */
@Controller
public class SymptomManagementController {

    static final Logger logger = LoggerFactory.getLogger(SymptomManagementController.class);

    // Autowire repositories
    private final PatientRepository patientRepository;
    private final PhysicianRepository physicianRepository;
    private final MedicationRepository medicationRepository;
    private final AlertRepository alertRepository;
    private final UserCredentialRepository userCredentialRepository;

    /**
     * Constructor for the SymptomManagementController class.
     *
     * @param patientRepository        The repository for patient data.
     * @param physicianRepository      The repository for physician data.
     * @param medicationRepository     The repository for medication data.
     * @param alertRepository          The repository for alert data.
     * @param userCredentialRepository The repository for user credential data.
     */
    public SymptomManagementController(PatientRepository patientRepository,
                                       PhysicianRepository physicianRepository,
                                       MedicationRepository medicationRepository,
                                       AlertRepository alertRepository,
                                       UserCredentialRepository userCredentialRepository) {
        this.patientRepository = patientRepository;
        this.physicianRepository = physicianRepository;
        this.medicationRepository = medicationRepository;
        this.alertRepository = alertRepository;
        this.userCredentialRepository = userCredentialRepository;
    }

    /**
     * This endpoint requires ROLE_PHYSICIAN or ROLE_ADMIN to access.
     * <p>
     * Retrieve the list of all patients from the patient repository.
     *
     * @return A collection of Patient objects representing all patients.
     */
    @PreAuthorize("hasAnyRole('ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.PATIENT_PATH, method = RequestMethod.GET)
    public @ResponseBody Collection<Patient> getPatientList() {
        return patientRepository.findAll();
    }

    /**
     * This endpoint requires ROLE_PATIENT, ROLE_PHYSICIAN, or ROLE_ADMIN to access.
     * <p>
     * Retrieve a specific patient by their ID from the patient repository.
     *
     * @param id The ID of the patient to retrieve.
     * @return The Patient object representing the specific patient, or null if not found.
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.PATIENT_PATH
            + SymptomManagementApi.ID_PATH, method = RequestMethod.GET)
    public @ResponseBody Patient getPatient(
            @PathVariable(SymptomManagementApi.ID_PARAMETER) String id) {
        return patientRepository.findById(id).orElse(null);
    }

    /**
     * This endpoint requires ROLE_ADMIN to access.
     * <p>
     * Add a new patient to the patient repository.
     *
     * @param patient The Patient object to add to the repository.
     * @return The Patient object representing the newly added patient.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.PATIENT_PATH, method = RequestMethod.POST)
    public @ResponseBody Patient addPatient(
            @RequestBody Patient patient) {
        // Save a new patient to the patient repository
        Patient savedPatient = patientRepository.save(patient);
        if (savedPatient != null) {
            // Add credentials for the newly added patient
            addCredentials(savedPatient);
        }
        return savedPatient;
    }

    /**
     * This endpoint requires ROLE_PATIENT, ROLE_PHYSICIAN, or ROLE_ADMIN to access.
     * <p>
     * Update an existing patient's records on the patient repository.
     *
     * @param id        The ID of the patient to update.
     * @param patient   The updated Patient object to save on the repository.
     * @param principal The Principal object representing the currently authenticated user.
     * @return The Patient object representing the updated patient.
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.PATIENT_PATH
            + SymptomManagementApi.ID_PATH, method = RequestMethod.PUT)
    public @ResponseBody Patient updatePatient(
            @PathVariable(SymptomManagementApi.ID_PARAMETER) String id,
            @RequestBody Patient patient,
            Principal principal) {

        // Logging the user who initiated the update
        logger.debug("Updating the Patient Records - BEGIN - User is  : " + principal.getName());

        // Sorting status logs, medication logs, and pain logs of the patient
        sortStatusLogs(patient);
        sortMedLogs(patient);
        sortPainLogs(patient);

        // Creating alerts for this patient
        logger.debug("Creating alerts for this patient");
        processAlerts(id, patient);

        // Final saving the patient to storage
        logger.debug("FINAL Saving the patient to storage.");
        // Save the updated patient to the patient repository
        Patient savedPatient = patientRepository.save(patient);
        if (savedPatient != null) {
            if (principal.getName().equalsIgnoreCase("admin")) {
                // If the user is an admin, update physician's patient list as well
                updatePhysicianPatientList(savedPatient);
            } else {
                logger.info("This user is not admin, so we won't bother about the doctor update.");
            }
        }
        return savedPatient;
    }

    /**
     * This endpoint requires ROLE_ADMIN to access.
     * <p>
     * Delete a specific patient by their ID from the patient repository.
     *
     * @param id The ID of the patient to delete.
     * @return The Patient object representing the deleted patient, or null if not found.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.PATIENT_PATH
            + SymptomManagementApi.ID_PATH, method = RequestMethod.DELETE)
    public @ResponseBody Patient deletePatient(
            @PathVariable(SymptomManagementApi.ID_PARAMETER) String id) {
        // Find the patient to delete by ID
        Patient found = patientRepository.findById(id).orElse(null);
        if (found != null) {
            // Delete the patient from the patient repository
            patientRepository.delete(found);
        }
        return found;
    }

    /**
     * This endpoint requires ROLE_PHYSICIAN or ROLE_ADMIN to access.
     * <p>
     * Search patients by name from the patient repository.
     *
     * @param name The name of the patients to search for.
     * @return A collection of Patient objects matching the given name.
     */
    @PreAuthorize("hasAnyRole('ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.PATIENT_SEARCH_PATH, method = RequestMethod.GET)
    public @ResponseBody Collection<Patient> findByPatientName(
            @RequestParam(SymptomManagementApi.NAME_PARAMETER) String name) {
        // Create a collection to store found patients
        Collection<Patient> foundList = new HashSet<>();
        logger.debug("Patient Name to Search " + name);
        // Get all patients from the patient repository
        Collection<Patient> patients = patientRepository.findAll();
        if (patients != null) {
            logger.debug("All patients : " + patients.size());
            // Iterate through all patients and add matching patients to the foundList
            for (Patient patient : patients) {
                if (patient.getName().contentEquals(name)) {
                    logger.debug("Patient Match is " + patient.getName());
                    foundList.add(patient);
                }
            }
        }
        logger.debug("Found patients : " + foundList.size());
        return foundList;
    }

    /**
     * This endpoint requires ROLE_PHYSICIAN or ROLE_ADMIN to access.
     * <p>
     * Retrieve the list of all physicians from the physician repository.
     *
     * @return A collection of Physician objects representing all physicians.
     */
    @PreAuthorize("hasAnyRole('ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.PHYSICIAN_PATH, method = RequestMethod.GET)
    public @ResponseBody Collection<Physician> getPhysicianList() {
        return physicianRepository.findAll();
    }

    /**
     * This endpoint requires ROLE_PHYSICIAN or ROLE_ADMIN to access.
     * <p>
     * Retrieve a specific physician by their ID from the physician repository.
     *
     * @param id The ID of the physician to retrieve.
     * @return The Physician object representing the specific physician, or null if not found.
     */
    @PreAuthorize("hasAnyRole('ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.PHYSICIAN_PATH
            + SymptomManagementApi.ID_PATH, method = RequestMethod.GET)
    public @ResponseBody Physician getPhysician(
            @PathVariable(SymptomManagementApi.ID_PARAMETER) String id) {
        return physicianRepository.findById(id).orElse(null);
    }

    /**
     * This endpoint requires ROLE_ADMIN to access.
     * <p>
     * Add a new physician to the physician repository.
     *
     * @param physician The Physician object to add to the repository.
     * @return The Physician object representing the newly added physician.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.PHYSICIAN_PATH, method = RequestMethod.POST)
    public @ResponseBody Physician addPhysician(
            @RequestBody Physician physician) {
        // Save a new physician to the physician repository
        Physician savedPhysician = physicianRepository.save(physician);
        if (savedPhysician != null) {
            // Add credentials for the newly added physician
            addCredentials(savedPhysician);
        }
        return savedPhysician;
    }

    /**
     * This endpoint requires ROLE_PHYSICIAN or ROLE_ADMIN to access.
     * <p>
     * Update an existing physician's records on the physician repository.
     *
     * @param id        The ID of the physician to update.
     * @param physician The updated Physician object to save on the repository.
     * @return The Physician object representing the updated physician.
     */
    @PreAuthorize("hasAnyRole('ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.PHYSICIAN_PATH
            + SymptomManagementApi.ID_PATH, method = RequestMethod.PUT)
    public @ResponseBody Physician updatePhysician(
            @PathVariable(SymptomManagementApi.ID_PARAMETER) String id,
            @RequestBody Physician physician) {
        // Save the updated physician to the physician repository
        return physicianRepository.save(physician);
    }

    /**
     * This endpoint requires ROLE_ADMIN to access.
     * <p>
     * Delete a specific physician by their ID from the physician repository.
     *
     * @param id The ID of the physician to delete.
     * @return The Physician object representing the deleted physician, or null if not found.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.PHYSICIAN_PATH
            + SymptomManagementApi.ID_PATH, method = RequestMethod.DELETE)
    public @ResponseBody Physician deletePhysician(@PathVariable(SymptomManagementApi.ID_PARAMETER) String id) {
        // Find the physician to delete by ID
        Physician found = physicianRepository.findById(id).orElse(null);
        if (found != null) {
            // Delete the physician from the physician repository
            physicianRepository.delete(found);
        }
        return found;
    }

    /**
     * This endpoint requires ROLE_PHYSICIAN or ROLE_ADMIN to access.
     * <p>
     * Search physicians by last name from the physician repository.
     *
     * @param lastName The last name of the physicians to search for.
     * @return A collection of Physician objects matching the given last name.
     */
    @PreAuthorize("hasAnyRole('ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.PHYSICIAN_SEARCH_PATH, method = RequestMethod.GET)
    public @ResponseBody Collection<Physician> findByPhysicianName(
            @RequestParam(SymptomManagementApi.NAME_PARAMETER) String lastName) {
        // Search physicians by last name in the physician repository
        return physicianRepository.findByLastName(lastName);
    }

    /**
     * This endpoint requires ROLE_PHYSICIAN or ROLE_ADMIN to access.
     * <p>
     * Retrieve the list of alerts associated with a specific physician from the alert repository.
     *
     * @param id The ID of the physician to retrieve alerts for.
     * @return A collection of Alert objects representing the alerts associated with the physician.
     */
    @PreAuthorize("hasAnyRole('ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.PHYSICIAN_ALERT_PATH, method = RequestMethod.GET)
    public @ResponseBody Collection<Alert> getPatientAlerts(
            @PathVariable(SymptomManagementApi.ID_PARAMETER) String id) {
        // Find all alerts associated with the specified physician ID
        Collection<Alert> foundAlerts = alertRepository.findByPhysicianId(id);
        if (foundAlerts != null && foundAlerts.size() > 0) {
            logger.debug("Alerts to be verified: " + foundAlerts.size());
            // Iterate through the found alerts and check if physicianContacted status exists
            for (Alert alert : foundAlerts) {
                logger.debug("Checking this alert: " + alert.toString());
                // Find the physicianContacted status log associated with the alert
                StatusLog s = findPhysicianContactedStatus(alert);
                if (s != null) {
                    logger.debug("With this status Log: " + s);
                    // Set physicianContacted field of the alert to status log created date
                    alert.setPhysicianContacted(s.getCreated());
                    logger.debug("Found a Physician Contact Message post-alert"
                            + "...setting contacted date to status log created: " + alert);
                } else {
                    logger.debug("No Status Logs found to compare with.");
                }
            }
        }
        return foundAlerts;
    }

    /**
     * This endpoint requires ROLE_ADMIN to access.
     * <p>
     * Retrieve the list of all alerts from the alert repository.
     *
     * @return A collection of Alert objects representing all alerts.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.ALERT_PATH, method = RequestMethod.GET)
    public @ResponseBody Collection<Alert> getAlertList() {
        return alertRepository.findAll();
    }

    /**
     * This endpoint requires ROLE_PHYSICIAN or ROLE_ADMIN to access.
     * <p>
     * Add a new alert to the alert repository.
     *
     * @param alert The Alert object to add to the repository.
     * @return The Alert object representing the newly added alert.
     */
    @PreAuthorize("hasAnyRole('ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.ALERT_PATH, method = RequestMethod.POST)
    public @ResponseBody Alert addAlert(@RequestBody Alert alert) {
        return alertRepository.save(alert);
    }

    /**
     * This endpoint requires ROLE_PHYSICIAN or ROLE_ADMIN to access.
     * <p>
     * Delete a specific alert by its ID from the alert repository.
     *
     * @param id The ID of the alert to delete.
     * @return The Alert object representing the deleted alert, or null if not found.
     */
    @PreAuthorize("hasAnyRole('ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.ALERT_PATH
            + SymptomManagementApi.ID_PATH, method = RequestMethod.DELETE)
    public @ResponseBody Alert deleteNotification(
            @PathVariable(SymptomManagementApi.ID_PARAMETER) String id) {
        // Find the alert to delete by ID
        Alert found = alertRepository.findById(id).orElse(null);
        if (found != null) {
            // Delete the alert from the alert repository
            alertRepository.delete(found);
        }
        return found;
    }

    /**
     * Retrieve the list of all medications from the medication repository.
     *
     * @return A collection of Medication objects representing all medications.
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.MEDICATION_PATH, method = RequestMethod.GET)
    public @ResponseBody Collection<Medication> getMedicationList() {
        return medicationRepository.findAll();
    }

    /**
     * Retrieve a specific medication by its ID from the medication repository.
     *
     * @param id The ID of the medication to retrieve.
     * @return The Medication object representing the specific medication, or null if not found.
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.MEDICATION_PATH
            + SymptomManagementApi.ID_PATH, method = RequestMethod.GET)
    public @ResponseBody Medication getMedication(
            @PathVariable(SymptomManagementApi.ID_PARAMETER) String id) {
        return medicationRepository.findById(id).orElse(null);
    }

    /**
     * Add a new medication to the medication repository.
     *
     * @param medication The Medication object to add to the repository.
     * @return The Medication object representing the newly added medication.
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.MEDICATION_PATH, method = RequestMethod.POST)
    public @ResponseBody Medication addMedication(
            @RequestBody Medication medication) {
        return medicationRepository.save(medication);
    }

    /**
     * Update an existing medication's records on the medication repository.
     *
     * @param id         The ID of the medication to update.
     * @param medication The updated Medication object to save on the repository.
     * @return The Medication object representing the updated medication.
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.MEDICATION_PATH
            + SymptomManagementApi.ID_PATH, method = RequestMethod.PUT)
    public @ResponseBody Medication updateMedication(
            @PathVariable(SymptomManagementApi.ID_PARAMETER) String id,
            @RequestBody Medication medication) {
        medication.setId(id);
        return medicationRepository.save(medication);
    }

    /**
     * This endpoint requires ROLE_PATIENT, ROLE_PHYSICIAN, or ROLE_ADMIN to access.
     * <p>
     * Search medications by name from the medication repository.
     *
     * @param name The name of the medications to search for.
     * @return A collection of Medication objects matching the given name.
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.MEDICATION_SEARCH_PATH, method = RequestMethod.GET)
    public @ResponseBody Collection<Medication> findByMedicationName(
            @RequestParam(SymptomManagementApi.NAME_PARAMETER) String name) {
        // Search medications by name in the medication repository
        return medicationRepository.findByName(name);
    }

    /**
     * This endpoint requires ROLE_PATIENT, ROLE_PHYSICIAN, or ROLE_ADMIN to access.
     * <p>
     * Delete a specific medication by its ID from the medication repository.
     *
     * @param id The ID of the medication to delete.
     * @return The Medication object representing the deleted medication, or null if not found.
     */
    @PreAuthorize("hasAnyRole('ROLE_PATIENT','ROLE_PHYSICIAN', 'ROLE_ADMIN')")
    @RequestMapping(value = SymptomManagementApi.MEDICATION_PATH
            + SymptomManagementApi.ID_PATH, method = RequestMethod.DELETE)
    public @ResponseBody Medication deleteMedication(
            @PathVariable(SymptomManagementApi.ID_PARAMETER) String id) {
        // Find the medication to delete by ID
        Medication found = medicationRepository.findById(id).orElse(null);
        if (found != null) {
            // Delete the medication from the medication repository
            medicationRepository.delete(found);
        }
        return found;
    }

    /**
     * Retrieve a collection of user credentials associated with a specific username from the user credential repository.
     * If the username is "admin," it returns a single user credential with admin role.
     *
     * @param username The username to search for.
     * @return A collection of UserCredential objects associated with the given username.
     */
    @RequestMapping(value = SymptomManagementApi.CREDENTIAL_SEARCH_PATH, method = RequestMethod.GET)
    public @ResponseBody Collection<UserCredential> findByUserName(
            @RequestParam(SymptomManagementApi.NAME_PARAMETER) String username) {
        if (username.toLowerCase().contentEquals("admin")) {
            // Return a single user credential with admin role for the username "admin"
            UserCredential cred = new UserCredential();
            cred.setId("");
            cred.setUserId("");
            cred.setUserName(username);
            cred.setUserRole(UserCredential.UserRole.ADMIN);
            Collection<UserCredential> creds = new HashSet<>();
            creds.add(cred);
            return creds;
        }

        // Search for user credentials by username in the user credential repository
        return userCredentialRepository.findByUserName(username.toLowerCase());
    }

    /**
     * Clear all the databases.
     */
    @RequestMapping(value = SymptomManagementApi.CLEAR_PATH, method = RequestMethod.GET)
    public @ResponseBody boolean clear() {
        try {
            alertRepository.deleteAll();
            medicationRepository.deleteAll();
            patientRepository.deleteAll();
            physicianRepository.deleteAll();
            userCredentialRepository.deleteAll();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Helper method to find the status log associated with the physician contacted status
     * for a specific alert.
     *
     * @param a The Alert object to check for physician contacted status.
     * @return The StatusLog object representing the physician contacted status, or null if not found.
     */
    private synchronized StatusLog findPhysicianContactedStatus(Alert a) {
        // Check if the alert has a physician ID associated with it
        if (a.getPhysicianId() != null) {
            // Find the physician associated with the alert
            Physician dr = physicianRepository.findById(a.getPhysicianId()).orElse(null);
            if (dr != null && dr.getPatients() != null) {
                // Iterate through the physician's patients to find status logs
                for (Patient p : dr.getPatients()) {
                    if (p.getStatusLog() != null && p.getStatusLog().size() > 0) {
                        // Find the status log where the alert was created
                        for (StatusLog s : p.getStatusLog()) {
                            if (a.getCreated() < s.getCreated()) {
                                logger.debug("found status log turning off the alert.");
                                return s;
                            }
                        }
                    }
                }
            }
        }
        logger.debug("NO physician contact message found for the alert.");
        return null;
    }

    /**
     * Helper method to update the patient list of physicians with a cloned patient.
     *
     * @param patient The Patient object to be cloned and added to the physicians' patient list.
     */
    private void updatePhysicianPatientList(Patient patient) {
        Collection<Physician> doctors = patient.getPhysicians();
        if (doctors == null) {
            logger.debug("This patient has no doctors assigned to them.");
            return;
        }

        // Clone the patient with limited information for the physicians
        Patient clonedPatient = Patient.cloneForPhysician(patient);
        logger.debug("The Cloned Patient is : " + clonedPatient);

        for (Physician dr : doctors) {
            logger.debug("Checking this doctor's list : " + dr.getName());
            Physician thisDoctor = physicianRepository.findById(dr.getId()).orElse(null);
            if (thisDoctor != null) {
                // Add the cloned patient to each physician's patient list
                addPatient(thisDoctor, clonedPatient);
            } else {
                logger.error("Something fishy! Could not find this doctor by id: " + dr.getId());
            }
        }
    }

    /**
     * Helper method to add a patient to a physician's patient list.
     *
     * @param physician The Physician object to add the patient to.
     * @param patient   The Patient object to be added to the physician's patient list.
     */
    private void addPatient(Physician physician, Patient patient) {
        logger.info("Adding patient to physician list.");

        boolean found = false;
        if (physician.getPatients() == null) {
            // If physician's patient list is null, initialize it as a new HashSet
            physician.setPatients(new HashSet<>());

        } else {
            logger.debug("Check to see if the patient is in the doctor's list already.");
            // Check if the patient is already in the physician's patient list
            for (Patient p : physician.getPatients()) {
                if (Objects.equals(p.getId(), patient.getId())) {
                    found = true;
                    logger.debug("We found the patient there already. Good to go.");
                    break;
                }
            }
        }

        if (!found) {
            // If the patient is not in the physician's patient list, add them and save the physician
            logger.debug("We are adding this patient to the doctor's list : " + patient);
            physician.getPatients().add(patient);
            Physician saved = physicianRepository.save(physician);
            if (saved == null) {
                logger.error("Physician's updated patient list did not save! Something went wrong!");
            }
        }
    }

    /**
     * Helper method to add credentials for a new patient.
     *
     * @param patient The Patient object for whom to add credentials.
     */
    private void addCredentials(Patient patient) {
        logger.debug("Adding CREDENTIALS for new patient : " + patient.toString());
        // Create a new UserCredential object with patient information
        UserCredential credential = new UserCredential();
        credential.setUserId(patient.getId());
        credential.setPassword("pass");
        credential.setUserName(patient.getUserName().toLowerCase());
        credential.setUserRole(UserCredential.UserRole.PATIENT);
        // Save the user credentials in the user credential repository
        UserCredential saved = userCredentialRepository.save(credential);
        if (saved == null) {
            logger.error("ERROR : Credentials did not SAVE!!");
        } else {
            logger.debug("Credential SAVED is: " + saved);
        }
    }

    /**
     * Helper method to add credentials for a new physician.
     *
     * @param physician The Physician object for whom to add credentials.
     */
    private void addCredentials(Physician physician) {
        logger.debug("Adding CREDENTIALS for new physician : " + physician.toString());
        // Create a new UserCredential object with physician information
        UserCredential credential = new UserCredential();
        credential.setUserId(physician.getId());
        credential.setPassword("pass");
        credential.setUserName(physician.getUserName().toLowerCase());
        credential.setUserRole(UserCredential.UserRole.PHYSICIAN);
        // Save the user credentials in the user credential repository
        UserCredential saved = userCredentialRepository.save(credential);
        if (saved == null) {
            logger.error("ERROR : Credentials did not SAVE!!");
        } else {
            logger.debug("Credential SAVED is: " + saved);
        }
    }

    /**
     * Helper method to process alerts for a patient and create alerts for associated physicians.
     *
     * @param id      The ID of the patient for whom to process alerts.
     * @param patient The Patient object for whom to process alerts.
     */
    private void processAlerts(String id, Patient patient) {
        logger.debug("Processing Alerts for patient :" + patient.toString());

        // Clear old alerts associated with the patient
        int deleted = deleteAlertsByPatientId(id);
        logger.debug("Number of alerts deleted: " + deleted);

        // Check the severity level of the patient and create alerts for associated physicians
        int severityLevel = checkForPatientSeverity(patient);
        if (severityLevel > Alert.PAIN_SEVERITY_LEVEL_0) {
            logger.debug("Patient is SEVERE, so we are creating alerts for doctors.");
            if (patient.getPhysicians() != null) {
                for (Physician physician : patient.getPhysicians()) {
                    logger.debug("Creating alert for Dr. :" + physician.toString());
                    Alert alert = new Alert();
                    alert.setPatientId(id);
                    alert.setPhysicianId(physician.getId());
                    alert.setPatientName(patient.getName());
                    alert.setCreated(System.currentTimeMillis());
                    alert.setSeverityLevel(severityLevel);
                    alertRepository.save(alert);
                }
            }
        }
    }

    /**
     * Helper method to delete all alerts associated with a patient by their ID.
     *
     * @param id The ID of the patient for whom to delete alerts.
     * @return The number of alerts deleted.
     */
    private int deleteAlertsByPatientId(String id) {
        logger.debug("Deleting all Alerts for patient.");
        Collection<Alert> alerts = alertRepository.findAll();
        int count = 0;
        if (alerts != null & alerts.size() > 0) {
            // Iterate through all alerts and delete those associated with the patient's ID
            for (Alert alert : alerts) {
                if (alert.getPatientId().equals(id)) {
                    count++;
                    alertRepository.delete(alert);
                }
            }
        }
        logger.debug("Final count of deleted Alerts : " + count);
        return count;
    }

    /**
     * Helper method to check for the severity level of a patient based on their pain logs and eating habits.
     *
     * @param patient The Patient object for whom to check the severity level.
     * @return The severity level of the patient's condition (PAIN_SEVERITY_LEVEL_0 to PAIN_SEVERITY_LEVEL_3).
     */
    private int checkForPatientSeverity(Patient patient) {
        // Calculate the timestamps for 12 hours and 16 hours ago from the current time
        long sixteenHoursAgo = getHoursFromNow(-16);
        long twelveHoursAgo = getHoursFromNow(-12);

        logger.debug("Checking Severity of Patient : 12 Hours Ago is " + twelveHoursAgo
                + " 16 hours ago is " + sixteenHoursAgo);

        Collection<PainLog> painLogs = patient.getPainLog();
        if (painLogs != null && painLogs.size() > 0) {

            // Check for 12+ hours of severe pain
            logger.debug("Checking for 12+ hours of severe pain.");
            for (PainLog painLog : painLogs) {
                if (painLog.getSeverity().getValue() < PainLog.Severity.SEVERE.getValue()) {
                    logger.debug("Patient is not SEVERE.");
                    break;
                } else if (painLog.getCreated() <= twelveHoursAgo) {
                    logger.debug("Patient has been severe for 12+ hours.");
                    patient.setSeverityLevel(Alert.PAIN_SEVERITY_LEVEL_3);
                    return Alert.PAIN_SEVERITY_LEVEL_3;
                } else {
                    logger.debug("This log indicates SEVERE checking next one. " + painLog);
                }
            }

            // Check for 16+ hours of moderate to severe pain
            logger.debug("Checking for 16+ hours of moderate to severe pain.");
            for (PainLog painLog : painLogs) {
                if (painLog.getSeverity().getValue() < PainLog.Severity.MODERATE.getValue()) {
                    logger.debug("Patient is not MODERATE to SEVERE.");
                    break;
                } else if (painLog.getCreated() <= sixteenHoursAgo) {
                    logger.debug("Patient has been MODERATE to SEVERE for 16+ hours");
                    patient.setSeverityLevel(Alert.PAIN_SEVERITY_LEVEL_2);
                    return Alert.PAIN_SEVERITY_LEVEL_2;
                } else {
                    logger.debug("This log indicates MODERATE to SEVERE checking next one. " + painLog);
                }
            }

            // Check for 12+ hours of not eating
            logger.debug("Checking for 12+ hours of not eating.");
            for (PainLog painLog : painLogs) {
                if (painLog.getEating().getValue() < PainLog.Eating.NOT_EATING.getValue()) {
                    logger.debug("Patient is EATING.");
                    break;
                }
                if (painLog.getCreated() <= twelveHoursAgo) {
                    logger.debug("Patient has not eaten for 12+ hours.");
                    patient.setSeverityLevel(Alert.PAIN_SEVERITY_LEVEL_1);
                    return Alert.PAIN_SEVERITY_LEVEL_1;
                } else {
                    logger.debug("This log indicates NOT EATING checking next one. " + painLog);
                }
            }
        } else {
            logger.debug("There are no pain logs, so cannot check severity.");
        }
        // If none of the conditions are met, the patient's severity level is 0
        return Alert.PAIN_SEVERITY_LEVEL_0;
    }

    /**
     * Helper method to get the timestamp of a certain number of hours from now.
     *
     * @param hours The number of hours from now.
     * @return The timestamp of the specified number of hours from now.
     */
    public static long getHoursFromNow(int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTimeInMillis();
    }

    /**
     * Helper method to sort the patient's pain logs by reverse creation date.
     *
     * @param patient The Patient object whose pain logs need to be sorted.
     */
    private void sortPainLogs(Patient patient) {
        logger.debug("Sorting Pain Logs by reverse creation date");
        Collection<PainLog> logs = patient.getPainLog();
        if (logs == null || logs.size() == 0)
            return;
        PainLogSorter sorter = new PainLogSorter();
        TreeSet<PainLog> sortedLogs = new TreeSet<>(Collections.reverseOrder(sorter));
        sortedLogs.addAll(logs);
        patient.setPainLog(sortedLogs);
    }

    /**
     * Helper method to sort the patient's status logs by reverse creation date.
     *
     * @param patient The Patient object whose status logs need to be sorted.
     */
    private void sortStatusLogs(Patient patient) {
        logger.debug("Sorting Status Logs by reverse creation date");
        Collection<StatusLog> statusLogs = patient.getStatusLog();
        if (statusLogs == null || statusLogs.size() == 0)
            return;
        StatusLogSorter sorter = new StatusLogSorter();
        TreeSet<StatusLog> logs = new TreeSet<>(Collections.reverseOrder(sorter));
        logs.addAll(statusLogs);
        patient.setStatusLog(logs);
    }

    /**
     * Helper method to sort the patient's medication logs by reverse creation date.
     *
     * @param patient The Patient object whose medication logs need to be sorted.
     */
    private void sortMedLogs(Patient patient) {
        logger.debug("Sorting Medication Logs by reverse creation date");
        Collection<MedicationLog> logs = patient.getMedLog();
        if (logs == null || logs.size() == 0)
            return;
        MedLogSorter sorter = new MedLogSorter();
        TreeSet<MedicationLog> sortedLogs = new TreeSet<>(Collections.reverseOrder(sorter));
        sortedLogs.addAll(logs);
        patient.setMedLog(sortedLogs);
    }

    /**
     * Custom comparator to sort PainLog objects by reverse creation date.
     */
    private static class PainLogSorter implements Comparator<PainLog> {
        public int compare(PainLog x, PainLog y) {
            return Long.compare(x.getCreated(), y.getCreated());
        }
    }

    /**
     * Custom comparator to sort MedicationLog objects by reverse creation date.
     */
    private static class MedLogSorter implements Comparator<MedicationLog> {
        public int compare(MedicationLog x, MedicationLog y) {
            return Long.compare(x.getCreated(), y.getCreated());
        }
    }

    /**
     * Custom comparator to sort StatusLog objects by reverse creation date.
     */
    private static class StatusLogSorter implements Comparator<StatusLog> {
        public int compare(StatusLog x, StatusLog y) {
            return Long.compare(x.getCreated(), y.getCreated());
        }
    }
}
