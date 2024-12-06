# HTTP Server in Java

A simple HTTP server in Java that handles basic operations for managing students with the following HTTP methods:

- **GET /aluno/{id}**: Retrieves a student by ID.
- **POST /aluno**: Creates a new student with a unique ID.
- **DELETE /aluno/{id}**: Deletes a student by ID.

## Prerequisites
- Java 8 or higher.

## Running the Server

1. **Compile the Code**:
   ```bash
   javac HttpServer.java
   ```

2. **Run the Server**:
   ```bash
   java HttpServer
   ```

The server will run on port 8080.

## Testing the Server

1. **GET /aluno/{id}**:
   - **Request**: `GET http://localhost:8080/aluno/1`
   - **Response**: 
     - `200 OK` if the student exists.
     - `404 Not Found` if the student doesn't exist.

2. **POST /aluno**:
   - **Request**: `POST http://localhost:8080/aluno`
   - **Response**: `201 Created` with the new student's ID.

3. **DELETE /aluno/{id}**:
   - **Request**: `DELETE http://localhost:8080/aluno/1`
   - **Response**:
     - `200 OK` if the student is deleted.
     - `404 Not Found` if the student doesn't exist.
