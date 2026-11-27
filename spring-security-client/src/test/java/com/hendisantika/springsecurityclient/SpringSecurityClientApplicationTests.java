package com.hendisantika.springsecurityclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(properties = "app.auth-server-uri=http://127.0.0.1:1")
@AutoConfigureMockMvc
class SpringSecurityClientApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void indexIsPublic() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Login to OAuth here")));
    }

    @Test
    void secureRedirectsToAuthorizationServer() throws Exception {
        mockMvc.perform(get("/secure"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/oauth2/authorization/auth-server"));
    }

    @Test
    void secureRendersForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/secure").with(oidcLogin().idToken(token -> token.subject("hendi"))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hendi")));
    }
}
