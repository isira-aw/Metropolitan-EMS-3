'use client';
import { useEffect, useState } from 'react';
import { adminGeneratorAPI } from '@/lib/api';
import { Generator } from '@/types';
import { Plus, Edit, Trash2, X } from 'lucide-react';

export default function GeneratorsPage() {
  const [generators, setGenerators] = useState<Generator[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingGen, setEditingGen] = useState<Generator | null>(null);
  const [formData, setFormData] = useState({
    model: '', name: '', capacity: '', locationName: '', ownerEmail: '', note: ''
  });

  useEffect(() => { loadGenerators(); }, []);

  const loadGenerators = async () => {
    try {
      const response = await adminGeneratorAPI.getAll(0, 100);
      setGenerators(response.data.content);
    } catch (error) {
      console.error('Failed to load:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingGen(null);
    setFormData({ model: '', name: '', capacity: '', locationName: '', ownerEmail: '', note: '' });
    setShowModal(true);
  };

  const handleEdit = (gen: Generator) => {
    setEditingGen(gen);
    setFormData({
      model: gen.model, name: gen.name, capacity: gen.capacity || '',
      locationName: gen.locationName || '', ownerEmail: gen.ownerEmail || '', note: gen.note || ''
    });
    setShowModal(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingGen) {
        await adminGeneratorAPI.update(editingGen.id, formData);
      } else {
        await adminGeneratorAPI.create(formData);
      }
      setShowModal(false);
      loadGenerators();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Operation failed');
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this generator?')) return;
    try {
      await adminGeneratorAPI.delete(id);
      loadGenerators();
    } catch (error) {
      alert('Failed to delete');
    }
  };

  if (loading) return <div className="text-center py-12">Loading...</div>;

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">Generators</h1>
        <button onClick={handleCreate} className="btn-primary flex items-center space-x-2">
          <Plus className="w-5 h-5" />
          <span>Add Generator</span>
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {generators.map((gen) => (
          <div key={gen.id} className="card">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h3 className="font-bold text-lg">{gen.name}</h3>
                <p className="text-sm text-gray-600">{gen.model}</p>
              </div>
              <div className="space-x-2">
                <button onClick={() => handleEdit(gen)} className="text-blue-600">
                  <Edit className="w-4 h-4" />
                </button>
                <button onClick={() => handleDelete(gen.id)} className="text-red-600">
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
            {gen.capacity && <p className="text-sm"><strong>Capacity:</strong> {gen.capacity}</p>}
            {gen.locationName && <p className="text-sm"><strong>Location:</strong> {gen.locationName}</p>}
            {gen.ownerEmail && <p className="text-sm"><strong>Owner:</strong> {gen.ownerEmail}</p>}
          </div>
        ))}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md max-h-screen overflow-y-auto">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold">{editingGen ? 'Edit' : 'Add'} Generator</h2>
              <button onClick={() => setShowModal(false)}><X className="w-6 h-6" /></button>
            </div>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-bold mb-2">Model *</label>
                <input type="text" value={formData.model} onChange={(e) => setFormData({ ...formData, model: e.target.value })} className="input-field" required />
              </div>
              <div>
                <label className="block text-sm font-bold mb-2">Name *</label>
                <input type="text" value={formData.name} onChange={(e) => setFormData({ ...formData, name: e.target.value })} className="input-field" required />
              </div>
              <div>
                <label className="block text-sm font-bold mb-2">Capacity</label>
                <input type="text" value={formData.capacity} onChange={(e) => setFormData({ ...formData, capacity: e.target.value })} className="input-field" />
              </div>
              <div>
                <label className="block text-sm font-bold mb-2">Location</label>
                <input type="text" value={formData.locationName} onChange={(e) => setFormData({ ...formData, locationName: e.target.value })} className="input-field" />
              </div>
              <div>
                <label className="block text-sm font-bold mb-2">Owner Email</label>
                <input type="email" value={formData.ownerEmail} onChange={(e) => setFormData({ ...formData, ownerEmail: e.target.value })} className="input-field" />
              </div>
              <div>
                <label className="block text-sm font-bold mb-2">Note</label>
                <textarea value={formData.note} onChange={(e) => setFormData({ ...formData, note: e.target.value })} className="input-field" rows={3} />
              </div>
              <div className="flex space-x-3">
                <button type="submit" className="flex-1 btn-primary">{editingGen ? 'Update' : 'Create'}</button>
                <button type="button" onClick={() => setShowModal(false)} className="flex-1 btn-secondary">Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
