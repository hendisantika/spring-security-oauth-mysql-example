package com.hendisantika.springsecurityclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-security-client
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Link : s.id/hendisantika
 * Date: 27/11/26
 * Time: 07.56
 */
@Controller
public class SecureController {

    private final RestClient restClient;
    private final String authServerUri;

    public SecureController(RestClient restClient, @Value("${app.auth-server-uri}") String authServerUri) {
        this.restClient = restClient;
        this.authServerUri = authServerUri;
    }

    @GetMapping("/secure")
    public String secure(@AuthenticationPrincipal OidcUser user,
                         @RegisteredOAuth2AuthorizedClient("auth-server") OAuth2AuthorizedClient authorizedClient,
                         Model model) {
        model.addAttribute("user", user);
        model.addAttribute("scopes", authorizedClient.getAccessToken().getScopes());
        try {
            String principal = restClient.get()
                    .uri(authServerUri + "/rest/hello/principal")
                    .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                    .retrieve()
                    .body(String.class);
            model.addAttribute("apiResponse", principal);
        } catch (RestClientException ex) {
            model.addAttribute("apiError", ex.getMessage());
        }
        return "secure";
    }
}
