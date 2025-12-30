'use client';
import { useEffect, useState } from 'react';
import { employeeDayAPI } from '@/lib/api';
import { DayStatus } from '@/types';
import { Play, Square, Clock } from 'lucide-react';

export default function EmployeeDashboard() {
  const [dayStatus, setDayStatus] = useState<DayStatus | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => { loadDayStatus(); }, []);

  const loadDayStatus = async () => {
    try {
      const response = await employeeDayAPI.getStatus();
      setDayStatus(response.data);
    } catch (error) {
      console.error('Failed to load:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleStartDay = async () => {
    try {
      await employeeDayAPI.start();
      loadDayStatus();
    } catch (error: any) {
      alert(error.response?.data?.message || 'Failed to start day');
    }
  };

  const handleEndDay = async () => {
    try {
      await employeeDayAPI.end();
      loadDayStatus();
    } catch (error: any) {
      alert(error.response?.data?.message || 'Failed to end day');
    }
  };

  if (loading) return <div className="text-center py-12">Loading...</div>;

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Dashboard</h1>

      <div className="card bg-indigo-500 text-white">
        <div className="flex justify-between items-center">
          <div>
            <h3 className="text-xl font-semibold mb-2">Day Management</h3>
            {dayStatus?.dayStarted && !dayStatus?.dayEnded && (
              <p>Started: {dayStatus.dayStartTime ? new Date(dayStatus.dayStartTime).toLocaleTimeString() : '-'}</p>
            )}
            {dayStatus?.dayEnded && <p>Day ended</p>}
            {!dayStatus?.dayStarted && <p>Start your day to begin working</p>}
          </div>
          <div>
            {!dayStatus?.dayStarted && (
              <button onClick={handleStartDay} className="bg-white text-indigo-600 hover:bg-gray-100 px-6 py-3 rounded-lg font-semibold flex items-center space-x-2">
                <Play className="w-5 h-5" />
                <span>Start Day</span>
              </button>
            )}
            {dayStatus?.dayStarted && !dayStatus?.dayEnded && (
              <button onClick={handleEndDay} className="bg-white text-indigo-600 hover:bg-gray-100 px-6 py-3 rounded-lg font-semibold flex items-center space-x-2">
                <Square className="w-5 h-5" />
                <span>End Day</span>
              </button>
            )}
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="card bg-yellow-500 text-white">
          <Clock className="w-8 h-8 mb-2" />
          <p className="text-sm opacity-90">Pending Jobs</p>
          <p className="text-3xl font-bold">0</p>
        </div>
        <div className="card bg-blue-500 text-white">
          <Play className="w-8 h-8 mb-2" />
          <p className="text-sm opacity-90">In Progress</p>
          <p className="text-3xl font-bold">0</p>
        </div>
        <div className="card bg-green-500 text-white">
          <Square className="w-8 h-8 mb-2" />
          <p className="text-sm opacity-90">Completed</p>
          <p className="text-3xl font-bold">0</p>
        </div>
      </div>
    </div>
  );
}
