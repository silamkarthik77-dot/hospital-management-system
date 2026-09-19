import React, { useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import { forgotPassword } from "../services/authService";

export default function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await forgotPassword(email);
      toast.success("If that email exists, reset instructions have been sent");
    } catch (err) {
      toast.error(err.response?.data?.message || "Something went wrong");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="d-flex justify-content-center align-items-center vh-100 bg-light">
      <div className="card shadow-sm p-4" style={{ width: 380 }}>
        <h5 className="mb-3 text-center">Forgot Password</h5>
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">Email</label>
            <input type="email" className="form-control" required value={email} onChange={(e) => setEmail(e.target.value)} />
          </div>
          <button type="submit" className="btn btn-primary w-100" disabled={loading}>
            {loading ? "Sending..." : "Send Reset Link"}
          </button>
        </form>
        <div className="text-center mt-3">
          <Link to="/login" className="small">Back to login</Link>
        </div>
      </div>
    </div>
  );
}
