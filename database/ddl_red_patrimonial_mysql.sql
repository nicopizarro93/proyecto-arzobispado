-- =============================================================================
--  Proyecto    : App móvil AuraSacrapp - caso Red Patrimonial de Santiago
--                (Área de Cultura, Arzobispado de Santiago)
--  Asignatura  : DSY1105 Desarrollo de Aplicaciones Móviles - Duoc UC
--  Integrantes : Alexis Acuña, Nicolás Pizarro
--  Archivo     : ddl_red_patrimonial_mysql.sql
--  Motor       : MySQL 8.4 (sirve desde MySQL 8.0.16, que respeta los CHECK)
--  Descripción : Crea la base de datos central que usa la API REST (Spring Boot
--                con JPA y Spring Security). Es el mismo modelo relacional (3FN)
--                del MER, escrito para MySQL.
--  Versión     : 3.0-MySQL - 13-10-2026
-- =============================================================================
--
--  CONTENIDO
--    0. Base de datos y limpieza
--    1. Catálogo                     (comuna)
--    2. Recintos y horarios          (recinto, recurso_multimedia, horario_regular)
--    3. Usuarios y acceso            (usuario)
--    4. Eventos, aperturas y cierres (evento)
--    5. Rutas y visitas              (ruta, parada_ruta, visita)
--    6. Guías voluntarios            (guia, disponibilidad_guia, solicitud_guia)
--    7. Vistas
--    8. Datos iniciales (comunas)
--
--  CONVENCIONES
--    - Tablas y columnas en singular y snake_case.
--    - Claves primarias BIGINT AUTO_INCREMENT; parada_ruta usa clave primaria
--      compuesta (id_ruta, orden).
--    - Restricciones con nombre: uk_<tabla>_<campo>, fk_<tabla_hija>_<tabla_padre>,
--      ck_<tabla>_<regla>, idx_<tabla>_<campo>.
--    - Motor InnoDB (claves foráneas y transacciones) y utf8mb4 (tildes, ñ y
--      emojis). La intercalación utf8mb4_0900_ai_ci compara sin distinguir
--      mayúsculas ni tildes: "maipu" encuentra "Maipú" (HU-09).
--    - Las listas cortas y fijas (rol, tipo de recinto, tipo de evento y los
--      estados) se validan con CHECK; las comunas son una tabla catálogo.
--    - Borrado lógico con la columna "activo" en las entidades principales.
--
--  EQUIVALENCIA CON EL MODELO ORACLE (Data Modeler)
--    NUMBER(10) de las claves -> BIGINT       NUMBER(n) -> INT
--    NUMBER(1) activo          -> TINYINT(1)   NUMBER(9,6) -> DECIMAL(9,6)
--    VARCHAR2(n CHAR)          -> VARCHAR(n)   CLOB -> TEXT
--    TIMESTAMP                 -> DATETIME     IDENTITY -> AUTO_INCREMENT
--    Las horas de horario_regular pasan de VARCHAR2(5) a TIME, por eso no
--    necesitan los CHECK de formato HH24:MI.
--
--  NORMALIZACIÓN
--    1FN  Atributos atómicos y sin grupos repetitivos: nombre y apellido
--         separados; dirección en calle, número y comuna; horarios, galería,
--         paradas y disponibilidad de guías en tablas propias.
--    2FN  En parada_ruta (id_ruta, orden) cada atributo depende de la clave
--         completa; los datos del recinto viven en recinto.
--    3FN  Sin dependencias transitivas: recinto guarda id_comuna (el nombre vive
--         en comuna); evento no guarda coordenadas (son las de su recinto);
--         visita no copia datos del usuario, del recinto ni de la ruta, y
--         "requiere guía" se deduce de sus filas en solicitud_guia. El tiempo
--         total de una ruta se calcula (vista v_ruta_resumen).
--    visita apunta a un recinto o a una ruta, nunca a ambos (CHECK
--    ck_visita_destino, arco exclusivo).
--
--  EJECUCIÓN
--    - En la API, Flyway crea las tablas al arrancar (migraciones V1 y V2, con
--      el mismo contenido de este script sin la sección 0).
--    - A mano: en phpMyAdmin, pestaña SQL, pegar y ejecutar el script completo.
--      Ojo: la sección 0 borra la base aura_sacrapp si ya existe.
--
--  NOTA: La app guarda en el teléfono (Room) solo la sesión, los favoritos, las
--        rutas creadas sin cuenta y una copia de las fichas para usarlas sin
--        señal. Este script es solo para la base de datos central.
-- =============================================================================


