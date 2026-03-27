# Quick Reference - ShopeSphere Microservices

## 🚀 Quick Start
```bash
cd /Users/mjamik/Desktop/finalChecker2\ copy
docker-compose up -d
```

## 📡 Service Ports
- **API Gateway**: http://localhost:8080
- **Auth Service**: http://localhost:8081
- **Catalog Service**: http://localhost:8082
- **Order Service**: http://localhost:8083
- **Admin Service**: http://localhost:8084
- **Eureka Server**: http://localhost:8761
- **MySQL**: localhost:3306
- **RabbitMQ**: localhost:5672 (amqp), 15672 (management)

## 🔐 Health Checks
```bash
# All services have /actuator/health endpoint
curl http://localhost:8084/actuator/health  # Admin Service
curl http://localhost:8083/actuator/health  # Order Service
curl http://localhost:8082/actuator/health  # Catalog Service
curl http://localhost:8081/actuator/health  # Auth Service
curl http://localhost:8080/actuator/health  # API Gateway
```

## 🔌 Feign Client Usage (Admin Service)

### OrderClient - Call Order Service
```java
@Autowired
private OrderClient orderClient;

// Get all orders
Object allOrders = orderClient.getAllOrders(userEmail, userRole);

// Get specific order
Object order = orderClient.getOrder(orderId, userEmail, userRole);

// Delete order
orderClient.deleteOrder(orderId, userEmail, userRole);
```

### CatalogClient - Call Catalog Service
```java
@Autowired
private CatalogClient catalogClient;

// Get all products
Object products = catalogClient.getAllProducts(userEmail, userRole);

// Get specific product
Object product = catalogClient.getProduct(productId, userEmail, userRole);

// Create product
Object newProduct = catalogClient.createProduct(
    productRequest, userEmail, userRole);

// Update product
Object updated = catalogClient.updateProduct(
    productId, productRequest, userEmail, userRole);

// Delete product
catalogClient.deleteProduct(productId, userEmail, userRole);
```

## 🔑 Authentication Flow

### 1. Get Token
```bash
curl -X POST http://localhost:8080/gateway/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

### 2. Use Token
```bash
curl -X GET http://localhost:8080/gateway/catalog/products \
  -H "Authorization: Bearer <token>"
```

## 📊 Docker Compose Commands

```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# View logs
docker-compose logs -f
docker-compose logs -f service-name

# Restart service
docker-compose restart service-name

# Remove all data (volumes)
docker-compose down -v

# Rebuild images
docker-compose up -d --build
```

## 🐛 Troubleshooting

### Services Not Registering in Eureka
```bash
# Check Eureka dashboard
open http://localhost:8761

# Check service logs
docker-compose logs auth-service
docker-compose logs catalog-service
docker-compose logs order-service
```

### Database Connection Failed
```bash
# Check MySQL
docker-compose logs mysql

# Verify MySQL is running
docker ps | grep mysql

# Manual test
mysql -h 127.0.0.1 -u root -p<password>
```

### Port Already in Use
```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>
```

## 📝 Configuration Files

All services configured via `application.properties`:
- Database: `spring.datasource.*`
- Eureka: `eureka.client.*`
- JWT: `app.jwt.*`
- RabbitMQ: `spring.rabbitmq.*`
- Actuator: `management.endpoints.web.exposure.include=health`

## 🔄 Inter-Service Communication

Admin Service → Order Service:
```
Admin Service
    ↓ (Feign)
OrderClient (auto-discovers via Eureka)
    ↓
Eureka Registry
    ↓
Order Service (load-balanced)
```

Admin Service → Catalog Service:
```
Admin Service
    ↓ (Feign)
CatalogClient (auto-discovers via Eureka)
    ↓
Eureka Registry
    ↓
Catalog Service (load-balanced)
```

## 📦 Dependencies Added

```xml
<!-- OpenFeign for inter-service communication -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

## ✅ What's Included

- ✓ 5 Microservices (Auth, Catalog, Order, Admin, API Gateway)
- ✓ Service Discovery (Eureka)
- ✓ API Gateway with routing & JWT validation
- ✓ MySQL database with auto-initialization
- ✓ RabbitMQ for async messaging
- ✓ Feign clients for inter-service communication
- ✓ Docker multi-stage builds for all services
- ✓ Health checks and monitoring
- ✓ Environment-based configuration
- ✓ JWT-based security

## 🔗 Important Links

- API Gateway: http://localhost:8080
- Eureka Dashboard: http://localhost:8761
- RabbitMQ Management: http://localhost:15672 (guest:guest)
- MySQL: jdbc:mysql://localhost:3306/authService

## 📚 Documentation

- `SETUP_GUIDE.md` - Comprehensive setup and architecture guide
- `COMPLETION_SUMMARY.md` - Detailed completion report
- This file - Quick reference

---
**Last Updated**: March 27, 2026
**Status**: ✅ Production Ready
