# YourPetHealth — Sistema de Gestão Clínica Veterinária

Aplicação web e API REST para gestão de clínica veterinária, desenvolvida em Java com Spring
Boot. O sistema permite que tutores cadastrem seus pets e agendem consultas, e que veterinários
consultem sua agenda, realizem atendimentos e registrem o histórico clínico dos animais.

A aplicação oferece duas formas de acesso ao mesmo conjunto de regras de negócio:

- **Interface web** (Thymeleaf), com autenticação por sessão
- **API REST** sob `/api`, com autenticação por JWT, consumida pelo aplicativo mobile

---

# Desenvolvido por

- Guilherme Cintra RM562850
- Erick de Faria Gama RM561951
- Matheus Nascimento Corregio RM563765
- Pedro Fonseca de Almeida RM563466
- Daniel Fonseca de Almeida RM563045

# Link do Deploy
`https://yourpethealth-java.onrender.com`

# Sumário

- [Objetivo](#objetivo)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Segurança e perfis de acesso](#segurança-e-perfis-de-acesso)
- [Banco de dados](#banco-de-dados)
- [Instalação e execução](#instalação-e-execução)
- [Acessando a aplicação](#acessando-a-aplicação)
- [Rotas da interface web](#rotas-da-interface-web)
- [Endpoints da API REST](#endpoints-da-api-rest)
- [Fluxos completos](#fluxos-completos)
- [Validações e regras de negócio](#validações-e-regras-de-negócio)
- [Tratamento de exceções](#tratamento-de-exceções)
- [Testes](#testes)
- [Diagramas](#diagramas)
- [Cronograma](#cronograma)

---

# Objetivo

A aplicação auxilia no acompanhamento clínico contínuo de animais de estimação, permitindo:

- Cadastro e autenticação de tutores e veterinários
- Gestão dos pets de cada tutor
- Agendamento de consultas com validação de disponibilidade
- Registro de atendimentos pelo veterinário
- Histórico clínico gerado automaticamente a cada consulta concluída

Cada usuário acessa apenas os dados que lhe pertencem: um tutor vê somente seus próprios pets e
consultas; um veterinário vê apenas a agenda dele.

---

# Tecnologias

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem |
| Spring Boot | Framework base |
| Spring Data JPA / Hibernate | Persistência |
| Spring Security | Autenticação e autorização |
| java-jwt (auth0) | Geração e validação dos tokens JWT |
| Spring HATEOAS | Links de navegação nas respostas da API |
| Flyway | Versionamento do schema do banco |
| Thymeleaf | Camada de visualização web |
| Bootstrap 5 | Estilos da interface web |
| Lombok | Redução de código repetitivo nas entidades |
| Oracle Database | Banco de dados |
| Maven | Build e dependências |
| Swagger / springdoc-openapi | Documentação da API |

---

# Arquitetura

O projeto segue arquitetura em camadas. O ponto central do desenho é que **os controllers web e
os controllers REST chamam os mesmos services** — nenhuma regra de negócio é duplicada, muda
apenas o que cada um devolve (template HTML ou JSON).

![Arquitetura](documentos/imagens/ArquiteturaYourPetHealth.drawio.png)

## Camadas

### Controller

Dividido em dois pacotes:

- `controller/api` — `@RestController` sob `/api`, consumidos pelo app mobile
- `controller/web` — `@Controller` que devolvem templates Thymeleaf

### Service

Concentra as regras de negócio: validação de posse dos dados, regras de agendamento, transição de
status das consultas e geração automática do histórico clínico.

### Repository

Interfaces Spring Data JPA para acesso ao banco.

### Entity

Entidades JPA mapeadas para as tabelas do Oracle, com Lombok (`@Getter`, `@Setter`, `@Builder`).

### DTO

Registros (`record`) que trafegam entre cliente e API, separados em `request` e `response`.

### Form

Classes de formulário usadas pela camada web, com Bean Validation e conversão para os DTOs da API.

### Assembler

Componentes que adicionam os links HATEOAS às respostas REST.

### Security

Configuração das duas cadeias de filtros, filtro JWT, serviço de tokens e o componente que expõe o
usuário autenticado para os services.

---

# Estrutura do projeto

```text
src/main/java/br/com/yourpethealth
├── assembler          Links HATEOAS das respostas REST
├── controller
│   ├── api            Controllers REST (/api/**)
│   └── web            Controllers web (Thymeleaf)
├── dto
│   ├── request        Dados de entrada
│   └── response       Dados de saída
├── entity
│   └── enums          Perfil, SexoPet, StatusConsulta, TipoConsulta, TipoHistorico
├── exception          Exceções de domínio e handlers globais
├── form               Objetos de formulário da camada web
├── repository         Interfaces Spring Data JPA
├── security           SecurityConfig, SecurityFilter, JwtService, UsuarioLogado
└── service            Regras de negócio

src/main/resources
├── db/migration       Migrations Flyway
├── static
│   ├── css/app.css
│   └── js/app.js
└── templates
    ├── layout.html    Fragmentos comuns (head, navbar, mensagens, footer)
    ├── login.html  cadastro.html  403.html  erro.html
    ├── app/           Telas do tutor
    └── vet/           Telas do veterinário
```

---

# Segurança e perfis de acesso

## Duas cadeias de filtros

A aplicação atende dois clientes com mecanismos de autenticação diferentes, configurados em
`SecurityConfig`:

| Cadeia | Rotas | Autenticação | Sessão |
|---|---|---|---|
| API | `/api/**` | JWT no header `Authorization: Bearer <token>` | Stateless |
| Web | demais rotas | Formulário de login | Sessão HTTP |

Ambas compartilham o mesmo `UserDetailsService` e o mesmo banco de usuários.

## Perfis

| Perfil | Acesso |
|---|---|
| `RESPONSAVEL` | Área `/app/**` — seus pets, suas consultas, histórico dos seus pets |
| `VETERINARIO` | Área `/vet/**` — agenda do dia, atendimento, busca de pets |
| `ADMIN` | Rotas administrativas de responsáveis e veterinários |

Após o login, o usuário é redirecionado automaticamente para a área do seu perfil. Tentativas de
acessar a área do outro perfil resultam em **403**, exibido como página de erro na web e como JSON
na API.

## Isolamento de dados

Além da separação por perfil, os services validam a posse de cada recurso: um tutor não consegue
ler, editar ou remover pets e consultas de outro tutor, mesmo informando o identificador correto
diretamente na API.

## Senhas

Armazenadas com BCrypt (força 10) na tabela `yp_t_usuarios`. As tabelas de tutores e veterinários
não guardam credenciais.

---

# Banco de dados

Oracle Database, com schema versionado por Flyway. A propriedade `spring.jpa.hibernate.ddl-auto`
está fixada em `validate`: o Hibernate nunca altera o schema, apenas confere se o mapeamento
corresponde às tabelas criadas pelas migrations.

## Tabelas

Todas as tabelas usam o prefixo `yp_t_`.

| Tabela | Conteúdo |
|---|---|
| `yp_t_usuarios` | Credenciais e perfil de acesso |
| `yp_t_responsaveis` | Tutores |
| `yp_t_veterinarios` | Veterinários, com CRMV e especialidade |
| `yp_t_pets` | Animais, vinculados a um tutor |
| `yp_t_consultas` | Consultas, vinculadas a um pet e a um veterinário |
| `yp_t_historico_clinico` | Registros clínicos gerados ao concluir consultas |

## Migrations

Localizadas em `src/main/resources/db/migration`:

| Arquivo | Conteúdo |
|---|---|
| `V1__baseline_schema.sql` | Tabelas de tutores, veterinários, pets, consultas e histórico |
| `V2__usuarios.sql` | Tabela de autenticação |

As migrations são aplicadas automaticamente na inicialização da aplicação.

## Relacionamentos

| Relacionamento | Cardinalidade |
|---|---|
| Responsável → Pet | Um tutor possui vários pets |
| Pet → Consulta | Um pet possui várias consultas |
| Veterinário → Consulta | Um veterinário realiza várias consultas |
| Pet → Histórico clínico | Um pet possui vários registros clínicos |
| Usuário → Responsável / Veterinário | Cada login referencia um tutor **ou** um veterinário |

## Constraints

**Primary key** — identificação única, com `GENERATED BY DEFAULT AS IDENTITY`.

**Foreign key** — integridade dos relacionamentos: pet vinculado ao tutor, consulta vinculada ao
pet e ao veterinário, histórico vinculado ao pet.

**Unique** — e-mail (em usuários, tutores e veterinários) e CRMV.

**Check** — validação de valores: sexo do pet, status e tipo da consulta, perfil do usuário e a
regra que garante que cada usuário esteja vinculado a exatamente um tipo de perfil.

---

# Instalação e execução

## Pré-requisitos

- **Java 21** ou superior (`java -version` para conferir)
- **Maven 3.9+** — ou use o wrapper `./mvnw` incluído no projeto
- **Acesso a um banco Oracle** com permissão para criar tabelas

## 1. Clonar o repositório

```bash
git clone <url-do-repositorio>
cd yourpethealth
```

## 2. Configurar as variáveis de ambiente

A aplicação **não sobe sem estas quatro variáveis**. Nenhum segredo é versionado no repositório.

| Variável | Descrição | Exemplo |
|---|---|---|
| `DB_URL` | URL JDBC do Oracle | `jdbc:oracle:thin:@localhost:1521/XEPDB1` |
| `DB_USER` | Usuário do banco | `yourpethealth` |
| `DB_PASSWORD` | Senha do banco | — |
| `JWT_SECRET` | Chave de assinatura dos tokens, **mínimo 32 caracteres** | — |

> A chave JWT precisa ter pelo menos 32 caracteres. O algoritmo HS256 exige uma chave de 256 bits
> e a aplicação falha na inicialização com uma chave menor.

**Linux / macOS:**

```bash
export DB_URL="jdbc:oracle:thin:@localhost:1521/XEPDB1"
export DB_USER="yourpethealth"
export DB_PASSWORD="sua-senha"
export JWT_SECRET="uma-chave-secreta-com-pelo-menos-32-caracteres"
```

**Windows (PowerShell):**

```powershell
$env:DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1"
$env:DB_USER = "yourpethealth"
$env:DB_PASSWORD = "sua-senha"
$env:JWT_SECRET = "uma-chave-secreta-com-pelo-menos-32-caracteres"
```

**Pela IDE:** IntelliJ IDEA em *Run > Edit Configurations > Environment variables*; Eclipse em
*Run Configurations > Environment*.

## 3. Executar

```bash
./mvnw spring-boot:run
```

Ou, gerando o `.jar`:

```bash
./mvnw clean package -DskipTests
java -jar target/yourpethealth-0.0.1-SNAPSHOT.jar
```

Na primeira execução o Flyway cria as tabelas automaticamente. Para confirmar:

```sql
SELECT "version", "description", "success"
  FROM "flyway_schema_history"
 ORDER BY "installed_rank";
```

## 4. Verificar

A aplicação sobe em `http://localhost:8080`. Se a inicialização falhar, os erros mais comuns são:

| Mensagem | Causa |
|---|---|
| `Empty key` | `JWT_SECRET` não definida ou vazia |
| `Unsupported Database: Oracle` | Falta a dependência `flyway-database-oracle` |
| `Schema-validation: missing table` | Migrations não aplicadas — confira as credenciais do banco |
| `Failed to configure a DataSource` | `DB_URL`, `DB_USER` ou `DB_PASSWORD` ausentes |

---

# Acessando a aplicação

## Interface web

```
http://localhost:8080
```

Na primeira execução o banco está vazio. Crie as contas pela própria interface:

1. Acesse `http://localhost:8080/cadastro`
2. Para acessar a área do tutor, escolha **Tutor de pet**
3. Para acessar a área clínica, escolha **Veterinário** e informe o CRMV
4. Faça login — o sistema redireciona automaticamente para a área do perfil escolhido

Para demonstrar o sistema por completo, crie **uma conta de cada perfil**: o tutor agenda a
consulta e o veterinário a atende.

## Documentação da API

```
http://localhost:8080/swagger-ui.html
```

Para testar endpoints protegidos no Swagger:

1. Execute `POST /api/auth/register` ou `POST /api/auth/login`
2. Copie o valor do campo `token` da resposta
3. Clique em **Authorize** no topo da página e cole apenas o token, sem o prefixo `Bearer`
4. Os endpoints protegidos passam a exibir o cadeado fechado

---

# Rotas da interface web

## Públicas

| Rota | Descrição |
|---|---|
| `GET /login` | Tela de login |
| `GET /cadastro` · `POST /cadastro` | Criação de conta |

## Área do tutor (`RESPONSAVEL`)

| Rota | Descrição |
|---|---|
| `GET /app/home` | Resumo: total de pets e consultas das próximas 48 horas |
| `GET /app/pets` | Lista de pets |
| `GET /app/pets/novo` · `POST /app/pets` | Cadastro de pet |
| `GET /app/pets/{id}` | Detalhe do pet com suas consultas |
| `GET /app/pets/{id}/editar` · `POST /app/pets/{id}` | Edição de pet |
| `POST /app/pets/{id}/remover` | Remoção de pet |
| `GET /app/pets/{id}/historico` | Histórico clínico do pet |
| `GET /app/consultas` | Lista de consultas |
| `GET /app/consultas/nova` · `POST /app/consultas` | Agendamento |
| `GET /app/consultas/{id}/editar` · `POST /app/consultas/{id}` | Reagendamento |
| `POST /app/consultas/{id}/cancelar` | Cancelamento (mantém o registro) |
| `POST /app/consultas/{id}/remover` | Exclusão do registro |

## Área clínica (`VETERINARIO`)

| Rota | Descrição |
|---|---|
| `GET /vet/agenda` | Agenda do dia, com seletor de data |
| `GET /vet/atender/{id}` · `POST /vet/atender/{id}` | Atendimento e conclusão da consulta |
| `GET /vet/pets` | Busca de pets por nome |

## Comuns

| Rota | Descrição |
|---|---|
| `GET /403` | Acesso negado |
| `POST /logout` | Encerramento da sessão |

---

# Endpoints da API REST

Base: `http://localhost:8080/api`

Todas as rotas exigem o header `Authorization: Bearer <token>`, exceto login e cadastro.

## Autenticação

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/auth/register` | Cria usuário e perfil, devolve o token |
| `POST` | `/api/auth/login` | Autentica e devolve o token |
| `GET` | `/api/auth/me` | Dados do usuário autenticado |

## Pets

| Método | Rota | Perfil |
|---|---|---|
| `GET` | `/api/pets` | RESPONSAVEL — apenas os seus |
| `GET` | `/api/pets/{id}` | O tutor dono, ou qualquer veterinário |
| `GET` | `/api/pets/buscar?nome=` | VETERINARIO |
| `POST` | `/api/pets` | RESPONSAVEL |
| `PUT` | `/api/pets/{id}` | O tutor dono |
| `DELETE` | `/api/pets/{id}` | O tutor dono |

## Consultas

| Método | Rota | Perfil |
|---|---|---|
| `GET` | `/api/consultas` | RESPONSAVEL |
| `GET` | `/api/consultas/{id}` | O tutor dono ou o veterinário da consulta |
| `GET` | `/api/consultas/agenda?data=AAAA-MM-DD` | VETERINARIO |
| `POST` | `/api/consultas` | RESPONSAVEL |
| `PUT` | `/api/consultas/{id}` | RESPONSAVEL — apenas se agendada |
| `PATCH` | `/api/consultas/{id}/cancelar` | RESPONSAVEL |
| `PATCH` | `/api/consultas/{id}/concluir` | VETERINARIO |
| `DELETE` | `/api/consultas/{id}` | RESPONSAVEL — apenas se agendada |

> `DELETE` e `PATCH /cancelar` são operações distintas: o primeiro apaga o registro, o segundo
> altera o status para `CANCELADA` preservando o histórico.

## Histórico clínico

| Método | Rota | Perfil |
|---|---|---|
| `GET` | `/api/pets/{petId}/historico` | O tutor dono, ou qualquer veterinário |
| `GET` | `/api/historico/{id}` | idem |

Somente leitura — os registros são criados automaticamente ao concluir uma consulta.

## Veterinários

| Método | Rota | Perfil |
|---|---|---|
| `GET` | `/api/veterinarios` | Qualquer usuário autenticado |
| `GET` | `/api/veterinarios/{id}` | Qualquer usuário autenticado |

---

# Fluxos completos

## Fluxo 1 — Agendamento com validação de disponibilidade

O tutor escolhe o pet, o veterinário e o horário. Antes de criar a consulta, o sistema verifica em
sequência:

1. O pet pertence ao tutor autenticado
2. A data respeita a antecedência mínima de 2 horas
3. O horário está dentro do expediente (segunda a sábado, das 08:00 às 18:00)
4. O veterinário não tem outra consulta na janela de 1 hora
5. O pet não tem outra consulta marcada no mesmo horário

Qualquer regra violada interrompe o agendamento e devolve a mensagem correspondente ao usuário. As
mesmas validações são aplicadas no reagendamento.

## Fluxo 2 — Atendimento e registro clínico

O veterinário abre a agenda do dia, seleciona a consulta e registra as observações do atendimento.
Ao concluir, dentro de uma única transação:

1. O status da consulta passa para `REALIZADA`
2. As observações são gravadas na consulta
3. Um registro é criado automaticamente no histórico clínico do pet

Se a criação do histórico falhar, a conclusão é revertida — a consulta não fica marcada como
realizada sem o registro clínico correspondente.

---

# Validações e regras de negócio

## Validação de campos

Aplicada com Bean Validation nos DTOs e nos formulários. Na interface web, as mensagens são
exibidas abaixo de cada campo; na API, retornam no array `campos` da resposta de erro.

| Entidade | Regras |
|---|---|
| Pet | Nome de 2 a 100 caracteres · raça até 100 · idade entre 0 e 30 · peso maior que 0 e menor que 200 · sexo `MACHO` ou `FEMEA` |
| Consulta | Pet, veterinário, tipo e data obrigatórios · data futura · descrição até 1000 caracteres |
| Conclusão | Observações obrigatórias, de 10 a 1000 caracteres |
| Cadastro | Nome de 2 a 100 · e-mail válido · senha com no mínimo 8 caracteres · CRMV obrigatório para veterinários |

## Regras de estado

- Apenas consultas com status `AGENDADA` podem ser reagendadas, canceladas, concluídas ou
  excluídas
- Um pet com consultas agendadas não pode ser removido
- Uma consulta concluída não pode ser concluída novamente
- Consultas canceladas liberam o horário para novos agendamentos

## Controle de edição

As respostas de consulta trazem os campos `podeEditar` e `podeCancelar`, calculados no backend
considerando o status e a antecedência de 24 horas. Os clientes usam esses valores para habilitar
os botões, sem replicar a regra.

---

# Tratamento de exceções

O tratamento é centralizado e separado por tipo de cliente:

- `GlobalExceptionHandler` (`@RestControllerAdvice`) — respostas JSON padronizadas para a API
- `WebExceptionHandler` (`@ControllerAdvice`) — páginas de erro para a interface web

## Formato de erro da API

```json
{
  "timestamp": "2026-09-07T10:32:11",
  "status": 400,
  "erro": "VALIDACAO",
  "mensagem": "Há campos inválidos na requisição",
  "path": "/api/pets",
  "campos": [
    { "campo": "idade", "mensagem": "Idade deve ser no máximo 30" }
  ]
}
```

| Código | Status | Situação |
|---|---|---|
| `VALIDACAO` | 400 | Campo inválido, enum desconhecido, regra de horário |
| `NAO_AUTORIZADO` | 401 | Token ausente, expirado ou credenciais inválidas |
| `ACESSO_NEGADO` | 403 | Recurso de outro usuário ou perfil sem permissão |
| `NAO_ENCONTRADO` | 404 | Identificador inexistente |
| `CONFLITO` | 409 | Conflito de agenda, estado inválido, e-mail duplicado |
| `ERRO_INTERNO` | 500 | Falha não tratada |

O campo `campos` aparece somente em erros de validação de formulário. Mensagens internas nunca são
expostas ao cliente — detalhes de falhas ficam registrados apenas no log da aplicação.

---

# Testes

Os endpoints foram testados com **Postman** e **Swagger UI**. A coleção do Postman está disponível
na pasta `documentos`, com os testes organizados por área e verificações automáticas de status,
formato de resposta e regras de negócio.

Cenários cobertos:

- Cadastro e autenticação nos dois perfis
- CRUD completo de pets e consultas
- Isolamento de dados entre tutores diferentes
- Bloqueio de acesso entre perfis
- As cinco regras de agendamento
- Ciclo completo: agendar, atender, concluir e gerar histórico
- Validação de campos e tratamento de erros

---

# Diagramas

## Diagrama de classes

![Diagrama de classes](documentos/imagens/DiagramaYourPetHealth.drawio.png)

## Diagrama entidade-relacionamento

![DER](documentos/imagens/DER.jpeg)

---

# Cronograma

![Cronograma](documentos/imagens/Print1Responsabilidades.png)
![Cronograma](documentos/imagens/Print2Responsabilidades.png)

O cronograma organiza as etapas do desenvolvimento: modelagem do banco, implementação do backend,
segurança, interface web, documentação e testes.
