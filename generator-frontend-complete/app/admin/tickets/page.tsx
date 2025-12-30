'use client';
import { Plus } from 'lucide-react';

export default function TicketsPage() {
  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">Tickets</h1>
        <button className="btn-primary flex items-center space-x-2">
          <Plus className="w-5 h-5" />
          <span>Create Ticket</span>
        </button>
      </div>
      <div className="card text-center py-12 text-gray-500">
        No tickets yet. Create your first ticket to get started.
      </div>
    </div>
  );
}
