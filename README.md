# AuraSacrapp

App móvil del caso **Red Patrimonial de Santiago** (Área de Cultura, Arzobispado de Santiago).
DSY1105 Desarrollo de Aplicaciones Móviles · Duoc UC.

*Templos, arte e historia de Santiago.*

## Estructura

| Carpeta | Contenido |
|---|---|
| `android/` | App Android en Kotlin + Jetpack Compose (MVVM, Retrofit, Room como caché, Navigation, ML Kit) |
| `backend/` | API REST en Spring Boot (Java 21, JPA, Flyway, MySQL, Spring Security con JWT) |
| `database/` | Script MySQL del modelo relacional |
| `docs/` | Casos de uso, historias de usuario, MER, mockup, códigos QR de prueba (`docs/qr`) e informe de auditoría |

## Cómo se conectan las piezas

```
App Android  ──HTTP + JWT──►  Backend Spring Boot  ──JPA──►  MySQL (aura_sacrapp)
     │
     └── Room (en el teléfono): caché de recintos y eventos, sesión, favoritos y escaneos de QR
```

- La app **siempre pide los datos al backend**. Lo que baja se guarda en Room, que es lo que observan las
  pantallas; sin conexión se siguen viendo los últimos datos guardados.
- La sesión es un **token JWT** (dura 8 horas, se guarda en el teléfono; la contraseña nunca). Si vence, la app
  cierra la sesión y vuelve a Bienvenida.
- Solo viven en el teléfono, sin pasar por el backend: los **favoritos** y los **escaneos de QR**.

## App Android (versión 3.0.0)

Funciones por tipo de cuenta:

| Quién | Qué puede hacer |
|---|---|
| **Visitante y docente** | Explorar recintos (buscar sin tildes y filtrar por tipo, comuna y estado), favoritos, ficha con horario e historia, mapa, agenda cultural, rutas sugeridas y propias, **planificar, editar y cancelar visitas** (a un recinto o a una ruta, con observaciones), **pedir un guía** y ver su estado, escanear QR y editar su perfil. El docente además indica el curso de la salida pedagógica |
| **Encargado** | Cambiar el estado de apertura de su recinto, **publicar, editar y cancelar eventos y avisos** (incluidas aperturas y cierres especiales), editar su **reseña histórica y horarios**, administrar sus **guías y su disponibilidad** y **asignar o rechazar solicitudes de guía** |
| **Administrador** | **Registrar recintos** en la red y darlos de baja, y **crear, activar y desactivar las cuentas de los encargados** |

| Pantalla | Qué hace |
|---|---|
| Bienvenida, Login, Crear cuenta | Ingreso con JWT; el registro valida cada campo igual que el backend |
| Inicio | Lista de recintos con buscador, filtros combinables (tipo, comuna, estado y favoritos) y accesos al mapa y a las rutas |
| Mapa | Mapa esquemático con cada recinto ubicado por sus coordenadas y coloreado según su estado |
| Ficha | Tipo, dirección, estado, horario de la semana, historia, próximos eventos y botón de favorito |
| Planificar visita | Fecha, hora, personas, observaciones, curso (docentes) y casilla "Necesito un guía" |
| Rutas, Detalle de ruta, Crear ruta | Rutas sugeridas y propias; el tiempo estimado lo calcula el backend (fórmula de Haversine) |
| Agenda y Detalle de evento | Eventos próximos de los recintos |
| Escanear | Lector de QR con ML Kit de Google: registra la visita y abre la ficha |
| Mis visitas | Visitas planificadas (editar, cancelar y estado de sus guías) y recintos escaneados |
| Perfil | Datos de la cuenta, editar datos, cambiar contraseña y cerrar sesión |
| Mi recinto, Eventos y avisos, Editar ficha, Guías y solicitudes | Panel del encargado |
| Administración de la red | Panel del administrador |

### Cómo ejecutarla

1. **Encender el backend** (ver más abajo): la app no funciona sin él.
2. Abrir la carpeta `android/` en Android Studio (AGP 9.4, JDK incluido en Android Studio) y esperar la sincronización de Gradle.
3. Usar un emulador con **Android 17 (API 37) y Google Play**, con internet: el lector QR lo provee Google Play Services.
4. Ejecutar con **Run 'app'**. La app pide el permiso de **red local** (Android 17 lo exige para llegar al backend del PC): pulsar **Permitir**.

El emulador accede al PC por la dirección `10.0.2.2` (puerto 8080), definida en `remote/RetrofitClient.kt`.
Evitar el modo avión del emulador: deja dos conexiones de red activas y se pierde la conexión con el backend.

### Cuentas de prueba

Todas usan la contraseña `Prueba123`.

