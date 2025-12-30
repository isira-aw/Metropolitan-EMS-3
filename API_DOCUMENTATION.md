# Metropolitan EMS - API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication

All protected endpoints require JWT token in Authorization header:
```
Authorization: Bearer <access_token>
```

### Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}

Response:
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "expiresIn": 900000,
  "user": {
    "id": 1,
    "username": "admin",
    "fullName": "Admin User",
    "role": "ADMIN",
    "email": "admin@example.com"
  }
}
```

### Refresh Token
```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGc..."
}

Response: Same as login
```

## Admin APIs

### User Management

#### Get All Users
```http
GET /admin/users?page=0&size=10
Authorization: Bearer <token>

Response:
{
  "content": [
    {
      "id": 1,
      "username": "john",
      "fullName": "John Doe",
      "role": "EMPLOYEE",
      "email": "john@example.com",
      "phone": "0771234567",
      "active": true,
      "createdAt": "2025-01-01T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

#### Create User
```http
POST /admin/users
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "employee1",
  "password": "password123",
  "fullName": "Employee One",
  "role": "EMPLOYEE",
  "email": "employee1@example.com",
  "phone": "0771234567"
}

Response: User object
```

#### Update User
```http
PUT /admin/users/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "employee1",
  "fullName": "Employee One Updated",
  "role": "EMPLOYEE",
  "email": "employee1@example.com",
  "phone": "0771234567"
}

Response: User object
```

#### Delete User
```http
DELETE /admin/users/{id}
Authorization: Bearer <token>

Response: 200 OK
```

### Generator Management

#### Get All Generators
```http
GET /admin/generators?page=0&size=10
Authorization: Bearer <token>

Response:
{
  "content": [
    {
      "id": 1,
      "model": "CAT-500",
      "name": "Generator 1",
      "capacity": "500 KVA",
      "locationName": "Colombo Office",
      "ownerEmail": "owner@example.com",
      "latitude": 6.9271,
      "longitude": 79.8612,
      "note": "Main generator",
      "createdAt": "2025-01-01T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

#### Create Generator
```http
POST /admin/generators
Authorization: Bearer <token>
Content-Type: application/json

{
  "model": "CAT-500",
  "name": "Generator 1",
  "capacity": "500 KVA",
  "locationName": "Colombo Office",
  "ownerEmail": "owner@example.com",
  "latitude": 6.9271,
  "longitude": 79.8612,
  "note": "Main generator"
}

Response: Generator object
```

#### Update Generator
```http
PUT /admin/generators/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "model": "CAT-500",
  "name": "Generator 1 Updated",
  "capacity": "500 KVA",
  "locationName": "Colombo Office",
  "ownerEmail": "owner@example.com",
  "latitude": 6.9271,
  "longitude": 79.8612,
  "note": "Updated note"
}

Response: Generator object
```

#### Delete Generator
```http
DELETE /admin/generators/{id}
Authorization: Bearer <token>

Response: 200 OK
```

### Ticket Management

#### Get All Tickets
```http
GET /admin/tickets?page=0&size=10&status=ASSIGNED&scheduledDate=2025-01-15
Authorization: Bearer <token>

Query Parameters:
- page: Page number (default: 0)
- size: Page size (default: 10)
- status: Filter by status (optional)
- scheduledDate: Filter by scheduled date (optional, format: YYYY-MM-DD)

Response:
{
  "content": [
    {
      "id": 1,
      "ticketNumber": "TKT-20250101-0001",
      "generatorId": 1,
      "generatorName": "Generator 1",
      "generatorModel": "CAT-500",
      "title": "Routine Service",
      "description": "Regular maintenance check",
      "jobCardType": "SERVICE",
      "weight": 3,
      "weightDisplay": "***",
      "status": "ASSIGNED",
      "scheduledDate": "2025-01-15",
      "scheduledTime": "09:00:00",
      "createdById": 1,
      "createdByName": "Admin User",
      "createdAt": "2025-01-01T10:00:00",
      "subTickets": [
        {
          "id": 1,
          "ticketNumber": "TKT-20250101-0001-01",
          "mainTicketId": 1,
          "employeeId": 2,
          "employeeName": "Employee One",
          "status": "ASSIGNED"
        }
      ],
      "totalAssignments": 1,
      "completedAssignments": 0
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

#### Get Ticket by ID
```http
GET /admin/tickets/{id}
Authorization: Bearer <token>

Response: Ticket object with sub-tickets
```

#### Create Ticket
```http
POST /admin/tickets
Authorization: Bearer <token>
Content-Type: application/json

{
  "generatorId": 1,
  "title": "Emergency Repair",
  "description": "Generator not starting",
  "jobCardType": "EMERGENCY",
  "weight": 5,
  "scheduledDate": "2025-01-15",
  "scheduledTime": "09:00:00",
  "employeeIds": [2, 3, 4]
}

Job Card Types: SERVICE, REPAIR, MAINTENANCE, VISIT, EMERGENCY
Weight: 1-5 (1=*, 2=**, 3=***, 4=****, 5=*****)

Response: Ticket object
```

#### Update Ticket
```http
PUT /admin/tickets/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "generatorId": 1,
  "title": "Emergency Repair Updated",
  "description": "Updated description",
  "jobCardType": "EMERGENCY",
  "weight": 4,
  "scheduledDate": "2025-01-16",
  "scheduledTime": "10:00:00",
  "employeeIds": [2, 3]
}

Response: Ticket object
```

#### Delete Ticket
```http
DELETE /admin/tickets/{id}
Authorization: Bearer <token>

Response: 200 OK
```

#### Update Ticket Status
```http
PATCH /admin/tickets/{id}/status?status=COMPLETED
Authorization: Bearer <token>

Response: Ticket object
```

#### Approve/Reject Sub-Ticket
```http
POST /admin/tickets/sub-tickets/{subTicketId}/approve
Authorization: Bearer <token>
Content-Type: application/json

{
  "approved": true,
  "completionFactor": 1.0,
  "qualityFactor": 0.9,
  "adminReviewNotes": "Excellent work"
}

Response: SubTicket object with calculated score
```

#### Get Pending Approvals
```http
GET /admin/tickets/sub-tickets/pending-approval
Authorization: Bearer <token>

Response: Array of SubTicket objects
```

### Report APIs

#### Time Tracking Report
```http
GET /admin/reports/time-tracking?employeeId=2&startDate=2025-01-01&endDate=2025-01-31
Authorization: Bearer <token>

Query Parameters:
- employeeId: Employee ID (required)
- startDate: Start date (required, format: YYYY-MM-DD)
- endDate: End date (required, format: YYYY-MM-DD)
- Max range: 90 days

Response:
{
  "employeeId": 2,
  "employeeName": "Employee One",
  "employeeEmail": "employee1@example.com",
  "startDate": "2025-01-01",
  "endDate": "2025-01-31",
  "dailyRecords": [
    {
      "date": "2025-01-15",
      "dayStartTime": "2025-01-15T08:00:00",
      "dayEndTime": "2025-01-15T18:00:00",
      "totalWorkMinutes": 600,
      "morningOtMinutes": 30,
      "eveningOtMinutes": 30,
      "dayStarted": true,
      "dayEnded": true,
      "jobCards": [
        {
          "jobCardId": 1,
          "ticketNumber": "TKT-20250101-0001",
          "generatorName": "Generator 1",
          "status": "COMPLETED",
          "startTime": "2025-01-15T09:00:00",
          "endTime": "2025-01-15T12:00:00",
          "workMinutes": 180
        }
      ]
    }
  ],
  "totalWorkMinutes": 600,
  "totalMorningOtMinutes": 30,
  "totalEveningOtMinutes": 30,
  "totalOtMinutes": 60
}
```

#### OT Tracking Report
```http
GET /admin/reports/ot-tracking?employeeId=2&startDate=2025-01-01&endDate=2025-01-31
Authorization: Bearer <token>

Query Parameters:
- employeeId: Employee ID (required)
- startDate: Start date (required, format: YYYY-MM-DD)
- endDate: End date (required, format: YYYY-MM-DD)
- Max range: 90 days

Response:
{
  "employeeId": 2,
  "employeeName": "Employee One",
  "employeeEmail": "employee1@example.com",
  "startDate": "2025-01-01",
  "endDate": "2025-01-31",
  "totalDaysWorked": 20,
  "totalWorkMinutes": 12000,
  "totalMorningOtMinutes": 600,
  "totalEveningOtMinutes": 600,
  "totalOtMinutes": 1200,
  "dailyOTRecords": [
    {
      "date": "2025-01-15",
      "dayOfWeek": "Wednesday",
      "totalWorkMinutes": 600,
      "morningOtMinutes": 30,
      "eveningOtMinutes": 30,
      "regularWorkMinutes": 540,
      "generatorWorks": [
        {
          "generatorName": "Generator 1",
          "generatorModel": "CAT-500",
          "jobCardType": "SERVICE",
          "workMinutes": 180,
          "status": "COMPLETED"
        }
      ]
    }
  ],
  "workMinutesByJobType": {
    "SERVICE": 5000,
    "REPAIR": 3000,
    "MAINTENANCE": 2000,
    "VISIT": 1500,
    "EMERGENCY": 500
  },
  "performanceAnalysis": {
    "averageWorkMinutesPerDay": 600,
    "averageOTMinutesPerDay": 60,
    "totalJobsCompleted": 15,
    "totalJobsInProgress": 2,
    "totalJobsCancelled": 1,
    "jobCompletionRate": 83.33,
    "mostWorkedJobType": "SERVICE",
    "mostWorkedJobTypeMinutes": 5000
  }
}
```

## Employee APIs

### Job Card Management

#### Get My Job Cards
```http
GET /employee/job-cards?page=0&size=10&status=PENDING
Authorization: Bearer <token>

Query Parameters:
- page: Page number (default: 0)
- size: Page size (default: 10)
- status: Filter by status (optional)

Job Statuses: PENDING, TRAVELING, STARTED, ON_HOLD, COMPLETED, CANCEL

Response:
{
  "content": [
    {
      "id": 1,
      "mainTicketId": 1,
      "ticketNumber": "TKT-20250101-0001",
      "ticketTitle": "Routine Service",
      "generator": {
        "id": 1,
        "model": "CAT-500",
        "name": "Generator 1",
        "capacity": "500 KVA",
        "locationName": "Colombo Office",
        "latitude": 6.9271,
        "longitude": 79.8612
      },
      "status": "PENDING",
      "startTime": null,
      "endTime": null,
      "workMinutes": 0,
      "approved": false,
      "image": null,
      "createdAt": "2025-01-01T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

#### Get Job Card by ID
```http
GET /employee/job-cards/{id}
Authorization: Bearer <token>

Response: JobCard object
```

#### Update Job Card Status
```http
POST /employee/job-cards/{id}/status
Authorization: Bearer <token>
Content-Type: application/json

{
  "newStatus": "TRAVELING",
  "latitude": 6.9271,
  "longitude": 79.8612
}

Status Flow:
- PENDING → TRAVELING, CANCEL
- TRAVELING → STARTED, ON_HOLD, CANCEL
- STARTED → ON_HOLD, COMPLETED, CANCEL
- ON_HOLD → STARTED, CANCEL

Requirements:
- Day must be started
- Day must not be ended
- Location (lat/long) required
- Status transition must be valid

Response: JobCard object
```

#### Upload Image
```http
POST /employee/job-cards/{id}/image
Authorization: Bearer <token>
Content-Type: application/json

{
  "imageData": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgA...",
  "fileName": "generator_photo.jpg",
  "contentType": "image/jpeg"
}

Response: JobCard object with image
```

### Day Management

#### Get Day Status
```http
GET /employee/day/status
Authorization: Bearer <token>

Response:
{
  "dayStarted": true,
  "dayEnded": false,
  "dayStartTime": "2025-01-15T08:00:00",
  "dayEndTime": null
}
```

#### Start Day
```http
POST /employee/day/start
Authorization: Bearer <token>

Response:
{
  "dayStarted": true,
  "dayEnded": false,
  "dayStartTime": "2025-01-15T08:00:00",
  "dayEndTime": null
}

Note: Morning OT calculated if start time < 08:30
```

#### End Day
```http
POST /employee/day/end
Authorization: Bearer <token>

Response:
{
  "dayStarted": true,
  "dayEnded": true,
  "dayStartTime": "2025-01-15T08:00:00",
  "dayEndTime": "2025-01-15T18:00:00"
}

Note: Evening OT calculated if end time > 17:30
```

## Error Responses

All errors follow this format:

```json
{
  "timestamp": "2025-01-15T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/admin/tickets"
}
```

### Common HTTP Status Codes

- **200 OK**: Success
- **201 Created**: Resource created
- **400 Bad Request**: Validation error
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

### Custom Exceptions

- `ResourceNotFoundException`: Entity not found (404)
- `UnauthorizedException`: Access denied (403)
- `InvalidStatusTransitionException`: Invalid status change (400)
- `DayNotStartedException`: Day not started (400)
- `IllegalStateException`: Invalid operation (400)

## Pagination

All list endpoints support pagination:

```
?page=0&size=10
```

Response includes:
- `content`: Array of items
- `page`: Current page number
- `size`: Items per page
- `totalElements`: Total count
- `totalPages`: Total pages
- `first`: Is first page
- `last`: Is last page

## Time Zones

All datetime fields use Sri Lanka timezone (Asia/Colombo):
- Stored in database with timezone
- Returned in ISO 8601 format
- Timezone offset: +05:30

## Best Practices

1. **Always include Authorization header** for protected endpoints
2. **Refresh tokens before expiry** (15 minutes for access token)
3. **Use pagination** for large datasets
4. **Validate status transitions** before sending requests
5. **Capture location** for all status updates (employees)
6. **Start day** before performing job card operations
7. **Check date ranges** for reports (max 90 days)

## Example Workflows

### Admin: Create and Assign Ticket

1. Get list of employees: `GET /admin/users`
2. Get list of generators: `GET /admin/generators`
3. Create ticket: `POST /admin/tickets` with employee IDs
4. Ticket auto-generates sub-tickets for each employee

### Employee: Complete a Job

1. Login: `POST /auth/login`
2. Start day: `POST /employee/day/start`
3. Get job cards: `GET /employee/job-cards`
4. Update status to TRAVELING: `POST /employee/job-cards/{id}/status`
5. Update status to STARTED: `POST /employee/job-cards/{id}/status`
6. Upload image: `POST /employee/job-cards/{id}/image`
7. Update status to COMPLETED: `POST /employee/job-cards/{id}/status`
8. End day: `POST /employee/day/end`

### Admin: Generate Reports

1. Select employee and date range
2. Time tracking: `GET /admin/reports/time-tracking`
3. OT tracking: `GET /admin/reports/ot-tracking`
4. Review performance metrics
5. Export or visualize data

---

**API Version**: 1.0.0
**Last Updated**: 2025-12-30
