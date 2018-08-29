package com.hendisantika.springsecurityauthserver.repository;

import com.hendisantika.springsecurityauthserver.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-security-auth-server
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 30/08/18
 * Time: 05.21
 * To change this template use File | Settings | File Templates.
 */
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByName(String username);
}