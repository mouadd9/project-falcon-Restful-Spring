You have two environment templates:

1. **`.env.template`**: For application configuration (e.g., Spring Boot app).
2. **`.env.docker.template`**: For Docker Compose configuration (e.g., MySQL credentials).

---

# Project Setup Guide

This guide explains how to set up and configure the environment variables for both the application and Docker Compose using the provided templates: `.env.template` and `.env.docker.template`.

---

## Prerequisites
- Docker and Docker Compose installed on your machine.
- A copy of the `.env.template` and `.env.docker.template` files.

---

## Step 1: Create the `.env` Files

### For Application Configuration
1. Copy the `.env.template` file to a new file named `.env`:
   ```bash
   cp .env.template .env
   ```

2. Open the `.env` file in a text editor and fill in the required values for the application.

---


## Step 2: Configure Environment Variables

### Application Configuration (`.env`)
#### Application Settings
- **`APP_NAME`**: Set the name of your application.
- **`SERVER_PORT`**: Set the port on which the application will run.

Example:
```env
APP_NAME=MyApp
SERVER_PORT=8080
```

#### Database Connection Properties
- **`DB_URL`**: Set the JDBC URL for the MySQL database. Use the following format:
  ```
  jdbc:mysql://<host>:<port>/<database>?useSSL=false&serverTimezone=UTC
  ```
- **`DB_USERNAME`**: Set the database username.
- **`DB_PASSWORD`**: Set the database password.
- **`DB_DRIVER`**: Set the database driver (e.g., `com.mysql.cj.jdbc.Driver`).


#### Hibernate Properties
- **`JPA_DIALECT`**: Set the Hibernate dialect (e.g., `org.hibernate.dialect.MySQL8Dialect`).
- **`JPA_DDL_AUTO`**: Set the Hibernate DDL auto mode (e.g., `update`).
- **`JPA_SHOW_SQL`**: Set to `true` to enable SQL logging.
- **`JPA_FORMAT_SQL`**: Set to `true` to format SQL logs.

Example:
```env
JPA_DIALECT=org.hibernate.dialect.MySQL8Dialect
JPA_DDL_AUTO=update
JPA_SHOW_SQL=true
JPA_FORMAT_SQL=true
```

#### Redis Configuration
- **`REDIS_HOST`**: Set the Redis host (e.g., `redis` or `localhost`).
- **`REDIS_PORT`**: Set the Redis port (default is `6379`).

Example:
```env
REDIS_HOST=redis
REDIS_PORT=6379
```

#### Email Configuration
- **`MAIL_HOST`**: Set the SMTP host (e.g., `smtp.gmail.com`).
- **`MAIL_PORT`**: Set the SMTP port (e.g., `587` for Gmail).
- **`MAIL_USERNAME`**: Set the email username.
- **`MAIL_PASSWORD`**: Set the email app password.
- **`MAIL_AUTH`**: Set to `true` to enable SMTP authentication.
- **`MAIL_STARTTLS`**: Set to `true` to enable STARTTLS encryption.


#### Verification Code Settings
- **`VERIFICATION_CODE_EXPIRY`**: Set the expiry time (in minutes) for verification codes.

Example:
```env
VERIFICATION_CODE_EXPIRY=10
```

#### JWT Configuration
- **`JWT_PUBLIC_KEY`**: Set the path to the public key file (e.g., `classpath:keys/public.pem`).
- **`JWT_PRIVATE_KEY`**: Set the path to the private key file (e.g., `classpath:keys/private.pem`).

Example:
```env
JWT_PUBLIC_KEY=classpath:keys/public.pem
JWT_PRIVATE_KEY=classpath:keys/private.pem
```

---

## Step 3: Run the Application with Docker Compose
1. Ensure Docker and Docker Compose are installed.
2. Run the following command to start the services:
   ```bash
   docker-compose --env-file .env.docker up
   ```

---

## Step 4: Verify the Setup
- Access the application at `http://localhost:<SERVER_PORT>`.
- Check the logs to ensure all services (MySQL, Redis, etc.) are running correctly.

---

## Notes
- Keep the `.env` and `.env.docker` files secure and do not commit them to version control. Add them to `.gitignore`:
  ```bash
  echo ".env" >> .gitignore
  echo ".env.docker" >> .gitignore
  ```
- Use the `.env.template` and `.env.docker.template` files as references for required environment variables.

---

For further assistance, refer to the project documentation or contact the maintainers.

---

This `README.md` provides clear instructions for setting up and configuring the project using both `.env.template` and `.env.docker.template`. Let me know if you need further adjustments! 😊