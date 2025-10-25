-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3307
-- Generation Time: Oct 25, 2025 at 05:05 AM
-- Server version: 8.4.3
-- PHP Version: 8.3.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `pharmavictoria`
--

-- --------------------------------------------------------

--
-- Table structure for table `audit_log`
--

CREATE TABLE `audit_log` (
  `id` int NOT NULL,
  `usuario_id` int DEFAULT NULL,
  `accion` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tabla_afectada` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `registro_id` int DEFAULT NULL,
  `valores_anteriores` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `valores_nuevos` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` text COLLATE utf8mb4_unicode_ci,
  `timestamp` datetime DEFAULT CURRENT_TIMESTAMP
) ;

-- --------------------------------------------------------

--
-- Table structure for table `carrito_ventas`
--

CREATE TABLE `carrito_ventas` (
  `id` int NOT NULL,
  `usuario_id` int NOT NULL,
  `productos` json NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` enum('PENDIENTE','COMPLETADO','CANCELADO') COLLATE utf8mb4_general_ci DEFAULT 'PENDIENTE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `clientes`
--

CREATE TABLE `clientes` (
  `id` int NOT NULL,
  `dni` varchar(8) COLLATE utf8mb4_general_ci NOT NULL,
  `nombres` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `apellidos` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `direccion` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `puntos_totales` int DEFAULT '0',
  `puntos_usados` int DEFAULT '0',
  `puntos_disponibles` int GENERATED ALWAYS AS ((`puntos_totales` - `puntos_usados`)) STORED,
  `es_frecuente` tinyint(1) DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `cliente_historial_cambio`
--

CREATE TABLE `cliente_historial_cambio` (
  `id` int NOT NULL,
  `cliente_id` int NOT NULL,
  `campo_modificado` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `valor_anterior` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `valor_nuevo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `comprobantes`
--

CREATE TABLE `comprobantes` (
  `id` int NOT NULL,
  `venta_id` int NOT NULL,
  `tipo` enum('BOLETA','FACTURA') COLLATE utf8mb4_general_ci NOT NULL,
  `serie` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,
  `numero` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `hash_sunat` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `estado_sunat` enum('GENERADO','ENVIADO','ACEPTADO','RECHAZADO') COLLATE utf8mb4_general_ci DEFAULT 'GENERADO',
  `fecha_emision` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `configuracion`
--

CREATE TABLE `configuracion` (
  `id` int NOT NULL,
  `clave` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `valor` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo_dato` enum('STRING','INTEGER','DECIMAL','BOOLEAN','JSON') COLLATE utf8mb4_unicode_ci DEFAULT 'STRING',
  `categoria` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Configuraciones del sistema';

-- --------------------------------------------------------

--
-- Table structure for table `configuracion_codigos`
--

CREATE TABLE `configuracion_codigos` (
  `id` int NOT NULL,
  `categoria` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `prefijo` varchar(5) COLLATE utf8mb4_general_ci NOT NULL,
  `ultimo_numero` int DEFAULT '0',
  `longitud_numero` int DEFAULT '3'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `detalle_ventas`
--

CREATE TABLE `detalle_ventas` (
  `id` int NOT NULL,
  `venta_id` int NOT NULL,
  `producto_id` int NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `descuento` decimal(10,2) DEFAULT '0.00',
  `subtotal` decimal(10,2) NOT NULL,
  `lote` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha_vencimiento` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `productos`
--

CREATE TABLE `productos` (
  `id` int NOT NULL,
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
  `fecha_fabricacion` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `producto_historial_cambio`
--

CREATE TABLE `producto_historial_cambio` (
  `id` int NOT NULL,
  `producto_id` int NOT NULL,
  `campo_modificado` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `valor_anterior` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `valor_nuevo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `proveedores`
--

CREATE TABLE `proveedores` (
  `id` int NOT NULL,
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
  `tipo_producto` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Proveedores de medicamentos y productos';

-- --------------------------------------------------------

--
-- Table structure for table `proveedor_historial_cambio`
--

CREATE TABLE `proveedor_historial_cambio` (
  `id` int NOT NULL,
  `proveedor_id` int NOT NULL,
  `campo_modificado` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `valor_anterior` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `valor_nuevo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int NOT NULL,
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
  `recovery_token_expiry` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Usuarios del sistema con roles ADMIN y VENDEDOR';

-- --------------------------------------------------------

--
-- Table structure for table `usuario_historial_acceso`
--

CREATE TABLE `usuario_historial_acceso` (
  `id` bigint NOT NULL,
  `usuario_id` bigint NOT NULL,
  `fecha_acceso` timestamp NOT NULL,
  `ip_address` varchar(100) DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  `exito` tinyint(1) DEFAULT NULL,
  `motivo_fallo` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `usuario_historial_cambio`
--

CREATE TABLE `usuario_historial_cambio` (
  `id` int NOT NULL,
  `usuario_id` int NOT NULL,
  `campo_modificado` varchar(100) DEFAULT NULL,
  `valor_anterior` text,
  `valor_nuevo` text,
  `modificado_por` int DEFAULT NULL,
  `fecha` timestamp NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ventas`
--

CREATE TABLE `ventas` (
  `id` int NOT NULL,
  `cliente_id` int DEFAULT NULL,
  `usuario_id` int NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `descuento_monto` decimal(10,2) DEFAULT '0.00',
  `igv_monto` decimal(10,2) DEFAULT '0.00',
  `total` decimal(10,2) NOT NULL,
  `tipo_pago` enum('EFECTIVO','TARJETA','TRANSFERENCIA','MIXTO') COLLATE utf8mb4_general_ci NOT NULL,
  `tipo_comprobante` enum('BOLETA','FACTURA') COLLATE utf8mb4_general_ci NOT NULL,
  `numero_boleta` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `serie` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha_venta` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` enum('REALIZADA','ANULADA','PENDIENTE') COLLATE utf8mb4_general_ci DEFAULT 'REALIZADA',
  `observaciones` text COLLATE utf8mb4_general_ci,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `venta_alertas`
--

CREATE TABLE `venta_alertas` (
  `id` int NOT NULL,
  `venta_id` int NOT NULL,
  `tipo_alerta` enum('STOCK_BAJO','VENCIMIENTO_PROXIMO','SIN_STOCK') COLLATE utf8mb4_general_ci NOT NULL,
  `producto_id` int NOT NULL,
  `mensaje` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `venta_historial_cambio`
--

CREATE TABLE `venta_historial_cambio` (
  `id` int NOT NULL,
  `venta_id` int NOT NULL,
  `tipo_cambio` enum('CREACION','ANULACION','EDICION') COLLATE utf8mb4_general_ci NOT NULL,
  `motivo` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `usuario_id` int NOT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Stand-in structure for view `vw_busqueda_productos`
-- (See below for the actual view)
--
CREATE TABLE `vw_busqueda_productos` (
`categoria` enum('ANALGESICOS','ANTIBIOTICOS','ANTIINFLAMATORIOS','VITAMINAS','ANTIACIDOS','ANTIHISTAMINICOS','ANTIHIPERTENSIVOS','DIABETES','RESPIRATORIO','DERMATOLOGIA','HIGIENE','OTROS')
,`codigo` varchar(20)
,`codigo_nombre` varchar(173)
,`concentracion` varchar(50)
,`descripcion` text
,`forma_farmaceutica` varchar(20)
,`id` int
,`nombre` varchar(150)
,`precio_venta` decimal(10,2)
,`principio_activo` varchar(100)
,`requiere_receta` tinyint(1)
,`stock_actual` int
,`texto_busqueda` varchar(302)
,`ubicacion` varchar(20)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `vw_inventario_alertas`
-- (See below for the actual view)
--
CREATE TABLE `vw_inventario_alertas` (
`fecha_vencimiento` date
,`id_producto` int
,`nombre_producto` varchar(150)
,`stock_actual` int
,`stock_minimo` int
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `vw_resumen_ventas`
-- (See below for the actual view)
--
CREATE TABLE `vw_resumen_ventas` (
`cliente` varchar(201)
,`descuento_monto` decimal(10,2)
,`estado` enum('REALIZADA','ANULADA','PENDIENTE')
,`fecha_venta` datetime
,`id` int
,`numero_boleta` varchar(20)
,`serie` varchar(10)
,`subtotal` decimal(10,2)
,`tipo_pago` enum('EFECTIVO','TARJETA','TRANSFERENCIA','MIXTO')
,`total` decimal(10,2)
,`vendedor` varchar(201)
);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `audit_log`
--
ALTER TABLE `audit_log`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_usuario` (`usuario_id`),
  ADD KEY `idx_timestamp` (`timestamp`),
  ADD KEY `idx_tabla` (`tabla_afectada`),
  ADD KEY `idx_accion` (`accion`);

--
-- Indexes for table `carrito_ventas`
--
ALTER TABLE `carrito_ventas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Indexes for table `clientes`
--
ALTER TABLE `clientes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `dni` (`dni`),
  ADD KEY `idx_dni` (`dni`),
  ADD KEY `idx_nombres` (`nombres`,`apellidos`),
  ADD KEY `idx_puntos` (`puntos_disponibles`),
  ADD KEY `idx_frecuente` (`es_frecuente`),
  ADD KEY `idx_clientes_puntos_frecuente` (`puntos_disponibles`,`es_frecuente`);

--
-- Indexes for table `cliente_historial_cambio`
--
ALTER TABLE `cliente_historial_cambio`
  ADD PRIMARY KEY (`id`),
  ADD KEY `cliente_id` (`cliente_id`);

--
-- Indexes for table `comprobantes`
--
ALTER TABLE `comprobantes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `venta_id` (`venta_id`);

--
-- Indexes for table `configuracion`
--
ALTER TABLE `configuracion`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `clave` (`clave`),
  ADD KEY `updated_by` (`updated_by`),
  ADD KEY `idx_clave` (`clave`),
  ADD KEY `idx_categoria` (`categoria`);

--
-- Indexes for table `configuracion_codigos`
--
ALTER TABLE `configuracion_codigos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `categoria` (`categoria`),
  ADD KEY `idx_categoria` (`categoria`);

--
-- Indexes for table `detalle_ventas`
--
ALTER TABLE `detalle_ventas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `venta_id` (`venta_id`),
  ADD KEY `producto_id` (`producto_id`);

--
-- Indexes for table `productos`
--
ALTER TABLE `productos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `codigo` (`codigo`),
  ADD UNIQUE KEY `idx_codigo` (`codigo`),
  ADD KEY `idx_nombre` (`nombre`),
  ADD KEY `idx_stock` (`stock_actual`),
  ADD KEY `idx_vencimiento` (`fecha_vencimiento`),
  ADD KEY `idx_categoria` (`categoria`),
  ADD KEY `idx_proveedor` (`proveedor_id`),
  ADD KEY `idx_activo` (`activo`),
  ADD KEY `idx_productos_categoria_activo` (`categoria`,`activo`);
ALTER TABLE `productos` ADD FULLTEXT KEY `idx_busqueda_texto` (`nombre`,`descripcion`,`principio_activo`);

--
-- Indexes for table `producto_historial_cambio`
--
ALTER TABLE `producto_historial_cambio`
  ADD PRIMARY KEY (`id`),
  ADD KEY `producto_id` (`producto_id`);

--
-- Indexes for table `proveedores`
--
ALTER TABLE `proveedores`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `ruc` (`ruc`),
  ADD UNIQUE KEY `ruc_2` (`ruc`),
  ADD UNIQUE KEY `ruc_3` (`ruc`),
  ADD KEY `idx_ruc` (`ruc`),
  ADD KEY `idx_razon_social` (`razon_social`),
  ADD KEY `idx_activo` (`activo`);

--
-- Indexes for table `proveedor_historial_cambio`
--
ALTER TABLE `proveedor_historial_cambio`
  ADD PRIMARY KEY (`id`),
  ADD KEY `proveedor_id` (`proveedor_id`);

--
-- Indexes for table `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `dni` (`dni`),
  ADD KEY `idx_username` (`username`),
  ADD KEY `idx_rol` (`rol`),
  ADD KEY `idx_activo` (`activo`),
  ADD KEY `idx_dni` (`dni`);

--
-- Indexes for table `usuario_historial_acceso`
--
ALTER TABLE `usuario_historial_acceso`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `usuario_historial_cambio`
--
ALTER TABLE `usuario_historial_cambio`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `ventas`
--
ALTER TABLE `ventas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `cliente_id` (`cliente_id`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Indexes for table `venta_alertas`
--
ALTER TABLE `venta_alertas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `venta_id` (`venta_id`),
  ADD KEY `producto_id` (`producto_id`);

--
-- Indexes for table `venta_historial_cambio`
--
ALTER TABLE `venta_historial_cambio`
  ADD PRIMARY KEY (`id`),
  ADD KEY `venta_id` (`venta_id`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `audit_log`
--
ALTER TABLE `audit_log`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `carrito_ventas`
--
ALTER TABLE `carrito_ventas`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `clientes`
--
ALTER TABLE `clientes`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `cliente_historial_cambio`
--
ALTER TABLE `cliente_historial_cambio`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `comprobantes`
--
ALTER TABLE `comprobantes`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `configuracion`
--
ALTER TABLE `configuracion`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `configuracion_codigos`
--
ALTER TABLE `configuracion_codigos`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `detalle_ventas`
--
ALTER TABLE `detalle_ventas`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `productos`
--
ALTER TABLE `productos`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `producto_historial_cambio`
--
ALTER TABLE `producto_historial_cambio`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `proveedores`
--
ALTER TABLE `proveedores`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `proveedor_historial_cambio`
--
ALTER TABLE `proveedor_historial_cambio`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `usuario_historial_acceso`
--
ALTER TABLE `usuario_historial_acceso`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `usuario_historial_cambio`
--
ALTER TABLE `usuario_historial_cambio`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ventas`
--
ALTER TABLE `ventas`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `venta_alertas`
--
ALTER TABLE `venta_alertas`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `venta_historial_cambio`
--
ALTER TABLE `venta_historial_cambio`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

-- --------------------------------------------------------

--
-- Structure for view `vw_busqueda_productos`
--
DROP TABLE IF EXISTS `vw_busqueda_productos`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_busqueda_productos`  AS SELECT `p`.`id` AS `id`, `p`.`codigo` AS `codigo`, `p`.`nombre` AS `nombre`, `p`.`descripcion` AS `descripcion`, `p`.`principio_activo` AS `principio_activo`, `p`.`concentracion` AS `concentracion`, `p`.`forma_farmaceutica` AS `forma_farmaceutica`, `p`.`precio_venta` AS `precio_venta`, `p`.`stock_actual` AS `stock_actual`, `p`.`categoria` AS `categoria`, `p`.`ubicacion` AS `ubicacion`, `p`.`requiere_receta` AS `requiere_receta`, concat(`p`.`codigo`,' - ',`p`.`nombre`) AS `codigo_nombre`, concat(`p`.`nombre`,' ',ifnull(`p`.`principio_activo`,''),' ',ifnull(`p`.`concentracion`,'')) AS `texto_busqueda` FROM `productos` AS `p` WHERE (`p`.`activo` = 1) ;

-- --------------------------------------------------------

--
-- Structure for view `vw_inventario_alertas`
--
DROP TABLE IF EXISTS `vw_inventario_alertas`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_inventario_alertas`  AS SELECT `p`.`id` AS `id_producto`, `p`.`nombre` AS `nombre_producto`, `p`.`stock_actual` AS `stock_actual`, `p`.`stock_minimo` AS `stock_minimo`, `p`.`fecha_vencimiento` AS `fecha_vencimiento` FROM `productos` AS `p` WHERE ((`p`.`activo` = 1) AND ((`p`.`stock_actual` <= `p`.`stock_minimo`) OR ((to_days(`p`.`fecha_vencimiento`) - to_days(curdate())) <= 30))) ;

-- --------------------------------------------------------

--
-- Structure for view `vw_resumen_ventas`
--
DROP TABLE IF EXISTS `vw_resumen_ventas`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_resumen_ventas`  AS SELECT `v`.`id` AS `id`, `v`.`numero_boleta` AS `numero_boleta`, `v`.`serie` AS `serie`, `v`.`fecha_venta` AS `fecha_venta`, concat(ifnull(`c`.`nombres`,'SIN'),' ',ifnull(`c`.`apellidos`,'CLIENTE')) AS `cliente`, concat(`u`.`nombres`,' ',`u`.`apellidos`) AS `vendedor`, `v`.`subtotal` AS `subtotal`, `v`.`descuento_monto` AS `descuento_monto`, `v`.`total` AS `total`, `v`.`tipo_pago` AS `tipo_pago`, `v`.`estado` AS `estado` FROM ((`ventas` `v` left join `clientes` `c` on((`v`.`cliente_id` = `c`.`id`))) join `usuarios` `u` on((`v`.`usuario_id` = `u`.`id`))) ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `audit_log`
--
ALTER TABLE `audit_log`
  ADD CONSTRAINT `audit_log_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `carrito_ventas`
--
ALTER TABLE `carrito_ventas`
  ADD CONSTRAINT `carrito_ventas_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`);

--
-- Constraints for table `cliente_historial_cambio`
--
ALTER TABLE `cliente_historial_cambio`
  ADD CONSTRAINT `cliente_historial_cambio_ibfk_1` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`);

--
-- Constraints for table `comprobantes`
--
ALTER TABLE `comprobantes`
  ADD CONSTRAINT `comprobantes_ibfk_1` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `configuracion`
--
ALTER TABLE `configuracion`
  ADD CONSTRAINT `configuracion_ibfk_1` FOREIGN KEY (`updated_by`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `detalle_ventas`
--
ALTER TABLE `detalle_ventas`
  ADD CONSTRAINT `detalle_ventas_ibfk_1` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `detalle_ventas_ibfk_2` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`);

--
-- Constraints for table `productos`
--
ALTER TABLE `productos`
  ADD CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`proveedor_id`) REFERENCES `proveedores` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `producto_historial_cambio`
--
ALTER TABLE `producto_historial_cambio`
  ADD CONSTRAINT `producto_historial_cambio_ibfk_1` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`);

--
-- Constraints for table `proveedor_historial_cambio`
--
ALTER TABLE `proveedor_historial_cambio`
  ADD CONSTRAINT `proveedor_historial_cambio_ibfk_1` FOREIGN KEY (`proveedor_id`) REFERENCES `proveedores` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `ventas`
--
ALTER TABLE `ventas`
  ADD CONSTRAINT `ventas_ibfk_1` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`),
  ADD CONSTRAINT `ventas_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`);

--
-- Constraints for table `venta_alertas`
--
ALTER TABLE `venta_alertas`
  ADD CONSTRAINT `venta_alertas_ibfk_1` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `venta_alertas_ibfk_2` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`);

--
-- Constraints for table `venta_historial_cambio`
--
ALTER TABLE `venta_historial_cambio`
  ADD CONSTRAINT `venta_historial_cambio_ibfk_1` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `venta_historial_cambio_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
