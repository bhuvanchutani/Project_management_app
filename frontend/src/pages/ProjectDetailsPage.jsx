import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

const ProjectDetailsPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const isAdmin = user?.role === "ADMIN";
  const [project, setProject] = useState(null);

  useEffect(() => {
    api.get(`/projects/${id}`).then((r) => setProject(r.data));
  }, [id]);

  const onDelete = async () => {
    if (!window.confirm("Delete this project and all its tasks?")) return;
    try {
      await api.delete(`/projects/${id}`);
      navigate("/projects");
    } catch (e) {
      window.alert(e.response?.data?.message || e.message || "Delete failed");
    }
  };

  if (!project) return <div>Loading project...</div>;

  return (
    <div className="space-y-4">
      <div className="bg-slate-900 p-6 rounded-xl">
        <div className="flex items-start justify-between gap-4">
          <h2 className="text-2xl font-bold">{project.title}</h2>
          {isAdmin && (
            <button
              type="button"
              className="shrink-0 bg-rose-600 hover:bg-rose-500 px-4 py-2 rounded text-sm font-medium"
              onClick={onDelete}
            >
              Delete project
            </button>
          )}
        </div>
        <p className="text-slate-400 mt-2">{project.description || "No description"}</p>
        <div className="mt-4 text-sm text-slate-300 space-y-1">
          <p>Status: {project.status}</p>
          <p>Deadline: {project.deadline || "-"}</p>
          <p>Members: {project.members?.length || 0}</p>
        </div>
      </div>
    </div>
  );
};

export default ProjectDetailsPage;
