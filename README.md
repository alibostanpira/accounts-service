# Accounts Service

A RESTful microservice for managing customer accounts, built with Spring Boot.

## API Endpoints

### Create Account
- **Endpoint:** `POST /api/create`
- **Content-Type:** `application/json`
- **Request Body:** CustomerDTO
- **Success Response:** 201 Created
- **Description:** Creates a new customer account

### Fetch Account Details
- **Endpoint:** `GET /api/fetch`
- **Query Param:** mobileNumber
- **Success Response:** 200 OK
- **Response Body:** CustomerDTO
- **Description:** Retrieves account details by mobile number

### Update Account
- **Endpoint:** `PUT /api/update`
- **Content-Type:** `application/json`
- **Request Body:** CustomerDTO
- **Success Response:** 200 OK
- **Error Response:** 500 Internal Server Error
- **Description:** Updates existing customer account details

### Delete Account
- **Endpoint:** `DELETE /api/delete`
- **Query Param:** mobileNumber
- **Success Response:** 200 OK
- **Error Response:** 500 Internal Server Error
- **Description:** Deletes an account by mobile number

## Tech Stack
- Java
- Spring Boot
- Spring Web
- Lombok
- Maven/Gradle (depending on your build tool)

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven
- Your favorite IDE (IntelliJ IDEA, Eclipse, etc.)

### Running the Application
1. Clone the repository
2. Navigate to the project directory
3. Run the application:
   ```bash
   ./mvnw spring-boot:run   # for Maven