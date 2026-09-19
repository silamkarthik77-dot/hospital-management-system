import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuth } from "../context/AuthContext";

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await login(form.email, form.password);
      toast.success("Login successful");
      navigate("/dashboard");
    } catch (err) {
      toast.error(err.response?.data?.message || "Invalid email or password");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="d-flex justify-content-center align-items-center vh-100 bg-light">
      <div className="card shadow-sm p-4" style={{ width: 380 }}>
        <h4 className="mb-3 text-center">Sign in to HMS</h4>
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">Email</label>
            <input
              type="email" name="email" className="form-control" required
              value={form.email} onChange={handleChange}
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Password</label>
            <input
              type="password" name="password" className="form-control" required
              value={form.password} onChange={handleChange}
            />
          </div>
          <button type="submit" className="btn btn-primary w-100" disabled={loading}>
            {loading ? "Signing in..." : "Sign In"}
          </button>
        </form>
        <div className="d-flex justify-content-between mt-3">
          <Link to="/forgot-password" className="small">Forgot password?</Link>
          <Link to="/register" className="small">Register as patient</Link>
        </div>
      </div>
    </div>
  );
}
