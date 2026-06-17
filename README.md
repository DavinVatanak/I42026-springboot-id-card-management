# 🪪 ID Card Management System

A production-grade **Spring Boot 3** application for managing ID cards for **Students**, **Employees**, and **Normal Users**.

Features include CRUD profiles, photo upload, ID card template management, live HTML preview, QR code + barcode generation, PDF export, and batch processing.

---

## 📋 Table of Contents

- [Project Description](#-project-description)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Database Setup](#-database-setup)
- [Setup & Run](#-setup--run)
- [API Endpoints](#-api-endpoints)
- [Sample API Requests](#-sample-api-requests)
- [Testing](#-testing)
- [Screenshots](#-screenshots)
- [GitHub Upload Instructions](#-github-upload-instructions)

---

## 📝 Project Description

The **ID Card Management System** allows organizations to:

- Manage profiles for Students (`2026-STU-###`), Employees (`2026-EMP-###`), and Users (`2026-USR-###`)
- Upload profile photos (JPEG/PNG, max 5MB)
- Design reusable ID card templates with custom HTML/CSS
- Generate live HTML previews of ID cards
- Export individual or batch PDF ID cards (with embedded QR codes and barcodes)
- Verify cards via QR code URL

---

## 🛠 Tech Stack

| Layer         | Technology                           |
|---------------|--------------------------------------|
| Language      | Java 21                              |
| Framework     | Spring Boot 3.2.5                    |
| Build Tool    | Maven                                |
| Database      | MySQL 8+                             |
| ORM           | Spring Data JPA / Hibernate          |
| Mapping       | MapStruct 1.5.5                      |
| Boilerplate   | Lombok                               |
| Templating    | Thymeleaf                            |
| Security      | Spring Security 6 (stateless)        |
| API Docs      | SpringDoc OpenAPI 3 / Swagger UI     |
| PDF           | iText 8                              |
| QR Code       | ZXing 3.5.3                          |
| Barcode       | Barcode4J 2.1                        |
| Testing       | JUnit 5 + Mockito + MockMvc + H2     |

---

## 📁 Project Structure

```
id-card-management/
├── src/
│   ├── main/
│   │   ├── java/net/orderzone/idcard/
│   │   │   ├── IdCardManagementApplication.java
│   │   │   ├── config/
│   │   │   │   ├── OpenAPIConfig.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── WebMvcConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── ProfileController.java
│   │   │   │   ├── TemplateController.java
│   │   │   │   └── CardController.java
│   │   │   ├── dto/
│   │   │   │   ├── ProfileRequestDTO.java
│   │   │   │   ├── ProfileResponseDTO.java
│   │   │   │   ├── TemplateRequestDTO.java
│   │   │   │   ├── TemplateResponseDTO.java
│   │   │   │   ├── CardRequestDTO.java
│   │   │   │   └── BatchCardRequestDTO.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── DuplicateResourceException.java
│   │   │   │   └── InvalidFileException.java
│   │   │   ├── mapper/
│   │   │   │   ├── ProfileMapper.java
│   │   │   │   └── TemplateMapper.java
│   │   │   ├── model/
│   │   │   │   ├── Profile.java
│   │   │   │   ├── ProfileBuilder.java
│   │   │   │   ├── ProfileType.java
│   │   │   │   ├── Template.java
│   │   │   │   └── BarcodeType.java
│   │   │   ├── repository/
│   │   │   │   ├── ProfileRepository.java
│   │   │   │   └── TemplateRepository.java
│   │   │   ├── service/
│   │   │   │   ├── ProfileService.java
│   │   │   │   ├── TemplateService.java
│   │   │   │   ├── CardService.java
│   │   │   │   ├── FileStorageService.java
│   │   │   │   ├── QRCodeService.java
│   │   │   │   ├── BarcodeService.java
│   │   │   │   ├── PDFService.java
│   │   │   │   └── impl/
│   │   │   │       ├── ProfileServiceImpl.java
│   │   │   │       ├── TemplateServiceImpl.java
│   │   │   │       ├── CardServiceImpl.java
│   │   │   │       ├── FileStorageServiceImpl.java
│   │   │   │       ├── QRCodeServiceImpl.java
│   │   │   │       ├── BarcodeServiceImpl.java
│   │   │   │       └── PDFServiceImpl.java
│   │   │   └── util/
│   │   │       ├── RegistrationNumberGenerator.java
│   │   │       ├── QRCodeGenerator.java
│   │   │       └── BarcodeGenerator.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── schema.sql
│   └── test/java/net/orderzone/idcard/
│       ├── controller/ProfileControllerTest.java
│       ├── repository/ProfileRepositoryTest.java
│       └── service/
│           ├── ProfileServiceTest.java
│           └── TemplateServiceTest.java
├── uploads/           ← auto-created photo storage
└── pom.xml
```

---

## 🗄 Database Setup

### Prerequisites
- MySQL 8+ installed and running
- Create the database using the provided schema:

```bash
mysql -u root -p < src/main/resources/schema-mysql.sql
```

Or manually in MySQL Workbench / CLI:

```sql
CREATE DATABASE id_card_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Update credentials
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/id_card_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

---

## 🚀 Setup & Run

### Prerequisites
- Java 21 JDK
- Maven 3.9+
- MySQL 8+

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/YOUR_USERNAME/id-card-management.git
cd id-card-management

# 2. Set up the database (see above)

# 3. Configure application.properties with your DB password

# 4. Build the project
mvn clean install -DskipTests

# 5. Run the application
mvn spring-boot:run

# Or run the JAR directly:
java -jar target/id-card-management-1.0.0.jar
```

The application will be available at: **http://localhost:8080**

📚 **Swagger UI**: http://localhost:8080/swagger-ui.html

---

## 📡 API Endpoints

### Profile Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| `POST` | `/api/profiles` | Create a new profile |
| `GET` | `/api/profiles` | Get all profiles |
| `GET` | `/api/profiles?name=John` | Search profiles by name |
| `GET` | `/api/profiles?type=STUDENT` | Filter by profile type |
| `GET` | `/api/profiles/{id}` | Get profile by ID |
| `PUT` | `/api/profiles/{id}` | Update a profile |
| `DELETE` | `/api/profiles/{id}` | Delete a profile |
| `POST` | `/api/profiles/upload-photo` | Upload profile photo |

### Template Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| `POST` | `/api/templates` | Create a template |
| `GET` | `/api/templates` | Get all templates |
| `GET` | `/api/templates?activeOnly=true` | Get active templates only |
| `GET` | `/api/templates/{id}` | Get template by ID |
| `PUT` | `/api/templates/{id}` | Update a template |
| `DELETE` | `/api/templates/{id}` | Delete a template |
| `GET` | `/api/templates/{templateId}/preview/{profileId}` | Preview ID card HTML |

### Card Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| `POST` | `/api/cards/preview` | Live HTML preview |
| `GET` | `/api/cards/pdf/{profileId}` | Download PDF ID card |
| `POST` | `/api/cards/batch` | Batch PDF as ZIP |

---

## 🔬 Sample API Requests

### Create a Student Profile

```bash
curl -X POST http://localhost:8080/api/profiles \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john.doe@university.edu",
    "phone": "+855-12-345-678",
    "address": "Phnom Penh, Cambodia",
    "dateOfBirth": "2002-03-15",
    "profileType": "STUDENT"
  }'
```

### Upload a Photo

```bash
curl -X POST "http://localhost:8080/api/profiles/upload-photo?profileId=1" \
  -F "file=@/path/to/photo.jpg"
```

### Create a Template

```bash
curl -X POST http://localhost:8080/api/templates \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Standard ID Card",
    "htmlTemplate": "<div class=\"card\">[[${profile.fullName}]]</div>",
    "cssStyle": ".card { background: navy; color: white; }",
    "barcodeType": "CODE_128",
    "active": true
  }'
```

### Live Card Preview

```bash
curl -X POST http://localhost:8080/api/cards/preview \
  -H "Content-Type: application/json" \
  -d '{ "profileId": 1, "templateId": 1 }'
```

### Download PDF

```bash
curl -X GET http://localhost:8080/api/cards/pdf/1 \
  --output id_card_1.pdf
```

### Batch PDF as ZIP

```bash
curl -X POST http://localhost:8080/api/cards/batch \
  -H "Content-Type: application/json" \
  -d '{ "profileIds": [1, 2, 3], "templateId": 1 }' \
  --output batch_cards.zip
```

---

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run only service tests
mvn test -Dtest="ProfileServiceTest,TemplateServiceTest"

# Run controller tests
mvn test -Dtest="ProfileControllerTest"

# Run repository tests (uses H2 in-memory DB)
mvn test -Dtest="ProfileRepositoryTest"
```

### Test Coverage Summary

| Test Class | Layer | Tests |
|------------|-------|-------|
| `ProfileServiceTest` | Service | 6 tests (CRUD + duplicates) |
| `TemplateServiceTest` | Service | 5 tests |
| `ProfileControllerTest` | Controller (MockMvc) | 5 tests |
| `ProfileRepositoryTest` | Repository (DataJpa/H2) | 7 tests |

---

## 📸 Screenshots

> Place screenshots in a `screenshots/` folder and embed them here.

```
screenshots/
├── swagger-ui.png
├── create-profile.png
├── card-preview.png
└── pdf-output.png
```

---

## 📤 GitHub Upload Instructions

```bash
# 1. Initialize git (if not done)
git init

# 2. Add .gitignore
echo "target/" >> .gitignore
echo "uploads/" >> .gitignore
echo "*.log" >> .gitignore
echo ".env" >> .gitignore

# 3. Stage all files
git add .

# 4. Initial commit
git commit -m "feat: initial ID Card Management System"

# 5. Create repo on GitHub (replace with your username)
git remote add origin https://github.com/YOUR_USERNAME/id-card-management.git
git branch -M main

# 6. Push
git push -u origin main
```

> ⚠️ **Never commit `application.properties` with real passwords** to public repos.  
> Use environment variables or a `.env` file instead.

---

## 🔒 Security Notes

- Spring Security is configured in **stateless mode** with all `/api/**` endpoints open for development.
- In production, add JWT authentication to `SecurityConfig.java`.
- Store DB passwords in environment variables, not in `application.properties`.

---

## 👨‍💻 Author

Built with ❤️ using Spring Boot 3 + Java 21.
