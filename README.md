💊 **Sistema PharmaVictoria**

Sistema integral de gestión para farmacias, desarrollado en JavaFX, orientado a optimizar todos los procesos clave del negocio farmacéutico.
Cuenta con una interfaz moderna, lógica modular y control de accesos según roles, garantizando eficiencia, seguridad y trazabilidad en las operaciones diarias.

---

## 🧠 Descripción general

PharmaVictoria permite administrar:

- Inventarios
- Ventas
- Clientes
- Proveedores
- Usuarios
- Reportes detallados
- Configuraciones personalizadas

Cada módulo está diseñado para ofrecer una experiencia fluida, con controles visuales, validaciones y auditorías de acciones.

---

## 👥 Roles y permisos

- **Administrador:**
  - Acceso completo a todos los módulos y funciones.
- **Vendedor:**
  - Acceso restringido a ventas, inventario y clientes.

---

## 🔐 Login y Seguridad

- Inicio de sesión con validación de credenciales (administrador o vendedor)
- Recuperación de contraseña mediante envío de código a Gmail
- Contraseñas cifradas con BCrypt

---

## 🏠 Dashboard (Menú Principal)

- **Administrador:**
  - Cards con ventas del día, total de productos y total de clientes
  - Vista de últimas ventas y productos con stock bajo
- **Vendedor:**
  - Cards personalizadas de mis ventas
  - Vista simplificada centrada en su actividad diaria

---

## 🧾 Módulo Inventario

- Gestión completa y detallada del inventario farmacéutico
- Funciones:
  - Agregar, editar, ver, activar/inactivar y eliminar productos
  - Historial de auditoría por producto
  - Exportación a PDF o Excel
  - Edición masiva de productos
  - Cards de resumen: total de productos, stock bajo y próximos a vencer
  - Búsquedas y filtros por nombre, categoría, etc.
  - Paginación dinámica para grandes volúmenes
- Restricción para vendedores: solo visualización de detalles

---

## 🚚 Módulo Proveedores

- Gestión integral de proveedores y análisis de sus productos
- Funciones:
  - Agregar, editar, ver, activar/inactivar y eliminar proveedores
  - Historial de auditoría y contacto directo por correo
  - Exportación a PDF o Excel
  - Dashboard con gráficos (proveedores con más productos, pastel por estado)
  - Filtros avanzados y paginación
- Restricción para vendedores: solo visualización de detalles

---

## 👥 Módulo Clientes

- Control y seguimiento de clientes naturales y empresariales
- Funciones:
  - Agregar, editar, ver y eliminar clientes
  - Historial de auditoría por cliente
  - Exportación de lista de clientes
  - Búsqueda avanzada y filtros por tipo o nombre
  - Gráficos de pastel y Top 3 clientes con más compras
  - Paginación eficiente
- Restricción para vendedores: solo visualización de detalles y gráficos

---

## 👤 Módulo Usuarios

- Administración completa de usuarios del sistema
- Funciones:
  - Agregar, editar, ver y eliminar usuarios (según rol)
  - Historial de auditoría por usuario
  - Exportación de registros
  - Búsqueda avanzada y paginación
  - Contraseñas cifradas con BCrypt
- Acceso exclusivo del administrador

---

## 💳 Módulo Punto de Ventas

- Registro de ventas rápido, preciso y automatizado
- Funciones:
  - Búsqueda de productos con aviso de stock bajo o vencimiento próximo
  - Cálculo automático del total, selección de cliente y método de pago
  - Emisión de boleta o factura electrónica (NubeFact – SUNAT)
  - Vista previa del comprobante antes de confirmar la venta
  - Diálogo de confirmación: ver PDF, imprimir, enviar por WhatsApp o correo
  - Anulación de ventas dentro de las 24 horas
  - Descuento automático de stock al vender; reversión automática al anular
  - Sistema de puntos: 1 punto por cada S/ 1.00 en compras; canje de 100 puntos por S/ 1.00 de descuento
- Accesible para administradores y vendedores

---

## 📊 Módulo Reportes

- Análisis visual de ventas y rendimiento
- Funciones:
  - Cards de ventas totales y productos más vendidos
  - Reportes por intervalo de tiempo o por producto
  - Exportación en PDF y Excel
  - Gráficos estadísticos y resúmenes dinámicos
- Acceso exclusivo del administrador

---

## ⚙️ Módulo Configuración

- Personalización del sistema y alertas automáticas
- Funciones:
  - Cards de configuración visuales
  - Notificaciones por correo (Gmail y contraseña de aplicación)
  - Alertas de vencimiento configurables
- Acceso exclusivo del administrador

---

## 🖥️ Tecnologías utilizadas

- **Lenguaje:** Java 21
- **Framework:** JavaFX
- **Gestor de dependencias:** Maven
- **Base de datos:** MySQL
- **Integraciones:** NubeFact (SUNAT), Gmail API (notificaciones)
- **Seguridad:** BCrypt (encriptación de contraseñas)

---

## 👨‍💻 Desarrollador

- **Desarrollado por:** Jesus Loza
- **Correo:** lozayataco@gmail.com
- **Repositorio:** [GitHub – Sistema PharmaVictoria](https://github.com/JesusLoza1506/Sistema-PharmaVictoria)
