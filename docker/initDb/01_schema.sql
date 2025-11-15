DROP TABLE IF EXISTS tramos        CASCADE;
DROP TABLE IF EXISTS rutas         CASCADE;
DROP TABLE IF EXISTS solicitudes   CASCADE;
DROP TABLE IF EXISTS contenedores  CASCADE;
DROP TABLE IF EXISTS camiones      CASCADE;
DROP TABLE IF EXISTS depositos     CASCADE;
DROP TABLE IF EXISTS tarifas       CASCADE;
DROP TABLE IF EXISTS clientes      CASCADE;
DROP TABLE IF EXISTS transportistas CASCADE;

-- =========================
-- CLIENTES
-- =========================
CREATE TABLE clientes (
  id_cliente     SERIAL PRIMARY KEY,
  nombre         VARCHAR(120) NOT NULL,
  telefono       NUMERIC(20,0),
  email          VARCHAR(180)
  -- UNIQUE (email)               -- descomentar si queremos evitar duplicados
);

-- =========================
-- TRANSPORTISTAS
-- =========================
CREATE TABLE transportistas (
  id_transportista SERIAL PRIMARY KEY,
  nombre           VARCHAR(120) NOT NULL,
  telefono         NUMERIC(20,0)
);

-- =========================
-- CAMIONES
-- =========================
CREATE TABLE camiones (
  id_camion                 SERIAL PRIMARY KEY,
  id_transportista          INT NOT NULL REFERENCES transportistas(id_transportista) ON DELETE RESTRICT,
  patente                   VARCHAR(20) NOT NULL,
  telefono                  NUMERIC(20,0),
  capacidadKg               NUMERIC(12,2),
  volumen_max               NUMERIC(12,3),
  estado                    VARCHAR(40),
  consumoXKm                NUMERIC(10,3),
  costo_base_trasladoXKm    NUMERIC(12,2),
  CONSTRAINT uq_camion_patente UNIQUE (patente)
);
CREATE INDEX idx_camiones_transportista ON camiones(id_transportista);

-- =========================
-- DEPOSITOS
-- =========================
CREATE TABLE depositos (
  id_deposito        SERIAL PRIMARY KEY,
  nombre             VARCHAR(120) NOT NULL,
  direccion_txt      VARCHAR(200),
  longitud           NUMERIC(9,6),     -- ej. -64.123456
  latitud            NUMERIC(9,6),     -- ej. -31.123456
  costo_estadia_hora NUMERIC(12,2)
);

-- =========================
-- CONTENEDORES
-- =========================
CREATE TABLE contenedores (
  id_contenedor  SERIAL PRIMARY KEY,
  id_cliente     INT NOT NULL REFERENCES clientes(id_cliente) ON DELETE RESTRICT,
  pesoKg         NUMERIC(12,2),
  volumen        NUMERIC(12,3),
  estado         VARCHAR(40)
);
CREATE INDEX idx_contenedores_cliente ON contenedores(id_cliente);

-- =========================
-- TARIFAS
-- =========================
CREATE TABLE tarifas (
  id_tarifa             SERIAL PRIMARY KEY,
  descripcion           VARCHAR(150) NOT NULL,
  vol_min               NUMERIC(12,3) NOT NULL,
  vol_max               NUMERIC(12,3) NOT NULL,
  costo_base_km_xvol    NUMERIC(12,4) NOT NULL,
  valor_combustible     NUMERIC(12,4) NOT NULL,
  activa                BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT chk_tarifa_volumes CHECK (vol_min >= 0 AND vol_max >= vol_min)
);

-- =========================
-- SOLICITUDES
-- =========================
CREATE TABLE solicitudes (
  id_solicitud      SERIAL PRIMARY KEY,
  id_contenedor     INT NOT NULL REFERENCES contenedores(id_contenedor) ON DELETE RESTRICT,
  id_cliente        INT NOT NULL REFERENCES clientes(id_cliente) ON DELETE RESTRICT,
  id_tarifa         INT NOT NULL REFERENCES tarifas(id_tarifa) ON DELETE RESTRICT,

  costo_estimado    NUMERIC(14,2),
  tiempo_estimado   TIMESTAMP,
  costo_final       NUMERIC(14,2),
  tiempo_final      TIMESTAMP,
  estado            VARCHAR(40),

  origen_direccion  VARCHAR(200),
  origen_latitud    NUMERIC(9,6),
  origen_longitud   NUMERIC(9,6),

  destino_direccion VARCHAR(200),
  destino_latitud   NUMERIC(9,6),
  destino_longitud  NUMERIC(9,6),

  fechaSolicitud    TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_solicitudes_contenedor ON solicitudes(id_contenedor);
CREATE INDEX idx_solicitudes_cliente    ON solicitudes(id_cliente);
CREATE INDEX idx_solicitudes_tarifa     ON solicitudes(id_tarifa);

-- =========================
-- RUTAS
-- =========================
CREATE TABLE rutas (
  id_ruta         SERIAL PRIMARY KEY,
  id_solicitud    INT NOT NULL REFERENCES solicitudes(id_solicitud) ON DELETE CASCADE,
  cant_tramos     NUMERIC(6,0),
  cant_depositos  NUMERIC(6,0),
  -- FACU recomienda agregar esto:
  costo_aprox        NUMERIC(14,2),
  costo_real         NUMERIC(14,2), -- aunque son atributos calculados, nos simplifican un monton de cosas
);
CREATE INDEX idx_rutas_solicitud ON rutas(id_solicitud);

-- =========================
-- TRAMOS
-- =========================
CREATE TABLE tramos (
  id_tramo           SERIAL PRIMARY KEY,
  id_camion          INT REFERENCES camiones(id_camion) ON DELETE RESTRICT, -- CAMBIO NECESARIO by FACU
  id_ruta            INT NOT NULL REFERENCES rutas(id_ruta) ON DELETE CASCADE,
  nro_orden          NUMERIC(6,0) NOT NULL,

  deposito_origen    INT REFERENCES depositos(id_deposito) ON DELETE RESTRICT,
  deposito_destino   INT REFERENCES depositos(id_deposito) ON DELETE RESTRICT,

  tipo_tramo         VARCHAR(40),
  estado             VARCHAR(40),
  costo_aprox        NUMERIC(14,2),
  costo_real         NUMERIC(14,2),
  fechaHora_inicio   TIMESTAMP,
  fechaHora_fin      TIMESTAMP,
  -- FACU recomienda agregar esto:
  distancia_km       NUMERIC(14,2)
);
CREATE INDEX idx_tramos_ruta      ON tramos(id_ruta);
CREATE INDEX idx_tramos_camion    ON tramos(id_camion);
CREATE INDEX idx_tramos_dep_org   ON tramos(deposito_origen);
CREATE INDEX idx_tramos_dep_dst   ON tramos(deposito_destino);
CREATE UNIQUE INDEX uq_tramos_ruta_orden ON tramos(id_ruta, nro_orden);