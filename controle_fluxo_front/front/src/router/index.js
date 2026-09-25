import { createRouter, createWebHistory } from "vue-router";
// Importa o agregador Login.vue da estrutura de pastas modular
import Login from "../views/Login/Login.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/:pathMatch(.*)*", redirect: "/" },
    {
      path: "/login",
      name: "login",
      component: Login,
    },
    {
      path: "/",
      name: "home",
      // Carregamento assíncrono para telas protegidas
      component: () => import("../views/Produtos/Produtos.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/produtos",
      name: "produtos",
      component: () => import("../views/Produtos/Produtos.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/movimentacoes",
      name: "movimentacoes",
      component: () => import("../views/Movimentacoes/Movimentacoes.vue"),
      meta: { requiresAuth: true },
    },
  ],
});

// Guardião de navegação: verifica a existência do Token JWT antes de acessar rotas internas
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token");

  if (to.meta.requiresAuth && !token) {
    // Redireciona para o login se não houver token JWT salvo.
    next("/login");
  } else {
    next();
  }
});

export default router;
