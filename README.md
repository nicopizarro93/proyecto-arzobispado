# Santiago Sacro

App móvil del caso **Red Patrimonial de Santiago** (Área de Cultura, Arzobispado de Santiago).
DSY1105 Desarrollo de Aplicaciones Móviles · Duoc UC.

*Templos, arte e historia de Santiago.*

## Estructura

| Carpeta | Contenido |
|---|---|
| `android/` | App Android en Kotlin + Jetpack Compose (MVVM, Room, Navigation, ML Kit) |
| `backend/` | API REST en Spring Boot (Java 21, JPA, Flyway, MySQL) |
| `database/` | Script MySQL del modelo relacional |
| `docs/` | Casos de uso, historias de usuario, MER, mockup y códigos QR de prueba (`docs/qr`) |

## App Android (versión 1.0.0)

En esta entrega la app funciona con una **base de datos local Room** y **no se conecta al backend**.

| Pantalla | Qué hace |
|---|---|
| Bienvenida | Presentación de la app y botón para iniciar sesión |
| Login | Valida el formulario y el correo y la contraseña contra Room (la contraseña se guarda como hash) |
| Crear cuenta | Formulario de registro de visitantes y docentes |
| Inicio | Lista de recintos con su estado, buscador (sin importar tildes), filtros por tipo y accesos al mapa y a las rutas |
| Mapa | Mapa esquemático con cada recinto ubicado por sus coordenadas y coloreado según su estado |
| Ficha | Tipo, comuna, estado, dirección, horario de la semana, historia y próximos eventos del recinto |
| Planificar visita | Disponibilidad (horario y guías), fecha, hora y personas; el docente además indica su curso y puede solicitar guía |
| Rutas sugeridas | Recorridos patrimoniales con su modo de traslado, paradas y tiempo estimado |
| Detalle de ruta | Paradas en orden, tiempo de visita y de traslado (fórmula de Haversine, igual que el backend) |
| Crear ruta | Elegir recintos en orden y el modo de traslado, con el tiempo estimado calculado al instante |
| Agenda cultural | Conciertos, exposiciones y celebraciones próximas de los recintos |
| Detalle de evento | Fecha, lugar y descripción del evento, con acceso a la ficha del recinto |
| Escanear | Lector de códigos QR con ML Kit de Google: registra la visita y abre la ficha |
| Mis visitas | Historial de los recintos visitados por el usuario, con fecha y hora |
| Perfil | Datos de la cuenta y cerrar sesión |
| Publicar evento (solo encargados) | Formulario para publicar un evento o aviso del recinto |
| Mi recinto (solo encargados) | El encargado abre, cierra o marca "En celebración" su recinto (el cambio se ve al instante) y accede a su panel |
| Editar ficha (solo encargados) | Información del recinto y horario de atención por día |
| Guías y solicitudes (solo encargados) | Disponibilidad de los guías y solicitudes de guía de los colegios |

### Cómo ejecutarla

1. Abrir la carpeta `android/` en Android Studio (AGP 9.4, JDK incluido en Android Studio).
2. Esperar la sincronización de Gradle.
3. Usar un emulador con **Android 16 (API 36) y Google Play**, con internet: el lector QR lo provee Google Play Services.
4. Ejecutar con **Run 'app'**. Al abrirse por primera vez, la app carga los datos iniciales en Room.

Las fechas de los eventos se calculan desde el día en que se instala la app (como en las migraciones del backend), así siempre hay eventos próximos. Para "refrescarlas", desinstalar y volver a instalar la app.

### Cuentas de prueba

Todas usan la contraseña `Prueba123`.

| Correo | Tipo de cuenta |
|---|---|
| `visitante@santiagosacro.cl` | Visitante |
| `docente@santiagosacro.cl` | Docente |
| `encargado.capilla@santiagosacro.cl` | Encargado de la Capilla del Buen Pastor |
| `encargado.museo@santiagosacro.cl` | Encargado del Museo de Arte Sacro Los Aromos |
| `admin@santiagosacro.cl` | Administrador |

### Cómo probar el lector QR

Los códigos están en `docs/qr` (también en un PDF para imprimir). Cada uno contiene el texto `santiagosacro:recinto:{id}`.

1. En el emulador: **Extended controls (⋯) → Camera → Virtual scene images → Add image** y elegir, por ejemplo, `qr_01_capilla-del-buen-pastor.png`.
2. En la app: pestaña **Escanear → Escanear código QR**.
3. En la cámara virtual, mantener **Alt** y moverse con **W A S D** y el mouse hasta enfocar el código.

`qr_08` corresponde a un recinto dado de baja y `qr_99` a un código de otra página: ambos muestran un mensaje de error.

### Arquitectura

```
android/app/src/main/java/cl/duoc/santiagosacro/
├── model/        Entidades de Room (Comuna, Recinto, Usuario, Visita, Evento) y resultados de consultas
├── dao/          Consultas de Room (@Query, @Insert, @Update)
├── database/     AppDatabase y datos iniciales
├── repository/   Acceso a los datos y sesión del usuario
├── viewmodel/    Estado (StateFlow) y lógica de cada pantalla
├── view/         Pantallas en Jetpack Compose
├── navigation/   Rutas y NavHost con la barra inferior
├── ui/theme/     Colores y tipografía de Santiago Sacro
└── util/         Hash de contraseñas, lectura de códigos QR y textos
```

Flujo de los datos: **View → ViewModel → Repository → DAO → Room**. Las pantallas observan el estado del ViewModel y se actualizan solas cuando cambian los datos.

### Pruebas unitarias

En Android Studio: clic derecho en `app/src/test/java` → **Run 'Tests in…'** (12 pruebas: lector QR, búsqueda sin tildes y cálculo de tiempos de las rutas).

## Cómo ejecutar el backend

0. Copiar `backend/.env.example` como `backend/.env` y poner una clave en `JWT_SECRET`.
1. Tener JDK 21 y WAMP con MySQL 8.4 encendido en el puerto 3306.
2. En la carpeta `backend/` ejecutar:
```
   .\mvnw.cmd spring-boot:run
```
3. Flyway crea la base `santiago_sacro` con sus tablas y datos de prueba al arrancar.

Pruebas del backend: `.\mvnw.cmd test`.

## Flujo de trabajo con Git

- `main`: versiones entregadas (etiquetas `v1.0.0`, `v2.0.0`).
- `develop`: integración de lo terminado.
- `feature/...`, `fix/...`, `docs/...`: una rama por tarea, que entra a `develop` mediante Pull Request.

Todos los datos del proyecto son ficticios.
