import api from "./api";

export const bookAppointment = (payload) =>
  api.post("/appointments", payload).then((res) => res.data.data);

export const cancelAppointment = (id, reason) =>
  api.delete(`/appointments/${id}`, { params: { reason } }).then((res) => res.data);

export const getMyAppointments = () =>
  api.get("/appointments/my").then((res) => res.data.data);

export const getDoctorSchedule = (date) =>
  api.get("/appointments/doctor/schedule", {
    params: { date },
  }).then((res) => res.data.data);

export const updateDiagnosis = (id, diagnosisNotes) =>
  api.put(`/appointments/${id}/diagnosis`, { diagnosisNotes })
    .then((res) => res.data.data);

export const getAllAppointments = () =>
  api.get("/appointments").then((res) => res.data.data);