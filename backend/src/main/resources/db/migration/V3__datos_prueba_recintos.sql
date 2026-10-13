-- =============================================================================
-- V3 - Datos de prueba: recintos ficticios (HU-09, HU-10).
-- Todos los nombres, direcciones y coordenadas son ficticios.
-- =============================================================================

INSERT INTO recinto (tipo_recinto, id_comuna, nombre_oficial, calle, numero_calle,
                     latitud, longitud, descripcion_historica, estado_apertura, activo)
VALUES
    ('TEMPLO', (SELECT id_comuna FROM comuna WHERE nombre = 'Maipú'),
     'Capilla del Buen Pastor', 'Pasaje El Llano', '118', -33.509700, -70.757400,
     'Levantada a fines del siglo XIX por los vecinos del antiguo barrio del Llano. Conserva su fachada de albañilería y un campanario de madera restaurado.',
     'CERRADO', 1),
    ('TEMPLO', (SELECT id_comuna FROM comuna WHERE nombre = 'Maipú'),
     'Templo San Lucas del Llano', 'Av. Los Aromos', '1240', -33.511200, -70.752900,
     'Templo de una nave construido a comienzos del siglo XX, con vitrales donados por familias del sector.',
     'ABIERTO', 1),
    ('MUSEO', (SELECT id_comuna FROM comuna WHERE nombre = 'Maipú'),
     'Museo de Arte Sacro Los Aromos', 'Av. Los Aromos', '1302', -33.512600, -70.750100,
     'Reúne imaginería, platería y ornamentos litúrgicos de parroquias del poniente de Santiago.',
     'ABIERTO', 1),
    ('TEMPLO', (SELECT id_comuna FROM comuna WHERE nombre = 'Cerrillos'),
     'Parroquia Santa Rosa de la Cañada', 'Calle La Cañada', '77', -33.495800, -70.716300,
     'Parroquia de adobe y madera que mantiene su retablo original y una tradicional fiesta patronal.',
     'EN_CELEBRACION', 1),
    ('ARCHIVO', (SELECT id_comuna FROM comuna WHERE nombre = 'Recoleta'),
     'Archivo Histórico La Chimba', 'Calle Dominica', '540', -33.428900, -70.646200,
     'Custodia libros parroquiales, planos y fotografías de las capillas del sector norte.',
     'CERRADO', 1),
    ('TEMPLO', (SELECT id_comuna FROM comuna WHERE nombre = 'Puente Alto'),
     'Capilla San José Obrero', 'Pasaje Los Olivos', '320', -33.611700, -70.575800,
     'Capilla levantada por trabajadores del barrio a mediados del siglo XX, con murales de la vida de San José.',
     'ABIERTO', 1),
    ('COLEGIO', (SELECT id_comuna FROM comuna WHERE nombre = 'Puente Alto'),
     'Colegio San Esteban', 'Calle Los Talleres', NULL, -33.604500, -70.581200,
     'Colegio con una capilla interior y un pequeño museo escolar abiertos a visitas guiadas.',
     'CERRADO', 1),
    ('TEMPLO', (SELECT id_comuna FROM comuna WHERE nombre = 'Lo Espejo'),
     'Capilla Antigua del Molino', 'Camino El Molino', '15', -33.520300, -70.690800,
     'Recinto dado de baja: sirve para comprobar que los inactivos no aparecen en la app.',
     'CERRADO', 0);