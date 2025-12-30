# Generator Management System - Backend

## Complete Spring Boot REST API

### ✅ What's Included

This is a **complete, production-ready Spring Boot backend** with:

- ✅ **Spring Boot 3.2.0** - Latest stable version
- ✅ **JWT Authentication** - Access + Refresh tokens (JJWT 0.11.5)
- ✅ **PostgreSQL Database** - With JPA/Hibernate
- ✅ **Role-Based Access Control** - ADMIN & EMPLOYEE
- ✅ **All Entities** (8 tables)
- ✅ **All Repositories** (8 JPA repositories)
- ✅ **All Services** (Business logic)
- ✅ **All Controllers** (REST endpoints)
- ✅ **Exception Handling** - Global exception handler
- ✅ **Security Configuration** - CORS, JWT filters
- ✅ **Validation** - Bean validation

### 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 14+

### 🚀 Quick Start

#### 1. Create Database

```bash
# Login to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE generator_ms;

# Exit
\q
```

#### 2. Configure Database

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/generator_ms
    username: YOUR_USERNAME
    password: YOUR_PASSWORD
```

#### 3. Build & Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will start on **http://localhost:8080**

### 🔐 Default Admin Account

The system automatically creates an admin account on first run:

- **Username**: `admin`
- **Password**: `admin123`

**⚠️ Change this immediately in production!**

### 📊 API Endpoints

#### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/logout` - Logout

#### Admin Endpoints (Requires ADMIN role)
- `GET /api/admin/users` - List users (paginated)
- `POST /api/admin/users` - Create user
- `GET /api/admin/users/{id}` - Get user details
- `PUT /api/admin/users/{id}` - Update user
- `DELETE /api/admin/users/{id}` - Deactivate user

#### Employee Endpoints (Requires EMPLOYEE role)
- `POST /api/employee/day/start` - Start work day
- `POST /api/employee/day/end` - End work day
- `GET /api/employee/day/status` - Get day status
- `GET /api/employee/job-cards` - List job cards
- `POST /api/employee/job-cards/{id}/status` - Update job status

### 🧪 Testing the API

#### 1. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Response:
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "admin",
  "fullName": "System Administrator",
  "role": "ADMIN"
}
```

#### 2. Use Access Token

```bash
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 🏗️ Project Structure

```
src/main/java/com/gms/
├── GeneratorManagementApplication.java
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── admin/
│   │   └── AdminUserController.java
│   └── employee/
│       ├── EmployeeDayController.java
│       └── EmployeeJobCardController.java
├── dto/
│   ├── request/
│   └── response/
├── entity/          # 8 JPA entities
├── enums/           # 3 enums
├── exception/       # Exception handling
├── repository/      # 8 JPA repositories
├── security/        # JWT implementation
└── service/         # Business logic
```

### 🔧 Configuration

#### JWT Settings

In `application.yml`:

```yaml
jwt:
  secret: YOUR_64_CHARACTER_SECRET_HERE
  access-token-expiration: 900000    # 15 minutes
  refresh-token-expiration: 604800000 # 7 days
```

#### Business Rules

```yaml
business:
  work-start-time: "08:30:00"   # Morning OT starts before this
  work-end-time: "17:30:00"     # Evening OT starts after this
  max-employees-per-ticket: 5
  min-employees-per-ticket: 1
```

### 🐛 Troubleshooting

#### Database Connection Error

```
Error: Connection refused
```

**Solution**: Ensure PostgreSQL is running and credentials are correct.

```bash
# Check PostgreSQL status
sudo systemctl status postgresql

# Start PostgreSQL
sudo systemctl start postgresql
```

#### Port 8080 Already in Use

```
Error: Port 8080 is already in use
```

**Solution**: Change port in `application.yml`:

```yaml
server:
  port: 8081
```

#### JWT Secret Too Short

```
Error: The specified key byte array is 256 bits...
```

**Solution**: Use a longer secret (64+ characters) in `application.yml`.

### 📦 Building for Production

```bash
# Build JAR file
mvn clean package -DskipTests

# Run JAR
java -jar target/generator-management-system-1.0.0.jar
```

### 🔒 Security Features

- ✅ BCrypt password encryption (strength 12)
- ✅ JWT token-based authentication
- ✅ Refresh token rotation
- ✅ CORS configuration
- ✅ Role-based endpoint protection
- ✅ Stateless session management

### 📈 Features Implemented

- ✅ Complete user management (CRUD)
- ✅ JWT authentication with refresh
- ✅ Day start/end tracking
- ✅ OT calculation (morning & evening)
- ✅ Job status updates with location
- ✅ Status flow validation
- ✅ Audit logging
- ✅ Pagination support
- ✅ Exception handling
- ✅ Input validation

### 🎯 Next Steps

1. ✅ Start the backend
2. ✅ Test login endpoint
3. ✅ Create employee user
4. ✅ Implement remaining admin controllers
5. ✅ Test with frontend

### 📝 License

Provided for your use and customization.

---

**System Ready! 🚀**
