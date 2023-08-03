package com.example.symptommanagement.repository;

import com.example.symptommanagement.client.SymptomManagementApi;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;

/**
 * {@link UserCredentialRepository} is a Spring Data MongoDB repository interface
 * responsible for managing the persistence and retrieval of {@link UserCredential} entities.
 * It provides CRUD (Create, Read, Update, Delete) operations for the UserCredential entity
 * and interacts with the MongoDB database.
 * <p>
 * The repository is exposed as a REST resource through Spring Data REST.
 * The base path for accessing user credentials is defined as "/api/credentials".
 * The repository supports standard CRUD operations, allowing clients to perform
 * operations on the UserCredential entity over HTTP.
 */
@RepositoryRestResource(path = SymptomManagementApi.CREDENTIAL_PATH)
public interface UserCredentialRepository extends MongoRepository<UserCredential, String> {

    /**
     * Retrieves a collection of user credentials with the specified user name.
     *
     * @param userName The user name to search for.
     * @return A collection of user credentials with the specified user name.
     */
    Collection<UserCredential> findByUserName(@Param(SymptomManagementApi.NAME_PARAMETER) String userName);
}

