-- =============================================================================
-- V4 - Datos de prueba: cuentas internas y horarios regulares (HU-26, HU-27).
-- Las tres cuentas usan la contraseña Prueba123 (solo para desarrollo).
-- =============================================================================

-- Recintos a los que se asignan los encargados y los horarios.
SET @capilla   = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Capilla del Buen Pastor');
SET @san_lucas = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Templo San Lucas del Llano');
SET @museo     = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Museo de Arte Sacro Los Aromos');

-- Cuentas que en la app real crea el Administrador (CU-24). Hash BCrypt de "Prueba123".
INSERT INTO usuario (rol, id_recinto, nombre, apellido, correo, password_hash) VALUES
    ('ADMINISTRADOR', NULL,     'Ana',    'Rojas', 'admin@aurasacrapp.cl',
     '$2a$10$JIOeZDYRR7/30iROevj5TukVtifDs2N1AL91q6kOAji/ekN3ufdFC'),
    ('ENCARGADO',     @capilla, 'Pedro',  'Soto',  'encargado.capilla@aurasacrapp.cl',
     '$2a$10$JIOeZDYRR7/30iROevj5TukVtifDs2N1AL91q6kOAji/ekN3ufdFC'),
    ('ENCARGADO',     @museo,   'Carmen', 'Muñoz', 'encargado.museo@aurasacrapp.cl',
     '$2a$10$JIOeZDYRR7/30iROevj5TukVtifDs2N1AL91q6kOAji/ekN3ufdFC');

-- Horarios regulares (1 = lunes ... 7 = domingo). Un día puede tener dos bloques.
INSERT INTO horario_regular (id_recinto, dia_semana, hora_apertura, hora_cierre) VALUES
    -- Capilla del Buen Pastor: lunes a viernes mañana y tarde; fin de semana solo mañana.
    (@capilla, 1, '09:00', '13:00'), (@capilla, 1, '16:00', '19:00'),
    (@capilla, 2, '09:00', '13:00'), (@capilla, 2, '16:00', '19:00'),
    (@capilla, 3, '09:00', '13:00'), (@capilla, 3, '16:00', '19:00'),
    (@capilla, 4, '09:00', '13:00'), (@capilla, 4, '16:00', '19:00'),
    (@capilla, 5, '09:00', '13:00'), (@capilla, 5, '16:00', '19:00'),
    (@capilla, 6, '10:00', '13:00'),
    (@capilla, 7, '09:00', '12:00'),
    -- Templo San Lucas del Llano: lunes a sábado en la mañana; domingo mañana y tarde.
    (@san_lucas, 1, '08:30', '12:30'), (@san_lucas, 2, '08:30', '12:30'),
    (@san_lucas, 3, '08:30', '12:30'), (@san_lucas, 4, '08:30', '12:30'),
    (@san_lucas, 5, '08:30', '12:30'), (@san_lucas, 6, '08:30', '12:30'),
    (@san_lucas, 7, '09:00', '13:00'), (@san_lucas, 7, '18:00', '20:00'),
    -- Museo de Arte Sacro Los Aromos: martes a domingo, cerrado los lunes.
    (@museo, 2, '10:00', '18:00'), (@museo, 3, '10:00', '18:00'),
    (@museo, 4, '10:00', '18:00'), (@museo, 5, '10:00', '18:00'),
    (@museo, 6, '10:00', '18:00'), (@museo, 7, '10:00', '18:00');