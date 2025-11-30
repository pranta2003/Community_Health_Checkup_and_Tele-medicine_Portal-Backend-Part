# 🌐 Community Health Check & Telemedicine Portal  
A modern full-stack medical portal for small clinics & NGOs to manage community health checks, telemedicine sessions, and basic health utilities — powered by **Spring Boot (Backend)** and a clean **Tailwind + DaisyUI (Frontend)**.

<div align="center">

![size](https://img.shields.io/badge/Project%20Size-Medium-blue?style=for-the-badge)
![license](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)
![development](https://img.shields.io/badge/Status-Development-yellow?style=for-the-badge)
![spring](https://img.shields.io/badge/Backend-SpringBoot-brightgreen?style=for-the-badge)
![frontend](https://img.shields.io/badge/Frontend-TailwindCSS-blue?style=for-the-badge)

</div>

---

---

## 🎞️ System Flow Animations (Backend-Focused)

To better visualize how the backend of the Community Health Check & Telemedicine Portal works, here are a set of conceptual animations that illustrate the architecture, request flow, API routing, authentication, and data lifecycle of the system.

These animations help explain the internal process without requiring a live demo.

---

### 🔄 **Overall System Architecture**
A high-level visual of how the backend, database, security layer, and frontend communicate.

![System Architecture](https://raw.githubusercontent.com/PKP-Assets/health-system-architecture/main/architecture.gif)

---

### 🔐 **JWT Authentication Flow**
This animation demonstrates how login, token generation, verification, and protected routes work.

![JWT Flow](https://raw.githubusercontent.com/PKP-Assets/health-system-architecture/main/jwt-flow.gif)

---

### 📡 **API Request Lifecycle**
Shows how an incoming request moves through Controller → Service → Repository → Database → Response.

![API Flow](https://raw.githubusercontent.com/PKP-Assets/health-system-architecture/main/api-lifecycle.gif)

---

### 🗂️ **Appointment Creation Flow**
Illustrates the process of creating an appointment using REST endpoints.

![Appointment Flow](https://raw.githubusercontent.com/PKP-Assets/health-system-architecture/main/appointment.gif)

---

### 🧠 **Error Handling & Global Exceptions**
Explains how global exception handlers catch errors and return JSON ApiResponses.

![Exception Handling](https://raw.githubusercontent.com/PKP-Assets/health-system-architecture/main/exceptions.gif)

---

### 🔗 **Database Operations (CRUD)**
An animation showing how create/read/update/delete operations are handled using Spring Data JPA.

![CRUD Operations](https://raw.githubusercontent.com/PKP-Assets/health-system-architecture/main/crud.gif)

---

### 🩺 **Health Utilities (BMI & Glucose)**
Conceptual flow of how BMI and glucose calculations pass through your backend layers.

![Health Flow](https://raw.githubusercontent.com/PKP-Assets/health-system-architecture/main/health-flow.gif)

---

> 💡 *All animations are conceptual — not tied to your UI — and perfectly suitable for backend documentation.*



---

## 🚀 Key Features

### 🩺 **Appointment Management**
- Patients & doctors can book/manage appointments.
- Clean RESTful API endpoints.
- Calendar & time-slot selection (future enhancement-ready).

### 📞 **Telemedicine (Video Call)**
- Jitsi Meet API integrated.
- One-click online consultation.

### 🧮 **Health Utilities**
- BMI Calculator  
- Glucose Logging  
- Extendable design for future vitals (BP, SPO₂, etc.)

### 🔐 **Security & Auth**
- JWT Authentication  
- Role-based Access Control → Admin, Doctor, Patient  
- Secure API layer with CORS setup  

### 🧱 **Clean Architecture**
- DTO-driven communication  
- Layered structure → Controller → Service → Repository  
- Global Exception Handlers  
- Reusable ApiResponse model  

---

## 📁 Project Structure (Visual Tree)

doctor-portal/
│
├─ src/
│ ├─ main/
│ │ ├─ java/com/pranta/doctor_portal/
│ │ │ ├─ appointment/ # Appointment model, DTOs, controller, service, repo
│ │ │ ├─ user/ # User roles, security config, auth, services
│ │ │ ├─ health/ # BMI + Glucose controllers + DTOs
│ │ │ ├─ contact/ # Contact form handling
│ │ │ ├─ common/ # ApiResponse, global exceptions
│ │ │ └─ DoctorPortalApplication.java
│ │ │
│ │ └─ resources/
│ │ ├─ application.properties
│ │ └─ static/
│ │
│ └─ test/
│
└─ pom.xml


---

## ⚙️ Requirements

| Dependency | Version |
|-----------|----------|
| ☕ Java | **17+** |
| 📦 Maven | Latest |
| 🗄️ MySQL | 8.x (or any datasource you configure) |
| 🖥️ Optional | XAMPP for local DB |

---

## 🛠️ Local Setup Guide

### **1️⃣ Clone the repository**
```bash
git clone https://github.com/yourusername/doctor-portal.git
cd doctor-portal
2️⃣ Configure MySQL in application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/doctor_portal_db
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
server.port=8081

3️⃣ Build & Run
mvn clean package
mvn spring-boot:run


➡ Backend will run at:
http://localhost:8081

🔗 API Endpoints (Examples)
➤ Create Appointment

POST /api/appointments
Body

{
  "patientName": "Pranta",
  "patientEmail": "p@gmail.com",
  "patientPhone": "0177777777",
  "doctorName": "Dr. Rahman",
  "appointmentDate": "2025-11-19",
  "appointmentTime": "14:30:00"
}

➤ List All Appointments
GET /api/appointments

➤ Get Appointment by ID
GET /api/appointments/{id}

🤝 Contributing

Fork the repository

Create a new branch

git checkout -b feature/your-feature


Commit & push

Open a Pull Request with clear description

⭐ If this project helps you, don't forget to star the repo!


