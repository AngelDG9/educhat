-- Administradores
CREATE TABLE IF NOT EXISTS administradores (
    dni TEXT PRIMARY KEY,
    nombre TEXT NOT NULL
);

-- Profesores
CREATE TABLE IF NOT EXISTS profesores (
    dni TEXT PRIMARY KEY,
    nombre TEXT NOT NULL
);

-- Alumnos
CREATE TABLE IF NOT EXISTS alumnos (
    dni TEXT PRIMARY KEY,
    nombre TEXT NOT NULL
);

-- Asignaturas
CREATE TABLE IF NOT EXISTS asignaturas (
    id TEXT PRIMARY KEY,
    nombre TEXT NOT NULL,
    dni_profesor TEXT,
    FOREIGN KEY (dni_profesor) REFERENCES profesores(dni)
);

-- Matriculas: alumno se matricula en asignatura
CREATE TABLE IF NOT EXISTS matriculas (
    dni_alumno TEXT,
    id_asignatura TEXT,
    PRIMARY KEY (dni_alumno, id_asignatura),
    FOREIGN KEY (dni_alumno) REFERENCES alumnos(dni),
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id)
);

-- Notificaciones: se almacenan mensajes con fecha y hora
CREATE TABLE IF NOT EXISTS notificaciones (
    id SERIAL PRIMARY KEY,
    origen VARCHAR(20) NOT NULL,         -- 'profesor' o 'alumno'
    dni_origen TEXT NOT NULL,
    id_asignatura TEXT NOT NULL,
    asunto TEXT,
    mensaje TEXT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id)
);

-- Contenidos: se almacena el path al archivo de contenido
CREATE TABLE IF NOT EXISTS contenidos (
    id SERIAL PRIMARY KEY,
    id_asignatura TEXT NOT NULL,
    path TEXT NOT NULL,
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id)
);

