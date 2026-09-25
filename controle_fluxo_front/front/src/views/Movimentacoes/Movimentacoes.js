import { computed, onMounted, reactive, ref } from "vue";
import Navbar from "../../components/Navbar/Navbar.vue";
import api from "../../services/api";
const novoForm = () => ({
  produtoId: "",
  tipo: "ENTRADA",
  quantidade: 1,
  precoUnitario: 0,
  observacao: "",
});
export default {
  components: { Navbar },
  setup() {
    const movimentos = ref([]),
      produtos = ref([]),
      busca = ref(""),
      filtro = ref("todos"),
      carregando = ref(false),
      salvando = ref(false),
      modalAberto = ref(false),
      erro = ref(""),
      erroModal = ref(""),
      sucesso = ref("");
    const form = reactive(novoForm());
    const iniciais = computed(() =>
      (localStorage.getItem("nome") || "U")
        .trim()
        .split(/\s+/)
        .slice(0, 2)
        .map((p) => p[0]?.toUpperCase())
        .join(""),
    );
    const entradas = computed(
        () => movimentos.value.filter((m) => m.tipo === "ENTRADA").length,
      ),
      saidas = computed(
        () => movimentos.value.filter((m) => m.tipo === "SAIDA").length,
      ),
      unidades = computed(() =>
        movimentos.value.reduce((n, m) => n + Number(m.quantidade || 0), 0),
      );
    const filtrados = computed(() =>
      movimentos.value.filter(
        (m) =>
          (m.nomeProduto || "")
            .toLocaleLowerCase("pt-BR")
            .includes(busca.value.toLocaleLowerCase("pt-BR")) &&
          (filtro.value === "todos" || m.tipo === filtro.value),
      ),
    );
    const moeda = (v) =>
      new Intl.NumberFormat("pt-BR", {
        style: "currency",
        currency: "BRL",
      }).format(Number(v || 0));
    const dataHora = (v) =>
      v
        ? new Intl.DateTimeFormat("pt-BR", {
            dateStyle: "short",
            timeStyle: "short",
          }).format(new Date(v))
        : "—";
    async function carregar() {
      carregando.value = true;
      erro.value = "";
      try {
        const { data } = await api.get("/movimentacoes");
        movimentos.value = Array.isArray(data) ? data : [];
      } catch (e) {
        erro.value =
          e.response?.data?.message ||
          "Não foi possível carregar as movimentações. Confira a conexão com a API.";
      } finally {
        carregando.value = false;
      }
    }
    async function abrirFormulario() {
      Object.assign(form, novoForm());
      erroModal.value = "";
      modalAberto.value = true;
      try {
        const { data } = await api.get("/produtos");
        produtos.value = Array.isArray(data) ? data : [];
      } catch (e) {
        erroModal.value =
          e.response?.data?.message || "Não foi possível carregar os produtos.";
      }
    }
    async function salvar() {
      if (salvando.value) return;
      salvando.value = true;
      erroModal.value = "";
      try {
        await api.post("/movimentacoes", { ...form });
        modalAberto.value = false;
        sucesso.value = "Movimentação registrada com sucesso.";
        await carregar();
      } catch (e) {
        erroModal.value =
          e.response?.data?.message ||
          "Não foi possível registrar a movimentação.";
      } finally {
        salvando.value = false;
      }
    }
    onMounted(carregar);
    return {
      movimentos,
      produtos,
      busca,
      filtro,
      carregando,
      salvando,
      modalAberto,
      erro,
      erroModal,
      sucesso,
      form,
      iniciais,
      entradas,
      saidas,
      unidades,
      filtrados,
      moeda,
      dataHora,
      carregar,
      abrirFormulario,
      salvar,
    };
  },
};