-- =============================================================================
-- 0. BASE DE DATOS Y LIMPIEZA
-- =============================================================================

DROP DATABASE IF EXISTS aura_sacrapp;
CREATE DATABASE aura_sacrapp CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE aura_sacrapp;

-- =============================================================================
-- 1. CATÁLOGO
-- =============================================================================

-- -----------------------------------------------------------------------------
-- COMUNA: Catálogo de comunas donde se ubican los recintos. Es tabla (y no
--   CHECK) porque son muchas y se usan para filtrar (CU-02).
-- -----------------------------------------------------------------------------
CREATE TABLE comuna (
    id_comuna BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la comuna.',
    nombre    VARCHAR(60) NOT NULL COMMENT 'Nombre oficial de la comuna.',
    PRIMARY KEY (id_comuna),
    CONSTRAINT uk_comuna_nombre UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  COMMENT = 'Catálogo de comunas donde se ubican los recintos. Es tabla (y no CHECK) porque son muchas y se usan para filtrar (CU-02).';


-- =============================================================================
-- 2. RECINTOS Y HORARIOS
-- =============================================================================

-- -----------------------------------------------------------------------------
-- RECINTO: Ficha patrimonial de cada recinto de la red (nodo): templo, museo,
--   colegio, archivo o institución.
-- -----------------------------------------------------------------------------
CREATE TABLE recinto (
    id_recinto            BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador único del recinto.',
    tipo_recinto          VARCHAR(12) NOT NULL COMMENT 'TEMPLO, MUSEO, COLEGIO, ARCHIVO o INSTITUCION.',
    id_comuna             BIGINT NOT NULL COMMENT 'Comuna donde está el recinto (FK).',
    nombre_oficial        VARCHAR(150) NOT NULL COMMENT 'Nombre oficial del recinto.',
    calle                 VARCHAR(150) NOT NULL COMMENT 'Calle de la dirección.',
    numero_calle          VARCHAR(10) NULL COMMENT 'Número de la dirección; NULL si es sin número.',
    latitud               DECIMAL(9,6) NOT NULL COMMENT 'Latitud GPS (datos de prueba).',
    longitud              DECIMAL(9,6) NOT NULL COMMENT 'Longitud GPS (datos de prueba).',
    descripcion_historica TEXT NULL COMMENT 'Reseña histórica, arquitectónica y artística.',
    estado_apertura       VARCHAR(15) NOT NULL DEFAULT 'CERRADO' COMMENT 'Estado actual: ABIERTO, CERRADO o EN_CELEBRACION (CU-17).',
    fecha_estado          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Cuándo se cambió el estado por última vez.',
    activo                TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1 = activo, 0 = dado de baja (borrado lógico).',
    fecha_creacion        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de creación del registro.',
    fecha_actualizacion   DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Fecha y hora de la última modificación (MySQL la actualiza sola).',
    PRIMARY KEY (id_recinto),
    CONSTRAINT uk_recinto_nombre_comuna UNIQUE (nombre_oficial, id_comuna),
    KEY idx_recinto_comuna (id_comuna),
    KEY idx_recinto_tipo (tipo_recinto),
    KEY idx_recinto_coordenadas (latitud, longitud),
    CONSTRAINT fk_recinto_comuna FOREIGN KEY (id_comuna)
        REFERENCES comuna (id_comuna),
    CONSTRAINT ck_recinto_tipo
        CHECK (tipo_recinto IN ('TEMPLO', 'MUSEO', 'COLEGIO', 'ARCHIVO',
                                'INSTITUCION')),
    CONSTRAINT ck_recinto_latitud CHECK (latitud BETWEEN -90 AND 90),
    CONSTRAINT ck_recinto_longitud CHECK (longitud BETWEEN -180 AND 180),
    CONSTRAINT ck_recinto_estado_apertura
        CHECK (estado_apertura IN ('ABIERTO', 'CERRADO', 'EN_CELEBRACION')),
    CONSTRAINT ck_recinto_activo CHECK (activo IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  COMMENT = 'Ficha patrimonial de cada recinto de la red (nodo): templo, museo, colegio, archivo o institución.';

-- -----------------------------------------------------------------------------
-- RECURSO_MULTIMEDIA: Galería multimedia simulada de cada recinto.
-- -----------------------------------------------------------------------------
CREATE TABLE recurso_multimedia (
    id_recurso     BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador del recurso.',
    id_recinto     BIGINT NOT NULL COMMENT 'Recinto al que pertenece.',
    tipo_recurso   VARCHAR(10) NOT NULL COMMENT 'IMAGEN, VIDEO o AUDIO.',
    url            VARCHAR(500) NOT NULL COMMENT 'Dirección del archivo (http o https).',
    titulo         VARCHAR(150) NULL COMMENT 'Título o pie de foto.',
    orden          INT NOT NULL DEFAULT 1 COMMENT 'Posición en la galería.',
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de creación del registro.',
    PRIMARY KEY (id_recurso),
    CONSTRAINT uk_recurso_multimedia_url UNIQUE (id_recinto, url),
    CONSTRAINT fk_recurso_multimedia_recinto FOREIGN KEY (id_recinto)
        REFERENCES recinto (id_recinto) ON DELETE CASCADE,
    CONSTRAINT ck_recurso_multimedia_tipo
        CHECK (tipo_recurso IN ('IMAGEN', 'VIDEO', 'AUDIO')),
    CONSTRAINT ck_recurso_multimedia_url CHECK (REGEXP_LIKE(url, '^https?://')),
    CONSTRAINT ck_recurso_multimedia_orden CHECK (orden > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  COMMENT = 'Galería multimedia simulada de cada recinto.';

-- -----------------------------------------------------------------------------
-- HORARIO_REGULAR: Bloques de horario regular por día de la semana. Un día
--   puede tener varios bloques (mañana y tarde).
-- -----------------------------------------------------------------------------
CREATE TABLE horario_regular (
    id_horario    BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador del bloque.',
    id_recinto    BIGINT NOT NULL COMMENT 'Recinto al que pertenece.',
    dia_semana    INT NOT NULL COMMENT '1 = lunes ... 7 = domingo (ISO 8601).',
    hora_apertura TIME NOT NULL COMMENT 'Hora de apertura.',
    hora_cierre   TIME NOT NULL COMMENT 'Hora de cierre.',
    PRIMARY KEY (id_horario),
    CONSTRAINT uk_horario_regular_bloque UNIQUE (id_recinto, dia_semana, hora_apertura),
    CONSTRAINT fk_horario_regular_recinto FOREIGN KEY (id_recinto)
        REFERENCES recinto (id_recinto) ON DELETE CASCADE,
    CONSTRAINT ck_horario_regular_dia CHECK (dia_semana BETWEEN 1 AND 7),
    CONSTRAINT ck_horario_regular_rango CHECK (hora_cierre > hora_apertura)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  COMMENT = 'Bloques de horario regular por día de la semana. Un día puede tener varios bloques (mañana y tarde).';


-- =============================================================================
-- 3. USUARIOS Y ACCESO
-- =============================================================================

-- -----------------------------------------------------------------------------
-- USUARIO: Cuentas de la app. Visitantes y docentes se registran solos
--   (CU-26); encargados y administradores los crea el Administrador (CU-24).
-- -----------------------------------------------------------------------------
CREATE TABLE usuario (
    id_usuario                 BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador del usuario.',
    rol                        VARCHAR(15) NOT NULL COMMENT 'VISITANTE, DOCENTE, ENCARGADO o ADMINISTRADOR.',
    id_recinto                 BIGINT NULL COMMENT 'Recinto a cargo; obligatorio solo para encargados (CU-24).',
    nombre                     VARCHAR(50) NOT NULL COMMENT 'Nombre de pila.',
    apellido                   VARCHAR(50) NOT NULL COMMENT 'Apellido.',
    correo                     VARCHAR(120) NOT NULL COMMENT 'Correo de inicio de sesión, único y en minúsculas.',
    password_hash              VARCHAR(255) NOT NULL COMMENT 'Hash de la contraseña (BCrypt). Nunca se guarda en texto plano.',
    institucion                VARCHAR(120) NULL COMMENT 'Colegio o institución; solo para docentes.',
    activo                     TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1 = activo, 0 = dado de baja (borrado lógico).',
    intentos_fallidos          INT NOT NULL DEFAULT 0 COMMENT 'Intentos de inicio de sesión fallidos seguidos.',
    bloqueado_hasta            DATETIME NULL COMMENT 'Fin del bloqueo temporal tras 5 intentos fallidos.',
    codigo_recuperacion_hash   VARCHAR(255) NULL COMMENT 'Hash del código de un solo uso para recuperar la contraseña (CU-29).',
    codigo_recuperacion_expira DATETIME NULL COMMENT 'Vencimiento del código de recuperación.',
    fecha_acepta_privacidad    DATETIME NULL COMMENT 'Cuándo aceptó la política de privacidad.',
    fecha_creacion             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de creación del registro.',
    fecha_actualizacion        DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Fecha y hora de la última modificación (MySQL la actualiza sola).',
    PRIMARY KEY (id_usuario),
    CONSTRAINT uk_usuario_correo UNIQUE (correo),
    KEY idx_usuario_recinto (id_recinto),
    CONSTRAINT fk_usuario_recinto FOREIGN KEY (id_recinto)
        REFERENCES recinto (id_recinto),
    CONSTRAINT ck_usuario_rol
        CHECK (rol IN ('VISITANTE', 'DOCENTE', 'ENCARGADO', 'ADMINISTRADOR')),
    CONSTRAINT ck_usuario_encargado
        CHECK ((rol = 'ENCARGADO' AND id_recinto IS NOT NULL)
            OR (rol <> 'ENCARGADO' AND id_recinto IS NULL)),
    CONSTRAINT ck_usuario_institucion CHECK (institucion IS NULL OR rol = 'DOCENTE'),
    CONSTRAINT ck_usuario_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) >= 2),
    CONSTRAINT ck_usuario_apellido CHECK (CHAR_LENGTH(TRIM(apellido)) >= 2),
    CONSTRAINT ck_usuario_correo
        CHECK (REGEXP_LIKE(correo, '^[a-z0-9._%+-]+@[a-z0-9.-]+[.][a-z]{2,}$', 'c')),
    CONSTRAINT ck_usuario_activo CHECK (activo IN (0, 1)),
    CONSTRAINT ck_usuario_intentos CHECK (intentos_fallidos >= 0),
    CONSTRAINT ck_usuario_recuperacion
        CHECK ((codigo_recuperacion_hash IS NULL AND codigo_recuperacion_expira IS NULL)
            OR (codigo_recuperacion_hash IS NOT NULL AND codigo_recuperacion_expira IS NOT NULL))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  COMMENT = 'Cuentas de la app. Visitantes y docentes se registran solos (CU-26); encargados y administradores los crea el Administrador (CU-24).';


-- =============================================================================
-- 4. EVENTOS, AVISOS, APERTURAS Y CIERRES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- EVENTO: Eventos, avisos, aperturas y cierres excepcionales que publican los
--   encargados (CU-18 y CU-19). Su ubicación en el mapa es la de su recinto.
-- -----------------------------------------------------------------------------
CREATE TABLE evento (
    id_evento           BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador del evento.',
    id_recinto          BIGINT NOT NULL COMMENT 'Recinto donde se realiza.',
    id_usuario_publica  BIGINT NOT NULL COMMENT 'Encargado que lo publicó.',
    tipo_evento         VARCHAR(20) NOT NULL COMMENT 'CONCIERTO, EXPOSICION, HITO_LITURGICO, ACTIVIDAD_CULTURAL, AVISO, APERTURA_ESPECIAL o CIERRE_ESPECIAL.',
    titulo              VARCHAR(150) NOT NULL COMMENT 'Título del evento o aviso, o motivo de la apertura o del cierre.',
    descripcion         VARCHAR(2000) NULL COMMENT 'Descripción.',
    fecha_hora_inicio   DATETIME NOT NULL COMMENT 'Inicio.',
    fecha_hora_fin      DATETIME NULL COMMENT 'Término; obligatorio en las aperturas y los cierres excepcionales.',
    estado              VARCHAR(10) NOT NULL DEFAULT 'PUBLICADO' COMMENT 'PUBLICADO o CANCELADO.',
    fecha_creacion      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de creación del registro.',
    fecha_actualizacion DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Fecha y hora de la última modificación (MySQL la actualiza sola).',
    PRIMARY KEY (id_evento),
    KEY idx_evento_recinto (id_recinto, fecha_hora_inicio),
    KEY idx_evento_usuario (id_usuario_publica),
    KEY idx_evento_inicio (fecha_hora_inicio),
    CONSTRAINT fk_evento_recinto FOREIGN KEY (id_recinto)
        REFERENCES recinto (id_recinto),
    CONSTRAINT fk_evento_usuario FOREIGN KEY (id_usuario_publica)
        REFERENCES usuario (id_usuario),
    CONSTRAINT ck_evento_tipo
        CHECK (tipo_evento IN ('CONCIERTO', 'EXPOSICION', 'HITO_LITURGICO',
                               'ACTIVIDAD_CULTURAL', 'AVISO', 'APERTURA_ESPECIAL',
                               'CIERRE_ESPECIAL')),
    CONSTRAINT ck_evento_estado CHECK (estado IN ('PUBLICADO', 'CANCELADO')),
    CONSTRAINT ck_evento_fechas
        CHECK (fecha_hora_fin IS NULL OR fecha_hora_fin > fecha_hora_inicio),
    CONSTRAINT ck_evento_especial_fin
        CHECK (tipo_evento NOT IN ('APERTURA_ESPECIAL', 'CIERRE_ESPECIAL')
            OR fecha_hora_fin IS NOT NULL)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  COMMENT = 'Eventos, avisos, aperturas y cierres excepcionales que publican los encargados (CU-18 y CU-19). Su ubicación en el mapa es la de su recinto.';


-- =============================================================================
-- 5. RUTAS Y VISITAS
-- =============================================================================

-- -----------------------------------------------------------------------------
-- RUTA: Rutas patrimoniales: sugeridas (las crea el Administrador, CU-31) o
--   personalizadas (las crea un usuario con cuenta, CU-11).
-- -----------------------------------------------------------------------------
CREATE TABLE ruta (
    id_ruta             BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la ruta.',
    id_usuario_creador  BIGINT NOT NULL COMMENT 'Usuario que creó la ruta.',
    nombre              VARCHAR(100) NOT NULL COMMENT 'Nombre de la ruta.',
    descripcion         VARCHAR(1000) NULL COMMENT 'Descripción de la ruta.',
    tipo_ruta           VARCHAR(15) NOT NULL COMMENT 'SUGERIDA o PERSONALIZADA.',
    modo_traslado       VARCHAR(20) NOT NULL DEFAULT 'A_PIE' COMMENT 'A_PIE, VEHICULO o TRANSPORTE_PUBLICO; se usa para calcular tiempos (CU-12).',
    activo              TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1 = activo, 0 = dado de baja (borrado lógico).',
    fecha_creacion      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de creación del registro.',
    fecha_actualizacion DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Fecha y hora de la última modificación (MySQL la actualiza sola).',
    PRIMARY KEY (id_ruta),
    KEY idx_ruta_usuario (id_usuario_creador),
    CONSTRAINT fk_ruta_usuario FOREIGN KEY (id_usuario_creador)
        REFERENCES usuario (id_usuario),
    CONSTRAINT ck_ruta_tipo CHECK (tipo_ruta IN ('SUGERIDA', 'PERSONALIZADA')),
    CONSTRAINT ck_ruta_modo_traslado
        CHECK (modo_traslado IN ('A_PIE', 'VEHICULO', 'TRANSPORTE_PUBLICO')),
    CONSTRAINT ck_ruta_activo CHECK (activo IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  COMMENT = 'Rutas patrimoniales: sugeridas (las crea el Administrador, CU-31) o personalizadas (las crea un usuario con cuenta, CU-11).';

-- -----------------------------------------------------------------------------
-- PARADA_RUTA: Paradas ordenadas de cada ruta. El tiempo de traslado entre
--   paradas no se guarda: se calcula con las coordenadas (CU-12).
-- -----------------------------------------------------------------------------
CREATE TABLE parada_ruta (
    id_ruta           BIGINT NOT NULL COMMENT 'Ruta a la que pertenece la parada.',
    orden             INT NOT NULL COMMENT 'Posición de la parada en la ruta (1, 2, 3...).',
    id_recinto        BIGINT NOT NULL COMMENT 'Recinto que se visita.',
    tiempo_visita_min INT NOT NULL DEFAULT 30 COMMENT 'Minutos estimados de visita en la parada.',
    PRIMARY KEY (id_ruta, orden),
    CONSTRAINT uk_parada_ruta_recinto UNIQUE (id_ruta, id_recinto),
    KEY idx_parada_ruta_recinto (id_recinto),
    CONSTRAINT fk_parada_ruta_ruta FOREIGN KEY (id_ruta)
        REFERENCES ruta (id_ruta) ON DELETE CASCADE,
    CONSTRAINT fk_parada_ruta_recinto FOREIGN KEY (id_recinto)
        REFERENCES recinto (id_recinto),
    CONSTRAINT ck_parada_ruta_orden CHECK (orden > 0),
    CONSTRAINT ck_parada_ruta_tiempo CHECK (tiempo_visita_min > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  COMMENT = 'Paradas ordenadas de cada ruta. El tiempo de traslado entre paradas no se guarda: se calcula con las coordenadas (CU-12).';

-- -----------------------------------------------------------------------------
-- VISITA: Visitas que agenda un visitante registrado o un docente, a un
--   recinto o a una ruta (CU-13 y CU-30). Solo se guarda la cantidad de
--   personas, sin datos personales.
-- -----------------------------------------------------------------------------
CREATE TABLE visita (
    id_visita           BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la visita.',
    id_usuario          BIGINT NOT NULL COMMENT 'Visitante registrado o docente que agenda la visita.',
    id_recinto          BIGINT NULL COMMENT 'Recinto que se visita, si se agenda desde su ficha. Excluyente con id_ruta.',
    id_ruta             BIGINT NULL COMMENT 'Ruta que se recorre, si se agenda sobre una ruta. Excluyente con id_recinto.',
    fecha_hora_inicio   DATETIME NOT NULL COMMENT 'Fecha y hora de inicio de la visita.',
    cantidad_personas   INT NOT NULL COMMENT 'Cantidad de personas (o de estudiantes, en una salida pedagógica).',
    nivel_curso         VARCHAR(50) NULL COMMENT 'Curso o nivel, solo en salidas pedagógicas de docentes; por ejemplo, 3° medio.',
    observaciones       VARCHAR(500) NULL COMMENT 'Notas de quien agenda.',
    estado              VARCHAR(10) NOT NULL DEFAULT 'AGENDADA' COMMENT 'AGENDADA, REALIZADA o CANCELADA.',
    fecha_creacion      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de creación del registro.',
    fecha_actualizacion DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Fecha y hora de la última modificación (MySQL la actualiza sola).',
    PRIMARY KEY (id_visita),
    KEY idx_visita_usuario (id_usuario),
    KEY idx_visita_recinto (id_recinto),
    KEY idx_visita_ruta (id_ruta),
    CONSTRAINT fk_visita_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario),
    CONSTRAINT fk_visita_recinto FOREIGN KEY (id_recinto)
        REFERENCES recinto (id_recinto),
    CONSTRAINT fk_visita_ruta FOREIGN KEY (id_ruta)
        REFERENCES ruta (id_ruta),
    CONSTRAINT ck_visita_destino
        CHECK ((id_recinto IS NOT NULL AND id_ruta IS NULL)
            OR (id_recinto IS NULL AND id_ruta IS NOT NULL)),
    CONSTRAINT ck_visita_cantidad CHECK (cantidad_personas > 0),
    CONSTRAINT ck_visita_estado CHECK (estado IN ('AGENDADA', 'REALIZADA', 'CANCELADA'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  COMMENT = 'Visitas que agenda un visitante registrado o un docente, a un recinto o a una ruta (CU-13 y CU-30). Solo se guarda la cantidad de personas, sin datos personales.';


-- =============================================================================
-- 6. GUÍAS VOLUNTARIOS
-- =============================================================================

-- -----------------------------------------------------------------------------
-- GUIA: Guías voluntarios (datos ficticios) de cada recinto. No son usuarios
--   de la app: los administra el encargado del recinto (CU-22).
-- -----------------------------------------------------------------------------
CREATE TABLE guia (
    id_guia        BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador del guía.',
    id_recinto     BIGINT NOT NULL COMMENT 'Recinto donde hace recorridos.',
    nombre         VARCHAR(50) NOT NULL COMMENT 'Nombre de pila.',
    apellido       VARCHAR(50) NOT NULL COMMENT 'Apellido.',
    activo         TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1 = activo, 0 = dado de baja (borrado lógico).',
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de creación del registro.',
    PRIMARY KEY (id_guia),
    CONSTRAINT uk_guia_recinto UNIQUE (id_guia, id_recinto),
    KEY idx_guia_recinto (id_recinto),
    CONSTRAINT fk_guia_recinto FOREIGN KEY (id_recinto)
        REFERENCES recinto (id_recinto),
    CONSTRAINT ck_guia_activo CHECK (activo IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  COMMENT = 'Guías voluntarios (datos ficticios) de cada recinto. No son usuarios de la app: los administra el encargado del recinto (CU-22).';

-- -----------------------------------------------------------------------------
-- DISPONIBILIDAD_GUIA: Bloques de tiempo en que un guía está disponible
--   (CU-22).
-- -----------------------------------------------------------------------------
CREATE TABLE disponibilidad_guia (
    id_disponibilidad BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador del bloque.',
    id_guia           BIGINT NOT NULL COMMENT 'Guía disponible.',
    fecha_hora_inicio DATETIME NOT NULL COMMENT 'Inicio de la disponibilidad.',
    fecha_hora_fin    DATETIME NOT NULL COMMENT 'Término de la disponibilidad.',
    PRIMARY KEY (id_disponibilidad),
    CONSTRAINT uk_disponibilidad_guia_inicio UNIQUE (id_guia, fecha_hora_inicio),
    CONSTRAINT fk_disponibilidad_guia_guia FOREIGN KEY (id_guia)
        REFERENCES guia (id_guia) ON DELETE CASCADE,
    CONSTRAINT ck_disponibilidad_guia_fechas CHECK (fecha_hora_fin > fecha_hora_inicio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  COMMENT = 'Bloques de tiempo en que un guía está disponible (CU-22).';

-- -----------------------------------------------------------------------------
-- SOLICITUD_GUIA: Solicitudes de guía de una visita para uno de sus recintos
--   (CU-14, CU-15 y CU-22). La resuelve el encargado de ese recinto.
-- -----------------------------------------------------------------------------
CREATE TABLE solicitud_guia (
    id_solicitud        BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la solicitud.',
    id_visita           BIGINT NOT NULL COMMENT 'Visita que necesita el guía.',
    id_recinto          BIGINT NOT NULL COMMENT 'Recinto de la visita donde se necesita el guía.',
    id_guia             BIGINT NULL COMMENT 'Guía asignado; NULL mientras está pendiente. Debe ser del mismo recinto.',
    id_usuario_resuelve BIGINT NULL COMMENT 'Encargado que asignó o rechazó la solicitud.',
    estado              VARCHAR(10) NOT NULL DEFAULT 'PENDIENTE' COMMENT 'PENDIENTE, ASIGNADA, RECHAZADA o CANCELADA.',
    motivo_rechazo      VARCHAR(300) NULL COMMENT 'Obligatorio si la solicitud se rechaza.',
    fecha_solicitud     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Cuándo se creó la solicitud.',
    fecha_resolucion    DATETIME NULL COMMENT 'Cuándo se asignó o rechazó.',
    PRIMARY KEY (id_solicitud),
    CONSTRAINT uk_solicitud_guia_visita UNIQUE (id_visita, id_recinto),
    KEY idx_solicitud_guia_recinto (id_recinto),
    KEY idx_solicitud_guia_guia (id_guia, id_recinto),
    KEY idx_solicitud_guia_usuario (id_usuario_resuelve),
    CONSTRAINT fk_solicitud_guia_visita FOREIGN KEY (id_visita)
        REFERENCES visita (id_visita) ON DELETE CASCADE,
    CONSTRAINT fk_solicitud_guia_recinto FOREIGN KEY (id_recinto)
        REFERENCES recinto (id_recinto),
    CONSTRAINT fk_solicitud_guia_guia FOREIGN KEY (id_guia, id_recinto)
        REFERENCES guia (id_guia, id_recinto),
    CONSTRAINT fk_solicitud_guia_usuario FOREIGN KEY (id_usuario_resuelve)
        REFERENCES usuario (id_usuario),
    CONSTRAINT ck_solicitud_guia_estado
        CHECK (estado IN ('PENDIENTE', 'ASIGNADA', 'RECHAZADA', 'CANCELADA')),
    CONSTRAINT ck_solicitud_guia_asignada
        CHECK (estado <> 'ASIGNADA' OR id_guia IS NOT NULL),
    CONSTRAINT ck_solicitud_guia_rechazo
        CHECK (estado <> 'RECHAZADA' OR motivo_rechazo IS NOT NULL)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  COMMENT = 'Solicitudes de guía de una visita para uno de sus recintos (CU-14, CU-15 y CU-22). La resuelve el encargado de ese recinto.';


-- =============================================================================
-- 7. VISTAS
-- =============================================================================

-- Recintos activos con sus datos para mostrarlos en el mapa (CU-01, CU-02).
CREATE OR REPLACE VIEW v_recinto_mapa AS
SELECT r.id_recinto,
       r.nombre_oficial,
       r.tipo_recinto,
       co.nombre AS comuna,
       r.calle,
       r.numero_calle,
       r.latitud,
       r.longitud,
       r.estado_apertura,
       r.fecha_estado
  FROM recinto r
  JOIN comuna co ON co.id_comuna = r.id_comuna
 WHERE r.activo = 1;

-- Eventos, avisos, aperturas y cierres publicados que aún no terminan, con las
-- coordenadas de su recinto (CU-07). Si no tienen término, se muestran hasta
-- 24 horas después de su inicio.
CREATE OR REPLACE VIEW v_evento_mapa AS
SELECT e.id_evento,
       e.tipo_evento,
       e.titulo,
       e.fecha_hora_inicio,
       e.fecha_hora_fin,
       r.id_recinto,
       r.nombre_oficial AS recinto,
       r.latitud,
       r.longitud
  FROM evento e
  JOIN recinto r ON r.id_recinto = e.id_recinto
 WHERE e.estado = 'PUBLICADO'
   AND r.activo = 1
   AND COALESCE(e.fecha_hora_fin, e.fecha_hora_inicio + INTERVAL 1 DAY) >= NOW();

-- Aperturas y cierres excepcionales vigentes o próximos de cada recinto
-- (CU-06 y CU-18).
CREATE OR REPLACE VIEW v_horario_excepcional AS
SELECT e.id_evento AS id_excepcion,
       e.id_recinto,
       CASE e.tipo_evento WHEN 'APERTURA_ESPECIAL' THEN 'APERTURA' ELSE 'CIERRE' END AS tipo,
       e.titulo AS motivo,
       e.fecha_hora_inicio,
       e.fecha_hora_fin,
       e.id_usuario_publica AS id_usuario_registra
  FROM evento e
 WHERE e.tipo_evento IN ('APERTURA_ESPECIAL', 'CIERRE_ESPECIAL')
   AND e.estado = 'PUBLICADO'
   AND e.fecha_hora_fin >= NOW();

-- Resumen de cada ruta activa: cantidad de paradas y tiempo total de visita
-- (CU-10). Estos valores se calculan y no se guardan (3FN).
CREATE OR REPLACE VIEW v_ruta_resumen AS
SELECT ru.id_ruta,
       ru.nombre,
       ru.tipo_ruta,
       ru.modo_traslado,
       COUNT(p.orden)                        AS cantidad_paradas,
       COALESCE(SUM(p.tiempo_visita_min), 0) AS tiempo_visita_total_min
  FROM ruta ru
  LEFT JOIN parada_ruta p ON p.id_ruta = ru.id_ruta
 WHERE ru.activo = 1
 GROUP BY ru.id_ruta, ru.nombre, ru.tipo_ruta, ru.modo_traslado;


-- =============================================================================
-- 8. DATOS INICIALES (COMUNAS)
-- =============================================================================

-- Comunas (provincia de Santiago y Puente Alto). Agregar las que falten.
INSERT INTO comuna (nombre) VALUES
    ('Santiago'),
    ('Cerrillos'),
    ('Cerro Navia'),
    ('Conchalí'),
    ('El Bosque'),
    ('Estación Central'),
    ('Huechuraba'),
    ('Independencia'),
    ('La Cisterna'),
    ('La Florida'),
    ('La Granja'),
    ('La Pintana'),
    ('La Reina'),
    ('Las Condes'),
    ('Lo Barnechea'),
    ('Lo Espejo'),
    ('Lo Prado'),
    ('Macul'),
    ('Maipú'),
    ('Ñuñoa'),
    ('Pedro Aguirre Cerda'),
    ('Peñalolén'),
    ('Providencia'),
    ('Pudahuel'),
    ('Quilicura'),
    ('Quinta Normal'),
    ('Recoleta'),
    ('Renca'),
    ('San Joaquín'),
    ('San Miguel'),
    ('San Ramón'),
    ('Vitacura'),
    ('Puente Alto');


-- =============================================================================
-- FIN DEL SCRIPT
-- =============================================================================
