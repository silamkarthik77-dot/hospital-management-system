# Hospital Management System

A full-stack Hospital Management System developed to manage patients, doctors, appointments, billing, medical reports, and role-based access.

## Project Overview

The system provides separate functionality for different users such as **Admin, Doctor, Patient, and Receptionist**.

### Main Features

* User registration and login
* Role-based access control
* Patient profile management
* Doctor information and departments
* Appointment booking and management
* Patient appointment history
* Billing and invoice management
* Medical report management
* REST APIs
* Swagger/OpenAPI API documentation
* MySQL database
* Automated build and test workflow using GitHub Actions

## Technologies Used

### Backend

* Java 21
* Spring Boot 3
* Spring Security
* JWT Authentication
* Spring Data JPA
* Hibernate
* Maven
* REST APIs

### Frontend

* React
* JavaScript
* HTML
* CSS
* Axios
* Bootstrap

### Database

* MySQL

### Tools

* Git & GitHub
* Postman
* Swagger/OpenAPI
* Docker
* GitHub Actions

## User Roles

| Role         | Main Functions                                               |
| ------------ | ------------------------------------------------------------ |
| Admin        | Manage system information and view dashboard data            |
| Doctor       | View appointments and manage patient-related information     |
| Patient      | Register, manage profile, book appointments and view records |
| Receptionist | Manage patients, appointments and billing                    |

## Project Structure

```text
hospital-management-system/
│
├── backend/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── Dockerfile
│
├── database/
│   └── schema.sql
│
├── postman/
│   └── HMS-API.postman_collection.json
│
├── .github/
│   └── workflows/
│       └── ci-cd.yml
│
├── .env.example
├── .gitignore
├── docker-compose.yml
└── README.md
```

## How to Run Locally

### Prerequisites

Install:

* Java 21
* Maven
* Node.js 20+
* MySQL 8
* Git

### 1. Clone the Repository

```bash
git clone https://github.com/silamkarthik77-dot/hospital-management-system.git
cd hospital-management-system
```

### 2. Configure the Database

Create a MySQL database named:

```text
hms_db
```

Configure the required database values using the environment variables described in:

```text
.env.example
```

### 3. Run the Backend

Open a terminal:

```bash
cd backend
mvn spring-boot:run
```

Backend:

```text
http://localhost:8080
```

### 4. Run the Frontend

Open another terminal:

```bash
cd frontend
npm install
npm start
```

Frontend:

```text
http://localhost:3000
```

## API Documentation

After starting the backend, Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

The REST API includes endpoints for areas such as:

```text
/api/auth
/api/patients
/api/doctors
/api/appointments
/api/billing
/api/medical-reports
/api/admin
/api/receptionist
```

A Postman collection is also available in:

```text
postman/HMS-API.postman_collection.json
```

## Authentication

The application uses JWT-based authentication.

After successful login, the API returns an authentication token. Protected API requests use the token through the:

```text
Authorization: Bearer <token>
```

header.

Role-based authorization is used to restrict access to different parts of the application.

## Database

The application uses **MySQL** with Spring Data JPA/Hibernate.

Main entities include:

* Users
* Roles
* Patients
* Doctors
* Departments
* Appointments
* Prescriptions
* Medical Reports
* Invoices
* Payments
* Notifications

The database schema is available in:

```text
database/schema.sql
```

## Testing

Backend tests can be executed using:

```bash
cd backend
mvn test
```

The project includes unit tests for selected backend services.

## Docker

The project includes Docker configuration for the application components.

```bash
docker compose up --build
```

> Docker configuration may require environment-specific settings before running.

## GitHub Actions

The project contains a GitHub Actions workflow:

```text
.github/workflows/ci-cd.yml
```

The workflow is configured for backend testing/building, frontend building, and Docker-related deployment steps using GitHub Secrets.

## Project Status

This project is a **college/portfolio project** developed to demonstrate full-stack application development, REST APIs, database integration, authentication, and role-based access control.

Some advanced production features and deployment configurations may require additional setup.

## Future Enhancements

* Improve automated test coverage
* Add additional validation and error handling
* Improve UI/UX
* Add more reporting and analytics
* Add notification improvements
* Improve production deployment configuration
* Add additional security hardening

## Author

**Silam Karthik**

B.Tech Computer Science & Engineering Student

GitHub:
https://github.com/silamkarthik77-dot
