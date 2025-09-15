package com.example.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

/**
 * OAUTH - OAuth2 configuration for third-party authentication
 * TECHNICAL CONCEPT: OAUTH
 */
@Configuration
public class OAuth2Config {

    // OAUTH - Client registration repository for OAuth2 providers
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(
            googleClientRegistration(),  // OAUTH - Google OAuth2 client
            githubClientRegistration()   // OAUTH - GitHub OAuth2 client
        );
    }

    // OAUTH - Google OAuth2 client registration
    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
                .clientId("your-google-client-id")  // OAUTH - Google client ID
                .clientSecret("your-google-client-secret")  // OAUTH - Google client secret
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)  // OAUTH - Authentication method
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)  // OAUTH - Grant type
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")  // OAUTH - Redirect URI
                .scope("openid", "profile", "email")  // OAUTH - Requested scopes
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")  // OAUTH - Authorization endpoint
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")  // OAUTH - Token endpoint
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")  // OAUTH - User info endpoint
                .userNameAttributeName("sub")  // OAUTH - Username attribute
                .clientName("Google")  // OAUTH - Client name
                .build();
    }

    // OAUTH - GitHub OAuth2 client registration
    private ClientRegistration githubClientRegistration() {
        return ClientRegistration.withRegistrationId("github")
                .clientId("your-github-client-id")  // OAUTH - GitHub client ID
                .clientSecret("your-github-client-secret")  // OAUTH - GitHub client secret
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)  // OAUTH - Authentication method
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)  // OAUTH - Grant type
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")  // OAUTH - Redirect URI
                .scope("read:user", "user:email")  // OAUTH - Requested scopes
                .authorizationUri("https://github.com/login/oauth/authorize")  // OAUTH - Authorization endpoint
                .tokenUri("https://github.com/login/oauth/access_token")  // OAUTH - Token endpoint
                .userInfoUri("https://api.github.com/user")  // OAUTH - User info endpoint
                .userNameAttributeName("id")  // OAUTH - Username attribute
                .clientName("GitHub")  // OAUTH - Client name
                .build();
    }
}
