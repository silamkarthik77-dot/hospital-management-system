import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";
import api from "../services/api";

export default function Profile() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get("/patients/me").then((res) => setProfile(res.data.data)).finally(() => setLoading(false));
  }, []);

  const handleChange = (e) => setProfile({ ...profile, [e.target.name]: e.target.value });

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      const res = await api.put("/patients/me", profile);
      setProfile(res.data.data);
      toast.success("Profile updated");
    } catch (err) {
      toast.error(err.response?.data?.message || "Could not update profile");
    }
  };

  if (loading) return <p>Loading profile...</p>;
  if (!profile) return <p>No profile found.</p>;

  return (
    <div>
      <h4 className="mb-4">My Profile</h4>
      <div className="card p-4" style={{ maxWidth: 560 }}>
        <form onSubmit={handleSave}>
          <div className="mb-3">
            <label className="form-label">Blood Group</label>
            <input name="bloodGroup" className="form-control" value={profile.bloodGroup || ""} onChange={handleChange} />
          </div>
          <div className="mb-3">
            <label className="form-label">Address</label>
            <input name="address" className="form-control" value={profile.address || ""} onChange={handleChange} />
          </div>
          <div className="mb-3">
            <label className="form-label">Emergency Contact Name</label>
            <input name="emergencyContactName" className="form-control" value={profile.emergencyContactName || ""} onChange={handleChange} />
          </div>
          <div className="mb-3">
            <label className="form-label">Emergency Contact Phone</label>
            <input name="emergencyContactPhone" className="form-control" value={profile.emergencyContactPhone || ""} onChange={handleChange} />
          </div>
          <div className="mb-3">
            <label className="form-label">Medical History</label>
            <textarea name="medicalHistory" className="form-control" rows="3" value={profile.medicalHistory || ""} onChange={handleChange} />
          </div>
          <button type="submit" className="btn btn-primary">Save Changes</button>
        </form>
      </div>
    </div>
  );
}
