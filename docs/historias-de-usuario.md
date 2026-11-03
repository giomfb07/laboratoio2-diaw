# Histórias de Usuário

Atores: **Cliente** (pessoa física que aluga), **Agente** (empresa ou banco que analisa pedidos e executa contratos) e **Banco** (agente que também concede crédito).

| ID | História | Critérios de aceitação |
|----|----------|------------------------|
| HU01 | Como **cliente**, quero me cadastrar informando RG, CPF, nome, endereço, profissão e até 3 rendimentos, para poder usar o sistema. | CPF com 11 dígitos e único; login único; senha com no mínimo 6 caracteres; mais de 3 rendimentos é rejeitado; após o cadastro o usuário já entra autenticado. |
| HU02 | Como **agente (empresa ou banco)**, quero me cadastrar com nome, CNPJ e tipo, para avaliar pedidos. | CNPJ com 14 dígitos e único; tipo EMPRESA ou BANCO. |
| HU03 | Como **usuário cadastrado**, quero entrar com login e senha, para acessar o sistema. | Sem login nenhuma tela interna é exibida; credenciais inválidas mostram mensagem clara; token expira em 8 horas. |
| HU04 | Como **cliente**, quero consultar e alterar meus dados, para mantê-los atualizados. | Só vejo e altero o meu cadastro; alterações respeitam as mesmas validações do cadastro. |
| HU05 | Como **cliente**, quero excluir meu cadastro, para encerrar minha conta. | Pede confirmação; remove usuário, rendimentos e pedidos; encerra a sessão. |
| HU06 | Como **cliente**, quero cadastrar um automóvel (matrícula, ano, marca, modelo e placa), para que ele possa ser alugado. | Placa e matrícula únicas; o proprietário fica registrado como o cliente. |
| HU07 | Como **empresa ou banco**, quero cadastrar automóveis da instituição. | O proprietário fica registrado como EMPRESA ou BANCO conforme o tipo do agente. |
| HU08 | Como **usuário**, quero ver a lista de automóveis e seu proprietário, para escolher um veículo. | Edição e exclusão só para o proprietário; veículo com pedidos não pode ser excluído. |
| HU09 | Como **cliente**, quero introduzir um pedido de aluguel (automóvel, período e valor), para solicitar um contrato. | Início não pode ser no passado; término posterior ao início; valor maior que zero; o pedido nasce PENDENTE. |
| HU10 | Como **cliente**, quero acompanhar o status dos meus pedidos, para saber em que etapa estão. | Lista apenas os meus pedidos; mostra status, parecer financeiro e crédito, quando houver; filtro por status. |
| HU11 | Como **cliente**, quero modificar um pedido pendente, para corrigir datas ou valor. | Só enquanto PENDENTE; mesmas validações da criação. |
| HU12 | Como **cliente**, quero cancelar um pedido, para desistir do aluguel. | Permitido enquanto não estiver CONTRATADO ou CANCELADO; pede confirmação; status passa a CANCELADO. |
| HU13 | Como **agente**, quero ver todos os pedidos e filtrá-los por status, para organizar a análise. | Lista pedidos de todos os clientes. |
| HU14 | Como **agente**, quero modificar um pedido, para ajustar condições durante a análise. | Não permitido para pedidos CANCELADOS ou CONTRATADOS. |
| HU15 | Como **agente**, quero dar um parecer financeiro positivo ou negativo, para decidir se o pedido segue. | Só para pedidos PENDENTES; parecer obrigatório; status vira APROVADO ou REPROVADO e o agente fica registrado. |
| HU16 | Como **agente**, quero executar o contrato de um pedido aprovado, para formalizar o aluguel. | Só para pedidos APROVADOS; status vira CONTRATADO. |
| HU17 | Como **banco**, quero associar um contrato de crédito ao contrato de aluguel, para financiar o cliente. | Somente agentes do tipo BANCO; valor financiado não excede o valor do pedido; registra valor, juros ao mês e parcelas. |
| HU18 | Como **agente**, quero consultar, editar e excluir clientes, para manter a base organizada. | Acesso restrito a agentes. |

## Requisitos não funcionais atendidos

- **RNF01** Sistema web: back-end em Spring Boot (Java 25), front-end React + Vite.
- **RNF02** Arquitetura MVC no back-end (model, controller; a View é a SPA React que consome JSON).
- **RNF03** Banco de dados Supabase (PostgreSQL).
- **RNF04** Autenticação obrigatória (usuário e senha), senhas com BCrypt e sessão por JWT.
- **RNF05** Dois subsistemas: gestão de pedidos e contratos (API) e construção dinâmica das páginas web (SPA React).
