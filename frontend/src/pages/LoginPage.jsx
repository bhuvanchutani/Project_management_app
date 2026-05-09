import { useState } from "react";
import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

const LoginPage = () => {
  const { register, handleSubmit } = useForm();
  const navigate = useNavigate();
  const { login } = useAuth();
  const [error, setError] = useState(null);

  const onSubmit = async (payload) => {
    setError(null);
    try {
      const { data } = await api.post("/auth/login", payload);
      login(data);
      navigate("/dashboard");
    } catch (err) {
      const msg =
        err.response?.data?.message ||
        err.response?.data?.detail ||
        err.message ||
        "Login failed. Is the API running on port 8081?";
      setError(typeof msg === "string" ? msg : "Login failed");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-950 px-4">
      <form onSubmit={handleSubmit(onSubmit)} className="bg-slate-900 p-6 rounded-xl w-full max-w-md space-y-4">
        <h1 className="text-2xl font-bold">Login</h1>
        {error && (
          <p className="text-sm text-rose-400 bg-rose-950/50 border border-rose-800 rounded p-3">{error}</p>
        )}
        <input {...register("email")} className="w-full p-3 rounded bg-slate-800" placeholder="Email" />
        <input {...register("password")} type="password" className="w-full p-3 rounded bg-slate-800" placeholder="Password" />
        <button className="w-full bg-indigo-600 p-3 rounded font-semibold">Sign In</button>
        <p className="text-sm text-slate-400">
          No account? <Link className="text-indigo-400" to="/signup">Create one</Link>
        </p>
      </form>
    </div>
  );
};

export default LoginPage;
