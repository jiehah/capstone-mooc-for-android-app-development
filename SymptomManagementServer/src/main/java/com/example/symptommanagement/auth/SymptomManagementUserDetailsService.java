package com.example.symptommanagement.auth;

import com.example.symptommanagement.repository.UserCredential;
import com.example.symptommanagement.repository.UserCredentialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Custom implementation of the UserDetailsService interface.
 * This class is responsible for loading user details for the Spring Security framework.
 */
@Service
public class SymptomManagementUserDetailsService implements UserDetailsService {

    // Logger for logging debug and error messages
    static final Logger logger = LoggerFactory.getLogger(SymptomManagementUserDetailsService.class);

    // Hard-coded admin username and password for demonstration purposes
    public static final String adminUsername = "admin";
    public static final String adminPassword = "pass";

    // The repository for managing user credentials (database access)
    private final UserCredentialRepository userCredentialRepository;

    /**
     * Constructor for SymptomManagementUserDetailsService.
     *
     * @param userCredentialRepository The UserCredentialRepository instance for managing user credentials.
     */
    public SymptomManagementUserDetailsService(UserCredentialRepository userCredentialRepository) {
        this.userCredentialRepository = userCredentialRepository;
    }

    /**
     * Load the user details by the given username.
     *
     * @param username The username for which the details should be loaded.
     * @return The UserDetails object containing the details of the user.
     * @throws UsernameNotFoundException If the user is not found or there is an error during the retrieval.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Validate the provided username
        if (username == null || username.isEmpty())
            throw new UsernameNotFoundException("Invalid user name. User name is null or empty");

        logger.debug("USERNAME is: " + username);

        Collection<UserCredential> userCredentials = null;

        // For the hard-coded admin user, create a new UserCredential with admin role
        if (username.toLowerCase().contentEquals("admin")) {
            userCredentials = new HashSet<>();
            UserCredential adminCredential = new UserCredential();
            adminCredential.setId(null);
            adminCredential.setUserName(adminUsername);
            adminCredential.setPassword(adminPassword);
            adminCredential.setUserRole(UserCredential.UserRole.ADMIN);
            userCredentials.add(adminCredential);
        } else {
            // Load user credentials based on the username from the repository (database)
            try {
                if (userCredentialRepository == null) {
                    // In case the userCredentialRepository is not properly autowired, use hard-coding
                    logger.error("ARGH! WHY IS AUTOWIRED NOT WORKING!!!! ... USING HARD-CODING");
                    if (username.startsWith("d") || username.startsWith("e")
                            || username.startsWith("f") || username.startsWith("g")) {
                        userCredentials = new HashSet<>();
                        UserCredential physicianCredential = new UserCredential();
                        physicianCredential.setId(null);
                        physicianCredential.setUserName(username);
                        physicianCredential.setPassword(adminPassword);
                        physicianCredential.setUserRole(UserCredential.UserRole.PHYSICIAN);
                        userCredentials.add(physicianCredential);
                    } else { // All other names are patients
                        userCredentials = new HashSet<>();
                        UserCredential patientCredential = new UserCredential();
                        patientCredential.setId(null);
                        patientCredential.setUserName(username);
                        patientCredential.setPassword(adminPassword);
                        patientCredential.setUserRole(UserCredential.UserRole.PATIENT);
                        userCredentials.add(patientCredential);
                    }

                } else {
                    // Load user credentials from the database using the repository
                    logger.debug("OMG this is working... I have credentials!");
                    userCredentials = userCredentialRepository.findByUserName(username);
                }
            } catch (Exception e) {
                logger.error("Unable to find Credentials for " + username + " error is " + e.getMessage());
            }
        }

        // Validate and prepare user details based on the user credentials
        if (userCredentials == null || userCredentials.size() == 0) {
            throw new UsernameNotFoundException("User details not found with this username: " + username);
        }
        if (userCredentials.size() > 1) {
            throw new UsernameNotFoundException("ERROR Too Many User details found with this username: "
                    + username + " It should be unique.");
        }

        // Find the authorities related to the user role
        UserCredential[] creds = userCredentials.toArray(new UserCredential[0]);
        List<String> authList = getAuthorities(creds[0].getUserRole());

        logger.debug("Creating UserDetails for : " + username + " password " + creds[0].getPassword());

        // Create the UserDetails object for the user with the specified username and password
        if (authList == null) {
            return User.create(username, creds[0].getPassword());
        }
        return User.create(username, creds[0].getPassword(), authList.toArray(new String[0]));
    }

    /**
     * Get the list of authorities (roles) based on the user role.
     *
     * @param role The UserRole enum representing the user role.
     * @return The List of authorities (roles) for the user.
     */
    private List<String> getAuthorities(UserCredential.UserRole role) {
        logger.debug("Setting up the User Authorities list for User Role of " + role);

        if (role == null || role == UserCredential.UserRole.NOT_ASSIGNED) {
            return null;
        }

        List<String> authList = new ArrayList<>();
        // Map the user role to the corresponding authorities (roles)
        switch (role) {
            case ADMIN: {
                authList.add("ROLE_ADMIN");
                authList.add("ROLE_PATIENT");
                authList.add("ROLE_PHYSICIAN");
                break;
            }
            case PATIENT: {
                authList.add("ROLE_PATIENT");
                break;
            }
            case PHYSICIAN: {
                authList.add("ROLE_PHYSICIAN");
                break;
            }
            default:
                return null;
        }
        return authList;
    }
}
