# Sistema de Inscricao em Oficinas

Uma API REST feita com Spring Boot para consultar participantes, gerenciar oficinas e registrar/cancelar inscricoes. O projeto usa Java 21, Maven e PostgreSQL.

> Feito para organizar oficinas sem perder o historico das inscricoes. Bora! :-)

## O que a API faz?

- Consulta participantes e oficinas.
- Cadastra, edita e exclui oficinas, respeitando o historico de inscricoes.
- Registra inscricoes e controla as vagas disponiveis.
- Cancela inscricoes sem apagar o historico.
- Ordena a lista de oficinas pela quantidade de vagas disponiveis, da maior para a menor.

Sem pagamentos, certificados, lista de espera ou controle de conflitos de agenda. `:-P`

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring JDBC (`JdbcTemplate`)
- PostgreSQL
- Maven Wrapper

## Pre-requisitos

1. JDK 21 instalado.
2. PostgreSQL acessivel pela aplicacao.
3. As tabelas `participantes`, `oficinas` e `inscricoes` criadas no banco.

Confira o Java no PowerShell:

```powershell
java -version
```

Se `JAVA_HOME` ainda nao estiver definido, configure-o para a pasta onde o JDK 21 foi instalado. Exemplo:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

O caminho varia conforme a instalacao. Depois, confirme:

```powershell
$env:JAVA_HOME
java -version
```

## Configurar o PostgreSQL

A aplicacao le a configuracao padrao do Spring Boot. Configure a URL, o usuario e a senha por variaveis de ambiente no terminal antes de iniciar. Exemplo para um PostgreSQL local:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/oficinas"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "sua_senha"
```

Troque os valores de exemplo pelos dados do seu banco. **Nao coloque senhas reais no README, no codigo ou em commits.** As variaveis definidas dessa forma ficam disponiveis apenas no terminal atual.

As tabelas esperadas sao:

- `participantes`: `id`, `nome`, `email`
- `oficinas`: `id`, `titulo`, `capacidade`, `vagas_disponiveis`
- `inscricoes`: `id`, `participante_id`, `oficina_id`, `data_registro`, `situacao`

## Executar o projeto

No PowerShell, na pasta raiz do repositorio:

```powershell
.\mvnw.cmd spring-boot:run
```

Por padrao, o servidor fica disponivel em `http://localhost:8080`. Para parar, pressione `Ctrl+C`. Tudo certo? `\o/`

### Executar com debugger

Para iniciar a aplicacao com o debugger Java habilitado na porta `5005`:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

Configure o debugger da IDE para conectar em `localhost:5005`. A porta `5005` e para o protocolo JDWP; acesse as rotas HTTP na porta `8080`. Por exemplo, `/api/oficinas` deve ser chamado em `http://localhost:8080/api/oficinas`, nao na porta do debugger. :-)

## Rotas

Todas as rotas usam o prefixo `/api`.

| Metodo | Rota | Descricao |
| --- | --- | --- |
| `GET` | `/api/participantes` | Lista todos os participantes. |
| `GET` | `/api/participantes/{id}` | Consulta um participante pelo ID. |
| `POST` | `/api/oficinas` | Cadastra uma oficina; as vagas comecam iguais a capacidade. |
| `GET` | `/api/oficinas` | Lista oficinas, ordenadas por vagas disponiveis decrescentes. |
| `GET` | `/api/oficinas/{id}` | Consulta uma oficina pelo ID. |
| `PUT` | `/api/oficinas/{id}` | Atualiza titulo e capacidade (com restricoes de historico). |
| `DELETE` | `/api/oficinas/{id}` | Exclui uma oficina somente se nao houver inscricoes no historico. |
| `POST` | `/api/inscricoes` | Registra uma inscricao e consome uma vaga. |
| `GET` | `/api/inscricoes` | Lista o historico de inscricoes. |
| `PATCH` | `/api/inscricoes/{id}/cancelamento` | Cancela uma inscricao ativa e devolve uma vaga. |

### Exemplos de requisicao

Cadastrar uma oficina:

```http
POST /api/oficinas
Content-Type: application/json

{
  "titulo": "Introducao a programacao",
  "capacidade": 20
}
```

O servidor define `vagas_disponiveis` com o valor de `capacidade`.

Registrar uma inscricao:

