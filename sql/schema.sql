-- Esquema de la base de datos de Cursus v2.0 (MySQL 8)
-- Autores: Francis Romero Flores y Antonio Tienda Díaz
SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS cursus CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE cursus;
DROP TABLE IF EXISTS entrevista;
DROP TABLE IF EXISTS oferta;
DROP TABLE IF EXISTS empresa;
CREATE TABLE empresa (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         sector VARCHAR(60),
                         web VARCHAR(255),
                         fechaRegistro DATE NOT NULL,
                         CONSTRAINT uq_empresa_nombre UNIQUE (nombre)
);
CREATE TABLE oferta (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        id_empresa INT NOT NULL,
                        titulo VARCHAR(150) NOT NULL,
                        descripcion TEXT,
                        estado VARCHAR(20) NOT NULL,
                        fechaPostulacion DATE NOT NULL,
                        enlace VARCHAR(255),
                        salario DECIMAL(10, 2),
                        notas TEXT,
                        CONSTRAINT fk_oferta_empresa FOREIGN KEY (id_empresa) REFERENCES empresa (id),
                        CONSTRAINT ck_oferta_estado CHECK (estado IN ('pendiente', 'entrevista', 'rechazado', 'aceptado')),
                        CONSTRAINT ck_oferta_salario CHECK (salario IS NULL OR salario >= 0)
);
CREATE TABLE entrevista (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            id_oferta INT NOT NULL,
                            fecha DATE NOT NULL,
                            tipo VARCHAR(20) NOT NULL,
                            notas TEXT,
                            resultado VARCHAR(20) NOT NULL,
                            CONSTRAINT fk_entrevista_oferta FOREIGN KEY (id_oferta) REFERENCES oferta (id),
                            CONSTRAINT ck_entrevista_tipo CHECK (tipo IN ('telefónica', 'técnica', 'presencial', 'online')),
                            CONSTRAINT ck_entrevista_resultado CHECK (resultado IN ('pendiente', 'positivo', 'negativo'))
);
