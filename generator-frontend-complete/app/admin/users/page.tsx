'use client';
import { useEffect, useState } from 'react';
import { adminUserAPI } from '@/lib/api';
import { User } from '@/types';
import { UserPlus, Edit, Trash2, X } from 'lucide-react';

export default function UsersPage() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    fullName: '',
    role: 'EMPLOYEE' as 'ADMIN' | 'EMPLOYEE',
    phone: '',
    email: '',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      const response = await adminUserAPI.getAll(0, 100);
      setUsers(response.data.content);
    } catch (error) {
      console.error('Failed to load users:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingUser(null);
    setFormData({
      username: '',
      password: '',
      fullName: '',
      role: 'EMPLOYEE',
      phone: '',
      email: '',
    });
    setError('');
    setShowModal(true);
  };

  const handleEdit = (user: User) => {
    setEditingUser(user);
    setFormData({
      username: user.username,
      password: '',
      fullName: user.fullName,
      role: user.role,
      phone: user.phone || '',
      email: user.email || '',
    });
    setError('');
    setShowModal(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    
    try {
      if (editingUser) {
        await adminUserAPI.update(editingUser.id, formData);
      } else {
        await adminUserAPI.create(formData);
      }
      setShowModal(false);
      loadUsers();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Operation failed');
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this user?')) return;
    
    try {
      await adminUserAPI.delete(id);
      loadUsers();
    } catch (error) {
      alert('Failed to delete user');
    }
  };

  if (loading) {
    return <div className="text-center py-12">Loading...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">User Management</h1>
        <button onClick={handleCreate} className="btn-primary flex items-center space-x-2">
          <UserPlus className="w-5 h-5" />
          <span>Add User</span>
        </button>
      </div>

      <div className="card">
        <table className="min-w-full">
          <thead>
            <tr className="border-b">
              <th className="text-left py-3 px-4">Username</th>
              <th className="text-left py-3 px-4">Full Name</th>
              <th className="text-left py-3 px-4">Role</th>
              <th className="text-left py-3 px-4">Email</th>
              <th className="text-left py-3 px-4">Status</th>
              <th className="text-right py-3 px-4">Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id} className="border-b hover:bg-gray-50">
                <td className="py-3 px-4 font-medium">{user.username}</td>
                <td className="py-3 px-4">{user.fullName}</td>
                <td className="py-3 px-4">
                  <span className={`px-2 py-1 rounded text-xs font-semibold ${
                    user.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' : 'bg-blue-100 text-blue-800'
                  }`}>
                    {user.role}
                  </span>
                </td>
                <td className="py-3 px-4">{user.email || '-'}</td>
                <td className="py-3 px-4">
                  <span className={`px-2 py-1 rounded text-xs font-semibold ${
                    user.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                  }`}>
                    {user.active ? 'Active' : 'Inactive'}
                  </span>
                </td>
                <td className="py-3 px-4 text-right space-x-2">
                  <button onClick={() => handleEdit(user)} className="text-blue-600 hover:text-blue-800">
                    <Edit className="w-4 h-4 inline" />
                  </button>
                  <button onClick={() => handleDelete(user.id)} className="text-red-600 hover:text-red-800">
                    <Trash2 className="w-4 h-4 inline" />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold">{editingUser ? 'Edit User' : 'Add User'}</h2>
              <button onClick={() => setShowModal(false)}>
                <X className="w-6 h-6" />
              </button>
            </div>

            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
                {error}
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-bold mb-2">Username</label>
                <input
                  type="text"
                  value={formData.username}
                  onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                  className="input-field"
                  required
                  disabled={!!editingUser}
                />
              </div>

              <div>
                <label className="block text-sm font-bold mb-2">
                  Password {editingUser && '(leave blank to keep current)'}
                </label>
                <input
                  type="password"
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                  className="input-field"
                  required={!editingUser}
                />
              </div>

              <div>
                <label className="block text-sm font-bold mb-2">Full Name</label>
                <input
                  type="text"
                  value={formData.fullName}
                  onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                  className="input-field"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-bold mb-2">Role</label>
                <select
                  value={formData.role}
                  onChange={(e) => setFormData({ ...formData, role: e.target.value as 'ADMIN' | 'EMPLOYEE' })}
                  className="input-field"
                  required
                >
                  <option value="EMPLOYEE">EMPLOYEE</option>
                  <option value="ADMIN">ADMIN</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-bold mb-2">Phone</label>
                <input
                  type="text"
                  value={formData.phone}
                  onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                  className="input-field"
                />
              </div>

              <div>
                <label className="block text-sm font-bold mb-2">Email</label>
                <input
                  type="email"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  className="input-field"
                />
              </div>

              <div className="flex space-x-3">
                <button type="submit" className="flex-1 btn-primary">
                  {editingUser ? 'Update' : 'Create'}
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
