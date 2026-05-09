import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";
import { Link } from "react-router-dom";

const ProjectsPage = () => {
  const [projects, setProjects] = useState([]);
  const { register, handleSubmit, reset } = useForm();
  const { user } = useAuth();
  const isAdmin = user?.role === "ADMIN";

  const load = () => api.get("/projects").then((r) => setProjects(r.data));
  useEffect(() => {
    load();
  }, []);

  const onCreate = async (payload) => {
    await api.post("/projects", { ...payload, status: "ACTIVE" });
    reset();
    load();
  };

  const onDeleteProject = async (id) => {
    if (!window.confirm("Delete this project and all its tasks?")) return;
    try {
      await api.delete(`/projects/${id}`);
      load();
    } catch (e) {
      window.alert(e.response?.data?.message || e.message || "Delete failed");
    }
  };

  return (
    <div className="space-y-6">
      {isAdmin && (
        <form onSubmit={handleSubmit(onCreate)} className="bg-slate-900 p-4 rounded-xl grid md:grid-cols-3 gap-3">
          <input {...register("title")} className="p-3 rounded bg-slate-800" placeholder="Project title" />
          <input {...register("deadline")} type="date" className="p-3 rounded bg-slate-800" />
          <button className="bg-indigo-600 rounded px-4">Create Project</button>
        </form>
      )}
      <div className="bg-slate-900 rounded-xl overflow-x-auto">
        <table className="w-full text-left">
          <thead className="bg-slate-800">
            <tr>
              <th className="p-3">Title</th>
              <th className="p-3">Status</th>
              <th className="p-3">Deadline</th>
              {isAdmin && <th className="p-3 w-28">Actions</th>}
            </tr>
          </thead>
          <tbody>
            {projects.map((p) => (
              <tr key={p.id} className="border-t border-slate-800">
                <td className="p-3">
                  <Link className="text-indigo-400" to={`/projects/${p.id}`}>{p.title}</Link>
                </td>
                <td className="p-3">{p.status}</td>
                <td className="p-3">{p.deadline || "-"}</td>
                {isAdmin && (
                  <td className="p-3">
                    <button
                      type="button"
                      className="text-rose-400 hover:text-rose-300 text-sm font-medium"
                      onClick={() => onDeleteProject(p.id)}
                    >
                      Delete
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ProjectsPage;
