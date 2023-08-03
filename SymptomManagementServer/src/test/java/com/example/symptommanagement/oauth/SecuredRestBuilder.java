/*
 **
 ** Copyright 2014, Jules White
 **
 **
 */
package com.example.symptommanagement.oauth;

import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import retrofit.*;
import retrofit.RestAdapter.Log;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.*;
import retrofit.client.Client.Provider;
import retrofit.converter.Converter;
import retrofit.mime.FormUrlEncodedTypedOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * The {@code SecuredRestBuilder} class extends the {@link RestAdapter.Builder} class and provides
 * additional functionality for adding OAuth 2.0 authentication to HTTP requests made using Retrofit.
 */
public class SecuredRestBuilder extends RestAdapter.Builder {

    /**
     * The inner class {@code OAuthHandler} implements the {@link RequestInterceptor} interface
     * to handle OAuth 2.0 authentication for HTTP requests made by Retrofit.
     */
    private static class OAuthHandler implements RequestInterceptor {

        /**
         * The Retrofit Client to execute HTTP requests.
         */
        private final Client client;

        /**
         * The endpoint URL to obtain OAuth 2.0 bearer tokens.
         */
        private final String tokenIssuingEndpoint;

        /**
         * OAuth's credentials: username, password, client ID, and client secret.
         */
        private final String username;
        private final String password;
        private final String clientId;
        private final String clientSecret;

        /**
         * Flag to track if the user is logged in and an access token is available.
         */
        private boolean loggedIn = false;

        /**
         * The obtained access token after successful authentication.
         */
        private String accessToken;

        /**
         * Constructs an {@code OAuthHandler} with the necessary OAuth 2.0 authentication parameters.
         *
         * @param client               The HTTP client implementation used to make requests.
         * @param tokenIssuingEndpoint The token issuing endpoint URL for obtaining access tokens.
         * @param username             The username for OAuth 2.0 password grant request.
         * @param password             The password for OAuth 2.0 password grant request.
         * @param clientId             The client ID for OAuth 2.0 password grant request.
         * @param clientSecret         The client secret for OAuth 2.0 password grant request.
         */
        public OAuthHandler(Client client, String tokenIssuingEndpoint, String username,
                            String password, String clientId, String clientSecret) {
            this.client = client;
            this.tokenIssuingEndpoint = tokenIssuingEndpoint;
            this.username = username;
            this.password = password;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        /**
         * This method intercepts HTTP requests made by Retrofit and adds the OAuth 2.0 bearer token
         * to the "Authorization" header of the request. If the token is not yet obtained, it sends
         * a password grant request to the token issuing endpoint to obtain the token.
         *
         * @param request The request facade representing the HTTP request.
         */
        @Override
        public void intercept(RequestFacade request) {
            if (!loggedIn) {
                try {
                    accessToken = getAccessToken(client, tokenIssuingEndpoint, username, password, clientId, clientSecret);
                    request.addHeader("Authorization", "Bearer " + accessToken);
                    loggedIn = true;
                } catch (Exception e) {
                    throw new SecuredRestException(e);
                }
            } else {
                request.addHeader("Authorization", "Bearer " + accessToken);
            }
        }

        /**
         * Sends a password grant request to the token issuing endpoint and obtains the access token.
         *
         * @param client               The HTTP client implementation used to make requests.
         * @param tokenIssuingEndpoint The token issuing endpoint URL for obtaining access tokens.
         * @param username             The username for OAuth 2.0 password grant request.
         * @param password             The password for OAuth 2.0 password grant request.
         * @param clientId             The client ID for OAuth 2.0 password grant request.
         * @param clientSecret         The client secret for OAuth 2.0 password grant request.
         * @return The access token as a string.
         * @throws IOException If an I/O error occurs during the token request.
         */
        public String getAccessToken(Client client, String tokenIssuingEndpoint, String username, String password,
                                     String clientId, String clientSecret) throws IOException {
            // Construct form URL encoded body for the password grant request.
            FormUrlEncodedTypedOutput to = new FormUrlEncodedTypedOutput();
            to.addField("username", username);
            to.addField("password", password);
            to.addField("client_id", clientId);
            to.addField("client_secret", clientSecret);
            to.addField("grant_type", "password");

            // Perform BASIC authentication using Base64-encoded client_id:client_secret.
            String base64Auth = BaseEncoding.base64()
                    .encode((clientId + ":" + clientSecret).getBytes());
            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", "Basic " + base64Auth));

            // Create the password grant request.
            Request req = new Request("POST", tokenIssuingEndpoint, headers, to);

            // Execute the request using the provided client.
            Response resp = client.execute(req);

            // Check if the response is successful and return the access token.
            if (resp.getStatus() < 200 || resp.getStatus() > 299) {
                throw new SecuredRestException("Login failure: "
                        + resp.getStatus() + " - " + resp.getReason());
            } else {
                String body = IOUtils.toString(resp.getBody().in());
                return new Gson().fromJson(body, JsonObject.class).get("access_token").getAsString();
            }
        }
    }

    /**
     * The username for OAuth authentication.
     */
    private String username;

    /**
     * The password for OAuth authentication.
     */
    private String password;

    /**
     * The login endpoint URL for OAuth authentication.
     */
    private String loginUrl;

    /**
     * The client ID for OAuth authentication.
     */
    private String clientId;

    /**
     * The client secret for OAuth authentication.
     */
    private String clientSecret = "";

    /**
     * The client used for executing HTTP requests.
     */
    private Client client;

    /**
     * Method to set the login endpoint URL for OAuth authentication.
     *
     * @param endpoint The login endpoint URL.
     * @return The current SecuredRestBuilder instance.
     */
    public SecuredRestBuilder setLoginEndpoint(String endpoint) {
        loginUrl = endpoint;
        return this;
    }

