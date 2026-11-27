package com.hendisantika.springsecurityauthserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SpringSecurityAuthServerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void openIdConfigurationIsExposed() throws Exception {
        mockMvc.perform(get("/.well-known/openid-configuration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorization_endpoint").exists())
                .andExpect(jsonPath("$.token_endpoint").exists());
    }

    @Test
    void restApiRequiresAccessToken() throws Exception {
        mockMvc.perform(get("/rest/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void restApiAcceptsJwt() throws Exception {
        mockMvc.perform(get("/rest/hello/principal").with(jwt().jwt(j -> j.subject("hendi"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("hendi"));
    }

    @Test
    void seededUserCanLogin() throws Exception {
        mockMvc.perform(formLogin().user("hendi").password("password"))
                .andExpect(authenticated().withUsername("hendi"));
    }
}
