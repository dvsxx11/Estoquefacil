import { computed, onMounted, reactive, ref } from "vue";
import Navbar from "../../components/Navbar/Navbar.vue";
import api from "../../services/api";
const novoForm = () => ({
  nome: "",
  sku: "",
  descricao: "",
  precoCusto: 0,
  precoVenda: 0,
  quantidadeAtual: 0,
  estoqueMinimo: 0,
});
export default {
  components: { Navbar },
  setup() {
    const produtos = ref([]),
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
    const emEstoque = computed(
      () => produtos.value.filter((p) => Number(p.quantidadeAtual) > 0).length,
    );
    const estoqueBaixo = computed(
      () =>
        produtos.value.filter(
          (p) =>
            Number(p.quantidadeAtual) > 0 &&
            Number(p.quantidadeAtual) <= Number(p.estoqueMinimo),
        ).length,
    );
    const unidades = computed(() =>
      produtos.value.reduce((n, p) => n + Number(p.quantidadeAtual || 0), 0),
    );
    const filtrados = computed(() =>
      produtos.value.filter((p) => {
        const termo = busca.value.toLocaleLowerCase("pt-BR");
        const corresponde = `${p.nome || ""} ${p.sku || ""}`
          .toLocaleLowerCase("pt-BR")
          .includes(termo);
        return (
          corresponde &&
          (filtro.value === "todos" ||
            (filtro.value === "baixo" &&
              Number(p.quantidadeAtual) > 0 &&
              Number(p.quantidadeAtual) <= Number(p.estoqueMinimo)) ||
            (filtro.value === "sem" && Number(p.quantidadeAtual) <= 0))
        );
      }),
    );
    const moeda = (v) =>
      new Intl.NumberFormat("pt-BR", {
        style: "currency",
        currency: "BRL",
      }).format(Number(v || 0));
    const statusClasse = (p) =>
      Number(p.quantidadeAtual) <= 0
        ? "empty"
        : Number(p.quantidadeAtual) <= Number(p.estoqueMinimo)
          ? "low"
          : "ok";
    const statusTexto = (p) =>
      ({ empty: "Sem estoque", low: "Estoque baixo", ok: "Disponível" })[
        statusClasse(p)
      ];
    async function carregar() {
      carregando.value = true;
      erro.value = "";
      try {
        const { data } = await api.get("/produtos");
        produtos.value = Array.isArray(data) ? data : [];
      } catch (e) {
        erro.value =
          e.response?.data?.message ||
          "Não foi possível carregar os produtos. Confira a conexão com a API.";
      } finally {
        carregando.value = false;
      }
    }
    function abrirFormulario() {
      Object.assign(form, novoForm());
      erroModal.value = "";
      modalAberto.value = true;
    }
    async function salvar() {
      if (salvando.value) return;
      if (!/^[0-9]+$/.test(form.sku)) {
        erroModal.value = "O código do produto deve conter apenas números.";
        return;
      }
      salvando.value = true;
      erroModal.value = "";
      try {
        await api.post("/produtos", { ...form });
        modalAberto.value = false;
        sucesso.value = "Produto cadastrado com sucesso.";
        await carregar();
      } catch (e) {
        erroModal.value =
          e.response?.data?.message || "Não foi possível cadastrar o produto.";
      } finally {
        salvando.value = false;
      }
    }
    onMounted(carregar);
    return {
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
      emEstoque,
      estoqueBaixo,
      unidades,
      filtrados,
      moeda,
      statusClasse,
      statusTexto,
      carregar,
      abrirFormulario,
      salvar,
    };
  },
};
