import api from "./api";

export const getDashboardStats = () => api.get("/admin/dashboard").then((res) => res.data.data);

export const getAllPatients = () => api.get("/admin/patients").then((res) => res.data.data);
