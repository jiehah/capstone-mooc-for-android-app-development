package com.example.symptommanagement.integration.test;

import com.example.symptommanagement.repository.Medication;
import com.example.symptommanagement.repository.Patient;
import com.example.symptommanagement.repository.Physician;
import com.example.symptommanagement.oauth.SecuredRestBuilder;
import com.example.symptommanagement.oauth.unsafe.EasyHttpClient;
import com.example.symptommanagement.client.SymptomManagementApi;
import com.example.symptommanagement.testdata.TestData;
import org.junit.Before;
import org.junit.Test;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;

import static org.junit.Assert.assertNotNull;

/**
 * This integration test class is responsible for creating the initial database for testing purposes.
 * It uses the SymptomManagementApi to add patients, physicians, medications, and log data for them.
 * The test sets up an OAuth-secured connection to the server using the SecuredRestBuilder.
 * Note: Before running this test, make sure the server is running
 * and the correct server address and credentials are provided.
 * Also, ensure that the server has an accessible database connection and is ready to receive data.
 */
public class BuildDataTest {

    public static final String CLIENT_ID = "mobile";
    public final static String SERVER_ADDRESS = "https://localhost:8443";

    /**
     * Create an instance of SymptomManagementApi with OAuth-secured connection
     */
    SymptomManagementApi symptomManagementApi = new SecuredRestBuilder()
            // Set the login endpoint for obtaining access tokens
            .setLoginEndpoint(SERVER_ADDRESS + SymptomManagementApi.TOKEN_PATH)
            // Set the username for authentication
            .setUsername("admin")
            // Set the password for authentication
            .setPassword("pass")
            // Set the client ID for authentication
            .setClientId(CLIENT_ID)
            // Set the HTTP client to use for the REST calls
            .setClient(new ApacheClient(new EasyHttpClient()))
            // Set the base endpoint URL for the REST API
            .setEndpoint(SERVER_ADDRESS)
            // Set the logging level for Retrofit
            .setLogLevel(RestAdapter.LogLevel.FULL)
            // Create the SymptomManagementApi instance using the configured settings
            .build()
            .create(SymptomManagementApi.class);

    private Patient harryPotter = TestData.randomPatient("Harry", "Potter", "4/14/1965");
    private Patient hanSolo = TestData.randomPatient("Han", "Solo", "12/21/1933");
    private Patient bellaSwan = TestData.randomPatient("Bella", "Swan", "5/30/1995");
    private Patient martyMcFly = TestData.randomPatient("Marty", "McFly", "03/15/1965");
    private Patient mickeyMouse = TestData.randomPatient("Mickey", "Mouse", "04/11/1947");
    private Patient minnieMouse = TestData.randomPatient("Minnie", "Mouse", "04/29/1948");
    private Patient donaldDuck = TestData.randomPatient("Donald", "Duck", "06/15/1957");
    private Patient daiseyDuck = TestData.randomPatient("Daisey", "Duck", "09/21/1977");

    private Physician maryPoppins = TestData.randomPhysician("Mary", "Poppins");
    private Physician drWho = TestData.randomPhysician("Dr", "Who");
    private Physician drJules = TestData.randomPhysician("Dr", "Jules");
    private Physician drAdam = TestData.randomPhysician("Dr", "Adam");
    private Physician drDoug = TestData.randomPhysician("Dr", "Doug");
    private Physician bonesMccoy = TestData.randomPhysician("Bones", "McCoy");

    private Medication oxycontin = TestData.randomMedication("OxyContin");
    private Medication lortab = TestData.randomMedication("Lortab");
    private Medication aspirin = TestData.randomMedication("Aspirin");
    private Medication tylenol = TestData.randomMedication("Tylenol");

    @Before
    public void setUp() {
        symptomManagementApi.clear();
    }

