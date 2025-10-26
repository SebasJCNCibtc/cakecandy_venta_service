CREATE TABLE venta (
                       id_venta INT PRIMARY KEY AUTO_INCREMENT,
                       id_usuario INT NOT NULL,
                       fecha_venta DATETIME DEFAULT CURRENT_TIMESTAMP,
                       total DECIMAL(10, 2) NOT NULL
);

CREATE TABLE detalle_venta (
                               id_detalle_venta INT PRIMARY KEY AUTO_INCREMENT,
                               id_venta INT NOT NULL,
                               id_producto INT NOT NULL,
                               cantidad INT NOT NULL,
                               precio_unitario DECIMAL(10, 2) NOT NULL,
                               subtotal DECIMAL(10, 2) NOT NULL,
                               FOREIGN KEY (id_venta) REFERENCES venta(id_venta)
);