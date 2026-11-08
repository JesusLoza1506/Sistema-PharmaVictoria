-- Agrega columna para marcar clientes rápidos/temporales
ALTER TABLE `clientes`
ADD COLUMN `es_rapido` BOOLEAN NOT NULL DEFAULT 0
AFTER `documento`;