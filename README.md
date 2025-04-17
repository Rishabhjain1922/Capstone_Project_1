# Event Management System - README

## 📌 Table of Contents
- [Project Overview](#-project-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Database Schema](#-database-schema)
- [API Endpoints](#-api-endpoints)
- [Setup Instructions](#-setup-instructions)
- [Security Implementation](#-security-implementation)
- [Frontend Integration](#-frontend-integration)

## 🌟 Project Overview
A Spring Boot event management system with:
- Organizer and attendee accounts
- Basic authentication (no JWT)
- Event creation and booking
- PostgreSQL database
- SQL injection protection

## 🚀 Features

### Organizer Features
- Create, update, and manage events
- View all created events
- Cancel events (soft delete)

### Attendee Features
- Browse upcoming events
- Book and cancel event reservations
- View booked events

## 💻 Tech Stack

### Backend
- **Framework**: Spring Boot
- **Database**: PostgreSQL
- **Security**: Spring Security with BCrypt
- **ORM**: Spring Data JPA/Hibernate

### Frontend
- HTML, CSS, JavaScript
- Bootstrap for styling
- Fetch API for backend communication

## 🗃 Database Schema

### Tables
```sql
CREATE TABLE attendees (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL
);

CREATE TABLE organizers (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    organization_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL
);

CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    event_date TIMESTAMP NOT NULL,
    organizer_id INTEGER REFERENCES organizers(id),
    status VARCHAR(20) DEFAULT 'ACTIVE'
);
```

## 📡 API Endpoints

### Authentication
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/register/attendee` | POST | Register attendee |
| `/api/auth/register/organizer` | POST | Register organizer |
| `/api/auth/login/attendee` | POST | Attendee login |
| `/api/auth/login/organizer` | POST | Organizer login |

### Events
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/events` | POST | Create event |
| `/api/events` | GET | List events |
| `/api/events/{id}` | GET | Get event details |
| `/api/events/{id}` | PUT | Update event |
| `/api/events/{id}` | DELETE | Cancel event |

## 🛠 Setup Instructions

### Requirements
- PostgreSQL
- Java 17+
- Maven

### Configuration
1. Create database:
   ```bash
   createdb eventdb
   ```

2. Update `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/eventdb
   spring.datasource.username=postgres
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   ```

3. Build and run:
   ```bash
   mvn spring-boot:run
   ```

## 🔒 Security Implementation

### Key Components
- BCrypt password hashing
- SQL injection protection
- Input validation
- Role-based access control

### SecurityConfig
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/events/**").authenticated()
                .anyRequest().permitAll()
            )
            .formLogin().disable()
            .logout().disable();
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## 🖥 Frontend Integration

### Key Files
- `login.html` - Authentication page
- `organizer.js` - Organizer event management
- `attendee.js` - Attendee booking logic

### Sample API Call
```javascript
fetch('/api/events', {
    method: 'GET',
    headers: {
        'Content-Type': 'application/json'
    }
})
.then(response => response.json())
.then(data => console.log(data));
```



