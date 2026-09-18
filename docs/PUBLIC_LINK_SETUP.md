# Public 24/7 Link Setup

## Ziel

Du willst einen öffentlichen Link, den du Investoren, Dozenten oder Recruitern geben kannst. Dafür braucht die App:

- einen GitHub Remote
- einen Backend-Host
- eine PostgreSQL-Datenbank
- einen Frontend-Host
- HTTPS
- Environment Variables statt lokaler Passwörter

## Schnellste Route: Render

Render ist für den ersten öffentlichen MVP-Link am schnellsten.

Das Repo enthält bereits einen Render Blueprint: [../render.yaml](../render.yaml). Du kannst entweder diesen Blueprint verwenden oder die Services manuell anlegen.

Für die kurze Klick-für-Klick-Version nutze [RENDER_LAUNCH_CHECKLIST.md](RENDER_LAUNCH_CHECKLIST.md).

### Variante A: Render Blueprint

1. Projekt zu GitHub pushen.
2. Render öffnen: `https://render.com`
3. `New` -> `Blueprint`
4. GitHub Repository auswählen.
5. Render liest `render.yaml`.
6. Die fehlenden Secret-/Environment-Werte eintragen:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://<render-db-host>:5432/bank_platform
SPRING_DATASOURCE_USERNAME=<render-db-user>
SPRING_DATASOURCE_PASSWORD=<render-db-password>
BACKEND_URL=https://<dein-backend-service>.onrender.com
```

7. Deploy starten.
8. Den Frontend-Link öffnen.

Der öffentliche Link ist danach die URL des Frontend-Service, zum Beispiel:

```text
https://bank-platform-frontend.onrender.com
```

### Variante B: Manuell

#### 1. GitHub Repository erstellen

```powershell
git init
git add .
git commit -m "feat: prepare investor banking mvp"
git branch -M main
git remote add origin https://github.com/<dein-user>/<repo-name>.git
git push -u origin main
```

#### 2. PostgreSQL auf Render erstellen

1. Render öffnen: `https://render.com`
2. `New` -> `PostgreSQL`
3. Name: `bank-platform-db`
4. Region wählen
5. Datenbank erstellen
6. `Internal Database URL` kopieren

#### 3. Backend als Web Service deployen

1. `New` -> `Web Service`
2. GitHub Repository verbinden
3. Runtime: `Docker`
4. Root Directory: leer lassen, wenn das Repo direkt im Projektroot liegt
5. Dockerfile Path: `./Dockerfile`
6. Environment Variables setzen:

```text
SPRING_PROFILES_ACTIVE=postgres
SPRING_DATASOURCE_URL=<Render internal postgres jdbc url>
SPRING_DATASOURCE_USERNAME=<db user>
SPRING_DATASOURCE_PASSWORD=<db password>
```

Wichtig: Render zeigt oft eine PostgreSQL URL im Format `postgres://...`. Spring Boot braucht JDBC:

```text
jdbc:postgresql://HOST:PORT/DATABASE
```

Nach Deploy bekommst du einen Link wie:

```text
https://bank-platform-backend.onrender.com
```

#### 4. Frontend deployen

Variante A: Frontend als Render Web Service mit Docker.

1. `New` -> `Web Service`
2. Gleiches GitHub Repository wählen
3. Runtime: `Docker`
4. Root Directory: `frontend`
5. Dockerfile Path: `./Dockerfile`
6. Deploy starten

Danach bekommst du einen Link wie:

```text
https://bank-platform-frontend.onrender.com
```

#### 5. API-Verbindung prüfen

Für getrennte Render-Services muss das Frontend auf die Backend-URL zeigen. Aktuell nutzt Production `apiUrl: '/api'`, was ideal ist, wenn Nginx Backend proxyt. Für getrennte Services brauchst du entweder:

- Nginx Proxy auf die echte Backend-URL anpassen
- oder Angular Production Environment auf Backend-URL setzen

Empfohlen für Render: Setze im Frontend-Service die Environment Variable `BACKEND_URL` auf die Backend-URL. Der Nginx-Container nutzt diese Variable automatisch.

## Professionelle Route: Azure Container Apps

Azure ist stärker für Enterprise-/Investor-Demos.

### 1. Voraussetzungen

- Azure Account
- Azure CLI installiert
- Docker installiert
- GitHub Repository

### 2. Login

```powershell
az login
az account set --subscription "<subscription-id>"
```

### 3. Resource Group

```powershell
az group create --name rg-bank-platform --location westeurope
```

### 4. Container Registry

```powershell
az acr create --resource-group rg-bank-platform --name <uniqueRegistryName> --sku Basic
az acr login --name <uniqueRegistryName>
```

### 5. Images bauen und pushen

```powershell
docker build -t <uniqueRegistryName>.azurecr.io/bank-backend:latest .
docker build -t <uniqueRegistryName>.azurecr.io/bank-frontend:latest ./frontend
docker push <uniqueRegistryName>.azurecr.io/bank-backend:latest
docker push <uniqueRegistryName>.azurecr.io/bank-frontend:latest
```

### 6. PostgreSQL erstellen

```powershell
az postgres flexible-server create `
  --resource-group rg-bank-platform `
  --name <unique-postgres-name> `
  --location westeurope `
  --admin-user bankadmin `
  --admin-password "<strong-password>" `
  --sku-name Standard_B1ms `
  --tier Burstable `
  --storage-size 32 `
  --version 16
```

### 7. Container Apps Environment

```powershell
az containerapp env create `
  --name bank-platform-env `
  --resource-group rg-bank-platform `
  --location westeurope
```

### 8. Backend Container App

```powershell
az containerapp create `
  --name bank-backend `
  --resource-group rg-bank-platform `
  --environment bank-platform-env `
  --image <uniqueRegistryName>.azurecr.io/bank-backend:latest `
  --target-port 8080 `
  --ingress external `
  --env-vars `
    SPRING_PROFILES_ACTIVE=postgres `
    SPRING_DATASOURCE_URL="jdbc:postgresql://<postgres-host>:5432/postgres" `
    SPRING_DATASOURCE_USERNAME="bankadmin" `
    SPRING_DATASOURCE_PASSWORD="<strong-password>"
```

### 9. Frontend Container App

```powershell
az containerapp create `
  --name bank-frontend `
  --resource-group rg-bank-platform `
  --environment bank-platform-env `
  --image <uniqueRegistryName>.azurecr.io/bank-frontend:latest `
  --target-port 80 `
  --ingress external
```

Azure gibt dir danach eine URL wie:

```text
https://bank-frontend.<region>.azurecontainerapps.io
```

## Was ich von dir brauche, um es exakt fertig zu machen

Wähle eine Plattform:

```text
1. Render: schnellster öffentlicher Link
2. Azure Container Apps: professionellste Cloud-Richtung
3. VPS: günstig, aber mehr Betrieb
```

Wenn du Azure willst, brauche ich:

- Azure Subscription ID
- gewünschte Region, z. B. `westeurope`
- Registry-Name, weltweit eindeutig
- PostgreSQL Server Name, weltweit eindeutig

Wenn du Render willst, brauche ich:

- GitHub Repo URL
- ob Backend und Frontend als zwei Services oder gemeinsamer Docker-Stack laufen sollen

## Ehrliche Grenze

Ich kann dir alle Dateien, Befehle und CI/CD-Definitionen erstellen. Den endgültigen öffentlichen Link kann ich erst erzeugen, wenn du dich bei Azure/Render anmeldest und die notwendigen Ressourcen erstellen lässt, weil dafür Account, Berechtigungen, Kosten und Secrets nötig sind.