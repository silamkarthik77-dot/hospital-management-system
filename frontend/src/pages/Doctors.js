import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { getAllDoctors } from "../services/doctorService";
import { bookAppointment } from "../services/appointmentService";
import { useAuth } from "../context/AuthContext";

export default function Doctors() {
  const { user } = useAuth();
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [bookingDoctor, setBookingDoctor] = useState(null);
  const [form, setForm] = useState({ appointmentDate: "", appointmentTime: "", reasonForVisit: "" });

  useEffect(() => {
    getAllDoctors().then(setDoctors).finally(() => setLoading(false));
  }, []);

  const openBookingModal = (doctor) => {
    setBookingDoctor(doctor);
    setForm({ appointmentDate: "", appointmentTime: "", reasonForVisit: "" });
  };

  const submitBooking = async (e) => {
    e.preventDefault();
    try {
      await bookAppointment({ doctorId: bookingDoctor.id, ...form });
      toast.success("Appointment booked successfully");
      setBookingDoctor(null);
    } catch (err) {
      toast.error(err.response?.data?.message || "Could not book appointment");
    }
  };

  return (
    <div>
      <h4 className="mb-4">Find a Doctor</h4>
      {loading ? (
        <p>Loading doctors...</p>
      ) : (
        <div className="row g-3">
          {doctors.map((d) => (
            <div className="col-md-4" key={d.id}>
              <div className="card p-3 h-100">
                <h6>Dr. {d.fullName}</h6>
                <p className="text-muted small mb-1">{d.departmentName} · {d.specialization}</p>
                <p className="small mb-1">Experience: {d.experienceYears} yrs</p>
                <p className="small mb-2">Fee: ₹{d.consultationFee}</p>
                {user?.role === "PATIENT" && (
                  <button className="btn btn-sm btn-primary" onClick={() => openBookingModal(d)}>
                    Book Appointment
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {bookingDoctor && (
        <div className="modal d-block" style={{ background: "rgba(0,0,0,0.5)" }}>
          <div className="modal-dialog">
            <div className="modal-content p-3">
              <h6>Book with Dr. {bookingDoctor.fullName}</h6>
              <form onSubmit={submitBooking}>
                <div className="mb-2">
                  <label className="form-label">Date</label>
                  <input type="date" className="form-control" required
                    value={form.appointmentDate}
                    onChange={(e) => setForm({ ...form, appointmentDate: e.target.value })} />
                </div>
                <div className="mb-2">
                  <label className="form-label">Time</label>
                  <input type="time" className="form-control" required
                    value={form.appointmentTime}
                    onChange={(e) => setForm({ ...form, appointmentTime: e.target.value })} />
                </div>
                <div className="mb-3">
                  <label className="form-label">Reason for visit</label>
                  <textarea className="form-control" rows="2"
                    value={form.reasonForVisit}
                    onChange={(e) => setForm({ ...form, reasonForVisit: e.target.value })} />
                </div>
                <div className="d-flex justify-content-end gap-2">
                  <button type="button" className="btn btn-outline-secondary" onClick={() => setBookingDoctor(null)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary">Confirm Booking</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
