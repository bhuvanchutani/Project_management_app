import { useEffect, useState } from "react";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

const TeamPage = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === "ADMIN";
  const isMember = user?.role === "MEMBER";

  const [users, setUsers] = useState([]);
  const [search, setSearch] = useState("");
  const [note, setNote] = useState("");
  const [reportStatus, setReportStatus] = useState(null);

  useEffect(() => {
    if (!isAdmin) return;
    const t = setTimeout(() => {
      const params = search.trim().length >= 2 ? { q: search.trim() } : {};
      api
        .get("/users", { params })
        .then((r) => setUsers(r.data))
        .catch(() => setUsers([]));
    }, 300);
    return () => clearTimeout(t);
  }, [search, isAdmin]);

  const submitWorkReport = async (e) => {
    e.preventDefault();
    setReportStatus(null);
    try {
      await api.post("/members/work-completion", { note: note.trim() || undefined });
      setNote("");
      setReportStatus({ ok: true, msg: "Admins have been notified." });
    } catch (err) {
      setReportStatus({
        ok: false,
        msg: err.response?.data?.message || err.message || "Could not send report.",
      });
    }
  };

  return (
    <div className="space-y-8">
      {isMember && (
        <div className="bg-slate-900 p-6 rounded-xl border border-slate-800 max-w-xl">
          <h2 className="text-lg font-semibold mb-2">Work completion</h2>
          <p className="text-slate-400 text-sm mb-4">
            Tell admins you have finished your assigned work. They will get a notification.
          </p>
          <form onSubmit={submitWorkReport} className="space-y-3">
            <textarea
              value={note}
              onChange={(e) => setNote(e.target.value)}
              className="w-full p-3 rounded bg-slate-800 text-sm min-h-[100px]"
              placeholder="Optional note (what you completed, blockers, etc.)"
              maxLength={500}
            />
            <button type="submit" className="bg-emerald-600 hover:bg-emerald-500 px-4 py-2 rounded font-medium">
              Notify admins — work is complete
            </button>
            {reportStatus && (
              <p className={`text-sm ${reportStatus.ok ? "text-emerald-400" : "text-rose-400"}`}>
                {reportStatus.msg}
              </p>
            )}
          </form>
        </div>
      )}

      {isAdmin && (
        <>
          <div className="flex flex-col sm:flex-row sm:items-center gap-3">
            <h2 className="text-lg font-semibold">Team directory</h2>
            <input
              type="search"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by name or email (min 2 characters to filter)"
              className="flex-1 max-w-md p-3 rounded bg-slate-900 border border-slate-800 text-sm"
            />
          </div>
          <div className="bg-slate-900 rounded-xl overflow-x-auto border border-slate-800">
            <table className="w-full text-left">
              <thead className="bg-slate-800">
                <tr>
                  <th className="p-3">Name</th>
                  <th className="p-3">Email</th>
                  <th className="p-3">Role</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => (
                  <tr key={u.id} className="border-t border-slate-800">
                    <td className="p-3">{u.name}</td>
                    <td className="p-3">{u.email}</td>
                    <td className="p-3">{u.role}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {users.length === 0 && (
              <p className="p-6 text-slate-500 text-sm">No members match your search.</p>
            )}
          </div>
        </>
      )}

      {!isAdmin && !isMember && (
        <p className="text-slate-500 text-sm">Sign in as admin or member to use this page.</p>
      )}
    </div>
  );
};

export default TeamPage;
