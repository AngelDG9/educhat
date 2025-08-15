#!/bin/bash
# run.sh: Script para configurar, compilar, y ejecutar el sistema RMI con PostgreSQL
# Uso: ./run.sh [--reset|-r]
#   --reset, -r   Elimina la base de datos 'academico' antes de crearla de nuevo.

export PGPASSWORD=dit

RESET_DB=false
if [[ "$1" == "--reset" || "$1" == "-r" ]]; then
    RESET_DB=true
fi

# ---------------------------
# 1. Verificar e iniciar PostgreSQL
# ---------------------------
echo "Verificando el servicio de PostgreSQL..."
if ! pg_isready -U dit > /dev/null 2>&1; then
    echo "PostgreSQL no está activo. Intentando iniciarlo..."
    sudo systemctl start postgresql
    sleep 3
    if ! pg_isready -U dit > /dev/null 2>&1; then
        echo "Error: no se pudo iniciar PostgreSQL."
        exit 1
    fi
fi
echo "PostgreSQL está activo."

# ---------------------------
# 2. (Opcional) Resetear base de datos
# ---------------------------
if $RESET_DB; then
    echo "Opción reset activada: eliminando base de datos 'academico'..."
    dropdb -U dit academico 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "Base de datos 'academico' eliminada."
    else
        echo "La base de datos 'academico' no existía o no pudo eliminarse."
    fi
fi

# ---------------------------
# 3. Crear la base de datos 'academico' si no existe
# ---------------------------
echo "Comprobando si existe la base de datos 'academico'..."
DB_EXISTS=$(psql -U dit -lqt | cut -d \| -f 1 | grep -w academico)
if [ -z "$DB_EXISTS" ]; then
    echo "La base de datos 'academico' no existe. Creándola..."
    createdb -U dit academico
    if [ $? -ne 0 ]; then
        echo "Error: no se pudo crear la base de datos 'academico'."
        exit 1
    fi
else
    echo "La base de datos 'academico' ya existe."
fi

# ---------------------------
# 4. Ejecutar el script SQL para crear las tablas
# ---------------------------
echo "Ejecutando el script SQL de creación de tablas..."
psql -U dit -d academico -f bbdd/creatablas.sql
if [ $? -ne 0 ]; then
    echo "Error: fallo al ejecutar bbdd/creatablas.sql."
    exit 1
fi

# ---------------------------
# 5. Compilar el código Java
# ---------------------------
echo "Compilando el código fuente..."
javac -d . interfaces/*.java servidor/*.java clientes/*.java
if [ $? -ne 0 ]; then
    echo "Error: compilación fallida. Revisa los errores."
    exit 1
fi

# ---------------------------
# 6. Iniciar el Servidor RMI
# ---------------------------
echo "Iniciando el servidor RMI..."
java -cp .:libs/postgresql-42.7.3.jar servidor.ServidorAcademico &
SERVER_PID=$!
sleep 2

echo "Servidor RMI en ejecución (PID: $SERVER_PID) en el puerto 54355."
echo "--------------------------------------------------------------"
echo "Para probar, abre otra terminal y ejecuta el cliente:"
echo "  java -cp .:libs/postgresql-42.7.3.jar clientes.ClienteAcademico"
echo "--------------------------------------------------------------"
echo "Presiona [ENTER] para detener el servidor."
read -r

# ---------------------------
# 7. Detener el servidor
# ---------------------------
echo "Deteniendo el servidor RMI (PID: $SERVER_PID)..."
kill $SERVER_PID 2>/dev/null
wait $SERVER_PID 2>/dev/null

echo "Servidor detenido. Proceso finalizado."

