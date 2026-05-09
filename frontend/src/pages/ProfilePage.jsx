import { useAuth } from "../context/AuthContext";

const ProfilePage = () => {
  const { user } = useAuth();

  return (
    <div className="bg-slate-900 p-6 rounded-xl max-w-xl">
      <h2 className="text-xl font-bold mb-4">User Profile</h2>
      <p><span className="text-slate-400">Name:</span> {user?.name}</p>
      <p><span className="text-slate-400">Email:</span> {user?.email}</p>
      <p><span className="text-slate-400">Role:</span> {user?.role}</p>
    </div>
  );
};

export default ProfilePage;
