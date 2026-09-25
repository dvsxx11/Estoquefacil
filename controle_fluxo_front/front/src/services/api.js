import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "/api/v1",
  timeout: 15000,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token && config.url !== "/auth/login") {
    config.headers.Authorization = `Bearer ${token}`;
    const tenantId = localStorage.getItem("tenantId");
    const usuarioId = localStorage.getItem("usuarioId");
    if (tenantId) config.headers["X-Tenant-ID"] = tenantId;
    if (usuarioId) config.headers["X-Usuario-ID"] = usuarioId;
  }
  return config;
});

export default api;