    /**
     * Method to set the base URL of the remote service for regular REST calls.
     *
     * @param endpoint The base URL of the remote service.
     * @return The current SecuredRestBuilder instance.
     */
    @Override
    public SecuredRestBuilder setEndpoint(String endpoint) {
        return (SecuredRestBuilder) super.setEndpoint(endpoint);
    }

    /**
     * Method to set the base URL of the remote service for regular REST calls.
     *
     * @param endpoint The base URL of the remote service.
     * @return The current SecuredRestBuilder instance.
     */
    @Override
    public SecuredRestBuilder setEndpoint(Endpoint endpoint) {
        return (SecuredRestBuilder) super.setEndpoint(endpoint);
    }

    /**
     * Sets the HTTP client implementation to be used for making requests.
     *
     * @param client The HTTP client implementation.
     * @return The current {@code SecuredRestBuilder} instance.
     */
    @Override
    public SecuredRestBuilder setClient(Client client) {
        this.client = client;
        return (SecuredRestBuilder) super.setClient(client);
    }

    /**
     * Sets the provider for obtaining the HTTP client implementation to be used for making requests.
     *
     * @param clientProvider The provider for obtaining the HTTP client implementation.
     * @return The current {@code SecuredRestBuilder} instance.
     */
    @Override
    public SecuredRestBuilder setClient(Provider clientProvider) {
        client = clientProvider.get();
        return (SecuredRestBuilder) super.setClient(clientProvider);
    }

    /**
     * Sets the error handler for handling errors that occur during HTTP requests.
     *
     * @param errorHandler The error handler.
     * @return The current {@code SecuredRestBuilder} instance.
     */
    @Override
    public SecuredRestBuilder setErrorHandler(ErrorHandler errorHandler) {
        return (SecuredRestBuilder) super.setErrorHandler(errorHandler);
    }

    /**
     * Sets the executors for managing the threads used for making HTTP requests.
     *
     * @param httpExecutor     The executor for executing HTTP requests.
     * @param callbackExecutor The executor for executing callback functions.
     * @return The current {@code SecuredRestBuilder} instance.
     */
    @Override
    public SecuredRestBuilder setExecutors(Executor httpExecutor, Executor callbackExecutor) {
        return (SecuredRestBuilder) super.setExecutors(httpExecutor, callbackExecutor);
    }

    /**
     * Sets the request interceptor for adding additional information to HTTP requests.
     *
     * @param requestInterceptor The request interceptor.
     * @return The current {@code SecuredRestBuilder} instance.
     */
    @Override
    public SecuredRestBuilder setRequestInterceptor(RequestInterceptor requestInterceptor) {
        return (SecuredRestBuilder) super.setRequestInterceptor(requestInterceptor);
    }

    /**
     * Sets the converter for converting between Java objects and JSON representations.
     *
     * @param converter The converter.
     * @return The current {@code SecuredRestBuilder} instance.
     */
    @Override
    public SecuredRestBuilder setConverter(Converter converter) {
        return (SecuredRestBuilder) super.setConverter(converter);
    }

    /**
     * Sets the profiler for measuring the performance of HTTP requests.
     *
     * @param profiler The profiler.
     * @return The current {@code SecuredRestBuilder} instance.
     */
    @Override
    public SecuredRestBuilder setProfiler(Profiler profiler) {
        return (SecuredRestBuilder) super.setProfiler(profiler);
    }

    /**
     * Sets the log for logging HTTP requests and responses.
     *
     * @param log The log.
     * @return The current {@code SecuredRestBuilder} instance.
     */
    @Override
    public SecuredRestBuilder setLog(Log log) {
        return (SecuredRestBuilder) super.setLog(log);
    }

    /**
     * Sets the log level for determining the amount of logging.
     *
     * @param logLevel The log level.
     * @return The current {@code SecuredRestBuilder} instance.
     */
    @Override
    public SecuredRestBuilder setLogLevel(LogLevel logLevel) {
        return (SecuredRestBuilder) super.setLogLevel(logLevel);
    }

    /**
     * Sets the username for OAuth 2.0 password grant request.
     *
     * @param username The username.
     * @return The current {@code SecuredRestBuilder} instance.
     */
    public SecuredRestBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * Sets the password for OAuth 2.0 password grant request.
     *
     * @param password The password.
     * @return The current {@code SecuredRestBuilder} instance.
     */
    public SecuredRestBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * Sets the client ID for OAuth 2.0 password grant request.
     *
     * @param clientId The client ID.
     * @return The current {@code SecuredRestBuilder} instance.
     */
    public SecuredRestBuilder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    /**
     * Sets the client secret for OAuth 2.0 password grant request.
     *
     * @param clientSecret The client secret.
     * @return The current {@code SecuredRestBuilder} instance.
     */
    public SecuredRestBuilder setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    /**
     * Overrides the {@link RestAdapter.Builder#build()} method to add OAuth 2.0 authentication
     * request interceptor before building the {@link RestAdapter}.
     *
     * @return The constructed {@link RestAdapter} with OAuth 2.0 authentication.
     * @throws SecuredRestException If either the username or password is null.
     */
    @Override
    public RestAdapter build() {
        if (username == null || password == null) {
            throw new SecuredRestException(
                    "You must specify both a username and password for a "
                            + "SecuredRestBuilder before calling the build() method.");
        }

        if (client == null) {
            client = new OkClient();
        }
        OAuthHandler oAuthHandler = new OAuthHandler(client, loginUrl, username, password, clientId, clientSecret);
        setRequestInterceptor(oAuthHandler);

        return super.build();
    }
}
