package com.example.symptommanagement.repository;

import com.example.symptommanagement.client.SymptomManagementApi;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;

/**
 * {@link PatientRepository} is a Spring Data MongoDB repository interface
 * responsible for managing the persistence and retrieval of {@link Patient} entities.
 * It provides CRUD (Create, Read, Update, Delete) operations for the Patient entity
 * and interacts with the MongoDB database.
 *
 * The repository is exposed as a REST resource through Spring Data REST.
 * The base path for accessing patients is defined as "/api/patients".
 * The repository supports standard CRUD operations, allowing clients to perform
 * operations on the Patient entity over HTTP.
 */
@RepositoryRestResource(path = SymptomManagementApi.PATIENT_PATH)
public interface PatientRepository extends MongoRepository<Patient, String> {

    /**
     * Retrieves a collection of patients with the specified last name.
     *
     * @param lastName The last name of the patients to search for.
     * @return A collection of patients with the specified last name.
     */
    Collection<Patient> findByLastName(@Param(SymptomManagementApi.NAME_PARAMETER) String lastName);
}

