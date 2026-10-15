-- =============================================================================
-- V6 - Datos de prueba: galería multimedia de algunos recintos (HU-10).
-- Las imágenes son de muestra (picsum.photos): la palabra después de /seed/
-- hace que cada URL muestre siempre la misma foto.
-- =============================================================================

SET @capilla    = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Capilla del Buen Pastor');
SET @san_lucas  = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Templo San Lucas del Llano');
SET @museo      = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Museo de Arte Sacro Los Aromos');
SET @santa_rosa = (SELECT id_recinto FROM recinto WHERE nombre_oficial = 'Parroquia Santa Rosa de la Cañada');

INSERT INTO recurso_multimedia (id_recinto, tipo_recurso, url, titulo, orden) VALUES
    (@capilla,    'IMAGEN', 'https://picsum.photos/seed/buen-pastor-fachada/800/600',    'Fachada principal', 1),
    (@capilla,    'IMAGEN', 'https://picsum.photos/seed/buen-pastor-nave/800/600',       'Nave central', 2),
    (@capilla,    'IMAGEN', 'https://picsum.photos/seed/buen-pastor-campanario/800/600', 'Campanario', 3),
    (@san_lucas,  'IMAGEN', 'https://picsum.photos/seed/san-lucas-vitrales/800/600',     'Vitrales donados por familias del sector', 1),
    (@san_lucas,  'IMAGEN', 'https://picsum.photos/seed/san-lucas-altar/800/600',        'Altar mayor', 2),
    (@museo,      'IMAGEN', 'https://picsum.photos/seed/aromos-plateria/800/600',        'Sala de platería', 1),
    (@museo,      'IMAGEN', 'https://picsum.photos/seed/aromos-imagineria/800/600',      'Imaginería colonial', 2),
    (@museo,      'IMAGEN', 'https://picsum.photos/seed/aromos-ornamentos/800/600',      'Ornamentos litúrgicos', 3),
    (@santa_rosa, 'IMAGEN', 'https://picsum.photos/seed/santa-rosa-retablo/800/600',     'Retablo original', 1),
    (@santa_rosa, 'IMAGEN', 'https://picsum.photos/seed/santa-rosa-fiesta/800/600',      'Fiesta patronal', 2);