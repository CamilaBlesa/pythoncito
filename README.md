# 🐍 Pythoncito - Trabajo Práctico Integrador (TPI)

**Materia:** Programación III  
**Institución:** Universidad Nacional de San Martín (UNSAM) - Escuela de Ciencia y Tecnología (ECyT)  
**Equipo:** Grupo 1 — Trejo Samuel · Pintos Sam · Blesa Camila  
**Docente:** Ing. Maximiliano Di Ludovico  

---

## 🎯 Descripción del Proyecto

**Pythoncito** es una aplicación web educativa diseñada para niños de 8 a 14 años que facilita el aprendizaje de lógica de programación y Python mediante bloques visuales e interactivos, reduciendo la fricción y la frustración sintáctica bajo lineamientos de **Diseño Universal para el Aprendizaje (DUA)** y **Educación Asistida**.

El proyecto implementa una plataforma completa con:
- **Mapa de niveles progresivos** con desbloqueo por completación
- **Ejercicios de arrastrar y soltar bloques de código** (drag & drop + teclado)
- **Sistema de pistas progresivas** desbloqueadas por intentos fallidos
- **Validación automática** con feedback detallado (faltantes, sobrantes, fuera de orden)
- **Panel administrativo** para gestión de niveles, ejercicios, bloques y pistas
- **Accesibilidad completa**: alto contraste, fuentes legibles (Atkinson Hyperlegible, OpenDyslexic), tamaños de texto ajustables, navegación por teclado
- **Autenticación JWT** para estudiantes (anónima) y docentes (con clave)

---

## 🛠️ Tecnologías y Requisitos

| Tecnología | Versión |
|------------|---------|
| **Java** | 21 |
| **Spring Boot** | 4.1.2 |
| **Build Tool** | Maven 3.9.9 (wrapper `mvnw` incluido) |
| **Persistencia** | Spring Data JPA / Hibernate |
| **Base de Datos** | Microsoft SQL Server (producción) / H2 (tests) |
| **Seguridad** | Spring Security + OAuth2 Resource Server (JWT) |
| **Documentación API** | Swagger / OpenAPI 3 (`springdoc-openapi`) |
| **Testing** | JUnit 5, Mockito, **Cucumber (Gherkin)**, **PITest (mutation testing)** |
| **Frontend** | HTML5, CSS3 (variables, Grid/Flex), Vanilla JS (ES6+) |

---

## 🏗️ Arquitectura de Paquetes

```
src/main/java/ar/edu/unsam/programacion3/pythoncito/
├── config/                 # SecurityConfig, OpenApiConfig, DataInitializer, JwtConfig
├── controllers/            # AdminController, AuthController, EstudianteController
├── dtos/                   # DTOs para Requests/Responses (Admin, Estudiante)
├── exceptions/             # Excepciones personalizadas + GlobalExceptionHandler
├── mappers/                # EjercicioMapper, NivelMapper (Entity ↔ DTO)
├── models/                 # Entidades JPA (@Entity)
├── repositories/           # Spring Data JPA Repositories
├── services/               # Lógica de negocio (@Service)
└── PythoncitoApplication.java
```

**Endpoints principales:**
- `POST /api/auth/estudiante` — Crear sesión estudiante (JWT)
- `POST /api/auth/admin` — Login docente (JWT)
- `POST /api/auth/estudiante/recuperar` — Recuperar sesión estudiante
- `GET /api/estudiante/niveles` — Mapa de niveles (bloqueados/desbloqueados/completados)
- `GET /api/estudiante/niveles/{id}/ejercicios` — Ejercicios de un nivel
- `GET /api/estudiante/ejercicios/{id}` — Detalle de ejercicio (bloques, pistas)
- `POST /api/estudiante/ejercicios/{id}/validar` — Validar solución
- `GET /api/estudiante/ejercicios/{id}/pistas/{orden}` — Obtener pista progresiva
- `GET /api/admin/niveles` — CRUD niveles (ADMIN)
- `GET/POST/PUT/DELETE /api/admin/ejercicios` — CRUD ejercicios (ADMIN)
- `GET/PUT /api/admin/bloques/{id}` — Gestión bloques (ADMIN)

---

## ✨ Funcionalidades Implementadas

### Estudiante
- **Autenticación anónima** con JWT (24h) y recuperación de sesión
- **Mapa de niveles** con progreso visual (completados/desbloqueados/bloqueados)
- **Ejercicios multi-nivel** con navegación entre ejercicios
- **Editor de bloques** accesible:
  - Drag & drop (ratón)
  - Click para mover
  - Teclado: Enter/Space, Flechas ↑↓, Delete/Backspace, Tab
  - Botones ▲ ▼ ✖ por bloque
- **Pistas progresivas**: 1ra disponible, siguientes requieren intentos fallidos
- **Validación DUA** con feedback granular:
  - Correcta
  - Bloques faltantes
  - Bloques sobrantes
  - Bloques fuera de orden
