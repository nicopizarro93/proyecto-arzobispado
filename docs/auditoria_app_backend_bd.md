# Auditoría app + backend + base de datos (26 nov 2026)

Qué se revisó: contrato de la API contra los DTO de la app (con `curl` por rol), integridad de la base
MySQL (consultas de solo lectura), recorrido real de las pantallas en el emulador (Android 17) leyendo el
Logcat, y compilación de release. Casos de uso: `docs/casos de uso...docx` e `historias de usuario...docx`.

## 1. Fallos encontrados y corregidos en esta auditoría

| # | Fallo | Causa | Estado |
|---|---|---|---|
| 1 | La app se cerraba al abrir una ruta | `RutaDetalleViewModel` sin el constructor `(SavedStateHandle)` que Android busca | Corregido (`@JvmOverloads`) |
| 2 | **El build de release no compilaba** | `registerForActivityResult` en `MainActivity` rechazado por lint de release (Fragment antiguo de ML Kit) | Corregido (`requestPermissions`) |
| 3 | Sin conexión con el backend en el emulador | Android 17 exige `ACCESS_LOCAL_NETWORK` para llegar a `10.0.2.2` | Corregido (manifiesto + petición al abrir) |
| 4 | Encargado/administrador veían "Tu rol no tiene permiso" en Mis visitas | La app pedía `/api/visitas`, que solo permite visitantes y docentes (403) | Corregido (solo se pide a esos roles) |
| 5 | El encargado veía el botón "Planificar visita" (el servidor lo rechaza con 403) | Botón sin filtro por rol | Corregido |
| 6 | Dirección repetida: "Pasaje El Llano 118, Maipú, Maipú" | El backend ya incluye la comuna; la app la agregaba otra vez | Corregido |
| 7 | Con el token vencido (8 h) la app quedaba inutilizable | El backend rechaza un token vencido hasta en endpoints públicos (401) y la app no lo manejaba | Corregido: cierra sesión, avisa y vuelve a Bienvenida (probado) |
| 8 | Cancelar una pantalla mientras cargaba se registraba como "error de red" | `CancellationException` capturada como error | Corregido |
| 9 | Radios y casillas solo respondían al tocar el círculo, no el texto | Etiquetas sin `clickable` | Corregido (registro, crear ruta, mi recinto, planificar) |
| 10 | "Dice que guarda" y no guardaba (planificar visita) | La app instalada era una versión anterior | Corregido; además se agregaron observaciones y visita a una ruta |

## 2. Verificado y funcionando (de punta a punta contra la base)

Login con clave mala (mensaje correcto), login, registro (validaciones por campo, campo Colegio solo para
docente), recintos / eventos / rutas desde la API con caché en Room, búsqueda sin tildes, ficha con horario
e historia, mapa, agenda y detalle de evento, rutas sugeridas y detalle con tiempos del servidor, planificar
visita y cancelarla, cambio de estado del encargado (se guarda en MySQL), escaneo de QR guardado en Room,
cierre de sesión, sesión que se mantiene al reabrir la app. Sin cierres inesperados en ninguna pantalla.

## 3. Base de datos

Sin inconsistencias de integridad: sin eventos con fin anterior al inicio, sin horarios traslapados, sin
paradas en recintos inactivos, encargados todos con recinto activo, visitas de docente siempre con curso.
Observaciones:
- `guia`, `disponibilidad_guia` y `solicitud_guia` están **vacías**, pero la app muestra guías y solicitudes
  inventadas (`GuiaRepository`, datos fijos). Es la inconsistencia de datos más visible.
- Los recintos 4, 5, 6 y 7 no tienen **horarios** (la ficha dice "aún no informa su horario"); 5, 6 y 7 no
  tienen fotos.
- No hay proceso que pase a `REALIZADA` las visitas agendadas cuyo día ya pasó.
- Hay datos de prueba creados desde la app (usuario `pollo.asado@...`, visita cancelada con observación
  "chupalo"): conviene limpiarlos antes de entregar.

## 4. Casos de uso comprometidos que faltan (por prioridad de trabajo)

Backend ya existe, falta la app:
- HU-27 Actualizar horarios del recinto (CRUD de horarios).
- HU-28 Publicar evento / aviso (la pantalla es solo visual), HU-29 editar o cancelar evento.
- HU-30 Aperturas y cierres excepcionales (tipos de evento), y mostrarlos en la ficha (el backend los entrega
  en `excepciones` y la app los ignora).
- HU-05 Perfil: editar datos y cambiar contraseña (`PUT /api/usuarios/yo` y `/yo/password`).
- HU-25 Editar una visita agendada (`PUT /api/visitas/{id}`).
- HU-35 Registrar un recinto (administrador): la app no tiene ninguna pantalla de administrador.
- HU-09 Filtros por comuna y estado (el backend ya filtra).
- HU-10 Galería de imágenes de la ficha (el backend entrega las URL).