| Correo | Tipo de cuenta |
|---|---|
| `visitante@aurasacrapp.cl` | Visitante |
| `docente@aurasacrapp.cl` | Docente |
| `encargado.capilla@aurasacrapp.cl` | Encargado de la Capilla del Buen Pastor |
| `encargado.museo@aurasacrapp.cl` | Encargado del Museo de Arte Sacro Los Aromos |
| `admin@aurasacrapp.cl` | Administrador |

### Cómo probar el lector QR

Los códigos están en `docs/qr` (también en un PDF para imprimir). Cada uno contiene el texto `aurasacrapp:recinto:{id}`.

1. En el emulador: **Extended controls (⋯) → Camera → Virtual scene images → Add image** y elegir, por ejemplo, `qr_01_capilla-del-buen-pastor.png`.
2. En la app: pestaña **Escanear → Escanear código QR**.
3. En la cámara virtual, mantener **Alt** y moverse con **W A S D** y el mouse hasta enfocar el código.

`qr_08` corresponde a un recinto dado de baja y `qr_99` a un código de otra página: ambos muestran un mensaje de error.

### Arquitectura

```
android/app/src/main/java/cl/duoc/aurasacrapp/
├── model/        Entidades de Room (Comuna, Recinto, Usuario, Visita, Evento, Favorito) y datos que vienen del backend
├── dao/          Consultas de Room (@Query, @Upsert, @Insert)
├── database/     AppDatabase (Room)
├── remote/       Retrofit: las interfaces *Api, RetrofitClient (con el token JWT) y los DTO
├── repository/   Acceso a los datos (backend + Room) y sesión del usuario
├── viewmodel/    Estado (StateFlow) y lógica de cada pantalla
├── view/         Pantallas en Jetpack Compose
├── navigation/   Rutas y NavHost con la barra inferior
├── ui/theme/     Colores y tipografía de AuraSacrapp
└── util/         Lectura de códigos QR, fechas y textos
```

Flujo de los datos: **View → ViewModel → Repository → (Retrofit → backend) y (DAO → Room)**. Las pantallas observan
el estado del ViewModel y se actualizan solas cuando cambian los datos.

### Pruebas unitarias de la app

En Android Studio: clic derecho en `app/src/test/java` → **Run 'Tests in…'** (11 pruebas: lector QR, búsqueda sin tildes y cálculo de tiempos de las rutas).

## Backend

### Cómo ejecutarlo

0. Copiar `backend/.env.example` como `backend/.env` y poner una clave en `JWT_SECRET`.
1. Tener JDK 21 y WAMP con MySQL 8.4 encendido en el puerto 3306.
2. En la carpeta `backend/` ejecutar:
```
   .\mvnw.cmd spring-boot:run
```
3. Flyway crea la base `aura_sacrapp` con sus tablas y datos de prueba al arrancar.

Después de cambiar el código del backend hay que reiniciarlo para que la app vea los cambios.
Pruebas del backend: `.\mvnw.cmd test` (117 pruebas, incluida la que arranca la aplicación completa contra MySQL).

### Qué expone la API

| Área | Rutas | Quién |
|---|---|---|
| Acceso | `/api/auth/registro`, `/api/auth/login` | Público |
| Consulta | `/api/recintos`, `/api/eventos`, `/api/comunas`, `/api/rutas` | Público |
| Perfil | `/api/usuarios/yo` (datos y contraseña) | Sesión iniciada |
| Rutas propias | `/api/rutas/mias` | Sesión iniciada |
| Visitas y guías | `/api/visitas` (agendar, editar, cancelar) y `/api/visitas/solicitudes-guia` | Visitante y docente |
| Panel del encargado | `/api/encargado/recinto` (estado, ficha, horarios, eventos, guías y solicitudes de guía) | Encargado |
| Administración | `/api/admin/recintos` y `/api/admin/encargados` | Administrador |

Los errores se responden en JSON (`ProblemDetail`) con el mensaje en `detail` y, en validaciones, un mensaje por campo en `errores`.

## Fuera de esta entrega

Por decisión del equipo y por tiempo, no se incluyen: la ubicación GPS y los recintos cercanos, la galería de
fotos de la ficha, la recuperación de contraseña, los avisos de eventos cercanos, "iniciar ruta" con seguimiento
y la gestión de rutas sugeridas por el administrador. Ver `docs/auditoria_app_backend_bd.md`.

## Flujo de trabajo con Git

- `main`: versiones entregadas (etiquetas `v1.0.0`, `v2.0.0`).
- `develop`: integración de lo terminado.
- `feature/...`, `fix/...`, `docs/...`: una rama por tarea, que entra a `develop` mediante Pull Request.

Todos los datos del proyecto son ficticios.
