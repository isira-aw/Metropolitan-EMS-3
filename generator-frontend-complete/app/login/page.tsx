'use client';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { authAPI } from '@/lib/api';
import { saveAuthData } from '@/lib/auth';

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const router = useRouter();
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    
    try {
      const response = await authAPI.login(username, password);
      const data = response.data;
      saveAuthData(data);
      
      if (data.role === 'ADMIN') {
        router.push('/admin/dashboard');
      } else if (data.role === 'EMPLOYEE') {
        router.push('/employee/dashboard');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-500 to-purple-600">
      <div className="bg-white rounded-lg shadow-xl p-8 w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Generator MS</h1>
          <p className="text-gray-600 mt-2">Sign in to continue</p>
        </div>
        
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}
        
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2">
              Username
            </label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="input-field"
              placeholder="Enter username"
              required
              disabled={loading}
            />
          </div>
          
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2">
              Password
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="input-field"
              placeholder="Enter password"
              required
              disabled={loading}
            />
          </div>
          
          <button
            type="submit"
            disabled={loading}
            className="w-full btn-primary disabled:opacity-50"
          >
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>
        
        <div className="mt-6 p-4 bg-gray-50 rounded">
          <p className="text-center text-sm text-gray-600 mb-2">Default Credentials</p>
          <div className="flex justify-center space-x-4 text-sm">
            <div>
              <p className="text-gray-500">Username</p>
              <p className="font-mono font-bold">admin</p>
            </div>
            <div className="border-l border-gray-300"></div>
            <div>
              <p className="text-gray-500">Password</p>
              <p className="font-mono font-bold">admin123</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
