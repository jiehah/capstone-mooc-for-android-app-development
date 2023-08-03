package com.example.symptommanagement.integration.test;

import com.example.symptommanagement.client.SymptomManagementApi;
import com.example.symptommanagement.repository.Medication;
import com.example.symptommanagement.repository.Patient;
import com.example.symptommanagement.repository.Physician;
import com.example.symptommanagement.oauth.SecuredRestBuilder;
import com.example.symptommanagement.oauth.unsafe.EasyHttpClient;
import com.example.symptommanagement.testdata.TestData;
import org.junit.Test;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;

import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for the SymptomManagementApi client.
 * Tests the functionality of adding and listing patients, physicians, and medications.
 */
public class SymptomManagementClientApiTest {

    public static final String CLIENT_ID = "mobile";
    public final static String SERVER_ADDRESS = "https://localhost:8443";

    /**
     * SymptomManagementApi instance used for testing.
     */
    SymptomManagementApi symptomManagementApi = new SecuredRestBuilder()
            .setLoginEndpoint(SERVER_ADDRESS + SymptomManagementApi.TOKEN_PATH)
            .setUsername("admin")
            .setPassword("pass")
            .setClientId(CLIENT_ID)
            .setClient(new ApacheClient(new EasyHttpClient()))
            .setEndpoint(SERVER_ADDRESS)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build()
            .create(SymptomManagementApi.class);

    /**
     * Create a random patient, physician, and medication to use in the test
     */
    private final Patient randomPatient = TestData.randomPatient("Donald", "Duck", "12/11/1944");
    private final Physician randomPhysician = TestData.randomPhysician("Minnie", "Mouse");
    private final Medication randomMedication = TestData.randomMedication("hugs");

    /**
     * Test the functionality of adding and listing patients, physicians, and medications.
     */
    @Test
    public void testUsersAddAndList() {
        // Add the random patient to the server and check if it is not null
        Patient addedPatient = symptomManagementApi.addPatient(randomPatient);
        assertNotNull(addedPatient);

        // Get the list of patients from the server and check if the added patient is present in the list
        Collection<Patient> patients = symptomManagementApi.getPatientList();
        assertTrue(patients.contains(addedPatient));

        // Add the random physician to the server and check if it is not null
        Physician addedPhysician = symptomManagementApi.addPhysician(randomPhysician);
        assertNotNull(addedPhysician);

        // Get the list of physicians from the server and check if the added physician is present in the list
        Collection<Physician> physicians = symptomManagementApi.getPhysicianList();
        assertTrue(physicians.contains(addedPhysician));

        // Add the random medication to the server and check if it is not null
        Medication addedMedication = symptomManagementApi.addMedication(randomMedication);
        assertNotNull(addedMedication);

        // Get the list of medications from the server and check if the added medication is present in the list
        Collection<Medication> medications = symptomManagementApi.getMedicationList();
        assertTrue(medications.contains(addedMedication));
    }
}
