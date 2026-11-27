# spring-security-oauth-mysql-example

[![Java CI with Maven](https://github.com/hendisantika/spring-security-oauth-mysql-example/actions/workflows/maven.yml/badge.svg)](https://github.com/hendisantika/spring-security-oauth-mysql-example/actions/workflows/maven.yml)
![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.2-6DB33F?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)

Spring Security OAuth2 example with users stored in MySQL. It consists of two Spring Boot applications:

| Module                        | Port   | Context path | Description                                                                                          |
|-------------------------------|--------|--------------|------------------------------------------------------------------------------------------------------|
| `spring-security-auth-server` | `8081` | `/auth`      | OAuth2 Authorization Server + OpenID Connect provider (Spring Authorization Server), users in MySQL. Also exposes a JWT-protected REST API. |
| `spring-security-client`      | `8082` | `/ui`        | Thymeleaf web app that signs users in with `oauth2Login()` and calls the auth server API with the access token. |

## Tech stack

- Java 25
- Spring Boot 4.2 / Spring Security 7 (Spring Authorization Server, OAuth2 Client, OAuth2 Resource Server)
- Spring Data JPA + MySQL 8
- Thymeleaf + `thymeleaf-extras-springsecurity6`
- Maven Wrapper 3.9
- JUnit 5, MockMvc, H2 (tests only)

## How it works

```
Browser ──► Client (127.0.0.1:8082/ui) ──(1) redirect──► Auth Server (localhost:8081/auth)
                                                          │  /login (form login, users from MySQL)
                                                          │  /oauth2/authorize  (authorization code + PKCE)
   ◄──────────────(2) redirect with code──────────────────┘
Client ──(3) POST /oauth2/token (client_secret_basic)──► Auth Server  ──► access token + ID token (JWT)
Client ──(4) GET  /userinfo, /rest/hello/principal (Bearer token)──► Auth Server
```

1. Opening `/ui/secure` on the client redirects to the auth server's `/oauth2/authorize`.
2. The user signs in on the auth server login page (credentials checked against the `users` table).
3. The client exchanges the authorization code for tokens.
4. The secured page shows the user, the granted scopes and the response of the JWT-protected
   `GET /auth/rest/hello/principal` endpoint.

### Registered client

| Property      | Value                                                       |
|---------------|-------------------------------------------------------------|
| Client ID     | `ClientId`                                                  |
| Client secret | `secret`                                                    |
| Grant types   | `authorization_code`, `refresh_token`                       |
| Scopes        | `openid`, `profile`, `user_info`                            |
| Redirect URI  | `http://127.0.0.1:8082/ui/login/oauth2/code/auth-server`    |

> Spring Authorization Server does not allow `localhost` in redirect URIs, so open the client via
> `http://127.0.0.1:8082/ui`. Using different host names for the client and the auth server also
> keeps their session cookies apart.

### Useful endpoints (auth server)

| Endpoint                                   | Description                        |
|--------------------------------------------|------------------------------------|
| `/auth/.well-known/openid-configuration`   | OpenID Connect discovery document  |
| `/auth/oauth2/authorize`                   | Authorization endpoint             |
| `/auth/oauth2/token`                       | Token endpoint                     |
| `/auth/oauth2/jwks`                        | JSON Web Key Set                   |
| `/auth/userinfo`                           | OIDC UserInfo endpoint             |
| `/auth/rest/hello`                         | Sample API (requires Bearer token) |
| `/auth/rest/hello/principal`               | Returns the token's principal      |

## Prerequisites

- JDK 25
- MySQL 8 running on `localhost:3306` (the `oauth2` database is created automatically)

Quick start MySQL with Docker:

```bash
docker run -d --name mysql-oauth2 -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root mysql:8
```

Database credentials default to `root` / `root` and can be overridden with the `DB_USERNAME` and
`DB_PASSWORD` environment variables.

## Running

Start the auth server:

```bash
cd spring-security-auth-server
./mvnw spring-boot:run
```

On first start a demo user is seeded (configurable via `app.seed.*` in `application.properties`):

| Username | Password   | Role   |
|----------|------------|--------|
| `hendi`  | `password` | `USER` |

Start the client in another terminal:

```bash
cd spring-security-client
./mvnw spring-boot:run
```

Then open <http://127.0.0.1:8082/ui>, click **Login to OAuth here** and sign in with the demo user.

### Calling the API with curl

```bash
# after signing in, copy the access token, or test that the API is protected:
curl -i http://localhost:8081/auth/rest/hello            # 401 Unauthorized
curl -H "Authorization: Bearer <access_token>" http://localhost:8081/auth/rest/hello/principal
```

## Configuration

| Module      | Property                         | Default                                                    |
|-------------|----------------------------------|------------------------------------------------------------|
| auth-server | `app.oauth2.client.id`           | `ClientId`                                                 |
| auth-server | `app.oauth2.client.secret`       | `secret`                                                   |
| auth-server | `app.oauth2.client.redirect-uri` | `http://127.0.0.1:8082/ui/login/oauth2/code/auth-server`   |
| auth-server | `app.seed.enabled`               | `true`                                                     |
| client      | `app.auth-server-uri`            | `http://localhost:8081/auth`                               |

## Testing

Tests use an in-memory H2 database, so MySQL is not required:

```bash
(cd spring-security-auth-server && ./mvnw verify)
(cd spring-security-client && ./mvnw verify)
```

## CI/CD & dependency updates

- **GitHub Actions** ([`maven.yml`](.github/workflows/maven.yml)) builds and tests both modules on JDK 25 in a
  matrix and submits the Maven dependency graph on pushes to `master`.
- **Dependabot** ([`dependabot.yml`](.github/dependabot.yml)) checks Maven (both modules) and GitHub Actions
  weekly, grouping Spring Boot, Spring and minor/patch updates into fewer pull requests.

## Author

**Hendi Santika**

- Email: hendisantika@gmail.com
- Telegram: [@hendisantika34](https://t.me/hendisantika34)
- Link: [s.id/hendisantika](https://s.id/hendisantika)
