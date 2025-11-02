CREATE TABLE usuario (
    id UUID PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    senha VARCHAR(200) NOT NULL,           
    role VARCHAR(50) NOT NULL,
    fk_empresa UUID NOT NULL                               
);

CREATE TABLE empresa (
    id UUID PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    razao_social VARCHAR(150)  NOT NULL,
    cnpj VARCHAR(20) NOT NULL,
    telefone VARCHAR(20) NOT NULL
);

CREATE TABLE endereco (
    id UUID PRIMARY KEY,
    logradouro VARCHAR(150) NOT NULL,
    numero INTEGER NOT NULL,
    bairro VARCHAR(100) NOT NULL,
    cep CHAR(8) NOT NULL,         
    cidade VARCHAR(100) NOT NULL, 
    uf CHAR(2) NOT NULL,              
    complemento VARCHAR(150),  
    fk_empresa UUID NOT NULL        
);

CREATE TABLE setor (
    id UUID PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(150),
    fk_empresa UUID NOT NULL
);

CREATE TABLE sala (
    id UUID PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(150),
    fk_setor UUID NOT NULL,
    fk_empresa UUID NOT NULL
);

CREATE TABLE dispositivo (
    id UUID PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    potencia REAL NOT NULL,
    fk_tipo_dispositivo UUID NOT NULL,
    fk_empresa UUID NOT NULL
);

CREATE TABLE dispositivo_sala (
    id UUID PRIMARY KEY,
    apelido VARCHAR(100) NOT NULL,
    tempo_medio_hora FLOAT,
    fk_sala UUID NOT NULL,
    fk_dispositivo UUID NOT NULL,
    fk_empresa UUID NOT NULL
);

CREATE TABLE consumo (
    id UUID,
    event_time TIMESTAMPTZ NOT NULL,
    corrente REAL NOT NULL,
    tensao REAL NOT NULL,
    potencia_ativa REAL NOT NULL,
    fk_dispositivo_sala UUID NOT NULL,
    PRIMARY KEY(id,event_time)
);

CREATE TABLE tipo_dispositivo (
    id UUID PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    fk_empresa UUID NOT NULL
);


ALTER TABLE empresa
    ADD CONSTRAINT uq_empresa_cnpj UNIQUE (cnpj);

ALTER TABLE endereco
    ADD CONSTRAINT fk_endereco_empresa FOREIGN KEY (fk_empresa)
        REFERENCES empresa(id) ON DELETE CASCADE;
 
ALTER TABLE usuario
    ADD CONSTRAINT uq_usuario_email UNIQUE (email),
    ADD CONSTRAINT fk_usuario_empresa FOREIGN KEY (fk_empresa)
        REFERENCES empresa(id) ON DELETE CASCADE;

ALTER TABLE setor 
    ADD CONSTRAINT fk_setor_empresa FOREIGN KEY (fk_empresa)
        REFERENCES empresa(id) ON DELETE CASCADE,
    ADD CONSTRAINT uq_setor_nome UNIQUE (nome, fk_empresa);


