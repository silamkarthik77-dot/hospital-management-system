import api from "./api";

export const getMyInvoices = () => api.get("/billing/my").then((res) => res.data.data);

export const getAllInvoices = () => api.get("/billing/invoices").then((res) => res.data.data);

export const createInvoice = (payload) =>
  api.post("/billing/invoices", payload).then((res) => res.data.data);

export const makePayment = (payload) =>
  api.post("/billing/pay", payload).then((res) => res.data.data);

export const downloadInvoicePdf = (id) =>
  api.get(`/billing/invoices/${id}/pdf`, { responseType: "blob" }).then((res) => res.data);
