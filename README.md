# CST 438 Project 02: Availability Planner API

Backend REST API for an availability-planning application. Users can create and join scheduling groups, manage their availability, and update or delete group-related data.

The API is built with Kotlin, Spring Boot, Spring Data JDBC, PostgreSQL/Supabase, Docker, and Render.

## Features

- Authenticated user profile access
- Administrator-only user listing and user lookup
- Create scheduling groups
- Join scheduling groups
- Leave scheduling groups
- Administrator-only group deletion
- Update a group's event name using PATCH
- Replace group details using PUT
- Replace a user's availability in a group using PUT
- Delete a user's own account
- Delete a user's availability record
- List standard weekly time slots
- PostgreSQL database integration through Supabase
- Dockerized Spring Boot application
- Render deployment
- Unit and integration testing

## API deployment

The API is deployed on Render:

https://cst438-01-project-02-group-01-api.onrender.com

The free Render instance may take several seconds to wake up after inactivity.

## API routes

All routes require bearer authentication unless otherwise noted.

### Users

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/users/me` | Get the signed-in user's profile |
| `DELETE` | `/api/v1/users/me` | Delete the signed-in user's account |
| `GET` | `/api/v1/users` | Get all users; administrator permission required |
| `GET` | `/api/v1/users/{userId}` | Get one user; administrator permission required |

### Groups

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/groups` | Create a scheduling group |
| `PATCH` | `/api/v1/groups/{groupId}` | Partially update a group's event name |
| `PUT` | `/api/v1/groups/{groupId}` | Replace a group's editable details |
| `DELETE` | `/api/v1/groups/{groupId}` | Delete a group; administrator permission required |

### Group memberships

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/groups/{groupId}/members` | Join a scheduling group |
| `DELETE` | `/api/v1/groups/{groupId}/members/me` | Leave a scheduling group |

### Availability

| Method | Endpoint | Description |
|---|---|---|
| `PUT` | `/api/v1/groups/{groupId}/availability/me` | Replace the signed-in user's availability for a group |
| `DELETE` | `/api/v1/availability/{availabilityId}` | Delete an availability record belonging to the signed-in user |

### Time slots

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/time-slots` | Get all standard weekly time slots |

The time-slot endpoint is publicly accessible. Other endpoints require authentication.

## Example requests

### Create a group

```http
POST /api/v1/groups
Authorization: Bearer <token>
Content-Type: application/json

{
  "eventName": "Team planning meeting"
}
```

### Join a group

```http
POST /api/v1/groups/1/members
Authorization: Bearer <token>
```

### Update a group event name

Only the group creator can update the event name.

```http
PATCH /api/v1/groups/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "eventName": "Updated team planning meeting"
}
```

A successful update returns:

```text
204 No Content
```

### Replace availability

Availability values use the format:

```text
dayOfWeek + startHour + endHour
```

For example, `10912` represents day 1 from hour 9 through hour 12.

```http
PUT /api/v1/groups/1/availability/me
Authorization: Bearer <token>
Content-Type: application/json

"10912 21318"
```

### Get time slots

```http
GET /api/v1/time-slots
```

## Authentication and authorization

Protected routes use bearer-token authentication:

```http
Authorization: Bearer <token>
```

Administrator-only routes require the `USER_ADMIN` authority:

- `GET /api/v1/users`
- `GET /api/v1/users/{userId}`
- `DELETE /api/v1/groups/{groupId}`

The PATCH group route only permits the creator of the group to change its event name.

## Database

The application uses PostgreSQL through Supabase.

The database contains:

- `users`
- `schedule_groups`
- `group_members`
- `time_slots`
- `availability`

The initial database schema is located at:

```text
supabase/migrations/20261006120000_initial_schema.sql
```

The schema includes relationships, foreign keys, uniqueness constraints, and 168 standard one-hour weekly time slots.

## Configuration

Copy the example environment file and provide the database values:

```bash
cp .env.example .env
```

The environment file should contain the Supabase database URL and password. Do not commit `.env` or any file containing real credentials.

Example:

```text
SUPABASE_DB_URL=jdbc:postgresql://<supabase-host>:5432/postgres?user=<supabase-user>
SUPABASE_DB_PASSWORD=<your-database-password>
```

## Running locally with Gradle

On Windows:

```powershell
.\gradlew.bat bootRun
```

On macOS or Linux:

```bash
./gradlew bootRun
```

The API runs at:

```text
http://localhost:8080
```

## Running with Docker

Build and start the application:

```bash
docker compose up --build
```

Check the running container:

```bash
docker compose ps
```

The API is exposed on port `8080`.

Test the public time-slot route:

```bash
curl http://localhost:8080/api/v1/time-slots
```

Stop the containers:

```bash
docker compose down
```

## Testing

Run the complete test suite on Windows:

```powershell
.\gradlew.bat clean test
```

Run the complete test suite on macOS or Linux:

```bash
./gradlew clean test
```

The project includes:

- Controller unit tests
- Service unit tests
- PostgreSQL integration tests
- Testcontainers-based database tests
- POST route tests
- DELETE route tests
- PUT route tests
- PATCH route tests

## OpenAPI documentation

The API contract is located at:

```text
docs/openapi.yaml
```

Lint the OpenAPI document with:

```bash
npx @redocly/cli lint docs/openapi.yaml
```

The contract can also be viewed with the [Swagger Editor](https://editor.swagger.io).

## Project structure

```text
src/main/kotlin/com/scheduler/
├── config/          Security configuration
├── controller/      REST controllers
├── dto/             Request and response data-transfer objects
├── mapper/          Database model-to-DTO mappers
├── model/           Spring Data JDBC models
├── repository/      Database repositories
├── security/        Authentication principal classes
└── service/         Business logic and JDBC operations

src/test/kotlin/     Unit and integration tests
docs/                Proposal and OpenAPI documentation
supabase/            Database migrations
Dockerfile           Docker image definition
docker-compose.yml   Local container configuration
```

## Technology stack

- Kotlin
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JDBC
- PostgreSQL
- Supabase
- Gradle
- Docker
- Docker Compose
- Testcontainers
- Render
- OpenAPI 3.0.3

## Useful links

- [OpenAPI 3.0 specification](https://spec.openapis.org/oas/v3.0.3)
- [Swagger Editor](https://editor.swagger.io)
- [Redocly CLI](https://redocly.com/docs/cli/)
- [Supabase](https://supabase.com)
- [Render](https://render.com)
- [Docker](https://www.docker.com)
- [RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457)