package com.hendisantika.springsecurityauthserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-security-auth-server
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 30/08/18
 * Time: 05.23
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/rest/hello")
public class HelloController {

    @GetMapping("/principal")
    public Principal user(Principal principal) {
        return principal;
    }

    @GetMapping
    public String hello() {
        return "Hello World";
    }

}

