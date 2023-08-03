package com.example.symptommanagement.repository;

import com.example.symptommanagement.client.SymptomManagementApi;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collection;

/**
 * {@link AlertRepository} is a Spring Data MongoDB repository interface
 * that manages the persistence and retrieval of {@link Alert} entities.
 * It provides CRUD (Create, Read, Update, Delete) operations for the
 * Alert entity and interacts with the MongoDB database.
 *
 * The repository is exposed as a REST resource through Spring Data REST.
 * The base path for accessing alerts is defined as "/api/alerts".
 * The repository supports standard CRUD operations, allowing clients
 * to perform operations on the Alert entity over HTTP.
 */
@RepositoryRestResource(path = SymptomManagementApi.ALERT_PATH)
public interface AlertRepository extends MongoRepository<Alert, String> {

    /**
     * Retrieves a collection of alerts associated with a specific physician.
     *
     * @param id The identifier of the physician to filter alerts by.
     * @return A collection of alerts associated with the specified physician.
     */
    Collection<Alert> findByPhysicianId(@PathVariable(SymptomManagementApi.ID_PARAMETER) String id);
}