    /**
     * This method creates the initial database for testing by adding patients, physicians, medications,
     * and log data for them using the SymptomManagementApi.
     */
    @Test
    public void createDatabaseForTesting() {
        harryPotter = symptomManagementApi.addPatient(harryPotter);
        assertNotNull(harryPotter);

        hanSolo = symptomManagementApi.addPatient(hanSolo);
        assertNotNull(hanSolo);

        bellaSwan = symptomManagementApi.addPatient(bellaSwan);
        assertNotNull(bellaSwan);

        martyMcFly = symptomManagementApi.addPatient(martyMcFly);
        assertNotNull(martyMcFly);

        mickeyMouse = symptomManagementApi.addPatient(mickeyMouse);
        assertNotNull(mickeyMouse);

        donaldDuck = symptomManagementApi.addPatient(donaldDuck);
        assertNotNull(donaldDuck);

        daiseyDuck = symptomManagementApi.addPatient(daiseyDuck);
        assertNotNull(daiseyDuck);

        minnieMouse = symptomManagementApi.addPatient(minnieMouse);
        assertNotNull(minnieMouse);

        maryPoppins = symptomManagementApi.addPhysician(maryPoppins);
        assertNotNull(maryPoppins);

        drWho = symptomManagementApi.addPhysician(drWho);
        assertNotNull(drWho);

        drJules = symptomManagementApi.addPhysician(drJules);
        assertNotNull(drJules);

        drAdam = symptomManagementApi.addPhysician(drAdam);
        assertNotNull(drAdam);

        drDoug = symptomManagementApi.addPhysician(drDoug);
        assertNotNull(drDoug);

        bonesMccoy = symptomManagementApi.addPhysician(bonesMccoy);
        assertNotNull(bonesMccoy);

        oxycontin = symptomManagementApi.addMedication(oxycontin);
        assertNotNull(oxycontin);

        lortab = symptomManagementApi.addMedication(lortab);
        assertNotNull(lortab);

        aspirin = symptomManagementApi.addMedication(aspirin);
        tylenol = symptomManagementApi.addMedication(tylenol);

        TestData.addPhysicianToPatient(maryPoppins, harryPotter);
        TestData.addPhysicianToPatient(drWho, harryPotter);
        TestData.addPhysicianToPatient(maryPoppins, bellaSwan);
        TestData.addPhysicianToPatient(maryPoppins, hanSolo);
        TestData.addPhysicianToPatient(drWho, hanSolo);
        TestData.addPhysicianToPatient(bonesMccoy, martyMcFly);
        TestData.addPhysicianToPatient(drWho, martyMcFly);
        TestData.addPhysicianToPatient(maryPoppins, martyMcFly);
        TestData.addPhysicianToPatient(maryPoppins, mickeyMouse);
        TestData.addPhysicianToPatient(bonesMccoy, mickeyMouse);
        TestData.addPhysicianToPatient(drWho, mickeyMouse);
        TestData.addPhysicianToPatient(drJules, minnieMouse);
        TestData.addPhysicianToPatient(drAdam, daiseyDuck);
        TestData.addPhysicianToPatient(drDoug, donaldDuck);

        TestData.addPrescriptionToPatient(oxycontin, harryPotter);
        TestData.addPrescriptionToPatient(lortab, harryPotter);
        TestData.addPrescriptionToPatient(oxycontin, hanSolo);
        TestData.addPrescriptionToPatient(lortab, hanSolo);
        TestData.addPrescriptionToPatient(aspirin, bellaSwan);
        TestData.addPrescriptionToPatient(lortab, martyMcFly);
        TestData.addPrescriptionToPatient(oxycontin, martyMcFly);
        TestData.addPrescriptionToPatient(lortab, mickeyMouse);
        TestData.addPrescriptionToPatient(lortab, minnieMouse);
        TestData.addPrescriptionToPatient(lortab, donaldDuck);
        TestData.addPrescriptionToPatient(tylenol, donaldDuck);
        TestData.addPrescriptionToPatient(lortab, daiseyDuck);

        for (int i = 0; i < 70; i++) {
            TestData.addPainLogToPatient(TestData.randomPainLog(), harryPotter);
            for (Medication medication : harryPotter.getPrescriptions())
                TestData.addMedLogToPatient(TestData.randomMedLog(medication), harryPotter);
        }
        harryPotter = symptomManagementApi.updatePatient(harryPotter.getId(), harryPotter);
        TestData.resetRandomDate();

        for (int i = 0; i < 70; i++) {
            TestData.addPainLogToPatient(TestData.randomPainLog(), hanSolo);
            for (Medication medication : hanSolo.getPrescriptions())
                TestData.addMedLogToPatient(TestData.randomMedLog(medication), hanSolo);
        }
        hanSolo = symptomManagementApi.updatePatient(hanSolo.getId(), hanSolo);
        TestData.resetRandomDate();

        for (int i = 0; i < 70; i++) {
            TestData.addPainLogToPatient(TestData.randomPainLog(), bellaSwan);
            for (Medication medication : bellaSwan.getPrescriptions())
                TestData.addMedLogToPatient(TestData.randomMedLog(medication), bellaSwan);
        }
        bellaSwan = symptomManagementApi
                .updatePatient(bellaSwan.getId(), bellaSwan);
        TestData.resetRandomDate();

        for (int i = 0; i < 70; i++) {
            TestData.addPainLogToPatient(TestData.randomPainLog(), martyMcFly);
            for (Medication medication : martyMcFly.getPrescriptions())
                TestData.addMedLogToPatient(TestData.randomMedLog(medication), martyMcFly);
        }
        martyMcFly = symptomManagementApi
                .updatePatient(martyMcFly.getId(), martyMcFly);
        TestData.resetRandomDate();

        for (int i = 0; i < 70; i++) {
            TestData.addPainLogToPatient(TestData.randomPainLog(), minnieMouse);
            for (Medication medication : minnieMouse.getPrescriptions())
                TestData.addMedLogToPatient(TestData.randomMedLog(medication), minnieMouse);
        }
        minnieMouse = symptomManagementApi
                .updatePatient(minnieMouse.getId(), minnieMouse);
        TestData.resetRandomDate();

        for (int i = 0; i < 70; i++) {
            TestData.addPainLogToPatient(TestData.randomPainLog(), mickeyMouse);
            for (Medication medication : mickeyMouse.getPrescriptions())
                TestData.addMedLogToPatient(TestData.randomMedLog(medication), mickeyMouse);
        }
        mickeyMouse = symptomManagementApi
                .updatePatient(mickeyMouse.getId(), mickeyMouse);
        TestData.resetRandomDate();

        for (int i = 0; i < 70; i++) {
            TestData.addPainLogToPatient(TestData.randomPainLog(), donaldDuck);
            for (Medication medication : donaldDuck.getPrescriptions())
                TestData.addMedLogToPatient(TestData.randomMedLog(medication), donaldDuck);
        }
        donaldDuck = symptomManagementApi
                .updatePatient(donaldDuck.getId(), donaldDuck);
        TestData.resetRandomDate();

        for (int i = 0; i < 70; i++) {
            TestData.addPainLogToPatient(TestData.randomPainLog(), daiseyDuck);
            for (Medication medication : daiseyDuck.getPrescriptions())
                TestData.addMedLogToPatient(TestData.randomMedLog(medication), daiseyDuck);
        }
        daiseyDuck = symptomManagementApi
                .updatePatient(daiseyDuck.getId(), daiseyDuck);
        TestData.resetRandomDate();
    }
}
