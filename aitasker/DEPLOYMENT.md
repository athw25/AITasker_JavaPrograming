# AITasker — Deployment Guide

## Prerequisites

- Docker 24+
- Docker Compose v2+
- Microsoft SQL Server 2022 (running locally or separately)
- Gemini API key (optional — for AI features)

---

## 1. Prepare Environment

```bash
cp .env.example .env
```

Edit `.env` — required values:

```env
DB_HOST=host.docker.internal    # points to your local SQL Server
DB_PASSWORD=YourSQLPassword
JWT_SECRET=your-32-char-minimum-secret-key-here
```

`host.docker.internal` resolves to your host machine from inside Docker.
On Linux, ensure `/etc/hosts` has `127.0.0.1 host.docker.internal` or use `DB_HOST=172.17.0.1`.

---

## 2. Build and Run Backend

```bash
# Build image and start
docker compose up --build -d

# Check status
docker compose ps

# Check logs
docker compose logs -f aitasker-backend
```

App available at: `http://localhost:8080`

Swagger: `http://localhost:8080/swagger-ui/index.html`

---

## 3. Health Check

```bash
curl http://localhost:8080/actuator/health
```

Expected:
```json
{"status":"UP"}
```

---

## 4. Stop / Restart

```bash
# Stop
docker compose down

# Restart
docker compose restart aitasker-backend

# Rebuild after code change
docker compose up --build -d aitasker-backend
```

---

## 5. Production Deployment

**Set production profile:**

```env
SPRING_PROFILES_ACTIVE=prod
```

**Production checklist:**

```
□ DB_PASSWORD is strong and not the default
□ JWT_SECRET is at least 64 random characters
□ AI_GEMINI_API_KEY is set (if AI features needed)
□ MAIL credentials are configured
□ FILE_UPLOAD_DIR has write permissions
□ SPRING_PROFILES_ACTIVE=prod
□ SQL Server is accessible from Docker container
□ Firewall: only port 8080 exposed (or behind reverse proxy)
```

**Behind Nginx (recommended):**

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_read_timeout 300s;
    }

    location /ws {
        proxy_pass http://localhost:8080/ws;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

---

## 6. Volumes

| Volume | Container Path | Purpose |
|---|---|---|
| `aitasker-uploads` | `/app/uploads` | Uploaded files |
| `aitasker-logs` | `/app/logs` | Application logs |

Backup volumes before update:

```bash
docker run --rm \
  -v aitasker-uploads:/data \
  -v $(pwd)/backup:/backup \
  alpine tar czf /backup/uploads-$(date +%Y%m%d).tar.gz /data
```

---

## 7. Manual Build (without Docker)

```bash
# Build JAR
./mvnw clean package -DskipTests

# Run with prod profile
java -Xms256m -Xmx512m \
  -Dspring.profiles.active=prod \
  -jar target/aitasker-0.0.1-SNAPSHOT.jar
```

---

## 8. Troubleshooting

| Symptom | Likely Cause | Fix |
|---|---|---|
| Container exits immediately | DB connection failed | Check `DB_HOST`, `DB_PASSWORD`, SQL Server running |
| 403 on `/api/expert/**` | Role mismatch | Ensure `Role.EXPERT` used, not `AI_EXPERT` |
| AI endpoints return empty | `AI_ENABLED=false` | Set `AI_ENABLED=true` and provide API key |
| File upload fails | Missing directory | Ensure `FILE_UPLOAD_DIR` exists and is writable |
| Swagger not loading | Missing OpenAPI config | Verify springdoc dependency in pom.xml |

Check application logs:

```bash
docker compose logs aitasker-backend | grep -i "error\|exception"
```
