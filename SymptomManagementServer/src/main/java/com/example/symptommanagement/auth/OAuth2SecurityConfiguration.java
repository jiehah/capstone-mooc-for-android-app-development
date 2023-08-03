package com.example.symptommanagement.auth;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

import java.io.File;

/**
 * Configuration class for OAuth2 Security settings.
 */
@Configuration
public class OAuth2SecurityConfiguration {

    /**
     * Configuration class for Web Security.
     */
    @Configuration
    @EnableWebSecurity
    protected static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        // The UserDetailsService instance to manage user details
        private final UserDetailsService userDetailsService;

        /**
         * Constructor for WebSecurityConfiguration.
         *
         * @param userDetailsService The UserDetailsService instance to manage user details.
         */
        public WebSecurityConfiguration(UserDetailsService userDetailsService) {
            this.userDetailsService = userDetailsService;
        }

        /**
         * Register the UserDetailsService for authentication manager.
         *
         * @param authenticationManagerBuilder The AuthenticationManagerBuilder instance.
         * @throws Exception If there is an error during authentication manager registration.
         */
        @Autowired
        protected void registerAuthentication(final AuthenticationManagerBuilder authenticationManagerBuilder)
                throws Exception {
            authenticationManagerBuilder.userDetailsService(userDetailsService);
        }

        /**
         * Create and expose the AuthenticationManager as a bean.
         *
         * @return The AuthenticationManager instance.
         * @throws Exception If there is an error while creating the AuthenticationManager.
         */
        @Override
        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    /**
     * Configuration class for Resource Server (OAuth2 Resource Server).
     */
    @Configuration
    @EnableResourceServer
    protected static class ResourceServer extends ResourceServerConfigurerAdapter {

        /**
         * Configure the HttpSecurity for the resource server.
         *
         * @param httpSecurity The HttpSecurity instance to be configured.
         * @throws Exception If there is an error during HttpSecurity configuration.
         */
        @Override
        public void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity.csrf().disable();
            // Allow anonymous access to "/oauth/token" endpoint
            httpSecurity.authorizeRequests().antMatchers("/oauth/token").anonymous();
            // Require "read" scope for all HTTP GET requests
            httpSecurity.authorizeRequests().antMatchers(HttpMethod.GET, "/**").access("#oauth2.hasScope('read')");
            // Require "write" scope for all other HTTP requests
            httpSecurity.authorizeRequests().antMatchers("/**").access("#oauth2.hasScope('write')");
        }
    }

    /**
     * Configuration class for Authorization Server (OAuth2 Authorization Server).
     */
    @Configuration
    @EnableAuthorizationServer
    @Order(Ordered.LOWEST_PRECEDENCE - 100)
    protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

        // The AuthenticationManager instance used for OAuth2 authentication
        private final AuthenticationManager authenticationManager;

        // The custom ClientDetailsService implementation to manage OAuth2 client details
        private final SymptomManagementClientDetailsService clientDetailsService;

        /**
         * Constructor for OAuth2Config.
         *
         * @param authenticationManager The AuthenticationManager instance used for OAuth2 authentication.
         * @param clientDetailsService  The SymptomManagementClientDetailsService instance to manage OAuth2 client details.
         */
        public OAuth2Config(AuthenticationManager authenticationManager,
                            SymptomManagementClientDetailsService clientDetailsService) {
            this.authenticationManager = authenticationManager;
            this.clientDetailsService = clientDetailsService;
        }

        /**
         * Configure the AuthorizationServerEndpointsConfigurer with the AuthenticationManager.
         *
         * @param authorizationServerEndpointsConfigurer The AuthorizationServerEndpointsConfigurer instance to be configured.
         */
        @Override
        public void configure(AuthorizationServerEndpointsConfigurer authorizationServerEndpointsConfigurer) {
            authorizationServerEndpointsConfigurer.authenticationManager(authenticationManager);
        }

        /**
         * Configure the ClientDetailsServiceConfigurer with the custom ClientDetailsService.
         *
         * @param clientDetailsServiceConfigurer The ClientDetailsServiceConfigurer instance to be configured.
         * @throws Exception If there is an error during ClientDetailsService configuration.
         */
        @Override
        public void configure(ClientDetailsServiceConfigurer clientDetailsServiceConfigurer) throws Exception {
            clientDetailsServiceConfigurer.withClientDetails(clientDetailsService);
        }
    }

    /**
     * Bean definition for the Tomcat Servlet Web Server Factory.
     * This bean is used to customize the Tomcat embedded web server for HTTPS support.
     *
     * @param keystoreFile The path to the keystore file.
     * @param keystorePass The keystore password.
     * @return The TomcatServletWebServerFactory instance with HTTPS configuration.
     */
    @Bean
    TomcatServletWebServerFactory servletContainer(
            @Value("${keystore.file:src/main/resources/private/keystore}") String keystoreFile,
            @Value("${keystore.pass:changeit}") final String keystorePass) {

        final String absoluteKeystoreFile = new File(keystoreFile).getAbsolutePath();

        return new TomcatServletWebServerFactory() {
            @Override
            protected void customizeConnector(Connector connector) {
                // Configure the embedded Tomcat connector for HTTPS
                connector.setPort(8443);
                connector.setSecure(true);
                connector.setScheme("https");

                Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
                proto.setSSLEnabled(true);
                proto.setKeystoreFile(absoluteKeystoreFile);
                proto.setKeystorePass(keystorePass);
                proto.setKeystoreType("JKS");
                proto.setKeyAlias("tomcat");
            }
        };
    }

    /**
     * Bean definition for the PasswordEncoder.
     * This bean is used to specify the password encoder used for authentication.
     * WARNING: NoOpPasswordEncoder is not secure and should not be used in production.
     * It is used here only for demonstration purposes.
     *
     * @return The PasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
