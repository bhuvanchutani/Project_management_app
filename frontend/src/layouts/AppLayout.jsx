import { Link, Outlet } from "react-router-dom";
import { useCallback, useEffect, useRef, useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../services/api";

const AppLayout = () => {
  const { user, logout } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [notifOpen, setNotifOpen] = useState(false);
  const panelRef = useRef(null);

  const loadNotifications = useCallback(() => {
    api
      .get("/notifications")
      .then((r) => setNotifications(r.data))
      .catch(() => setNotifications([]));
  }, []);

  useEffect(() => {
    loadNotifications();
    const id = setInterval(loadNotifications, 60000);
    return () => clearInterval(id);
  }, [loadNotifications]);

  useEffect(() => {
    const onDoc = (e) => {
      if (panelRef.current && !panelRef.current.contains(e.target)) {
        setNotifOpen(false);
      }
    };
    document.addEventListener("click", onDoc);
    return () => document.removeEventListener("click", onDoc);
  }, []);

  const unread = notifications.filter((n) => !n.read).length;

  const markRead = async (id) => {
    try {
      await api.patch(`/notifications/${id}/read`);
      loadNotifications();
    } catch {
      /* ignore */
    }
  };

  const menu = [
    { to: "/dashboard", label: "Dashboard" },
    { to: "/projects", label: "Projects" },
    { to: "/tasks", label: "Tasks" },
    { to: "/team", label: "Team" },
    { to: "/profile", label: "Profile" },
  ];

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex">
      <aside className="w-64 bg-slate-900 border-r border-slate-800 p-5 hidden md:block">
        <h1 className="text-xl font-bold mb-6">PM SaaS</h1>
        <nav className="space-y-2">
          {menu.map((item) => (
            <Link key={item.to} to={item.to} className="block px-3 py-2 rounded hover:bg-slate-800">
              {item.label}
            </Link>
          ))}
        </nav>
      </aside>
      <main className="flex-1 p-4 md:p-8">
        <header className="flex items-center justify-between mb-6 gap-4">
          <div>
            <h2 className="font-semibold text-lg">{user?.name}</h2>
            <p className="text-slate-400 text-sm">{user?.role}</p>
          </div>
          <div className="flex items-center gap-3">
            <div className="relative" ref={panelRef}>
              <button
                type="button"
                className="relative px-3 py-2 rounded bg-slate-800 hover:bg-slate-700 text-sm"
                onClick={(e) => {
                  e.stopPropagation();
                  setNotifOpen((o) => {
                    const next = !o;
                    if (next) loadNotifications();
                    return next;
                  });
                }}
                aria-label="Notifications"
              >
                Alerts
                {unread > 0 && (
                  <span className="absolute -top-1 -right-1 bg-rose-600 text-white text-xs rounded-full min-w-[1.25rem] h-5 flex items-center justify-center px-1">
                    {unread > 9 ? "9+" : unread}
                  </span>
                )}
              </button>
              {notifOpen && (
                <div className="absolute right-0 mt-2 w-80 max-h-96 overflow-y-auto bg-slate-900 border border-slate-700 rounded-lg shadow-xl z-50 text-left">
                  {notifications.length === 0 ? (
                    <p className="p-4 text-slate-500 text-sm">No notifications</p>
                  ) : (
                    notifications.map((n) => (
                      <button
                        key={n.id}
                        type="button"
                        className={`w-full text-left p-3 border-b border-slate-800 hover:bg-slate-800/80 ${n.read ? "opacity-70" : ""}`}
                        onClick={() => markRead(n.id)}
                      >
                        <p className="font-medium text-sm text-slate-100">{n.title}</p>
                        <p className="text-xs text-slate-400 mt-1 line-clamp-3">{n.message}</p>
                      </button>
                    ))
                  )}
                </div>
              )}
            </div>
            <button className="bg-rose-600 px-4 py-2 rounded shrink-0" onClick={logout}>
              Logout
            </button>
          </div>
        </header>
        <Outlet />
      </main>
    </div>
  );
};

export default AppLayout;
