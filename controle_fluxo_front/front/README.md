# Controle de fluxo

Frontend Vue 3 com Vite e Vue Router.

## Executar

```sh
npm install
npm run dev
```

Abra o endereço informado pelo Vite. Sem token salvo, a aplicação redireciona para `/login`.

## Backend

Durante o desenvolvimento, o Vite encaminha `/api/v1` para o backend em `http://127.0.0.1:8080`, evitando bloqueios de CORS. Inicie o backend na porta 8080. Em produção, configure `VITE_API_URL` com a URL pública da API (incluindo `/api/v1`) ou configure um proxy equivalente no servidor. Reinicie o Vite após alterar variáveis de ambiente.

O login envia `POST /api/v1/auth/login` com `{ email, senha }` e espera `{ token, tenantId }`. O token é obrigatório; `tenantId` é opcional. As demais requisições pelo cliente HTTP enviam `Authorization: Bearer <token>` quando há sessão salva. O backend precisa permitir a origem do frontend nas configurações de CORS.

As páginas de Produtos e Movimentações listam dados da API, oferecem busca e filtros e permitem cadastrar produtos e registrar entradas e saídas. O login armazena também `usuarioId` e `nome`; o cliente HTTP envia `X-Tenant-ID` e `X-Usuario-ID` nas requisições autenticadas conforme exigido pelo backend.

## Compilar

```sh
npm run build
npm run preview
```

Na hospedagem, configure o servidor para servir `index.html` ao acessar rotas como `/login` ou `/produtos` diretamente, pois o roteador usa o histórico do navegador.
