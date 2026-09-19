import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { useAuth } from "../context/AuthContext";
import {
  getMyAppointments, cancelAppointment, getAllAppointments, updateDiagnosis,
} from "../services/appointmentService";

export default function Appointments() {
  const { user } = useAuth();
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [diagnosisDraft, setDiagnosisDraft] = useState({});

  const load = () => {
    setLoading(true);
    const fetcher = user.role === "ADMIN" || user.role === "RECEPTIONIST"
      ? getAllAppointments
      : getMyAppointments;
    fetcher().then(setAppointments).finally(() => setLoading(false));
  };

  useEffect(load, [user.role]);

  const handleCancel = async (id) => {
    try {
      await cancelAppointment(id, "Cancelled by patient");
      toast.success("Appointment cancelled");
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || "Could not cancel appointment");
    }
  };

  const handleSaveDiagnosis = async (id) => {
    try {
      await updateDiagnosis(id, diagnosisDraft[id] || "");
      toast.success("Diagnosis updated");
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || "Could not update diagnosis");
    }
  };

  return (
    <div>
      <h4 className="mb-4">
        {user.role === "DOCTOR" ? "My Schedule" : user.role === "PATIENT" ? "My Appointments" : "All Appointments"}
      </h4>
      {loading ? (
        <p>Loading...</p>
      ) : (
        <div className="card p-3">
          <table className="table align-middle">
            <thead>
              <tr>
                <th>Date</th><th>Time</th>
                {user.role !== "PATIENT" && <th>Patient</th>}
                {user.role !== "DOCTOR" && <th>Doctor</th>}
                <th>Status</th><th>Reason</th>
                {user.role === "DOCTOR" && <th>Diagnosis</th>}
                <th></th>
              </tr>
            </thead>
            <tbody>
              {appointments.map((a) => (
                <tr key={a.id}>
                  <td>{a.appointmentDate}</td>
                  <td>{a.appointmentTime}</td>
                  {user.role !== "PATIENT" && <td>{a.patientName}</td>}
                  {user.role !== "DOCTOR" && <td>Dr. {a.doctorName}</td>}
                  <td><span className="badge bg-secondary">{a.status}</span></td>
                  <td>{a.reasonForVisit}</td>
                  {user.role === "DOCTOR" && (
                    <td style={{ minWidth: 220 }}>
                      <textarea
                        className="form-control form-control-sm mb-1"
                        rows="2"
                        defaultValue={a.diagnosisNotes || ""}
                        onChange={(e) => setDiagnosisDraft({ ...diagnosisDraft, [a.id]: e.target.value })}
                      />
                      <button className="btn btn-sm btn-outline-primary" onClick={() => handleSaveDiagnosis(a.id)}>
                        Save
                      </button>
                    </td>
                  )}
                  <td>
                    {user.role === "PATIENT" && a.status !== "CANCELLED" && a.status !== "COMPLETED" && (
                      <button className="btn btn-sm btn-outline-danger" onClick={() => handleCancel(a.id)}>
                        Cancel
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {appointments.length === 0 && <p className="text-muted mb-0">No appointments found.</p>}
        </div>
      )}
    </div>
  );
}
