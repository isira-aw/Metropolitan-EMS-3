'use client';
import { useEffect, useState } from 'react';
import { adminUserAPI } from '@/lib/api';
import { Users, Box, FileText } from 'lucide-react';

export default function AdminDashboard() {
  const [stats, setStats] = useState({ users: 0 });

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const usersRes = await adminUserAPI.getAll(0, 1);
      setStats({ users: usersRes.data.totalElements });
    } catch (error) {
      console.error('Failed to load:', error);
    }
  };

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Dashboard</h1>
      
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="card bg-blue-500 text-white">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm opacity-90">Total Users</p>
              <p className="text-4xl font-bold">{stats.users}</p>
            </div>
            <Users className="w-12 h-12 opacity-80" />
          </div>
        </div>

        <div className="card bg-green-500 text-white">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm opacity-90">Generators</p>
              <p className="text-4xl font-bold">0</p>
            </div>
            <Box className="w-12 h-12 opacity-80" />
          </div>
        </div>

        <div className="card bg-purple-500 text-white">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm opacity-90">Active Tickets</p>
              <p className="text-4xl font-bold">0</p>
            </div>
            <FileText className="w-12 h-12 opacity-80" />
          </div>
        </div>
      </div>

      <div className="card">
        <h2 className="text-xl font-bold mb-4">Quick Actions</h2>
        <div className="space-y-2">
          <a href="/admin/users" className="block p-4 bg-blue-50 hover:bg-blue-100 rounded transition">
            <p className="font-semibold">Manage Users</p>
            <p className="text-sm text-gray-600">Create, edit, and manage system users</p>
          </a>
          <a href="/admin/generators" className="block p-4 bg-green-50 hover:bg-green-100 rounded transition">
            <p className="font-semibold">Manage Generators</p>
            <p className="text-sm text-gray-600">Add and manage generator assets</p>
          </a>
          <a href="/admin/tickets" className="block p-4 bg-purple-50 hover:bg-purple-100 rounded transition">
            <p className="font-semibold">Create Ticket</p>
            <p className="text-sm text-gray-600">Create maintenance tickets</p>
          </a>
        </div>
      </div>
    </div>
  );
}
