import { useEffect, useState } from "react";
import api from "../services/api";
import { Bar } from "react-chartjs-2";
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from "chart.js";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const DashboardPage = () => {
  const [metrics, setMetrics] = useState(null);

  useEffect(() => {
    api.get("/dashboard/metrics").then((res) => setMetrics(res.data));
  }, []);

  if (!metrics) return <div>Loading dashboard...</div>;

  const chartData = {
    labels: ["Completed", "Pending", "Overdue"],
    datasets: [
      {
        label: "Tasks",
        data: [metrics.completedTasks, metrics.pendingTasks, metrics.overdueTasks],
        backgroundColor: ["#16a34a", "#f59e0b", "#dc2626"],
      },
    ],
  };

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
        <Card title="Projects" value={metrics.totalProjects} />
        <Card title="Tasks" value={metrics.totalTasks} />
        <Card title="Completed" value={metrics.completedTasks} />
        <Card title="Pending" value={metrics.pendingTasks} />
        <Card title="Overdue" value={metrics.overdueTasks} />
      </div>
      <div className="bg-slate-900 p-4 rounded-xl">
        <h3 className="font-semibold mb-3">Task Progress</h3>
        <Bar data={chartData} />
      </div>
    </div>
  );
};

const Card = ({ title, value }) => (
  <div className="bg-slate-900 p-4 rounded-xl">
    <p className="text-slate-400 text-sm">{title}</p>
    <p className="text-2xl font-bold">{value}</p>
  </div>
);

export default DashboardPage;
