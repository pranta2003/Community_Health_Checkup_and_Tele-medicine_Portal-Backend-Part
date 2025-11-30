# Community Health Check & Telemedicine Portal

[![Repo size](https://img.shields.io/badge/size-medium-blue.svg)]()
[![License](https://img.shields.io/badge/license-MIT-green.svg)]()
[![Status](https://img.shields.io/badge/status-development-yellow.svg)]()

> A lightweight portal to let small clinics and NGOs manage community health checks and offer telemedicine consultations — built with Spring Boot (backend) and a responsive frontend (Tailwind + DaisyUI).

---

## 🎬 Demo / Animated Preview
> Add an animated GIF here (record a short screen demo and upload it to the repo, then replace the GIF URL below)

![Demo GIF](docs/demo.gif)

---

## 🚀 Quick highlights
- Book / manage appointments (doctor + patient)
- Teleconsultation (Jitsi integration)
- Basic health utilities (BMI, glucose logging)
- JWT-based authentication & role-based access control
- RESTful API design, DTOs, and structured service-repository layers

---

## 📁 Project structure
doctor-portal/
├─ src/
│ ├─ main/
│ │ ├─ java/com/pranta/doctor_portal/
│ │ │ ├─ appointment/ # Appointment model, DTOs, controller, service, repository
│ │ │ ├─ user/ # User, roles, security, user service
│ │ │ ├─ health/ # BMI/glucose controllers + DTOs
│ │ │ ├─ contact/ # Contact form message
│ │ │ ├─ common/ # ApiResponse, error handlers, global exceptions
│ │ │ └─ DoctorPortalApplication.java
│ └─ resources/
│ ├─ application.properties
│ └─ static/
└─ pom.xml


---

## ⚙️ Requirements
- Java 17+ (or same JDK used by your project)
- Maven
- MySQL (or configure your datasource)
- Optional: XAMPP for local MySQL during development

---

## 🛠️ Setup (local)
1. Clone repository:
```bash
git clone https://github.com/yourusername/doctor-portal.git
cd doctor-portal

Configure src/main/resources/application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/doctor_portal_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
server.port=8081


Build and run:

mvn clean package
mvn spring-boot:run


Backend will run on http://localhost:8081 (unless changed).

📌 Important endpoints (examples)

Base URL: http://localhost:8081/api

POST /api/appointments — create appointment
Body (JSON):

{
  "patientName":"Pranta",
  "patientEmail":"p@gmail.com",
  "patientPhone":"0177777777",
  "doctorName":"Dr. Rahman",
  "appointmentDate":"2025-11-19",
  "appointmentTime":"14:30:00"
}


GET /api/appointments — list appointments

GET /api/appointments/{id} — get one appointment

🛠️ How to contribute

Fork the repo

Create a branch feature/your-feature

Commit & push

Open a PR with a clear description

Thank you — please star & fork if you find this useful! ⭐


---

## Extra: how to add *more animations* to README
- Record a short GIF of the running app (3–8 seconds) using a screen recorder (ShareX, Loom). Put `docs/demo.gif` into the repo and the README already references it.
- Use GitHub shields for build status, license, and dependency scan badges to make the README feel alive.
- Optionally embed a short YouTube walkthrough (screenshare) and place the YouTube link or thumbnail in the README.

---

## Wrap-up: Quick checklist for you to finish
1. Paste the README content into your `README.md`.
2. Add a demo GIF file at `docs/demo.gif` or replace the link with your GIF URL.
3. Use the secure CORS snippet in your `@Configuration` or `SecurityConfig` as shown.
4. When teacher asks about CORS: use the provided short explanation — remember to say **CORS is browser-side, and not a DDOS measure**.

---

If you want, I can:
- Produce a one-page printable cheat-sheet of the CORS commands & browser console text for you to show the teacher.
- Create the actual GIF-ready README section with example images (I can generate suggested ALT tags and exact markdown if you upload your GIFs/screenshots).

Which of those would you like next?
