# ShopeSphere Microservices - Setup Guide

## Overview

ShopeSphere is a Spring Boot microservices architecture with the following components:
- **API Gateway**: Route all requests (port 8080)
- **Auth Service**: User authentication and JWT token generation (port 8081)
- **Catalog Service**: Product catalog management (port 8082)
- **Order Service**: Order processing (port 8083)
- **Admin Service**: Administrative operations with Feign clients (port 8084)
- **Eureka Server**: Service discovery (port 8761)
- **Zipkin**: Distributed tracing (port 9411)
- **MySQL**: Persistent data storage
- **RabbitMQ**: Message broker for event-driven communication

## Architecture Features

### 1. **Service Discovery (Eureka)**
All microservices register with Eureka Server for dynamic service discovery. Services communicate via load-balanced service names rather than hardcoded IPs.

### 2. **API Gateway Routing**
The API Gateway (Spring Cloud Gateway) provides:
- Centralized routing to microservices
- JWT token validation
- Request/response filtering
- Path rewriting and stripping

Routes:
- `/gateway/auth/**` → Auth Service
- `/gateway/catalog/**` → Catalog Service
- `/gateway/orders/**` → Order Service
- `/gateway/admin/**` → Admin routes (products/orders)

### 3. **Inter-Service Communication (Feign)**
Admin Service uses OpenFeign clients to communicate with other microservices:
- **OrderClient**: Call Order Service endpoints
- **CatalogClient**: Call Catalog Service endpoints

The Feign clients automatically:
- Use service names registered in Eureka
- Handle load balancing
- Propagate user context via headers (X-User-Email, X-User-Role)

### 4. **Security & Authentication**
- JWT tokens issued by Auth Service
- API Gateway validates tokens for all protected routes
- User headers (email, role) propagated to downstream services
- Role-based access control (ADMIN, CUSTOMER)

### 5. **Docker Configuration**
All microservices use multi-stage Docker builds:
- **Build Stage**: Uses Maven to compile and package JAR
- **Runtime Stage**: Runs minimal JRE image
- Non-root user execution for security
- Health checks for container orchestration

## Prerequisites

- Docker & Docker Compose
- Java 17
- Maven 3.8+
- 4GB+ RAM for Docker containers

## Quick Start

### 1. Start All Services
```bash
cd /path/to/finalChecker2\ copy
docker-compose up -d
```

### 2. Verify Services
```bash
# Check Eureka Dashboard
open http://localhost:8761

# Check Zipkin Dashboard
open http://localhost:9411

# Check API Gateway
curl http://localhost:8080/gateway/catalog/products

# Check individual services
curl http://localhost:8081/auth/health
curl http://localhost:8082/catalog/health
curl http://localhost:8083/orders/health
curl http://localhost:8084/admin/health
```

### 3. Database Initialization
MySQL database is automatically initialized with `init-db.sql` on first run.

## Authentication Flow

1. **Signup/Login** via Auth Service:
   ```bash
   POST http://localhost:8080/gateway/auth/signup
   POST http://localhost:8080/gateway/auth/login
   ```

2. **Receive JWT Token**: Token included in response

3. **Use Token**: Include in Authorization header for protected endpoints:
   ```bash
   Authorization: Bearer <token>
   ```

4. **Token Validation**: API Gateway validates and extracts user info

## Feign Client Usage in Admin Service

The Admin Service has Feign clients that automatically discover and call other services:

```java
@FeignClient(name = "order-service")
public interface OrderClient {
  @GetMapping("/orders/{orderId}")
  Object getOrder(
      @PathVariable Long orderId,
      @RequestHeader("X-User-Email") String email,
      @RequestHeader("X-User-Role") String role
  );
}
```

Usage:
```java
@Autowired
private OrderClient orderClient;

// Call Order Service
Object order = orderClient.getOrder(123, userEmail, userRole);
```

## Configuration

### Environment Variables (in docker-compose.yml)

