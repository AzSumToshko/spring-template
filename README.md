# Spring Boot API Template

This is a **Spring Boot API starter template** designed to help you quickly kick off a backend project with a solid foundation, built-in best practices, and automation tools. It includes everything from JWT-based authentication, layered architecture, environment configuration, CLI code generation, and a well-documented structure.

---

## ✨ Features

- Modular **Service-Repository-Controller** architecture
- JWT Authentication support via **BetterAuth** (ready for integration)
- Flyway for **database migrations**
- Environment management with `.env` + Spring's `application.properties`
- Support for PostgreSQL
- SMTP Service
- PDF Generation service
- Chart generation service
- Builtin Log Service for DB logs
- Events
- Global Exception Handler
- Request Interceptor
- Localization
- Auto-generated Swagger/OpenAPI documentation
- CRUD Abstraction for faster development
- CLI Tool to generate:
    - Entity class
    - Request/Response DTOs
    - Repository
    - Service class
    - Controller
    - Migration script
- Dockerized for easy containerization and deployment
- Secure route configuration with role-based access (admin/user)
- DTO-based validation and conversion
- Preconfigured ModelMapper and MessageSource

---

## 💪 Initial Setup

### 1. Clone the project

```bash
git clone https://github.com/yourusername/spring-template.git
```

### 2. Open in IntelliJ IDEA

> Make sure you open the **project root**, not just the `src/` directory.

### 3. Rename Group ID, Artifact ID & Project Name

IntelliJ can do this for you:

1. Open `pom.xml`
2. Right-click the `<groupId>`, `<artifactId>`, and `<name>` tags > `Refactor > Rename`
3. Update these to match your new project details (e.g. `com.mycompany`, `my-awesome-api`, `MyAwesomeAPI`)
4. IntelliJ will auto-refactor packages and folders.

---

## 🔨 Running the App

### 1. Setup `.env`

Create a `.env` file in the project root:

```env
DATASOURCE_URL=jdbc:postgresql://localhost:5432/dev_db
DATASOURCE_DB=dev_db
DATASOURCE_USER=postgres
DATASOURCE_PASSWORD=yourpassword
```

### 2. Build the Project

```bash
./mvnw clean install
```

### 3. Run the App

```bash
./mvnw spring-boot:run
```

or with Docker:

```bash
docker-compose up -d --build
```

---

## 🚀 CLI Code Generator

The project includes a CLI generator to scaffold new components:

### Usage

```bash
java cli/EntityGeneratorCLI.java <EntityName> [<field:type>...]
```

### Examples

Generate an empty entity:

```bash
java cli/EntityGeneratorCLI.java Engine
```

Generate an entity with fields:

```bash
java cli/EntityGeneratorCLI.java Motor model:String horsepower:int torque:int
```

This will generate:

- `Engine.java` in `domain.entity`
- `EngineRequestDTO.java` / `EngineResponseDTO.java`
- `EngineRepository.java`
- `EngineService.java`
- `EngineController.java`
- `V1.X__CREATE_ENGINE_TABLE.sql` Flyway migration
- Automatically update `SecurityConfiguration.java` with new route permissions

---

## 🔐 Route Security

Each controller is registered with access rules in `SecurityConfiguration.java`:

```java
.requestMatchers(HttpMethod.GET, API_ENDPOINT + "/engines/**").permitAll()
.requestMatchers(HttpMethod.POST, API_ENDPOINT + "/engines/**").hasAuthority(Roles.ADMIN.name())
.requestMatchers(HttpMethod.PUT, API_ENDPOINT + "/engines/**").hasAuthority(Roles.ADMIN.name())
.requestMatchers(HttpMethod.DELETE, API_ENDPOINT + "/engines/**").hasAuthority(Roles.ADMIN.name())
```

Roles and permissions are handled through BetterAuth JWT and Spring Security integration.

---

## 📁 Folder Structure Overview

```
spring-template
├── cli/                         # Code generation scripts
├── config/                      # Spring config classes
├── constant/                    # Constants like API_ENDPOINT
├── controller/                  # REST controllers
├── domain/
│   ├── dto/                     # Request and response DTOs
│   ├── entity/                  # JPA entities
│   ├── enums/                   # Domain enums like Roles
├── repository/                  # Repository interfaces
├── service/
│   └── crud/                    # Services and base logic
├── util/                        # Utility classes (e.g., dotenv loader)
├── resources/
│   ├── db/migration/            # Flyway SQL migration scripts
│   ├── application.properties   # Spring config
│   ├── .env                     # Environment variables
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

## ⚙️ Docker Support

To build and run the app with Docker:

```bash
docker-compose up -d --build
```

Ensure `.env` is present in the root for app configuration.

---

## 📅 Migrations with Flyway

All SQL migrations go into `src/main/resources/db/migration/` following the format:

```
V1.1__CREATE_ENGINE_TABLE.sql
```

These are applied automatically at startup.

---

## 🎨 Swagger/OpenAPI

Once the server is running, access:

```
http://localhost:8877/swagger-ui.html
```

---

## 🚀 What's Next

- Add integration tests
- Enhance CLI with relation support (e.g. `user:User`)
- Add GraphQL or gRPC module if needed
- Add CI/CD GitHub workflow

---

## ✉️ Contact / Support

If you want to contribute or report issues, feel free to create an issue or PR.

Enjoy building with this template! ✨

