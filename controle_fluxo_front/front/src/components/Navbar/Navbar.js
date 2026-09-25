import { useRouter } from "vue-router";
export default {
  setup() {
    const router = useRouter();
    function sair() {
      localStorage.removeItem("token");
      localStorage.removeItem("tenantId");
      localStorage.removeItem("usuarioId");
      localStorage.removeItem("nome");
      router.replace("/login");
    }
    return { sair };
  },
};
