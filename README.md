# MAT Global — Laboratory Information System

## Project Structure
```
mat-global-lims/
├── frontend/
│   └── index.html          ← Single-file web app (open in browser, no build needed)
└── backend/
    ├── pom.xml
    └── src/main/java/com/matglobal/lims/
        ├── LimsApplication.java
        ├── config/          SecurityConfig, AuditConfig, DataBootstrap
        ├── controller/      Auth, Patient, Registration, Test, RefDoctor, User, Dashboard
        ├── dto/             request/ + response/ DTOs
        ├── entity/          Patient, Registration, RegistrationTest, Test, ReferringDoctor, User, Role, AuditLog
        ├── exception/       ResourceNotFoundException, BusinessException, DuplicateResourceException, GlobalExceptionHandler
        ├── repository/      All Spring Data JPA Repositories
        ├── security/        JwtTokenProvider, JwtAuthenticationFilter, UserDetailsServiceImpl
        └── service/impl/    AuthService, PatientService, RegistrationService, OtherServices
```

---

## Frontend — Quick Start

1. Open `frontend/index.html` directly in your browser (no server needed)
2. Login: `admin` / `admin123`
3. All data is in-memory — refresh resets it

---

## Backend — Setup & Run

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 14+

### 1. Create the Database
```sql
CREATE DATABASE matglobal_lims;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE matglobal_lims TO postgres;
```

### 2. Configure (if needed)
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/matglobal_lims
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 3. Build & Run
```bash
cd backend
mvn clean install -DskipTests
mvn spring-boot:run
```
Backend starts on: **http://localhost:8080/api**

On first run, the system automatically seeds:
- Admin user: `admin` / `admin123`
- 20 default tests/packages
- All roles

---

## API Reference

### Auth
| Method | URL | Body | Auth |
|--------|-----|------|------|
| POST | `/api/auth/login` | `{username, password}` | Public |
| POST | `/api/auth/register` | `{username, password, firstName, lastName, email, role}` | Admin |

### Patients
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/patients` | Create patient |
| GET | `/api/patients/{id}` | Get by ID |
| GET | `/api/patients/by-mobile/{mobile}` | Search by mobile |
| GET | `/api/patients?name=&mobile=&page=0&size=20` | Search |
| PUT | `/api/patients/{id}` | Update |

### Registrations
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/registrations` | Create registration with tests |
| GET | `/api/registrations/{id}` | Get by ID |
| GET | `/api/registrations?from=&to=&patientName=&status=&page=0` | Search |
| PATCH | `/api/registrations/{id}/status?status=SAMPLE_ACCEPTED` | Update status |

**Status values:** `REGISTERED` → `SAMPLE_COLLECTED` → `SAMPLE_ACCEPTED` → `TESTED` → `AUTHORIZED` → `COMPLETED` → `DISPATCHED`

### Tests
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/tests` | All active tests |
| GET | `/api/tests/search?q=blood` | Search tests |
| POST | `/api/tests` | Add test (Admin) |
| PUT | `/api/tests/{id}` | Update (Admin) |
| DELETE | `/api/tests/{id}` | Deactivate (Admin) |

### Referring Doctors
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/ref-doctors` | All active doctors |
| POST | `/api/ref-doctors` | Add doctor |
| PUT | `/api/ref-doctors/{id}` | Update |
| DELETE | `/api/ref-doctors/{id}` | Deactivate (Admin) |

### Users (Admin only)
| Method | URL |
|--------|-----|
| GET | `/api/users?page=0&size=20` |
| GET | `/api/users/{id}` |
| PUT | `/api/users/{id}` |
| PATCH | `/api/users/{id}/toggle-active` |

### Dashboard
| Method | URL |
|--------|-----|
| GET | `/api/dashboard/stats` |

---

## Example Workflow

```bash
# 1. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
# → Returns { data: { token: "eyJ..." } }

# 2. Create Patient (use token from above)
curl -X POST http://localhost:8080/api/patients \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","gender":"Male","age":35,"ageUnit":"Years","mobile":"9876543210"}'

# 3. Create Registration
curl -X POST http://localhost:8080/api/registrations \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json" \
  -d '{"patientId":1,"patientType":"OPD","paymentType":"Cash","testIds":[1,2,3],"paidAmount":1200}'

# 4. Update Status
curl -X PATCH "http://localhost:8080/api/registrations/1/status?status=SAMPLE_ACCEPTED" \
  -H "Authorization: Bearer eyJ..."
```

---

## Connect Frontend to Backend

In `frontend/index.html`, the `apiBase()` function returns `http://localhost:8080/api`.
To wire up real API calls, replace the mock save functions with `fetch()` calls to the endpoints above.

---

© 2024 MAT Global · Laboratory Information System
