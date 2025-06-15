-- Criar banco de dados
CREATE DATABASE SistemaLanchonete;

-- Usar banco de dados
USE SistemaLanchonete;

-- Tabela Clientes
CREATE TABLE Clientes (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL,
    CPF CHAR(11) UNIQUE NOT NULL,
    Telefone VARCHAR(15)
);

-- Tabela Funcionarios
CREATE TABLE Funcionarios (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL,
    Cargo VARCHAR(50),
    Login VARCHAR(50) UNIQUE NOT NULL,
    Senha VARCHAR(255) NOT NULL
);

-- Tabela Produtos
CREATE TABLE Produtos (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL,
    Categoria VARCHAR(50),
    Preco DECIMAL(10,2) NOT NULL,
    Estoque INT DEFAULT 0
);

-- Tabela Pedidos
CREATE TABLE Pedidos (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ID_Cliente INT NOT NULL,
    ID_Produto INT NOT NULL,
    Quantidade INT NOT NULL,
    Valor_Total DECIMAL(10,2) NOT NULL,estoque
    Status VARCHAR(20) DEFAULT 'Pendente',
    FOREIGN KEY (ID_Cliente) REFERENCES Clientes(ID),
    FOREIGN KEY (ID_Produto) REFERENCES Produtos(ID)
);

-- Tabela Estoque
CREATE TABLE Estoque (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ID_Produto INT NOT NULL,
    Quantidade_Entrada INT DEFAULT 0,
    Quantidade_Saida INT DEFAULT 0,
    FOREIGN KEY (ID_Produto) REFERENCES Produtos(ID)
);


select * from clientes;