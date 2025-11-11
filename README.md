💊 Sistema PharmaVictoria

Sistema integral de gestión para farmacias, desarrollado en JavaFX, orientado a optimizar todos los procesos clave del negocio farmacéutico.
Cuenta con una interfaz moderna, lógica modular y control de accesos según roles, garantizando eficiencia, seguridad y trazabilidad en las operaciones diarias.

🧠 Descripción general

PharmaVictoria permite administrar inventarios, ventas, clientes, proveedores y usuarios, además de generar reportes detallados y configuraciones personalizadas.
Cada módulo está diseñado para ofrecer una experiencia fluida, con controles visuales, validaciones y auditorías de acciones.

El sistema diferencia los permisos y vistas según el rol del usuario:

🧑‍💼 Administrador: acceso completo a todos los módulos y funciones.

👩‍🔬 Vendedor: acceso restringido a ventas, inventario y clientes.

🔐 Login y Roles

Inicio de sesión con validación de credenciales (administrador o vendedor).

Recuperación de contraseña mediante envío de código a Gmail.

Gestión segura de contraseñas con BCrypt.

🏠 Dashboard (Menú Principal)

Administrador:

Cards con ventas del día, total de productos y total de clientes.

Vista de últimas ventas y productos con stock bajo.

Vendedor:

Cards personalizadas de mis ventas.

Vista simplificada centrada en su actividad diaria.

🧾 Módulo Inventario

Gestión completa y detallada del inventario farmacéutico.

Agregar, editar, ver, activar/inactivar y eliminar productos.

Historial de auditoría por producto.

Exportación del inventario a PDF o Excel.

Edición masiva de múltiples productos.

Cards de resumen: total de productos, stock bajo y próximos a vencer.

Búsquedas y filtros por nombre, categoría, etc.

Paginación dinámica para grandes volúmenes.

Restricción para vendedores: solo pueden visualizar detalles de productos.

🚚 Módulo Proveedores

Gestión integral de proveedores y análisis de sus productos.

Agregar, editar, ver, activar/inactivar y eliminar proveedores.

Historial de auditoría y contacto directo por correo.

Exportación a PDF o Excel.

Dashboard con:

Gráfico de proveedores con más productos.

Gráfico de pastel por estado.

Filtros avanzados de búsqueda y paginación.

Restricción para vendedores: solo pueden ver detalles.

👥 Módulo Clientes

Control y seguimiento de clientes naturales y empresariales.

Agregar, editar, ver, y eliminar clientes.

Historial de auditoría por cliente.

Exportación de lista de clientes.

Búsqueda avanzada y filtros por tipo o nombre.

Gráficos de pastel y Top 3 clientes con más compras.

Paginación eficiente.

Restricción para vendedores: solo visualización de detalles y gráficos.

👤 Módulo Usuarios

Administración completa de usuarios del sistema.

Agregar, editar, ver, y eliminar usuarios (según rol).

Historial de auditoría por usuario.

Exportación de registros.

Búsqueda avanzada y paginación.

Contraseñas cifradas con BCrypt.

Acceso exclusivo del administrador.

💳 Módulo Punto de Ventas

Diseñado para registrar ventas de forma rápida, precisa y automatizada.

Búsqueda de productos con aviso de stock bajo o vencimiento próximo.

Cálculo automático del total, selección de cliente y método de pago.

Emisión de boleta o factura electrónica (integración con NubeFact – SUNAT).

Vista previa del comprobante antes de confirmar la venta.

Al confirmar, se muestra un diálogo con opciones para ver PDF, imprimir comprobante, enviar por WhatsApp o por correo electrónico.

Anulación de ventas disponible dentro de las 24 horas posteriores a la transacción.

Descuento automático de stock al realizar una venta; reversión automática al anularla.

Sistema de puntos integrado: por cada S/ 1.00 en compras, el cliente acumula 1 punto.
Al alcanzar 100 puntos, podrá canjearlos como S/ 1.00 de descuento en futuras compras, incentivando la fidelidad del cliente.

Accesible tanto para administradores como vendedores.

📊 Módulo Reportes

Análisis visual de ventas y rendimiento.

Cards de ventas totales y productos más vendidos.

Reportes por intervalo de tiempo o por producto.

Exportación de resultados en PDF y Excel.

Gráficos estadísticos y resúmenes dinámicos.

Acceso exclusivo del administrador.

⚙️ Módulo Configuración

Permite personalizar el comportamiento del sistema y las alertas automáticas.

Cards de configuración visuales.

Notificaciones por correo: el usuario puede ingresar su Gmail y contraseña de aplicación para recibir alertas de stock bajo o productos próximos a vencer.

Alertas de vencimiento: configuración del intervalo de tiempo para recibir notificaciones anticipadas.

Acceso exclusivo del administrador.

🖥️ Tecnologías utilizadas

Lenguaje: Java 21

Framework: JavaFX

Gestor de dependencias: Maven

Base de datos: MySQL

Integraciones: NubeFact (SUNAT), Gmail API (notificaciones)

Seguridad: BCrypt (encriptación de contraseñas)

👨‍💻 Desarrollador

Desarrollado por: Jesus Loza
📧 Correo: lozayataco@gmail.com

💻 Repositorio: GitHub – Sistema PharmaVictoria
