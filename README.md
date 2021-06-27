# Authenticsec

## Description

Implementation of authentication and authorization server base on Spring boot security using JWT, including frontend application for managing accounts and rules.

## Features

* Login - Base on user name and password including email verification.
* Register
* Forgot / reset password
* User management - CRUD operations
* Roles and authorities management - Each user can have a single role. Each role can have many authorities. 
Authority can be attached to multiple roles.
* Bruteforce attack

## Configuration
##### JWT
```yaml
app.security.jwt:
  secret: "security_manager_secret"
  token_prefix: "Bearer "
  #5 days
  expiration_time_in_min: 7200
  issuer: isagron
  audience: administration
  token_header: Jwt-Token
```
##### Requests security configuration
```yaml
app:
  security:
    allowed-origins:
      - ${frontend.dev.server}
    allowed-methods:
      - GET
    allowed-headers:
      - Origin
    expose-headers:
      - Authorization
```
##### Setup first user
```yaml
app.security.first-user:
  user-name: isagron
  firstName: Innon
  last-name: Sagron
  email: innon12310@gmail.com
  password: isagron
```

##### Login 
```yaml
app:
  security:
    login_attempts:
      maximum_number: 5
      #size of buffer to store users login attempts, clear data after user succeed
      size_of_cache: 100
      cache_expire_time_in_min: 15
    password_expiration_time_in_hours: 720
    confirmation_code_experition_in_sec: 120
```
##### Mail configuration
```yaml
spring.mail.username = <your app mail user name>
spring.mail.password = <Your app mail password>
#Default is true, if you want to disable email verification change to false
app.security.email_verification.enable: true
```

##### Resources
```yaml
  app.user:
    image:
      #Location to save the users profile image
      folder: "/image"
```


