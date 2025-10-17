# SAGE - Sistema de Análise e Gerenciamento Energético

## Visão Geral

SAGE é uma aplicação desenvolvida em **Java Spring Boot** para análise e gerenciamento do consumo energético empresarial. O sistema permite cadastrar estruturas organizacionais (empresa, setor, sala, dispositivo), monitorar consumo, registrar contas e gerar relatórios estratégicos.

## Funcionalidades Principais

* Cadastro de empresas, setores, salas e dispositivos
* Registro de potência e consumo energético
* Dashboards e relatórios (diário, semanal, mensal, anual)
* Rateio de consumo por dispositivo, sala e setor
* Autenticação e autorização com JWT
* Integração com banco **PostgreSQL 16**

## Tecnologias Utilizadas

| Tecnologia                  | Descrição                   |
| --------------------------- | --------------------------- |
| **Java 21**                 | Linguagem principal         |
| **Spring Boot**             | Framework backend           |
| **PostgreSQL 16**           | Banco de dados              |
| **Maven**                   | Gerenciador de dependências |
| **Docker & Docker Compose** | Containerização             |
| **Spring Security / JWT**   | Autenticação                |

## Docker - Como Executar

1️⃣ **Clone o repositório**

```bash
git clone https://github.com/paulohramalho/PI-II---Backend.git
cd PI-II---Backend
```

2️⃣ **Rodar com Docker Compose**

```bash
docker-compose up --build -d
```

3️⃣ **Acessar a API**

```
http://localhost:8080
```