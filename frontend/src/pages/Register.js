import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuth } from "../context/AuthContext";

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: "", email: "", password: "", phone: "",
    dateOfBirth: "", gender: "", bloodGroup: "", address: "",
  });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await register(form);
      toast.success("Registration successful");
      navigate("/dashboard");
    } catch (err) {
      toast.error(err.response?.data?.message || "Registration failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="d-flex justify-content-center align-items-center py-5 bg-light" style={{ minHeight: "100vh" }}>
      <div className="card shadow-sm p-4" style={{ width: 480 }}>
        <h4 className="mb-3 text-center">Patient Registration</h4>
        <form onSubmit={handleSubmit}>
          <div className="row">
            <div className="col-md-6 mb-3">
              <label className="form-label">Full Name</label>
              <input name="fullName" className="form-control" required value={form.fullName} onChange={handleChange} />
            </div>
            <div className="col-md-6 mb-3">
              <label className="form-label">Phone</label>
              <input name="phone" className="form-control" required value={form.phone} onChange={handleChange} />
            </div>
          </div>
          <div className="mb-3">
            <label className="form-label">Email</label>
            <input type="email" name="email" className="form-control" required value={form.email} onChange={handleChange} />
          </div>
          <div className="mb-3">
            <label className="form-label">Password</label>
            <input type="password" name="password" className="form-control" required minLength={8} value={form.password} onChange={handleChange} />
          </div>
          <div className="row">
            <div className="col-md-4 mb-3">
              <label className="form-label">Date of Birth</label>
              <input type="date" name="dateOfBirth" className="form-control" value={form.dateOfBirth} onChange={handleChange} />
            </div>
            <div className="col-md-4 mb-3">
              <label className="form-label">Gender</label>
              <select name="gender" className="form-select" value={form.gender} onChange={handleChange}>
                <option value="">Select</option>
                <option>Male</option>
                <option>Female</option>
                <option>Other</option>
              </select>
            </div>
            <div className="col-md-4 mb-3">
              <label className="form-label">Blood Group</label>
              <input name="bloodGroup" className="form-control" value={form.bloodGroup} onChange={handleChange} />
            </div>
          </div>
          <div className="mb-3">
            <label className="form-label">Address</label>
            <input name="address" className="form-control" value={form.address} onChange={handleChange} />
          </div>
          <button type="submit" className="btn btn-primary w-100" disabled={loading}>
            {loading ? "Creating account..." : "Register"}
          </button>
        </form>
        <div className="text-center mt-3">
          <Link to="/login" className="small">Already have an account? Sign in</Link>
        </div>
      </div>
    </div>
  );
}