- **Progreso persistente**: completación de ejercicios → desbloqueo de niveles

### Docente (Admin)
- **CRUD Niveles**: crear, editar, dar de baja (soft delete), reordenar
- **CRUD Ejercicios**: crear, editar, eliminar, reordenar por nivel
- **Gestión de Bloques**: contenido, orden presentación, orden correcto
- **Gestión de Pistas**: texto, orden progresivo
- **Tabla responsive** con scroll horizontal en móvil

### Accesibilidad (WCAG 2.1 AA)
- **Alto contraste** (modo oscuro/amarillo)
- **Fuentes legibles**: Atkinson Hyperlegible, OpenDyslexic, Default
- **Tamaños de texto**: Predeterminado / Mediano / Grande (persistidos en localStorage)
- **Navegación 100% por teclado** (focus visible, skip link, ARIA labels)
- **ARIA live regions** para feedback dinámico
- **Modo reducido de movimiento** respetado

---

## Testing y Calidad

| Herramienta | Cobertura |
|-------------|-----------|
| **JUnit 5 + Mockito** | 176 tests unitarios (services, mappers) |
| **Cucumber (Gherkin)** | 34 tests de integración E2E (4 features) |
| **PITest (mutation testing)** | **92% mutation coverage** (285/310 mutaciones muertas) |
| **JaCoCo** | >98% line coverage en services |

**Ejecutar tests:**
```bash
# Todos los tests (JUnit + Cucumber)
./mvnw test

# Solo tests unitarios
./mvnw test -Dtest=*Test

# Solo Cucumber
./mvnw test -Dtest=RunCucumberTest

# Mutation testing (PITest)
./mvnw org.pitest:pitest-maven:mutationCoverage
# Reporte en: target/pit-reports/index.html
```

---

## 🚀 Cómo Ejecutar el Proyecto

### 1. Requisitos previos
- **Java 21** instalado (`java -version`)
- **SQL Server** corriendo en puerto `1433` con base `pythoncito_db`
  - Usuario/contraseña en `src/main/resources/application.properties`
  - O usar perfil `test` con H2 en memoria (tests)

### 2. Compilar y correr
```bash
# Compilar
./mvnw clean compile

# Ejecutar (producción - SQL Server)
./mvnw spring-boot:run

# Ejecutar tests con H2
./mvnw test
```

### 3. Acceder a la aplicación
| URL | Descripción |
|-----|-------------|
| `http://localhost:8080/` | Página principal (mapa de niveles) |
| `http://localhost:8080/nivel.html?id=1` | Ejercicio de un nivel |
| `http://localhost:8080/admin.html` | Panel docente |
| `http://localhost:8080/swagger-ui.html` | Documentación API (Swagger) |
| `http://localhost:8080/h2-console` | Consola H2 (solo perfil test) |

### 4. Credenciales Admin
- Clave por defecto: `secret_admin_key` (configurable en `application.properties` → `pythoncito.security.admin-token`)

---

## 📁 Estructura Frontend (Recursos Estáticos)

```
src/main/resources/static/
├── index.html          # Mapa de niveles (estudiante)
├── nivel.html          # Ejercicio individual (estudiante)
├── admin.html          # Panel administrativo (docente)
├── css/
│   └── styles.css      # Estilos completos (variables, responsive, a11y)
└── js/
    ├── api.js          # Autenticación, fetch, accesibilidad
    ├── app.js          # Carga mapa de niveles
    ├── puzzle.js       # Editor de bloques (drag&drop, teclado, validación)
    └── admin.js        # CRUD admin (niveles)
```

---

## 📊 Estado Actual del Proyecto

| Módulo | Estado |
|--------|--------|
| Backend (Spring Boot) | ✅ Completo |
| API REST + Swagger | ✅ Completo |
| Base de datos + JPA | ✅ Completo |
| Seguridad JWT | ✅ Completo |
| Frontend Estudiante | ✅ Completo |
| Frontend Admin | ✅ Completo (CRUD niveles) |
| Accesibilidad (WCAG AA) | ✅ Completa |
| Tests unitarios | ✅ 176 tests |
| Tests Cucumber (Gherkin) | ✅ 34 scenarios |
| Mutation Testing (PITest) | ✅ 92% |
| Javadoc | ✅ Generado |

---

## 📚 Documentación Generada

- **Javadoc**: `target/reports/apidocs/index.html`
  ```bash
  ./mvnw javadoc:javadoc
  ```
- **PITest Report**: `target/pit-reports/index.html`
  ```bash
  ./mvnw org.pitest:pitest-maven:mutationCoverage
  ```
- **Swagger UI**: `http://localhost:8080/swagger-ui.html` (app corriendo)

---

## 📄 Licencia

Proyecto académico - Trabajo Práctico Integrador (TPI) - UNSAM 2026