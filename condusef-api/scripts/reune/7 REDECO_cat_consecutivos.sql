DELETE FROM redeco.cat_consecutivos 
WHERE id = 'consulta' OR id = 'reclamacion' OR id = 'aclaracion';

INSERT INTO redeco.cat_consecutivos VALUES ('reune', 'UNE-2024-', 1, 1);
INSERT INTO redeco.cat_consecutivos VALUES ('reune', 'UNE-2024-', 1, 2);