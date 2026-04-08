# 🏥 Community Health Check & Telemedicine Portal (Backend)

A production-oriented backend system designed to support community healthcare services, telemedicine, and patient data management for clinics, NGOs, and small healthcare providers.

This project focuses on building a **scalable, secure, and maintainable backend** using Spring Boot, following real-world engineering practices.

---

## 🚀 Project Overview

The goal of this system is to provide a digital backbone for community healthcare by:

* Enabling remote doctor-patient interaction
* Managing patient data and medical workflows
* Supporting scalable API-driven health services
* Serving as a base for future AI-powered healthcare tools

---

## 🏗️ Architecture

The project follows a **layered architecture**:

* **Controller Layer** → Handles incoming HTTP requests
* **Service Layer** → Contains business logic
* **Repository Layer** → Manages database operations
* **Security Layer** → Handles authentication and authorization

The structure is designed to be clean, modular, and easy to extend.

---

## 🔐 Security

Security is implemented using **Spring Security + JWT**:

* Stateless authentication using JWT tokens
* Role-Based Access Control (RBAC):

  * Admin
  * Doctor
  * Patient
* Protected endpoints with secure request validation

---

## ⚙️ Core Features

### 🩺 Appointment Management

* Create and manage appointments
* Structured RESTful APIs
* Designed for future scheduling integration

### 📞 Telemedicine Support

* Integrated with Jitsi Meet API
* Supports real-time video consultation sessions

### 🧮 Health Utilities

* BMI calculation
* Glucose tracking
* Easily extendable for additional health metrics

---

## 🧱 Engineering Highlights

* DTO-based request and response handling
* Global exception handling
* Standardized API response format
* Modular and maintainable codebase

---

## 📄 API Documentation (Swagger)

Interactive API documentation is available using Swagger:

```
http://localhost:8081/swagger-ui/index.html
```

Features:

* Explore all endpoints
* Test APIs directly from the browser
* JWT authentication support via "Authorize" button

---

## 🐳 Docker Support

The project can be run using Docker for easy setup and deployment.

### Build and Run

```bash
docker-compose up --build
```

### Services

* Backend → http://localhost:8081
* Swagger UI → http://localhost:8081/swagger-ui/index.html
* MySQL → running in container

---

## 🧪 Tech Stack

| Category   | Technology            |
| ---------- | --------------------- |
| Backend    | Spring Boot           |
| Language   | Java                  |
| Database   | MySQL                 |
| Security   | Spring Security + JWT |
| Build Tool | Maven                 |
| API Style  | RESTful               |
| Docs       | Swagger (OpenAPI)     |
| DevOps     | Docker                |

---

## 📁 Project Structure

```
src/main/java/com/Community/demo

├── controller/      # REST controllers
├── services/        # Business logic
├── repository/      # Database layer
├── model/           # Entities
├── payload/         # DTOs
├── security/        # JWT & security config
├── exception/       # Global exception handling
├── config/          # App configurations (Swagger, etc.)
```

---

## 🛠️ Local Setup

### 1. Clone Repository

```bash
git clone https://github.com/pranta2003/Community_Health_Checkup_and_Tele-medicine_Portal-Backend-Part
cd Community_Health_Checkup_and_Tele-medicine_Portal-Backend-Part
```

### 2. Configure Database

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/doctor_portal_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
server.port=8081
```

### 3. Run Application

```bash
mvn clean install
mvn spring-boot:run
```

---

## 📌 Example API

**POST /api/appointments**

```json
{
  "patientName": "Pranta",
  "patientEmail": "p@gmail.com",
  "doctorName": "Dr. Rahman",
  "appointmentDate": "2025-11-19"
}
```

---

## 📈 Future Improvements

* AI-based health prediction system
* Advanced analytics dashboard
* Microservices architecture
* Cloud deployment (AWS / Docker orchestration)

---

## 🤝 Contribution

Contributions are welcome.

* Fork the repository
* Create a feature branch
* Submit a pull request

---

## ⭐ Final Note

This project represents a practical attempt to build a real-world healthcare backend system with clean architecture and production-ready practices.

If you find it useful, consider giving it a ⭐
