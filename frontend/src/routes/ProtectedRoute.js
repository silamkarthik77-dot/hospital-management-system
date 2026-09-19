import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

/**
 * Guards a route behind authentication and, optionally, a set of allowed
 * roles. Unauthenticated users are sent to /login; authenticated users
 * whose role isn't in `roles` are sent to their own dashboard.
 */
export default function ProtectedRoute({ children, roles }) {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (roles && !roles.includes(user.role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}
