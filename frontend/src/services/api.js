import axios from "axios";

// Dev: use same-origin "/api" so Vite proxies to the backend (see vite.config.js).
// Prod: set VITE_API_BASE_URL to your API root, e.g. https://api.example.com/api
const baseURL =
  import.meta.env.VITE_API_BASE_URL?.trim() || "/api";

const api = axios.create({
  baseURL,
  timeout: 20000,
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
