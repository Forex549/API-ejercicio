CREATE TABLE usuario (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         nombre VARCHAR(120) NOT NULL,
                         correoElectronico VARCHAR(255) NOT NULL UNIQUE,
                         contrasena VARCHAR(255) NOT NULL
);

CREATE TABLE curso (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       nombre VARCHAR(120) NOT NULL,
                       categoria VARCHAR(120) NOT NULL
);

CREATE TABLE topico (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        titulo VARCHAR(200) NOT NULL,
                        mensaje TEXT NOT NULL,
                        fechaCreacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        status ENUM('ABIERTO', 'CERRADO') DEFAULT 'ABIERTO',
                        autor BIGINT,
                        curso BIGINT,
                        FOREIGN KEY (autor) REFERENCES usuario(id),
                        FOREIGN KEY (curso) REFERENCES curso(id)
);

CREATE TABLE respuesta (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           mensaje TEXT NOT NULL,
                           topico BIGINT NOT NULL,
                           fechaCreacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           autor BIGINT,
                           solucion BOOLEAN DEFAULT FALSE,
                           FOREIGN KEY (topico) REFERENCES topico(id),
                           FOREIGN KEY (autor) REFERENCES usuario(id)
);