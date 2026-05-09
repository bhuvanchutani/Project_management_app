# Project Management Web Application

Production-ready full-stack Project Management Web Application with:
- React + Vite + Tailwind frontend
- Spring Boot + Spring Security + JWT backend
- PostgreSQL for relational data
- MongoDB for logs/comments/notifications
- RBAC (ADMIN, MEMBER)

## 1) Project Structure

```text
project/
  backend/
    src/main/java/com/pm/backend/
      config/ controller/ dto/ entity/ exception/ repository/ security/ service/ util/
    src/main/resources/application.yml
    pom.xml
  frontend/
    src/
      components/ context/ hooks/ layouts/ pages/ services/ utils/
    package.json
```

## 2) Backend Setup (Spring Boot)

Created key files:
- `backend/pom.xml`: Spring Boot, Security, JPA, Validation, MongoDB, JWT deps.
- `backend/src/main/resources/application.yml`: env-based DB, Mongo, JWT and CORS config.
- `backend/src/main/java/com/pm/backend/config/SecurityConfig.java`: stateless JWT security + RBAC.
- `backend/src/main/java/com/pm/backend/security/JwtAuthenticationFilter.java`: request token validation.
- `backend/src/main/java/com/pm/backend/security/JwtService.java`: token generation and parsing.

## 3) Database Configuration

### SQL (PostgreSQL)
Tables:
- `users`
- `projects`
- `tasks`
- `project_members` (many-to-many join)

### MongoDB
Collections:
- `activity_logs`
- `comments`
- `notifications`

## 4) Entity Creation

JPA entities:
- `User`: id, name, email, password, role, createdAt.
- `Project`: id, title, description, createdBy, status, deadline, members.
- `Task`: id, title, description, assignedTo, project, createdBy, status, priority, dueDate.

Mongo documents:
- `ActivityLog`: userId, action, entityType, entityId, timestamp.
- `Comment`: taskId, userId, content, createdAt.
- `Notification`: userId, title, message, read, createdAt.

## 5) Security and JWT

Implemented:
- BCrypt password hashing
- JWT token issue on register/login
- JWT filter + protected routes
- method-level security (`@PreAuthorize`)
- CORS allowlist via env var

## 6) REST API Endpoints

### AUTH
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

### PROJECT
- `GET /api/projects`
- `POST /api/projects`
- `GET /api/projects/{id}`
- `PUT /api/projects/{id}`
- `DELETE /api/projects/{id}`

### TASK
- `GET /api/tasks?page=0&size=10`
- `POST /api/tasks`
- `PUT /api/tasks/{id}`
- `DELETE /api/tasks/{id}`

### USER
- `GET /api/users`
- `GET /api/users/{id}`

### DASHBOARD
- `GET /api/dashboard/metrics`

## 7) RBAC Rules

ADMIN:
- full project/task CRUD
- team/user listing

MEMBER:
- sees assigned tasks
- can update only own tasks
- project access only when member

## 8) Frontend Setup

Created major files:
- `frontend/src/context/AuthContext.jsx`: auth state persistence + logout
- `frontend/src/components/ProtectedRoute.jsx`: route guarding
- `frontend/src/layouts/AppLayout.jsx`: dashboard shell/sidebar
- `frontend/src/pages/*`: Login, Signup, Dashboard, Projects, Project Details, Tasks, Team, Profile
- `frontend/src/services/api.js`: Axios client + JWT interceptor

## 9) Dashboard UI

Includes:
- KPI cards (projects/tasks/completed/pending/overdue)
- Bar chart using Chart.js
- responsive dark SaaS layout

## 10) ER Diagram (Textual)

- One `User` creates many `Project`
- Many `User` belong to many `Project` via `project_members`
- One `Project` has many `Task`
- One `User` can be assigned many `Task`
- One `User` can create many `Task`

## 11) Run Locally

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

## 12) Railway Deployment

Deploy as two Railway services:

1. **Backend service**
   - Root directory: `backend`
   - Build command: `mvn -DskipTests package`
   - Start command: `java -jar target/*.jar`
   - Add env vars from `backend/.env.example`

2. **Frontend service**
   - Root directory: `frontend`
   - Build command: `npm install && npm run build`
   - Start command: serve static build (Railway Nixpacks auto-detects; or use Dockerfile)
   - Set `VITE_API_BASE_URL` to backend public URL + `/api`

Recommended Railway add-ons:
- PostgreSQL plugin
- MongoDB plugin

## 13) Environment Variables

- `backend/.env.example`
- `frontend/.env.example`

Copy to `.env` for local development.

## 14) Production Notes

- Keep strong `JWT_SECRET` (minimum 32+ chars)
- Restrict `CORS_ALLOWED_ORIGINS`
- Use managed PostgreSQL + Mongo backups
- Configure HTTPS domains in Railway
