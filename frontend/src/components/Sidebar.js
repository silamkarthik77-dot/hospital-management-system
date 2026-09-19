import React from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const LINKS_BY_ROLE = {
  ADMIN: [
    { to: "/dashboard", label: "Dashboard" },
    { to: "/doctors", label: "Doctors" },
    { to: "/patients", label: "Patients" },
    { to: "/appointments", label: "Appointments" },
    { to: "/billing", label: "Billing" },
  ],
  DOCTOR: [
    { to: "/dashboard", label: "Dashboard" },
    { to: "/appointments", label: "My Schedule" },
    { to: "/profile", label: "Profile" },
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
    { to: "/patients", label: "Patients" },
    { to: "/appointments", label: "Appointments" },
    { to: "/billing", label: "Billing" },
  ],
};

export default function Sidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const links = LINKS_BY_ROLE[user?.role] || [];

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="sidebar p-3 d-flex flex-column" style={{ width: 240 }}>
      <h5 className="text-white mb-4">🏥 HMS</h5>
      <div className="flex-grow-1">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) => (isActive ? "active" : "")}
          >
            {link.label}
          </NavLink>
        ))}
      </div>
      <div className="text-secondary small mb-2 px-2">
        {user?.fullName} · {user?.role}
      </div>
      <button className="btn btn-outline-light btn-sm" onClick={handleLogout}>
        Logout
      </button>
    </div>
  );
}
