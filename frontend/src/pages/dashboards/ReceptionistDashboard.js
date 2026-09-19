import React, { useEffect, useState } from "react";
import { getAllAppointments } from "../../services/appointmentService";

export default function ReceptionistDashboard() {
  const [appointments, setAppointments] = useState([]);

  useEffect(() => {
    getAllAppointments().then(setAppointments).catch(() => {});
  }, []);

  const today = new Date().toISOString().slice(0, 10);
  const todaysAppointments = appointments.filter((a) => a.appointmentDate === today);

  return (
    <div>
      <h4 className="mb-4">Front Desk Dashboard</h4>
      <div className="card p-3">
        <h6>Today's Appointments ({todaysAppointments.length})</h6>
        <table className="table">
          <thead><tr><th>Time</th><th>Patient</th><th>Doctor</th><th>Status</th></tr></thead>
          <tbody>
            {todaysAppointments.map((a) => (
              <tr key={a.id}>
                <td>{a.appointmentTime}</td>
                <td>{a.patientName}</td>
                <td>Dr. {a.doctorName}</td>
                <td>{a.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