Falta en el backend y en la app:
- HU-23/24, HU-32/33 Guías, disponibilidad y solicitudes de guía (tablas listas, sin controladores).
- HU-31 Editar la ficha del recinto (descripción): no existe el endpoint.
- HU-34 Cuentas de encargados y HU-36 rutas sugeridas (administrador).
- HU-06 Recuperar contraseña (hay columnas en `usuario`, no hay endpoints).

Falta solo en la app (requiere GPS / almacenamiento local):
- HU-07 Mapa con ubicación, HU-08 Recintos cercanos, HU-20 Iniciar ruta, HU-11 Favoritos (Room),
  HU-12 Fichas sin conexión completas (el horario de la ficha no se guarda en Room).

## 5. Riesgos para la entrega

- **Release firmado:** compila (10,5 MB sin firmar) pero falta firmarlo y probarlo en el emulador. El tráfico
  HTTP está permitido solo hacia `10.0.2.2` (emulador).
- Los tokens duran 8 horas; ya se maneja el vencimiento.
- La carpeta `android/app/build` de Android Studio tiene archivos bloqueados: compilar desde Android Studio.
- Las pruebas unitarias del backend no se ejecutaron en esta auditoría (el backend estaba corriendo).
- En la presentación está prohibido usar apuntes e IA: conviene poder explicar cada capa (`docs/guia_estudio_prueba2.md`).


---

# Actualización final (26 nov 2026, después de las ramas perfil, guías, favoritos y admin)

Todo lo de la sección 4 que no quedó fuera de alcance se construyó y se probó en el emulador contra el backend.

## Casos de uso: estado final

| Historia | Estado |
|---|---|
| HU-01 a HU-04 Cuenta, login, roles y cierre de sesión | Hecho (token vencido cierra la sesión sola) |
| HU-05 Perfil: editar datos y cambiar contraseña | Hecho |
| HU-09 Buscar y filtrar (tipo, comuna, estado, favoritos) y limpiar filtros | Hecho |
| HU-10 Ficha: datos, horario, historia y eventos | Hecho (sin galería, ver abajo) |
| HU-11 Favoritos en Room, sin cuenta y sin conexión | Hecho |
| HU-13 Estado y horario | Hecho (aperturas y cierres especiales se publican, pero la ficha no los muestra aparte) |
| HU-14, HU-15 Agenda y detalle de evento | Hecho |
| HU-17 a HU-19 Rutas sugeridas, propias y tiempo estimado | Hecho |
| HU-21, HU-22 Agendar visita y salida pedagógica (recinto o ruta, con observaciones) | Hecho |
| HU-23, HU-24 Pedir un guía y ver su estado | Hecho |
| HU-25 Editar y cancelar mis visitas | Hecho |
| HU-26 a HU-31 Panel del encargado (estado, horarios, eventos, aperturas y cierres, reseña) | Hecho |
| HU-32, HU-33 Guías, disponibilidad y asignación de solicitudes | Hecho |
| HU-34 Cuentas de encargados (administrador) | Hecho (backend nuevo) |
| HU-35 Registrar y dar de baja recintos (administrador) | Hecho |

## Fuera de esta entrega (decisión del equipo)

GPS (HU-07 con ubicación, HU-08 recintos cercanos, HU-20 iniciar ruta), galería de fotos de la ficha (parte de
HU-10), recuperar contraseña (HU-06), avisos de eventos cercanos (HU-16), rutas sugeridas del administrador
(HU-36) y fichas sin conexión completas (HU-12: los recintos y eventos sí quedan en Room; el horario y la
historia de una ficha no se guardan).

## Verificación final

- **Backend:** 117 pruebas unitarias y de arranque, todas pasan.
- **App:** 11 pruebas unitarias pasan; el APK de release compila (10,9 MB sin firmar).
- **API contra MySQL real:** 28 comprobaciones de guías y 22 de administración, todas correctas.
- **Emulador:** recorrido completo con cada rol (visitante, docente, encargado y administrador) sin cierres
  inesperados; los datos de prueba se borraron después de cada recorrido.

## Cosas que conviene saber

- La lista de recintos se actualiza al abrir la app (Inicio la baja del backend) o al registrar o dar de baja
  desde el panel del administrador. Si alguien cambia datos desde fuera de la app, se ven al reabrirla.
- El token JWT dura 8 horas y el tráfico HTTP solo está permitido hacia `10.0.2.2` (emulador).
- Android 17 exige el permiso de red local para que la app llegue al backend del PC.
