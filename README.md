# T-MAT Global LIMS — Laboratory Information Management System

## System Requirements

| Software | Version | Download |
|----------|---------|----------|
| Java (JDK) | 21 or higher | https://adoptium.net |
| Apache Maven | 3.9+ | https://maven.apache.org |
| Microsoft SQL Server | 2019+ (Express edition is sufficient) | https://www.microsoft.com/en-us/sql-server/sql-server-downloads |
| SQL Server Management Studio (SSMS) | Any recent version | https://learn.microsoft.com/en-us/sql/ssms/download-sql-server-management-studio-ssms |
| A modern web browser | Chrome / Edge / Firefox | — |

---

## Step 1 — Install & Configure SQL Server

1. Install **SQL Server Express** (free edition).
2. During installation, note your instance name (default: `SQLEXPRESS`).
3. Enable **SQL Server Authentication** and set the `sa` password.
4. Open **SQL Server Configuration Manager** → enable **TCP/IP** on port 1433.
5. Restart the SQL Server service.

### Create the Database

Open SSMS, connect to `localhost\SQLEXPRESS`, then run:

```sql
CREATE DATABASE matglobal_lims;
```

> The application will auto-create all tables on first startup via `schema.sql`.

---

## Step 2 — Configure the Application

Open the file:

```
backend/src/main/resources/application.properties
```

Update these values to match your environment:

```properties
# SQL Server connection
spring.datasource.url=jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=matglobal_lims;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YOUR_SA_PASSWORD

# Application port (default 8081 — change if in use)
server.port=8081
```

> **Production tip:** Do not commit credentials to version control.
> Use environment variables instead:
> ```
> SPRING_DATASOURCE_PASSWORD=your_password mvn spring-boot:run
> ```

---

## Step 3 — Build the Backend

Open a terminal in the project root directory (where the top-level `pom.xml` lives) and run:

```bash
# On Windows
mvn clean package -DskipTests

# On Linux / macOS
./mvnw clean package -DskipTests
```

This produces:

```
backend/target/lims-backend-*.jar
```

---

## Step 4 — Run the Backend

```bash
java -jar backend/target/lims-backend-*.jar
```

Or using Maven directly:

```bash
cd backend
mvn spring-boot:run
```

Wait for the log line:

```
Started LimsApplication in X seconds
```

The API is now available at: `http://localhost:8081/api`

---

## Step 5 — Open the Frontend

The frontend is a single HTML file — no build step required.

**Option A — Served by the backend (recommended):**
Open your browser and navigate to:
```
http://localhost:8081/app
```

**Option B — Open the file directly:**
```
frontend/index.html
```
Open it in any browser (double-click or drag into browser).

---

## Step 6 — First Login

| Field | Value |
|-------|-------|
| Username | `admin` |
| Password | `admin123` |

> Change the admin password immediately after first login.

---

## Step 7 — Verify the System

1. Log in — the status indicator in the bottom bar should show **Backend Online**.
2. Go to **Front Desk → New Registration** and register a test patient.
3. Open SSMS and verify a new row appears in `matglobal_lims.dbo.patients`.
4. Insert a patient directly in SSMS:
   ```sql
   INSERT INTO patients (name, gender, age, age_unit, mobile, created_at, updated_at)
   VALUES ('Test Patient', 'Male', 30, 'Years', '9876543210', GETDATE(), GETDATE());
   ```
5. Refresh the Patient Status screen in the UI — the new patient should appear.

---

## Folder Structure Reference

```
lims-dipak/
├── backend/                  Spring Boot REST API (Java 21)
│   ├── src/
│   │   └── main/
│   │       ├── java/         Application source code
│   │       └── resources/
│   │           ├── application.properties   Configuration
│   │           └── schema.sql               Database DDL (auto-run)
│   └── pom.xml
├── frontend/
│   └── index.html            Complete single-page application
├── deployment/
│   └── setup-guide.md        This file
└── pom.xml                   Root Maven build file
```

---

## API Endpoints Summary

All endpoints are prefixed with `/api` (context path).

| Module | Base URL | Notes |
|--------|----------|-------|
| Auth | `/api/auth/login` | POST — returns JWT token |
| Patients | `/api/v1/patients` | CRUD |
| Registrations | `/api/v1/registrations` | CRUD + status update |
| Tests | `/api/v1/tests` | CRUD (ADMIN only for mutations) |
| Referring Doctors | `/api/v1/ref-doctors` | CRUD |
| Dashboard | `/api/v1/dashboard/stats` | GET |
| Users | `/api/v1/users` | ADMIN only |
| Health | `/api/health` | GET — public |

---

## Troubleshooting

### Backend won't start

| Symptom | Fix |
|---------|-----|
| `Connection refused` to SQL Server | Ensure SQL Server is running and TCP/IP is enabled on port 1433 |
| `Login failed for user 'sa'` | Check `spring.datasource.password` in application.properties |
| Port 8081 already in use | Change `server.port` to 8082 or another free port; update the `apiBase()` in frontend/index.html to match |
| Schema errors on startup | `continue-on-error=true` is set — check logs for details but startup will proceed |

### Frontend shows "Offline / Demo"

- Verify the backend is running on port 8081.
- Check browser console for CORS or network errors.
- Ensure `apiBase()` in `frontend/index.html` matches your backend port.

### Data not appearing after SSMS insert

- The frontend pulls fresh data on every page navigation.
- Click on the menu item again to force a reload, or press F5 to refresh the app.

---

## Changing the Default Port

1. In `backend/src/main/resources/application.properties` change:
   ```properties
   server.port=8082
   ```
2. In `frontend/index.html` find and update (there are two places):
   ```javascript
   function apiBase() { return 'http://localhost:8082/api'; }
   // ...and in the integration patch:
   const res = await fetch('http://localhost:8082' + path, ...)
   ```

---

## Packaging for ZIP Delivery

```bash
# 1. Build the JAR
mvn clean package -DskipTests

# 2. Create delivery folder
mkdir lims-delivery
cp backend/target/lims-backend-*.jar lims-delivery/lims-backend.jar
cp -r frontend/ lims-delivery/frontend/
cp deployment/setup-guide.md lims-delivery/

# 3. Zip it
zip -r lims-delivery.zip lims-delivery/
```

The client runs the system with:
```bash
java -jar lims-backend.jar
# Then opens: http://localhost:8081/app
```

---------------------------------------------------------------------------------------------------------------------
# T-MAT Global Technologies Pvt. Ltd.
# India
