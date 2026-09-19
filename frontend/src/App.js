import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import { AuthProvider, useAuth } from "./context/AuthContext";
import ProtectedRoute from "./routes/ProtectedRoute";
import Layout from "./components/Layout";

import Login from "./pages/Login";
import Register from "./pages/Register";
import ForgotPassword from "./pages/ForgotPassword";
import Dashboard from "./pages/Dashboard";
import Doctors from "./pages/Doctors";
import Appointments from "./pages/Appointments";
import Billing from "./pages/Billing";
import Profile from "./pages/Profile";
import NotFound from "./pages/NotFound";

function AuthedLayout({ children }) {
  return <Layout>{children}</Layout>;
}

function RootRedirect() {
  const { user } = useAuth();
  return <Navigate to={user ? "/dashboard" : "/login"} replace />;
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <ToastContainer position="top-right" autoClose={3000} />
        <Routes>
          <Route path="/" element={<RootRedirect />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />

          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <AuthedLayout><Dashboard /></AuthedLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/doctors"
            element={
              <ProtectedRoute roles={["ADMIN", "PATIENT"]}>
                <AuthedLayout><Doctors /></AuthedLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/appointments"
            element={
              <ProtectedRoute roles={["ADMIN", "DOCTOR", "PATIENT", "RECEPTIONIST"]}>
                <AuthedLayout><Appointments /></AuthedLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/billing"
            element={
              <ProtectedRoute roles={["ADMIN", "PATIENT", "RECEPTIONIST"]}>
                <AuthedLayout><Billing /></AuthedLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ProtectedRoute roles={["PATIENT"]}>
                <AuthedLayout><Profile /></AuthedLayout>
              </ProtectedRoute>
            }
          />

          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
