# Pessoa Endereço API

API REST para cadastro de **Pessoas** e seus **Endereços**.

## Stack / Versões

- Java: **17+** (o projeto compila com `release 17`)
- Spring Boot: **4.0.2**
- Build: **Maven Wrapper** (`mvnw` / `mvnw.cmd`)
- Banco: **H2 em memória**
- Documentação: **springdoc-openapi** (`springdoc-openapi-starter-webmvc-ui` 3.0.0)

## Requisitos

- JDK **17 ou superior** instalado e no `PATH`
- (Opcional) Maven instalado — **não é necessário** por causa do Maven Wrapper

## Como rodar

### Windows (PowerShell / CMD)

- Subir a aplicação:

```bash
.\mvnw.cmd spring-boot:run
```

### Linux/macOS

```bash
./mvnw spring-boot:run
```

A aplicação sobe (por padrão) em:
- http://localhost:8080

## Como rodar os testes

### Windows

```bash
.\mvnw.cmd test
```

### Linux/macOS

```bash
./mvnw test
```

## Build (JAR)

### Windows

```bash
.\mvnw.cmd clean package
```

### Linux/macOS

```bash
./mvnw clean package
```

Executar o JAR:

```bash
java -jar target/pessoa-endereco-api-0.0.1-SNAPSHOT.jar
```

## Endpoints principais

Base path:
- `/api/pessoas`

Exemplos (cURL):

- Criar pessoa:

```bash
curl -X POST http://localhost:8080/api/pessoas \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria",
    "dataNascimento": "1990-01-01",
    "cpf": "52998224725",
    "enderecos": [
      {
        "rua": "Rua A",
        "numero": "10",
        "bairro": "Centro",
        "cidade": "Cidade",
        "estado": "SP",
        "cep": "12345678",
        "principal": true
      }
    ]
  }'
```

- Listar pessoas (paginado):

```bash
curl "http://localhost:8080/api/pessoas?page=0&size=10"
```

- Buscar por id:

```bash
curl http://localhost:8080/api/pessoas/1
```

- Atualizar pessoa (PUT)

Observação: após as mudanças de update/merge de endereços, o `PUT` aceita `id` opcional em cada item de endereço.
Para atualizar um endereço existente **sem perder o id**, envie o `id` do endereço no payload.

Passo 1: busque a pessoa para descobrir o `id` do endereço:

```bash
curl http://localhost:8080/api/pessoas/1
```

Passo 2: faça o `PUT` incluindo o `id` do endereço (exemplo atualizando o nome e o número do endereço):

```bash
curl -X PUT http://localhost:8080/api/pessoas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria da Silva",
    "dataNascimento": "1990-01-01",
    "cpf": "52998224725",
    "enderecos": [
      {
        "id": 1,
        "rua": "Rua A",
        "numero": "11",
        "bairro": "Centro",
        "cidade": "Cidade",
        "estado": "SP",
        "cep": "12345678",
        "principal": true
      }
    ]
  }'
```

Dica: no `PUT`, envie a lista completa de endereços que você quer manter (endereços omitidos podem ser removidos no merge).

## Swagger / OpenAPI

- Swagger UI:
  - http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON:
  - http://localhost:8080/v3/api-docs

## H2 Console

Config padrão (ver `src/main/resources/application.properties`):

- Console:
  - http://localhost:8080/h2-console
- JDBC URL:
  - `jdbc:h2:mem:pessoadb`
- Usuário:
  - `sa`
- Senha:
  - (vazia)

## Observações

- O banco H2 é **em memória**: os dados são perdidos ao reiniciar a aplicação.
- Validações de entrada usam Bean Validation (ex.: CPF válido, `dataNascimento` no passado, ao menos 1 endereço e exatamente 1 principal).
