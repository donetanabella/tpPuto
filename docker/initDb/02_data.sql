-- Insertar datos de ejemplo en las tablas

-- Clientes
INSERT INTO clientes (nombre, telefono, email) VALUES
('Juan Pérez', 1122334455, 'juan.perez@email.com'),
('María García', 1133445566, 'maria.garcia@email.com'),
('Carlos López', 1144556677, 'carlos.lopez@email.com');

-- Transportistas
INSERT INTO transportistas (nombre, telefono) VALUES
('Transportes Rápidos SA', 1155667788),
('Logística Express', 1166778899),
('Transportes del Sur', 1177889900);

-- Camiones
INSERT INTO camiones (id_transportista, patente, telefono, capacidadKg, volumen_max, estado, consumoXKm, costo_base_trasladoXKm) VALUES
(1, 'ABC123', 1155667788, 5000.00, 20.000, 'DISPONIBLE', 0.500, 100.00),
(1, 'DEF456', 1155667789, 7500.00, 30.000, 'DISPONIBLE', 0.750, 150.00),
(2, 'GHI789', 1166778899, 10000.00, 40.000, 'DISPONIBLE', 1.000, 200.00);

-- Depósitos
INSERT INTO depositos (nombre, direccion_txt, longitud, latitud, costo_estadia_hora) VALUES
('Depósito Central', 'Av. Principal 123', -58.381559, -34.603684, 50.00),
('Depósito Norte', 'Ruta 9 km 50', -58.515475, -34.451662, 40.00),
('Depósito Sur', 'Ruta 205 km 30', -58.381582, -34.817844, 45.00);

-- Contenedores
INSERT INTO contenedores (id_cliente, pesoKg, volumen, estado) VALUES
(1, 1000.00, 5.000, 'DISPONIBLE'),
(2, 2000.00, 8.000, 'DISPONIBLE'),
(3, 1500.00, 6.000, 'DISPONIBLE');

-- Tarifas
INSERT INTO tarifas (descripcion, vol_min, vol_max, costo_base_km_xvol, valor_combustible, activa) VALUES
('Tarifa Económica', 0.000, 10.000, 5.0000, 2.5000, true),
('Tarifa Estándar', 10.001, 20.000, 7.5000, 3.0000, true),
('Tarifa Premium', 20.001, 40.000, 10.0000, 3.5000, true);

-- Solicitudes
INSERT INTO solicitudes (id_contenedor, id_cliente, id_tarifa, costo_estimado, estado, 
                        origen_direccion, origen_latitud, origen_longitud,
                        destino_direccion, destino_latitud, destino_longitud) VALUES
(1, 1, 1, 5000.00, 'PENDIENTE', 
 'Origen 1', -34.603684, -58.381559,
 'Destino 1', -34.451662, -58.515475),
(2, 2, 2, 7500.00, 'PENDIENTE',
 'Origen 2', -34.451662, -58.515475,
 'Destino 2', -34.817844, -58.381582);

-- Rutas
INSERT INTO rutas (id_solicitud, cant_tramos, cant_depositos) VALUES
(1, 2, 1),
(2, 3, 2);

-- Tramos
INSERT INTO tramos (id_camion, id_ruta, nro_orden, deposito_origen, deposito_destino,
                   tipo_tramo, estado, costo_aprox, fechaHora_inicio) 
SELECT 1, 1, 1, 1, 2, 'RECOLECCION', 'PENDIENTE', 2500.00, NOW() UNION ALL
SELECT 1, 1, 2, 2, NULL, 'ENTREGA', 'PENDIENTE', 2500.00, NOW() + interval '2 hours' UNION ALL
SELECT 2, 2, 1, 1, 2, 'RECOLECCION', 'PENDIENTE', 2500.00, NOW() UNION ALL
SELECT 2, 2, 2, 2, 3, 'TRANSPORTE', 'PENDIENTE', 2500.00, NOW() + interval '2 hours' UNION ALL
SELECT 2, 2, 3, 3, NULL, 'ENTREGA', 'PENDIENTE', 2500.00, NOW() + interval '4 hours';