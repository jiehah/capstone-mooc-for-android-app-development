package com.example.symptommanagement.repository;

import com.example.symptommanagement.client.SymptomManagementApi;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;

/**
 * {@link MedicationRepository} is a Spring Data MongoDB repository interface
 * responsible for managing the persistence and retrieval of {@link Medication} entities.
 * It provides CRUD (Create, Read, Update, Delete) operations for the Medication entity
 * and interacts with the MongoDB database.
 *
 * The repository is exposed as a REST resource through Spring Data REST.
 * The base path for accessing medications is defined as "/api/medications".
 * The repository supports standard CRUD operations, allowing clients to perform
 * operations on the Medication entity over HTTP.
 */
@RepositoryRestResource(path = SymptomManagementApi.MEDICATION_PATH)
public interface MedicationRepository extends MongoRepository<Medication, String> {

    /**
     * Retrieves a collection of medications with names similar to the specified name.
     *
     * @param name The name of the medication to search for.
     * @return A collection of medications with names similar to the specified name.
     */
    Collection<Medication> findByName(@Param(SymptomManagementApi.NAME_PARAMETER) String name);
}
