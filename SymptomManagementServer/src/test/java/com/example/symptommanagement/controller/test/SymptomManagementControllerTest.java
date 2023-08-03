package com.example.symptommanagement.controller.test;

import com.example.symptommanagement.controller.SymptomManagementController;
import com.example.symptommanagement.repository.*;
import com.example.symptommanagement.testdata.TestData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * This class contains test cases for the SymptomManagementController class.
 */
public class SymptomManagementControllerTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PhysicianRepository physicianRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private UserCredentialRepository userCredentialRepository;

    @InjectMocks
    private SymptomManagementController symptomManagementController;

    private final Patient randomPatient = TestData.randomPatient("Donald", "Duck", "12/11/1944");
    private final Physician randomPhysician = TestData.randomPhysician("Minnie", "Mouse");
    private final Medication randomMedication = TestData.randomMedication("hugs");

    @Before
    public void setUp() {
        // Initialize mock objects and set up behavior for repository calls
        MockitoAnnotations.initMocks(this);
        when(userCredentialRepository.save(any())).thenReturn(null);
        when(patientRepository.save(randomPatient)).thenReturn(randomPatient);
        when(physicianRepository.save(randomPhysician)).thenReturn(randomPhysician);
        when(medicationRepository.save(randomMedication)).thenReturn(randomMedication);
        when(patientRepository.findAll()).thenReturn(Collections.singletonList(randomPatient));
        when(physicianRepository.findAll()).thenReturn(Collections.singletonList(randomPhysician));
        when(medicationRepository.findAll()).thenReturn(Collections.singletonList(randomMedication));
    }

    /**
     * This test case validates the addPatient, getPatientList, addPhysician, getPhysicianList,
     * addMedication, and getMedicationList methods of the SymptomManagementController class.
     */
    @Test
    public void testPatientPhysicianMedication() {
        // Add a random patient and make sure it is not null
        Patient addedPatient = symptomManagementController.addPatient(randomPatient);
        assertNotNull("The added patient should not be null.", addedPatient);

        // Get the list of patients and check if the added patient is present in the list
        Collection<Patient> patients = symptomManagementController.getPatientList();
        assertTrue("The list of patients should contain the added patient.", patients.contains(addedPatient));

        // Add a random physician and make sure it is not null
        Physician addedPhysician = symptomManagementController.addPhysician(randomPhysician);
        assertNotNull("The added physician should not be null.", addedPhysician);

        // Get the list of physicians and check if the added physician is present in the list
        Collection<Physician> physicians = symptomManagementController.getPhysicianList();
        assertTrue("The list of physicians should contain the added physician.", physicians.contains(addedPhysician));

        // Add a random medication and make sure it is not null
        Medication addedMedication = symptomManagementController.addMedication(randomMedication);
        assertNotNull("The added medication should not be null.", addedMedication);

        // Get the list of medications and check if the added medication is present in the list
        Collection<Medication> medications = symptomManagementController.getMedicationList();
        assertTrue("The list of medications should contain the added medication.", medications.contains(addedMedication));
    }
}
