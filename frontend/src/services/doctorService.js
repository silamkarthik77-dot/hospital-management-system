import api from "./api";

export const getAllDoctors = () => api.get("/doctors").then((res) => res.data.data);

export const getDoctorById = (id) => api.get(`/doctors/${id}`).then((res) => res.data.data);
