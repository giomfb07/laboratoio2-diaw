# Sistema de Aluguel de Carros

Laboratório 02 · DIAW · Engenharia de Software · PUC Minas
Professor: João Paulo Carneiro Aramuni

Sistema web para gestão de aluguéis de automóveis. Clientes criam, modificam, consultam e cancelam pedidos; agentes (empresas e bancos) avaliam os pedidos, executam o contrato e, no caso dos bancos, associam um crédito.

**Integrantes:** _preencher nome e matrícula de cada integrante_

## Tecnologias

- Back-end: Java 25, Spring Boot 4.0.0 (Web MVC, Data JPA, Validation, Security + JWT), Maven
- Front-end: React 19, React Router 7, Vite 7
- Banco de dados: Supabase (PostgreSQL)

## Estrutura

```
aluguel-carros/
├── backend/    # Spring Boot: config, controller, dto, exception, model, repository, security, service
├── frontend/   # React + Vite: components, pages, api.js
└── docs/       # histórias de usuário, diagramas UML e schema.sql
```

## Instalação

Pré-requisitos: JDK 25, Maven, Node.js 20.19+ e um projeto no [Supabase](https://supabase.com).

**Back-end**

```bash
cd backend
cp .env.example .env
```

Preencha no `.env` as variáveis `SUPABASE_DB_PASSWORD` e `JWT_SECRET` (mínimo de 32 caracteres). A URL e o usuário do banco ficam em `src/main/resources/application.yml`; ajuste-os para o seu projeto Supabase. Depois:

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080` e as tabelas são criadas automaticamente.

**Front-end**

```bash
cd frontend
npm install
npm run dev
```

Acesse `http://localhost:5173`.

## Ciclo de vida do pedido

```
PENDENTE ──aprovação──▶ APROVADO ──contrato──▶ CONTRATADO
    ├──reprovação──▶ REPROVADO
    └──cliente cancela──▶ CANCELADO
```

## Regras principais

- Todas as rotas, exceto login e cadastros, exigem token JWT.
- Cliente: RG, CPF, nome, endereço, profissão e no máximo 3 rendimentos.
- Automóvel: matrícula, ano, marca, modelo e placa; o proprietário pode ser cliente, empresa ou banco.
- O cliente só acessa os próprios dados e pedidos; os agentes avaliam e modificam.