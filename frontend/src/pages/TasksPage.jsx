import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

const TasksPage = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === "ADMIN";
  const [tasks, setTasks] = useState([]);
  const [users, setUsers] = useState([]);
  const [projects, setProjects] = useState([]);
  const { register, handleSubmit, reset } = useForm();

  const loadTasks = async () => {
    const { data } = await api.get("/tasks?page=0&size=20");
    setTasks(data.content || []);
  };

  useEffect(() => {
    loadTasks();
    api.get("/projects").then((r) => setProjects(r.data));
  }, []);

  useEffect(() => {
    if (!isAdmin) return;
    api.get("/users").then((r) => setUsers(r.data)).catch(() => setUsers([]));
  }, [isAdmin]);

  const onCreate = async (payload) => {
    await api.post("/tasks", payload);
    reset();
    loadTasks();
  };

  const onDeleteTask = async (id) => {
    if (!window.confirm("Delete this task?")) return;
    try {
      await api.delete(`/tasks/${id}`);
      loadTasks();
    } catch (e) {
      window.alert(e.response?.data?.message || e.message || "Delete failed");
    }
  };

  const onMarkComplete = async (id) => {
    try {
      await api.patch(`/tasks/${id}/complete`);
      loadTasks();
    } catch (e) {
      window.alert(e.response?.data?.message || e.message || "Could not mark complete");
    }
  };

  const canMarkComplete = (t) =>
    t.status !== "COMPLETED" && (isAdmin || user?.id === t.assignedToId);

  return (
    <div className="space-y-6">
      {isAdmin && (
        <form onSubmit={handleSubmit(onCreate)} className="bg-slate-900 p-4 rounded-xl grid md:grid-cols-4 gap-3">
          <input {...register("title")} className="p-3 rounded bg-slate-800" placeholder="Task title" />
          <select {...register("projectId")} className="p-3 rounded bg-slate-800">
            {projects.map((p) => (
              <option key={p.id} value={p.id}>{p.title}</option>
            ))}
          </select>
          <select {...register("assignedToId")} className="p-3 rounded bg-slate-800">
            {users.map((u) => (
              <option key={u.id} value={u.id}>{u.name}</option>
            ))}
          </select>
          <button className="bg-indigo-600 rounded px-4">Create Task</button>
        </form>
      )}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {tasks.map((t) => (
          <div key={t.id} className="bg-slate-900 p-4 rounded-xl flex items-start justify-between gap-3">
            <div>
              <h3 className="font-semibold">{t.title}</h3>
              <p className="text-sm text-slate-400">{t.status} / {t.priority}</p>
            </div>
            <div className="flex flex-col items-end gap-2 shrink-0">
              {canMarkComplete(t) && (
                <button
                  type="button"
                  className="text-emerald-400 hover:text-emerald-300 text-sm font-medium"
                  onClick={() => onMarkComplete(t.id)}
                >
                  Mark complete
                </button>
              )}
              {isAdmin && (
                <button
                  type="button"
                  className="text-rose-400 hover:text-rose-300 text-sm font-medium"
                  onClick={() => onDeleteTask(t.id)}
                >
                  Delete
                </button>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default TasksPage;
