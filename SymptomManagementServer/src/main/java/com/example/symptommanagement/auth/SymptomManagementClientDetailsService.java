package com.example.symptommanagement.auth;

import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of the ClientDetailsService interface.
 * This class is responsible for managing OAuth2 client details for the Symptom Management application.
 */
@Service
public class SymptomManagementClientDetailsService implements ClientDetailsService {

    // The internal ClientDetailsService instance to manage OAuth2 client details
    private ClientDetailsService clientDetailsService;

    /**
     * Default constructor to initialize the client details for Symptom Management application.
     * The constructor sets up an in-memory ClientDetailsService using InMemoryClientDetailsServiceBuilder.
     * In this case, it configures a single client with ID "mobile" for demonstration purposes.
     * WARNING: In-memory configuration is not suitable for production and should be replaced with a persistent store.
     *
     * @throws Exception If there is an error while building the in-memory client details service.
     */
    public SymptomManagementClientDetailsService() throws Exception {
        clientDetailsService = new InMemoryClientDetailsServiceBuilder()
                .withClient("mobile") // Client ID
                .authorizedGrantTypes("password") // Grant type - password
                .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT") // Client authorities
                .scopes("read", "write") // Allowed scopes
                .accessTokenValiditySeconds(2419200) // Access token validity period (4 weeks)
                .and()
                .build();
    }

    /**
     * Load the client details by the given client ID.
     *
     * @param clientId The client ID for which the details should be loaded.
     * @return The ClientDetails object containing the details of the client.
     * @throws ClientRegistrationException If the client is not found or there is an error during the retrieval.
     */
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return clientDetailsService.loadClientByClientId(clientId);
    }
}
