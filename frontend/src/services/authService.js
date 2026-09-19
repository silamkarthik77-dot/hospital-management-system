import api from "./api";

export const login = (email, password) =>
  api.post("/auth/login", { email, password }).then((res) => res.data.data);

export const registerPatient = (payload) =>
  api.post("/auth/register", payload).then((res) => res.data.data);

export const forgotPassword = (email) =>
  api.post("/auth/forgot-password", { email }).then((res) => res.data);

export const resetPassword = (token, newPassword) =>
  api.post("/auth/reset-password", { token, newPassword }).then((res) => res.data);
