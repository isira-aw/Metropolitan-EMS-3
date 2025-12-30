'use client';
import { useRouter } from 'next/navigation';
import { authAPI } from '@/lib/api';
import { getFullName, getRole, getUserId, clearAuthData } from '@/lib/auth';
import { useEffect, useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { LayoutDashboard, Users, Box, FileText, BarChart3, LogOut } from 'lucide-react';

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const pathname = usePathname();
  const [fullName, setFullName] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const role = getRole();
    if (role !== 'ADMIN') {
      router.push('/login');
      return;
    }
    setFullName(getFullName() || 'Admin');
    setLoading(false);
  }, [router]);

  const handleLogout = async () => {
    try {
      const userId = getUserId();
      if (userId) await authAPI.logout(userId);
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      clearAuthData();
      router.push('/login');
    }
  };

  const menuItems = [
    { href: '/admin/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
    { href: '/admin/users', icon: Users, label: 'Users' },
    { href: '/admin/generators', icon: Box, label: 'Generators' },
    { href: '/admin/tickets', icon: FileText, label: 'Tickets' },
    { href: '/admin/reports', icon: BarChart3, label: 'Reports' },
  ];

  if (loading) return <div>Loading...</div>;

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow-lg">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-8">
              <h1 className="text-xl font-bold text-blue-600">Admin Panel</h1>
              <div className="hidden md:flex space-x-4">
                {menuItems.map((item) => (
                  <Link
                    key={item.href}
                    href={item.href}
                    className={`flex items-center space-x-2 px-3 py-2 rounded-md ${
                      pathname === item.href
                        ? 'bg-blue-600 text-white'
                        : 'text-gray-700 hover:bg-gray-200'
                    }`}
                  >
                    <item.icon className="w-5 h-5" />
                    <span>{item.label}</span>
                  </Link>
                ))}
              </div>
            </div>
            <div className="flex items-center space-x-4">
              <span className="text-gray-700">{fullName}</span>
              <button onClick={handleLogout} className="btn-danger flex items-center space-x-2">
                <LogOut className="w-4 h-4" />
                <span>Logout</span>
              </button>
            </div>
          </div>
        </div>
      </nav>
      <main className="max-w-7xl mx-auto px-4 py-8">{children}</main>
    </div>
  );
}
