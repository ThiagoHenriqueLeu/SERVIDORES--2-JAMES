# SERVIDORES--2-JAMES

 Configuração do Banco de Dados (PostgreSQL)

Este projeto utiliza PostgreSQL como banco de dados.

 Pré-requisitos
PostgreSQL instalado
(Opcional) pgAdmin
 1. Criar banco + usuário

Execute no PostgreSQL:

-- Criar banco
CREATE DATABASE catalogo;

-- Criar usuário (opcional)
CREATE USER catalogo_user WITH PASSWORD '123456';

-- Permissões
ALTER ROLE catalogo_user SUPERUSER;

-- Conectar no banco
\c catalogo;
 2. Criar tabelas (SQL COMPLETO)
--  TABELA DE CATEGORIA
CREATE TABLE tb_categoria (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

--  TABELA DE PRODUTO
CREATE TABLE tb_produto (
    id_produto SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    valor NUMERIC(10,2) NOT NULL,
    data_cadastro TIMESTAMP NOT NULL,
    categoria_id INTEGER,

    CONSTRAINT fk_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES tb_categoria(id)
        ON DELETE RESTRICT
);
 3. Dados iniciais (opcional)
INSERT INTO tb_categoria (nome) VALUES 
('Eletrônicos'),
('Alimentos'),
('Roupas');

INSERT INTO tb_produto (nome, valor, data_cadastro, categoria_id) VALUES
('Notebook', 3500.00, NOW(), 1),
('Arroz', 25.00, NOW(), 2),
('Camiseta', 50.00, NOW(), 3);
 4. Configurar application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/catalogo
spring.datasource.username=catalogo_user
spring.datasource.password=123456

spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
 5. Rodar o projeto
mvn spring-boot:run

ou execute pela sua IDE.

 6. Acessar sistema

 http://localhost:8080/produtos
