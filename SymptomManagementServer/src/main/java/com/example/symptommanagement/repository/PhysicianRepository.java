package com.example.symptommanagement.repository;

import com.example.symptommanagement.client.SymptomManagementApi;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;

/**
 * {@link PhysicianRepository} is a Spring Data MongoDB repository interface
 * responsible for managing the persistence and retrieval of {@link Physician} entities.
 * It provides CRUD (Create, Read, Update, Delete) operations for the Physician entity
 * and interacts with the MongoDB database.
 *
 * The repository is exposed as a REST resource through Spring Data REST.
 * The base path for accessing physicians is defined as "/api/physicians".
 * The repository supports standard CRUD operations, allowing clients to perform
 * operations on the Physician entity over HTTP.
 */
@RepositoryRestResource(path = SymptomManagementApi.PHYSICIAN_PATH)
public interface PhysicianRepository extends MongoRepository<Physician, String> {

    /**
     * Retrieves a collection of physicians with the specified last name.
     *
     * @param lastName The last name of the physicians to search for.
     * @return A collection of physicians with the specified last name.
     */
    Collection<Physician> findByLastName(@Param(SymptomManagementApi.NAME_PARAMETER) String lastName);
}

