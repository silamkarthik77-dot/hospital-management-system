import React, { useEffect, useState } from "react";
import { getMyAppointments } from "../../services/appointmentService";

export default function DoctorDashboard() {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getMyAppointments()
      .then(setAppointments)
      .finally(() => setLoading(false));
  }, []);

  const today = new Date().toISOString().slice(0, 10);
  const todaysAppointments = appointments.filter((a) => a.appointmentDate === today);

  return (
    <div>
      <h4 className="mb-4">Doctor Dashboard</h4>
      <div className="row g-3 mb-4">
        <div className="col-md-3">
          <div className="card stat-card p-3 text-center">
            <div className="text-muted small">Today's Appointments</div>
            <div className="fs-4 fw-bold">{todaysAppointments.length}</div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card stat-card p-3 text-center">
            <div className="text-muted small">Total Appointments</div>
            <div className="fs-4 fw-bold">{appointments.length}</div>
          </div>
        </div>
      </div>

      <div className="card p-3">
        <h6>Today's Schedule</h6>
        {loading ? (
          <p>Loading...</p>
        ) : todaysAppointments.length === 0 ? (
          <p className="text-muted">No appointments scheduled for today.</p>
        ) : (
          <table className="table">
            <thead><tr><th>Time</th><th>Patient</th><th>Status</th><th>Reason</th></tr></thead>
            <tbody>
              {todaysAppointments.map((a) => (
                <tr key={a.id}>
                  <td>{a.appointmentTime}</td>
                  <td>{a.patientName}</td>
                  <td>{a.status}</td>
                  <td>{a.reasonForVisit}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
