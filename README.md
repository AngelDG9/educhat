# EDUCHAT

## 📝 Descripción

**Educhat** es un sistema de gestión académica distribuido, desarrollado como un proyecto universitario. El sistema implementa una arquitectura cliente-servidor utilizando **Java RMI** (Remote Method Invocation) para la comunicación remota y **PostgreSQL** como base de datos para la persistencia de la información.

## ✨ Características

-   **Arquitectura Distribuida:** Sistema basado en el modelo cliente-servidor con Java RMI.
    
-   **Persistencia de Datos:** Uso de una base de datos relacional PostgreSQL.
    
-   **Gestión por Roles:** Tres tipos de usuarios con permisos y funcionalidades específicas:
    
    -   👤 **Administradores:** Creación, borrado y asignación de asignaturas.
        
    -   🧑‍🏫 **Profesores:** Envío de notificaciones y gestión de sus asignaturas.
        
    -   🎓 **Alumnos:** Matriculación en asignaturas y recepción de notificaciones.
        
-   **Sistema de Notificaciones:** Implementado con un patrón Publicador-Suscriptor.
    
-   **Gestión Académica:** Funcionalidades para matricularse, consultar asignaturas, etc.
    
-   **Script de Automatización:** Incluye un script en Bash (`run.sh`) para facilitar el despliegue del servidor y la gestión de la base de datos.
    

## 📋 Requisitos Previos

-   Java Development Kit (JDK).
    
-   PostgreSQL instalado y en ejecución.
    
-   Un usuario `dit` con contraseña `dit` configurado en PostgreSQL (configuración por defecto en las VMs de la US).
    
-   El driver de PostgreSQL JDBC (incluido en el proyecto en `libs/postgresql-42.7.3.jar`).
    

## 🚀 Instalación y Ejecución

### 1\. Clonar el Repositorio (Ejemplo)

    git clone https://github.com/AngelDG9/educhat.git
    cd rmi-academico
    

### 2\. Ejecutar el Servidor

El script `run.sh` compila el proyecto, inicia el registro RMI y despliega el servidor.

    # Dar permisos de ejecución al script
    chmod +x run.sh
    
    # Iniciar el servidor
    ./run.sh
    

> **Nota:** Para reiniciar la base de datos y eliminar todos los datos existentes, ejecuta el script con la opción `--reset` o `-r`.
> 
>     ./run.sh --reset
>     

### 3\. Ejecutar el Cliente

Abre una **nueva terminal** y ejecuta el siguiente comando desde el directorio raíz del proyecto para iniciar la aplicación cliente.

    java -cp .:libs/postgresql-42.7.3.jar clientes.ClienteAcademico
    

## 📂 Estructura del Proyecto

    .
    ├── run.sh              # Script para compilar y ejecutar el servidor
    ├── libs/               # Bibliotecas externas (driver de PostgreSQL)
    │   └── postgresql-42.7.3.jar
    ├── interfaces/         # Interfaces RMI que definen los contratos remotos
    ├── servidor/           # Implementación de la lógica del servidor
    │   └── bbdd/           # Script SQL para la creación de las tablas
    └── clientes/           # Implementación de la lógica del cliente
    

## 🛠️ Tecnologías Utilizadas

-   **Lenguaje:** Java SE
    
-   **Comunicación Distribuida:** Java RMI
    
-   **Base de Datos:** PostgreSQL
    
-   **Scripting:** Bash
    

## 🧑‍💻 Autores

-   Víctor Bazaga Velasco
    
-   Ángel Donet Granado
    
-   Ricardo Sanabria Vega
    

> **Aviso:** Este es un proyecto académico desarrollado en el contexto de una asignatura universitaria. No se recomienda su uso en entornos de producción sin realizar las revisiones y mejoras de seguridad pertinentes.
