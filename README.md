
# Sistema de Cupons - Desafio

API REST para gerenciamento de cupons de desconto construída com Spring Boot.

## Como Executar

### Pré-requisitos
- Docker e Docker Compose instalados
- Porta 8080 disponível
```markdown
### Passos

1. **Clone o repositório**
```bash
git clone https://github.com/JoaoUntura/cupom_desafio
cd cupom_desafio
```

2. **Verifique se a porta 8080 está disponível**
```bash
# Linux/Mac
lsof -i :8080

# Windows
netstat -ano | findstr :8080
```

3. **Inicie a aplicação**
```bash
docker compose up -d
```

4. **Acesse a documentação da API**
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Ou utilize sua ferramenta de testes preferida (Postman, Insomnia, etc.)

## Arquitetura da Aplicação

A aplicação segue os princípios de **Clean Architecture** e **Domain-Driven Design (DDD)**.

### Camadas

#### Domain (Domínio)
- **Cupom**: Entidade principal que agrega os domínios abaixo
    - Criação de cupons
    - Validações de descrição, deleção e data de expiração

- **CupomCode**: Normaliza e valida códigos alfanuméricos de 6 caracteres

- **Discount**: Valida valores de desconto (mínimo de R$ 0,50)

- **CupomRepository**: Interface com operações de domínio (findById, etc.)

- **Exceptions**: Exceções específicas de domínio

#### Application (Aplicação)
Casos de uso que orquestram a lógica de negócio:

- **CreateCupomUseCase**
    - Verifica existência e unicidade do código
    - Utiliza lock para evitar códigos duplicados em operações concorrentes
    - Persiste o cupom através do repositório

- **DeleteCupomUseCase**
    - Encontra o cupom
    - Verifica se já não está deletado
    - Realiza soft delete e salva a data de deleção
    - Utiliza Optimistic Lock via versions para evitar duplicação de operação

- **FindCupomUseCase**
    - Busca cupom por ID através do repositório

- **DTOs**: Objetos de transferência para criação e resposta

- **Exceptions**: Exceções específicas da camada de aplicação

#### Infrastructure (Infraestrutura)
- **Entity**: Entidade JPA do cupom para persistência em banco de dados
- **JpaRepository**: Interface para operações no banco
- **Repository Implementation**: Implementação da interface de repositório do domínio usando JPA

#### Web (Apresentação)
- **Controllers**: Endpoints REST
    - `GET /cupom/{id}` - Buscar cupom por ID
    - `POST /cupom` - Criar novo cupom
    - `DELETE /cupom/{id}` - Deletar cupom

- **GlobalExceptionHandler**: Tratamento centralizado de exceções personalizadas

### Testes
Suite completa de testes cobrindo:
- Todas as regras de negócio do domínio
- Validações de entidades
- Casos de uso
- Comportamentos esperados e edge cases

## Tecnologias Utilizadas
- Java
- Spring Boot
- JPA/Hibernate
- Docker
- Swagger/OpenAPI

## Licença
Este projeto foi desenvolvido como desafio técnico.
```
