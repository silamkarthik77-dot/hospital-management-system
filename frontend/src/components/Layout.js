import React from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const NAV_ITEMS = {
  ADMIN: [
    { to: "/dashboard", label: "Dashboard" },
    { to: "/doctors", label: "Doctors" },
    { to: "/appointments", label: "Appointments" },
    { to: "/billing", label: "Billing" },
  ],
  DOCTOR: [
    { to: "/dashboard", label: "Dashboard" },
    { to: "/appointments", label: "My Schedule" },
  ],
  PATIENT: [
    { to: "/dashboard", label: "Dashboard" },
    { to: "/doctors", label: "Find a Doctor" },
    { to: "/appointments", label: "My Appointments" },
    { to: "/billing", label: "My Bills" },
    { to: "/profile", label: "Profile" },
  ],
  RECEPTIONIST: [
    { to: "/dashboard", label: "Dashboard" },
    { to: "/appointments", label: "Appointments" },
    { to: "/billing", label: "Billing" },
  ],
};

export default function Layout({ children }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const items = user ? NAV_ITEMS[user.role] || [] : [];

  return (
    <div className="d-flex">
      <div className="sidebar" style={{ width: 220 }}>
        <div className="p-3 fw-bold fs-5 border-bottom border-secondary">HMS</div>
        <nav className="mt-3">
          {items.map((item) => (
            <Link
              key={item.to}
              to={item.to}
              className={location.pathname === item.to ? "active" : ""}
            >
              {item.label}
            </Link>
          ))}
        </nav>
      </div>
      <div className="flex-grow-1">
        <header className="d-flex justify-content-between align-items-center bg-white border-bottom px-4 py-2">
          <span className="text-muted">Hospital Management System</span>
          <div className="d-flex align-items-center gap-3">
            <span>{user?.fullName} <small className="text-muted">({user?.role})</small></span>
            <button className="btn btn-sm btn-outline-secondary" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </header>
        <main className="p-4">{children}</main>
      </div>
    </div>
  );
}
