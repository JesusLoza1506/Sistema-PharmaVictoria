-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: PHARMAVICTORIA
-- ------------------------------------------------------
-- Server version	8.4.3

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cliente_historial_cambio`
--

DROP TABLE IF EXISTS `cliente_historial_cambio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente_historial_cambio` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cliente_id` int NOT NULL,
  `campo_modificado` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `valor_anterior` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `valor_nuevo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `cliente_id` (`cliente_id`),
  CONSTRAINT `cliente_historial_cambio_ibfk_1` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente_historial_cambio`
--

LOCK TABLES `cliente_historial_cambio` WRITE;
/*!40000 ALTER TABLE `cliente_historial_cambio` DISABLE KEYS */;
INSERT INTO `cliente_historial_cambio` VALUES (1,3,'documento','20567891234','20488990013','jesus','2025-11-14 23:25:05'),(2,3,'frecuente','false','true','jesus','2025-11-14 23:25:11'),(3,5,'frecuente','false','true','jesus','2025-11-14 23:25:16'),(4,11,'frecuente','false','true','jesus','2025-11-14 23:25:20'),(5,21,'CREACION',NULL,'Nubefact','jesus','2025-11-14 23:29:54'),(6,21,'direccion','','Av emancipación','jesus','2025-11-14 23:31:09'),(7,2,'frecuente','false','true','jesus','2025-11-17 23:42:11'),(8,8,'frecuente','false','true','jesus','2025-11-17 23:42:17'),(9,31,'CREACION',NULL,'PRUEBA PRUEBA','jesus','2025-11-17 23:45:06'),(10,32,'CREACION',NULL,'PRUEBAS RUC','jesus','2025-11-17 23:45:47'),(11,20,'nombres','José Miguel','JOSÉ MIGUEL','jesus','2025-11-17 23:47:02'),(12,20,'apellidos','Castillo Vega','CASTILLO VEGA','jesus','2025-11-17 23:47:02'),(13,20,'fecha_nacimiento','1975-01-27','1964-12-31','jesus','2025-11-17 23:47:02'),(14,20,'frecuente','false','true','jesus','2025-11-17 23:47:18'),(15,2,'razon_social','Droguería Salud Total E.I.R.L.','Droguería Salud Total E.I.R.L.A','jesus','2025-11-18 04:25:25'),(16,2,'documento','20456789123','20456789128','jesus','2025-11-18 19:58:56'),(17,20,'email','jose.castillo@live.com','lozayataco@gmail.com','jesus','2025-11-19 17:28:35'),(18,36,'CREACION',NULL,'SERVICIOS GENERALES LUCIANITAS E.I.R.L.','jesus','2025-11-20 11:01:50'),(19,21,'razon_social','Nubefact','Productos Generales','jesus','2025-11-20 11:03:37'),(20,37,'CREACION',NULL,'LUBRICENTROS GLORIA','jesus','2025-11-20 11:08:12');
/*!40000 ALTER TABLE `cliente_historial_cambio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente_puntos_historial`
--

DROP TABLE IF EXISTS `cliente_puntos_historial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente_puntos_historial` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cliente_id` int NOT NULL,
  `tipo_movimiento` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,
  `puntos` int NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP,
  `usuario_id` int NOT NULL,
  `venta_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `cliente_id` (`cliente_id`),
  KEY `venta_id` (`venta_id`),
  CONSTRAINT `cliente_puntos_historial_ibfk_1` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`),
  CONSTRAINT `cliente_puntos_historial_ibfk_2` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente_puntos_historial`
--

LOCK TABLES `cliente_puntos_historial` WRITE;
/*!40000 ALTER TABLE `cliente_puntos_historial` DISABLE KEYS */;
INSERT INTO `cliente_puntos_historial` VALUES (1,20,'GANADO',5,'Puntos ganados por compra','2025-11-21 01:55:05',12,2),(2,20,'EXPIRADO',5,'Puntos expirados por anulación de venta','2025-11-21 23:41:30',12,2),(3,40,'GANADO',5,'Puntos ganados por compra','2025-11-21 23:49:12',12,6),(4,41,'GANADO',3,'Puntos ganados por compra','2025-11-21 23:51:09',12,7),(5,42,'GANADO',4,'Puntos ganados por compra','2025-11-21 23:51:49',12,8),(6,43,'GANADO',7,'Puntos ganados por compra','2025-11-21 23:52:22',12,9);
/*!40000 ALTER TABLE `cliente_puntos_historial` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `dni` varchar(8) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nombres` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `apellidos` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `razon_social` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `direccion` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `puntos_totales` int DEFAULT '0',
  `puntos_usados` int DEFAULT '0',
  `puntos_disponibles` int GENERATED ALWAYS AS ((`puntos_totales` - `puntos_usados`)) STORED,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `tipo_cliente` enum('Natural','Empresa') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Natural',
  `documento` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `dni` (`dni`),
  KEY `idx_dni` (`dni`),
  KEY `idx_nombres` (`nombres`,`apellidos`),
  KEY `idx_puntos` (`puntos_disponibles`),
  KEY `idx_clientes_puntos_frecuente` (`puntos_disponibles`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` (`id`, `dni`, `nombres`, `apellidos`, `razon_social`, `telefono`, `email`, `direccion`, `fecha_nacimiento`, `puntos_totales`, `puntos_usados`, `created_at`, `updated_at`, `tipo_cliente`, `documento`) VALUES (2,NULL,'N/A','N/A','Droguería Salud Total E.I.R.L.A','912345678','ventas@saludtotal.com','Jr. Comercio 456, Arequipa',NULL,0,0,'2025-11-13 18:30:23','2025-11-19 00:58:55','Empresa','20456789128'),(3,NULL,'N/A','N/A','Botica del Pueblo S.R.L.','998877665','admin@boticapueblo.pe','Calle Real 789, Cusco',NULL,0,0,'2025-11-13 18:30:23','2025-11-15 04:25:11','Empresa','20488990013'),(4,NULL,'N/A','N/A','Distribuidora Medifarma SAC','955443322','compras@medifarma.com.pe','Av. Industrial 101, Callao',NULL,0,0,'2025-11-13 18:30:23','2025-11-13 18:37:28','Empresa','20198765432'),(5,NULL,'N/A','N/A','Farmacias Peruanas S.A.','933221144','gerencia@farmaciasperuanas.pe','Av. Larco 555, Trujillo',NULL,0,0,'2025-11-13 18:30:23','2025-11-15 04:25:15','Empresa','20111222333'),(6,NULL,'N/A','N/A','Quifarma Norte E.I.R.L.','977665544','ventas@quifarmanorte.com','Jr. Bolívar 210, Chiclayo',NULL,0,0,'2025-11-13 18:30:23','2025-11-13 18:37:28','Empresa','20344556677'),(7,NULL,'N/A','N/A','Universal Medic S.A.C.','966554433','logistica@universalmedic.pe','Av. Canadá 890, Lima',NULL,0,0,'2025-11-13 18:30:23','2025-11-13 18:37:28','Empresa','20233445566'),(8,NULL,'N/A','N/A','Bayer Andino Perú','944332211','distribucion@bayerandino.pe','Av. Salaverry 3200, Lima',NULL,0,0,'2025-11-13 18:30:23','2025-11-18 04:42:17','Empresa','20199887766'),(9,NULL,'N/A','N/A','Sanofi Aventis del Perú','933221100','ventas@sanofi.pe','Av. Primavera 1200, Surco',NULL,0,0,'2025-11-13 18:30:23','2025-11-13 18:37:28','Empresa','20177665544'),(10,NULL,'N/A','N/A','Genfar Perú S.A.','922110099','compras@genfar.pe','Calle Los Pinos 300, San Isidro',NULL,0,0,'2025-11-13 18:30:23','2025-11-13 18:37:28','Empresa','20255667788'),(11,NULL,'N/A','N/A','El Sol E.I.R.L.','911223344','admin@elsol.com.pe','Jr. Amazonas 150, Iquitos',NULL,0,0,'2025-11-13 18:30:23','2025-11-15 04:25:20','Empresa','20488990011'),(12,NULL,'N/A','N/A','FarmaCenter del Sur','988776655','ventas@farmasur.pe','Av. Grau 700, Tacna',NULL,0,0,'2025-11-13 18:30:23','2025-11-13 18:37:28','Empresa','20511223344'),(13,NULL,'N/A','N/A','Vitalis Distribuidora','977554433','contacto@vitalis.pe','Av. Universitaria 1800, Lima',NULL,0,0,'2025-11-13 18:30:23','2025-11-13 18:37:28','Empresa','20366778899'),(14,'12345678','Juan Carlos','Pérez Gómez',NULL,'987123456','juan.perez@gmail.com','Jr. Lima 123, Miraflores','1985-03-15',0,0,'2025-11-13 18:30:23','2025-11-21 06:04:59','Natural','12345678'),(15,'87654321','María Elena','López Vargas',NULL,'987654321','maria.lopez@hotmail.com','Av. Arequipa 456, Lince','1990-07-22',0,0,'2025-11-13 18:30:23','2025-11-21 06:04:59','Natural','87654321'),(16,'11223344','Pedro Antonio','Ramírez Soto',NULL,'991122334','pedro.ramirez@outlook.com','Calle Los Olivos 789, San Juan','1978-11-30',0,0,'2025-11-13 18:30:23','2025-11-21 06:04:59','Natural','11223344'),(17,'55667788','Ana Sofía','Martínez Ruiz',NULL,'944556677','ana.martinez@gmail.com','Jr. Unión 101, Pueblo Libre','1995-05-10',0,0,'2025-11-13 18:30:23','2025-11-21 06:04:59','Natural','55667788'),(18,'33445566','Luis Fernando','García Mendoza',NULL,'933445566','luis.garcia@yahoo.com','Av. Brasil 250, Magdalena','1982-09-18',0,0,'2025-11-13 18:30:23','2025-11-18 08:08:21','Natural','33445566'),(19,'77889900','Carmen Rosa','Torres Flores',NULL,'922778899','carmen.torres@gmail.com','Calle Shell 300, Barranco','1988-12-05',0,0,'2025-11-13 18:30:23','2025-11-21 06:04:59','Natural','77889900'),(20,'99001122','JOSÉ MIGUEL','CASTILLO VEGA',NULL,'911990011','lozayataco@gmail.com','Av. La Marina 500, San Miguel','1964-12-31',5,5,'2025-11-13 18:30:23','2025-11-22 04:41:29','Natural','99001122'),(21,NULL,'','','Productos Generales','986526321','nube@gmail.com','Av emancipación',NULL,0,0,'2025-11-15 04:29:54','2025-11-20 16:03:36','Empresa','20600695771'),(31,NULL,'PRUEBA','PRUEBA',NULL,'956236159','lozki@gmail.com','','2022-11-17',0,0,'2025-11-18 04:45:06','2025-11-18 04:45:06','Natural','72018559'),(32,NULL,'','','PRUEBAS RUC','956236123','loz@gmail.com','',NULL,0,0,'2025-11-18 04:45:47','2025-11-18 04:45:47','Empresa','74855625108'),(36,NULL,'','','SERVICIOS GENERALES LUCIANITAS E.I.R.L.','956514835','germanloza@gmail.xom','Sunampe',NULL,0,0,'2025-11-20 16:01:50','2025-11-20 16:01:50','Empresa','20609774631'),(37,NULL,'','','LUBRICENTROS GLORIA','923333839','puma09112004@gmail.com','Ica',NULL,0,0,'2025-11-20 16:08:12','2025-11-20 16:08:12','Empresa','20452326974'),(39,NULL,'CONSUMIDOR','FINAL',NULL,NULL,NULL,NULL,NULL,0,0,'2025-11-22 04:39:20','2025-11-22 04:39:20','Natural','00000000'),(40,NULL,'Fanny','Yataco',NULL,NULL,'','-',NULL,5,0,'2025-11-22 04:47:39','2025-11-22 04:49:11','Natural','21873849'),(41,NULL,'Jesús','Loza',NULL,NULL,'','-',NULL,3,0,'2025-11-22 04:51:04','2025-11-22 04:51:08','Natural','78459612'),(42,NULL,'Jesús','Loza',NULL,NULL,'','-',NULL,4,0,'2025-11-22 04:51:46','2025-11-22 04:51:48','Natural','78459612'),(43,NULL,'Fanny','Yataco',NULL,NULL,'','-',NULL,7,0,'2025-11-22 04:52:18','2025-11-22 04:52:21','Natural','21873849'),(44,NULL,'','','QUISPE ALDERETE FREDDY RUFINO',NULL,'','Sunampe',NULL,0,0,'2025-11-22 05:11:38','2025-11-22 05:11:38','Empresa','10468894501');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comprobantes`
--

DROP TABLE IF EXISTS `comprobantes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comprobantes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `venta_id` int NOT NULL,
  `tipo` enum('BOLETA','FACTURA','TICKET') COLLATE utf8mb4_general_ci NOT NULL,
  `serie` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,
  `numero` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `hash_sunat` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `estado_sunat` enum('GENERADO','ENVIADO','ACEPTADO','RECHAZADO') COLLATE utf8mb4_general_ci DEFAULT 'GENERADO',
  `fecha_emision` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `venta_id` (`venta_id`),
  CONSTRAINT `comprobantes_ibfk_1` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comprobantes`
--

LOCK TABLES `comprobantes` WRITE;
/*!40000 ALTER TABLE `comprobantes` DISABLE KEYS */;
INSERT INTO `comprobantes` VALUES (1,1,'TICKET','TKT1','000001',NULL,'GENERADO','2025-11-21 01:13:58'),(2,2,'BOLETA','BBB1','000001','','GENERADO','2025-11-21 01:55:05'),(3,3,'FACTURA','FFF1','000001','','GENERADO','2025-11-21 02:21:58'),(4,4,'TICKET','TKT1','000002',NULL,'GENERADO','2025-11-21 23:06:32'),(5,6,'BOLETA','BBB1','000002','','GENERADO','2025-11-21 23:49:12'),(6,7,'BOLETA','BBB1','000003','','GENERADO','2025-11-21 23:51:09'),(7,8,'BOLETA','BBB1','000004','','GENERADO','2025-11-21 23:51:49'),(8,9,'BOLETA','BBB1','000005','','GENERADO','2025-11-21 23:52:22'),(9,10,'FACTURA','FFF1','000002','','GENERADO','2025-11-22 00:11:45');
/*!40000 ALTER TABLE `comprobantes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuracion`
--

DROP TABLE IF EXISTS `configuracion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configuracion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `clave` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `valor` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo_dato` enum('STRING','INTEGER','DECIMAL','BOOLEAN','JSON') COLLATE utf8mb4_unicode_ci DEFAULT 'STRING',
  `categoria` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `clave` (`clave`),
  KEY `updated_by` (`updated_by`),
  KEY `idx_clave` (`clave`),
  KEY `idx_categoria` (`categoria`),
  CONSTRAINT `configuracion_ibfk_1` FOREIGN KEY (`updated_by`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Configuraciones del sistema';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuracion`
--

LOCK TABLES `configuracion` WRITE;
/*!40000 ALTER TABLE `configuracion` DISABLE KEYS */;
/*!40000 ALTER TABLE `configuracion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuracion_codigos`
--

DROP TABLE IF EXISTS `configuracion_codigos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configuracion_codigos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `categoria` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `prefijo` varchar(5) COLLATE utf8mb4_general_ci NOT NULL,
  `ultimo_numero` int DEFAULT '0',
  `longitud_numero` int DEFAULT '3',
  PRIMARY KEY (`id`),
  UNIQUE KEY `categoria` (`categoria`),
  KEY `idx_categoria` (`categoria`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuracion_codigos`
--

LOCK TABLES `configuracion_codigos` WRITE;
/*!40000 ALTER TABLE `configuracion_codigos` DISABLE KEYS */;
INSERT INTO `configuracion_codigos` VALUES (1,'ANALGESICOS','ANA',0,3),(2,'ANTIBIOTICOS','ANT',0,3),(3,'ANTIINFLAMATORIOS','AIF',0,3),(4,'VITAMINAS','VIT',0,3),(5,'ANTIACIDOS','AAC',0,3),(6,'ANTIHISTAMINICOS','AHT',0,3),(7,'ANTIHIPERTENSIVOS','AFP',0,3),(8,'DIABETES','DIA',0,3),(9,'RESPIRATORIO','RES',0,3),(10,'DERMATOLOGIA','DER',0,3),(11,'HIGIENE','HIG',0,3),(12,'OTROS','OTR',0,3);
/*!40000 ALTER TABLE `configuracion_codigos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_pago_venta`
--

DROP TABLE IF EXISTS `detalle_pago_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_pago_venta` (
  `id` int NOT NULL AUTO_INCREMENT,
  `venta_id` int NOT NULL,
  `tipo_pago` enum('EFECTIVO','TARJETA','TRANSFERENCIA') COLLATE utf8mb4_general_ci NOT NULL,
  `monto` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `venta_id` (`venta_id`),
  CONSTRAINT `detalle_pago_venta_ibfk_1` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_pago_venta`
--

LOCK TABLES `detalle_pago_venta` WRITE;
/*!40000 ALTER TABLE `detalle_pago_venta` DISABLE KEYS */;
INSERT INTO `detalle_pago_venta` VALUES (1,2,'EFECTIVO',3.50),(2,2,'TARJETA',2.40),(3,3,'EFECTIVO',10.00),(4,3,'TARJETA',5.00),(5,3,'TRANSFERENCIA',5.65);
/*!40000 ALTER TABLE `detalle_pago_venta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_ventas`
--

DROP TABLE IF EXISTS `detalle_ventas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_ventas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `venta_id` int NOT NULL,
  `producto_id` int NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `descuento` decimal(10,2) DEFAULT '0.00',
  `subtotal` decimal(10,2) NOT NULL,
  `lote` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `venta_id` (`venta_id`),
  KEY `producto_id` (`producto_id`),
  CONSTRAINT `detalle_ventas_ibfk_1` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `detalle_ventas_ibfk_2` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_ventas`
--

LOCK TABLES `detalle_ventas` WRITE;
/*!40000 ALTER TABLE `detalle_ventas` DISABLE KEYS */;
INSERT INTO `detalle_ventas` VALUES (1,1,22,1,8.50,0.00,8.50,NULL,NULL),(2,2,21,1,5.00,0.00,5.00,NULL,NULL),(3,3,49,1,12.00,0.00,12.00,NULL,NULL),(4,3,26,1,5.50,0.00,5.50,NULL,NULL),(5,4,131,1,11.00,0.00,11.00,NULL,NULL),(6,6,111,1,5.00,0.00,5.00,NULL,NULL),(7,7,99,1,3.00,0.00,3.00,NULL,NULL),(8,8,50,1,4.00,0.00,4.00,NULL,NULL),(9,9,80,1,6.00,0.00,6.00,NULL,NULL),(10,10,79,1,11.00,0.00,11.00,NULL,NULL);
/*!40000 ALTER TABLE `detalle_ventas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto_historial_cambio`
--

DROP TABLE IF EXISTS `producto_historial_cambio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto_historial_cambio` (
  `id` int NOT NULL AUTO_INCREMENT,
  `producto_id` int NOT NULL,
  `campo_modificado` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `valor_anterior` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `valor_nuevo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `producto_id` (`producto_id`),
  CONSTRAINT `producto_historial_cambio_ibfk_1` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto_historial_cambio`
--

LOCK TABLES `producto_historial_cambio` WRITE;
/*!40000 ALTER TABLE `producto_historial_cambio` DISABLE KEYS */;
INSERT INTO `producto_historial_cambio` VALUES (1,21,'stock_actual','120','8','Jesus Administrador','2025-11-13 17:07:54'),(2,21,'stock_actual','8','5','Jesus Administrador','2025-11-13 17:09:03'),(3,22,'stock_actual','90','8','Jesus Administrador','2025-11-13 17:20:55'),(4,23,'stock_actual','110','5','Jesus Administrador','2025-11-13 17:26:41'),(5,21,'nombre','Omeprazol 20mg','Omeprazol 2mg','Vendedor Loza','2025-11-15 01:17:02'),(6,21,'stock_actual','4','2','jesus','2025-11-17 20:19:01'),(7,99,'stock_actual','199','198','jesus','2025-11-17 20:26:34'),(8,21,'stock_actual','4','2','jesus','2025-11-17 20:40:09'),(9,130,'stock_actual','80','78','jesus','2025-11-17 20:44:26'),(10,130,'stock_actual','78','77','jesus','2025-11-17 20:47:48'),(11,49,'stock_actual','80','77','jesus','2025-11-17 20:52:01'),(12,99,'stock_actual','198','197','jesus','2025-11-17 20:54:18'),(13,99,'fecha_vencimiento','2027-09-30','2025-11-30','Jesus Administrador','2025-11-17 23:28:46'),(14,22,'stock_actual','8','80','Jesus Administrador','2025-11-17 23:29:20'),(15,21,'stock_actual','1','10','Jesus Administrador','2025-11-17 23:29:30'),(16,21,'stock_actual','10','15','Jesus Administrador','2025-11-17 23:29:38'),(22,99,'stock_actual','195','194','jesus','2025-11-17 23:59:17'),(23,80,'stock_actual','107','106','jesus','2025-11-18 00:07:26'),(24,129,'stock_actual','98','97','jesus','2025-11-18 00:07:45'),(25,80,'stock_actual','106','105','jesus','2025-11-18 00:07:57'),(26,21,'nombre','Omeprazol 2mg','Omeprazol 2mgA','Jesus Administrador','2025-11-18 00:33:34'),(27,21,'nombre','Omeprazol 2mgA','Omeprazol 2mg','Jesus Administrador','2025-11-18 00:34:56'),(28,49,'stock_actual','77','8','Jesus Administrador','2025-11-18 01:47:48'),(29,130,'stock_actual','77','76','jesus','2025-11-18 03:04:55'),(30,21,'stock_actual','15','10','jesus','2025-11-18 03:05:35'),(31,111,'stock_actual','128','123','jesus','2025-11-18 03:14:18'),(32,52,'stock_actual','105','100','jesus','2025-11-18 03:14:18'),(33,21,'nombre','Omeprazol 2mg','Omeprazol 2mgr','Jesuss Lozas','2025-11-18 04:25:10'),(34,26,'stock_actual','110','109','jesus','2025-11-18 18:58:10'),(35,82,'stock_actual','68','8','Jesus Lozas','2025-11-18 19:29:26'),(36,126,'stock_actual','65','2','Jesus Lozas','2025-11-18 19:29:48'),(37,125,'proveedor_id','17','34','Jesus Lozas','2025-11-18 19:58:41'),(38,179,'proveedor_id','50','31','Jesus Loza','2025-11-18 20:30:34'),(39,80,'stock_actual','105','104','jesus','2025-11-18 23:54:02'),(40,79,'stock_actual','74','73','jesus','2025-11-18 23:54:02'),(41,22,'stock_actual','80','8','Jesus Loza','2025-11-19 17:44:19'),(42,50,'stock_actual','148','147','jesus','2025-11-19 19:35:53'),(43,129,'stock_actual','96','95','jesus','2025-11-19 19:35:53'),(44,158,'stock_actual','88','87','jesus','2025-11-20 21:22:51'),(45,111,'stock_actual','122','121','jesus','2025-11-20 23:33:37'),(46,129,'stock_actual','95','93','jesus','2025-11-20 23:39:40'),(47,146,'stock_actual','79','78','jesus','2025-11-20 23:39:40'),(48,111,'stock_actual','121','120','jesus','2025-11-21 00:17:11'),(49,49,'stock_actual','7','6','jesus','2025-11-21 00:17:11'),(50,144,'stock_actual','105','104','jesus','2025-11-21 00:18:33'),(51,23,'stock_actual','1','0','jesus','2025-11-21 00:43:58'),(52,129,'stock_actual','91','90','jesus','2025-11-21 00:43:58'),(53,22,'stock_actual','7','6','jesus','2025-11-21 01:13:58'),(54,131,'stock_actual','120','119','María','2025-11-21 23:06:31');
/*!40000 ALTER TABLE `producto_historial_cambio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos`
--

DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `nombre` varchar(150) COLLATE utf8mb4_general_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_general_ci,
  `principio_activo` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `concentracion` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `forma_farmaceutica` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `precio_compra` decimal(10,2) NOT NULL,
  `precio_venta` decimal(10,2) NOT NULL,
  `margen_ganancia` decimal(5,2) GENERATED ALWAYS AS ((((`precio_venta` - `precio_compra`) / `precio_compra`) * 100)) STORED,
  `stock_actual` int DEFAULT '0',
  `stock_minimo` int DEFAULT '5',
  `stock_maximo` int DEFAULT '100',
  `proveedor_id` int DEFAULT NULL,
  `categoria` enum('ANALGESICOS','ANTIBIOTICOS','ANTIINFLAMATORIOS','VITAMINAS','ANTIACIDOS','ANTIHISTAMINICOS','ANTIHIPERTENSIVOS','DIABETES','RESPIRATORIO','DERMATOLOGIA','HIGIENE','OTROS') COLLATE utf8mb4_general_ci DEFAULT 'OTROS',
  `ubicacion` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `lote` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `requiere_receta` tinyint(1) DEFAULT '0',
  `es_controlado` tinyint(1) DEFAULT '0',
  `activo` tinyint(1) DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `laboratorio` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '',
  `fecha_fabricacion` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`),
  UNIQUE KEY `idx_codigo` (`codigo`),
  KEY `idx_nombre` (`nombre`),
  KEY `idx_stock` (`stock_actual`),
  KEY `idx_vencimiento` (`fecha_vencimiento`),
  KEY `idx_categoria` (`categoria`),
  KEY `idx_proveedor` (`proveedor_id`),
  KEY `idx_activo` (`activo`),
  KEY `idx_productos_categoria_activo` (`categoria`,`activo`),
  FULLTEXT KEY `idx_busqueda_texto` (`nombre`,`descripcion`,`principio_activo`),
  CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`proveedor_id`) REFERENCES `proveedores` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=180 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos`
--

LOCK TABLES `productos` WRITE;
/*!40000 ALTER TABLE `productos` DISABLE KEYS */;
INSERT INTO `productos` (`id`, `codigo`, `nombre`, `descripcion`, `principio_activo`, `concentracion`, `forma_farmaceutica`, `precio_compra`, `precio_venta`, `stock_actual`, `stock_minimo`, `stock_maximo`, `proveedor_id`, `categoria`, `ubicacion`, `lote`, `fecha_vencimiento`, `requiere_receta`, `es_controlado`, `activo`, `created_at`, `updated_at`, `laboratorio`, `fecha_fabricacion`) VALUES (1,'ANA001','Paracetamol 500mg','Analgésico y antipirético','Paracetamol','500mg','TABLETA',1.80,3.50,85,5,100,NULL,'ANALGESICOS','A1','LOT2025A01','2027-06-15',0,0,1,'2025-11-13 17:32:25','2025-11-16 22:27:43','Medrock','2025-01-10'),(2,'ANA002','Ibuprofeno 400mg','Antiinflamatorio no esteroide','Ibuprofeno','400mg','TABLETA',2.50,5.00,57,5,100,9,'ANALGESICOS','A2','LOT2025A02','2027-08-20',0,0,1,'2025-11-13 17:32:25','2025-11-20 17:20:16','Portugal','2025-02-05'),(3,'ANA003','Diclofenac 50mg','AINE para dolor intenso','Diclofenaco sódico','50mg','TABLETA',3.20,6.50,60,5,100,17,'ANALGESICOS','A3','LOT2025A03','2027-09-10',1,0,1,'2025-11-13 17:32:25','2025-11-16 22:27:43','Medifarma','2025-03-12'),(4,'ANA004','Tramadol 50mg','Analgésico opioide','Tramadol HCl','50mg','TABLETA',6.00,12.00,45,5,100,51,'ANALGESICOS','A4','LOT2025A04','2027-11-30',1,1,1,'2025-11-13 17:32:25','2025-11-16 22:27:43','El Sol EIRL','2025-04-18'),(5,'ANA005','Ketorolaco 10mg','AINE inyectable','Ketorolaco trometamol','10mg/ml','AMPOLLA',8.50,15.00,30,5,100,6,'ANALGESICOS','A5','LOT2025A05','2027-07-25',1,0,1,'2025-11-13 17:32:25','2025-11-16 22:27:43','Genfar','2025-02-28'),(6,'ANA006','Naproxeno 500mg','AINE de larga duración','Naproxeno sódico','500mg','TABLETA',4.00,8.00,55,5,100,79,'ANALGESICOS','A6','LOT2025A06','2027-10-05',0,0,1,'2025-11-13 17:32:25','2025-11-16 22:27:43','Bayer Andino','2025-03-20'),(7,'ANT001','Amoxicilina 500mg','Penicilina de amplio espectro','Amoxicilina','500mg','CAPSULA',7.50,12.00,90,5,100,5,'ANTIBIOTICOS','B1','LOT2025B01','2027-05-18',1,0,1,'2025-11-13 17:32:25','2025-11-16 22:27:43','Genfar','2025-01-08'),(8,'ANT002','Azitromicina 500mg','Macrólido x3 días','Azitromicina','500mg','TABLETA',11.00,18.00,75,5,100,60,'ANTIBIOTICOS','B2','LOT2025B02','2027-09-22',1,0,1,'2025-11-13 17:32:25','2025-11-16 22:27:43','Quifarma Norte','2025-03-15'),(9,'ANT003','Ciprofloxacino 500mg','Quinolona','Ciprofloxacino','500mg','TABLETA',9.00,15.00,65,5,100,55,'ANTIBIOTICOS','B3','LOT2025B03','2027-08-30',1,0,1,'2025-11-13 17:32:25','2025-11-16 22:27:43','MedGlobal','2025-02-20'),(10,'ANT004','Clindamicina 300mg','Lincosamida','Clindamicina','300mg','CAPSULA',13.00,22.00,50,5,100,10,'ANTIBIOTICOS','B4','LOT2025B04','2027-11-10',1,0,1,'2025-11-13 17:32:25','2025-11-16 22:27:43','Portugal','2025-04-05'),(11,'ANT005','Cefalexina 500mg','Cefalosporina 1ra gen','Cefalexina','500mg','CAPSULA',8.50,14.00,80,5,100,87,'ANTIBIOTICOS','B5','LOT2025B05','2027-10-15',1,0,1,'2025-11-13 17:58:16','2025-11-16 22:27:43','Sanofi Andino','2025-03-25'),(12,'ANT006','Doxiciclina 100mg','Tetraciclina','Doxiciclina','100mg','CAPSULA',10.00,16.50,60,5,100,23,'ANTIBIOTICOS','B6','LOT2025B06','2027-12-01',1,0,1,'2025-11-13 17:58:46','2025-11-16 22:27:43','Boticas y Salud','2025-05-10'),(13,'AIF001','Prednisona 5mg','Corticosteroide oral','Prednisona','5mg','TABLETA',6.00,10.00,70,5,100,NULL,'ANTIINFLAMATORIOS','C1','LOT2025C01','2027-07-20',1,0,1,'2025-11-13 17:59:25','2025-11-16 22:27:43','San Miguel','2025-02-15'),(14,'AIF002','Dexametasona 4mg','Corticosteroide inyectable','Dexametasona','4mg/ml','AMPOLLA',7.50,13.00,40,5,100,59,'ANTIINFLAMATORIOS','C2','LOT2025C02','2027-09-25',1,0,1,'2025-11-13 17:59:25','2025-11-16 22:27:43','Vitalis','2025-03-18'),(15,'AIF003','Meloxicam 15mg','AINE selectivo','Meloxicam','15mg','TABLETA',5.50,9.50,55,5,100,32,'ANTIINFLAMATORIOS','C3','LOT2025C03','2027-11-05',1,0,1,'2025-11-13 17:59:44','2025-11-16 22:27:43','Bayer Perú','2025-04-22'),(16,'AIF004','Hidrocortisona crema 1%','Corticosteroide tópico','Hidrocortisona','1%','CREMA',8.00,14.00,60,5,100,11,'ANTIINFLAMATORIOS','C4','LOT2025C04','2027-10-30',0,0,1,'2025-11-13 17:59:44','2025-11-16 22:27:43','Universal','2025-03-10'),(17,'VIT001','Complejo B x30','Vitaminas B1,B6,B12','Complejo B','100/100/1mg','TABLETA',9.00,15.00,95,5,100,56,'VITAMINAS','D1','LOT2025D01','2028-01-15',0,0,1,'2025-11-13 17:59:44','2025-11-16 22:27:43','Salud Total','2025-06-01'),(18,'VIT002','Vitamina C 1g x10','Ácido ascórbico efervescente','Vitamina C','1000mg','TABLETA',4.50,8.00,100,5,100,91,'VITAMINAS','D2','LOT2025D02','2027-12-20',0,0,1,'2025-11-13 17:59:44','2025-11-16 22:27:43','Merck Andino','2025-05-15'),(19,'VIT003','Vitamina D3 1000UI','Colecalciferol','Vitamina D3','1000UI','GOTAS',12.00,20.00,70,5,100,69,'VITAMINAS','D3','LOT2025D03','2028-03-10',0,0,1,'2025-11-13 17:59:44','2025-11-16 22:27:43','FarmaCenter','2025-07-08'),(20,'VIT004','Calcio + Vit D x30','Citrato de calcio','Calcio 500mg + D 400UI','500mg + 400UI','TABLETA',10.50,18.00,80,5,100,33,'VITAMINAS','D4','LOT2025D04','2028-02-25',0,0,1,'2025-11-13 18:01:45','2025-11-16 22:27:43','FarmaCenter','2025-06-20'),(21,'AAC001','Omeprazol 2mgr','Inhibidor de bomba de protones','Omeprazol','20mg','CAPSULA',2.50,5.00,15,10,200,45,'ANTIACIDOS','E1','LOT2025E01','2027-11-15',0,0,1,'2025-11-13 18:11:17','2025-11-22 04:41:29','Medifarma','2025-04-10'),(22,'AAC002','Pantoprazol 40mg','IBP para úlcera','Pantoprazol','40mg','TABLETA',4.00,8.50,6,10,150,12,'ANTIACIDOS','E2','LOT2025E02','2027-12-20',1,0,1,'2025-11-13 18:11:17','2025-11-21 06:13:58','Genfar','2025-05-05'),(23,'AAC003','Ranitidina 150mg','Bloqueador H2','Ranitidina','150mg','TABLETA',1.80,3.50,0,10,200,78,'ANTIACIDOS','E3','LOT2025E03','2027-10-30',0,0,1,'2025-11-13 18:11:17','2025-11-21 05:43:58','Portugal','2025-03-18'),(24,'AHT001','Loratadina 10mg','Antihistamínico no sedante','Loratadina','10mg','TABLETA',1.20,2.80,150,10,300,34,'ANTIHISTAMINICOS','F1','LOT2025F01','2028-01-10',0,0,1,'2025-11-13 18:11:17','2025-11-16 22:27:43','Bayer Andino','2025-06-12'),(25,'AHT002','Cetirizina 10mg','Antihistamínico 2da gen','Cetirizina','10mg','TABLETA',1.50,3.20,129,10,250,56,'ANTIHISTAMINICOS','F2','LOT2025F02','2027-12-05',0,0,1,'2025-11-13 18:11:17','2025-11-20 16:12:59','Quifarma Norte','2025-05-20'),(26,'AHP001','Enalapril 10mg','IECAS para hipertensión','Enalapril','10mg','TABLETA',2.80,5.50,106,10,200,78,'ANTIHIPERTENSIVOS','K1','LOT2025K01','2027-09-25',1,0,1,'2025-11-13 18:11:17','2025-11-21 07:21:57','Genfar','2025-02-28'),(27,'AHP002','Losartán 50mg','ARA II','Losartán potásico','50mg','TABLETA',3.50,7.00,92,10,180,56,'ANTIHIPERTENSIVOS','K2','LOT2025K02','2027-10-20',1,0,1,'2025-11-13 18:11:17','2025-11-21 04:43:09','Medifarma','2025-03-10'),(28,'DIA001','Metformina 850mg','Antidiabético oral','Metformina','850mg','TABLETA',1.90,3.80,130,10,250,89,'DIABETES','M1','LOT2025M01','2027-11-15',1,0,1,'2025-11-13 18:11:17','2025-11-16 22:27:43','Vitalis','2025-04-22'),(29,'DIA002','Glibenclamida 5mg','Sulfonilurea','Glibenclamida','5mg','TABLETA',2.10,4.20,120,10,220,78,'DIABETES','M2','LOT2025M02','2027-11-05',1,0,1,'2025-11-13 18:11:17','2025-11-16 22:27:43','Genfar','2025-04-10'),(30,'DER001','Ketoconazol crema 2%','Antifúngico tópico','Ketoconazol','2%','CREMA',6.00,12.00,80,5,150,67,'DERMATOLOGIA','H2','LOT2025H02','2027-12-15',0,0,1,'2025-11-13 18:11:17','2025-11-16 22:27:43','Universal','2025-05-22'),(31,'DER002','Hidrocortisona crema 1%','Corticosteroide tópico','Hidrocortisona','1%','CREMA',5.50,11.00,90,5,160,11,'DERMATOLOGIA','H3','LOT2025H03','2027-11-25',0,0,1,'2025-11-13 18:11:17','2025-11-16 22:27:43','Universal','2025-04-18'),(32,'RES001','Salbutamol inhalador','Broncodilatador','Salbutamol','100mcg/dosis','OTRO',15.00,28.00,60,5,100,23,'RESPIRATORIO','P1','LOT2025P01','2027-10-15',1,0,1,'2025-11-13 18:11:17','2025-11-16 22:27:43','Sanofi Andino','2025-03-20'),(33,'HIG001','Jabón antibacteriano','Higiene personal','Triclosán','0.3%','OTRO',3.00,6.50,200,20,400,91,'HIGIENE','Q1','LOT2025Q01','2027-12-10',0,0,1,'2025-11-13 18:11:17','2025-11-16 22:27:43','FarmaCenter','2025-06-05'),(34,'OTR001','Suero fisiológico 500ml','Solución salina','Cloruro de sodio','0.9%','OTRO',4.50,9.00,150,10,300,69,'OTROS','R1','LOT2025R01','2028-01-15',0,0,1,'2025-11-13 18:11:17','2025-11-16 22:27:43','FarmaCenter','2025-07-10'),(35,'AIF005','Ibuprofeno gel 5%','Antiinflamatorio tópico','Ibuprofeno','5%','CREMA',8.00,15.00,75,5,130,32,'ANTIINFLAMATORIOS','C5','LOT2025C05','2027-12-01',0,0,1,'2025-11-13 18:11:17','2025-11-16 22:27:43','Bayer Perú','2025-05-15'),(36,'VIT006','Vitamina E 400UI','Antioxidante','Tocoferol','400UI','CAPSULA',11.00,20.00,85,5,150,6,'VITAMINAS','D6','LOT2025D06','2028-01-20',0,0,1,'2025-11-13 18:11:17','2025-11-16 22:27:43','Genfar','2025-06-10'),(37,'ANT007','Levofloxacino 500mg','Quinolona','Levofloxacino','500mg','TABLETA',12.00,22.00,65,5,120,10,'ANTIBIOTICOS','B7','LOT2025B07','2027-10-10',1,0,1,'2025-11-13 18:11:17','2025-11-16 22:27:43','Portugal','2025-03-05'),(38,'ANA007','Tramadol gotas 100mg/ml','Analgésico opioide','Tramadol','100mg/ml','GOTAS',14.00,28.00,55,5,100,51,'ANALGESICOS','A7','LOT2025A07','2027-09-15',1,1,1,'2025-11-13 18:11:17','2025-11-16 22:27:43','El Sol EIRL','2025-02-10'),(39,'HIG002','Alcohol 70% 1L','Desinfectante','Etanol','70%','OTRO',5.50,11.00,179,20,350,56,'HIGIENE','Q2','LOT2025Q02','2027-12-20',0,0,1,'2025-11-13 18:11:17','2025-11-20 17:16:55','Quifarma Norte','2025-06-18'),(40,'OTR002','Agua oxigenada 10 vol','Antiséptico','Peróxido de hidrógeno','3%','OTRO',2.00,4.50,200,20,400,91,'OTROS','R2','LOT2025R02','2028-01-05',0,0,1,'2025-11-13 18:11:17','2025-11-16 22:27:43','FarmaCenter','2025-07-01'),(41,'ANA008','Diclofenac 50mg','AINE oral','Diclofenac sódico','50mg','TABLETA',3.20,6.50,110,5,200,32,'ANALGESICOS','A8','LOT2025A08','2027-11-15',1,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Bayer Perú','2025-04-10'),(42,'ANA009','Ketorolaco 10mg','AINE inyectable','Ketorolaco','10mg/ml','AMPOLLA',9.00,18.00,50,5,100,11,'ANALGESICOS','A9','LOT2025A09','2027-10-20',1,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Universal','2025-03-15'),(43,'ANT008','Azitromicina 500mg','Macrólido','Azitromicina','500mg','TABLETA',11.00,20.00,70,5,130,60,'ANTIBIOTICOS','B8','LOT2025B08','2027-09-30',1,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Quifarma Norte','2025-02-25'),(44,'ANT009','Ciprofloxacino 500mg','Quinolona','Ciprofloxacino','500mg','TABLETA',7.50,14.00,85,5,150,87,'ANTIBIOTICOS','B9','LOT2025B09','2027-11-10',1,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Sanofi Andino','2025-04-05'),(45,'AIF006','Celecoxib 200mg','COX-2 selectivo','Celecoxib','200mg','CAPSULA',13.00,25.00,60,5,110,17,'ANTIINFLAMATORIOS','C6','LOT2025C06','2027-12-05',1,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Medifarma','2025-05-18'),(46,'AIF007','Meloxicam 15mg','AINE preferencial','Meloxicam','15mg','TABLETA',5.80,11.00,75,5,140,32,'ANTIINFLAMATORIOS','C7','LOT2025C07','2027-11-20',1,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Bayer Perú','2025-04-22'),(47,'VIT007','Complejo B forte','Vitaminas B1,B6,B12','Complejo B','100/100/1mg','TABLETA',8.50,16.00,90,5,160,56,'VITAMINAS','D7','LOT2025D07','2028-01-15',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Salud Total','2025-06-01'),(48,'VIT008','Vitamina C 500mg','Ácido ascórbico','Vitamina C','500mg','TABLETA',3.20,6.50,120,5,200,91,'VITAMINAS','D8','LOT2025D08','2027-12-20',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Merck Andino','2025-05-15'),(49,'AAC004','Esomeprazol 40mg','IBP de larga duración','Esomeprazol','40mg','CAPSULA',6.00,12.00,4,10,150,45,'ANTIACIDOS','E4','LOT2025E04','2027-11-30',1,0,1,'2025-11-13 18:13:36','2025-11-21 07:21:57','Medifarma','2025-04-25'),(50,'AAC005','Aluminio + Magnesio','Antiácido masticable','Hidróxido de Al/Mg','200/200mg','TABLETA',2.00,4.00,144,10,300,12,'ANTIACIDOS','E5','LOT2025E05','2027-10-15',0,0,1,'2025-11-13 18:13:36','2025-11-22 04:51:48','Genfar','2025-03-20'),(51,'AHT003','Desloratadina 5mg','Antihistamínico 3ra gen','Desloratadina','5mg','TABLETA',2.50,5.00,130,10,250,34,'ANTIHISTAMINICOS','F3','LOT2025F03','2028-01-05',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Bayer Andino','2025-06-10'),(52,'AHP003','Amlodipino 5mg','Bloqueador de calcio','Amlodipino','5mg','TABLETA',3.20,6.50,99,10,200,78,'ANTIHIPERTENSIVOS','K3','LOT2025K03','2027-09-20',1,0,1,'2025-11-13 18:13:36','2025-11-21 05:32:10','Genfar','2025-02-15'),(53,'DIA003','Insulina glargina 100UI/ml','Insulina basal','Insulina glargina','100UI/ml','AMPOLLA',45.00,90.00,30,5,60,89,'DIABETES','M3','LOT2025M03','2027-08-30',1,1,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Vitalis','2025-01-20'),(54,'DER003','Clotrimazol crema 1%','Antifúngico','Clotrimazol','1%','CREMA',4.50,9.00,95,5,180,67,'DERMATOLOGIA','H4','LOT2025H04','2027-12-10',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Universal','2025-05-25'),(55,'RES002','Budesonida inhalador','Corticosteroide','Budesonida','200mcg/dosis','OTRO',28.00,55.00,45,5,90,23,'RESPIRATORIO','P2','LOT2025P02','2027-11-15',1,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Sanofi Andino','2025-04-10'),(56,'HIG003','Toallas húmedas antibacteriales','Higiene','Alcohol + Clorhexidina','0.2%','OTRO',4.00,8.00,200,20,400,91,'HIGIENE','Q3','LOT2025Q03','2027-12-25',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','FarmaCenter','2025-06-20'),(57,'OTR003','Guantes quirúrgicos estériles','Material médico','Látex','Talla 7.5','OTRO',1.50,3.00,300,50,600,69,'OTROS','R3','LOT2025R03','2028-02-10',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','FarmaCenter','2025-07-15'),(58,'ANA010','Paracetamol gotas 100mg/ml','Analgésico pediátrico','Paracetamol','100mg/ml','GOTAS',5.00,10.00,100,5,180,51,'ANALGESICOS','A10','LOT2025A10','2027-10-25',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','El Sol EIRL','2025-03-18'),(59,'VIT009','Calcio + Vitamina D3','Suplemento óseo','Calcio + Colecalciferol','500mg + 400UI','TABLETA',10.50,19.00,80,5,150,33,'VITAMINAS','D9','LOT2025D09','2028-02-25',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','FarmaCenter','2025-06-20'),(60,'DER004','Miconazol polvo 2%','Antifúngico','Miconazol','2%','OTRO',7.00,14.00,70,5,130,67,'DERMATOLOGIA','H5','LOT2025H05','2027-11-30',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Universal','2025-05-10'),(61,'RES003','Montelukast 10mg','Antileucotrieno','Montelukast','10mg','TABLETA',12.00,24.00,60,5,110,23,'RESPIRATORIO','P3','LOT2025P03','2027-10-30',1,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Sanofi Andino','2025-03-25'),(62,'HIG004','Gel antibacterial 500ml','Desinfectante manos','Alcohol etílico','70%','OTRO',6.50,13.00,180,20,350,91,'HIGIENE','Q4','LOT2025Q04','2027-12-15',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','FarmaCenter','2025-06-15'),(63,'OTR004','Jeringas 5ml','Material desechable','Plástico','5ml','OTRO',0.80,1.60,500,100,1000,69,'OTROS','R4','LOT2025R04','2028-03-10',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','FarmaCenter','2025-07-20'),(64,'ANA011','Naproxeno 550mg','AINE de acción prolongada','Naproxeno','550mg','TABLETA',6.00,12.00,90,5,160,32,'ANALGESICOS','A11','LOT2025A11','2027-11-05',1,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Bayer Perú','2025-04-15'),(65,'VIT010','Multivitamínico adultos','Suplemento diario','Vitaminas + Minerales','Varios','TABLETA',15.00,28.00,70,5,130,6,'VITAMINAS','D10','LOT2025D10','2028-02-15',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Genfar','2025-06-25'),(66,'DER005','Ácido salicílico 2%','Queratólico','Ácido salicílico','2%','CREMA',7.50,15.00,75,5,140,67,'DERMATOLOGIA','H6','LOT2025H06','2027-12-20',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Universal','2025-05-30'),(67,'RES004','Teofilina 300mg','Broncodilatador','Teofilina','300mg','TABLETA',8.00,16.00,50,5,100,23,'RESPIRATORIO','P4','LOT2025P04','2027-11-10',1,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','Sanofi Andino','2025-04-05'),(68,'HIG005','Mascarillas quirúrgicas x50','Protección','Tela no tejida','3 capas','OTRO',10.00,20.00,200,50,500,91,'HIGIENE','Q5','LOT2025Q05','2028-01-20',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','FarmaCenter','2025-07-01'),(69,'OTR005','Termómetro digital','Medición temperatura','Electrónico','Digital','OTRO',12.00,25.00,100,10,200,69,'OTROS','R5','LOT2025R05','2029-01-01',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','FarmaCenter','2025-07-25'),(70,'ANA012','Lidocaína spray 10%','Anestésico tópico','Lidocaína','10%','OTRO',18.00,35.00,40,5,80,51,'ANALGESICOS','A12','LOT2025A12','2027-12-15',0,0,1,'2025-11-13 18:13:36','2025-11-16 22:27:43','El Sol EIRL','2025-06-05'),(71,'ANA013','Aspirina 500mg','Antiagregante y analgésico','Ácido acetilsalicílico','500mg','TABLETA',2.00,4.50,150,10,300,32,'ANALGESICOS','A13','LOT2025A13','2027-11-20',0,0,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','Bayer Perú','2025-04-18'),(72,'ANA014','Metamizol 500mg/ml','Analgésico potente','Metamizol sódico','500mg/ml','AMPOLLA',8.50,17.00,60,5,120,11,'ANALGESICOS','A14','LOT2025A14','2027-10-15',1,0,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','Universal','2025-03-25'),(73,'ANT010','Amoxicilina 1g','Antibiótico penicilina','Amoxicilina','1000mg','TABLETA',9.00,18.00,80,5,150,87,'ANTIBIOTICOS','B10','LOT2025B10','2027-12-01',1,0,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','Sanofi Andino','2025-05-10'),(74,'ANT011','Clindamicina 300mg','Lincosamida','Clindamicina','300mg','CAPSULA',10.50,20.00,70,5,130,60,'ANTIBIOTICOS','B11','LOT2025B11','2027-11-15',1,0,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','Quifarma Norte','2025-04-20'),(75,'AIF008','Piroxicam 20mg','AINE oxicam','Piroxicam','20mg','CAPSULA',7.00,14.00,65,5,120,17,'ANTIINFLAMATORIOS','C8','LOT2025C08','2027-10-25',1,0,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','Medifarma','2025-03-30'),(76,'AIF009','Dexametasona 0.5mg','Corticosteroide oral','Dexametasona','0.5mg','TABLETA',4.50,9.00,90,5,160,32,'ANTIINFLAMATORIOS','C9','LOT2025C09','2027-12-10',1,0,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','Bayer Perú','2025-05-05'),(77,'VIT011','Vitamina A 5000UI','Retinol','Vitamina A','5000UI','CAPSULA',9.00,17.00,85,5,150,56,'VITAMINAS','D11','LOT2025D11','2028-01-10',0,0,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','Salud Total','2025-06-15'),(78,'VIT012','Zinc 50mg','Suplemento inmunológico','Zinc gluconato','50mg','TABLETA',6.50,13.00,100,5,180,91,'VITAMINAS','D12','LOT2025D12','2027-12-20',0,0,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','Merck Andino','2025-05-20'),(79,'AAC006','Lansoprazol 30mg','IBP','Lansoprazol','30mg','CAPSULA',5.50,11.00,71,10,140,45,'ANTIACIDOS','E6','LOT2025E06','2027-11-05',1,0,1,'2025-11-13 18:16:00','2025-11-22 05:11:44','Medifarma','2025-04-15'),(80,'AAC007','Domperidona 10mg','Procinético','Domperidona','10mg','TABLETA',3.00,6.00,102,10,200,12,'ANTIACIDOS','E7','LOT2025E07','2027-10-20',0,0,1,'2025-11-13 18:16:00','2025-11-22 04:52:21','Genfar','2025-03-25'),(81,'AHT004','Fexofenadina 180mg','Antihistamínico','Fexofenadina','180mg','TABLETA',4.50,9.00,116,10,220,34,'ANTIHISTAMINICOS','F4','LOT2025F04','2028-01-15',0,0,1,'2025-11-13 18:16:00','2025-11-18 08:19:16','Bayer Andino','2025-06-20'),(82,'AHP004','Valsartán 160mg','ARA II','Valsartán','160mg','TABLETA',8.00,16.00,67,10,130,78,'ANTIHIPERTENSIVOS','K4','LOT2025K04','2027-09-30',1,0,1,'2025-11-13 18:16:00','2025-11-19 22:29:05','Genfar','2025-02-20'),(83,'DIA004','Sitagliptina 100mg','Inhibidor DPP-4','Sitagliptina','100mg','TABLETA',25.00,50.00,40,5,80,89,'DIABETES','M4','LOT2025M04','2027-08-25',1,0,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','Vitalis','2025-01-15'),(84,'DER006','Betametasona crema 0.1%','Corticosteroide','Betametasona','0.1%','CREMA',6.00,12.00,85,5,150,67,'DERMATOLOGIA','H7','LOT2025H07','2027-12-15',0,0,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','Universal','2025-05-25'),(85,'RES005','Ipratropio inhalador','Anticolinérgico','Ipratropio','20mcg/dosis','OTRO',20.00,40.00,50,5,100,23,'RESPIRATORIO','P5','LOT2025P05','2027-11-20',1,0,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','Sanofi Andino','2025-04-15'),(86,'HIG006','Enjuague bucal antiséptico','Higiene oral','Clorhexidina','0.12%','OTRO',7.50,15.00,150,20,300,91,'HIGIENE','Q6','LOT2025Q06','2027-12-30',0,0,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','FarmaCenter','2025-06-25'),(87,'OTR006','Apósitos estériles x10','Curación','Algodón + gasa','10x10cm','OTRO',3.00,6.00,200,50,500,69,'OTROS','R6','LOT2025R06','2028-02-20',0,0,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','FarmaCenter','2025-07-30'),(88,'ANA015','Buprenorfina parche 5mcg/h','Analgésico opioide','Buprenorfina','5mcg/h','OTRO',35.00,70.00,29,5,60,51,'ANALGESICOS','A15','LOT2025A15','2027-10-10',1,1,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','El Sol EIRL','2025-03-05'),(89,'VIT013','Omega 3 1000mg','Ácidos grasos','Aceite de pescado','1000mg','CAPSULA',18.00,35.00,70,5,130,6,'VITAMINAS','D13','LOT2025D13','2028-03-01',0,0,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','Genfar','2025-07-10'),(90,'DER007','Permetrina crema 5%','Escabicida','Permetrina','5%','CREMA',12.00,24.00,60,5,110,67,'DERMATOLOGIA','H8','LOT2025H08','2027-11-25',0,0,1,'2025-11-13 18:16:00','2025-11-16 22:27:43','Universal','2025-05-15'),(91,'RES006','Formoterol + Budesonida','Broncodilatador + corticoide','Formoterol + Budesonida','12/400mcg','OTRO',35.00,70.00,40,5,80,23,'RESPIRATORIO','P6','LOT2025P06','2027-10-25',1,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Sanofi Andino','2025-03-10'),(92,'HIG007','Pasta dental con flúor','Higiene bucal','Fluoruro de sodio','0.24%','OTRO',4.50,9.00,180,20,350,91,'HIGIENE','Q7','LOT2025Q07','2027-12-10',0,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','FarmaCenter','2025-06-05'),(93,'OTR007','Vendas elásticas 10cm','Soporte','Algodón + elastano','10cm x 4m','OTRO',3.50,7.00,250,50,500,69,'OTROS','R7','LOT2025R07','2028-03-15',0,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','FarmaCenter','2025-07-20'),(94,'ANA016','Fentanilo parche 25mcg/h','Analgésico opioide fuerte','Fentanilo','25mcg/h','OTRO',45.00,90.00,25,5,50,51,'ANALGESICOS','A16','LOT2025A16','2027-09-30',1,1,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','El Sol EIRL','2025-02-15'),(95,'VIT014','Magnesio 400mg','Relajante muscular','Magnesio citrato','400mg','TABLETA',7.00,14.00,90,5,160,6,'VITAMINAS','D14','LOT2025D14','2028-02-10',0,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Genfar','2025-06-30'),(96,'DER008','Terbinafina crema 1%','Antifúngico','Terbinafina','1%','CREMA',8.50,17.00,70,5,130,67,'DERMATOLOGIA','H9','LOT2025H09','2027-12-05',0,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Universal','2025-05-20'),(97,'ANT012','Doxiciclina 100mg','Tetraciclina','Doxiciclina','100mg','CAPSULA',6.00,12.00,85,5,150,87,'ANTIBIOTICOS','B12','LOT2025B12','2027-11-20',1,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Sanofi Andino','2025-04-10'),(98,'AIF010','Indometacina 25mg','AINE','Indometacina','25mg','CAPSULA',5.50,11.00,75,5,140,17,'ANTIINFLAMATORIOS','C10','LOT2025C10','2027-10-15',1,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Medifarma','2025-03-20'),(99,'AAC008','Bicarbonato de sodio 500mg','Antiácido','Bicarbonato de sodio','500mg','TABLETA',1.50,3.00,192,10,400,12,'ANTIACIDOS','E8','LOT2025E08','2025-11-30',0,0,1,'2025-11-13 18:18:20','2025-11-22 04:51:08','Genfar','2025-02-25'),(100,'AHT005','Bilastina 20mg','Antihistamínico','Bilastina','20mg','TABLETA',5.00,10.00,109,10,200,34,'ANTIHISTAMINICOS','F5','LOT2025F05','2028-02-01',0,0,1,'2025-11-13 18:18:20','2025-11-21 05:08:22','Bayer Andino','2025-07-05'),(101,'AHP005','Carvedilol 25mg','Betabloqueador','Carvedilol','25mg','TABLETA',7.50,15.00,80,10,150,78,'ANTIHIPERTENSIVOS','K5','LOT2025K05','2027-09-15',1,0,1,'2025-11-13 18:18:20','2025-11-18 05:08:16','Genfar','2025-02-10'),(102,'DIA005','Empagliflozina 10mg','Inhibidor SGLT2','Empagliflozina','10mg','TABLETA',30.00,60.00,35,5,70,89,'DIABETES','M5','LOT2025M05','2027-08-20',1,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Vitalis','2025-01-10'),(103,'DER009','Calamina loción','Antipruriginoso','Óxido de zinc + calamina','8% + 8%','OTRO',6.50,13.00,90,5,160,67,'DERMATOLOGIA','H10','LOT2025H10','2027-12-25',0,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Universal','2025-06-01'),(104,'HIG008','Shampoo anticaspa','Higiene capilar','Ketoconazol','2%','OTRO',12.00,24.00,120,20,250,91,'HIGIENE','Q8','LOT2025Q08','2027-11-30',0,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','FarmaCenter','2025-05-15'),(105,'OTR008','Preservativos x12','Anticonceptivo','Látex','Estándar','OTRO',8.00,16.00,300,50,600,69,'OTROS','R8','LOT2025R08','2028-04-01',0,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','FarmaCenter','2025-08-10'),(106,'ANA017','Oxicodona 10mg','Analgésico opioide','Oxicodona','10mg','TABLETA',28.00,55.00,30,5,60,51,'ANALGESICOS','A17','LOT2025A17','2027-09-25',1,1,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','El Sol EIRL','2025-02-20'),(107,'VIT015','Vitamina D3 2000UI','Salud ósea','Colecalciferol','2000UI','CAPSULA',10.00,20.00,100,5,180,6,'VITAMINAS','D15','LOT2025D15','2028-03-15',0,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Genfar','2025-07-15'),(108,'DER010','Ácido fusídico crema 2%','Antibiótico tópico','Ácido fusídico','2%','CREMA',9.00,18.00,70,5,130,67,'DERMATOLOGIA','H11','LOT2025H11','2027-12-10',1,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Universal','2025-05-25'),(109,'ANT013','Nitrofurantoína 100mg','Antibiótico urinario','Nitrofurantoína','100mg','CAPSULA',7.50,15.00,80,5,140,87,'ANTIBIOTICOS','B13','LOT2025B13','2027-11-05',1,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Sanofi Andino','2025-04-15'),(110,'AIF011','Prednisona 5mg','Corticosteroide','Prednisona','5mg','TABLETA',3.00,6.00,120,5,200,17,'ANTIINFLAMATORIOS','C11','LOT2025C11','2027-10-30',1,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Medifarma','2025-03-25'),(111,'AAC009','Metoclopramida 10mg','Antiemético','Metoclopramida','10mg','TABLETA',2.50,5.00,119,10,250,12,'ANTIACIDOS','E9','LOT2025E09','2027-09-20',1,0,1,'2025-11-13 18:18:20','2025-11-22 04:49:11','Genfar','2025-02-15'),(112,'AHT006','Levocetirizina 5mg','Antihistamínico','Levocetirizina','5mg','TABLETA',3.50,7.00,140,10,260,34,'ANTIHISTAMINICOS','F6','LOT2025F06','2028-02-20',0,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Bayer Andino','2025-07-20'),(113,'AHP006','Metoprolol 100mg','Betabloqueador','Metoprolol','100mg','TABLETA',6.00,12.00,90,10,160,78,'ANTIHIPERTENSIVOS','K6','LOT2025K06','2027-09-10',1,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Genfar','2025-02-05'),(114,'DIA006','Liraglutida inyectable','Análogo GLP-1','Liraglutida','6mg/ml','AMPOLLA',80.00,160.00,20,5,40,89,'DIABETES','M6','LOT2025M06','2027-07-30',1,1,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Vitalis','2025-01-05'),(115,'DER011','Povidona yodada solución','Antiséptico','Povidona yodada','10%','OTRO',5.00,10.00,150,10,300,67,'DERMATOLOGIA','H12','LOT2025H12','2027-12-20',0,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Universal','2025-06-10'),(116,'HIG009','Desodorante antibacterial','Higiene personal','Triclosán + alcohol','0.1% + 60%','OTRO',6.00,12.00,200,20,400,91,'HIGIENE','Q9','LOT2025Q09','2027-11-25',0,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','FarmaCenter','2025-05-20'),(117,'OTR009','Tiras reactivas glucosa x50','Diagnóstico','Enzimas','50 tiras','OTRO',25.00,50.00,100,20,200,69,'OTROS','R9','LOT2025R09','2028-05-01',0,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','FarmaCenter','2025-08-15'),(118,'ANA018','Gabapentina 300mg','Neuropático','Gabapentina','300mg','CAPSULA',12.00,24.00,70,5,130,51,'ANALGESICOS','A18','LOT2025A18','2027-10-20',1,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','El Sol EIRL','2025-03-15'),(119,'VIT016','Probióticos 10 billones','Salud intestinal','Lactobacillus + Bifidus','10 billones UFC','CAPSULA',20.00,40.00,80,5,150,6,'VITAMINAS','D16','LOT2025D16','2028-04-10',0,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Genfar','2025-07-25'),(120,'DER012','Ciclopirox olamina crema','Antifúngico','Ciclopirox olamina','1%','CREMA',11.00,22.00,60,5,110,67,'DERMATOLOGIA','H13','LOT2025H13','2027-12-15',0,0,1,'2025-11-13 18:18:20','2025-11-16 22:27:43','Universal','2025-06-05'),(121,'ANA019','Pregabalina 75mg','Neuropático','Pregabalina','75mg','CAPSULA',15.00,30.00,60,5,120,51,'ANALGESICOS','A19','LOT2025A19','2027-11-10',1,0,1,'2025-11-13 18:19:57','2025-11-16 22:27:43','El Sol EIRL','2025-04-05'),(122,'ANA020','Codeína 30mg','Analgésico opioide','Codeína','30mg','TABLETA',12.00,24.00,50,5,100,51,'ANALGESICOS','A20','LOT2025A20','2027-10-25',1,1,1,'2025-11-13 18:19:57','2025-11-16 22:27:43','El Sol EIRL','2025-03-15'),(123,'ANT014','Cefalexina 500mg','Cefalosporina 1ra gen','Cefalexina','500mg','CAPSULA',8.00,16.00,90,5,160,87,'ANTIBIOTICOS','B14','LOT2025B14','2027-12-15',1,0,1,'2025-11-13 18:19:57','2025-11-16 22:27:43','Sanofi Andino','2025-05-20'),(124,'ANT015','Ceftriaxona 1g','Cefalosporina 3ra gen','Ceftriaxona','1g','AMPOLLA',18.00,36.00,40,5,80,60,'ANTIBIOTICOS','B15','LOT2025B15','2027-11-30',1,0,1,'2025-11-13 18:19:57','2025-11-16 22:27:43','Quifarma Norte','2025-04-25'),(125,'AIF012','Etodolac 400mg','AINE','Etodolac','400mg','TABLETA',9.50,19.00,70,5,130,34,'ANTIINFLAMATORIOS','C12','LOT2025C12','2027-10-20',1,0,1,'2025-11-13 18:19:57','2025-11-19 00:58:42','Medifarma','2025-03-10'),(126,'AIF013','Nabumetona 500mg','AINE','Nabumetona','500mg','TABLETA',11.00,22.00,2,5,120,32,'ANTIINFLAMATORIOS','C13','LOT2025C13','2027-12-05',1,0,1,'2025-11-13 18:19:57','2025-11-19 00:29:49','Bayer Perú','2025-05-15'),(127,'VIT017','Hierro 100mg','Antianémico','Sulfato ferroso','100mg','TABLETA',4.00,8.00,110,5,200,56,'VITAMINAS','D17','LOT2025D17','2028-01-20',0,0,1,'2025-11-13 18:19:57','2025-11-16 22:27:43','Salud Total','2025-06-10'),(128,'VIT018','Ácido fólico 5mg','Suplemento','Ácido fólico','5mg','TABLETA',3.50,7.00,130,5,250,91,'VITAMINAS','D18','LOT2025D18','2027-12-25',0,0,1,'2025-11-13 18:19:57','2025-11-16 22:27:43','Merck Andino','2025-05-25'),(129,'AAC010','Famotidina 20mg','Bloqueador H2','Famotidina','20mg','TABLETA',3.00,6.00,90,10,180,45,'ANTIACIDOS','E10','LOT2025E10','2027-11-15',0,0,1,'2025-11-13 18:19:57','2025-11-21 05:43:58','Medifarma','2025-04-20'),(130,'AAC011','Sucralfato 1g','Protector gástrico','Sucralfato','1g','TABLETA',7.00,14.00,72,10,150,12,'ANTIACIDOS','E11','LOT2025E11','2027-10-30',1,0,1,'2025-11-13 18:19:57','2025-11-21 05:19:09','Genfar','2025-03-30'),(131,'AHT007','Rupatadina 10mg','Antihistamínico','Rupatadina','10mg','TABLETA',5.50,11.00,119,10,220,34,'ANTIHISTAMINICOS','F7','LOT2025F07','2028-03-01',0,0,1,'2025-11-13 18:23:24','2025-11-22 04:06:31','Bayer Andino','2025-07-25'),(132,'AHP007','Irbesartán 300mg','ARA II','Irbesartán','300mg','TABLETA',10.00,20.00,75,10,140,78,'ANTIHIPERTENSIVOS','K7','LOT2025K07','2027-09-05',1,0,1,'2025-11-13 18:23:24','2025-11-16 22:27:43','Genfar','2025-02-01'),(133,'DIA007','Dapagliflozina 10mg','Inhibidor SGLT2','Dapagliflozina','10mg','TABLETA',28.00,55.00,40,5,80,89,'DIABETES','M7','LOT2025M07','2027-08-15',1,0,1,'2025-11-13 18:23:24','2025-11-16 22:27:43','Vitalis','2025-01-01'),(134,'DER013','Fluocinolona crema 0.025%','Corticosteroide','Fluocinolona','0.025%','CREMA',7.00,14.00,80,5,150,67,'DERMATOLOGIA','H14','LOT2025H14','2027-12-30',0,0,1,'2025-11-13 18:23:24','2025-11-16 22:27:43','Universal','2025-06-15'),(135,'RES007','Tiotropio inhalador','Anticolinérgico','Tiotropio','18mcg/dosis','OTRO',45.00,90.00,35,5,70,23,'RESPIRATORIO','P7','LOT2025P07','2027-11-10',1,0,1,'2025-11-13 18:23:24','2025-11-16 22:27:43','Sanofi Andino','2025-04-05'),(136,'HIG010','Crema hidratante manos','Hidratación','Urea + glicerina','10% + 5%','CREMA',8.00,16.00,150,20,300,91,'HIGIENE','Q10','LOT2025Q10','2027-12-15',0,0,1,'2025-11-13 18:23:24','2025-11-16 22:27:43','FarmaCenter','2025-06-20'),(137,'OTR010','Bolsas para colostomía x10','Material médico','Plástico médico','50mm','OTRO',25.00,50.00,80,20,160,69,'OTROS','R10','LOT2025R10','2028-06-01',0,0,1,'2025-11-13 18:23:24','2025-11-16 22:27:43','FarmaCenter','2025-08-20'),(138,'ANA021','Duloxetina 60mg','Antidepresivo y dolor','Duloxetina','60mg','CAPSULA',22.00,44.00,50,5,100,51,'ANALGESICOS','A21','LOT2025A21','2027-10-15',1,0,1,'2025-11-13 18:23:25','2025-11-16 22:27:43','El Sol EIRL','2025-03-10'),(139,'VIT019','Coenzima Q10 100mg','Antioxidante','Ubiquinona','100mg','CAPSULA',30.00,60.00,60,5,120,6,'VITAMINAS','D19','LOT2025D19','2028-04-20',0,0,1,'2025-11-13 18:23:25','2025-11-16 22:27:43','Genfar','2025-07-30'),(140,'DER014','Econazol crema 1%','Antifúngico','Econazol','1%','CREMA',9.00,18.00,75,5,140,67,'DERMATOLOGIA','H15','LOT2025H15','2027-12-25',0,0,1,'2025-11-13 18:23:25','2025-11-16 22:27:43','Universal','2025-06-10'),(141,'ANT016','Vancomicina 500mg','Glicopéptido','Vancomicina','500mg','AMPOLLA',35.00,70.00,30,5,60,60,'ANTIBIOTICOS','B16','LOT2025B16','2027-11-20',1,0,1,'2025-11-13 18:25:44','2025-11-16 22:27:43','Quifarma Norte','2025-04-15'),(142,'AIF014','Tenoxicam 20mg','AINE oxicam','Tenoxicam','20mg','TABLETA',8.50,17.00,70,5,130,17,'ANTIINFLAMATORIOS','C14','LOT2025C14','2027-10-10',1,0,1,'2025-11-13 18:25:44','2025-11-16 22:27:43','Medifarma','2025-03-05'),(143,'VIT020','Biotina 5000mcg','Salud cabello','Biotina','5000mcg','TABLETA',12.00,24.00,90,5,160,56,'VITAMINAS','D20','LOT2025D20','2028-05-01',0,0,1,'2025-11-13 18:25:44','2025-11-16 22:27:43','Salud Total','2025-08-01'),(144,'AAC012','Cimetidina 400mg','Bloqueador H2','Cimetidina','400mg','TABLETA',4.00,8.00,104,10,200,45,'ANTIACIDOS','E12','LOT2025E12','2027-11-05',0,0,1,'2025-11-13 18:25:44','2025-11-21 05:18:33','Medifarma','2025-04-10'),(145,'AHT008','Ebastina 10mg','Antihistamínico','Ebastina','10mg','TABLETA',4.50,9.00,130,10,240,34,'ANTIHISTAMINICOS','F8','LOT2025F08','2028-03-15',0,0,1,'2025-11-13 18:25:44','2025-11-16 22:27:43','Bayer Andino','2025-08-05'),(146,'AHP008','Telmisartán 80mg','ARA II','Telmisartán','80mg','TABLETA',9.00,18.00,77,10,150,78,'ANTIHIPERTENSIVOS','K8','LOT2025K08','2027-09-01',1,0,1,'2025-11-13 18:25:44','2025-11-21 05:07:36','Genfar','2025-01-30'),(147,'DIA008','Semaglutida 0.5mg','Análogo GLP-1','Semaglutida','0.5mg','AMPOLLA',90.00,180.00,20,5,40,89,'DIABETES','M8','LOT2025M08','2027-07-25',1,1,1,'2025-11-13 18:25:44','2025-11-16 22:27:43','Vitalis','2024-12-25'),(148,'DER015','Mupirocina pomada 2%','Antibiótico tópico','Mupirocina','2%','CREMA',10.00,20.00,70,5,130,67,'DERMATOLOGIA','H16','LOT2025H16','2027-12-30',0,0,1,'2025-11-13 18:25:44','2025-11-16 22:27:43','Universal','2025-06-20'),(149,'RES008','Fluticasona nasal','Corticosteroide','Fluticasona','50mcg/dosis','OTRO',22.00,44.00,50,5,100,23,'RESPIRATORIO','P8','LOT2025P08','2027-11-15',0,0,1,'2025-11-13 18:25:44','2025-11-16 22:27:43','Sanofi Andino','2025-04-10'),(150,'HIG011','Protector solar FPS50','Fotoprotección','Filtros químicos','FPS50','CREMA',15.00,30.00,120,20,250,91,'HIGIENE','Q11','LOT2025Q11','2027-12-20',0,0,1,'2025-11-13 18:25:44','2025-11-16 22:27:43','FarmaCenter','2025-06-25'),(151,'OTR011','Gasas estériles x10','Curación','Algodón','10x10cm','OTRO',2.50,5.00,300,50,600,69,'OTROS','R11','LOT2025R11','2028-06-15',0,0,1,'2025-11-13 18:26:40','2025-11-16 22:27:43','FarmaCenter','2025-08-25'),(152,'ANA022','Morfina 10mg/ml','Analgésico opioide','Morfina','10mg/ml','AMPOLLA',50.00,100.00,20,5,40,51,'ANALGESICOS','A22','LOT2025A22','2027-09-20',1,1,1,'2025-11-13 18:26:40','2025-11-16 22:27:43','El Sol EIRL','2025-02-10'),(153,'VIT021','Melatonina 5mg','Regulador sueño','Melatonina','5mg','TABLETA',8.00,16.00,100,5,180,6,'VITAMINAS','D21','LOT2025D21','2028-05-10',0,0,1,'2025-11-13 18:26:40','2025-11-16 22:27:43','Genfar','2025-08-10'),(154,'DER016','Hidratante con urea 10%','Hidratación piel seca','Urea','10%','CREMA',6.00,12.00,90,5,160,67,'DERMATOLOGIA','H17','LOT2025H17','2027-12-15',0,0,1,'2025-11-13 18:26:40','2025-11-16 22:27:43','Universal','2025-06-05'),(155,'ANT017','Linezolid 600mg','Oxazolidinona','Linezolid','600mg','TABLETA',40.00,80.00,30,5,60,87,'ANTIBIOTICOS','B17','LOT2025B17','2027-11-25',1,0,1,'2025-11-13 18:26:40','2025-11-16 22:27:43','Sanofi Andino','2025-04-20'),(156,'AIF015','Flurbiprofeno 100mg','AINE','Flurbiprofeno','100mg','TABLETA',7.50,15.00,80,5,140,17,'ANTIINFLAMATORIOS','C15','LOT2025C15','2027-10-30',1,0,1,'2025-11-13 18:26:40','2025-11-16 22:27:43','Medifarma','2025-03-15'),(157,'VIT022','Selenio 200mcg','Antioxidante','Selenio','200mcg','TABLETA',9.00,18.00,110,5,200,56,'VITAMINAS','D22','LOT2025D22','2028-06-01',0,0,1,'2025-11-13 18:26:40','2025-11-16 22:27:43','Salud Total','2025-09-01'),(158,'AAC013','Ranitidina jarabe 150mg/10ml','Bloqueador H2','Ranitidina','150mg/10ml','OTRO',5.00,10.00,86,10,180,12,'ANTIACIDOS','E13','LOT2025E13','2027-10-15',0,0,1,'2025-11-13 18:26:40','2025-11-21 05:07:36','Genfar','2025-03-20'),(159,'AHT009','Azelastina nasal','Antihistamínico nasal','Azelastina','0.1%','OTRO',15.00,30.00,70,10,140,34,'ANTIHISTAMINICOS','F9','LOT2025F09','2028-03-20',0,0,1,'2025-11-13 18:26:40','2025-11-16 22:27:43','Bayer Andino','2025-08-15'),(160,'AHP009','Bisoprolol 5mg','Betabloqueador','Bisoprolol','5mg','TABLETA',6.50,13.00,99,10,180,78,'ANTIHIPERTENSIVOS','K9','LOT2025K09','2027-09-25',1,0,1,'2025-11-13 18:26:40','2025-11-18 02:02:45','Genfar','2025-02-15'),(161,'DIA009','Glimepirida 4mg','Sulfonilurea','Glimepirida','4mg','TABLETA',5.00,10.00,80,5,150,89,'DIABETES','M9','LOT2025M09','2027-08-10',1,0,1,'2025-11-13 18:26:40','2025-11-16 22:27:43','Vitalis','2025-01-05'),(162,'DER017','Clindamicina gel 1%','Antibiótico tópico','Clindamicina','1%','CREMA',12.00,24.00,60,5,120,67,'DERMATOLOGIA','H18','LOT2025H18','2027-12-20',0,0,1,'2025-11-13 18:26:40','2025-11-16 22:27:43','Universal','2025-06-10'),(163,'RES009','Salmeterol inhalador','Broncodilatador','Salmeterol','25mcg/dosis','OTRO',30.00,60.00,45,5,90,23,'RESPIRATORIO','P9','LOT2025P09','2027-11-30',1,0,1,'2025-11-13 18:26:40','2025-11-16 22:27:43','Sanofi Andino','2025-04-15'),(164,'HIG012','Cepillo dental blando','Higiene bucal','Nailon','Suave','OTRO',3.00,6.00,200,50,400,91,'HIGIENE','Q12','LOT2025Q12','2028-01-01',0,0,1,'2025-11-13 18:26:40','2025-11-16 22:27:43','FarmaCenter','2025-09-01'),(165,'OTR012','Sonda nasogástrica 12Fr','Material médico','Poliuretano','12Fr','OTRO',15.00,30.00,50,10,100,69,'OTROS','R12','LOT2025R12','2028-07-01',0,0,1,'2025-11-13 18:26:40','2025-11-16 22:27:43','FarmaCenter','2025-10-01'),(166,'ANA023','Tapentadol 50mg','Analgésico opioide','Tapentadol','50mg','TABLETA',20.00,40.00,40,5,80,51,'ANALGESICOS','A23','LOT2025A23','2027-10-10',1,1,1,'2025-11-13 18:26:41','2025-11-16 22:27:43','El Sol EIRL','2025-03-05'),(167,'VIT023','Curcumina 500mg','Antiinflamatorio natural','Curcumina','500mg','CAPSULA',25.00,50.00,70,5,130,6,'VITAMINAS','D23','LOT2025D23','2028-06-15',0,0,1,'2025-11-13 18:26:41','2025-11-16 22:27:43','Genfar','2025-09-15'),(168,'DER018','Hidrocortisona pomada 1%','Corticosteroide','Hidrocortisona','1%','CREMA',5.50,11.00,85,5,150,67,'DERMATOLOGIA','H19','LOT2025H19','2027-12-10',0,0,1,'2025-11-13 18:26:41','2025-11-16 22:27:43','Universal','2025-05-25'),(169,'ANT018','Meropenem 1g','Carbapenem','Meropenem','1g','AMPOLLA',60.00,120.00,25,5,50,87,'ANTIBIOTICOS','B18','LOT2025B18','2027-11-15',1,0,1,'2025-11-13 18:26:41','2025-11-16 22:27:43','Sanofi Andino','2025-04-10'),(170,'AIF016','Aceclofenaco 100mg','AINE','Aceclofenaco','100mg','TABLETA',6.00,12.00,90,5,160,17,'ANTIINFLAMATORIOS','C16','LOT2025C16','2027-10-25',1,0,1,'2025-11-13 18:26:41','2025-11-18 04:38:28','Medifarma','2025-03-20'),(171,'ANT019','Prueba','Prueba','Prueba','500','JARABE',2.00,3.00,23,10,100,24,'ANTIBIOTICOS','','','2026-11-13',0,0,1,'2025-11-14 00:58:28','2025-11-18 04:38:28','','2024-10-30'),(172,'AIF017','QWWEQEWWWEEWE','Prueba','Prueba','200','JARABE',2.00,5.00,20,10,100,4,'ANTIINFLAMATORIOS','','','2026-11-07',0,0,1,'2025-11-17 02:35:48','2025-11-18 04:38:28','','2025-11-06'),(176,'ANT020','SADADADASSDDSADSA','','','','JARABE',2.00,3.00,12,10,100,67,'ANTIBIOTICOS','','','2027-11-18',0,0,1,'2025-11-18 23:54:15','2025-11-18 23:54:14','',NULL),(177,'AHT010','QWERTYUIOP','','','','SUSPENSION',1.00,1.50,15,10,100,10,'ANTIHISTAMINICOS','','','2027-11-12',0,0,1,'2025-11-19 01:11:43','2025-11-19 01:11:43','',NULL),(178,'AIF018','POIUYTREWQ','','','','CREMA',2.00,3.00,15,10,100,15,'ANTIINFLAMATORIOS','','','2026-11-13',0,0,1,'2025-11-19 01:17:20','2025-11-19 01:17:20','',NULL),(179,'AHT011','QASWED','','','','CREMA',2.00,4.00,10,1,100,31,'ANTIHISTAMINICOS','','','2027-11-19',0,0,1,'2025-11-19 01:29:55','2025-11-19 01:30:35','',NULL);
/*!40000 ALTER TABLE `productos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proveedor_historial_cambio`
--

DROP TABLE IF EXISTS `proveedor_historial_cambio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedor_historial_cambio` (
  `id` int NOT NULL AUTO_INCREMENT,
  `proveedor_id` int NOT NULL,
  `campo_modificado` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `valor_anterior` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `valor_nuevo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `proveedor_id` (`proveedor_id`),
  CONSTRAINT `proveedor_historial_cambio_ibfk_1` FOREIGN KEY (`proveedor_id`) REFERENCES `proveedores` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proveedor_historial_cambio`
--

LOCK TABLES `proveedor_historial_cambio` WRITE;
/*!40000 ALTER TABLE `proveedor_historial_cambio` DISABLE KEYS */;
INSERT INTO `proveedor_historial_cambio` VALUES (10,139,'ruc',NULL,'20102563104','jesus','2025-11-17 23:16:04'),(11,139,'contacto',NULL,'Juan Luis','jesus','2025-11-17 23:16:12'),(12,139,'condiciones_pago','30 días','90 días','jesus','2025-11-17 23:16:18'),(13,139,'email',NULL,'lozayataco@gmail.com','jesus','2025-11-17 23:19:17'),(14,139,'email','lozayataco@gmail.com','lozayatacojesus@gmail.com','jesus','2025-11-17 23:21:25'),(15,20,'activo','true','false','jesus','2025-11-17 23:24:09'),(16,9,'activo','true','false','jesus','2025-11-17 23:24:17'),(17,8,'activo','true','false','jesus','2025-11-17 23:24:17'),(18,4,'razon_social','Distribuidora Norte SAC','Distribuidora Norte SACA','jesus','2025-11-18 00:27:32'),(19,4,'razon_social','Distribuidora Norte SACA','Distribuidora Norte SAC','jesus','2025-11-18 00:29:08'),(20,4,'razon_social','Distribuidora Norte SAC','Distribuidora Norte SACA','jesus','2025-11-18 00:29:54'),(21,4,'razon_social','Distribuidora Norte SACA','Distribuidora Norte SAC','jesus','2025-11-18 00:30:08'),(22,4,'email','norte@distribuidora.pe','fannyyataco031175@gmail.com','jesus','2025-11-18 19:13:43'),(23,4,'razon_social','Distribuidora Norte SAC','Distribuidora Norte SACA','jesus','2025-11-18 19:59:07'),(24,4,'razon_social','Distribuidora Norte SACA','Distribuidora Norte SAC','jesus','2025-11-18 19:59:35'),(25,31,'razon_social','Droguería Santa Rosa','Droguería Santa Rosas','jesus','2025-11-18 20:30:48'),(26,4,'ruc','20345678901','20345678909','jesus','2025-11-18 20:33:19'),(27,6,'razon_social','Químicos del Sur EIRL','Químicos del Sur EIRLA','jesus','2025-11-18 23:57:39'),(28,4,'razon_social','Distribuidora Norte SAC','Distribuidora Norte SACA','María','2025-11-19 17:17:50');
/*!40000 ALTER TABLE `proveedor_historial_cambio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proveedores`
--

DROP TABLE IF EXISTS `proveedores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedores` (
  `id` int NOT NULL AUTO_INCREMENT,
  `razon_social` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ruc` varchar(11) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contacto` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `direccion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `condiciones_pago` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `tipo_producto` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ruc` (`ruc`),
  UNIQUE KEY `ruc_2` (`ruc`),
  UNIQUE KEY `ruc_3` (`ruc`),
  KEY `idx_ruc` (`ruc`),
  KEY `idx_razon_social` (`razon_social`),
  KEY `idx_activo` (`activo`)
) ENGINE=InnoDB AUTO_INCREMENT=140 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Proveedores de medicamentos y productos';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proveedores`
--

LOCK TABLES `proveedores` WRITE;
/*!40000 ALTER TABLE `proveedores` DISABLE KEYS */;
INSERT INTO `proveedores` VALUES (4,'Distribuidora Norte SACA','20345678909','José Mendoza','944567890','fannyyataco031175@gmail.com','Av. España 234, Trujillo','30 días',1,'2025-11-13 17:01:39','Cobertura La Libertad','Insumos'),(5,'Laboratorio Genfar Perú','20678901234','Luis Vargas','933456789','info@genfar.pe','Calle Los Pinos 456, Surco, Lima','45 días',1,'2025-11-13 17:01:39','Genéricos de calidad','Medicamentos'),(6,'Químicos del Sur EIRLA','20234567890','Rosa Guzmán','955678901','ventas@quimicosur.pe','Av. Goyeneche 789, Arequipa','Contado',1,'2025-11-13 17:01:39','Reactivos para laboratorio','Insumos'),(7,'Boticas del Pueblo SAC','20156789012','Pedro Castro','01-3345678','compras@boticasdelpueblo.pe','Jr. Ayacucho 123, Cercado de Lima','15 días',1,'2025-11-13 17:01:39','Red de boticas propias','Medicamentos'),(8,'Importadora Farmacéutica Global','20478901234','Elena Díaz','922345678','import@globalpharma.pe','Av. Canadá 567, La Victoria, Lima','30 días',0,'2025-11-13 17:01:39','Marcas importadas','Medicamentos'),(9,'Laboratorios Portugal SAC','20589012345','Miguel Soto','911234567','ventas@portugal.pe','Calle Schell 890, Miraflores, Lima','30 días',0,'2025-11-13 17:01:39','Línea oncológica','Medicamentos'),(10,'Droguería Central del Perú','20367890123','Sofía Ramírez','01-5678901','central@drogueria.pe','Av. Abancay 456, Cercado de Lima','Contado',1,'2025-11-13 17:01:39','Mayorista','Medicamentos'),(11,'Farmacia Universal SAC','20189012345','Jorge Vega','966789012','contacto@farmuni.pe','Av. Brasil 1234, Magdalena, Lima','15 días',1,'2025-11-13 17:01:39','Dermocosmética y cuidado personal','Otros'),(12,'Provefarma del Norte','20690123456','Claudia Morales','977890123','norte@provefarma.pe','Jr. Bolívar 567, Chiclayo','30 días',1,'2025-11-13 17:01:39','Cobertura Lambayeque','Medicamentos'),(13,'Laboratorios Bagó Perú','20456789013','Ricardo León','01-6789012','ventas@bago.pe','Av. Aramburú 789, San Isidro, Lima','45 días',1,'2025-11-13 17:01:39','Línea cardiovascular','Medicamentos'),(14,'Distribuidora Andina SAC','20278901234','Patricia Herrera','988901234','andina@distribuidora.pe','Calle Mercaderes 234, Cusco','30 días',1,'2025-11-13 17:01:39','Cobertura sur','Insumos'),(15,'Farmacéutica del Oriente','20512345678','Fernando Ortiz','999012345','oriente@farma.pe','Av. Grau 567, Iquitos','Contado',1,'2025-11-13 17:01:39','Cobertura Loreto','Medicamentos'),(16,'Quifarma SAC','20134567890','Lucía Fernández','01-7890123','ventas@quifarma.pe','Jr. Unión 890, Cercado de Lima','15 días',1,'2025-11-13 17:01:39','Antibióticos','Medicamentos'),(17,'Medifarma Perú','20389012345','Andrés Salazar','977012345','contacto@medifarma.pe','Av. Primavera 123, Surco, Lima','30 días',1,'2025-11-13 17:01:39','Línea neurológica','Medicamentos'),(18,'Droguería del Valle EIRL','20623456789','Carmen Rojas','966123456','valle@drogueria.pe','Av. Los Héroes 456, San Juan de Lurigancho','Contado',1,'2025-11-13 17:01:39','Entrega gratuita en SJL','Medicamentos'),(19,'Laboratorios Roche Perú','20490123456','Diego Navarro','01-8901234','roche@proveedor.pe','Av. Víctor Andrés Belaúnde 123, San Isidro, Lima','60 días',1,'2025-11-13 17:01:39','Oncología y biotecnología','Medicamentos'),(20,'FarmaExpress SAC','20256789012','Valeria Paredes','955234567','express@farma.pe','Jr. de la Unión 789, Cercado de Lima','15 días',0,'2025-11-13 17:01:39','Entrega en 2 horas','Medicamentos'),(21,'Distribuidora Costa Verde','20567890134','Raúl Mendoza','944345678','costa@distribuidora.pe','Av. La Marina 890, Callao','30 días',1,'2025-11-13 17:01:39','Cobertura Callao','Material Médico'),(22,'Químicos Industriales del Perú','20178901234','Isabel Cruz','01-9012345','quimicos@industria.pe','Calle Los Rosales 567, Ate','45 días',1,'2025-11-13 17:01:39','Laboratorios clínicos','Insumos'),(23,'Boticas y Salud SAC','20345678912','Gonzalo Rivera','933456789','salud@boticas.pe','Av. Angamos 1234, Surquillo, Lima','Contado',1,'2025-11-13 17:01:39','Red de 50 locales','Otros'),(24,'Laboratorio Abbott Perú','20678901245','Mónica Silva','922567890','abbott@proveedor.pe','Av. República de Panamá 3456, Miraflores','60 días',1,'2025-11-13 17:01:39','Línea pediátrica','Medicamentos'),(25,'FarmaSur EIRL','20434567890','Julio Ramos','911678901','sur@farmasur.pe','Calle San Martín 234, Tacna','30 días',1,'2025-11-13 17:01:39','Cobertura Tacna','Medicamentos'),(26,'Droguería La Merced','20290123456','Gloria Tapia','01-2345678','merced@drogueria.pe','Jr. La Merced 567, Cercado de Lima','15 días',1,'2025-11-13 17:01:39','Desde 1980','Medicamentos'),(27,'Importadora Salud Global','20556789012','Esteban Quito','900123456','global@importsalud.pe','Av. Petit Thouars 789, Lince','45 días',1,'2025-11-13 17:01:39','Marcas europeas','Medicamentos'),(28,'Laboratorios Pfizer Perú','20167890123','Camila Ortiz','01-3456789','pfizer@proveedor.pe','Av. Camino Real 456, San Isidro','60 días',1,'2025-11-13 17:01:39','Vacunas y oncológicos','Medicamentos'),(29,'FarmaNorte Distribuidora','20378901234','Renato Flores','989012345','norte@farma.pe','Av. América Norte 123, Trujillo','30 días',1,'2025-11-13 17:01:39','Cobertura norte','Medicamentos'),(30,'Químicos del Pacífico','20690123467','Daniela Vera','978901234','pacifico@quimicos.pe','Calle Piérola 456, Piura','Contado',1,'2025-11-13 17:01:39','Cobertura Piura','Insumos'),(31,'Droguería Santa Rosas','20467890123','Alberto Mejía','967890123','santarosa@drogueria.pe','Jr. Santa Rosa 789, Cercado de Lima','15 días',1,'2025-11-13 17:01:39','Entrega nocturna','Medicamentos'),(32,'Laboratorios Bayer Perú','20234567891','Fiorella Luna','01-4567890','bayer@proveedor.pe','Av. Paseo de la República 2345, Miraflores','60 días',1,'2025-11-13 17:01:39','Línea ginecológica','Medicamentos'),(33,'FarmaCenter SAC','20589012356','Víctor Hugo','956789012','center@farma.pe','Av. Benavides 1234, Miraflores','30 días',1,'2025-11-13 17:01:39','Farmacia propia','Otros'),(34,'Distribuidora Médica del Sur','20145678901','Sandra Ponce','945678901','sur@medica.pe','Av. Salaverry 567, Arequipa','45 días',1,'2025-11-13 17:01:39','Cobertura sur','Material Médico'),(35,'Laboratorios GSK Perú','20356789012','Rodrigo Paz','01-5678901','gsk@proveedor.pe','Av. Larco 890, Miraflores','60 días',1,'2025-11-13 17:01:39','Vacunas y respiratorio','Medicamentos'),(36,'Droguería del Centro','20623456790','Lucero Vargas','934567890','centro@drogueria.pe','Jr. Huancavelica 123, Cercado de Lima','Contado',1,'2025-11-13 17:01:39','Mayorista','Medicamentos'),(37,'FarmaVida SAC','20489012345','Mario Cárdenas','923456789','vida@farmavida.pe','Av. Universitaria 567, San Miguel','15 días',1,'2025-11-13 17:01:39','Línea natural','Otros'),(38,'Importadora del Norte','20267890123','Carla Fuentes','912345678','norte@import.pe','Av. Túpac Amaru 890, Comas','30 días',1,'2025-11-13 17:01:39','Marcas asiáticas','Medicamentos'),(39,'Laboratorios Novartis Perú','20512345679','Felipe Arce','01-6789012','novartis@proveedor.pe','Av. Arequipa 4567, Lince','60 días',1,'2025-11-13 17:01:39','Oncología','Medicamentos'),(40,'Droguería Express Lima','20189012356','Verónica Salas','901234567','express@drogueria.pe','Av. Canadá 123, La Victoria','Contado',1,'2025-11-13 17:01:39','Entrega en 1 hora','Medicamentos'),(41,'FarmaSur Distribuidora','20345678923','Eduardo Tello','990123456','sur@farmasur.pe','Calle Jerusalén 234, Cusco','30 días',1,'2025-11-13 17:01:39','Cobertura andina','Medicamentos'),(42,'Quifarma del Valle','20678901256','Paola Rivas','979012345','valle@quifarma.pe','Av. Los Incas 567, San Juan de Miraflores','15 días',1,'2025-11-13 17:01:39','Farmacias asociadas','Medicamentos'),(43,'Laboratorios Sanofi Perú','20456789024','Gustavo Medina','01-7890123','sanofi@proveedor.pe','Av. Del Ejército 789, Miraflores','60 días',1,'2025-11-13 17:01:39','Vacunas','Medicamentos'),(44,'Droguería La Victoria','20290123467','Teresa Gómez','968901234','victoria@drogueria.pe','Av. México 1234, La Victoria','Contado',1,'2025-11-13 17:01:39','Mayorista popular','Medicamentos'),(45,'FarmaSalud del Perú','20567890145','Ángel Romero','957890123','salud@farma.pe','Jr. Paruro 567, Cercado de Lima','15 días',1,'2025-11-13 17:01:39','Línea vitamínica','Otros'),(46,'Distribuidora Médica Nacional','20123456790','Luz Marina','01-8901234','nacional@medica.pe','Av. Emancipación 890, Cercado de Lima','30 días',1,'2025-11-13 17:01:39','Cobertura nacional','Material Médico'),(47,'Laboratorios Merck Perú','20389012356','Oscar Vallejo','946789012','merck@proveedor.pe','Av. Salaverry 2345, Jesús María','60 días',1,'2025-11-13 17:01:39','Oncología y fertilidad','Medicamentos'),(48,'FarmaPlus SAC','20690123478','Natalia Bravo','935678901','plus@farma.pe','Av. Pardo 123, Miraflores','30 días',1,'2025-11-13 17:01:39','Red de farmacias','Otros'),(49,'Droguería del Pacífico','20434567891','Héctor Llanos','924567890','pacifico@drogueria.pe','Calle Comercio 456, Chimbote','Contado',1,'2025-11-13 17:01:39','Cobertura Áncash','Medicamentos'),(50,'Laboratorios AstraZeneca Perú','20256789013','Jimena Castro','01-9012345','astra@proveedor.pe','Av. Javier Prado Oeste 1234, San Isidro','60 días',1,'2025-11-13 17:01:39','Respiratorio y oncología','Medicamentos'),(51,'Droguería El Sol EIRL','20111111111','Miguel Ángel','987123456','elsol@drogueria.pe','Jr. Amazonas 123, Cercado de Lima','Contado',1,'2025-11-13 17:08:01','Mayorista en genéricos','Medicamentos'),(52,'Laboratorios Andino SAC','20122222222','Carmen Vega','01-3210987','andino@lab.pe','Av. Arequipa 5678, Lince','30 días',1,'2025-11-13 17:08:01','Línea andina natural','Medicamentos'),(53,'FarmaDistribuidora Nacional','20133333333','Raúl Sánchez','944567123','nacional@farmadist.pe','Av. Industrial 890, Ate','45 días',1,'2025-11-13 17:08:01','Distribución a provincias','Insumos'),(54,'Químicos del Centro EIRL','20144444444','Lucía Mendoza','955678234','centro@quimicos.pe','Jr. Puno 456, Cercado de Lima','15 días',1,'2025-11-13 17:08:01','Reactivos clínicos','Insumos'),(55,'Importadora MedGlobal SAC','20155555555','José Luis','933456123','medglobal@import.pe','Av. Canadá 2345, La Victoria','60 días',1,'2025-11-13 17:08:01','Marcas europeas y americanas','Medicamentos'),(56,'Droguería Salud Total','20166666666','María Elena','01-1098765','saludtotal@drogueria.pe','Jr. Tacna 789, Cercado de Lima','Contado',1,'2025-11-13 17:08:01','Vitaminas y suplementos','Otros'),(57,'FarmaVida del Sur SAC','20177777777','Pedro Morales','966789234','vida@farmasur.pe','Av. Goyeneche 123, Arequipa','30 días',1,'2025-11-13 17:08:01','Cobertura sur','Medicamentos'),(58,'Laboratorios Vitalis Perú','20188888888','Ana María','922345123','vitalis@lab.pe','Calle Los Rosales 456, Surco','45 días',1,'2025-11-13 17:08:01','Línea vitamínica','Medicamentos'),(59,'Distribuidora Médica Costa','20199999999','Carlos Peña','01-9876543','costa@medica.pe','Av. La Marina 1234, San Miguel','30 días',1,'2025-11-13 17:08:01','Insumos quirúrgicos','Material Médico'),(60,'Quifarma del Norte EIRL','20200000001','Rosa Jiménez','977890234','norte@quifarma.pe','Jr. Bolívar 890, Chiclayo','15 días',1,'2025-11-13 17:08:01','Antibióticos y genéricos','Medicamentos'),(61,'FarmaExpress del Centro','20200000002','Luis Alberto','988901345','express@farma.pe','Av. Abancay 567, Cercado de Lima','Contado',1,'2025-11-13 17:08:01','Entrega rápida','Medicamentos'),(62,'Laboratorios Biofarma Perú','20200000003','Sofía Torres','01-8765432','biofarma@lab.pe','Av. Primavera 789, Surco','60 días',1,'2025-11-13 17:08:01','Línea biológicos','Medicamentos'),(63,'Droguería del Oriente SAC','20200000004','Fernando Ruiz','999012456','oriente@drogueria.pe','Av. Grau 123, Iquitos','30 días',1,'2025-11-13 17:08:01','Cobertura selva','Insumos'),(64,'Importadora Andina Global','20200000005','Elena Vargas','955678345','andina@import.pe','Calle Mercaderes 567, Cusco','45 días',1,'2025-11-13 17:08:01','Marcas andinas','Medicamentos'),(65,'FarmaSalud Nacional SAC','20200000006','Miguel Soto','944567234','salud@farmacia.pe','Av. Brasil 890, Magdalena','15 días',1,'2025-11-13 17:08:01','Cuidado personal','Otros'),(66,'Químicos del Pacífico EIRL','20200000007','Claudia Herrera','966789345','pacifico@quimicos.pe','Calle Piérola 123, Piura','Contado',1,'2025-11-13 17:08:01','Reactivos','Insumos'),(67,'Laboratorios Medifarma Perú','20200000008','Ricardo León','01-7654321','medifarma@lab.pe','Av. Aramburú 456, San Isidro','60 días',1,'2025-11-13 17:08:01','Línea neurológica','Medicamentos'),(68,'Droguería La Libertad','20200000009','Patricia Gómez','977890345','libertad@drogueria.pe','Av. España 789, Trujillo','30 días',1,'2025-11-13 17:08:01','Cobertura norte','Medicamentos'),(69,'FarmaCenter del Sur','20200000010','Julio Ramos','988901456','center@farma.pe','Av. Salaverry 123, Arequipa','45 días',1,'2025-11-13 17:08:01','Farmacia propia','Otros'),(70,'Distribuidora Médica Andina','20200000011','Gloria Tapia','999012567','andina@medica.pe','Calle Jerusalén 456, Cusco','30 días',1,'2025-11-13 17:08:01','Insumos hospitalarios','Material Médico'),(71,'Laboratorios Roche Andino','20200000012','Esteban Quito','01-6543210','roche@andino.pe','Av. Víctor Belaúnde 789, San Isidro','60 días',1,'2025-11-13 17:08:01','Oncología','Medicamentos'),(72,'FarmaNorte Express','20200000013','Camila Ortiz','955678456','norte@express.pe','Av. América Norte 456, Trujillo','15 días',1,'2025-11-13 17:08:01','Entrega en 2h','Medicamentos'),(73,'Quifarma del Sur EIRL','20200000014','Renato Flores','966789456','sur@quifarma.pe','Calle San Martín 789, Tacna','Contado',1,'2025-11-13 17:08:01','Farmacias asociadas','Medicamentos'),(74,'Droguería del Valle Central','20200000015','Daniela Vera','977890456','valle@drogueria.pe','Av. Los Héroes 123, SJL','30 días',1,'2025-11-13 17:08:01','Mayorista','Insumos'),(75,'Laboratorios Pfizer Andino','20200000016','Alberto Mejía','01-5432109','pfizer@andino.pe','Av. Camino Real 789, San Isidro','60 días',1,'2025-11-13 17:08:01','Vacunas','Medicamentos'),(76,'FarmaSalud del Norte','20200000017','Fiorella Luna','988901567','norte@salud.pe','Jr. Bolívar 123, Chiclayo','45 días',1,'2025-11-13 17:08:01','Línea natural','Otros'),(77,'Importadora del Sur Global','20200000018','Víctor Hugo','999012678','sur@import.pe','Av. Goyeneche 456, Arequipa','30 días',1,'2025-11-13 17:08:01','Marcas internacionales','Medicamentos'),(78,'Droguería Costa Verde','20200000019','Sandra Ponce','955678567','costa@drogueria.pe','Av. La Marina 567, Callao','Contado',1,'2025-11-13 17:08:01','Entrega gratuita','Medicamentos'),(79,'Laboratorios Bayer Andino','20200000020','Rodrigo Paz','01-4321098','bayer@andino.pe','Av. Paseo República 123, Miraflores','60 días',1,'2025-11-13 17:08:01','Ginecología','Medicamentos'),(80,'FarmaVida Express','20200000021','Lucero Vargas','966789567','vida@express.pe','Jr. Unión 456, Cercado de Lima','15 días',1,'2025-11-13 17:08:01','Vitaminas','Otros'),(81,'Distribuidora Nacional Sur','20200000022','Mario Cárdenas','977890567','nacional@distribuidora.pe','Calle Mercaderes 789, Cusco','45 días',1,'2025-11-13 17:08:01','Cobertura andina','Material Médico'),(82,'Químicos del Oriente EIRL','20200000023','Carla Fuentes','988901678','oriente@quimicos.pe','Av. Grau 456, Iquitos','Contado',1,'2025-11-13 17:08:01','Reactivos','Insumos'),(83,'Laboratorios Novartis Andino','20200000024','Felipe Arce','01-3210987','novartis@andino.pe','Av. Arequipa 7890, Lince','60 días',1,'2025-11-13 17:08:01','Oncología','Medicamentos'),(84,'Droguería Express Nacional','20200000025','Verónica Salas','999012789','express@nacional.pe','Av. Canadá 456, La Victoria','15 días',1,'2025-11-13 17:08:01','Entrega en 1h','Medicamentos'),(85,'FarmaSur Andina','20200000026','Eduardo Tello','955678678','sur@farma.pe','Calle Jerusalén 123, Cusco','30 días',1,'2025-11-13 17:08:01','Cobertura sur','Medicamentos'),(86,'Quifarma Nacional EIRL','20200000027','Paola Rivas','966789678','nacional@quifarma.pe','Av. Los Incas 456, SJM','45 días',1,'2025-11-13 17:08:01','Genéricos','Medicamentos'),(87,'Laboratorios Sanofi Andino','20200000028','Gustavo Medina','01-2109876','sanofi@andino.pe','Av. Del Ejército 456, Miraflores','60 días',1,'2025-11-13 17:08:01','Vacunas','Medicamentos'),(88,'Droguería La Merced Andina','20200000029','Teresa Gómez','977890678','merced@andina.pe','Jr. La Merced 123, Cercado de Lima','Contado',1,'2025-11-13 17:08:01','Mayorista','Medicamentos'),(89,'FarmaSalud Andina','20200000030','Ángel Romero','988901789','salud@andina.pe','Jr. Paruro 456, Cercado de Lima','15 días',1,'2025-11-13 17:08:01','Suplementos','Otros'),(90,'Distribuidora Médica Global','20200000031','Luz Marina','999012890','global@medica.pe','Av. Emancipación 123, Cercado de Lima','30 días',1,'2025-11-13 17:08:01','Cobertura nacional','Material Médico'),(91,'Laboratorios Merck Andino','20200000032','Oscar Vallejo','01-1098765','merck@andino.pe','Av. Salaverry 456, Jesús María','60 días',1,'2025-11-13 17:08:01','Fertilidad','Medicamentos'),(92,'FarmaPlus Andina','20200000033','Natalia Bravo','955678789','plus@andina.pe','Av. Pardo 456, Miraflores','45 días',1,'2025-11-13 17:08:01','Red de farmacias','Otros'),(93,'Droguería del Norte Global','20200000034','Héctor Llanos','966789890','norte@drogueria.pe','Calle Comercio 789, Chimbote','Contado',1,'2025-11-13 17:08:01','Cobertura Áncash','Medicamentos'),(94,'Laboratorios AstraZeneca Andino','20200000035','Jimena Castro','977890901','astra@andino.pe','Av. Javier Prado Oeste 456, San Isidro','60 días',1,'2025-11-13 17:08:01','Respiratorio','Medicamentos'),(95,'FarmaExpress Andina','20200000036','Diego Navarro','988901012','express@andina.pe','Av. Canadá 789, La Victoria','15 días',1,'2025-11-13 17:08:01','Entrega rápida','Medicamentos'),(96,'Quifarma del Centro Global','20200000037','Valeria Paredes','999012123','centro@quifarma.pe','Jr. Huancavelica 456, Cercado de Lima','30 días',1,'2025-11-13 17:08:01','Reactivos','Insumos'),(97,'Droguería Nacional Sur','20200000038','Raúl Mendoza','01-9876543','nacional@drogueria.pe','Av. Salaverry 789, Arequipa','45 días',1,'2025-11-13 17:08:01','Mayorista','Medicamentos'),(98,'FarmaVida Nacional','20200000039','Isabel Cruz','955678901','vida@nacional.pe','Av. Universitaria 123, San Miguel','Contado',1,'2025-11-13 17:08:01','Línea natural','Otros'),(99,'Importadora Nacional Andina','20200000040','Gonzalo Rivera','966789012','nacional@import.pe','Av. Túpac Amaru 456, Comas','30 días',1,'2025-11-13 17:08:01','Marcas globales','Medicamentos'),(100,'Laboratorios GSK Andino','20200000041','Mónica Silva','977890123','gsk@andino.pe','Av. Larco 123, Miraflores','60 días',1,'2025-11-13 17:08:01','Respiratorio','Medicamentos'),(101,'Droguería Express Andina','20200000042','Julio Ramos','988901234','express@andina.pe','Jr. de la Unión 123, Cercado de Lima','15 días',1,'2025-11-13 17:08:01','Entrega 1h','Medicamentos'),(102,'proveedor','21859623151','Luis Mendoza','977860423','loza@gmail.com','Calle','15 días',1,'2025-11-14 00:59:59',NULL,'Insumos'),(139,'Proveedor prueba','20102563104','Juan Luis',NULL,'lozayatacojesus@gmail.com',NULL,'90 días',1,'2025-11-18 04:15:36',NULL,'Material Médico');
/*!40000 ALTER TABLE `proveedores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario_historial_acceso`
--

DROP TABLE IF EXISTS `usuario_historial_acceso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_historial_acceso` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint NOT NULL,
  `fecha_acceso` timestamp NOT NULL,
  `ip_address` varchar(100) DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  `exito` tinyint(1) DEFAULT NULL,
  `motivo_fallo` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=489 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario_historial_acceso`
--

LOCK TABLES `usuario_historial_acceso` WRITE;
/*!40000 ALTER TABLE `usuario_historial_acceso` DISABLE KEYS */;
INSERT INTO `usuario_historial_acceso` VALUES (1,12,'2025-11-13 16:55:18','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(2,12,'2025-11-13 17:13:14','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(3,12,'2025-11-13 22:06:25','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(4,12,'2025-11-13 22:19:50','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(5,12,'2025-11-13 22:25:56','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(6,12,'2025-11-13 23:48:18','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(7,12,'2025-11-14 00:23:54','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(8,12,'2025-11-14 00:30:25','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(9,12,'2025-11-14 00:33:11','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(10,12,'2025-11-14 00:37:56','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(11,12,'2025-11-14 00:39:59','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(12,12,'2025-11-14 00:43:29','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(13,12,'2025-11-14 00:46:39','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(14,12,'2025-11-14 00:48:57','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(15,12,'2025-11-14 00:52:13','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(16,12,'2025-11-14 00:54:20','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(17,12,'2025-11-14 00:56:29','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(18,12,'2025-11-15 04:16:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(19,16,'2025-11-15 04:46:43','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(20,12,'2025-11-15 04:51:07','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(21,12,'2025-11-15 05:09:09','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(22,12,'2025-11-15 05:11:45','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(23,12,'2025-11-15 05:17:15','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(24,16,'2025-11-15 05:25:08','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(25,12,'2025-11-15 05:36:57','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(26,12,'2025-11-15 05:42:50','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(27,12,'2025-11-15 05:49:33','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(28,16,'2025-11-15 05:49:49','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(29,12,'2025-11-15 05:50:09','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(30,12,'2025-11-15 05:58:59','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(31,16,'2025-11-15 05:59:22','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(32,12,'2025-11-15 06:00:10','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(33,16,'2025-11-15 06:00:30','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(34,12,'2025-11-15 06:05:36','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(35,16,'2025-11-15 06:06:04','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(36,12,'2025-11-15 06:06:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(37,16,'2025-11-15 06:06:55','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(38,12,'2025-11-15 06:07:17','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(39,12,'2025-11-15 06:13:49','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(40,16,'2025-11-15 06:14:12','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(41,12,'2025-11-15 06:14:28','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(42,16,'2025-11-15 06:14:50','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(43,12,'2025-11-15 06:15:16','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(44,16,'2025-11-15 06:15:29','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(45,12,'2025-11-15 06:15:44','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(46,16,'2025-11-15 06:16:01','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(47,16,'2025-11-15 06:16:12','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(48,12,'2025-11-15 06:16:19','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(49,16,'2025-11-15 06:16:49','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(50,12,'2025-11-15 06:17:16','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(51,16,'2025-11-15 06:17:28','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(52,12,'2025-11-15 06:19:46','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(53,16,'2025-11-15 06:20:04','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(54,16,'2025-11-15 06:20:17','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(55,12,'2025-11-15 06:20:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(56,16,'2025-11-15 06:20:50','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(57,12,'2025-11-15 06:22:34','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(58,16,'2025-11-15 06:22:48','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(59,12,'2025-11-15 06:22:59','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(60,16,'2025-11-15 06:23:11','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(61,12,'2025-11-15 06:23:39','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(62,12,'2025-11-15 06:43:02','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(63,16,'2025-11-15 06:43:21','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(64,12,'2025-11-15 06:43:36','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(65,16,'2025-11-15 06:43:54','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(66,12,'2025-11-15 06:44:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(67,12,'2025-11-15 06:48:21','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(68,16,'2025-11-15 06:48:37','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(69,12,'2025-11-15 06:48:50','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(70,16,'2025-11-15 06:49:09','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(71,12,'2025-11-15 06:55:55','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(72,16,'2025-11-15 06:56:11','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(73,12,'2025-11-15 06:56:30','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(74,16,'2025-11-15 06:56:47','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(75,12,'2025-11-15 06:57:02','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(76,16,'2025-11-15 06:57:16','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(77,12,'2025-11-15 06:57:44','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(78,16,'2025-11-15 06:58:04','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(79,12,'2025-11-15 06:58:16','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(80,12,'2025-11-15 07:02:30','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(81,16,'2025-11-15 07:03:36','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(82,12,'2025-11-15 07:07:57','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(83,16,'2025-11-15 07:08:13','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(84,12,'2025-11-15 07:08:46','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(85,16,'2025-11-15 07:09:04','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(86,12,'2025-11-15 07:16:39','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(87,16,'2025-11-15 07:17:52','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(88,12,'2025-11-15 07:18:11','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(89,16,'2025-11-15 07:19:29','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(90,12,'2025-11-15 07:28:43','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(91,16,'2025-11-15 07:29:20','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(92,12,'2025-11-15 07:29:39','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(93,16,'2025-11-15 07:30:27','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(94,12,'2025-11-15 07:39:43','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(95,16,'2025-11-15 07:40:11','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(96,12,'2025-11-15 07:40:38','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(97,16,'2025-11-15 07:40:59','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(98,12,'2025-11-15 07:45:35','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(99,16,'2025-11-15 07:45:52','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(100,12,'2025-11-15 07:50:25','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(101,16,'2025-11-15 07:50:48','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(102,12,'2025-11-15 07:51:12','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(103,16,'2025-11-15 07:52:12','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(104,12,'2025-11-15 07:53:47','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(105,16,'2025-11-15 07:54:08','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(106,12,'2025-11-15 07:54:44','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(107,12,'2025-11-15 07:57:36','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(108,16,'2025-11-15 07:57:56','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(109,12,'2025-11-15 21:10:14','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(110,16,'2025-11-15 21:11:17','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(111,12,'2025-11-15 21:11:46','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(112,16,'2025-11-15 21:12:00','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(113,12,'2025-11-15 21:12:21','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(114,16,'2025-11-15 21:12:40','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(115,12,'2025-11-15 21:12:55','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(116,16,'2025-11-15 21:13:08','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(117,12,'2025-11-15 21:13:19','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(118,16,'2025-11-15 21:13:42','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(119,12,'2025-11-15 21:14:54','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(120,16,'2025-11-15 21:15:06','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(121,12,'2025-11-15 21:18:18','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(122,16,'2025-11-15 21:19:06','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(123,12,'2025-11-15 21:19:16','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(124,16,'2025-11-15 21:19:28','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(125,12,'2025-11-15 21:20:19','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(126,12,'2025-11-15 21:25:51','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(127,12,'2025-11-15 21:29:04','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(128,16,'2025-11-15 21:29:17','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(129,12,'2025-11-15 21:29:30','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(130,12,'2025-11-15 21:29:43','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(131,16,'2025-11-15 21:29:49','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(132,12,'2025-11-15 21:30:25','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(133,16,'2025-11-15 21:30:37','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(134,12,'2025-11-15 21:35:32','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(135,12,'2025-11-15 21:35:49','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(136,16,'2025-11-15 21:35:56','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(137,16,'2025-11-15 21:38:17','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(138,16,'2025-11-15 21:45:12','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(139,12,'2025-11-15 21:45:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(140,12,'2025-11-15 21:45:36','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(141,12,'2025-11-16 05:54:20','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(142,12,'2025-11-16 06:01:21','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(143,12,'2025-11-16 06:05:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(144,12,'2025-11-16 06:11:42','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(145,12,'2025-11-16 06:24:15','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(146,12,'2025-11-16 06:28:33','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(147,12,'2025-11-16 06:30:57','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(148,12,'2025-11-16 06:36:04','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(149,12,'2025-11-16 06:41:05','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(150,12,'2025-11-16 06:43:05','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(151,12,'2025-11-16 22:22:04','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(152,12,'2025-11-16 22:25:35','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(153,12,'2025-11-16 22:36:51','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(154,12,'2025-11-16 22:46:54','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(155,12,'2025-11-17 02:35:14','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(156,12,'2025-11-17 05:52:25','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(157,12,'2025-11-17 05:53:19','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(158,12,'2025-11-17 05:55:27','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(159,12,'2025-11-17 06:04:49','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(160,12,'2025-11-17 06:12:32','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(161,12,'2025-11-17 06:15:01','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(162,12,'2025-11-17 06:15:59','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(163,12,'2025-11-17 06:24:21','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(164,12,'2025-11-17 06:27:56','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(165,12,'2025-11-17 06:32:58','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(166,12,'2025-11-17 06:38:34','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(167,12,'2025-11-18 00:08:53','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(168,12,'2025-11-18 00:43:25','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(169,12,'2025-11-18 01:03:57','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(170,12,'2025-11-18 01:06:22','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(171,12,'2025-11-18 01:08:32','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(172,12,'2025-11-18 01:12:56','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(173,12,'2025-11-18 01:15:46','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(174,12,'2025-11-18 01:17:10','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(175,12,'2025-11-18 01:18:49','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(176,12,'2025-11-18 01:25:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(177,12,'2025-11-18 01:38:25','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(178,12,'2025-11-18 01:44:11','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(179,12,'2025-11-18 01:47:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(180,12,'2025-11-18 01:51:41','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(181,12,'2025-11-18 01:54:05','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(182,12,'2025-11-18 02:00:03','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(183,12,'2025-11-18 02:09:43','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(184,12,'2025-11-18 02:12:13','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(185,12,'2025-11-18 02:47:13','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(186,12,'2025-11-18 04:04:35','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(187,12,'2025-11-18 04:07:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(188,20,'2025-11-18 04:56:10','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(189,12,'2025-11-18 04:56:17','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(190,20,'2025-11-18 05:14:03','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(191,12,'2025-11-18 05:18:59','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(192,12,'2025-11-18 05:23:33','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(193,12,'2025-11-18 05:29:38','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(194,12,'2025-11-18 05:34:47','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(195,12,'2025-11-18 05:37:59','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(196,12,'2025-11-18 05:38:15','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(197,12,'2025-11-18 05:41:14','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(198,12,'2025-11-18 06:13:39','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(199,12,'2025-11-18 06:13:52','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(200,20,'2025-11-18 06:14:02','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(201,12,'2025-11-18 06:14:10','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(202,20,'2025-11-18 06:18:14','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(203,12,'2025-11-18 06:19:01','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(204,20,'2025-11-18 06:19:20','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(205,12,'2025-11-18 06:33:45','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(206,20,'2025-11-18 06:35:11','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(207,12,'2025-11-18 06:35:20','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(208,12,'2025-11-18 06:41:05','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(209,12,'2025-11-18 06:47:16','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(210,12,'2025-11-18 06:59:46','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(211,12,'2025-11-18 07:04:23','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(212,12,'2025-11-18 07:05:02','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(213,12,'2025-11-18 07:16:27','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(214,12,'2025-11-18 07:22:56','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(215,12,'2025-11-18 07:33:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(216,12,'2025-11-18 07:36:46','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(217,12,'2025-11-18 07:42:30','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(218,12,'2025-11-18 07:45:47','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(219,20,'2025-11-18 07:46:14','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(220,12,'2025-11-18 07:47:11','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(221,12,'2025-11-18 07:52:18','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(222,12,'2025-11-18 07:57:13','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(223,12,'2025-11-18 08:04:46','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(224,12,'2025-11-18 08:09:46','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(225,12,'2025-11-18 08:10:05','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(226,12,'2025-11-18 08:13:50','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(227,12,'2025-11-18 08:22:30','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(228,12,'2025-11-18 08:25:49','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(229,12,'2025-11-18 08:33:34','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(230,12,'2025-11-18 08:41:07','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(231,12,'2025-11-18 08:43:51','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(232,12,'2025-11-18 09:05:55','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(233,12,'2025-11-18 09:11:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(234,12,'2025-11-18 09:18:08','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(235,12,'2025-11-18 09:25:01','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(236,12,'2025-11-18 09:27:58','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(237,12,'2025-11-18 23:06:12','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(238,12,'2025-11-18 23:14:30','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(239,12,'2025-11-18 23:18:55','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(240,12,'2025-11-18 23:21:19','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(241,12,'2025-11-18 23:25:51','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(242,12,'2025-11-18 23:27:39','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(243,12,'2025-11-18 23:31:22','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(244,12,'2025-11-18 23:37:08','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(245,12,'2025-11-18 23:39:18','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(246,12,'2025-11-18 23:45:07','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(247,12,'2025-11-18 23:47:43','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(248,12,'2025-11-18 23:52:31','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(249,12,'2025-11-18 23:55:10','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(250,12,'2025-11-18 23:57:37','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(251,12,'2025-11-19 00:02:33','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(252,12,'2025-11-19 00:06:12','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(253,12,'2025-11-19 00:12:14','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(254,12,'2025-11-19 00:19:30','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(255,12,'2025-11-19 00:37:23','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(256,12,'2025-11-19 00:40:24','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(257,12,'2025-11-19 00:44:43','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(258,12,'2025-11-19 00:50:33','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(259,12,'2025-11-19 00:52:50','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(260,12,'2025-11-19 01:01:15','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(261,12,'2025-11-19 01:03:01','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(262,12,'2025-11-19 01:05:37','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(263,12,'2025-11-19 01:10:53','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(264,12,'2025-11-19 01:15:10','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(265,12,'2025-11-19 01:16:40','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(266,12,'2025-11-19 01:18:28','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(267,12,'2025-11-19 01:22:02','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(268,12,'2025-11-19 01:23:12','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(269,12,'2025-11-19 01:25:04','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(270,12,'2025-11-19 01:27:31','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(271,12,'2025-11-19 01:29:19','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(272,12,'2025-11-19 01:33:11','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(273,12,'2025-11-19 02:52:49','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(274,12,'2025-11-19 04:50:29','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(275,12,'2025-11-19 04:52:16','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(276,12,'2025-11-19 04:55:51','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(277,12,'2025-11-19 05:02:30','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(278,12,'2025-11-19 05:03:47','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(279,12,'2025-11-19 05:10:55','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(280,12,'2025-11-19 05:16:19','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(281,12,'2025-11-19 05:51:28','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(282,12,'2025-11-19 06:10:17','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(283,12,'2025-11-19 06:23:06','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(284,12,'2025-11-19 22:14:20','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(285,12,'2025-11-19 22:15:07','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(286,20,'2025-11-19 22:15:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(287,12,'2025-11-19 22:16:12','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(288,20,'2025-11-19 22:17:41','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(289,12,'2025-11-19 22:18:00','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(290,12,'2025-11-19 22:21:22','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(291,20,'2025-11-19 22:26:17','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(292,12,'2025-11-19 22:28:16','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(293,20,'2025-11-19 22:28:44','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(294,12,'2025-11-19 22:32:18','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(295,20,'2025-11-19 22:32:33','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(296,12,'2025-11-19 22:41:10','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(297,12,'2025-11-19 22:44:05','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(298,12,'2025-11-19 23:11:38','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(299,12,'2025-11-20 00:15:44','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(300,12,'2025-11-20 00:29:54','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(301,12,'2025-11-20 01:00:47','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(302,12,'2025-11-20 01:02:29','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(303,12,'2025-11-20 01:08:10','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(304,12,'2025-11-20 01:12:08','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(305,12,'2025-11-20 01:17:42','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(306,12,'2025-11-20 01:19:03','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(307,12,'2025-11-20 01:24:51','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(308,12,'2025-11-20 01:26:06','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(309,12,'2025-11-20 01:27:25','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(310,12,'2025-11-20 01:28:22','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(311,12,'2025-11-20 01:29:58','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(312,12,'2025-11-20 01:31:04','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(313,12,'2025-11-20 01:32:52','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(314,12,'2025-11-20 02:35:14','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(315,12,'2025-11-20 02:38:58','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(316,12,'2025-11-20 02:45:52','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(317,12,'2025-11-20 03:00:55','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(318,12,'2025-11-20 03:02:57','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(319,12,'2025-11-20 03:03:36','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(320,12,'2025-11-20 03:05:12','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(321,12,'2025-11-20 03:06:20','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(322,12,'2025-11-20 03:10:56','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(323,12,'2025-11-20 03:14:00','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(324,12,'2025-11-20 03:14:56','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(325,12,'2025-11-20 03:17:09','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(326,12,'2025-11-20 03:23:37','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(327,12,'2025-11-20 03:32:33','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(328,12,'2025-11-20 03:34:33','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(329,12,'2025-11-20 03:37:18','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(330,12,'2025-11-20 03:39:23','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(331,12,'2025-11-20 03:42:25','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(332,12,'2025-11-20 03:53:10','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(333,12,'2025-11-20 03:55:00','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(334,12,'2025-11-20 04:02:11','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(335,12,'2025-11-20 04:04:37','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(336,12,'2025-11-20 04:13:21','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(337,12,'2025-11-20 04:20:22','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(338,12,'2025-11-20 04:23:42','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(339,12,'2025-11-20 04:25:59','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(340,12,'2025-11-20 04:28:36','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(341,12,'2025-11-20 04:30:35','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(342,12,'2025-11-20 04:33:08','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(343,12,'2025-11-20 04:37:41','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(344,12,'2025-11-20 04:40:54','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(345,12,'2025-11-20 04:43:20','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(346,12,'2025-11-20 04:45:50','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(347,12,'2025-11-20 04:49:34','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(348,12,'2025-11-20 05:13:56','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(349,12,'2025-11-20 05:16:40','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(350,12,'2025-11-20 05:19:21','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(351,12,'2025-11-20 05:21:15','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(352,12,'2025-11-20 05:28:21','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(353,12,'2025-11-20 05:32:48','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(354,12,'2025-11-20 05:39:36','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(355,12,'2025-11-20 05:49:47','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(356,12,'2025-11-20 05:53:17','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(357,12,'2025-11-20 06:11:32','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(358,12,'2025-11-20 06:20:29','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(359,12,'2025-11-20 06:57:49','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(360,12,'2025-11-20 08:06:19','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(361,12,'2025-11-20 08:36:28','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(362,12,'2025-11-20 08:38:20','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(363,12,'2025-11-20 15:56:42','192.168.244.50','lozay - Windows 11 10.0',1,NULL),(364,12,'2025-11-20 16:01:12','192.168.43.201','lozay - Windows 11 10.0',1,NULL),(365,12,'2025-11-20 16:09:50','192.168.43.201','lozay - Windows 11 10.0',1,NULL),(366,12,'2025-11-20 16:10:54','192.168.43.201','lozay - Windows 11 10.0',1,NULL),(367,12,'2025-11-20 16:12:17','192.168.43.201','lozay - Windows 11 10.0',1,NULL),(368,12,'2025-11-20 16:14:16','192.168.137.57','lozay - Windows 11 10.0',1,NULL),(369,12,'2025-11-20 16:19:46','192.168.137.57','lozay - Windows 11 10.0',1,NULL),(370,20,'2025-11-20 16:23:15','192.168.137.57','lozay - Windows 11 10.0',1,NULL),(371,20,'2025-11-20 17:07:30','192.168.137.57','lozay - Windows 11 10.0',1,NULL),(372,12,'2025-11-20 17:23:05','192.168.137.57','lozay - Windows 11 10.0',1,NULL),(373,12,'2025-11-20 20:16:23','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(374,12,'2025-11-20 22:49:22','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(375,12,'2025-11-20 22:53:49','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(376,12,'2025-11-20 23:01:34','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(377,12,'2025-11-20 23:15:09','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(378,12,'2025-11-20 23:16:49','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(379,12,'2025-11-20 23:23:36','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(380,12,'2025-11-20 23:26:46','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(381,12,'2025-11-20 23:28:42','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(382,12,'2025-11-20 23:44:11','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(383,12,'2025-11-20 23:46:42','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(384,12,'2025-11-20 23:50:29','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(385,12,'2025-11-20 23:55:15','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(386,12,'2025-11-21 00:03:24','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(387,12,'2025-11-21 00:05:59','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(388,12,'2025-11-21 00:07:20','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(389,12,'2025-11-21 00:08:27','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(390,12,'2025-11-21 00:09:47','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(391,12,'2025-11-21 00:12:08','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(392,12,'2025-11-21 00:15:34','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(393,12,'2025-11-21 00:17:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(394,12,'2025-11-21 00:21:07','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(395,12,'2025-11-21 00:25:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(396,12,'2025-11-21 00:29:29','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(397,12,'2025-11-21 00:32:58','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(398,12,'2025-11-21 00:35:15','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(399,12,'2025-11-21 00:37:39','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(400,12,'2025-11-21 00:38:36','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(401,12,'2025-11-21 00:39:22','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(402,12,'2025-11-21 01:12:53','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(403,12,'2025-11-21 01:17:53','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(404,12,'2025-11-21 01:20:19','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(405,12,'2025-11-21 01:22:25','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(406,12,'2025-11-21 01:23:47','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(407,12,'2025-11-21 01:26:48','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(408,12,'2025-11-21 01:28:11','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(409,12,'2025-11-21 01:29:31','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(410,12,'2025-11-21 01:31:53','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(411,12,'2025-11-21 01:57:45','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(412,12,'2025-11-21 02:01:02','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(413,12,'2025-11-21 02:03:52','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(414,12,'2025-11-21 02:16:25','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(415,12,'2025-11-21 02:19:54','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(416,12,'2025-11-21 02:21:38','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(417,12,'2025-11-21 02:24:35','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(418,12,'2025-11-21 02:25:04','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(419,12,'2025-11-21 02:29:18','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(420,12,'2025-11-21 02:31:14','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(421,12,'2025-11-21 02:33:17','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(422,12,'2025-11-21 03:09:47','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(423,12,'2025-11-21 03:40:35','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(424,12,'2025-11-21 04:32:56','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(425,12,'2025-11-21 04:38:30','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(426,12,'2025-11-21 04:42:30','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(427,12,'2025-11-21 04:48:10','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(428,12,'2025-11-21 04:53:43','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(429,12,'2025-11-21 05:01:02','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(430,12,'2025-11-21 05:06:49','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(431,12,'2025-11-21 05:16:29','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(432,12,'2025-11-21 05:31:28','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(433,12,'2025-11-21 05:39:34','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(434,12,'2025-11-21 05:42:55','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(435,12,'2025-11-21 06:03:56','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(436,12,'2025-11-21 06:06:09','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(437,12,'2025-11-21 06:13:44','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(438,12,'2025-11-21 06:18:06','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(439,12,'2025-11-21 06:20:06','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(440,12,'2025-11-21 06:23:19','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(441,12,'2025-11-21 06:28:00','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(442,12,'2025-11-21 06:30:10','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(443,12,'2025-11-21 06:31:50','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(444,12,'2025-11-21 06:34:20','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(445,12,'2025-11-21 06:36:27','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(446,12,'2025-11-21 06:37:40','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(447,12,'2025-11-21 06:43:46','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(448,12,'2025-11-21 06:46:51','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(449,12,'2025-11-21 06:47:40','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(450,12,'2025-11-21 06:48:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(451,12,'2025-11-21 06:49:10','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(452,12,'2025-11-21 06:50:20','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(453,12,'2025-11-21 06:51:06','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(454,12,'2025-11-21 06:52:05','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(455,12,'2025-11-21 06:53:08','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(456,12,'2025-11-21 06:54:08','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(457,12,'2025-11-21 07:10:21','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(458,12,'2025-11-21 07:11:07','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(459,12,'2025-11-21 07:12:43','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(460,12,'2025-11-21 07:17:05','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(461,12,'2025-11-21 07:18:26','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(462,12,'2025-11-21 07:23:33','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(463,12,'2025-11-21 07:28:31','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(464,12,'2025-11-21 07:31:49','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(465,12,'2025-11-21 07:35:02','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(466,12,'2025-11-21 07:40:15','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(467,12,'2025-11-21 07:47:02','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(468,12,'2025-11-21 07:49:45','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(469,12,'2025-11-21 07:51:04','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(470,12,'2025-11-22 03:50:11','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(471,12,'2025-11-22 04:03:14','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(472,20,'2025-11-22 04:06:11','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(473,12,'2025-11-22 04:07:17','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(474,12,'2025-11-22 04:15:49','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(475,12,'2025-11-22 04:17:59','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(476,12,'2025-11-22 04:23:03','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(477,12,'2025-11-22 04:33:23','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(478,12,'2025-11-22 04:38:11','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(479,12,'2025-11-22 04:41:05','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(480,12,'2025-11-22 04:47:13','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(481,12,'2025-11-22 04:50:34','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(482,12,'2025-11-22 04:57:19','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(483,12,'2025-11-22 04:59:05','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(484,12,'2025-11-22 05:01:34','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(485,12,'2025-11-22 05:07:13','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(486,12,'2025-11-22 05:11:14','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(487,12,'2025-11-22 05:37:23','192.168.0.109','lozay - Windows 11 10.0',1,NULL),(488,12,'2025-11-22 05:39:10','192.168.0.109','lozay - Windows 11 10.0',1,NULL);
/*!40000 ALTER TABLE `usuario_historial_acceso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario_historial_cambio`
--

DROP TABLE IF EXISTS `usuario_historial_cambio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_historial_cambio` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `campo_modificado` varchar(100) DEFAULT NULL,
  `valor_anterior` text,
  `valor_nuevo` text,
  `modificado_por` int DEFAULT NULL,
  `fecha` timestamp NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario_historial_cambio`
--

LOCK TABLES `usuario_historial_cambio` WRITE;
/*!40000 ALTER TABLE `usuario_historial_cambio` DISABLE KEYS */;
INSERT INTO `usuario_historial_cambio` VALUES (1,17,'CREACION',NULL,'Usuario creado',12,'2025-11-17 06:16:47'),(2,18,'CREACION',NULL,'Usuario creado',12,'2025-11-17 06:16:50'),(3,18,'ELIMINACION','Usuario activo','Usuario eliminado',12,'2025-11-17 06:17:01'),(4,19,'CREACION',NULL,'Usuario creado',12,'2025-11-17 06:17:27'),(5,19,'ELIMINACION','Usuario activo','Usuario eliminado',12,'2025-11-17 06:17:32'),(6,8,'ESTADO','Activo','Inactivo',1,'2025-11-17 06:39:32'),(7,8,'ELIMINACION','Usuario activo','Usuario eliminado',12,'2025-11-18 04:52:30'),(8,7,'ELIMINACION','Usuario activo','Usuario eliminado',12,'2025-11-18 04:52:34'),(9,13,'ELIMINACION','Usuario activo','Usuario eliminado',12,'2025-11-18 04:52:38'),(10,17,'ELIMINACION','Usuario activo','Usuario eliminado',12,'2025-11-18 04:52:41'),(11,15,'ELIMINACION','Usuario activo','Usuario eliminado',12,'2025-11-18 04:52:44'),(12,16,'ELIMINACION','Usuario activo','Usuario eliminado',12,'2025-11-18 04:52:47'),(13,20,'CREACION',NULL,'Usuario creado',12,'2025-11-18 04:54:22'),(14,12,'ESTADO','Activo','Inactivo',1,'2025-11-18 04:55:20'),(15,12,'ESTADO','Inactivo','Activo',1,'2025-11-18 04:55:24'),(16,20,'dni','','72017470',12,'2025-11-18 04:56:27'),(17,12,'ESTADO','Activo','Inactivo',1,'2025-11-18 04:57:13'),(18,12,'ESTADO','Inactivo','Activo',1,'2025-11-18 04:57:14'),(19,20,'ESTADO','Activo','Inactivo',1,'2025-11-18 04:57:18'),(20,20,'ESTADO','Inactivo','Activo',1,'2025-11-18 04:57:21'),(21,20,'email','maría@gmail.com','lozayatacojesus@gmail.com',12,'2025-11-18 06:34:03'),(22,20,'username','María','Marías',12,'2025-11-18 08:27:09'),(23,12,'apellidos','Administrador','Loza',12,'2025-11-18 08:41:45'),(24,12,'telefono','977860423','977860428',12,'2025-11-18 08:41:57'),(25,12,'nombres','Jesus','Jesús',12,'2025-11-18 08:42:12'),(26,12,'nombres','Jesús','Jesus',12,'2025-11-18 08:42:20'),(27,12,'apellidos','Loza','Lozas',12,'2025-11-18 08:42:28'),(28,12,'nombres','Jesus','Jesuss',12,'2025-11-18 08:42:32'),(29,12,'telefono','977860428','977860429',12,'2025-11-18 08:42:41'),(30,20,'telefono','','956236123',12,'2025-11-18 08:42:49'),(31,12,'nombres','Jesuss','Jesus',12,'2025-11-18 09:28:09'),(32,12,'apellidos','Lozas','Loza',12,'2025-11-19 00:59:18'),(33,20,'username','Marías','María',12,'2025-11-19 00:59:25');
/*!40000 ALTER TABLE `usuario_historial_cambio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario_permisos`
--

DROP TABLE IF EXISTS `usuario_permisos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_permisos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `permiso` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `valor` tinyint(1) NOT NULL,
  `fecha_asignacion` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_usuario_id` (`usuario_id`),
  CONSTRAINT `fk_usuario_permisos_usuario_id` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario_permisos`
--

LOCK TABLES `usuario_permisos` WRITE;
/*!40000 ALTER TABLE `usuario_permisos` DISABLE KEYS */;
INSERT INTO `usuario_permisos` VALUES (85,20,'clientes.nuevo',0,'2025-11-19 17:26:02'),(86,20,'proveedores.nuevo',0,'2025-11-18 02:38:29'),(87,20,'inventario.agregar',0,'2025-11-18 02:42:52'),(88,20,'inventario.editar',0,'2025-11-18 02:42:54'),(89,20,'inventario.eliminar',0,'2025-11-18 02:42:54'),(90,20,'proveedores.eliminar',0,'2025-11-19 17:26:00'),(91,20,'proveedores.editar',0,'2025-11-19 17:26:00');
/*!40000 ALTER TABLE `usuario_permisos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rol` enum('ADMIN','VENDEDOR') COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombres` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellidos` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `dni` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login` timestamp NULL DEFAULT NULL,
  `recovery_token` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `recovery_token_expiry` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `dni` (`dni`),
  KEY `idx_username` (`username`),
  KEY `idx_rol` (`rol`),
  KEY `idx_activo` (`activo`),
  KEY `idx_dni` (`dni`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Usuarios del sistema con roles ADMIN y VENDEDOR';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (12,'jesus','$2a$10$reEjUfxLN.sF/lpC4FOwkOZise.DDhApoTfOfxmF/B2BleHuBd.4O','ADMIN','Jesus','Loza','72017471','977860429','lozayataco@gmail.com',1,'2025-10-02 06:01:09','2025-11-22 05:39:10','THO1F6','2025-11-19 18:25:21'),(20,'María','$2a$10$drR4nuls/3DPlZiYJdHl2uc9MHLyYJqcb2te/nC3VBDHq.DVQx7Mi','VENDEDOR','María','Delgado','72017470','956236123','lozayatacojesus@gmail.com',1,'2025-11-18 04:54:21','2025-11-22 04:06:10',NULL,NULL);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `venta_historial_cambio`
--

DROP TABLE IF EXISTS `venta_historial_cambio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `venta_historial_cambio` (
  `id` int NOT NULL AUTO_INCREMENT,
  `venta_id` int NOT NULL,
  `tipo_cambio` enum('CREACION','ANULACION','EDICION') COLLATE utf8mb4_general_ci NOT NULL,
  `motivo` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `usuario_id` int NOT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `venta_id` (`venta_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `venta_historial_cambio_ibfk_1` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `venta_historial_cambio_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `venta_historial_cambio`
--

LOCK TABLES `venta_historial_cambio` WRITE;
/*!40000 ALTER TABLE `venta_historial_cambio` DISABLE KEYS */;
INSERT INTO `venta_historial_cambio` VALUES (1,2,'CREACION','Venta registrada',12,'2025-11-21 01:55:05'),(2,3,'CREACION','Venta registrada',12,'2025-11-21 02:21:58'),(3,2,'ANULACION','Venta anulada desde historial',12,'2025-11-21 23:41:30'),(4,6,'CREACION','Venta registrada',12,'2025-11-21 23:49:12'),(5,7,'CREACION','Venta registrada',12,'2025-11-21 23:51:09'),(6,8,'CREACION','Venta registrada',12,'2025-11-21 23:51:49'),(7,9,'CREACION','Venta registrada',12,'2025-11-21 23:52:22'),(8,10,'CREACION','Venta registrada',12,'2025-11-22 00:11:45');
/*!40000 ALTER TABLE `venta_historial_cambio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ventas`
--

DROP TABLE IF EXISTS `ventas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ventas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cliente_id` int DEFAULT NULL,
  `usuario_id` int NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `descuento_monto` decimal(10,2) DEFAULT '0.00',
  `igv_monto` decimal(10,2) DEFAULT '0.00',
  `total` decimal(10,2) NOT NULL,
  `tipo_pago` enum('EFECTIVO','TARJETA','TRANSFERENCIA','MIXTO') COLLATE utf8mb4_general_ci NOT NULL,
  `tipo_comprobante` enum('BOLETA','FACTURA','TICKET') COLLATE utf8mb4_general_ci NOT NULL,
  `numero_boleta` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `serie` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha_venta` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` enum('REALIZADA','ANULADA','PENDIENTE') COLLATE utf8mb4_general_ci DEFAULT 'REALIZADA',
  `observaciones` text COLLATE utf8mb4_general_ci,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `cliente_id` (`cliente_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `ventas_ibfk_1` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`),
  CONSTRAINT `ventas_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ventas`
--

LOCK TABLES `ventas` WRITE;
/*!40000 ALTER TABLE `ventas` DISABLE KEYS */;
INSERT INTO `ventas` VALUES (1,NULL,12,8.50,NULL,1.53,10.03,'TARJETA','TICKET','000001','TKT1','2025-11-21 01:13:58','REALIZADA','Venta registrada como ticket simple','2025-11-21 06:13:58','2025-11-21 06:13:58'),(2,20,12,5.00,0.00,0.90,5.90,'MIXTO','BOLETA','000001','BBB1','2025-11-21 01:55:05','ANULADA','','2025-11-21 06:55:05','2025-11-21 06:55:05'),(3,37,12,17.50,0.00,3.15,20.65,'MIXTO','FACTURA','000001','FFF1','2025-11-21 02:21:58','REALIZADA','','2025-11-21 07:21:58','2025-11-21 07:21:58'),(4,18,20,11.00,NULL,1.98,12.98,'TARJETA','TICKET','000002','TKT1','2025-11-21 23:06:32','REALIZADA','Venta registrada como ticket simple','2025-11-22 04:06:32','2025-11-22 04:06:32'),(6,40,12,5.00,0.00,0.90,5.90,'EFECTIVO','BOLETA','000002','BBB1','2025-11-21 23:49:12','REALIZADA','','2025-11-22 04:49:12','2025-11-22 04:49:12'),(7,41,12,3.00,0.00,0.54,3.54,'EFECTIVO','BOLETA','000003','BBB1','2025-11-21 23:51:09','REALIZADA','','2025-11-22 04:51:09','2025-11-22 04:51:09'),(8,42,12,4.00,0.00,0.72,4.72,'TARJETA','BOLETA','000004','BBB1','2025-11-21 23:51:49','REALIZADA','','2025-11-22 04:51:49','2025-11-22 04:51:49'),(9,43,12,6.00,0.00,1.08,7.08,'EFECTIVO','BOLETA','000005','BBB1','2025-11-21 23:52:22','REALIZADA','','2025-11-22 04:52:22','2025-11-22 04:52:22'),(10,44,12,11.00,0.00,1.98,12.98,'TARJETA','FACTURA','000002','FFF1','2025-11-22 00:11:45','REALIZADA','','2025-11-22 05:11:45','2025-11-22 05:11:45');
/*!40000 ALTER TABLE `ventas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `vw_busqueda_productos`
--

DROP TABLE IF EXISTS `vw_busqueda_productos`;
/*!50001 DROP VIEW IF EXISTS `vw_busqueda_productos`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_busqueda_productos` AS SELECT 
 1 AS `id`,
 1 AS `codigo`,
 1 AS `nombre`,
 1 AS `descripcion`,
 1 AS `principio_activo`,
 1 AS `concentracion`,
 1 AS `forma_farmaceutica`,
 1 AS `precio_venta`,
 1 AS `stock_actual`,
 1 AS `categoria`,
 1 AS `ubicacion`,
 1 AS `requiere_receta`,
 1 AS `codigo_nombre`,
 1 AS `texto_busqueda`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_inventario_alertas`
--

DROP TABLE IF EXISTS `vw_inventario_alertas`;
/*!50001 DROP VIEW IF EXISTS `vw_inventario_alertas`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_inventario_alertas` AS SELECT 
 1 AS `id_producto`,
 1 AS `nombre_producto`,
 1 AS `stock_actual`,
 1 AS `stock_minimo`,
 1 AS `fecha_vencimiento`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_resumen_ventas`
--

DROP TABLE IF EXISTS `vw_resumen_ventas`;
/*!50001 DROP VIEW IF EXISTS `vw_resumen_ventas`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_resumen_ventas` AS SELECT 
 1 AS `id`,
 1 AS `numero_boleta`,
 1 AS `serie`,
 1 AS `fecha_venta`,
 1 AS `cliente`,
 1 AS `vendedor`,
 1 AS `subtotal`,
 1 AS `descuento_monto`,
 1 AS `total`,
 1 AS `tipo_pago`,
 1 AS `estado`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `vw_busqueda_productos`
--

/*!50001 DROP VIEW IF EXISTS `vw_busqueda_productos`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_busqueda_productos` AS select `p`.`id` AS `id`,`p`.`codigo` AS `codigo`,`p`.`nombre` AS `nombre`,`p`.`descripcion` AS `descripcion`,`p`.`principio_activo` AS `principio_activo`,`p`.`concentracion` AS `concentracion`,`p`.`forma_farmaceutica` AS `forma_farmaceutica`,`p`.`precio_venta` AS `precio_venta`,`p`.`stock_actual` AS `stock_actual`,`p`.`categoria` AS `categoria`,`p`.`ubicacion` AS `ubicacion`,`p`.`requiere_receta` AS `requiere_receta`,concat(`p`.`codigo`,' - ',`p`.`nombre`) AS `codigo_nombre`,concat(`p`.`nombre`,' ',ifnull(`p`.`principio_activo`,''),' ',ifnull(`p`.`concentracion`,'')) AS `texto_busqueda` from `productos` `p` where (`p`.`activo` = 1) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_inventario_alertas`
--

/*!50001 DROP VIEW IF EXISTS `vw_inventario_alertas`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_inventario_alertas` AS select `p`.`id` AS `id_producto`,`p`.`nombre` AS `nombre_producto`,`p`.`stock_actual` AS `stock_actual`,`p`.`stock_minimo` AS `stock_minimo`,`p`.`fecha_vencimiento` AS `fecha_vencimiento` from `productos` `p` where ((`p`.`activo` = 1) and ((`p`.`stock_actual` <= `p`.`stock_minimo`) or ((to_days(`p`.`fecha_vencimiento`) - to_days(curdate())) <= 30))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_resumen_ventas`
--

/*!50001 DROP VIEW IF EXISTS `vw_resumen_ventas`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_resumen_ventas` AS select `v`.`id` AS `id`,`v`.`numero_boleta` AS `numero_boleta`,`v`.`serie` AS `serie`,`v`.`fecha_venta` AS `fecha_venta`,concat(ifnull(`c`.`nombres`,'SIN'),' ',ifnull(`c`.`apellidos`,'CLIENTE')) AS `cliente`,concat(`u`.`nombres`,' ',`u`.`apellidos`) AS `vendedor`,`v`.`subtotal` AS `subtotal`,`v`.`descuento_monto` AS `descuento_monto`,`v`.`total` AS `total`,`v`.`tipo_pago` AS `tipo_pago`,`v`.`estado` AS `estado` from ((`ventas` `v` left join `clientes` `c` on((`v`.`cliente_id` = `c`.`id`))) join `usuarios` `u` on((`v`.`usuario_id` = `u`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-22  0:39:18
