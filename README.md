Spring Boot Microservices Project


1. Project Overview

This project is a Spring Boot microservices backend application designed to demonstrate service discovery, API gateway routing, service-to-service communication, and independent database management. The system is composed of Product, Order, Inventory, Discovery Server, and API Gateway services.

2. Architecture

                         ┌──────────────────────┐
                         │      API Gateway     │
                         │       Port 8080      │
                         └──────────┬───────────┘
                                    │
                    ┌───────────────┼────────────────┐
                    │               │                │
                    ▼               ▼                ▼
             ┌────────────┐  ┌────────────┐  ┌───────────────┐
             │  Product   │  │   Order    │  │   Inventory   │
             │  Service   │  │  Service   │  │    Service    │
             │  MongoDB   │  │  MariaDB   │  │    MariaDB    │
             └────────────┘  └─────┬──────┘  └───────────────┘
                                   │
                                   │ WebClient
                                   ▼
                            Inventory Service

                         ┌──────────────────────┐
                         │    Eureka Server     │
                         │       Port 8761      │
                         └──────────────────────┘

3. Services

Service

Purpose

Database

Port

Service Name

Discovery Server

Service discovery using Eureka

None

8761

N/A

API Gateway

Single entry point for client requests

None

8085*

api-gateways

Product Service

Product management

MongoDB

Dynamic

product-service

Order Service

Order management

MariaDB

8081

order-service

Inventory Service

Inventory and stock management

MariaDB

Dynamic

inventory-service

*The intended API Gateway port is 8085. Verify the active application.properties before running the project.

4. Technologies

Java 21

Spring Boot 4.1.1

Spring Cloud

Spring Cloud Netflix Eureka

Spring Cloud Gateway

Spring Data JPA

Spring Data MongoDB

Spring Web / WebFlux

WebClient

MariaDB

MongoDB

Maven

Lombok

JUnit

Testcontainers

5. Project Structure

microsoft_real/
├── discovery-server/
├── api-gateways/
├── product_service/
├── oder_service/
├── inventory_service/
├── pom.xml
└── README.md

The module directory is currently named 'oder_service', while the Spring application/service name is 'order-service'.

6. Service Discovery

The project uses Netflix Eureka Server for service discovery. Services register themselves with the Eureka Server and can be located by service name rather than hardcoded hostnames and ports.

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
eureka.instance.ip-address=127.0.0.1

Eureka Dashboard:

http://localhost:8761

7. API Gateway

The API Gateway provides a single entry point for client requests and routes requests to the appropriate microservice using Eureka and load balancing.

/api/products/**   → product-service
/api/order/**      → order-service
/api/inventory/**  → inventory-service

Gateway routes use the lb:// scheme, for example:

spring.cloud.gateway.server.webflux.routes[0].id=product-service
spring.cloud.gateway.server.webflux.routes[0].uri=lb://product-service
spring.cloud.gateway.server.webflux.routes[0].predicates[0]=Path=/api/products/**

8. Product Service

The Product Service manages product information using MongoDB.

MongoDB URI: mongodb://localhost:27017/product-service

Example product:

{
  "name": "iPhone 13",
  "description": "Apple iPhone 13",
  "price": 1200
}

9. Order Service

The Order Service manages orders using Spring Data JPA and MariaDB. Before saving an order, it communicates with the Inventory Service to verify stock availability.

Example request:

POST /api/order

{
  "orderLineItemsDTOList": [
    {
      "skuCode": "Iphone_13",
      "price": 1200,
      "quantity": 1
    }
  ]
}

10. Inventory Service

The Inventory Service manages stock information using Spring Data JPA and MariaDB.

@Entity
@Table(name = "t_inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String skuCode;
    private Integer quantity;
}

Current development database:

jdbc:mariadb://localhost:3306/inverntory

The database name 'inverntory' is retained from the current project configuration.

11. Service-to-Service Communication

The Order Service uses a load-balanced WebClient to communicate with the Inventory Service through the Eureka service name.

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}

The Inventory Service is addressed using:

http://inventory-service/api/inventory

12. Database Configuration

Service

Database

Technology

Connection

Product Service

product-service

MongoDB

localhost:27017

Order Service

order_service

MariaDB

localhost:3306

Inventory Service

inverntory

MariaDB

localhost:3306

13. Running the Project

1. Start MariaDB: Ensure MariaDB is running on localhost:3306.

2. Start MongoDB: Ensure MongoDB is running on localhost:27017.

3. Start Eureka Server: Run the Discovery Server first and open http://localhost:8761.

4. Start Product Service: Start the Product Service and allow it to register with Eureka.

5. Start Inventory Service: Start the Inventory Service and allow it to register with Eureka.

6. Start Order Service: Start the Order Service and allow it to register with Eureka.

7. Start API Gateway: Start the API Gateway after the required services are available.

14. Maven Commands

# Clean and compile
.\mvnw.cmd clean compile

# Run tests
.\mvnw.cmd test

# Package
.\mvnw.cmd clean package

# Package without tests
.\mvnw.cmd clean package -DskipTests

15. Testing Through the Gateway

Example order request:

POST http://localhost:8085/api/order

{
  "orderLineItemsDTOList": [
    {
      "skuCode": "Iphone_13",
      "price": 1200,
      "quantity": 1
    }
  ]
}

The request is routed from the API Gateway to the Order Service. The Order Service then checks the requested SKU against the Inventory Service before saving the order.

16. Common Configuration Notes

For local development, services use Eureka IP registration to avoid Windows hostname resolution problems.

eureka.instance.prefer-ip-address=true
eureka.instance.ip-address=127.0.0.1

For database schema management during development, use 'update' instead of 'create-drop' if data should remain after restarting the application.

spring.jpa.hibernate.ddl-auto=update

17. Security

Database passwords and other secrets should not be committed to a public Git repository. Use environment variables or local configuration files that are excluded through .gitignore.

spring.datasource.password=${DB_PASSWORD}

18. Learning Objectives

Microservice architecture

Service discovery with Eureka

API Gateway routing

Client-side load balancing

REST API development

Service-to-service communication with WebClient

Database-per-service architecture

MariaDB and MongoDB integration

Maven multi-module project management

Integration testing and Testcontainers

19. Future Improvements

Spring Security and JWT authentication

Centralized configuration

Resilience4j circuit breakers

Kafka or RabbitMQ messaging

Distributed tracing

Prometheus and Grafana monitoring

Docker Compose

Kubernetes deployment

CI/CD pipeline

Expanded integration test coverage

Swagger/OpenAPI documentation

20. Author

Antony Oduor

Software Developer | Backend / Full-Stack Developer
