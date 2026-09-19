import React, { useEffect, useState } from "react";
import { Line } from "react-chartjs-2";
import {
  Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend,
} from "chart.js";
import { getDashboardStats } from "../../services/adminService";

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend);

const monthlyTrendPlaceholder = {
  labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun"],
  datasets: [
    {
      label: "Appointments",
      data: [40, 55, 48, 62, 70, 65],
      borderColor: "#0d6efd",
      backgroundColor: "rgba(13,110,253,0.15)",
      tension: 0.35,
      fill: true,
    },
  ],
};

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getDashboardStats()
      .then(setStats)
      .finally(() => setLoading(false));
  }, []);

  const cards = stats
    ? [
        { label: "Total Patients", value: stats.totalPatients },
        { label: "Total Doctors", value: stats.totalDoctors },
        { label: "Today's Appointments", value: stats.todaysAppointments },
        { label: "Monthly Revenue", value: `₹${stats.monthlyRevenue ?? 0}` },
        { label: "Pending Bills", value: stats.pendingBills },
      ]
    : [];

  return (
    <div>
      <h4 className="mb-4">Admin Dashboard</h4>
      {loading ? (
        <p>Loading stats...</p>
      ) : (
        <div className="row g-3 mb-4">
          {cards.map((c) => (
            <div className="col-md-4 col-lg-2" key={c.label}>
              <div className="card stat-card p-3 text-center h-100">
                <div className="text-muted small">{c.label}</div>
                <div className="fs-4 fw-bold">{c.value}</div>
              </div>
            </div>
          ))}
        </div>
      )}

      <div className="card p-3">
        <h6>Appointment Trends (last 6 months)</h6>
        {/* NOTE: monthly trend / revenue-by-month endpoints aren't wired up
            in this starter yet - wire a real /api/admin/analytics endpoint
            and replace this placeholder series with it. */}
        <Line data={monthlyTrendPlaceholder} />
      </div>
    </div>
  );
}
