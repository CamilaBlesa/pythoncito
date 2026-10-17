# 🐍 Pythoncito - Trabajo Práctico Integrador (TPI)

**Materia:** Programación III  
**Institución:** Universidad Nacional de San Martín (UNSAM) - Escuela de Ciencia y Tecnología (ECyT)  
**Equipo:** CSS  
**Docente:** Ing. Maximiliano Di Ludovico  

---

## 🎯 Descripción del Proyecto
**Pythoncito** es una aplicación web educativa pensada para chicos de 8 a 14 años que facilita el aprendizaje de lógica de programación y Python mediante bloques visuales e interactivos, reduciendo la fricción y la frustración sintáctica bajo lineamientos de **Diseño Universal para el Aprendizaje (DUA)** y Educación Asistida.

---

## 🛠️ Tecnologías y Requisitos
* **Java:** 21
* **Framework:** Spring Boot 4.1.1
* **Build Tool:** Maven (incluye `mvnw`)
* **Persistencia:** Spring Data JPA / Hibernate
* **Base de Datos:** Microsoft SQL Server
* **Documentación de API:** Swagger / OpenAPI 3 (`springdoc-openapi`)

---

## 🏗️ Arquitectura de Paquetes
Organizada en capas de responsabilidades separadas:

```
src/main/java/com/pythoncito/pythoncito/
├── configs/           # Configuración de Swagger/OpenAPI, CORS, Beans
├── controllers/       # RestControllers (endpoints HTTP con Swagger @Operation, @Tag)
├── dtos/              # Data Transfer Objects (Requests y Responses)
├── exceptions/        # Clases de excepción y GlobalExceptionHandler (@RestControllerAdvice)
├── models/            # Entidades JPA (@Entity)
├── repositories/      # Interfaces Spring Data JPA
├── repositories/impl/ # Implementaciones a medida de repositorios
├── services/          # Interfaces de lógica de negocio
├── services/impl/     # Implementaciones de servicios (@Service)
├── validators/        # Validaciones de reglas de negocio funcionales
└── utils/             # Funciones auxiliares y utilidades
```

---

## 🚀 Cómo Ejecutar el Proyecto

### 1. Requisitos previos
* Java 21 instalado (`java -version`).
* SQL Server corriendo localmente en el puerto `1433` con base de datos `pythoncito_db` (o ajustar credenciales en `src/main/resources/application.properties`).

### 2. Compilar y correr
En la terminal (PowerShell o CMD):

```bash
# Compilar
./mvnw clean compile

# Ejecutar
./mvnw spring-boot:run
```

### 3. Documentación Swagger UI
Una vez levantada la aplicación, ingresar en el navegador a:  
👉 **http://localhost:8080/swagger-ui.html**
