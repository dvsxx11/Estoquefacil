<template>
  <aside class="sidebar">
    <RouterLink to="/produtos" class="brand"
      ><span class="brand-mark">✦</span
      ><span
        >EstoqueFacil<span class="brand-dot">.</span
        ><small>GESTÃO INTELIGENTE</small></span
      ></RouterLink
    >
    <div class="nav-section">MENU PRINCIPAL</div>
    <nav aria-label="Navegação principal">
      <RouterLink to="/produtos" class="nav-link"
        ><span class="nav-icon">▦</span> Produtos</RouterLink
      >
      <RouterLink to="/movimentacoes" class="nav-link"
        ><span class="nav-icon">⇄</span> Movimentações</RouterLink
      >
    </nav>
    <div class="sidebar-bottom">
      <div class="sidebar-help">
        <span class="help-icon">✧</span><strong>Seu estoque em dia</strong
        ><small>Acompanhe produtos e movimentações em um só lugar.</small>
      </div>
      <button class="logout" type="button" @click="sair">
        <span>↪</span> Sair da conta
      </button>
    </div>
  </aside>
</template>
<script src="./Navbar.js"></script>
<style scoped src="./Navbar.css"></style>
