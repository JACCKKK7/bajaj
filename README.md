# Bajaj Finserv Health - JAVA Qualifier

## Candidate Information
- **Name:** Akash Jonnalagadda
- **Roll Number:** 22BCE20488
- **Email:** jonnalagaddaakash777@gmail.com

## Project Description
This is a Spring Boot application built for the Bajaj Finserv Health Java Qualifier assignment. The application:
1. Sends a POST request on startup to generate a webhook URL and access token
2. Determines the SQL question based on the registration number (even/odd)
3. Submits the SQL solution to the webhook URL using JWT authentication

## Registration Number Analysis
- **Registration Number:** 22BCE20488
- **Last Two Digits:** 88 (EVEN)
- **Assigned Question:** Question 2

## Technologies Used
- Java 17
- Spring Boot 3.2.0
- Spring WebFlux (WebClient)
- Maven
- Lombok

## Project Structure
```
src/
├── main/
│   ├── java/com/bajajfinserv/qualifier/
│   │   ├── BajajQualifierApplication.java
│   │   ├── config/
│   │   │   ├── AppConfig.java
│   │   │   └── WebClientConfig.java
│   │   ├── model/
│   │   │   ├── WebhookRequest.java
│   │   │   ├── WebhookResponse.java
│   │   │   └── SolutionRequest.java
│   │   ├── service/
│   │   │   └── QualifierService.java
│   │   └── runner/
│   │       └── StartupRunner.java
│   └── resources/
│       └── application.properties
└── pom.xml
```

## How It Works

### 1. Startup Flow
The application uses `CommandLineRunner` to execute the logic immediately after startup, without requiring any controller endpoints.

### 2. Webhook Generation
Sends a POST request to:
```
POST https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA
```
With body:
```json
{
  "name": "Akash Jonnalagadda",
  "regNo": "22BCE20488",
  "email": "jonnalagaddaakash777@gmail.com"
}
```

### 3. SQL Query Determination
- Registration number ends in 88 (even)
- Therefore, Question 2 is selected
- The SQL query needs to be updated in `QualifierService.getSqlQueryForQuestion2()`

### 4. Solution Submission
Sends the SQL solution to the webhook URL received in step 2:
```
POST <webhook_url>
Authorization: <accessToken>
Content-Type: application/json
```
With body:
```json
{
  "finalQuery": "YOUR_SQL_QUERY_HERE"
}
```

## Building the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build JAR
```bash
mvn clean package
```

The JAR file will be created in `target/bajaj-qualifier-1.0.0.jar`

### Run the Application
```bash
java -jar target/bajaj-qualifier-1.0.0.jar
```

## Configuration
All configuration is in `src/main/resources/application.properties`:
- User details (name, regno, email)
- API base URL and endpoints
- Logging levels

## Important Notes
1. **No Controllers:** The application has no REST controllers - all logic runs on startup
2. **WebClient:** Uses Spring WebFlux WebClient for HTTP requests
3. **JWT Authentication:** The access token from step 1 is used as Authorization header in step 2
4. **SQL Query:** The actual SQL query needs to be obtained from Question 2 and updated in the code

## Next Steps
1. Download Question 2 from the provided Google Drive link
2. Solve the SQL problem
3. Update the SQL query in `QualifierService.getSqlQueryForQuestion2()`
4. Build the JAR file
5. Test the application
6. Upload to GitHub
7. Submit the form

## Testing Locally
Run the application and check the logs:
```bash
mvn spring-boot:run
```

Look for log messages indicating:
- Webhook generation success
- SQL query determination
- Solution submission result

## License
This project is created for the Bajaj Finserv Health Qualifier assessment.
