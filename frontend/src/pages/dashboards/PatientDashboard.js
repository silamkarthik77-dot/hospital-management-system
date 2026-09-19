import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getMyAppointments } from "../../services/appointmentService";
import { getMyInvoices } from "../../services/billingService";

export default function PatientDashboard() {
  const [appointments, setAppointments] = useState([]);
  const [invoices, setInvoices] = useState([]);

  useEffect(() => {
    getMyAppointments().then(setAppointments).catch(() => {});
    getMyInvoices().then(setInvoices).catch(() => {});
  }, []);

  const upcoming = appointments.filter((a) => a.status === "CONFIRMED" || a.status === "PENDING");
  const pendingBills = invoices.filter((i) => i.status !== "PAID");

  return (
    <div>
      <h4 className="mb-4">My Dashboard</h4>
      <div className="row g-3 mb-4">
        <div className="col-md-3">
          <div className="card stat-card p-3 text-center">
            <div className="text-muted small">Upcoming Appointments</div>
            <div className="fs-4 fw-bold">{upcoming.length}</div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card stat-card p-3 text-center">
            <div className="text-muted small">Pending Bills</div>
            <div className="fs-4 fw-bold">{pendingBills.length}</div>
          </div>
        </div>
      </div>

      <div className="d-flex gap-2 mb-4">
        <Link to="/doctors" className="btn btn-primary btn-sm">Book Appointment</Link>
        <Link to="/billing" className="btn btn-outline-secondary btn-sm">View Bills</Link>
      </div>

      <div className="card p-3">
        <h6>Upcoming Appointments</h6>
        {upcoming.length === 0 ? (
          <p className="text-muted">No upcoming appointments.</p>
        ) : (
          <ul className="list-group list-group-flush">
            {upcoming.map((a) => (
              <li key={a.id} className="list-group-item d-flex justify-content-between">
                <span>Dr. {a.doctorName} — {a.appointmentDate} at {a.appointmentTime}</span>
                <span className="badge bg-info text-dark">{a.status}</span>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