```http
POST /api/inscricoes
Content-Type: application/json

{
  "participante_id": 1,
  "oficina_id": 1
}
```

Atualizar uma oficina:

```http
PUT /api/oficinas/1
Content-Type: application/json

{
  "titulo": "Programacao para iniciantes",
  "capacidade": 20
}
```

Cancelar uma inscricao:

```http
PATCH /api/inscricoes/1/cancelamento
```

Tambem e possivel testar a API com PowerShell:

```powershell
Invoke-RestMethod http://localhost:8080/api/oficinas
Invoke-RestMethod http://localhost:8080/api/participantes/1
```

## Regras de negocio

- Participante e oficina precisam existir para registrar uma inscricao.
- A oficina precisa ter pelo menos uma vaga disponivel.
- O mesmo participante nao pode ter duas inscricoes ativas na mesma oficina.
- Criar uma inscricao e consumir sua vaga ocorre na mesma transacao.
- Cancelar uma inscricao ativa e devolver sua vaga ocorre na mesma transacao.
- Uma inscricao cancelada nao pode ser cancelada novamente; uma nova inscricao cria um novo registro no historico.
- A capacidade nao pode ser alterada depois de existir historico de inscricoes; o titulo ainda pode ser editado.
- Uma oficina com historico de inscricoes nao pode ser excluida.
- Respostas de erro usam `400` para requisicao invalida, `404` para recurso inexistente e `409` para conflito com regras de negocio.

## Arquitetura

```text
controller -> businessprocess -> repository -> PostgreSQL
```

- `controller`: rotas HTTP e formatos das requisicoes.
- `businessprocess`: regras de negocio, ordenacao e coordenacao das transacoes.
- `repository`: consultas SQL e acesso ao banco com `JdbcTemplate`.
- `controller` chama somente `businessprocess`; somente `repository` acessa o banco. Cada camada no seu quadrado! `B-)`

## Testes

Os testes unitarios usam mocks e nao precisam conectar ao PostgreSQL:

```powershell
.\mvnw.cmd test
```

Eles cobrem as rotas REST, a delegacao entre camadas e cenarios principais das regras de inscricao e oficinas. Como usam mocks, nao validam integracao real com PostgreSQL, bloqueios concorrentes ou rollback do banco.

## Estrutura do projeto

```text
src/
  main/
    java/api_oficina/com/example/oficina_api_demo/
      businessprocess/  # regras de negocio
      controller/       # endpoints e DTOs
      repository/       # acesso JDBC
    resources/
      application.properties
  test/
    java/               # testes unitarios
pom.xml                 # dependencias e configuracao Maven
mvnw.cmd                # Maven Wrapper para Windows
```

Duvidas? Rode os testes, confira a configuracao do banco e tente de novo. `(^_^)`

```text
                     ♫ ♬ ♪  .  *  .  ♪ ♬ ♫
              ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
           .-'"""""'-.      .-'"""""'-.      .-'"""""'-.
          /  @     @  \    /  O     O  \    /  ^     ^  \
         |     ._.     |  |     ._.     |  |     ._.     |
          \    '-'    /    \    '-'    /    \    '-'    /
           '-.....-'        '-.....-'        '-.....-'
              \o/              \o/              \o/
               |                |                |
    /// ~~~    |                |                |    ~~~ ///
   /// ~~~   __|________________|________________|__   ~~~ ///
  /// ~~~   /=======================================\   ~~~ ///
 /// ~~~   /  *   *   *   *   *   *   *   *   *   *  \   ~~~ ///
/// ~~~   |  [=======================================]  |   ~~~ ///
/// ~~~   |  |  [ ]   [ ]   [ ]   [ ]   [ ]   [ ]  |   |   ~~~ ///
/// ~~~   |  |  [ ]   [ ]   [ ]   [ ]   [ ]   [ ]  |   |   ~~~ ///
/// ~~~   |__|_______________________________________|___|   ~~~ ///
 /// ~~~     /  \                               /  \      ~~~ ///
  /// ~~~   /____\                             /____\     ~~~ ///
           (  O  )                           (  O  )
            \___/                             \___/
          __|___|_____________________________|___|__
         /___________________________________________\
                    ♫ ♬ ♪  FUERAÇÃO  ♪ ♬ ♫
```
