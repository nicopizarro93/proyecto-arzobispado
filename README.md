# Santiago Sacro

App móvil del caso **Red Patrimonial de Santiago** (Área de Cultura, Arzobispado de Santiago).
DSY1105 Desarrollo de Aplicaciones Móviles · Duoc UC.

*Templos, arte e historia de Santiago.*

## Estructura

| Carpeta | Contenido |
|---|---|
| `backend/` | API REST en Spring Boot (Java 21, JPA, Flyway, MySQL) |
| `android/` | App Android en Kotlin + Jetpack Compose (próximamente) |
| `database/` | Script MySQL del modelo relacional |
| `docs/` | Casos de uso, historias de usuario, MER y mockup |

## Cómo ejecutar el backend

0. Copiar `backend/.env.example` como `backend/.env` y poner una clave en `JWT_SECRET`.
1. Tener JDK 21 y WAMP con MySQL 8.4 encendido en el puerto 3306.
2. En la carpeta `backend/` ejecutar:
```
   .\mvnw.cmd spring-boot:run
```
3. Flyway crea la base `santiago_sacro` con sus tablas al arrancar.

## Flujo de trabajo con Git

- `main`: versiones entregadas (etiquetas `v1.0.0`, `v2.0.0`).
- `develop`: integración de lo terminado.
- `feature/...`, `fix/...`, `docs/...`: una rama por tarea, que entra a `develop` mediante Pull Request.

Todos los datos del proyecto son ficticios.