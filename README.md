# Telemedicine backend

This repository is my thesis project for the University of Crete (UoC). It is a local demo of a telemedicine backend.

A patient creates a consultation request. Doctors send offers. When the patient accepts an offer, the system creates a consultation, a video session, and notifications. A doctor registers by uploading documents, and an admin reviews those documents before the doctor account is approved.

## Tech stack

- Java 17
- Spring Boot 4.1
- Spring Modulith 2.1
- Spring Security with a stateless JSON Web Token (JWT)
- PostgreSQL and Flyway
- Docker Compose for the local services
- Caffeine for the password-reset code cache

## Modules

- **auth**: register, login, logout, and password reset with a one-time password (OTP)
- **patient**, **doctor**, **admin**: profile details for each role
- **doctor registration**: document upload to Amazon Web Services (AWS) Simple Storage Service (S3), then admin review
- **marketplace**: consultation requests and doctor offers
- **consultation**: the appointment created after an offer is accepted
- **video**: Twilio video room and join token
- **notification**: email and Firebase Cloud Messaging (FCM) push
- **payment**: doctor and patient payment-account records

Modules publish Spring Modulith events. Listeners in the other modules react to those events. For example, an accepted offer creates the consultation, and a created consultation can open a video session and send notifications.

## Request flow

1. A user registers and logs in. The API returns a JWT.
2. The user completes a patient, doctor, or admin profile.
3. A doctor creates a registration and uploads documents.
4. An admin reviews the registration and each document.
5. A patient opens a consultation request.
6. A doctor sends an offer. The patient accepts it, or the doctor withdraws it.
7. The accepted offer becomes a consultation.
8. The patient and the doctor join the video session.
9. Email and push notifications are sent for the relevant steps.

Password reset sends an OTP by email. The code is stored in memory for 15 minutes.

## Run locally

Requirements: Java 17, Maven, and Docker.

1. Copy `.dev.env.example` to `.dev.env`.
2. Start the local services:

```
docker compose --env-file .dev.env up -d
```

3. Start the API:

```
mvnw.cmd spring-boot:run
```

The API listens on port 8080. On the first start, Flyway creates the database schema.

OpenAPI (OpenAPI Specification) documentation is served by Swagger UI at http://localhost:8080/swagger-ui.html.

## Local services

| Service | URL | Login |
|---|---|---|
| API | http://localhost:8080 | JWT from `/api/auth/login` |
| pgAdmin | http://localhost:5050 | `admin@admin.com` / `admin` |
| Mailpit | http://localhost:8025 | none |
| Zipkin | http://localhost:9411 | none |
| Prometheus | http://localhost:9090 | none |
| Grafana | http://localhost:3000 | `admin` / `admin` |

Mailpit receives the messages sent to `localhost:1025`. The database in `.dev.env.example` is `telemedicine_db`, with user `telemedicine_user` and password `telemedicine_password`.

Stop the services with:

```
docker compose --env-file .dev.env down
```

## Configuration

Values in `src/main/resources/application.yaml` have local defaults. Override them with environment variables when you connect a real account.

| Variable | Purpose |
|---|---|
| `AWS_ACCESS_KEY_ID` | AWS access key for S3 |
| `AWS_SECRET_ACCESS_KEY` | AWS secret key for S3 |
| `AWS_S3_BUCKET` | S3 bucket for registration documents |
| `TWILIO_ACCOUNT_SID` | Twilio account identifier |
| `TWILIO_API_KEY` | Twilio API key |
| `TWILIO_API_SECRET` | Twilio API secret |
| `TWILIO_WEBHOOK_BASE_URL` | Public base URL Twilio calls for room events |
| `FIREBASE_SERVICE_ACCOUNT` | Classpath or file location of the Firebase service-account JSON |
| `MAIL_FROM` | Sender address for email |
| `ADMIN_REGISTRATION_SECRET` | Shared secret required by `POST /api/auth/register/admin` |
| `JWT_SECRET` | Base64 secret used to sign tokens |

The local admin registration secret is `local-demo-admin-secret`. AWS, Twilio, and Firebase are optional. The application starts for a local demo without those accounts. Document upload, video, and push notifications need the matching account before those actions succeed.

Place a Firebase service-account file at `src/main/resources/firebase/service-account.json` only on your machine. That folder's JSON files are ignored by Git.

## Tests

```
mvnw.cmd test
```

Module tests use a PostgreSQL container through Testcontainers, so they do not use the demo database from Docker Compose. The S3 integration test runs only when `RUN_AWS_TESTS` is set to `true`.
