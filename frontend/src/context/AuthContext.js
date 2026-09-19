import React, { createContext, useContext, useEffect, useState } from "react";
import { login as loginRequest, registerPatient as registerRequest } from "../services/authService";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem("hms_user");
    return stored ? JSON.parse(stored) : null;
  });

  useEffect(() => {
    if (user) {
      localStorage.setItem("hms_user", JSON.stringify(user));
    } else {
      localStorage.removeItem("hms_user");
    }
  }, [user]);

  const login = async (email, password) => {
    const data = await loginRequest(email, password);
    localStorage.setItem("hms_token", data.accessToken);
    const authUser = { id: data.userId, fullName: data.fullName, email: data.email, role: data.role };
    setUser(authUser);
    return authUser;
  };

  const register = async (payload) => {
    const data = await registerRequest(payload);
    localStorage.setItem("hms_token", data.accessToken);
    const authUser = { id: data.userId, fullName: data.fullName, email: data.email, role: data.role };
    setUser(authUser);
    return authUser;
  };

  const logout = () => {
    localStorage.removeItem("hms_token");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within an AuthProvider");
  return ctx;
}
