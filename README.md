<div align="center">

<img src="docs/images/logo.png" alt="Logo PharmaVictoria" width="140"/>

# 💊 PharmaVictoria

**Sistema de gestión integral para farmacias**
Aplicación de escritorio para controlar ventas, inventario, clientes, proveedores y reportes.

![Java 21](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![JavaFX 21.0.1](https://img.shields.io/badge/JavaFX-21.0.1-blue)
![MySQL 8.0.44](https://img.shields.io/badge/MySQL-8.0.44-4479A1?logo=mysql&logoColor=white)
![HikariCP 5.0.1](https://img.shields.io/badge/HikariCP-5.0.1-555555)
![Maven 3.9.11](https://img.shields.io/badge/Maven-3.9.11-C71A36?logo=apachemaven&logoColor=white)
[![CI · PharmaVictoria](https://github.com/JesusLoza1506/Sistema-PharmaVictoria/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/JesusLoza1506/Sistema-PharmaVictoria/actions/workflows/ci.yml)

<img src="docs/images/screenshots/02-dashboard.png" alt="Dashboard de PharmaVictoria" width="850"/>

</div>

---

## 📑 Tabla de contenido

1. [Descripción](#descripcion)
2. [Características principales](#caracteristicas)
3. [Capturas de pantalla](#capturas)
4. [Módulos](#modulos)
5. [Roles y permisos](#roles-y-permisos)
6. [Arquitectura](#arquitectura)
7. [Tecnologías](#tecnologias)
8. [Estructura del proyecto](#estructura)
9. [Instalación y ejecución](#instalacion)
10. [Configuración](#configuracion)
11. [Base de datos](#base-de-datos)
12. [Pruebas y CI/CD](#pruebas-y-ci-cd)
13. [Documentación](#documentacion)
14. [Hoja de ruta](#hoja-de-ruta)
15. [Equipo](#equipo)
16. [Licencia](#licencia)

---

<a name="descripcion"></a>

## 📖 Descripción

**PharmaVictoria** es una aplicación de escritorio desarrollada en **Java con JavaFX** y **MySQL**, pensada para la gestión diaria de una farmacia local: registrar ventas, controlar el stock y los vencimientos, administrar clientes y proveedores, emitir comprobantes y generar reportes.

El proyecto nace del caso real de la farmacia **PharmaVictoria** (distrito de Imperial, provincia de Cañete, Perú), que cuenta con dos sucursales y un equipo reducido por sede. Además, es el trabajo de investigación del curso de **Arquitectura de Software** de la **Universidad Nacional de Cañete (UNCAÑETE)**, donde se aplican **patrones de diseño GoF** y el **modelo C4** para documentar la arquitectura.

**Objetivos**

- Digitalizar y centralizar las operaciones de la farmacia.
- Mejorar la trazabilidad del inventario y la precisión de la información.
- Mantener una arquitectura modular, segura y escalable.

---

<a name="caracteristicas"></a>

## ✨ Características principales

- 📦 **Inventario** con lotes, fechas de vencimiento, stock mínimo, edición masiva e historial de cambios.
- 🛒 **Punto de venta** con carrito, pagos mixtos y comprobantes (boleta, factura y ticket).
- 🧾 **Facturación electrónica** mediante integración con NubeFacT (descarga de PDF/XML).
- 👥 **Clientes** naturales y empresas, con **puntos canjeables** (hasta el 50 % del total).
- 🚚 **Proveedores** con contacto por correo desde el sistema.
- 📊 **Reportes** de ventas por período, producto y usuario, exportables a **PDF y Excel**.
- 🔐 **Roles y permisos** granulares (Administrador / Vendedor).
- 🕵️ **Auditoría**: historial de cambios y de accesos.
- 💾 **Respaldos y restauración** de la base de datos.
- 🔔 **Alertas** por correo de stock bajo y productos próximos a vencer.

---

<a name="capturas"></a>

## 🖼️ Capturas de pantalla

|                Inicio de sesión                |                       Dashboard                        |                        Punto de venta                         |
| :--------------------------------------------: | :----------------------------------------------------: | :-----------------------------------------------------------: |
| ![Login](docs/images/screenshots/01-login.png) | ![Dashboard](docs/images/screenshots/02-dashboard.png) | ![Punto de venta](docs/images/screenshots/03-punto-venta.png) |

|                        Inventario                        |                           Detalle de producto                           |                       Clientes                       |
| :------------------------------------------------------: | :---------------------------------------------------------------------: | :--------------------------------------------------: |
| ![Inventario](docs/images/screenshots/04-inventario.png) | ![Detalle de producto](docs/images/screenshots/05-detalle-producto.png) | ![Clientes](docs/images/screenshots/06-clientes.png) |

|                        Proveedores                         |                       Reportes                       |                    Configuración y permisos                    |
| :--------------------------------------------------------: | :--------------------------------------------------: | :------------------------------------------------------------: |
| ![Proveedores](docs/images/screenshots/07-proveedores.png) | ![Reportes](docs/images/screenshots/08-reportes.png) | ![Configuración](docs/images/screenshots/09-configuracion.png) |

**Demostración del punto de venta**

![Flujo de venta](docs/images/demo-venta.gif)

> ⚠️ Las capturas usan **datos ficticios**.

---

<a name="modulos"></a>

## 🧩 Módulos

### 🔑 Login y recuperación de contraseña

Acceso con usuario y contraseña, opción de mostrar u ocultar la clave, estado de la conexión a la base de datos, versión de la app y sistema operativo detectado. Si el usuario olvida su contraseña, recibe un **código por correo (válido 10 minutos)** para restablecerla.

### 🏠 Dashboard

Resumen del día: ventas, cantidad de productos (y cuántos tienen stock bajo) y clientes. El administrador además ve las **últimas ventas** y la tabla de **stock bajo**.

### 📦 Inventario

Alta, edición, activación/inactivación y eliminación de productos. Incluye generación automática de código, cálculo de margen, control de receta y producto controlado, **edición masiva**, filtros y búsqueda, exportación a Excel/PDF, estadísticas y una vista de detalle de solo lectura con **historial de cambios**.

### 🚚 Proveedores

Registro y edición, productos asociados, condiciones de pago, **envío de correos a uno o varios proveedores**, exportación y gráficos (top de proveedores con más productos y distribución por estado).

### 👥 Clientes

Clientes naturales (DNI) y empresas (RUC), historial de cambios, **historial de puntos** y de compras, exportación y gráficos (top 3 clientes con más compras y distribución por rango de edad).

### 🛒 Punto de venta

Búsqueda de productos en tiempo real, validación de stock y vencimiento, cálculo automático de subtotal, IGV y descuento, pagos (efectivo, tarjeta, transferencia o mixto), canje de puntos, confirmación de venta, historial de las últimas 24 h y **anulación de ventas**.

### 📈 Reportes _(administrador)_

- Ventas por período
- Ventas por producto
- Ventas por usuario

Con filtros por fecha y exportación.

### 🧑‍💼 Usuarios _(administrador)_

Alta, edición, activación/inactivación y eliminación de usuarios, con rol **ADMIN** o **VENDEDOR**. Cada usuario tiene historial de accesos (fecha, IP y dispositivo) y auditoría de cambios.

### ⚙️ Configuración _(administrador)_

- Notificaciones por correo
- Permisos del vendedor
- Respaldos y restauración
- Alerta de vencimiento (días de anticipación)

---

<a name="roles-y-permisos"></a>

## 🛡️ Roles y permisos

| Capacidad                                                      | Administrador |           Vendedor            |
| -------------------------------------------------------------- | :-----------: | :---------------------------: |
| Dashboard                                                      |  ✅ completo  | ✅ personalizado (sus ventas) |
| Punto de venta                                                 |      ✅       |              ✅               |
| Inventario (consultar y buscar)                                |      ✅       |              ✅               |
| Inventario (crear, editar, eliminar, exportar, edición masiva) |      ✅       |       ⚙️ según permisos       |
| Clientes y proveedores (gestión)                               |      ✅       |       ⚙️ según permisos       |
| Reportes                                                       |      ✅       |              ❌               |
| Usuarios                                                       |      ✅       |              ❌               |
| Configuración, respaldos y permisos                            |      ✅       |              ❌               |

⚙️ _El administrador decide qué acciones puede realizar el vendedor en cada módulo._

---

<a name="arquitectura"></a>

## 🏗️ Arquitectura

La aplicación sigue una **arquitectura en capas (MVC)** y está documentada con el **modelo C4**.

```
Controladores (JavaFX)  →  Servicios (lógica de negocio)  →  Repositorios  →  MySQL
```

### Diagramas C4

| Nivel            | Diagrama                                              |
| ---------------- | ----------------------------------------------------- |
| 1 · Contexto     | ![C4 Nivel 1](docs/images/c4/nivel1-contexto.png)     |
| 2 · Contenedores | ![C4 Nivel 2](docs/images/c4/nivel2-contenedores.png) |
| 3 · Componentes  | ![C4 Nivel 3](docs/images/c4/nivel3-componentes.png)  |
| 4 · Código       | ![C4 Nivel 4](docs/images/c4/nivel4-codigo.png)       |

### Patrones de diseño (GoF)

| Patrón         | Tipo           | Clase                                       | Propósito                                                  |
| -------------- | -------------- | ------------------------------------------- | ---------------------------------------------------------- |
| Singleton      | Creacional     | `DatabaseConfig`                            | Una única configuración y pool de conexiones (HikariCP)    |
| Factory Method | Creacional     | `ComprobanteFactory`                        | Crear distintos tipos de comprobantes                      |
| Builder        | Creacional     | `BoletaBuilder`                             | Construir boletas paso a paso                              |
| Facade         | Estructural    | `ExportFacade`                              | Interfaz única para exportar a Excel y PDF                 |
| Command        | Comportamiento | `InventarioCommand`, `AjusteStockCommand`   | Encapsular operaciones de inventario y registrar auditoría |
| Observer       | Comportamiento | `SystemEventManager`, `SystemEventObserver` | Actualizar la interfaz ante eventos del sistema            |
| Strategy       | Comportamiento | `SearchByVencimientoStrategy` y otras       | Búsquedas y filtros intercambiables                        |

---

<a name="tecnologias"></a>

## 🛠️ Tecnologías

| Área | Tecnología | Versión |
| --- | --- | --- |
| Lenguaje | Java | 21 LTS |
| Interfaz | JavaFX (FXML + CSS) | 21.0.1 |
| Base de datos | MySQL Server | 8.0.44, versión probada |
| Conexión JDBC | MySQL Connector/J | 8.0.33 |
| Pool de conexiones | HikariCP | 5.0.1 |
| Compilación y dependencias | Maven | 3.9.11, versión probada |
| Exportación Excel | Apache POI | 5.2.3 |
| Generación y manejo de PDF | iText y Apache PDFBox | 5.5.13.3 y 2.0.29 |
| Pruebas | JUnit Jupiter y Mockito | 5.10.0 y 5.5.0 |
| Cobertura | JaCoCo | 0.8.11 |
| Registro de eventos | SLF4J y Logback | 2.0.9 y 1.4.11 |
| Facturación electrónica | NubeFacT | Servicio externo |

---

<a name="estructura"></a>

## 📂 Estructura del proyecto

```text
Sistema-PharmaVictoria/
├── .github/workflows/
│   ├── ci.yml
│   └── windows-release.yml
├── docs/
│   └── images/
│       ├── c4/
│       └── screenshots/
├── scripts/
│   ├── pharmavictoria.sql
│   ├── migracion_add_es_rapido_clientes.sql
│   ├── build-portable.ps1
│   └── portable/
│       ├── config/application.properties.example
│       ├── Iniciar-PharmaVictoria.bat
│       └── LEEME.txt
├── src/
│   ├── main/
│   │   ├── java/                  # Controladores, servicios, repositorios y modelos
│   │   └── resources/             # FXML, CSS e íconos
│   └── test/java/                 # Pruebas automatizadas
├── pom.xml
└── README.md
```

---

<a name="instalacion"></a>

## 🚀 Instalación y ejecución

### Opción 1. Paquete portátil para Windows x64

1. Abre el [workflow de Windows](https://github.com/JesusLoza1506/Sistema-PharmaVictoria/actions/workflows/windows-release.yml) y descarga el artefacto `PharmaVictoria-Windows-x64` desde una ejecución exitosa.
2. Extrae el paquete. Si la descarga contiene otro ZIP con el mismo nombre, extráelo también.
3. Copia `config/application.properties.example` como `config/application.properties` dentro del paquete y completa los datos de MySQL.
4. Con MySQL en ejecución y una base PharmaVictoria preparada, abre `Iniciar-PharmaVictoria.bat`.

El paquete incluye Java y JavaFX. Puedes consultar los pasos en el [LEEME del paquete portátil](scripts/portable/LEEME.txt).

### Opción 2. Ejecutar desde el código fuente

Necesitas **Git, JDK 21, Maven y acceso a MySQL 8**. El proyecto se probó con Maven 3.9.11 y MySQL Server 8.0.44.

**1. Clona el repositorio desde PowerShell.**

```powershell
git clone https://github.com/JesusLoza1506/Sistema-PharmaVictoria.git
Set-Location Sistema-PharmaVictoria
mvn -version
```

**2. Prepara una base de desarrollo.** Desde la raíz del repositorio, abre el cliente MySQL. Ajusta el host, el puerto y el usuario a tu instalación.

```powershell
mysql -h 127.0.0.1 -P 3306 -u root -p
```

En el cliente MySQL, ejecuta lo siguiente para una **base nueva**:

```sql
CREATE DATABASE pharmavictoria CHARACTER SET utf8mb4;
USE pharmavictoria;
SOURCE scripts/pharmavictoria.sql;
SOURCE scripts/migracion_add_es_rapido_clientes.sql;
EXIT;
```

El esquema incluye vistas con un definidor `root@localhost`. Si tu servidor usa otra cuenta para las vistas, ajusta ese definidor para tu entorno antes de importar. En una base existente, comprueba si `clientes.es_rapido` ya existe antes de aplicar la migración.

**3. Configura la aplicación.** Si todavía no tienes el archivo local, créalo a partir de la plantilla.

```powershell
if (-not (Test-Path 'src/main/resources/application.properties')) {
    Copy-Item 'scripts/portable/config/application.properties.example' 'src/main/resources/application.properties'
}
```

Edita `src/main/resources/application.properties` con los datos de tu conexión. Si MySQL escucha en el puerto 3307, cámbialo en `db.url`.

**4. Inicia PharmaVictoria.**

```powershell
mvn clean javafx:run
```

Para iniciar sesión, usa una cuenta registrada en la base. El esquema del repositorio define la estructura de la base y requiere que prepares las cuentas de prueba por separado.

---

<a name="configuracion"></a>

## ⚙️ Configuración

Usa `src/main/resources/application.properties` al ejecutar desde el código fuente y `config/application.properties` en la distribución portátil. Ambos parten de la [plantilla sin credenciales](scripts/portable/config/application.properties.example).

```properties
db.url=jdbc:mysql://localhost:3306/pharmavictoria?serverTimezone=America/Lima
db.username=
db.password=
db.driver=com.mysql.cj.jdbc.Driver
```

Los archivos de configuración con credenciales se conservan localmente. El correo, NubeFacT y Google Drive requieren su propia configuración si se usan esas integraciones. Desde el módulo **Configuración** también puedes gestionar alertas, permisos del vendedor y respaldos.

---

<a name="base-de-datos"></a>

## 🗄️ Base de datos

![Diagrama ER](docs/images/diagrama-er.png)

| Tablas | Contenido |
| --- | --- |
| `usuarios`, `usuario_permisos`, `usuario_historial_acceso` | Cuentas, permisos y registro de accesos |
| `productos`, `producto_historial_cambio` | Catálogo, stock, lotes, vencimientos e historial |
| `clientes`, `cliente_puntos_historial` | Clientes y movimientos de puntos |
| `proveedores`, `proveedor_historial_cambio` | Proveedores e historial de cambios |
| `ventas`, `detalle_ventas`, `detalle_pago_venta` | Transacciones, productos vendidos y medios de pago |
| `comprobantes`, `venta_historial_cambio` | Comprobantes y trazabilidad de ventas |

- [Esquema MySQL](scripts/pharmavictoria.sql)
- [Migración para clientes rápidos](scripts/migracion_add_es_rapido_clientes.sql)

---

<a name="pruebas-y-ci-cd"></a>

## 🧪 Pruebas y CI/CD

Para reproducir en tu equipo la compilación de CI:

```powershell
mvn -Pportable-ci clean verify
```

En la verificación de integración pasaron **137 pruebas**. La suite incluye pruebas de totales de venta, filtros de inventario, puntos y reportes. El reporte de cobertura local se genera en `target/site/jacoco/index.html`.

| Workflow | Qué hace |
| --- | --- |
| [CI · PharmaVictoria](https://github.com/JesusLoza1506/Sistema-PharmaVictoria/actions/workflows/ci.yml) | Compila con Java 21, ejecuta las pruebas de Maven y conserva los reportes de pruebas y cobertura. |
| [CD · PharmaVictoria Windows](https://github.com/JesusLoza1506/Sistema-PharmaVictoria/actions/workflows/windows-release.yml) | Genera el ZIP portátil para Windows x64. Una ejecución manual deja el ZIP como artefacto. Al subir una etiqueta `v*`, el workflow publica el paquete en GitHub Releases si la compilación termina correctamente. |

---

<a name="documentacion"></a>

## 📚 Documentación

- [Diagramas de arquitectura C4](#arquitectura)
- [Diagrama entidad-relación y esquema de base de datos](#base-de-datos)
- [Guía del paquete portátil para Windows](scripts/portable/LEEME.txt)

- [Manual de usuario · Administrador](docs/manuales/ManualUsuario-PharmaVictoriaAdministrador.pdf)
- [Manual de usuario · Vendedor](docs/manuales/ManualUsuario-PharmaVictoriaVendedor.pdf)
- [Informe de Arquitectura de Software](docs/informe/ProyectoFinalArquitectura.pdf)

---

<a name="hoja-de-ruta"></a>

## 🗺️ Hoja de ruta

- [x] Módulos de login, inventario, clientes, proveedores, ventas, reportes, usuarios y configuración
- [x] Exportación a PDF y Excel
- [x] Auditoría y respaldos
- [ ] Sincronización entre sucursales
- [ ] Integración con DIGEMID para validación de medicamentos
- [ ] Función de "deshacer" en el patrón Command
- [ ] Borrado lógico (en lugar de eliminación permanente)
- [x] Suite automatizada con JUnit y Mockito en GitHub Actions
- [x] Generación del paquete portátil de Windows mediante GitHub Actions
- [ ] Verificación de flujos completos desde el paquete portátil
- [ ] Diagrama de despliegue y de secuencia (venta → inventario → NubeFacT)

---

<a name="equipo"></a>

## 👨‍💻 Equipo

**Universidad Nacional de Cañete · Facultad de Ingeniería · Escuela Profesional de Ingeniería de Sistemas**
Curso: Arquitectura de Software · Docente: Luis Enrique Ramirez Pacheco

| Integrante                       |
| -------------------------------- |
| Esteban Ricardo Puma Palomino    |
| Jesús Ángel Loza Yataco          |
| Jean Paul Barrientos Atocsa      |
| Augusto Salvador Solano Ayaucán  |
| Oliver Francisco Agüela Palomino |

San Vicente de Cañete, Perú · 2025

---

<a name="licencia"></a>

## 📄 Licencia

Licencia pendiente de definir.
