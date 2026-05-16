-- =========================================
-- T_RESPONSAVEIS
-- =========================================

CREATE TABLE t_responsaveis (
                                id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                nome VARCHAR2(100) NOT NULL,
                                email VARCHAR2(150) NOT NULL UNIQUE,
                                senha VARCHAR2(255) NOT NULL,
                                telefone VARCHAR2(20)
);

-- =========================================
-- T_VETERINARIOS
-- =========================================

CREATE TABLE t_veterinarios (
                                id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                nome VARCHAR2(100) NOT NULL,
                                email VARCHAR2(150) NOT NULL UNIQUE,
                                senha VARCHAR2(255) NOT NULL,
                                telefone VARCHAR2(20),
                                crmv VARCHAR2(30) NOT NULL UNIQUE,
                                especialidade VARCHAR2(100)
);

-- =========================================
-- T_PETS
-- =========================================

CREATE TABLE t_pets (
                        id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                        responsavel_id NUMBER NOT NULL,

                        nome VARCHAR2(100) NOT NULL,
                        raca VARCHAR2(100),
                        idade NUMBER,
                        peso NUMBER(5,2),

                        sexo VARCHAR2(20) NOT NULL,

                        CONSTRAINT fk_pet_responsavel
                            FOREIGN KEY (responsavel_id)
                                REFERENCES t_responsaveis(id),

                        CONSTRAINT ck_pet_sexo
                            CHECK (sexo IN ('MACHO', 'FEMEA'))
);

-- =========================================
-- T_CONSULTAS
-- =========================================

CREATE TABLE t_consultas (
                             id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                             pet_id NUMBER NOT NULL,

                             veterinario_id NUMBER NOT NULL,

                             tipo VARCHAR2(100) NOT NULL,

                             descricao VARCHAR2(1000),

                             data DATE NOT NULL,

                             observacoes VARCHAR2(1000),

                             status VARCHAR2(30) NOT NULL,

                             CONSTRAINT fk_consulta_pet
                                 FOREIGN KEY (pet_id)
                                     REFERENCES t_pets(id),

                             CONSTRAINT fk_consulta_veterinario
                                 FOREIGN KEY (veterinario_id)
                                     REFERENCES t_veterinarios(id),

                             CONSTRAINT ck_consulta_status
                                 CHECK (status IN (
                                                   'AGENDADA',
                                                   'REALIZADA',
                                                   'CANCELADA'
                                     ))
);

-- =========================================
-- T_HISTORICO_CLINICO
-- =========================================

CREATE TABLE t_historico_clinico (
                                     id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                                     pet_id NUMBER NOT NULL,

                                     tipo VARCHAR2(50) NOT NULL,

                                     descricao VARCHAR2(1000),

                                     data DATE NOT NULL,

                                     CONSTRAINT fk_historico_pet
                                         FOREIGN KEY (pet_id)
                                             REFERENCES t_pets(id),

                                     CONSTRAINT ck_historico_tipo
                                         CHECK (tipo IN (
                                             'CONSULTA'
                                             ))
);