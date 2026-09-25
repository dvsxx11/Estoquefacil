import { ref } from "vue";
import { useRouter } from "vue-router";
import api from "../../services/api"; // O cliente HTTP Axios configurado

export default {
  setup() {
    const router = useRouter();

    // Variáveis reativas
    const email = ref("");
    const senha = ref("");
    const erro = ref("");
    const loading = ref(false);

    const fazerLogin = async () => {
      if (loading.value) return;
      loading.value = true;
      erro.value = "";

      try {
        // 1. Dispara a requisição para o Spring Boot
        const response = await api.post("/auth/login", {
          email: email.value,
          senha: senha.value,
        });

        // 2. Extrai e salva os dados de isolamento e segurança
        const { token, tenantId, usuarioId, nome } = response.data || {};
        if (typeof token !== "string" || !token.trim()) {
          erro.value = "O servidor não retornou um token de acesso válido.";
          return;
        }
        localStorage.setItem("token", token);
        if (tenantId != null) localStorage.setItem("tenantId", tenantId);
        else localStorage.removeItem("tenantId");

        if (usuarioId != null) localStorage.setItem("usuarioId", usuarioId);
        else localStorage.removeItem("usuarioId");
        if (nome) localStorage.setItem("nome", nome);
        else localStorage.removeItem("nome");

        // 3. Validação visual do sucesso
        await router.push("/produtos");
      } catch (error) {
        const status = error.response?.status;
        if (error.code === "ECONNABORTED" || error.code === "ETIMEDOUT") {
          erro.value = "O servidor demorou para responder. Tente novamente.";
        } else if (!error.response) {
          erro.value =
            "Não foi possível acessar o servidor. Confirme se o backend está ativo na porta 8080 e tente novamente.";
        } else if (status === 401) {
          erro.value =
            "Login não autorizado (401). Confira o e-mail e a senha de um usuário cadastrado.";
        } else if (status === 403) {
          erro.value =
            "Acesso negado pelo servidor (403). Verifique as permissões e a configuração de segurança do backend.";
        } else if (status === 404) {
          erro.value =
            "Endpoint de login não encontrado (404). Verifique a URL da API e a rota /auth/login.";
        } else if (status === 400 || status === 422) {
          erro.value =
            error.response.data === "Credenciais inválidas."
              ? "Credenciais inválidas. Confira o e-mail e a senha de um usuário cadastrado."
              : `O servidor rejeitou os dados de login (${status}). Verifique os campos exigidos pela API.`;
        } else if (status >= 500) {
          erro.value = `Erro interno do backend (${status}). Consulte o log do servidor.`;
        } else {
          erro.value = `Não foi possível entrar (HTTP ${status}).`;
        }
      } finally {
        loading.value = false;
      }
    };
    return { email, senha, erro, loading, fazerLogin };
  },
};
