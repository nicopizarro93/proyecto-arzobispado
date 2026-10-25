-- =============================================================================
-- V7 - Datos de prueba: rutas sugeridas con sus paradas (HU-17, HU-19).
-- =============================================================================

SET @admin      = (SELECT id_usuario FROM usuario WHERE correo = 'admin@aurasacrapp.cl');
SET @capilla    = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Capilla del Buen Pastor');
SET @san_lucas  = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Templo San Lucas del Llano');
SET @museo      = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Museo de Arte Sacro Los Aromos');
SET @santa_rosa = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Parroquia Santa Rosa de la Cañada');
SET @archivo    = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Archivo Histórico La Chimba');
SET @san_jose   = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Capilla San José Obrero');
SET @colegio    = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Colegio San Esteban');

-- 1. A pie: tres recintos a pocas cuadras entre sí.
INSERT INTO ruta (id_usuario_creador, nombre, descripcion, tipo_ruta, modo_traslado, activo) VALUES
    (@admin, 'Ruta del Llano de Maipú',
     'Tres recintos del antiguo barrio del Llano, a pocas cuadras entre sí.', 'SUGERIDA', 'A_PIE', 1);
SET @ruta_llano = LAST_INSERT_ID();
INSERT INTO parada_ruta (id_ruta, orden, id_recinto, tiempo_visita_min) VALUES
    (@ruta_llano, 1, @capilla, 30), (@ruta_llano, 2, @san_lucas, 30), (@ruta_llano, 3, @museo, 60);

-- 2. Transporte público: de Maipú a Recoleta.
INSERT INTO ruta (id_usuario_creador, nombre, descripcion, tipo_ruta, modo_traslado, activo) VALUES
    (@admin, 'Patrimonio del poniente',
     'Del museo de Maipú al archivo de La Chimba, pasando por la parroquia de Cerrillos.',
     'SUGERIDA', 'TRANSPORTE_PUBLICO', 1);
SET @ruta_poniente = LAST_INSERT_ID();
INSERT INTO parada_ruta (id_ruta, orden, id_recinto, tiempo_visita_min) VALUES
    (@ruta_poniente, 1, @museo, 60), (@ruta_poniente, 2, @santa_rosa, 30), (@ruta_poniente, 3, @archivo, 45);

-- 3. Vehículo: dos recintos de Puente Alto.
INSERT INTO ruta (id_usuario_creador, nombre, descripcion, tipo_ruta, modo_traslado, activo) VALUES
    (@admin, 'Fe y escuela en Puente Alto',
     'Capilla del barrio y colegio con museo escolar.', 'SUGERIDA', 'VEHICULO', 1);
SET @ruta_sur = LAST_INSERT_ID();
INSERT INTO parada_ruta (id_ruta, orden, id_recinto, tiempo_visita_min) VALUES
    (@ruta_sur, 1, @san_jose, 30), (@ruta_sur, 2, @colegio, 45);

-- 4. Desactivada por el administrador: no debe aparecer en la app (HU-36).
INSERT INTO ruta (id_usuario_creador, nombre, descripcion, tipo_ruta, modo_traslado, activo) VALUES
    (@admin, 'Ruta en revisión', 'Desactivada: sirve para probar que no se muestra.', 'SUGERIDA', 'A_PIE', 0);
SET @ruta_inactiva = LAST_INSERT_ID();
INSERT INTO parada_ruta (id_ruta, orden, id_recinto, tiempo_visita_min) VALUES
    (@ruta_inactiva, 1, @capilla, 30), (@ruta_inactiva, 2, @santa_rosa, 30);