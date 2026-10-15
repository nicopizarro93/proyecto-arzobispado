-- =============================================================================
-- V8 - Datos de prueba: un visitante, un docente y sus visitas (HU-21, HU-22, HU-25).
-- Las dos cuentas usan la contraseña Prueba123 (solo para desarrollo).
-- =============================================================================

INSERT INTO usuario (rol, nombre, apellido, correo, password_hash, institucion, fecha_acepta_privacidad) VALUES
    ('VISITANTE', 'Lucía', 'Herrera', 'visitante@santiagosacro.cl',
     '$2a$10$JIOeZDYRR7/30iROevj5TukVtifDs2N1AL91q6kOAji/ekN3ufdFC', NULL, NOW()),
    ('DOCENTE', 'Jorge', 'Contreras', 'docente@santiagosacro.cl',
     '$2a$10$JIOeZDYRR7/30iROevj5TukVtifDs2N1AL91q6kOAji/ekN3ufdFC', 'Colegio San Esteban', NOW());

SET @visitante  = (SELECT id_usuario FROM usuario WHERE correo = 'visitante@santiagosacro.cl');
SET @docente    = (SELECT id_usuario FROM usuario WHERE correo = 'docente@santiagosacro.cl');
SET @capilla    = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Capilla del Buen Pastor');
SET @museo      = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Museo de Arte Sacro Los Aromos');
SET @ruta_llano = (SELECT id_ruta FROM ruta WHERE nombre = 'Ruta del Llano de Maipú');

INSERT INTO visita (id_usuario, id_recinto, id_ruta, fecha_hora_inicio, cantidad_personas,
                    nivel_curso, observaciones, estado) VALUES
    -- Visitante: visita al museo con su familia.
    (@visitante, @museo, NULL, TIMESTAMP(CURDATE() + INTERVAL 7 DAY, '11:00'), 4,
     NULL, 'Vamos con dos niños.', 'AGENDADA'),
    -- Visitante: visita que ya pasó.
    (@visitante, @capilla, NULL, TIMESTAMP(CURDATE() - INTERVAL 10 DAY, '10:00'), 2,
     NULL, NULL, 'REALIZADA'),
    -- Docente: salida pedagógica por la Ruta del Llano (HU-22).
    (@docente, NULL, @ruta_llano, TIMESTAMP(CURDATE() + INTERVAL 12 DAY, '09:30'), 32,
     '3° medio', 'Curso de Historia; llegamos en bus.', 'AGENDADA');