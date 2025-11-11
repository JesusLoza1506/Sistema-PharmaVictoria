# Sistema-PharmaVictoria

Sistema de gestión para farmacia, desarrollado en JavaFX. Permite administrar ventas, inventario, clientes, proveedores y usuarios, con interfaz moderna y funcionalidades avanzadas para el sector farmacéutico.

## Características principales

- Gestión de ventas y comprobantes electrónicos
- Control de inventario y stock bajo
- Administración de clientes y proveedores
- Módulo de usuarios y permisos
- Notificaciones por correo
- Reportes y estadísticas

## Requisitos

- Java 21
- Maven
- MySQL

## Instalación

1. Clona el repositorio:
   ```
   git clone https://github.com/JesusLoza1506/Sistema-PharmaVictoria.git
   ```
2. Configura la base de datos MySQL y actualiza los parámetros en `src/main/resources/application.properties`.
3. Instala las dependencias:
   ```
   mvn clean compile
   mvn dependency:copy-dependencies
   ```

## Ejecución

Puedes iniciar la aplicación con el script:

```
run-pharmavictoria.bat
```

O manualmente:

```
mvn javafx:run
```

## Uso

Accede con usuario administrador para configurar y comenzar a operar el sistema.

## Contacto

Desarrollado por Jesus Loza
Email: lozayataco@gmail.com
Repositorio: [GitHub](https://github.com/JesusLoza1506/Sistema-PharmaVictoria)
