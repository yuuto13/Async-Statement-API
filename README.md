# Statement Generation Service

A Spring Boot-based microservice for asynchronous account statement generation.

## Features
- Asynchronous statement generation
- Integration with mock Core Banking system
- PDF generation through mock Templating Engine
- Task status tracking
- Swagger API documentation

## Setup

### Installation
1. Clone the repository
2. Build & Run the project with bash command
```bash
mvn clean install
mvn spring-boot:run
```

## API Documentation
Access Swagger UI at: http://localhost:8080/swagger-ui.html

## Testing
Run all tests:
```bash
mvn test
```

## Design Choices Explanation

**1. Using Record Classes**
- **Immutability by default**: Ensures thread safety and predictable state
- **Concise syntax**: Reduces boilerplate code
- **No dependencies**: Eliminates need for additional dependencies such as Lombok

**2. Adding Cache Expiration**
- **Automatic cleanup**: Prevents memory overflow
- **Resource efficiency**: Self-maintaining storage
- **Simplified maintenance**: No manual cache management

**3. Using Virtual Threads**
- **High concurrency**: Enables handling thousands of simultaneous requests
- **Resource efficiency**: Lightweight compared to platform threads
- **Scalability**: Better utilization of server resources

**4. Swagger UI Integration**
- **Self-documenting API**: Always up-to-date documentation
- **Interactive testing**: Developers can try endpoints directly
- **Type safety**: Automatic parameter validation
- **Error documentation**: Clear response status definitions