package com.example.symptommanagement.integration.test;

import com.example.symptommanagement.Application;
import com.example.symptommanagement.client.SymptomManagementApi;
import com.example.symptommanagement.repository.Medication;
import com.example.symptommanagement.repository.Patient;
import com.example.symptommanagement.repository.Physician;
import com.example.symptommanagement.testdata.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for SymptomManagementApi client.
 * Tests the functionality of adding and listing patients, physicians, and medications.
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class SymptomManagementIntegrationTest {

    /**
     * MockMvc instance used for testing.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Access token obtained for authentication.
     */
    private String accessToken;

    /**
     * Set up the test environment and obtain an access token for authentication.
     *
     * @throws Exception if there are errors during setup.
     */
    @Before
    public void setUp() throws Exception {
        ResultActions perform = mockMvc.perform(post(SymptomManagementApi.TOKEN_PATH)
                .with(httpBasic("mobile", ""))
                .param("username", "admin")
                .param("password", "pass")
                .param("grant_type", "password"));

        accessToken = (String) new Jackson2JsonParser()
                .parseMap(perform.andReturn()
                        .getResponse()
                        .getContentAsString())
                .get("access_token");
    }

    /**
     * Test the functionality of adding and listing patients, physicians, and medications.
     *
     * @throws Exception if there are errors during the test.
     */
    @Test
    public void testVideoAddAndList() throws Exception {
        Patient randomPatient = TestData.randomPatient("Donald", "Duck", "12/11/1944");
        Physician randomPhysician = TestData.randomPhysician("Minnie", "Mouse");
        Medication randomMedication = TestData.randomMedication("hugs");
        String patientJson = TestData.toJson(randomPatient);
        String physicianJson = TestData.toJson(randomPhysician);
        String medicationJson = TestData.toJson(randomMedication);

        mockMvc.perform(post(SymptomManagementApi.PATIENT_PATH)
                        .with(user("admin").password("pass").roles("ADMIN"))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get(SymptomManagementApi.PATIENT_PATH)
                        .with(user("admin").password("pass").roles("ADMIN"))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Donald")))
                .andReturn();

        mockMvc.perform(post(SymptomManagementApi.PHYSICIAN_PATH)
                        .with(user("admin").password("pass").roles("ADMIN"))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(physicianJson))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get(SymptomManagementApi.PHYSICIAN_PATH)
                        .with(user("admin").password("pass").roles("ADMIN"))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Minnie")))
                .andReturn();

        mockMvc.perform(post(SymptomManagementApi.MEDICATION_PATH)
                        .with(user("admin").password("pass").roles("ADMIN"))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(medicationJson))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get(SymptomManagementApi.MEDICATION_PATH)
                        .with(user("admin").password("pass").roles("ADMIN"))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hugs")))
                .andReturn();
    }
}
