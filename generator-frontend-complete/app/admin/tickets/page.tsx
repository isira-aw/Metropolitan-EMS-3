'use client';
import { useEffect, useState } from 'react';
import { adminTicketAPI, adminGeneratorAPI } from '@/lib/api';
import { Ticket, Generator } from '@/types';
import { Plus, Edit, Trash2, X, Calendar, Clock } from 'lucide-react';

export default function TicketsPage() {
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [generators, setGenerators] = useState<Generator[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingTicket, setEditingTicket] = useState<Ticket | null>(null);
  const [formData, setFormData] = useState({
    ticketNumber: '',
    generatorId: '',
    title: '',
    description: '',
    weight: '',
    scheduledDate: '',
    scheduledTime: '',
  });

  useEffect(() => {
    loadTickets();
    loadGenerators();
  }, []);

  const loadTickets = async () => {
    try {
      const response = await adminTicketAPI.getAll(0, 100);
      setTickets(response.data.content);
    } catch (error) {
      console.error('Failed to load tickets:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadGenerators = async () => {
    try {
      const response = await adminGeneratorAPI.getAll(0, 100);
      setGenerators(response.data.content);
    } catch (error) {
      console.error('Failed to load generators:', error);
    }
  };

  const handleCreate = () => {
    setEditingTicket(null);
    setFormData({
      ticketNumber: '',
      generatorId: '',
      title: '',
      description: '',
      weight: '',
      scheduledDate: '',
      scheduledTime: '',
    });
    setShowModal(true);
  };

  const handleEdit = (ticket: Ticket) => {
    setEditingTicket(ticket);
    setFormData({
      ticketNumber: ticket.ticketNumber,
      generatorId: ticket.generatorId.toString(),
      title: ticket.title,
      description: ticket.description || '',
      weight: ticket.weight || '',
      scheduledDate: ticket.scheduledDate,
      scheduledTime: ticket.scheduledTime,
    });
    setShowModal(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const submitData = {
        ...formData,
        generatorId: parseInt(formData.generatorId),
      };

      if (editingTicket) {
        await adminTicketAPI.update(editingTicket.id, submitData);
      } else {
        await adminTicketAPI.create(submitData);
      }
      setShowModal(false);
      loadTickets();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Operation failed');
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this ticket?')) return;
    try {
      await adminTicketAPI.delete(id);
      loadTickets();
    } catch (error) {
      alert('Failed to delete');
    }
  };

  const getStatusBadge = (status: string) => {
    const colors = {
      CREATED: 'bg-blue-100 text-blue-800',
      ASSIGNED: 'bg-yellow-100 text-yellow-800',
      CLOSED: 'bg-green-100 text-green-800',
    };
    return colors[status as keyof typeof colors] || 'bg-gray-100 text-gray-800';
  };

  if (loading) return <div className="text-center py-12">Loading...</div>;

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">Tickets</h1>
        <button onClick={handleCreate} className="btn-primary flex items-center space-x-2">
          <Plus className="w-5 h-5" />
          <span>Create Ticket</span>
        </button>
      </div>

      {tickets.length === 0 ? (
        <div className="card text-center py-12 text-gray-500">
          No tickets yet. Create your first ticket to get started.
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {tickets.map((ticket) => (
            <div key={ticket.id} className="card">
              <div className="flex justify-between items-start mb-4">
                <div className="flex-1">
                  <h3 className="font-bold text-lg">{ticket.title}</h3>
                  <p className="text-sm text-gray-600">#{ticket.ticketNumber}</p>
                  <span className={`inline-block px-2 py-1 rounded text-xs font-semibold mt-2 ${getStatusBadge(ticket.status)}`}>
                    {ticket.status}
                  </span>
                </div>
                <div className="space-x-2 flex">
                  <button onClick={() => handleEdit(ticket)} className="text-blue-600 hover:text-blue-800">
                    <Edit className="w-4 h-4" />
                  </button>
                  <button onClick={() => handleDelete(ticket.id)} className="text-red-600 hover:text-red-800">
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>

              <div className="space-y-2 text-sm">
                <p><strong>Generator:</strong> {ticket.generatorName} ({ticket.generatorModel})</p>
                {ticket.description && <p><strong>Description:</strong> {ticket.description}</p>}
                {ticket.weight && <p><strong>Weight:</strong> {ticket.weight}</p>}

                <div className="flex items-center space-x-2 text-gray-600">
                  <Calendar className="w-4 h-4" />
                  <span>{ticket.scheduledDate}</span>
                </div>
                <div className="flex items-center space-x-2 text-gray-600">
                  <Clock className="w-4 h-4" />
                  <span>{ticket.scheduledTime}</span>
                </div>

                <p className="text-xs text-gray-500 mt-2">Created by: {ticket.createdByName}</p>
              </div>
            </div>
          ))}
        </div>
      )}

      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md max-h-screen overflow-y-auto">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold">{editingTicket ? 'Edit' : 'Create'} Ticket</h2>
              <button onClick={() => setShowModal(false)}>
                <X className="w-6 h-6" />
              </button>
            </div>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-bold mb-2">Ticket Number *</label>
                <input
                  type="text"
                  value={formData.ticketNumber}
                  onChange={(e) => setFormData({ ...formData, ticketNumber: e.target.value })}
                  className="input-field"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-bold mb-2">Generator *</label>
                <select
                  value={formData.generatorId}
                  onChange={(e) => setFormData({ ...formData, generatorId: e.target.value })}
                  className="input-field"
                  required
                >
                  <option value="">Select a generator</option>
                  {generators.map((gen) => (
                    <option key={gen.id} value={gen.id}>
                      {gen.name} - {gen.model}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-bold mb-2">Title *</label>
                <input
                  type="text"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  className="input-field"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-bold mb-2">Description</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="input-field"
                  rows={3}
                />
              </div>

              <div>
                <label className="block text-sm font-bold mb-2">Weight</label>
                <input
                  type="text"
                  value={formData.weight}
                  onChange={(e) => setFormData({ ...formData, weight: e.target.value })}
                  className="input-field"
                />
              </div>

              <div>
                <label className="block text-sm font-bold mb-2">Scheduled Date *</label>
                <input
                  type="date"
                  value={formData.scheduledDate}
                  onChange={(e) => setFormData({ ...formData, scheduledDate: e.target.value })}
                  className="input-field"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-bold mb-2">Scheduled Time *</label>
                <input
                  type="time"
                  value={formData.scheduledTime}
                  onChange={(e) => setFormData({ ...formData, scheduledTime: e.target.value })}
                  className="input-field"
                  required
                />
              </div>

              <div className="flex space-x-3">
                <button type="submit" className="flex-1 btn-primary">
                  {editingTicket ? 'Update' : 'Create'}
                </button>
                <button type="button" onClick={() => setShowModal(false)} className="flex-1 btn-secondary">
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
