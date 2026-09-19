import React from "react";
import { useAuth } from "../context/AuthContext";
import AdminDashboard from "./dashboards/AdminDashboard";
import DoctorDashboard from "./dashboards/DoctorDashboard";
import PatientDashboard from "./dashboards/PatientDashboard";
import ReceptionistDashboard from "./dashboards/ReceptionistDashboard";

export default function Dashboard() {
  const { user } = useAuth();

  switch (user?.role) {
    case "ADMIN":
      return <AdminDashboard />;
    case "DOCTOR":
      return <DoctorDashboard />;
    case "RECEPTIONIST":
      return <ReceptionistDashboard />;
    case "PATIENT":
    default:
      return <PatientDashboard />;
  }
}
