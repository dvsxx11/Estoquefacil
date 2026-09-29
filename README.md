# Controle de Fluxo de Produtos

Aplicação web para cadastrar produtos e registrar entradas e saídas de estoque por empresa. O projeto reúne uma API REST em Spring Boot e uma interface em Vue 3. Cada produto, usuário e movimentação pertence a uma empresa identificada por um `tenant_id`.

## Funcionalidades implementadas

- Login por e-mail e senha, com senha verificada por BCrypt e emissão de JWT válido por 24 horas.
- Listagem e cadastro de produtos, com SKU, descrição, preços, quantidade atual e estoque mínimo.
- Busca de produtos por nome ou SKU e filtros de estoque baixo ou zerado na interface.
- Registro de entradas e saídas, cálculo do valor total e atualização do saldo do produto na mesma transação.
- Histórico de movimentações por empresa, exibido da mais recente para a mais antiga.
- Bloqueio de saída quando a quantidade solicitada supera o saldo disponível.

O código possui serviços para atualizar produtos e consultar históricos por produto, mas **essas operações ainda não têm endpoints HTTP**. As entidades `Categoria` e `Empresa` também não possuem endpoints de cadastro neste repositório.

## Tecnologias

| Camada | Tecnologias |
| --- | --- |
| Backend | Java 17, Spring Boot 3.2.4, Spring Web, Spring Data JPA, Spring Security, Hibernate, Maven e Lombok |
| Autenticação | BCrypt e JWT com JJWT 0.11.5 |
| Banco de dados | PostgreSQL, com conexão configurável para uma instância como a do Supabase |
| Frontend | Vue 3, Vue Router, Axios e Vite 8 |

## Pré-requisitos

- JDK 17.
- Maven disponível no terminal. Este repositório não inclui um Maven Wrapper funcional em `back/`.
- Node.js compatível com Vite 8 e npm. Confira a versão exigida pelo Vite instalado antes de iniciar.
- Banco PostgreSQL acessível, com o esquema esperado pelas entidades JPA e pelo menos uma empresa e um usuário cadastrados.

O projeto usa `spring.jpa.hibernate.ddl-auto=validate`: a API **valida as tabelas existentes, mas não as cria**. Não há migrations ou script de criação do banco no repositório. Sem o esquema e um usuário inicial, a aplicação não estará pronta para login.

## Configuração do backend

O arquivo `back/src/main/resources/application.properties` ativa o perfil `local`. Crie ou ajuste `back/src/main/resources/application-local.properties` no seu computador. Esse arquivo é ignorado pelo Git. Exemplo com valores fictícios:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/controle_fluxo
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.datasource.driver-class-name=org.postgresql.Driver
api.security.token.secret=substitua-por-uma-chave-aleatoria-com-pelo-menos-32-bytes
```

Para PostgreSQL hospedado com TLS, ajuste a URL JDBC conforme o provedor, por exemplo com `?sslmode=require`. A chave JWT deve ter pelo menos 32 bytes para o algoritmo HS256 usado no código. Não publique credenciais nem a chave em arquivos versionados.

Com o banco e o perfil local configurados:

```bash
cd back
mvn spring-boot:run
```

A API usa a porta `8080` por padrão. Para verificar a compilação, use `mvn test` ou `mvn package` dentro de `back/`.

### Dados iniciais

O login consulta um usuário já existente no banco. Como ainda não há endpoint de registro nem script de seed, é preciso cadastrar previamente uma empresa e um usuário por um processo externo. A senha persistida na coluna `usuarios.senha` precisa ser um hash BCrypt. Os perfis modelados são `ADMIN` e `OPERADOR`; o código atual autentica ambos, sem restringir endpoints por perfil.

## Configuração do frontend

Em outro terminal:

```bash
cd controle_fluxo_front/front
npm install
npm run dev
```

Abra o endereço informado pelo Vite, normalmente `http://localhost:5173`. No desenvolvimento, o Vite encaminha `/api/v1` para `http://127.0.0.1:8080`. O cliente HTTP usa `/api/v1` por padrão; para apontar para outra API, configure `VITE_API_URL` em um arquivo `.env` local, incluindo o prefixo `/api/v1`.

```dotenv
VITE_API_URL=/api/v1
```

Para gerar e conferir a versão de produção:

```bash
npm run build
npm run preview
```

Em produção, a hospedagem precisa direcionar rotas como `/produtos` e `/movimentacoes` para `index.html`. Se frontend e API estiverem em origens diferentes, ajuste a lista de origens permitidas em `SecurityConfig.java`; atualmente ela contém apenas `http://localhost:5173` e `http://localhost:3000`.

## Uso da aplicação

1. Acesse `/login` com um usuário já cadastrado.
2. Após o login, a interface salva o token, o ID da empresa, o ID do usuário e o nome no armazenamento local do navegador e abre `/produtos`.
3. Cadastre produtos e acompanhe quantidade, estoque mínimo e valor de venda.
4. Abra `/movimentacoes` para registrar uma `ENTRADA` ou `SAIDA` e consultar o histórico.

As páginas `/`, `/produtos` e `/movimentacoes` exigem um token salvo para navegação. A API valida o JWT em todas as rotas fora de `/api/v1/auth/**`.

## API REST

URL base local: `http://localhost:8080/api/v1`.

