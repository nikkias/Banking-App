# Render Launch Checklist

## Namen

Use these exact names in Render:

```text
Project name:        bank-platform
Environment group:  bank-platform-env
Database:            bank-platform-db
Backend service:     bank-platform-backend
Frontend service:    bank-platform-frontend
Region:              Frankfurt or Oregon, use the same region for all resources
Runtime environment: Docker
```

Render uses the word `Environment` differently depending on the screen:

- For the service runtime, choose `Docker`.
- For shared variables, create an `Environment Group` named `bank-platform-env` if you want one.
- For project organization, create/select project `bank-platform`.

## Before Render

Run or confirm locally:

```powershell
git status
mvn test
```

If Docker is installed:

```powershell
docker compose up --build
```

Push to GitHub:

```powershell
git init
git add .
git commit -m "feat: prepare bank platform render deployment"
git branch -M main
git remote add origin https://github.com/<your-user>/bank-platform.git
git push -u origin main
```

If Git already exists, use only:

```powershell
git add .
git commit -m "feat: prepare bank platform render deployment"
git push
```

## Render Blueprint Path

The repo contains:

```text
render.yaml
```

In Render:

1. Click `New`.
2. Select `Blueprint`.
3. Connect GitHub.
4. Select repository `bank-platform`.
5. Confirm Render reads `render.yaml`.
6. Use project name `bank-platform`.

## Database Values

Render creates `bank-platform-db`. Open the database page and collect:

```text
Host
Port
Database
User
Password
```

Then build the backend JDBC URL:

```text
jdbc:postgresql://<host>:<port>/<database>
```

Example shape:

```text
jdbc:postgresql://dpg-xxxxx-a.frankfurt-postgres.render.com:5432/bank_platform
```

## Backend Environment Variables

For `bank-platform-backend` set:

```text
SPRING_PROFILES_ACTIVE=postgres
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:<port>/<database>
SPRING_DATASOURCE_USERNAME=<render-db-user>
SPRING_DATASOURCE_PASSWORD=<render-db-password>
```

Do not commit database passwords to Git.

After deploy, backend URL will look like:

```text
https://bank-platform-backend.onrender.com
```

Check backend health:

```text
https://bank-platform-backend.onrender.com/actuator/health
```

Expected response shape:

```json
{"status":"UP"}
```

## Frontend Environment Variables

For `bank-platform-frontend` set:

```text
BACKEND_URL=https://bank-platform-backend.onrender.com
```

If Render gives the backend a slightly different URL, use the exact backend URL from Render.

Frontend public link will look like:

```text
https://bank-platform-frontend.onrender.com
```

This is the link you share.

## Demo Logins

```text
employee / employee123
customer / customer123
admin    / admin123
```

Investor demo flow:

1. Open frontend link.
2. Login as `employee`.
3. Create a customer with `ownerUsername=customer`.
4. Open account with starting balance.
5. Deposit and withdraw.
6. Open account detail.
7. Make transfer.
8. Logout.
9. Login as `customer` and show only owned accounts.
10. Logout.
11. Login as `admin` and show Audit Log.

## If The Deploy Fails

### Backend fails to start

Check:

- `SPRING_DATASOURCE_URL` starts with `jdbc:postgresql://`
- database username/password are correct
- database and backend are in same Render region
- logs do not show Flyway migration error

### Frontend opens but API fails

Check:

- `BACKEND_URL` is set on frontend service
- value has no trailing `/api`
- correct: `https://bank-platform-backend.onrender.com`
- wrong: `https://bank-platform-backend.onrender.com/api`

### Login fails

Check:

- backend service is healthy
- frontend `BACKEND_URL` points to backend
- browser Network tab shows `/api/auth/me`

## What I Can Do Next

I can prepare:

- GitHub Actions CI/CD
- Render-specific hardening
- OpenAPI Swagger page
- production secrets checklist
- demo seed data
- custom domain checklist

## What I Cannot Do Without You

I cannot complete these without your account/session:

- click Render UI as you
- connect your GitHub account
- create paid resources
- read or generate your Render secrets
- purchase or configure a domain
- produce the final live URL before Render deploys the services