ALTER TABLE sala
    ADD CONSTRAINT fk_sala_setor FOREIGN KEY (fk_setor)
        REFERENCES setor(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_sala_empresa FOREIGN KEY (fk_empresa)
        REFERENCES empresa(id) ON DELETE CASCADE,
    ADD CONSTRAINT uq_sala_setor UNIQUE (nome, fk_setor);   

ALTER TABLE tipo_dispositivo 
    ADD CONSTRAINT fk_tipo_dispositivo_empresa FOREIGN KEY (fk_empresa)
        REFERENCES empresa(id) ON DELETE CASCADE,
    ADD CONSTRAINT uq_tipo_dispositivo_nome UNIQUE (nome,fk_empresa);

ALTER TABLE dispositivo 
    ADD CONSTRAINT fk_dispositivo_tipo FOREIGN KEY (fk_tipo_dispositivo)
        REFERENCES tipo_dispositivo(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_dispositivo_empresa FOREIGN KEY (fk_empresa)
        REFERENCES empresa(id) ON DELETE CASCADE,
    ADD CONSTRAINT uq_dispositivo_nome UNIQUE (nome, fk_empresa);

ALTER TABLE dispositivo_sala
    ADD CONSTRAINT fk_ds_sala FOREIGN KEY (fk_sala)
        REFERENCES sala(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_ds_dispositivo FOREIGN KEY (fk_dispositivo) 
        REFERENCES dispositivo(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_ds_empresa FOREIGN KEY (fk_empresa)
        REFERENCES empresa(id) ON DELETE CASCADE,
    ADD CONSTRAINT uq_ds_sala UNIQUE (apelido, fk_sala);

ALTER TABLE consumo
    ADD CONSTRAINT fk_consumo_dispositivo FOREIGN KEY (fk_dispositivo_sala)
        REFERENCES dispositivo_sala(id) ON DELETE CASCADE;
  

CREATE EXTENSION IF NOT EXISTS timescaledb;

-- ========================================
-- CRIAR HYPERTABLE
-- ========================================
SELECT create_hypertable('consumo', 'event_time', if_not_exists => TRUE);

-- ========================================
-- ÍNDICES NA HYPERTABLE (corrigidos)
-- ========================================
CREATE INDEX IF NOT EXISTS idx_consumo_dispositivo_sala 
    ON consumo (fk_dispositivo_sala, event_time DESC);

CREATE INDEX IF NOT EXISTS idx_consumo_event_time 
    ON consumo (event_time DESC);

CREATE INDEX IF NOT EXISTS idx_consumo_fk_dispositivo_sala_event_time
    ON consumo (fk_dispositivo_sala, event_time DESC)
    INCLUDE (potencia_ativa, corrente, tensao);

-- ========================================
-- ÍNDICES ADICIONAIS PARA MULTI-TENANT
-- ========================================
CREATE INDEX IF NOT EXISTS idx_dispositivo_sala_empresa_sala 
    ON dispositivo_sala (fk_empresa, fk_sala);

CREATE INDEX IF NOT EXISTS idx_dispositivo_sala_empresa 
    ON dispositivo_sala (fk_empresa);

-- ========================================
-- CONFIGURAR COMPRESSÃO
-- ========================================
ALTER TABLE consumo SET (
    timescaledb.compress,
    timescaledb.compress_orderby = 'event_time DESC',
    timescaledb.compress_segmentby = 'fk_dispositivo_sala'
);

SELECT add_compression_policy('consumo', INTERVAL '30 days');

-- ========================================
-- CONFIGURAR CHUNK TIME INTERVAL
-- ========================================
SELECT set_chunk_time_interval('consumo', INTERVAL '14 days');

-- ========================================
-- VIEWS HORARIAS
-- ========================================

-- ========================================
-- CONSUMO HORÁRIO POR DISPOSITIVO-SALA
-- ========================================
DROP MATERIALIZED VIEW IF EXISTS consumo_hourly_device_room CASCADE;

CREATE MATERIALIZED VIEW IF NOT EXISTS consumo_hourly_device_room
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 hour', c.event_time) AS hour,
    ds.fk_empresa,
    c.fk_dispositivo_sala,
    SUM(c.potencia_ativa) AS total_potencia,
    AVG(c.potencia_ativa) AS avg_potencia,
    MAX(c.potencia_ativa) AS max_potencia,
    MIN(c.potencia_ativa) AS min_potencia,
    SUM(c.corrente) AS total_corrente,
    AVG(c.corrente) AS avg_corrente,
    MAX(c.corrente) AS max_corrente,
    MIN(c.corrente) AS min_corrente,
    SUM(c.tensao) AS total_tensao,
    AVG(c.tensao) AS avg_tensao,
    MAX(c.tensao) AS max_tensao,
    MIN(c.tensao) AS min_tensao,
    COUNT(*) AS total_leituras
FROM consumo c
JOIN dispositivo_sala ds ON c.fk_dispositivo_sala = ds.id
GROUP BY hour, ds.fk_empresa, c.fk_dispositivo_sala
WITH NO DATA;

CREATE INDEX IF NOT EXISTS idx_consumo_hourly_device_room_empresa_hour
    ON consumo_hourly_device_room (fk_empresa, hour DESC);

SELECT add_continuous_aggregate_policy('consumo_hourly_device_room',
    start_offset => INTERVAL '48 hours',
    end_offset => INTERVAL '1 hour',
    schedule_interval => INTERVAL '30 minutes');
    
-- ========================================
-- CONSUMO HORÁRIO POR DISPOSITIVO
-- ========================================
DROP MATERIALIZED VIEW IF EXISTS consumo_hourly_device CASCADE;

CREATE MATERIALIZED VIEW IF NOT EXISTS consumo_hourly_device
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 hour', c.event_time) AS hour,
    d.fk_empresa,
    d.id AS fk_dispositivo,
    SUM(c.potencia_ativa) AS total_potencia,
    AVG(c.potencia_ativa) AS avg_potencia,
    MAX(c.potencia_ativa) AS max_potencia,
    MIN(c.potencia_ativa) AS min_potencia,
    COUNT(*) AS total_leituras
FROM consumo c
JOIN dispositivo_sala ds ON c.fk_dispositivo_sala = ds.id
JOIN dispositivo d ON ds.fk_dispositivo = d.id
GROUP BY hour, d.fk_empresa, d.id
WITH NO DATA;

CREATE INDEX IF NOT EXISTS idx_consumo_hourly_device_empresa_hour
    ON consumo_hourly_device (fk_empresa, hour DESC);

CREATE INDEX IF NOT EXISTS idx_consumo_hourly_device_dispositivo_hour
    ON consumo_hourly_device (fk_dispositivo, hour DESC);

SELECT add_continuous_aggregate_policy('consumo_hourly_device',
    start_offset => INTERVAL '48 hours',
    end_offset => INTERVAL '1 hour',
    schedule_interval => INTERVAL '30 minutes');

-- ========================================
-- CONSUMO HORÁRIO POR SALA
-- ========================================
DROP MATERIALIZED VIEW IF EXISTS consumo_hourly_room CASCADE;

CREATE MATERIALIZED VIEW IF NOT EXISTS consumo_hourly_room
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 hour', c.event_time) AS hour,
    ds.fk_empresa,
    ds.fk_sala,
    SUM(c.potencia_ativa) AS total_potencia,
    AVG(c.potencia_ativa) AS avg_potencia,
    MAX(c.potencia_ativa) AS max_potencia,
    MIN(c.potencia_ativa) AS min_potencia,
    COUNT(*) AS total_leituras
FROM consumo c
JOIN dispositivo_sala ds ON c.fk_dispositivo_sala = ds.id
GROUP BY hour, ds.fk_empresa, ds.fk_sala
WITH NO DATA;

CREATE INDEX IF NOT EXISTS idx_consumo_hourly_room_empresa_hour
    ON consumo_hourly_room (fk_empresa, hour DESC);

SELECT add_continuous_aggregate_policy('consumo_hourly_room',
    start_offset => INTERVAL '48 hours',
    end_offset => INTERVAL '1 hour',
    schedule_interval => INTERVAL '30 minutes');

-- ========================================
-- CONSUMO HORÁRIO POR SETOR
-- ========================================
DROP MATERIALIZED VIEW IF EXISTS consumo_hourly_department CASCADE;

CREATE MATERIALIZED VIEW IF NOT EXISTS consumo_hourly_department
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 hour', c.event_time) AS hour,
    e.id AS fk_empresa,
    se.id AS fk_setor,
    SUM(c.potencia_ativa) AS total_potencia,
    AVG(c.potencia_ativa) AS avg_potencia,
    MAX(c.potencia_ativa) AS max_potencia,
    MIN(c.potencia_ativa) AS min_potencia,
    COUNT(*) AS total_leituras
FROM consumo c
JOIN dispositivo_sala ds ON c.fk_dispositivo_sala = ds.id
JOIN sala s ON ds.fk_sala = s.id
JOIN setor se ON s.fk_setor = se.id
JOIN empresa e ON se.fk_empresa = e.id
GROUP BY hour, e.id, se.id
WITH NO DATA;

CREATE INDEX IF NOT EXISTS idx_consumo_hourly_department_empresa_hour
    ON consumo_hourly_department (fk_empresa, hour DESC);

SELECT add_continuous_aggregate_policy('consumo_hourly_department',
    start_offset => INTERVAL '48 hours',
    end_offset => INTERVAL '1 hour',
    schedule_interval => INTERVAL '30 minutes');

-- ========================================
-- VIEWS DIARIAS
-- ========================================    
    
-- ========================================
-- CONSUMO DIÁRIO POR DISPOSITIVO-SALA
-- ========================================
DROP MATERIALIZED VIEW IF EXISTS consumo_daily_device_room CASCADE;

CREATE MATERIALIZED VIEW IF NOT EXISTS consumo_daily_device_room
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 day', hour) AS day,
    fk_empresa,
    fk_dispositivo_sala,
    SUM(total_potencia) AS total_potencia,
    AVG(avg_potencia) AS avg_potencia,
    MAX(max_potencia) AS max_potencia,
    MIN(min_potencia) AS min_potencia,
    SUM(total_corrente) AS total_corrente,
    AVG(avg_corrente) AS avg_corrente,
    MAX(max_corrente) AS max_corrente,
    MIN(min_corrente) AS min_corrente,
    SUM(total_tensao) AS total_tensao,
    AVG(avg_tensao) AS avg_tensao,
    MAX(max_tensao) AS max_tensao,
    MIN(min_tensao) AS min_tensao,
    COUNT(total_leituras) AS total_leituras
FROM consumo_hourly_device_room
GROUP BY time_bucket('1 day', hour), fk_empresa, fk_dispositivo_sala
WITH NO DATA;

CREATE INDEX IF NOT EXISTS idx_consumo_daily_device_room_empresa_day
    ON consumo_daily_device_room (fk_empresa, day DESC);

SELECT add_continuous_aggregate_policy('consumo_daily_device_room',
    start_offset => INTERVAL '3 days',
    end_offset => INTERVAL '1 day',
    schedule_interval => INTERVAL '1 hour');
    
-- ========================================
-- CONSUMO DIÁRIO POR DISPOSITIVO
-- ========================================
DROP MATERIALIZED VIEW IF EXISTS consumo_daily_device CASCADE;

CREATE MATERIALIZED VIEW IF NOT EXISTS consumo_daily_device
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 day', hour) AS day,
    fk_empresa,
    fk_dispositivo,
    SUM(total_potencia) AS total_potencia,
    AVG(avg_potencia) AS avg_potencia,
    MAX(max_potencia) AS max_potencia,
    MIN(min_potencia) AS min_potencia,
    SUM(total_leituras) AS total_leituras
FROM consumo_hourly_device
GROUP BY time_bucket('1 day', hour), fk_empresa, fk_dispositivo
WITH NO DATA;

CREATE INDEX IF NOT EXISTS idx_consumo_daily_device_empresa_day
    ON consumo_daily_device (fk_empresa, day DESC);

CREATE INDEX IF NOT EXISTS idx_consumo_daily_device_dispositivo_day
    ON consumo_daily_device (fk_dispositivo, day DESC);

SELECT add_continuous_aggregate_policy('consumo_daily_device',
    start_offset => INTERVAL '3 days',
    end_offset => INTERVAL '1 day',
    schedule_interval => INTERVAL '1 hour');

-- ========================================
-- CONSUMO DIÁRIO POR SALA
-- ========================================
DROP MATERIALIZED VIEW IF EXISTS consumo_daily_room CASCADE;

CREATE MATERIALIZED VIEW IF NOT EXISTS consumo_daily_room
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 day', hour) AS day,
    fk_empresa,
    fk_sala,
    SUM(total_potencia) AS total_potencia,
    AVG(avg_potencia) AS avg_potencia,
    MAX(max_potencia) AS max_potencia,
    MIN(min_potencia) AS min_potencia
FROM consumo_hourly_room
GROUP BY time_bucket('1 day', hour), fk_empresa, fk_sala
WITH NO DATA;

CREATE INDEX IF NOT EXISTS idx_consumo_daily_room_empresa_day
    ON consumo_daily_room (fk_empresa, day DESC);

SELECT add_continuous_aggregate_policy('consumo_daily_room',
    start_offset => INTERVAL '3 days',
    end_offset => INTERVAL '1 day',
    schedule_interval => INTERVAL '1 hour');

-- ========================================
-- CONSUMO DIÁRIO POR SETOR
-- ========================================
DROP MATERIALIZED VIEW IF EXISTS consumo_daily_department CASCADE;

CREATE MATERIALIZED VIEW IF NOT EXISTS consumo_daily_department
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 day', hour) AS day,
    fk_empresa,
    fk_setor,
    SUM(total_potencia) AS total_potencia,
    AVG(avg_potencia) AS avg_potencia,
    MAX(max_potencia) AS max_potencia,
    MIN(min_potencia) AS min_potencia
FROM consumo_hourly_department
GROUP BY time_bucket('1 day', hour), fk_empresa, fk_setor
WITH NO DATA;

CREATE INDEX IF NOT EXISTS idx_consumo_daily_department_empresa_day
    ON consumo_daily_department (fk_empresa, day DESC);

SELECT add_continuous_aggregate_policy('consumo_daily_department',
    start_offset => INTERVAL '3 days',
    end_offset => INTERVAL '1 day',
    schedule_interval => INTERVAL '1 hour');
    
    
-- ========================================
-- INSERTS DE TESTE PARA VIEWS TIMESCALEDB
-- ========================================
-- Execute este script depois de criar as tabelas e views
-- Gera dados de consumo dos últimos 90 dias para teste

SELECT remove_continuous_aggregate_policy('consumo_hourly_device_room', if_exists => true);
SELECT remove_continuous_aggregate_policy('consumo_hourly_device', if_exists => true);
SELECT remove_continuous_aggregate_policy('consumo_hourly_room', if_exists => true);
SELECT remove_continuous_aggregate_policy('consumo_hourly_department', if_exists => true);
SELECT remove_continuous_aggregate_policy('consumo_daily_device_room', if_exists => true);
SELECT remove_continuous_aggregate_policy('consumo_daily_device', if_exists => true);
SELECT remove_continuous_aggregate_policy('consumo_daily_room', if_exists => true);
SELECT remove_continuous_aggregate_policy('consumo_daily_department', if_exists => true);

-- ========================================
-- 1. CRIAR EMPRESA
-- ========================================
INSERT INTO empresa (id, nome, razao_social, cnpj, telefone) 
VALUES ('a0000000-0000-0000-0000-000000000001', 'Tech Energy Solutions', 'Tech Energy Solutions LTDA', '12345678000190', '11987654321');

-- ========================================
-- 2. CRIAR ENDEREÇO
-- ========================================
INSERT INTO endereco (id, logradouro, numero, bairro, cep, cidade, uf, complemento, fk_empresa) 
VALUES ('a0000000-0000-0000-0000-000000000010', 'Av Paulista', 1000, 'Bela Vista', '01310100', 'São Paulo', 'SP', 'Andar 10', 'a0000000-0000-0000-0000-000000000001');

-- ========================================
-- 3. CRIAR USUÁRIO
-- ========================================
INSERT INTO usuario (id, nome, email, senha, role, fk_empresa) 
VALUES ('a0000000-0000-0000-0000-000000000020', 'João Silva', 'admin@admin', '$2a$10$5t9IJHtoqtNgyq3dkaKBaexmhqAgJiEh.v1KpiuXM2FUOC41Bmuie', 'ADMIN', 'a0000000-0000-0000-0000-000000000001');

-- ========================================
-- 4. CRIAR SETORES
-- ========================================
INSERT INTO setor (id, nome, descricao, fk_empresa) VALUES
('a0000000-0000-0000-0000-000000000101', 'TI', 'Tecnologia da Informação', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000102', 'Administrativo', 'Setor Administrativo', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000103', 'Produção', 'Setor de Produção', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000104', 'RH', 'Recursos Humanos', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000105', 'Financeiro', 'Setor Financeiro', 'a0000000-0000-0000-0000-000000000001');

-- ========================================
-- 5. CRIAR SALAS
-- ========================================
INSERT INTO sala (id, nome, descricao, fk_setor, fk_empresa) VALUES
('a0000000-0000-0000-0000-000000000201', 'Sala Servidores', 'Sala de Servidores Principal', 'a0000000-0000-0000-0000-000000000101', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000202', 'Sala Desenvolvimento', 'Sala dos Desenvolvedores', 'a0000000-0000-0000-0000-000000000101', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000203', 'Sala Reunião', 'Sala de Reuniões', 'a0000000-0000-0000-0000-000000000102', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000204', 'Linha Montagem 1', 'Linha de Montagem Principal', 'a0000000-0000-0000-0000-000000000103', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000205', 'Linha Montagem 2', 'Linha de Montagem Secundária', 'a0000000-0000-0000-0000-000000000103', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000206', 'RH Central', 'Sala Central de RH', 'a0000000-0000-0000-0000-000000000104', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000207', 'Contabilidade', 'Sala de Contabilidade', 'a0000000-0000-0000-0000-000000000105', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000208', 'Almoxarifado', 'Depósito e Almoxarifado', 'a0000000-0000-0000-0000-000000000103', 'a0000000-0000-0000-0000-000000000001');

-- ========================================
-- 6. CRIAR TIPOS DE DISPOSITIVO
-- ========================================
INSERT INTO tipo_dispositivo (id, nome, fk_empresa) VALUES
('a0000000-0000-0000-0000-000000000301', 'Ar Condicionado', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000302', 'Computador', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000303', 'Iluminação LED', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000304', 'Máquina Industrial', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000305', 'Impressora', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000306', 'Monitor', 'a0000000-0000-0000-0000-000000000001');

-- ========================================
-- 7. CRIAR DISPOSITIVOS
-- ========================================
INSERT INTO dispositivo (id, nome, potencia, fk_tipo_dispositivo, fk_empresa) VALUES
('a0000000-0000-0000-0000-000000000401', 'Ar Split 12000 BTU', 1200.0, 'a0000000-0000-0000-0000-000000000301', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000402', 'Ar Split 18000 BTU', 1800.0, 'a0000000-0000-0000-0000-000000000301', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000403', 'Desktop i7', 350.0, 'a0000000-0000-0000-0000-000000000302', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000404', 'Notebook i5', 65.0, 'a0000000-0000-0000-0000-000000000302', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000405', 'Painel LED 40W', 40.0, 'a0000000-0000-0000-0000-000000000303', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000406', 'Torno CNC', 5500.0, 'a0000000-0000-0000-0000-000000000304', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000407', 'Impressora Laser', 450.0, 'a0000000-0000-0000-0000-000000000305', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000408', 'Monitor 27"', 35.0, 'a0000000-0000-0000-0000-000000000306', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000409', 'Ar Split 9000 BTU', 900.0, 'a0000000-0000-0000-0000-000000000301', 'a0000000-0000-0000-0000-000000000001');

-- ========================================
-- 8. CRIAR DISPOSITIVOS-SALA
-- ========================================
INSERT INTO dispositivo_sala (id, apelido, tempo_medio_hora, fk_sala, fk_dispositivo, fk_empresa) VALUES
-- TI
('a0000000-0000-0000-0000-000000000501', 'Ar Servidores', 24.0, 'a0000000-0000-0000-0000-000000000201', 'a0000000-0000-0000-0000-000000000402', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000502', 'Ar Reunião', 4.0, 'a0000000-0000-0000-0000-000000000203', 'a0000000-0000-0000-0000-000000000401', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000503', 'PC Dev 01', 8.0, 'a0000000-0000-0000-0000-000000000202', 'a0000000-0000-0000-0000-000000000403', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000504', 'PC Dev 02', 8.0, 'a0000000-0000-0000-0000-000000000202', 'a0000000-0000-0000-0000-000000000403', 'a0000000-0000-0000-0000-000000000001'),
-- Produção
('a0000000-0000-0000-0000-000000000505', 'Iluminação Prod 1', 10.0, 'a0000000-0000-0000-0000-000000000204', 'a0000000-0000-0000-0000-000000000405', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000506', 'Torno Prod 1', 8.0, 'a0000000-0000-0000-0000-000000000204', 'a0000000-0000-0000-0000-000000000406', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000507', 'Iluminação Prod 2', 10.0, 'a0000000-0000-0000-0000-000000000205', 'a0000000-0000-0000-0000-000000000405', 'a0000000-0000-0000-0000-000000000001'),
-- RH
('a0000000-0000-0000-0000-000000000508', 'PC RH 01', 8.0, 'a0000000-0000-0000-0000-000000000206', 'a0000000-0000-0000-0000-000000000404', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000509', 'Impressora RH', 2.0, 'a0000000-0000-0000-0000-000000000206', 'a0000000-0000-0000-0000-000000000407', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000510', 'Ar RH', 8.0, 'a0000000-0000-0000-0000-000000000206', 'a0000000-0000-0000-0000-000000000409', 'a0000000-0000-0000-0000-000000000001'),
-- Financeiro
('a0000000-0000-0000-0000-000000000511', 'PC Contabil 01', 9.0, 'a0000000-0000-0000-0000-000000000207', 'a0000000-0000-0000-0000-000000000403', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000512', 'PC Contabil 02', 9.0, 'a0000000-0000-0000-0000-000000000207', 'a0000000-0000-0000-0000-000000000403', 'a0000000-0000-0000-0000-000000000001'),
('a0000000-0000-0000-0000-000000000513', 'Monitor Extra 01', 9.0, 'a0000000-0000-0000-0000-000000000207', 'a0000000-0000-0000-0000-000000000408', 'a0000000-0000-0000-0000-000000000001'),
-- Almoxarifado
('a0000000-0000-0000-0000-000000000514', 'Iluminação Almox', 12.0, 'a0000000-0000-0000-0000-000000000208', 'a0000000-0000-0000-0000-000000000405', 'a0000000-0000-0000-0000-000000000001');

-- ========================================
-- GERAR DADOS DE CONSUMO - TIMESTAMP DO SISTEMA
-- ========================================
-- Gera dados realistas dos últimos 180 dias (6 meses)
-- USANDO CURRENT_TIMESTAMP para garantir sincronia com o sistema

DO $$
DECLARE
    start_date TIMESTAMPTZ := CURRENT_TIMESTAMP - INTERVAL '180 days';
    end_date TIMESTAMPTZ := CURRENT_TIMESTAMP;
    curr_time TIMESTAMPTZ;
    hour_of_day INTEGER;
    day_of_week INTEGER;
    is_today BOOLEAN;
    is_yesterday BOOLEAN;
    is_this_week BOOLEAN;
    
    devices CONSTANT UUID[] := ARRAY[
        'a0000000-0000-0000-0000-000000000501'::UUID, -- Ar Servidores
        'a0000000-0000-0000-0000-000000000502'::UUID, -- Ar Reunião
        'a0000000-0000-0000-0000-000000000503'::UUID, -- PC Dev 1
        'a0000000-0000-0000-0000-000000000504'::UUID, -- PC Dev 2
        'a0000000-0000-0000-0000-000000000505'::UUID, -- Iluminação Prod 1
        'a0000000-0000-0000-0000-000000000506'::UUID, -- Torno CNC
        'a0000000-0000-0000-0000-000000000507'::UUID, -- Iluminação Prod 2
        'a0000000-0000-0000-0000-000000000508'::UUID, -- PC RH
        'a0000000-0000-0000-0000-000000000509'::UUID, -- Impressora RH
        'a0000000-0000-0000-0000-000000000510'::UUID, -- Ar RH
        'a0000000-0000-0000-0000-000000000511'::UUID, -- PC Contabil 1
        'a0000000-0000-0000-0000-000000000512'::UUID, -- PC Contabil 2
        'a0000000-0000-0000-0000-000000000513'::UUID, -- Monitor Extra
        'a0000000-0000-0000-0000-000000000514'::UUID  -- Iluminação Almox
    ];
    
    device_id UUID;
    potencia REAL;
    corrente REAL;
    tensao REAL;
    total_inserted INTEGER := 0;
    batch_size INTEGER := 0;
    batch_limit INTEGER := 1000;
    
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE 'INICIANDO GERACAO DE DADOS DE CONSUMO';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Timestamp Sistema: %', CURRENT_TIMESTAMP;
    RAISE NOTICE 'Periodo: % ate %', start_date, end_date;
    RAISE NOTICE 'Dispositivos: % dispositivos', array_length(devices, 1);
    RAISE NOTICE 'Intervalo: 5 minutos (12 registros/hora)';
    
    curr_time := start_date;
    
    CREATE TEMP TABLE temp_consumo (
        id UUID,
        event_time TIMESTAMPTZ,
        corrente REAL,
        tensao REAL,
        potencia_ativa REAL,
        fk_dispositivo_sala UUID
    ) ON COMMIT DROP;
    
    WHILE curr_time <= end_date LOOP
        hour_of_day := EXTRACT(HOUR FROM curr_time);
        day_of_week := EXTRACT(DOW FROM curr_time);
        
        is_today := DATE(curr_time) = DATE(CURRENT_TIMESTAMP);
        is_yesterday := DATE(curr_time) = DATE(CURRENT_TIMESTAMP - INTERVAL '1 day');
        is_this_week := DATE(curr_time) >= DATE(DATE_TRUNC('week', CURRENT_TIMESTAMP));
        
        FOREACH device_id IN ARRAY devices LOOP
            
            -- AR CONDICIONADO SERVIDORES (24/7)
            IF device_id = 'a0000000-0000-0000-0000-000000000501'::UUID THEN
                IF hour_of_day BETWEEN 14 AND 16 THEN
                    potencia := 2000.0 + (random() * 200 - 100);
                ELSIF hour_of_day BETWEEN 2 AND 5 THEN
                    potencia := 1600.0 + (random() * 150 - 75);
                ELSE
                    potencia := 1800.0 + (random() * 200 - 100);
                END IF;
                tensao := 220.0 + (random() * 10 - 5);
                corrente := potencia / tensao;
                INSERT INTO temp_consumo VALUES (gen_random_uuid(), curr_time, corrente, tensao, potencia, device_id);
                batch_size := batch_size + 1;
            
            -- AR CONDICIONADO REUNIÃO
            ELSIF device_id = 'a0000000-0000-0000-0000-000000000502'::UUID THEN
                IF day_of_week BETWEEN 1 AND 5 AND hour_of_day BETWEEN 8 AND 18 THEN
                    IF (hour_of_day BETWEEN 10 AND 12) OR (hour_of_day BETWEEN 14 AND 17) THEN
                        potencia := 1400.0 + (random() * 250 - 125);
                    ELSE
                        potencia := 1000.0 + (random() * 200 - 100);
                    END IF;
                    tensao := 220.0 + (random() * 10 - 5);
                    corrente := potencia / tensao;
                    INSERT INTO temp_consumo VALUES (gen_random_uuid(), curr_time, corrente, tensao, potencia, device_id);
                    batch_size := batch_size + 1;
                END IF;
            
            -- PCs DESENVOLVIMENTO
            ELSIF device_id IN ('a0000000-0000-0000-0000-000000000503'::UUID, 
                               'a0000000-0000-0000-0000-000000000504'::UUID) THEN
                IF day_of_week BETWEEN 1 AND 5 AND hour_of_day BETWEEN 7 AND 19 THEN
                    IF (hour_of_day BETWEEN 9 AND 12) OR (hour_of_day BETWEEN 14 AND 18) THEN
                        IF random() < 0.15 THEN
                            potencia := 400.0 + (random() * 100);
                        ELSE
                            potencia := 280.0 + (random() * 60);
                        END IF;
                    ELSIF hour_of_day = 7 OR hour_of_day = 19 THEN
                        potencia := 180.0 + (random() * 40);
                    ELSE
                        potencia := 150.0 + (random() * 30);
                    END IF;
                    tensao := 220.0 + (random() * 10 - 5);
                    corrente := potencia / tensao;
                    INSERT INTO temp_consumo VALUES (gen_random_uuid(), curr_time, corrente, tensao, potencia, device_id);
                    batch_size := batch_size + 1;
                END IF;
            
            -- ILUMINAÇÃO PRODUÇÃO
            ELSIF device_id IN ('a0000000-0000-0000-0000-000000000505'::UUID,
                               'a0000000-0000-0000-0000-000000000507'::UUID) THEN
                IF day_of_week BETWEEN 1 AND 5 AND hour_of_day BETWEEN 6 AND 22 THEN
                    IF hour_of_day BETWEEN 6 AND 8 OR hour_of_day BETWEEN 18 AND 22 THEN
                        potencia := 45.0 + (random() * 5);
                    ELSE
                        potencia := 38.0 + (random() * 4);
                    END IF;
                    tensao := 220.0 + (random() * 10 - 5);
                    corrente := potencia / tensao;
                    INSERT INTO temp_consumo VALUES (gen_random_uuid(), curr_time, corrente, tensao, potencia, device_id);
                    batch_size := batch_size + 1;
                END IF;
            
            -- TORNO CNC
            ELSIF device_id = 'a0000000-0000-0000-0000-000000000506'::UUID THEN
                IF day_of_week BETWEEN 1 AND 5 AND hour_of_day BETWEEN 6 AND 22 THEN
                    DECLARE estado REAL := random();
                    BEGIN
                        IF estado < 0.15 THEN
                            potencia := 50.0 + (random() * 20);
                        ELSIF estado < 0.35 THEN
                            potencia := 800.0 + (random() * 200);
                        ELSE
                            IF random() < 0.4 THEN
                                potencia := 3000.0 + (random() * 500);
                            ELSE
                                potencia := 4800.0 + (random() * 800);
                            END IF;
                        END IF;
                    END;
                    tensao := 220.0 + (random() * 10 - 5);
                    corrente := potencia / tensao;
                    INSERT INTO temp_consumo VALUES (gen_random_uuid(), curr_time, corrente, tensao, potencia, device_id);
                    batch_size := batch_size + 1;
                END IF;
            
            -- PC RH
            ELSIF device_id = 'a0000000-0000-0000-0000-000000000508'::UUID THEN
                IF day_of_week BETWEEN 1 AND 5 AND hour_of_day BETWEEN 8 AND 17 THEN
                    potencia := 50.0 + (random() * 30);
                    tensao := 220.0 + (random() * 10 - 5);
                    corrente := potencia / tensao;
                    INSERT INTO temp_consumo VALUES (gen_random_uuid(), curr_time, corrente, tensao, potencia, device_id);
                    batch_size := batch_size + 1;
                END IF;
            
            -- IMPRESSORA RH
            ELSIF device_id = 'a0000000-0000-0000-0000-000000000509'::UUID THEN
                IF day_of_week BETWEEN 1 AND 5 AND hour_of_day BETWEEN 8 AND 17 AND random() < 0.1 THEN
                    potencia := 350.0 + (random() * 100);
                    tensao := 220.0 + (random() * 10 - 5);
                    corrente := potencia / tensao;
                    INSERT INTO temp_consumo VALUES (gen_random_uuid(), curr_time, corrente, tensao, potencia, device_id);
                    batch_size := batch_size + 1;
                END IF;
            
            -- AR RH
            ELSIF device_id = 'a0000000-0000-0000-0000-000000000510'::UUID THEN
                IF day_of_week BETWEEN 1 AND 5 AND hour_of_day BETWEEN 8 AND 17 THEN
                    potencia := 850.0 + (random() * 100);
                    tensao := 220.0 + (random() * 10 - 5);
                    corrente := potencia / tensao;
                    INSERT INTO temp_consumo VALUES (gen_random_uuid(), curr_time, corrente, tensao, potencia, device_id);
                    batch_size := batch_size + 1;
                END IF;
            
            -- PCs CONTABILIDADE
            ELSIF device_id IN ('a0000000-0000-0000-0000-000000000511'::UUID,
                               'a0000000-0000-0000-0000-000000000512'::UUID) THEN
                IF day_of_week BETWEEN 1 AND 5 AND hour_of_day BETWEEN 8 AND 18 THEN
                    potencia := 300.0 + (random() * 80);
                    tensao := 220.0 + (random() * 10 - 5);
                    corrente := potencia / tensao;
                    INSERT INTO temp_consumo VALUES (gen_random_uuid(), curr_time, corrente, tensao, potencia, device_id);
                    batch_size := batch_size + 1;
                END IF;
            
            -- MONITOR EXTRA
            ELSIF device_id = 'a0000000-0000-0000-0000-000000000513'::UUID THEN
                IF day_of_week BETWEEN 1 AND 5 AND hour_of_day BETWEEN 8 AND 18 THEN
                    potencia := 32.0 + (random() * 6);
                    tensao := 220.0 + (random() * 10 - 5);
                    corrente := potencia / tensao;
                    INSERT INTO temp_consumo VALUES (gen_random_uuid(), curr_time, corrente, tensao, potencia, device_id);
                    batch_size := batch_size + 1;
                END IF;
            
            -- ILUMINAÇÃO ALMOXARIFADO
            ELSIF device_id = 'a0000000-0000-0000-0000-000000000514'::UUID THEN
                IF day_of_week BETWEEN 1 AND 5 AND hour_of_day BETWEEN 7 AND 18 THEN
                    potencia := 42.0 + (random() * 4);
                    tensao := 220.0 + (random() * 10 - 5);
                    corrente := potencia / tensao;
                    INSERT INTO temp_consumo VALUES (gen_random_uuid(), curr_time, corrente, tensao, potencia, device_id);
                    batch_size := batch_size + 1;
                END IF;
            
            END IF;
            
        END LOOP;
        
        IF batch_size >= batch_limit THEN
            INSERT INTO consumo (id, event_time, corrente, tensao, potencia_ativa, fk_dispositivo_sala)
            SELECT * FROM temp_consumo
            ON CONFLICT (id, event_time) DO NOTHING;
            
            total_inserted := total_inserted + batch_size;
            DELETE FROM temp_consumo;
            batch_size := 0;
        END IF;
        
        curr_time := curr_time + INTERVAL '5 minutes';
        
        IF EXTRACT(HOUR FROM curr_time) = 0 AND EXTRACT(MINUTE FROM curr_time) = 0 THEN
            IF is_today THEN
                RAISE NOTICE '[HOJE] %', DATE(curr_time - INTERVAL '1 day');
            ELSIF is_yesterday THEN
                RAISE NOTICE '[ONTEM] %', DATE(curr_time - INTERVAL '1 day');
            ELSIF is_this_week THEN
                RAISE NOTICE '[ESTA SEMANA] %', DATE(curr_time - INTERVAL '1 day');
            ELSIF EXTRACT(DAY FROM curr_time) = 1 THEN
                RAISE NOTICE '[MES] %', TO_CHAR(curr_time - INTERVAL '1 day', 'Mon/YYYY');
            END IF;
        END IF;
        
    END LOOP;
    
    IF batch_size > 0 THEN
        INSERT INTO consumo (id, event_time, corrente, tensao, potencia_ativa, fk_dispositivo_sala)
        SELECT * FROM temp_consumo
        ON CONFLICT (id, event_time) DO NOTHING;
        
        total_inserted := total_inserted + batch_size;
    END IF;
    
    RAISE NOTICE '========================================';
    RAISE NOTICE 'GERACAO CONCLUIDA COM SUCESSO!';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Timestamp Final Sistema: %', CURRENT_TIMESTAMP;
    RAISE NOTICE 'Registros inseridos: %', total_inserted;
    RAISE NOTICE 'Periodo: % dias', EXTRACT(DAY FROM (end_date - start_date));
    RAISE NOTICE 'Dispositivos: %', array_length(devices, 1);
    
END $$;

-- ========================================
-- 10. VERIFICAR DADOS INSERIDOS
-- ========================================

DO $$
DECLARE
    total_consumo BIGINT;
    min_date TIMESTAMPTZ;
    max_date TIMESTAMPTZ;
    total_dispositivos INTEGER;
BEGIN
    SELECT COUNT(*), MIN(event_time), MAX(event_time), COUNT(DISTINCT fk_dispositivo_sala)
    INTO total_consumo, min_date, max_date, total_dispositivos
    FROM consumo;
    
    RAISE NOTICE '========================================';
    RAISE NOTICE 'DADOS NA TABELA CONSUMO';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Total: %', total_consumo;
    RAISE NOTICE 'Período: % até %', min_date, max_date;
    RAISE NOTICE 'Dispositivos: %', total_dispositivos;
    
    IF total_consumo = 0 THEN
        RAISE EXCEPTION '❌ Nenhum dado foi inserido!';
    END IF;
    
END $$;

-- ========================================
-- 11. REFRESH VIEWS HORÁRIAS
-- ========================================

DO $$
DECLARE
    min_time TIMESTAMPTZ;
    max_time TIMESTAMPTZ;
    count_result BIGINT;
BEGIN
    SELECT MIN(event_time), MAX(event_time) INTO min_time, max_time FROM consumo;
    
    RAISE NOTICE '========================================';
    RAISE NOTICE 'REFRESH VIEWS HORÁRIAS';
    RAISE NOTICE 'Período: % até %', min_time, max_time;
    RAISE NOTICE '========================================';
    
    -- 1. device_room
    RAISE NOTICE '→ consumo_hourly_device_room...';
    CALL refresh_continuous_aggregate('consumo_hourly_device_room', min_time, max_time);
    SELECT COUNT(*) INTO count_result FROM consumo_hourly_device_room;
    RAISE NOTICE '  Registros: %', count_result;
    
    -- 2. device
    RAISE NOTICE '→ consumo_hourly_device...';
    CALL refresh_continuous_aggregate('consumo_hourly_device', min_time, max_time);
    SELECT COUNT(*) INTO count_result FROM consumo_hourly_device;
    RAISE NOTICE '  Registros: %', count_result;
    
    -- 3. room
    RAISE NOTICE '→ consumo_hourly_room...';
    CALL refresh_continuous_aggregate('consumo_hourly_room', min_time, max_time);
    SELECT COUNT(*) INTO count_result FROM consumo_hourly_room;
    RAISE NOTICE '  Registros: %', count_result;
    
    -- 4. department
    RAISE NOTICE '→ consumo_hourly_department...';
    CALL refresh_continuous_aggregate('consumo_hourly_department', min_time, max_time);
    SELECT COUNT(*) INTO count_result FROM consumo_hourly_department;
    RAISE NOTICE '  Registros: %', count_result;
    
    RAISE NOTICE '✅ Views horárias prontas!';
    
END $$;

-- ========================================
-- 12. VERIFICAR VIEWS HORÁRIAS
-- ========================================

DO $$
DECLARE
    c1 BIGINT; c2 BIGINT; c3 BIGINT; c4 BIGINT;
BEGIN
    SELECT COUNT(*) INTO c1 FROM consumo_hourly_device_room;
    SELECT COUNT(*) INTO c2 FROM consumo_hourly_device;
    SELECT COUNT(*) INTO c3 FROM consumo_hourly_room;
    SELECT COUNT(*) INTO c4 FROM consumo_hourly_department;
    
    RAISE NOTICE '========================================';
    RAISE NOTICE 'VERIFICAÇÃO VIEWS HORÁRIAS';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'device_room:  %', c1;
    RAISE NOTICE 'device:       %', c2;
    RAISE NOTICE 'room:         %', c3;
    RAISE NOTICE 'department:   %', c4;
    
    IF c1 = 0 OR c3 = 0 OR c4 = 0 THEN
        RAISE WARNING '⚠️  Alguma view está vazia!';
    END IF;
END $$;

-- ========================================
-- 13. REFRESH VIEWS DIÁRIAS
-- ========================================

DO $$
DECLARE
    min_time TIMESTAMPTZ;
    max_time TIMESTAMPTZ;
    count_result BIGINT;
BEGIN
    SELECT MIN(hour), MAX(hour) INTO min_time, max_time FROM consumo_hourly_device_room;
    
    IF min_time IS NULL THEN
        RAISE EXCEPTION '❌ Views horárias vazias - não é possível gerar views diárias';
    END IF;
    
    RAISE NOTICE '========================================';
    RAISE NOTICE 'REFRESH VIEWS DIÁRIAS';
    RAISE NOTICE 'Período: % até %', min_time, max_time;
    RAISE NOTICE '========================================';
    
    -- 1. device_room
    RAISE NOTICE '→ consumo_daily_device_room...';
    CALL refresh_continuous_aggregate('consumo_daily_device_room', min_time, max_time);
    SELECT COUNT(*) INTO count_result FROM consumo_daily_device_room;
    RAISE NOTICE '  Registros: %', count_result;
    
    -- 2. device
    RAISE NOTICE '→ consumo_daily_device...';
    CALL refresh_continuous_aggregate('consumo_daily_device', min_time, max_time);
    SELECT COUNT(*) INTO count_result FROM consumo_daily_device;
    RAISE NOTICE '  Registros: %', count_result;
    
    -- 3. room
    RAISE NOTICE '→ consumo_daily_room...';
    CALL refresh_continuous_aggregate('consumo_daily_room', min_time, max_time);
    SELECT COUNT(*) INTO count_result FROM consumo_daily_room;
    RAISE NOTICE '  Registros: %', count_result;
    
    -- 4. department
    RAISE NOTICE '→ consumo_daily_department...';
    CALL refresh_continuous_aggregate('consumo_daily_department', min_time, max_time);
    SELECT COUNT(*) INTO count_result FROM consumo_daily_department;
    RAISE NOTICE '  Registros: %', count_result;
    
    RAISE NOTICE '✅ Views diárias prontas!';
    
END $$;

-- ========================================
-- 14. VERIFICAR VIEWS DIÁRIAS
-- ========================================

DO $$
DECLARE
    c1 BIGINT; c2 BIGINT; c3 BIGINT; c4 BIGINT;
BEGIN
    SELECT COUNT(*) INTO c1 FROM consumo_daily_device_room;
    SELECT COUNT(*) INTO c2 FROM consumo_daily_device;
    SELECT COUNT(*) INTO c3 FROM consumo_daily_room;
    SELECT COUNT(*) INTO c4 FROM consumo_daily_department;
    
    RAISE NOTICE '========================================';
    RAISE NOTICE 'VERIFICAÇÃO VIEWS DIÁRIAS';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'device_room:  %', c1;
    RAISE NOTICE 'device:       %', c2;
    RAISE NOTICE 'room:         %', c3;
    RAISE NOTICE 'department:   %', c4;
    
    IF c1 = 0 OR c3 = 0 OR c4 = 0 THEN
        RAISE WARNING '⚠️  Alguma view está vazia!';
    END IF;
END $$;

-- ========================================
-- 15. REABILITAR POLÍTICAS
-- ========================================

SELECT add_continuous_aggregate_policy('consumo_hourly_device_room',
    start_offset => INTERVAL '48 hours',
    end_offset => INTERVAL '1 hour',
    schedule_interval => INTERVAL '30 minutes');

SELECT add_continuous_aggregate_policy('consumo_hourly_device',
    start_offset => INTERVAL '48 hours',
    end_offset => INTERVAL '1 hour',
    schedule_interval => INTERVAL '30 minutes');

SELECT add_continuous_aggregate_policy('consumo_hourly_room',
    start_offset => INTERVAL '48 hours',
    end_offset => INTERVAL '1 hour',
    schedule_interval => INTERVAL '30 minutes');

SELECT add_continuous_aggregate_policy('consumo_hourly_department',
    start_offset => INTERVAL '48 hours',
    end_offset => INTERVAL '1 hour',
    schedule_interval => INTERVAL '30 minutes');

SELECT add_continuous_aggregate_policy('consumo_daily_device_room',
    start_offset => INTERVAL '3 days',
    end_offset => INTERVAL '1 day',
    schedule_interval => INTERVAL '1 hour');

SELECT add_continuous_aggregate_policy('consumo_daily_device',
    start_offset => INTERVAL '3 days',
    end_offset => INTERVAL '1 day',
    schedule_interval => INTERVAL '1 hour');

SELECT add_continuous_aggregate_policy('consumo_daily_room',
    start_offset => INTERVAL '3 days',
    end_offset => INTERVAL '1 day',
    schedule_interval => INTERVAL '1 hour');

SELECT add_continuous_aggregate_policy('consumo_daily_department',
    start_offset => INTERVAL '3 days',
    end_offset => INTERVAL '1 day',
    schedule_interval => INTERVAL '1 hour');