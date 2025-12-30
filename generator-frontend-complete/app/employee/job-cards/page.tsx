'use client';
import { useEffect, useState } from 'react';
import { employeeJobCardAPI } from '@/lib/api';
import { JobCard } from '@/types';
import { FileText } from 'lucide-react';

export default function JobCardsPage() {
  const [jobCards, setJobCards] = useState<JobCard[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => { loadJobCards(); }, []);

  const loadJobCards = async () => {
    try {
      const response = await employeeJobCardAPI.getAll(0, 20);
      setJobCards(response.data.content);
    } catch (error) {
      console.error('Failed to load:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="text-center py-12">Loading...</div>;

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Job Cards</h1>

      {jobCards.length === 0 ? (
        <div className="card text-center py-12">
          <FileText className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-500">No job cards assigned yet</p>
        </div>
      ) : (
        <div className="space-y-4">
          {jobCards.map((job) => (
            <div key={job.id} className="card">
              <div className="flex justify-between items-start">
                <div>
                  <h3 className="font-bold text-lg">{job.ticketTitle}</h3>
                  <p className="text-sm text-gray-600">Ticket: #{job.ticketNumber}</p>
                  <p className="text-sm text-gray-600">Generator: {job.generator.name}</p>
                </div>
                <span className="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-semibold">{job.status}</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
