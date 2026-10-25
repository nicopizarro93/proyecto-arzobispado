-- =============================================================================
-- V5 - Datos de prueba: eventos, avisos, aperturas y cierres (HU-14, HU-28 a HU-30).
-- Las fechas se calculan desde el día en que se aplica la migración, para que
-- siempre haya eventos próximos al probar la app.
-- =============================================================================

SET @capilla     = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Capilla del Buen Pastor');
SET @museo       = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Museo de Arte Sacro Los Aromos');
SET @enc_capilla = (SELECT id_usuario FROM usuario WHERE correo = 'encargado.capilla@aurasacrapp.cl');
SET @enc_museo   = (SELECT id_usuario FROM usuario WHERE correo = 'encargado.museo@aurasacrapp.cl');

INSERT INTO evento (id_recinto, id_usuario_publica, tipo_evento, titulo, descripcion,
                    fecha_hora_inicio, fecha_hora_fin, estado) VALUES
    -- Capilla del Buen Pastor
    (@capilla, @enc_capilla, 'CONCIERTO', 'Concierto de música sacra: Coro del Llano',
     'El coro del barrio interpreta obras de música sacra chilena del siglo XIX.',
     TIMESTAMP(CURDATE() + INTERVAL 5 DAY, '19:00'), TIMESTAMP(CURDATE() + INTERVAL 5 DAY, '20:30'), 'PUBLICADO'),
    (@capilla, @enc_capilla, 'HITO_LITURGICO', 'Misa solemne por el aniversario de la capilla',
     'Celebración presidida por el párroco, con procesión por el pasaje El Llano.',
     TIMESTAMP(CURDATE() + INTERVAL 14 DAY, '11:00'), TIMESTAMP(CURDATE() + INTERVAL 14 DAY, '12:30'), 'PUBLICADO'),
    (@capilla, @enc_capilla, 'HITO_LITURGICO', 'Bendición de las mascotas del barrio',
     'Sin hora de término: se considera vigente durante el día.',
     TIMESTAMP(CURDATE() + INTERVAL 3 DAY, '12:00'), NULL, 'PUBLICADO'),
    (@capilla, @enc_capilla, 'AVISO', 'Mantención del campanario',
     'El campanario permanece cerrado a las visitas. La capilla sigue abierta en su horario habitual.',
     TIMESTAMP(CURDATE() + INTERVAL 1 DAY, '09:00'), TIMESTAMP(CURDATE() + INTERVAL 8 DAY, '18:00'), 'PUBLICADO'),
    (@capilla, @enc_capilla, 'CIERRE_ESPECIAL', 'Cierre por limpieza de vitrales', NULL,
     TIMESTAMP(CURDATE() + INTERVAL 10 DAY, '09:00'), TIMESTAMP(CURDATE() + INTERVAL 10 DAY, '13:00'), 'PUBLICADO'),
    (@capilla, @enc_capilla, 'ACTIVIDAD_CULTURAL', 'Recorrido por la historia del barrio del Llano',
     'Ya terminado: no debe aparecer en la lista pública.',
     TIMESTAMP(CURDATE() - INTERVAL 7 DAY, '10:00'), TIMESTAMP(CURDATE() - INTERVAL 7 DAY, '12:00'), 'PUBLICADO'),
    (@capilla, @enc_capilla, 'CONCIERTO', 'Recital de órgano',
     'Cancelado por reparación del órgano: solo se ve en el historial del encargado.',
     TIMESTAMP(CURDATE() + INTERVAL 12 DAY, '19:00'), TIMESTAMP(CURDATE() + INTERVAL 12 DAY, '20:00'), 'CANCELADO'),
    -- Museo de Arte Sacro Los Aromos
    (@museo, @enc_museo, 'EXPOSICION', 'Exposición: platería colonial del poniente',
     'Muestra de cálices, custodias y candelabros de parroquias del sector poniente.',
     TIMESTAMP(CURDATE() + INTERVAL 2 DAY, '10:00'), TIMESTAMP(CURDATE() + INTERVAL 40 DAY, '18:00'), 'PUBLICADO'),
    (@museo, @enc_museo, 'ACTIVIDAD_CULTURAL', 'Taller familiar de restauración',
     'Taller para niños y adultos sobre el cuidado de las imágenes religiosas antiguas.',
     TIMESTAMP(CURDATE() + INTERVAL 9 DAY, '11:00'), TIMESTAMP(CURDATE() + INTERVAL 9 DAY, '13:00'), 'PUBLICADO'),
    (@museo, @enc_museo, 'APERTURA_ESPECIAL', 'Apertura nocturna del museo',
     'Visitas guiadas con iluminación especial.',
     TIMESTAMP(CURDATE() + INTERVAL 6 DAY, '19:00'), TIMESTAMP(CURDATE() + INTERVAL 6 DAY, '23:00'), 'PUBLICADO');