| Método | Rota | Função | Cabeçalhos adicionais |
| --- | --- | --- | --- |
| `POST` | `/auth/login` | Autentica um usuário | Nenhum |
| `GET` | `/produtos` | Lista produtos da empresa | `Authorization`, `X-Tenant-ID` |
| `POST` | `/produtos` | Cadastra um produto | `Authorization`, `X-Tenant-ID` |
| `GET` | `/movimentacoes` | Lista o histórico da empresa | `Authorization`, `X-Tenant-ID` |
| `POST` | `/movimentacoes` | Registra entrada ou saída | `Authorization`, `X-Tenant-ID`, `X-Usuario-ID` |

Use `Authorization: Bearer <token>` nos endpoints protegidos. `X-Tenant-ID` é um UUID e `X-Usuario-ID` é um número inteiro. O login devolve ambos. O frontend adiciona esses cabeçalhos automaticamente às requisições autenticadas.

### Exemplo: login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"usuario@exemplo.com","senha":"sua-senha"}'
```

Resposta de sucesso:

```json
{
  "token": "<jwt>",
  "tipo": "Bearer",
  "usuarioId": 1,
  "tenantId": "00000000-0000-0000-0000-000000000001",
  "nome": "Usuário Exemplo",
  "perfil": "ADMIN"
}
```

### Exemplo: cadastrar produto

```bash
curl -X POST http://localhost:8080/api/v1/produtos \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <jwt>' \
  -H 'X-Tenant-ID: <uuid-da-empresa>' \
  -d '{"sku":"SKU-001","nome":"Caderno","descricao":"96 folhas","precoCusto":8.50,"precoVenda":15.00,"quantidadeAtual":10,"estoqueMinimo":3}'
```

O campo `categoriaId` é opcional e, se informado, deve corresponder a uma categoria da mesma empresa. `quantidadeAtual` e `estoqueMinimo` recebem zero no cadastro quando omitidos. O endpoint responde com HTTP `201` e o produto criado.

### Exemplo: registrar movimentação

```bash
curl -X POST http://localhost:8080/api/v1/movimentacoes \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <jwt>' \
  -H 'X-Tenant-ID: <uuid-da-empresa>' \
  -H 'X-Usuario-ID: <id-do-usuario>' \
  -d '{"produtoId":1,"tipo":"SAIDA","quantidade":2,"precoUnitario":15.00,"observacao":"Venda no balcão"}'
```

`tipo` aceita `ENTRADA` ou `SAIDA`. O valor total é calculado como `quantidade × precoUnitario`. Uma saída com saldo insuficiente devolve HTTP `400`. A resposta de sucesso usa HTTP `201` e inclui produto, usuário, quantidade, valor total e data/hora.

### Erros

Os erros tratados pela API incluem `404` para recursos não encontrados e `400` para violações de regra de negócio. As respostas desses casos contêm `timestamp`, `status`, `error`, `message` e `path`. Requisições sem autenticação válida são barradas pelo Spring Security.

## Modelo de dados

| Entidade | Papel |
| --- | --- |
| `Empresa` | Representa o tenant; identificador UUID. |
| `Usuario` | Pertence a uma empresa; possui nome, e-mail único, hash de senha e perfil. |
| `Categoria` | Agrupa produtos de uma empresa. |
| `Produto` | Guarda dados comerciais, saldo atual e estoque mínimo; pertence a uma empresa. |
| `Movimentacao` | Registra produto, usuário, empresa, tipo, quantidade, preço, valor total e data/hora. |

O backend consulta produtos e movimentações usando o ID da empresa. O registro de movimentação também procura o produto e o usuário dentro da empresa indicada. **Atenção:** o código atual lê `X-Tenant-ID` e `X-Usuario-ID` diretamente dos cabeçalhos; o filtro JWT autentica o token, mas não confere se esses valores coincidem com os claims do token. Antes de uso com múltiplos clientes não confiáveis, esse vínculo precisa ser validado no backend.

## Estado atual e limitações

- O banco e os dados iniciais precisam ser preparados fora deste repositório; `ddl-auto=validate` não gera tabelas.
- Não há endpoint de criação de usuários, empresas ou categorias.
- Não há endpoints de edição ou exclusão de produtos, nem de exclusão de movimentações.
- O frontend guarda a sessão em `localStorage` e a proteção de rotas verifica a presença do token; a expiração é tratada pela API.
- O código de serviço não valida explicitamente quantidade positiva ou preço não negativo antes de registrar movimentações. Essas regras devem ser confirmadas no banco e na API antes de uso em produção.

## Solução de problemas

| Sintoma | Verificação |
| --- | --- |
| A API falha ao iniciar com erro de esquema | Confirme a conexão PostgreSQL e crie as tabelas compatíveis com as entidades; o Hibernate está em modo `validate`. |
| Login falha | Confirme que existe um usuário e que a senha armazenada é um hash BCrypt. |
| Frontend não alcança a API | Confira a porta `8080`, o proxy do Vite e `VITE_API_URL`. |
| API responde `401` ou `403` | Verifique o cabeçalho `Authorization`, a validade de 24 horas do JWT e a chave usada para assiná-lo. |
| Produtos ou movimentações não aparecem | Confira `X-Tenant-ID` e se os registros pertencem à empresa informada. |

## Licença

Nenhuma licença foi definida no repositório até o momento.