```yaml
DB_PASSWORD: Jamikhan@09
RABBITMQ_USERNAME: guest
RABBITMQ_PASSWORD: guest
APP_JWT_SECRET: mySuperSecretKeyThatIsAtLeast32CharactersLong!!
APP_JWT_EXPIRATION_MS: 3600000
EUREKA_URL: http://eureka-server:8761/eureka
```

### Per-Service Configuration

Each service has `application.properties`:
- Database credentials
- Eureka registration details
- JWT configuration
- RabbitMQ settings
- Swagger/OpenAPI settings

## Improvements Made

### 1. **Fixed Compilation Errors**
- Removed unused imports
- Updated deprecated JWT methods to new JJWT API
- Fixed Deprecated SignatureAlgorithm usage

### 2. **Added Feign Client Support**
- Added `spring-cloud-starter-openfeign` dependency to admin, order, and catalog services
- Created `OrderClient` interface for calling Order Service
- Created `CatalogClient` interface for calling Catalog Service
- Enabled Feign clients with `@EnableFeignClients` annotation

### 3. **Enhanced Docker Configuration**
- Multi-stage builds for smaller images
- Dependency caching optimization
- Non-root user execution
- Health checks in containers
- Proper working directories

### 4. **Updated application.properties**
- Added Actuator endpoints for health checks
- Enhanced logging configuration
- Better environment variable handling
- Consistent configuration across services

### 5. **Docker Compose Improvements**
- Proper service dependencies with health checks
- Environment variable configuration
- Volume mounting for databases
- Network configuration for inter-service communication
- Named volumes for data persistence

## Monitoring

### Health Checks
```bash
# Check service health
curl http://localhost:8084/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8081/actuator/health
```

### Logs
```bash
# View all container logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f auth-service
docker-compose logs -f order-service
```

### Eureka Dashboard
Visit `http://localhost:8761` to see all registered services and their status.

### Zipkin Dashboard
Visit `http://localhost:9411` to view distributed traces across all microservices. You can:
- Search for traces by service name, tags, or duration
- View detailed span information for each request
- Analyze performance bottlenecks and latency issues
- Debug inter-service communication problems

## Troubleshooting

### Services Not Starting
1. Check logs: `docker-compose logs service-name`
2. Verify ports are available
3. Check Docker daemon is running

### Service Discovery Issues
1. Verify Eureka Server is healthy: `http://localhost:8761`
2. Check service names in application.properties
3. Ensure all services are connected to same network

### Database Connection Issues
1. Verify MySQL container is running: `docker-compose logs mysql`
2. Check database credentials in docker-compose.yml
3. Ensure init-db.sql is correctly formatted

### Feign Client Issues
1. Verify service name matches Eureka registration
2. Check X-User-Email and X-User-Role headers are included
3. Verify downstream service is responding

## API Examples

### Authentication
```bash
# Signup
curl -X POST http://localhost:8080/gateway/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "role": "ROLE_CUSTOMER"
  }'

# Login
curl -X POST http://localhost:8080/gateway/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### Catalog Operations
```bash
# Get all products
curl -X GET http://localhost:8080/gateway/catalog/products \
  -H "Authorization: Bearer <token>"

# Create product (admin only)
curl -X POST http://localhost:8080/gateway/admin/products \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Product Name",
    "price": 99.99,
    "stock": 100
  }'
```

### Order Operations
```bash
# Create order
curl -X POST http://localhost:8080/gateway/orders/checkout \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{...order data...}'

# Get orders
curl -X GET http://localhost:8080/gateway/orders \
  -H "Authorization: Bearer <token>"
```

## Stopping Services

```bash
# Stop all containers
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Restart services
docker-compose restart
```

## Next Steps

1. Deploy to Kubernetes for production
2. Add API rate limiting and throttling
3. Implement distributed caching (Redis)
4. Add centralized logging (ELK stack)
5. Implement circuit breaker pattern (Spring Cloud CircuitBreaker)
6. Add API versioning and backwards compatibility

---

For questions or issues, check individual service logs or the Eureka dashboard.
