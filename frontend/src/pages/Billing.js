import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { useAuth } from "../context/AuthContext";
import { getMyInvoices, getAllInvoices, makePayment, downloadInvoicePdf } from "../services/billingService";

export default function Billing() {
  const { user } = useAuth();
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    setLoading(true);
    const fetcher = user.role === "PATIENT" ? getMyInvoices : getAllInvoices;
    fetcher().then(setInvoices).finally(() => setLoading(false));
  };

  useEffect(load, [user.role]);

  const handlePay = async (invoice) => {
    try {
      await makePayment({
        invoiceId: invoice.id,
        amount: invoice.totalAmount,
        paymentMethod: "CARD",
      });
      toast.success("Payment successful");
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || "Payment failed");
    }
  };

  const handleDownload = async (invoice) => {
    try {
      const blob = await downloadInvoicePdf(invoice.id);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `invoice-${invoice.invoiceNumber}.pdf`;
      a.click();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      toast.error("Could not download invoice");
    }
  };

  return (
    <div>
      <h4 className="mb-4">{user.role === "PATIENT" ? "My Bills" : "Billing"}</h4>
      {loading ? (
        <p>Loading...</p>
      ) : (
        <div className="card p-3">
          <table className="table align-middle">
            <thead>
              <tr>
                <th>Invoice #</th><th>Type</th><th>Amount</th><th>Status</th><th></th>
              </tr>
            </thead>
            <tbody>
              {invoices.map((inv) => (
                <tr key={inv.id}>
                  <td>{inv.invoiceNumber}</td>
                  <td>{inv.billType}</td>
                  <td>₹{inv.totalAmount}</td>
                  <td><span className="badge bg-secondary">{inv.status}</span></td>
                  <td className="d-flex gap-2">
                    <button className="btn btn-sm btn-outline-secondary" onClick={() => handleDownload(inv)}>
                      Download PDF
                    </button>
                    {user.role === "PATIENT" && inv.status !== "PAID" && (
                      <button className="btn btn-sm btn-primary" onClick={() => handlePay(inv)}>
                        Pay Now
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {invoices.length === 0 && <p className="text-muted mb-0">No invoices found.</p>}
        </div>
      )}
    </div>
  );
}
