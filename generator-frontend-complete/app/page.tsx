'use client';
import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { getRole } from '@/lib/auth';

export default function Home() {
  const router = useRouter();
  
  useEffect(() => {
    const role = getRole();
    if (role === 'ADMIN') {
      router.push('/admin/dashboard');
    } else if (role === 'EMPLOYEE') {
      router.push('/employee/dashboard');
    } else {
      router.push('/login');
    }
  }, [router]);
  
  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-600"></div>
    </div>
  );
}
